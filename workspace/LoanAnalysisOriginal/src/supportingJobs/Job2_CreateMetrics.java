package supportingJobs;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;

import org.apache.commons.lang.time.DateUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.db.DBConfiguration;
import org.apache.hadoop.mapreduce.lib.db.DBOutputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.input.KeyValueTextInputFormat;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

import dataTypes.LoanRecord;
import dataTypes.MetricsOutput;
import dataTypes.MetricsRecord;


public class Job2_CreateMetrics {

	public static class MapClass extends Mapper<IntWritable, LoanRecord, IntWritable, MetricsRecord> {

		public void map(IntWritable key, LoanRecord value, Context context) throws IOException, InterruptedException {
			MetricsRecord newValue = new MetricsRecord();
			newValue.addLoan(value.finalStatus());
			context.write(new IntWritable(value.getRating()), newValue);
		}
	}
	
	public static class Combine extends Reducer<IntWritable, MetricsRecord,IntWritable, MetricsRecord> {
		public void reduce(IntWritable key, Iterable<MetricsRecord> values, Context context) throws IOException, InterruptedException {
			Iterator<MetricsRecord> it = values.iterator();
			
			MetricsRecord finalMetrics = new MetricsRecord();
			
			while (it.hasNext()) {
				finalMetrics.addMetrics(it.next().getMetrics());
			}
			
			context.write(key, finalMetrics);
		}
	}
	
	public static class Reduce extends Reducer<IntWritable, MetricsRecord, MetricsOutput, IntWritable> {
	
		private DecimalFormat df = new DecimalFormat("#.##");
		
		public void reduce(IntWritable key, Iterable<MetricsRecord> values, Context context) throws IOException, InterruptedException {
			Iterator<MetricsRecord> it = values.iterator();
			
			MetricsRecord finalMetrics = new MetricsRecord();
			
			while (it.hasNext()) {
				finalMetrics.addMetrics(it.next().getMetrics());
			}
			
			context.write(new MetricsOutput(key.get(), finalMetrics),key);
		}
	}

	public static void deletePreviousOuptut(Configuration conf, Path path) {
		try {
			FileSystem hdfs = FileSystem.get(conf);
			hdfs.delete(path, true);
		}
		catch (IOException e) {
			//ignore
		}
	}
	
	public static Job generateAndConfigureJob(Configuration conf, Path in) throws IOException {
		Job job = Job.getInstance(conf);
		job.setOutputKeyClass(IntWritable.class);
		job.setOutputValueClass(MetricsRecord.class);
		
		job.setMapperClass(MapClass.class);
		job.setCombinerClass(Combine.class);
		job.setReducerClass(Reduce.class);

		job.setInputFormatClass(SequenceFileInputFormat.class);
		job.setOutputFormatClass(DBOutputFormat.class);
		
		FileInputFormat.setInputPaths(job, in);
		
		String[] fields = {"rating","full","late1","late2","late3","notrepaid"};
		DBOutputFormat.setOutput(job,"metrics",fields);
		
		job.setJarByClass(Job2_CreateMetrics.class);
		return job;
	}
	
	public static void main(String[] args) throws Exception {
		Path in = new Path(args[0]); 
		
		Configuration conf = new Configuration();
		//conf.set("mapreduce.input.keyvaluelinerecordreader.key.value.separator",",");
		String cn = "jdbc:mysql://localhost/metrics?user=root&password=xrBouqf2";
		DBConfiguration.configureDB(conf,"com.mysql.jdbc.Driver",cn);
		
		Job job = generateAndConfigureJob(conf, in);
		job.submit();
	}
}
