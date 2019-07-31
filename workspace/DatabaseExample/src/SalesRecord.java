import java.math.BigDecimal;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.hadoop.mapreduce.lib.db.DBWritable;

public class SalesRecord implements DBWritable {

	private int id;
	private String region;
	private Date date;
	private int quantity;
	private BigDecimal price;
	
	public SalesRecord() {
		super();
	}

	public SalesRecord(int id, String region, Date date, int quantity, BigDecimal price) {
		super();
		this.id = id;
		this.region = region;
		this.date = date;
		this.quantity = quantity;
		this.price = price;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	@Override
	public void readFields(ResultSet arg0) throws SQLException {
		this.id = arg0.getInt("id");
		this.region = arg0.getString("region");
		this.date = arg0.getDate("date");
		this.quantity = arg0.getInt("quantity");
		this.price = arg0.getBigDecimal("price");
	}

	@Override
	public void write(PreparedStatement arg0) throws SQLException {
		arg0.setInt(1, id);
		arg0.setString(2, region);
		arg0.setDate(3, date);
		arg0.setInt(4, quantity);
		arg0.setBigDecimal(5, price);
	}
	
}
