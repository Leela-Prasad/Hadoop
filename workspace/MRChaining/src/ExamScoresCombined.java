import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.KeyValueTextInputFormat;
import org.apache.hadoop.mapreduce.lib.jobcontrol.ControlledJob;
import org.apache.hadoop.mapreduce.lib.jobcontrol.JobControl;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

public class ExamScoresCombined {

	public static void deletePreviousOutput(Configuration conf, Path path) {
		try {
			FileSystem hdfs = FileSystem.get(conf);
			hdfs.delete(path, true);
		} catch (IOException e) {
			// Ignore exceptions which will occur
			// if the output directory doesn't exist
		}
	}
	
	public static void main(String[] args) throws Exception {
		System.out.println("Starts ...");
		Path in = new Path(args[0]);
		Path out1 = new Path(args[1]);
		Path out2 = new Path(args[2]);
        
		// Here we need 2 conf objects as first MR job key value pair is delimited by ,
		// and second MR is tab separated and is default option so no need to specify.
        Configuration conf1 = new Configuration();
        Configuration conf2 = new Configuration();
        
        deletePreviousOutput(conf1, out1);
        deletePreviousOutput(conf2, out2);
        
        conf1.set  ("mapreduce.input.keyvaluelinerecordreader.key.value.separator", ",");
        
		Job job1 = Job.getInstance(conf1);
		Job job2 = Job.getInstance(conf2);
		
		
		job1.setOutputKeyClass(Text.class);
		job1.setOutputValueClass(LongWritable.class);
		
		job1.setMapperClass(ExamScoresAverageBySubjectAndSchool.MapClass.class);
		job1.setReducerClass(ExamScoresAverageBySubjectAndSchool.Reduce.class);
		
		job1.setInputFormatClass(KeyValueTextInputFormat.class); 
		job1.setOutputFormatClass(TextOutputFormat.class);
		
		FileInputFormat.setInputPaths(job1, in);
		FileOutputFormat.setOutputPath(job1, out1);
		
		job1.setJarByClass(ExamScoresAverageBySubjectAndSchool.class);
		
		job2.setOutputKeyClass(Text.class);
		job2.setOutputValueClass(Text.class);
		
		job2.setMapperClass(ExamScoresTopPerformers.MapClass.class);
		job2.setReducerClass(ExamScoresTopPerformers.Reduce.class);
	
		job2.setInputFormatClass(KeyValueTextInputFormat.class); 
		job2.setOutputFormatClass(TextOutputFormat.class);
		
		FileInputFormat.setInputPaths(job2, out1);
		FileOutputFormat.setOutputPath(job2, out2);
		
		job2.setJarByClass(ExamScoresTopPerformers.class);
		
		
		ControlledJob cj1 = new ControlledJob(conf1);
		ControlledJob cj2 = new ControlledJob(conf2);
		
		cj1.setJob(job1);
		cj2.setJob(job2);
		
		cj2.addDependingJob(cj1);
		
		JobControl jc = new JobControl("Exam Scores");
		jc.addJob(cj1);
		jc.addJob(cj2);
		
		Thread t = new Thread(jc);
		t.setDaemon(true);
		t.start();
		
		while(!jc.allFinished()) {
			Thread.sleep(500);
		}
	}
}
