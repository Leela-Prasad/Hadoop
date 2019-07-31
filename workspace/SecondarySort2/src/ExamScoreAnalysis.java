import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;


public class ExamScoreAnalysis  {

	
	public static class MapClass extends Mapper<LongWritable, Text,ExamSubjectAndScoreKey, LongWritable> {
		
		public void map (LongWritable key, Text value, Context context) throws IOException, InterruptedException {
			
			String line = value.toString();
			String[] values = line.split(",");
			Long score = Long.valueOf(values[3]); 
			ExamSubjectAndScoreKey newKey = new ExamSubjectAndScoreKey(values[2], score);
			// System.out.println("Mapper :::" + newKey.getSubject() + "   " + newKey.getScore() + "  " + score);
			context.write(newKey, new LongWritable(score));
		}
	}
	
	public static class Combine extends Reducer<ExamSubjectAndScoreKey, LongWritable,ExamSubjectAndScoreKey,LongWritable> {

		public void reduce(ExamSubjectAndScoreKey key, Iterable<LongWritable> values, Context context) throws IOException, InterruptedException {
			Iterator<LongWritable> it = values.iterator();
			context.write(key, it.next());
		}
	}
	
	public static class Reduce extends Reducer<ExamSubjectAndScoreKey, LongWritable,Text,LongWritable> {

		public void reduce(ExamSubjectAndScoreKey key, Iterable<LongWritable> values, Context context) throws IOException, InterruptedException {
			Iterator<LongWritable> it = values.iterator();
			// System.out.println("Reducer ::: " + key.getSubject() + "  " + key.getScore());
			// context.write(new Text(key.getSubject()), it.next());
			String subject = key.getSubject();
			Long score = key.getScore();
			while(it.hasNext()) {
			  System.out.println("Reducer ::: " + subject + "  " + score + "  " + it.next().get());
			  subject = key.getSubject();
			  score = key.getScore();
			 }
		}
	}
	
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
		Path out = new Path(args[1]);
        
        Configuration conf = new Configuration();
        deletePreviousOutput(conf, out);
        
		Job job = Job.getInstance(conf);		
		// These 2 lines are Map output types.
		job.setOutputKeyClass(ExamSubjectAndScoreKey.class);
		job.setOutputValueClass(LongWritable.class);
		
		job.setMapperClass(MapClass.class);
		// job.setCombinerClass(Combine.class);
		job.setReducerClass(Reduce.class);
		
		job.setPartitionerClass(SubjectPartitioner.class);
		job.setGroupingComparatorClass(SubjectComparator.class);
		
		job.setInputFormatClass(TextInputFormat.class); 
		job.setOutputFormatClass(TextOutputFormat.class);
		
		FileInputFormat.setInputPaths(job, in);
		FileOutputFormat.setOutputPath(job, out);
		
		job.setJarByClass(ExamScoreAnalysis.class);
		job.submit();
		
	}

}
