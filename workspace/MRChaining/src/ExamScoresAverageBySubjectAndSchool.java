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
import org.apache.hadoop.mapreduce.lib.input.KeyValueTextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

public class ExamScoresAverageBySubjectAndSchool {

	public static class MapClass extends Mapper<Text, Text,Text, LongWritable > {
		
		// If we use combine then we need output value as score,1
		public void map (Text key, Text value, Context context) throws IOException, InterruptedException {
			String line = value.toString();
			String[] values = line.split(",");
			context.write(new Text(values[0] + "," + values[1]), new LongWritable(Long.valueOf(values[2])));
		}
		
	}
		public static class Reduce extends Reducer<Text, LongWritable,Text,DoubleWritable> {

			public void reduce(Text key, Iterable<LongWritable> values, Context context) throws IOException, InterruptedException {
				Double sum = 0D;
				Long n = 0L;
				Iterator<LongWritable> it = values.iterator();
				while (it.hasNext()) {
					sum += it.next().get();  
					++n;
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
	        
	        conf.set  ("mapreduce.input.keyvaluelinerecordreader.key.value.separator", ",");
	        
			Job job = Job.getInstance(conf);		
			job.setOutputKeyClass(Text.class);
			job.setOutputValueClass(LongWritable.class);
			
			job.setMapperClass(MapClass.class);
			job.setReducerClass(Reduce.class);
			
			job.setInputFormatClass(KeyValueTextInputFormat.class); 
			job.setOutputFormatClass(TextOutputFormat.class);
			
			FileInputFormat.setInputPaths(job, in);
			FileOutputFormat.setOutputPath(job, out);
			
			job.setJarByClass(ExamScoresAverageBySubjectAndSchool.class);
			job.submit();
			
		}
}
