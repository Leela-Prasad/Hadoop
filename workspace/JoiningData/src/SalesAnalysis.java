import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.input.KeyValueTextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;


public class SalesAnalysis {

	public static class MapClass extends Mapper<Text,Text,CustomerAndTagKey,Text> {
		public void map(Text key, Text value, Context context) throws IOException, InterruptedException {

			//find out the file name
			FileSplit fileSplit = (FileSplit)context.getInputSplit();
			String fileName = fileSplit.getPath().getName();
			
			//find out the tag
			int tag = Integer.valueOf(context.getConfiguration().get(fileName));
			
			CustomerAndTagKey newKey = new CustomerAndTagKey(Integer.valueOf(key.toString()), tag);
			context.write(newKey,value);
		}
	}
	
	public static class Reduce extends Reducer<CustomerAndTagKey, Text, Text, DoubleWritable> {
		public void reduce(CustomerAndTagKey key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
			Iterator<Text> it = values.iterator();
			String country = it.next().toString();
			Double sales = 0d;
			while (it.hasNext()) {
				sales += Double.valueOf(it.next().toString());
			}
			context.write(new Text(country), new DoubleWritable(sales));
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
		Path in1 = new Path(args[0]); //country file
		Path in2 = new Path(args[1]); //sales file
		Path out = new Path(args[2]);
		
		Configuration conf = new Configuration();
		conf.set("mapreduce.input.keyvaluelinerecordreader.key.value.separator",","); 
		
		deletePreviousOutput(conf, out);
		
		conf.set(in1.getName(), "1"); //set up for tag
		conf.set(in2.getName(), "2"); //set up for tag
		
		Job job = Job.getInstance(conf);
		job.setOutputKeyClass(CustomerAndTagKey.class);
		job.setOutputValueClass(Text.class);
		
		job.setMapperClass(MapClass.class);
		job.setReducerClass(Reduce.class);
		
		job.setPartitionerClass(CustomerPartitioner.class);
		job.setGroupingComparatorClass(CustomerComparator.class);
		
		job.setInputFormatClass(KeyValueTextInputFormat.class);
		job.setOutputFormatClass(TextOutputFormat.class);
		
		//Here we are using addInputpath instead of setInputPath
		//this is because we need to add 2 files.
		FileInputFormat.addInputPath(job, in1);
		FileInputFormat.addInputPath(job, in2);
		
		FileOutputFormat.setOutputPath(job, out);
		
		job.setJarByClass(SalesAnalysis.class);
		job.submit();
	}
}
