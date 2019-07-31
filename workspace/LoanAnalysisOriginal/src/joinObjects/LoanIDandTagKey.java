package joinObjects;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.WritableComparable;


public class LoanIDandTagKey implements WritableComparable {


	@Override
	public String toString() {
		return "LoanIDandTagKey [loanID=" + loanID + ", tag=" + tag + "]";
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((loanID == null) ? 0 : loanID.hashCode());
		result = prime * result + ((tag == null) ? 0 : tag.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LoanIDandTagKey other = (LoanIDandTagKey) obj;
		if (loanID == null) {
			if (other.loanID != null)
				return false;
		} else if (!loanID.equals(other.loanID))
			return false;
		if (tag == null) {
			if (other.tag != null)
				return false;
		} else if (!tag.equals(other.tag))
			return false;
		return true;
	}
	private Integer loanID;
	private Integer tag;
	
	public Integer getLoanID() {
		return loanID;
	}
	public void setLoanID(Integer loanID) {
		this.loanID = loanID;
	}
	public Integer getTag() {
		return tag;
	}
	public void setTag(Integer tag) {
		this.tag = tag;
	}
	
	public LoanIDandTagKey(Integer loanID, Integer tag) {
		super();
		this.loanID = loanID;
		this.tag = tag;
	}
	
	public LoanIDandTagKey() {}
	
	@Override
	public void readFields(DataInput arg0) throws IOException {
		this.loanID = arg0.readInt();
		this.tag = arg0.readInt();
	}
	@Override
	public void write(DataOutput arg0) throws IOException {
		arg0.writeInt(loanID);
		arg0.writeInt(tag);
	}
	@Override
	public int compareTo(Object o) {
		LoanIDandTagKey obj = (LoanIDandTagKey)o;
		int result = this.loanID.compareTo(obj.getLoanID());
		if (result ==0) {
			result = this.tag.compareTo(obj.getTag());
		}
		return result;
	}
	
	

}
