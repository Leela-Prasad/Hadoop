import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.WritableComparable;


public class PostIdandTagKey implements WritableComparable {

	private Integer postId;
	private Integer tag; //1=votes, 2=posts
	
	public PostIdandTagKey() {}
		
	public PostIdandTagKey(Integer postId, Integer tag) {
		super();
		this.postId = postId;
		this.tag = tag;
	}

	public Integer getPostId() {
		return postId;
	}

	public void setPostId(Integer postId) {
		this.postId = postId;
	}

	public Integer getTag() {
		return tag;
	}

	public void setTag(Integer tag) {
		this.tag = tag;
	}

	@Override
	public void readFields(DataInput arg0) throws IOException {
		this.postId = arg0.readInt();
		this.tag = arg0.readInt();
	}

	@Override
	public void write(DataOutput arg0) throws IOException {
		arg0.writeInt(postId);
		arg0.writeInt(tag);
	}

	@Override
	public int compareTo(Object arg0) {
		PostIdandTagKey obj = (PostIdandTagKey)arg0;
		int result = this.postId.compareTo(obj.getPostId());
		if (result == 0) {
			result = this.tag.compareTo(obj.getTag());
		}
		return result;
	}

}
