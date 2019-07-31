import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

// This Comparator will create the Group and the list of values in that Group 
// here the group is subject
public class SubjectComparator extends WritableComparator {
	
	// This constructor is needed to create instances.
	public SubjectComparator() {
		super(ExamSubjectAndScoreKey.class, true);
	}

	public int compare(WritableComparable wc1, WritableComparable wc2) {
		ExamSubjectAndScoreKey key1 = (ExamSubjectAndScoreKey)wc1;
		ExamSubjectAndScoreKey key2 = (ExamSubjectAndScoreKey)wc2;
		return key1.getSubject().compareTo(key2.getSubject());
	}
}
