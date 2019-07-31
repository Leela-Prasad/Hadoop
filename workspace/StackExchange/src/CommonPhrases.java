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
import org.apache.hadoop.mapreduce.lib.input.KeyValueTextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;


public class CommonPhrases  {
	
	public static class MapClass extends Mapper<Text, Text,Text, LongWritable> {
		
		public void map (Text key, Text value, Context context) throws IOException, InterruptedException {
			SEWritable seWritableValue = new SEWritable(value.toString());
			String text = seWritableValue.getText().toLowerCase();
			if(text.length()>1)
				text = text.substring(1);
			text = text.replaceAll("[^A-Za-z\\s]", "");
			String[] words = text.split(" ");
			
			for(int i=0;i<words.length-1; ++i) {
				if(words[i].length()>=4 && words[i+1].length()>=4)
					context.write(new Text(words[i] + " " + words[i+1]), new LongWritable(1));
			}
			
		}
	}
	
	public static class Reduce extends Reducer<Text, LongWritable,Text,LongWritable> {

		public void reduce(Text key, Iterable<LongWritable> values, Context context) throws IOException, InterruptedException {
			Long sum = 0L;
			Iterator<LongWritable> it = values.iterator();
			while (it.hasNext()) {
				sum += it.next().get();
			}
			
			if(sum >= 200)
				context.write(key, new LongWritable(sum));
		}
	}
	
	public static class Combine extends Reducer<Text, LongWritable,Text,LongWritable> {

		public void reduce(Text key, Iterable<LongWritable> values, Context context) throws IOException, InterruptedException {
			Long sum = 0L;
			Iterator<LongWritable> it = values.iterator();
			while (it.hasNext()) {
				sum += it.next().get();
			}
			
			context.write(key, new LongWritable(sum));
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
		job.setOutputValueClass(LongWritable.class);
		
		job.setMapperClass(MapClass.class);
		job.setCombinerClass(Combine.class);
		job.setReducerClass(Reduce.class);
		
		job.setInputFormatClass(KeyValueTextInputFormat.class); 
		job.setOutputFormatClass(TextOutputFormat.class);
		
		FileInputFormat.setInputPaths(job, in);
		FileOutputFormat.setOutputPath(job, out);
		
		job.setJarByClass(CommonPhrases.class);
		job.submit();
		
	}

}
