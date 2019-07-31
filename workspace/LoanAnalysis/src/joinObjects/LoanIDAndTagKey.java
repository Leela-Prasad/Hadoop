package joinObjects;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.WritableComparable;

public class LoanIDAndTagKey implements WritableComparable<LoanIDAndTagKey> {

	private Integer loanId;
	private Integer tag; //1=ratings, 2=loan data 3= payments
	
	public LoanIDAndTagKey() {
		super();
	}

	public LoanIDAndTagKey(Integer loanId, Integer tag) {
		super();
		this.loanId = loanId;
		this.tag = tag;
	}

	public Integer getLoanId() {
		return loanId;
	}

	public void setLoanId(Integer loanId) {
		this.loanId = loanId;
	}

	public Integer getTag() {
		return tag;
	}

	public void setTag(Integer tag) {
		this.tag = tag;
	}

	@Override
	public void readFields(DataInput arg0) throws IOException {
		this.loanId = arg0.readInt();
		this.tag = arg0.readInt();
	}

	@Override
	public void write(DataOutput arg0) throws IOException {
		arg0.writeInt(loanId);
		arg0.writeInt(tag);
	}

	@Override
	public int compareTo(LoanIDAndTagKey o) {
		int result = loanId.compareTo(o.getLoanId());
		if(result == 0) {
			result = tag.compareTo(o.getTag());
		}
		return result;
	}

}
