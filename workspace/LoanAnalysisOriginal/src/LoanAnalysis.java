import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.db.DBConfiguration;
import org.apache.hadoop.mapreduce.lib.jobcontrol.ControlledJob;
import org.apache.hadoop.mapreduce.lib.jobcontrol.JobControl;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import supportingJobs.Job1_JoinAllData;
import supportingJobs.Job2_CreateMetrics;

public class LoanAnalysis extends Configured implements Tool {

	public static void deletePreviousOuptut(Configuration conf, Path path) {
		try {
			FileSystem hdfs = FileSystem.get(conf);
			hdfs.delete(path, true);
		}
		catch (IOException e) {
			//ignore
		}
	}
	
	public static void main(String[] args) throws Exception {
		 int res = ToolRunner.run(new Configuration(), new LoanAnalysis(), args);
	        System.exit(res);
	}

	@Override
	public int run(String[] args) throws Exception {
		Path in1 = new Path(args[0]); //ratings file
		Path in2 = new Path(args[1]); //loan data file
		Path in3 = new Path(args[2]); //payments file
		Path out1 = new Path(args[3]); //interim output location
		
		Configuration conf = this.getConf();
		conf.set("mapreduce.input.keyvaluelinerecordreader.key.value.separator",",");
		
		String cn = "jdbc:mysql://localhost/metrics?user=root";
		DBConfiguration.configureDB(conf,"com.mysql.jdbc.Driver",cn);

		deletePreviousOuptut(conf, out1);
		
		conf.set(in1.getName(),"1");
		conf.set(in2.getName(),"2");
		conf.set(in3.getName(),"3");

		Job job1 = Job1_JoinAllData.generateAndConfigureJob(conf, in1, in2, in3, out1);
		Job job2 = Job2_CreateMetrics.generateAndConfigureJob(conf, out1);
		
		ControlledJob cj1 = new ControlledJob(conf);
		ControlledJob cj2 = new ControlledJob(conf);
		
		cj1.setJob(job1);
		cj2.setJob(job2);
		cj2.addDependingJob(cj1);
		
		JobControl jc = new JobControl("LoanAnalysis");
		jc.addJob(cj1);
		jc.addJob(cj2);
		
		Thread t = new Thread(jc);
		t.setDaemon(true);
		t.start();
		
		while(!jc.allFinished()) {
			Thread.sleep(500);
		}
		
		return 0;
	}
	
}
