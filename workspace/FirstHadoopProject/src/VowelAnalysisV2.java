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
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;


public class VowelAnalysisV2  {

	
	//Mapper<MapperKeyType, MapperValueType, ShuffleKeyType, ShuffleValueType>
	public static class MapClass extends Mapper<LongWritable, Text,Text, LongWritable > {
		
		//map method will be invoked for every line in the input file.
		public void map (LongWritable key, Text value, Context context) throws IOException, InterruptedException {
			
			 System.out.println("Map Invoked, Input :: " + value);
			//Converting Hadoop Text Type to Java String Type.
			String line = value.toString().toLowerCase();
			char[] chars = line.toCharArray();
			for(char ch: chars) {
				if(isVowel(ch))
					context.write(new Text(String.valueOf(ch)), new LongWritable(1));
			}
		}
		
		public boolean isVowel(char ch) {
			if(ch == 'a' || ch == 'e' || ch == 'i' || ch == 'o' || ch == 'u')
				return true;
			return false;
		}
	}
	
	public static class Combine extends Reducer<Text, LongWritable,Text,LongWritable> {

		//reduce method will be invoked for evey record in shuffle output.
		//shuffle will transfer keys with same name to a single slave node so that we can do aggregation 
		// key with same name will be made available as list of values and this is done by hadoop internally for both combine and reduce step.
		public void reduce(Text key, Iterable<LongWritable> values, Context context) throws IOException, InterruptedException {
			Long sum = 0L;
			System.out.println("Combine Invoke Key: " + key.toString() + " Values : ");
			Iterator<LongWritable> it = values.iterator();
			while (it.hasNext()) {
				//Here get will return the long value from LongWritable.
				long value = it.next().get();
				System.out.print(value + "\t");
				sum += value;
			}
			System.out.println();
			context.write(key, new LongWritable(sum));
		}
	}
	
	//Reducer<ShuffleKeyType, ShuffleValueType, ReducerKeyType, ReducerValueType>
	public static class Reduce extends Reducer<Text, LongWritable,Text,LongWritable> {

		//reduce method will be invoked for evey record in shuffle output.
		//shuffle will transfer keys with same name to a single slave node so that we can do aggregation 
		// key with same name will be made available as list of values and this is done by hadoop internally for both combine and reduce step.
		public void reduce(Text key, Iterable<LongWritable> values, Context context) throws IOException, InterruptedException {
			Long sum = 0L;
			Iterator<LongWritable> it = values.iterator();
			System.out.println("Reduce Invoked for Key: " + key.toString());
			while (it.hasNext()) {
				//Here get will return the long value from LongWritable.
				sum += it.next().get();
			}
			context.write(key, new LongWritable(sum));
		}
	}
	
	public static void deletePreviousOutput(Configuration conf, Path path) {
		try {
			FileSystem hdfs = FileSystem.get(conf);
			//Boolean argument is for Recursive Delete
			hdfs.delete(path, true);
		} catch (IOException e) {
			// Ignore exceptions which will occur
			// if the output directory doesn't exist
		}
	}
	
	public static void main(String[] args) throws Exception {
        System.out.println("Starts ....");
		//Input File Path
		Path in = new Path(args[0]);
        //Output File Path
		Path out = new Path(args[1]);
        
		//Hadoop Configuration object and this will vary on 
		//we are running the job.
        Configuration conf = new Configuration();
        deletePreviousOutput(conf, out);
        
		Job job = Job.getInstance(conf);		
		//Setting Map and Reduce Key Value Types 
		// If map and reduce key values are same then we can use below 2 lines
		// If they are different we need set it explicitly for map and reduce so total 4 lines needed.
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(LongWritable.class);
		
		job.setMapperClass(MapClass.class);
		job.setCombinerClass(Combine.class);
		job.setReducerClass(Reduce.class);
		
		job.setInputFormatClass(TextInputFormat.class); 
		job.setOutputFormatClass(TextOutputFormat.class);
		
		FileInputFormat.setInputPaths(job, in);
		FileOutputFormat.setOutputPath(job, out);
		
		job.setJarByClass(VowelAnalysisV2.class);
		job.submit();
		
	}

}
