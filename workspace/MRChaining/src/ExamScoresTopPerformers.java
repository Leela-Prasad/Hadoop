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

public class ExamScoresTopPerformers {

	public static class MapClass extends Mapper<Text, Text,Text, Text > {
		
		public void map (Text key, Text value, Context context) throws IOException, InterruptedException {
			String[] keys = key.toString().split(",");
			String newKey = keys[1];
			String newValue = keys[0] + "," + value;
			
			context.write(new Text(newKey), new Text(newValue));
		}
	}
		
	public static class Reduce extends Reducer<Text, Text,Text,Text> {

		public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
			
			String college = "";
			Double highestScore = 0D;
			
			Iterator<Text> it = values.iterator();
			while(it.hasNext()) {
				String[] value = it.next().toString().split(",");
				if(Double.valueOf(value[1]) > highestScore) {
					highestScore = Double.valueOf(value[1]);
					college = value[0];
				}
			}
			context.write(key, new Text(college + " - " + highestScore));
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
		job.setReducerClass(Reduce.class);
		
		job.setInputFormatClass(KeyValueTextInputFormat.class); 
		job.setOutputFormatClass(TextOutputFormat.class);
		
		FileInputFormat.setInputPaths(job, in);
		FileOutputFormat.setOutputPath(job, out);
		
		job.setJarByClass(ExamScoresAverageBySubjectAndSchool.class);
		job.submit();
		
	}

}
