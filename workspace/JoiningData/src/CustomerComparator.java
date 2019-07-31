import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;


public class CustomerComparator extends WritableComparator {

	public CustomerComparator() {
		super(CustomerAndTagKey.class,true);
	}
	
	@Override
	public int compare(WritableComparable wc1, WritableComparable wc2) {
		CustomerAndTagKey key1 = (CustomerAndTagKey)wc1;
		CustomerAndTagKey key2 = (CustomerAndTagKey)wc2;
		return key1.getCustID().compareTo(key2.getCustID());	
	}
}

