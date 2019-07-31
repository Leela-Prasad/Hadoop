package joinObjects;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Partitioner;

public class LoanIdPartitioner extends Partitioner<LoanIDAndTagKey, IntWritable> {

	@Override
	public int getPartition(LoanIDAndTagKey key, IntWritable value, int noOfReducers) {
		return key.getLoanId()%noOfReducers;
	}

}
