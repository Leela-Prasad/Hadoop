package joinObjects;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Partitioner;


public class LoanIDPartitioner extends Partitioner<LoanIDandTagKey, Text> {

	@Override
	public int getPartition(LoanIDandTagKey key, Text value, int noOfPartitions) {
		// TODO Auto-generated method stub
		return key.getLoanID() % noOfPartitions;
	}

}
