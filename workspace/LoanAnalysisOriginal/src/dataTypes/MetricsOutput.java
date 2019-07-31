package dataTypes;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapred.lib.db.DBWritable;

public class MetricsOutput implements DBWritable,Writable {

	private Integer rating;
	private Double full;
	private Double late1;
	private Double late2;
	private Double late3;
	private Double notrepaid;
	
	@Override
	public void readFields(ResultSet arg0) throws SQLException {
		this.rating = arg0.getInt(1);
		this.full = arg0.getDouble(2);
		this.late1= arg0.getDouble(3);
		this.late2= arg0.getDouble(4);
		this.late3= arg0.getDouble(5);
		this.notrepaid= arg0.getDouble(6);
	}
	
	public MetricsOutput() {}

	public MetricsOutput(Integer rating, Double full, Double late1,
			Double late2, Double late3, Double notrepaid) {
		super();
		this.rating = rating;
		this.full = full;
		this.late1 = late1;
		this.late2 = late2;
		this.late3 = late3;
		this.notrepaid = notrepaid;
	}
	
	public MetricsOutput(int r, MetricsRecord mr) {
		this.rating = r;
		int total = 0;
		for (int i = 0; i <=4; i++) {
			total += mr.getMetrics()[i];
		}
		
		this.full = 100*mr.getMetrics()[0].doubleValue()/total;
		this.late1 = 100*mr.getMetrics()[1].doubleValue()/total;
		this.late2 = 100*mr.getMetrics()[2].doubleValue()/total;
		this.late3 = 100*mr.getMetrics()[3].doubleValue()/total;
		this.notrepaid = 100*mr.getMetrics()[4].doubleValue()/total;
	}

	public Integer getRating() {
		return rating;
	}

	public void setRating(Integer rating) {
		this.rating = rating;
	}

	public Double getFull() {
		return full;
	}

	public void setFull(Double full) {
		this.full = full;
	}

	public Double getLate1() {
		return late1;
	}

	public void setLate1(Double late1) {
		this.late1 = late1;
	}

	public Double getLate2() {
		return late2;
	}

	public void setLate2(Double late2) {
		this.late2 = late2;
	}

	public Double getLate3() {
		return late3;
	}

	public void setLate3(Double late3) {
		this.late3 = late3;
	}

	public Double getNotrepaid() {
		return notrepaid;
	}

	public void setNotrepaid(Double notrepaid) {
		this.notrepaid = notrepaid;
	}

	@Override
	public void write(PreparedStatement arg0) throws SQLException {
		arg0.setInt( 1,rating);
		arg0.setDouble(2,full);
		arg0.setDouble(3,late1);
		arg0.setDouble(4,late2);
		arg0.setDouble(5,late3);
		arg0.setDouble(6,notrepaid);
		
		
	}

	@Override
	public void readFields(DataInput arg0) throws IOException {
		this.rating = arg0.readInt();
		this.full = arg0.readDouble();
		this.late1 = arg0.readDouble();
		this.late2 = arg0.readDouble();
		this.late3 = arg0.readDouble();
		this.notrepaid = arg0.readDouble();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((full == null) ? 0 : full.hashCode());
		result = prime * result + ((late1 == null) ? 0 : late1.hashCode());
		result = prime * result + ((late2 == null) ? 0 : late2.hashCode());
		result = prime * result + ((late3 == null) ? 0 : late3.hashCode());
		result = prime * result
				+ ((notrepaid == null) ? 0 : notrepaid.hashCode());
		result = prime * result + ((rating == null) ? 0 : rating.hashCode());
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
		MetricsOutput other = (MetricsOutput) obj;
		if (full == null) {
			if (other.full != null)
				return false;
		} else if (!full.equals(other.full))
			return false;
		if (late1 == null) {
			if (other.late1 != null)
				return false;
		} else if (!late1.equals(other.late1))
			return false;
		if (late2 == null) {
			if (other.late2 != null)
				return false;
		} else if (!late2.equals(other.late2))
			return false;
		if (late3 == null) {
			if (other.late3 != null)
				return false;
		} else if (!late3.equals(other.late3))
			return false;
		if (notrepaid == null) {
			if (other.notrepaid != null)
				return false;
		} else if (!notrepaid.equals(other.notrepaid))
			return false;
		if (rating == null) {
			if (other.rating != null)
				return false;
		} else if (!rating.equals(other.rating))
			return false;
		return true;
	}

	@Override
	public void write(DataOutput arg0) throws IOException {
		arg0.writeInt(rating);
		arg0.writeDouble(full);
		arg0.writeDouble(late1);
		arg0.writeDouble(late2);
		arg0.writeDouble(late3);
		arg0.writeDouble(notrepaid);
	}

}
