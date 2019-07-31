package supportingJobs;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.db.DBConfiguration;
import org.apache.hadoop.mapreduce.lib.db.DBOutputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;

import dataTypes.LoanRecord;
import dataTypes.MetricsOutput;
import dataTypes.MetricsRecord;

public class Job2_CreateMetrics {

	public static class MapClass extends Mapper<IntWritable, LoanRecord,IntWritable, MetricsOutput> {
		
		public void map (IntWritable key, LoanRecord value, Context context) throws IOException, InterruptedException {
			MetricsOutput metrics = new MetricsOutput();
			metrics.addLoan(value.finalStatus());
			context.write(new IntWritable(value.getRating()), metrics);
		}
	}
	
	public static class Combine extends Reducer<IntWritable, MetricsOutput, IntWritable, MetricsOutput> {
		
		public void reduce(IntWritable key, Iterable<MetricsOutput> values, Context context) throws IOException, InterruptedException {
			Iterator<MetricsOutput> it = values.iterator();
			MetricsOutput metrics = new MetricsOutput();
			while(it.hasNext()) {
				metrics.addMetrics(it.next().getMetrics());
			}
			
			context.write(key, metrics);
		}
	}
	
	public static class Reduce extends Reducer<IntWritable, MetricsOutput, MetricsRecord, Text> {

		public void reduce(IntWritable key, Iterable<MetricsOutput> values, Context context) throws IOException, InterruptedException {
			Iterator<MetricsOutput> it = values.iterator();
			MetricsOutput metrics = new MetricsOutput();
			while(it.hasNext()) {
				metrics.addMetrics(it.next().getMetrics());
			}
			
			context.write(new MetricsRecord(key.get(),metrics), new Text(""));
		}
	}
	
	public static void main(String[] args) throws Exception {
		Path in = new Path(args[0]);
        
        Configuration conf = new Configuration();
        DBConfiguration.configureDB(conf, "com.mysql.jdbc.Driver", "jdbc:mysql://localhost/metrics?user=root&password=xrBouqf2");
        
		Job job = generateAndConfigureJob(in, conf);
		job.submit();
	}

	public static Job generateAndConfigureJob(Path in, Configuration conf) throws IOException, InterruptedException, ClassNotFoundException {
		
		Job job = Job.getInstance(conf);		
		
		job.setMapOutputKeyClass(IntWritable.class);
		job.setMapOutputValueClass(MetricsOutput.class);
		
		job.setOutputKeyClass(MetricsRecord.class);
		job.setOutputValueClass(Text.class);
		
		job.setMapperClass(MapClass.class);
		job.setCombinerClass(Combine.class);
		job.setReducerClass(Reduce.class);
		
				
		job.setInputFormatClass(SequenceFileInputFormat.class); 
		job.setOutputFormatClass(DBOutputFormat.class);
		
		FileInputFormat.setInputPaths(job, in);
		
		String[] fields = {"rating", "full", "late1", "late2", "late3", "notrepaid"};
		DBOutputFormat.setOutput(job, "metrics", fields);
		
		job.setJarByClass(Job2_CreateMetrics.class);
		return job;
	}
	
	
}