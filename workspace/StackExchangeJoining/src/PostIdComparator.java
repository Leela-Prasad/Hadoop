import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;


public class PostIdComparator extends WritableComparator {

	public PostIdComparator() {
		super(PostIdandTagKey.class,true);
	}
	
	@Override
	public int compare(WritableComparable wc1, WritableComparable wc2) {
		PostIdandTagKey key1 = (PostIdandTagKey)wc1;
		PostIdandTagKey key2 = (PostIdandTagKey)wc2;
		return key1.getPostId().compareTo(key2.getPostId());	
	}
}

