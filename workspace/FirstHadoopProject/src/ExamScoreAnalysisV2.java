import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;


public class ExamScoreAnalysisV2  {

	
	public static class MapClass extends Mapper<LongWritable, Text,Text, AverageWritable> {
		
		public void map (LongWritable key, Text value, Context context) throws IOException, InterruptedException {
			
			String line = value.toString();
			String[] values = line.split(",");
			context.write(new Text(values[2]), new AverageWritable(Double.valueOf(values[3]), 1));
		
		}
	}
	
	// Combine Input and Output Datatypes should be same otherwise
	// It might change the data which will not give you right result 
	// when hadoop runs our program with combiner and without combiner.
	public static class Combine extends Reducer<Text, AverageWritable,Text,AverageWritable> {

		public void reduce(Text key, Iterable<AverageWritable> values, Context context) throws IOException, InterruptedException {
			Double sum = 0D;
			Long n = 0L;
			Iterator<AverageWritable> it = values.iterator();
			System.out.println("Combiner Running");
			while (it.hasNext()) {
				AverageWritable nextItem = it.next();
				sum += Double.valueOf(nextItem.getTotal());
				n += Long.valueOf(nextItem.getNoOfRecords());
			}
			context.write(key, new AverageWritable(sum, n));
		}
	}
	
	public static class Reduce extends Reducer<Text, AverageWritable,Text,DoubleWritable> {

		public void reduce(Text key, Iterable<AverageWritable> values, Context context) throws IOException, InterruptedException {
			Double sum = 0D;
			Long n = 0L;
			Iterator<AverageWritable> it = values.iterator();
			while (it.hasNext()) {
				AverageWritable nextItem = it.next();
				sum += Double.valueOf(nextItem.getTotal());
				n += Long.valueOf(nextItem.getNoOfRecords());
			}
			double avg = sum/n;
			context.write(key, new DoubleWritable(avg));
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
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(AverageWritable.class);
		
		job.setMapperClass(MapClass.class);
		job.setCombinerClass(Combine.class);
		job.setReducerClass(Reduce.class);
		
		job.setInputFormatClass(TextInputFormat.class); 
		job.setOutputFormatClass(TextOutputFormat.class);
		
		FileInputFormat.setInputPaths(job, in);
		FileOutputFormat.setOutputPath(job, out);
		
		job.setJarByClass(ExamScoreAnalysisV2.class);
		job.submit();
		
	}

}
