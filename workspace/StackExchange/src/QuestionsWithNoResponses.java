import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.KeyValueTextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;


public class QuestionsWithNoResponses  {
	
	public static class MapClass extends Mapper<Text, Text,Text, SEWritable> {
		
		public void map (Text key, Text value, Context context) throws IOException, InterruptedException {
			SEWritable seWritableValue = new SEWritable(value.toString());
			
			if(seWritableValue.getPostType() == 2)
				key = new Text(String.valueOf(seWritableValue.getParentID()));
			
			context.write(key, new SEWritable(value.toString()));
		}
	}
	
	public static class Reduce extends Reducer<Text, SEWritable,Text,Text> {

		public void reduce(Text key, Iterable<SEWritable> values, Context context) throws IOException, InterruptedException {
			Long responses = 0L;
			Iterator<SEWritable> it = values.iterator();
			while (it.hasNext()) {
				SEWritable value = it.next();
				if(value.getPostType() == 2)
					responses++;
			}
			
			if(responses == 0)
				context.write(key, new Text(""));
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
		job.setOutputValueClass(SEWritable.class);
		
		job.setMapperClass(MapClass.class);
		job.setReducerClass(Reduce.class);
		
		job.setInputFormatClass(KeyValueTextInputFormat.class); 
		job.setOutputFormatClass(TextOutputFormat.class);
		
		FileInputFormat.setInputPaths(job, in);
		FileOutputFormat.setOutputPath(job, out);
		
		job.setJarByClass(QuestionsWithNoResponses.class);
		job.submit();
		
	}

}
