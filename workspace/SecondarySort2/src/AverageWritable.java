import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;

public class AverageWritable implements Writable {

	private double total;
	private long noOfRecords;
	
	// No arg constructor is mandatory as 
	// hadoop needs to serialize and deserialize
	public AverageWritable() {
		
	}
	
	public AverageWritable(double total, long noOfRecords) {
		super();
		this.total = total;
		this.noOfRecords = noOfRecords;
	}

	// This method is used to deserialize data.
	// While reading we need exactly match the order of elements
	// that is followed in write method, as hadoop will simply do
	// get(0), get(1), get(2) ...
	@Override
	public void readFields(DataInput arg0) throws IOException {
		this.total = arg0.readDouble();
		this.noOfRecords = arg0.readLong();
	}

	// This method is used to serialize data.
	@Override
	public void write(DataOutput arg0) throws IOException {
		arg0.writeDouble(total);
		arg0.writeLong(noOfRecords);
	}

	public double getTotal() {
		return total;
	}

	public void setTotal(double total) {
		this.total = total;
	}

	public long getNoOfRecords() {
		return noOfRecords;
	}

	public void setNoOfRecords(long noOfRecords) {
		this.noOfRecords = noOfRecords;
	}

}
