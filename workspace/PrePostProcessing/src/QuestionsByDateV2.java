import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.KeyValueTextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.mapreduce.lib.reduce.LongSumReducer;


public class QuestionsByDateV2  {
	
	public static class MapClass extends Mapper<Text, Text,Text, LongWritable> {
		
		public void map (Text key, Text value, Context context) throws IOException, InterruptedException {
			SEWritable seWritableValue = new SEWritable(value.toString());
			
			String date = seWritableValue.getDate().substring(0, 7);
			
			if(seWritableValue.getPostType() == 1)
				context.write(new Text(date), new LongWritable(1));
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
	
	public static Job generateAndConfigureJob(Path in, Path out, Configuration conf) throws IOException {
		deletePreviousOutput(conf, out);
        
        
		Job job = Job.getInstance(conf);		
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(LongWritable.class);
		
		job.setMapperClass(MapClass.class);
		job.setReducerClass(LongSumReducer.class);
		
		job.setInputFormatClass(KeyValueTextInputFormat.class); 
		job.setOutputFormatClass(TextOutputFormat.class);
		
		FileInputFormat.setInputPaths(job, in);
		FileOutputFormat.setOutputPath(job, out);
		
		job.setJarByClass(QuestionsByDateV2.class);
		return job;
	}
	
	public static void main(String[] args) throws Exception {
		System.out.println("Starts ...");
		Path in = new Path(args[0]);
		Path out = new Path(args[1]);
        
        Configuration conf = new Configuration();
        Job job = generateAndConfigureJob(in, out, conf);
		job.submit();
		
	}

}
