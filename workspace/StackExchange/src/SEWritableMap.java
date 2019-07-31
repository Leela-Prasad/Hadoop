import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class SEWritableMap extends Mapper<Text, Text,Text, SEWritable> {
	
	public void map (Text key, Text value, Context context) throws IOException, InterruptedException {
		SEWritable seWritableValue = new SEWritable(value.toString());
		
		if(seWritableValue.getPostType() == 2)
			key = new Text(String.valueOf(seWritableValue.getParentID()));
		
		context.write(key, new SEWritable(value.toString()));
	}

}