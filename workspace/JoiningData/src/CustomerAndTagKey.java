import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.WritableComparable;


public class CustomerAndTagKey implements WritableComparable {

	private Integer custID;
	private Integer tag; //1=country, 2=sales
	
	public CustomerAndTagKey() {}
		
	public CustomerAndTagKey(Integer custID, Integer tag) {
		super();
		this.custID = custID;
		this.tag = tag;
	}

	public Integer getCustID() {
		return custID;
	}

	public void setCustID(Integer custID) {
		this.custID = custID;
	}

	public Integer getTag() {
		return tag;
	}

	public void setTag(Integer tag) {
		this.tag = tag;
	}

	@Override
	public void readFields(DataInput arg0) throws IOException {
		this.custID = arg0.readInt();
		this.tag = arg0.readInt();
	}

	@Override
	public void write(DataOutput arg0) throws IOException {
		arg0.writeInt(custID);
		arg0.writeInt(tag);
	}

	@Override
	public int compareTo(Object arg0) {
		CustomerAndTagKey obj = (CustomerAndTagKey)arg0;
		int result = this.custID.compareTo(obj.getCustID());
		if (result == 0) {
			result = this.tag.compareTo(obj.getTag());
		}
		return result;
	}
	

}
