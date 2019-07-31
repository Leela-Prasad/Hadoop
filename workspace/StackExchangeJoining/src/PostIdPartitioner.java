import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Partitioner;


public class PostIdPartitioner extends Partitioner<PostIdandTagKey, IntWritable> {

	@Override
	public int getPartition(PostIdandTagKey key, IntWritable value, int noOfReducers) {
		return key.getPostId() % noOfReducers;
	}
}
