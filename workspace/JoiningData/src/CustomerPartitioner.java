import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.mapreduce.Partitioner;


public class CustomerPartitioner extends Partitioner<CustomerAndTagKey, DoubleWritable> {

	@Override
	public int getPartition(CustomerAndTagKey key, DoubleWritable value, int noOfReducers) {
		return key.getCustID() % noOfReducers;
	}
}
