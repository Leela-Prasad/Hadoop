import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.db.DBConfiguration;
import org.apache.hadoop.mapreduce.lib.db.DBOutputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.KeyValueTextInputFormat;


public class QuestionsByDate  {
	
	public static class MapClass extends Mapper<Text, Text,Text, LongWritable> {
		
		public void map (Text key, Text value, Context context) throws IOException, InterruptedException {
			SEWritable seWritableValue = new SEWritable(value.toString());
			
			String date = seWritableValue.getDate().substring(0, 7);
			
			if(seWritableValue.getPostType() == 1)
				context.write(new Text(date), new LongWritable(1));
		}
	}
	
	public static class Reduce extends Reducer<Text, LongWritable,QBDRecord,Text> {

		public void reduce(Text key, Iterable<LongWritable> values, Context context) throws IOException, InterruptedException {
			Long sum = 0L;
			Iterator<LongWritable> it = values.iterator();
			while (it.hasNext()) {
				sum += it.next().get();
			}
			
			context.write(new QBDRecord(key.toString(), sum), new Text(""));
		}
	}
	
	public static void main(String[] args) throws Exception {
		Path in = new Path(args[0]);
		String out = args[1];
        
        Configuration conf = new Configuration();
        DBConfiguration.configureDB(conf, "com.mysql.jdbc.Driver", "jdbc:mysql://localhost/" + out + "?user=root&password=xrBouqf2");
        
		Job job = Job.getInstance(conf);		
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(LongWritable.class);
		
		job.setMapperClass(MapClass.class);
		job.setReducerClass(Reduce.class);
		
		job.setInputFormatClass(KeyValueTextInputFormat.class); 
		job.setOutputFormatClass(DBOutputFormat.class);
		
		FileInputFormat.setInputPaths(job, in);
		
		String[] fieldNames = {"date","questions"};
		DBOutputFormat.setOutput(job, "qbd", fieldNames);
		
		job.setJarByClass(QuestionsByDate.class);
		job.submit();
		
	}

}