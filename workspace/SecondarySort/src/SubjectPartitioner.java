import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.Partitioner;

public class SubjectPartitioner extends Partitioner<ExamSubjectAndScoreKey, LongWritable>{

	// Here Shuffle step cannot do Shuffling based on a key as the key is custom key which contains 2 values
	// so all the keys will be different, so we will give partition number so that shuffle step will do shuffle
	// based on partition number. here we need to do shuffling based on subject we will take hashcode of subject
	// and do a module operator with number of slave nodes so that the partition number will not exceed number of slaves in the cluster.
	// so based on the partition number shuffle step will move that chuck of data to that slave node.
	//*** numPartitions means number of slave nodes in the cluster.
	@Override
	public int getPartition(ExamSubjectAndScoreKey arg0, LongWritable arg1, int numPartitions) {
		return arg0.getSubject().hashCode()%numPartitions;
	}

}
