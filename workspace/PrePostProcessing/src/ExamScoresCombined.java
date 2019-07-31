import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.lib.jobcontrol.ControlledJob;
import org.apache.hadoop.mapreduce.lib.jobcontrol.JobControl;

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
        
        conf1.set  ("mapreduce.input.keyvaluelinerecordreader.key.value.separator", ",");
        
		ControlledJob cj1 = new ControlledJob(conf1);
		ControlledJob cj2 = new ControlledJob(conf2);
		
		cj1.setJob(ExamScoresAverageBySubjectAndSchool.generateAndConfigureJob(in, out1, conf1, conf2));
		cj2.setJob(ExamScoresTopPerformers.generateAndConfigureJob(out1, out2, conf2));
		
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
