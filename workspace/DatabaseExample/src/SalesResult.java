import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.hadoop.mapreduce.lib.db.DBWritable;

public class SalesResult implements DBWritable {

	private String region;
	private Long quantity;
	
	public SalesResult() {
		super();
	}

	public SalesResult(String region, Long quantity) {
		super();
		this.region = region;
		this.quantity = quantity;
	}

	
	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public Long getQuantity() {
		return quantity;
	}

	public void setQuantity(Long quantity) {
		this.quantity = quantity;
	}

	@Override
	public void readFields(ResultSet arg0) throws SQLException {
		this.region = arg0.getString("region");
		this.quantity = arg0.getLong("quantity");
	}

	@Override
	public void write(PreparedStatement arg0) throws SQLException {
		arg0.setString(1, region);
		arg0.setLong(2, quantity);
	}

}
