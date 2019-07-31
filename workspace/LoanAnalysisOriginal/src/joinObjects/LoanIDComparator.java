package joinObjects;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;


public class LoanIDComparator extends WritableComparator {

	public LoanIDComparator() {
		super(LoanIDandTagKey.class,true);
	}
	
	@Override
	public int compare (WritableComparable wc1, WritableComparable wc2) {
		LoanIDandTagKey key1 = (LoanIDandTagKey)wc1;
		LoanIDandTagKey key2 = (LoanIDandTagKey)wc2;
		return key1.getLoanID().compareTo(key2.getLoanID());
	}
}