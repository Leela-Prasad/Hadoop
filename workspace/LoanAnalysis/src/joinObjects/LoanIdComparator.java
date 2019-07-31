package joinObjects;

import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

public class LoanIdComparator extends WritableComparator {

	public LoanIdComparator() {
		super(LoanIDAndTagKey.class,true);
	}
	
	@Override
	public int compare(WritableComparable wc1, WritableComparable wc2) {
		LoanIDAndTagKey key1 = (LoanIDAndTagKey)wc1;
		LoanIDAndTagKey key2 = (LoanIDAndTagKey)wc2;
		return key1.getLoanId().compareTo(key2.getLoanId());
	}
}
