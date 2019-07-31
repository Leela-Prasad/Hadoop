import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;

public class SEWritable implements Writable {

	private int postType;
	private int parentID;
	private String date;
	private int noOfComments;
	private String text;
	
	public SEWritable() {
		
	}
	
	public SEWritable(String text) {
		String[] values = text.split(",");
		this.postType = Integer.valueOf(values[0]);
		if(postType == 2) { 
			this.parentID = Integer.valueOf(values[1]);
		}
		this.date = values[2];
		this.noOfComments = Integer.valueOf(values[3]);
		
		StringBuilder sb = new StringBuilder();
		for(int i=4; i<values.length; ++i) {
			sb.append("," + values[i]);
		}
		this.text = sb.toString();
	}
	
	@Override
	public void readFields(DataInput arg0) throws IOException {
		this.postType = arg0.readInt();
		this.parentID = arg0.readInt();
		this.date = arg0.readUTF();
		this.noOfComments = arg0.readInt();
		this.text = arg0.readUTF();
	}

	@Override
	public void write(DataOutput arg0) throws IOException {
		arg0.writeInt(this.postType);
		arg0.writeInt(this.parentID);
		arg0.writeUTF(this.date);
		arg0.writeInt(this.noOfComments);
		arg0.writeUTF(this.text);
	}

	public int getPostType() {
		return postType;
	}

	public void setPostType(int postType) {
		this.postType = postType;
	}

	public int getParentID() {
		return parentID;
	}

	public void setParentID(int parentID) {
		this.parentID = parentID;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public int getNoOfComments() {
		return noOfComments;
	}

	public void setNoOfComments(int noOfComments) {
		this.noOfComments = noOfComments;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}
