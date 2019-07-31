import java.io.IOException;
import java.util.Iterator;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.input.KeyValueTextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;


public class VotesGreaterThan100 {

	public static class MapClass extends Mapper<Text,Text,PostIdandTagKey,Text> {
		public void map(Text key, Text value, Context context) throws IOException, InterruptedException {

			//find out the file name
			FileSplit fileSplit = (FileSplit)context.getInputSplit();
			String fileName = fileSplit.getPath().getName();
			
			//find out the tag
			int tag = Integer.valueOf(context.getConfiguration().get(fileName));
			
			PostIdandTagKey newKey = new PostIdandTagKey(Integer.valueOf(key.toString()), tag);
			context.write(newKey,value);
		}
	}
	
	public static class Reduce extends Reducer<PostIdandTagKey, Text, LongWritable, Text> {
		public void reduce(PostIdandTagKey key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
			Iterator<Text> it = values.iterator();
			
			String sValue = it.next().toString();
			if(StringUtils.isNumeric(sValue) && Long.parseLong(sValue) > 100) {
				SEWritable value = new SEWritable(it.next().toString());
				if(value.getPostType()==1) {
					LongWritable newKey = new LongWritable(Long.parseLong(sValue));
					Text newValue = new Text(key.getPostId() + " - " + value.getText());
					context.write(newKey, newValue);
				}	
			}
		}
	}
	
	public static void deletePreviousOutput(Configuration conf, Path path)  {

		try {
			FileSystem hdfs = FileSystem.get(conf);
			hdfs.delete(path,true);
		}
		catch (IOException e) {
			//ignore any exceptions
		}
	}
	
	public static void main(String[] args) throws Exception {
		Path in1 = new Path(args[0]); //votes file
		Path in2 = new Path(args[1]); //posts file
		Path out = new Path(args[2]);
		
		Configuration conf = new Configuration();
		//conf.set("mapreduce.input.keyvaluelinerecordreader.key.value.separator",","); 
		
		deletePreviousOutput(conf, out);
		
		conf.set(in1.getName(), "1"); //set up for tag
		conf.set(in2.getName(), "2"); //set up for tag
		
		Job job = Job.getInstance(conf);
		job.setOutputKeyClass(PostIdandTagKey.class);
		job.setOutputValueClass(Text.class);
		
		job.setMapperClass(MapClass.class);
		job.setReducerClass(Reduce.class);
		
		job.setPartitionerClass(PostIdPartitioner.class);
		job.setGroupingComparatorClass(PostIdComparator.class);
		
		job.setInputFormatClass(KeyValueTextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);
		
		//Here we are using addInputpath instead of setInputPath
		//this is because we need to add 2 files.
		FileInputFormat.addInputPath(job, in1);
		FileInputFormat.addInputPath(job, in2);
		
		FileOutputFormat.setOutputPath(job, out);
		
		job.setJarByClass(VotesGreaterThan100.class);
		job.submit();
	}
}
