package dataTypes;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;

public class MetricsOutput implements Writable {

	private Integer[] metrics = {0, 0, 0, 0, 0};

	public void addLoan(int status) {
		metrics[status]++;
	}
	
	public void addMetrics(Integer[] newMetrics) {
		for(int i=0; i<metrics.length; ++i) {
			metrics[i] += newMetrics[i];
		}
	}
	
	public Integer[] getMetrics() {
		return metrics;
	}

	public void setMetrics(Integer[] metrics) {
		this.metrics = metrics;
	}

	@Override
	public void readFields(DataInput arg0) throws IOException {
		for(int i=0;i<metrics.length; ++i) {
			this.metrics[i] = arg0.readInt();
		}
	}

	@Override
	public void write(DataOutput arg0) throws IOException {
		for(int i=0;i<metrics.length; ++i) {
			arg0.writeInt(metrics[i]);
		}
		
	}
	
	
}
