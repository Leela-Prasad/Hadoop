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
import org.apache.hadoop.mapreduce.lib.chain.ChainMapper;
import org.apache.hadoop.mapreduce.lib.chain.ChainReducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.KeyValueTextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.mapreduce.lib.reduce.LongSumReducer;


public class CommonPhrases  {
	
	public static class PreProcessing extends Mapper<Text, Text,Text, Text> {
		
		public void map (Text key, Text value, Context context) throws IOException, InterruptedException {
			String[] wordsToBeExcluded = {"from", "have", "some", "that", "then", "this", "what", "when", "where"};
			
			String newValue = value.toString().toLowerCase();
			for(int i=0;i<wordsToBeExcluded.length; ++i) {
				newValue = newValue.replaceAll(wordsToBeExcluded[i], ""); 
			}
			context.write(key, new Text(newValue));
		}
	}

	public static class MapClass extends Mapper<Text, Text,Text, LongWritable> {
		
		public void map (Text key, Text value, Context context) throws IOException, InterruptedException {
			SEWritable seWritableValue = new SEWritable(value.toString());
			String text = seWritableValue.getText();
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
	
	public static class PostProcessing extends Mapper<Text, LongWritable,Text,LongWritable> {
		
		public void map (Text key, LongWritable value, Context context) throws IOException, InterruptedException {
			long sum = value.get();
			if(sum >=200) {
				context.write(key, value);
			}
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
	
	public static Job generateAndConfigureJob(Path in, Path out, Configuration conf) throws IOException {
		deletePreviousOutput(conf, out);
        
        
		Job job = Job.getInstance(conf);		
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(LongWritable.class);
		
		ChainMapper.addMapper(job, PreProcessing.class, Text.class, Text.class, Text.class, Text.class, conf);
		ChainMapper.addMapper(job, MapClass.class, Text.class, Text.class, Text.class, LongWritable.class, conf);
		ChainReducer.setReducer(job, LongSumReducer.class, Text.class, LongWritable.class, Text.class, LongWritable.class, conf);
		ChainReducer.addMapper(job, PostProcessing.class, Text.class, LongWritable.class, Text.class, LongWritable.class, conf);
		
		job.setCombinerClass(Combine.class);
		
		job.setInputFormatClass(KeyValueTextInputFormat.class); 
		job.setOutputFormatClass(TextOutputFormat.class);
		
		FileInputFormat.setInputPaths(job, in);
		FileOutputFormat.setOutputPath(job, out);
		
		job.setJarByClass(CommonPhrases.class);
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
