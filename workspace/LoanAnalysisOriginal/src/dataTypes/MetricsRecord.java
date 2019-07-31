package dataTypes;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import org.apache.hadoop.io.Writable;


public class MetricsRecord implements Writable {

	private Integer[] metrics = {0,0,0,0,0};
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(metrics);
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
		MetricsRecord other = (MetricsRecord) obj;
		if (!Arrays.equals(metrics, other.metrics))
			return false;
		return true;
	}

	public void addLoan(int status) {
		metrics[status]++;
	}
	
	public void addMetrics(Integer[] newMetrics) {
		for (int i = 0; i <=4; i++) {
			metrics[i] += newMetrics[i];
		}
	}
		
	public Integer[] getMetrics() {
		return metrics;
	}

	public MetricsRecord() {}




	@Override
	public void readFields(DataInput arg0) throws IOException {
		for (int i = 0; i<=4; i++) {
			this.metrics[i] = arg0.readInt();
		}
		
	}

	@Override
	public void write(DataOutput arg0) throws IOException {
		for (int i = 0; i <=4; i++) {
			arg0.writeInt(metrics[i]);
		}
		
	}

}
