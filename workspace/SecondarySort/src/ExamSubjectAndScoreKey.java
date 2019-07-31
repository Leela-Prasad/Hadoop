import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.WritableComparable;

public class ExamSubjectAndScoreKey implements WritableComparable<ExamSubjectAndScoreKey>{

	private String subject;
	private Long score;
	
	public ExamSubjectAndScoreKey() {}
	
	public ExamSubjectAndScoreKey(String subject, Long score) {
		super();
		this.subject = subject;
		this.score = score;
	}
	
	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public Long getScore() {
		return score;
	}

	public void setScore(Long score) {
		this.score = score;
	}

	@Override
	public void readFields(DataInput arg0) throws IOException {
		this.subject=arg0.readUTF();
		this.score=arg0.readLong();
		
	}

	@Override
	public void write(DataOutput arg0) throws IOException {
		arg0.writeUTF(subject);
		arg0.writeLong(score);
	}

	// This compareTo method is used to sort Custom Key by BOTH VALUES DURING SORT STEP.
	@Override
	public int compareTo(ExamSubjectAndScoreKey o) {
		int result = this.getSubject().compareTo(o.getSubject());
		if(result == 0) {
			// Here we are multiplying with -1 as scores should be sorted in 
			// descending order.
			result = -1 * this.getScore().compareTo(o.getScore());
		}
		return result;
	}

}
