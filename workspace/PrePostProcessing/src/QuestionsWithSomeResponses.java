import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.KeyValueTextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;


public class QuestionsWithSomeResponses  {
	
	public static class Reduce0 extends Reducer<Text, SEWritable,Text,Text> {

		public void reduce(Text key, Iterable<SEWritable> values, Context context) throws IOException, InterruptedException {
			Long responses = ReduceHelper.getNumberOfResponses(values);
			
			if(responses == 0)
				context.write(key, new Text(""));
		}
		
	}
	
	public static class Reduce20 extends Reducer<Text, SEWritable,Text,Text> {

		public void reduce(Text key, Iterable<SEWritable> values, Context context) throws IOException, InterruptedException {
			Long responses = ReduceHelper.getNumberOfResponses(values);
			
			if(responses >= 20)
				context.write(key, new Text(""));
		}
		
	}
	
	public static class ReduceHelper {
		public static Long getNumberOfResponses(Iterable<SEWritable> values) {
			Long responses = 0L;
			Iterator<SEWritable> it = values.iterator();
			while (it.hasNext()) {
				SEWritable value = it.next();
				if(value.getPostType() == 2)
					responses++;
			}
			return responses;
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
	
	public static Job generateAndConfigureJob(Path in, Path out, Configuration conf, String requirement) throws IOException {
		deletePreviousOutput(conf, out);
        
        
		Job job = Job.getInstance(conf);		
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(SEWritable.class);
		
		job.setMapperClass(SEWritableMap.class);
		
		if(requirement.equals("20"))
			job.setReducerClass(Reduce20.class);
		else
			job.setReducerClass(Reduce0.class);
		
		job.setInputFormatClass(KeyValueTextInputFormat.class); 
		job.setOutputFormatClass(TextOutputFormat.class);
		
		FileInputFormat.setInputPaths(job, in);
		FileOutputFormat.setOutputPath(job, out);
		
		job.setJarByClass(QuestionsWithSomeResponses.class);
		return job;
	}
	
	public static void main(String[] args) throws Exception {
		System.out.println("Starts ...");
		Path in = new Path(args[0]);
		Path out = new Path(args[1]);
		String requirement = args[2];
        
        Configuration conf = new Configuration();
        Job job = generateAndConfigureJob(in, out, conf, requirement);
		job.submit();
		
	}

}
