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


public class HousePriceAnalysis  {

	
	public static class MapClass extends Mapper<LongWritable, Text,Text, Text > {
		
		public void map (LongWritable key, Text value, Context context) throws IOException, InterruptedException {
			
			String line = value.toString();
			String[] values = line.split(",");
			context.write(new Text(values[0]), new Text(values[1] + ",1"));
		
		}
	}
	
	// Combine Input and Output Datatypes should be same otherwise
	// It might change the data which will not give you right result 
	// when hadoop runs our program with combiner and without combiner.
	public static class Combine extends Reducer<Text, Text,Text,Text> {

		public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
			Double sum = 0D;
			Long n = 0L;
			Iterator<Text> it = values.iterator();
			//System.out.println("Combine Invoke Key: " + key.toString() + " Values : ");
			while (it.hasNext()) {
				String value = it.next().toString();
				//System.out.print(value + "\t");
				String[] sValues = value.split(",");
				sum += Double.valueOf(sValues[0]);
				n += Long.valueOf(sValues[1]);
			}
			//System.out.println();
			context.write(key, new Text(sum + "," + n));
		}
	}
	
	public static class Reduce extends Reducer<Text, Text,Text,DoubleWritable> {

		public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
			Double sum = 0D;
			Long n = 0L;
			Iterator<Text> it = values.iterator();
			while (it.hasNext()) {
				String value = it.next().toString();
				String[] sValues = value.split(",");
				sum += Double.valueOf(sValues[0]);
				n += Long.valueOf(sValues[1]);
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
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		
		job.setMapperClass(MapClass.class);
		job.setCombinerClass(Combine.class);
		job.setReducerClass(Reduce.class);
		
		job.setInputFormatClass(TextInputFormat.class); 
		job.setOutputFormatClass(TextOutputFormat.class);
		
		FileInputFormat.setInputPaths(job, in);
		FileOutputFormat.setOutputPath(job, out);
		
		job.setJarByClass(HousePriceAnalysis.class);
		job.submit();
		
	}

}
