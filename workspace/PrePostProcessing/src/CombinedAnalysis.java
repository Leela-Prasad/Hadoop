import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.jobcontrol.ControlledJob;
import org.apache.hadoop.mapreduce.lib.jobcontrol.JobControl;

public class CombinedAnalysis {

	public static void main(String[] args) throws Exception {
		System.out.println("Starts ...");
		Path in = new Path(args[0]);
		Path out1 = new Path(args[1]);
		Path out2 = new Path(args[2]);
		Path out3 = new Path(args[3]);
		Path out4 = new Path(args[4]);
        
        Configuration conf = new Configuration();
        
        List<Job> jobs = new ArrayList<>();
        jobs.add(CommonPhrases.generateAndConfigureJob(in, out1, conf));
        jobs.add(QuestionsByDateV2.generateAndConfigureJob(in, out2, conf));
        jobs.add(QuestionsWithSomeResponses.generateAndConfigureJob(in, out3, conf, "20"));
        jobs.add(QuestionsWithSomeResponses.generateAndConfigureJob(in, out4, conf, "0"));
        
        JobControl jc = new JobControl("Combined Analysis");
        for(int i=0; i<jobs.size(); ++i) {
        	ControlledJob cj = new ControlledJob(conf);
        	cj.setJob(jobs.get(i));
        	jc.addJob(cj);
        }
        
		Thread t = new Thread(jc);
		t.setDaemon(true);
		t.start();
		
		while(!jc.allFinished()) {
			Thread.sleep(500);
		}
	}
}
