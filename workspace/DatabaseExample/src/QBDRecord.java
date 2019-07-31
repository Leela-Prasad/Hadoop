import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.hadoop.mapreduce.lib.db.DBWritable;

public class QBDRecord implements DBWritable {

	private String date;
	private Long questions;
	
	public QBDRecord() {
		super();
	}

	public QBDRecord(String date, Long questions) {
		super();
		this.date = date;
		this.questions = questions;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public Long getQuestions() {
		return questions;
	}

	public void setQuestions(Long questions) {
		this.questions = questions;
	}

	@Override
	public void readFields(ResultSet arg0) throws SQLException {
		this.date = arg0.getString("date");
		this.questions = arg0.getLong("questions");
	}

	@Override
	public void write(PreparedStatement arg0) throws SQLException {
		arg0.setString(1, date);
		arg0.setLong(2, questions);
	}
	
}
