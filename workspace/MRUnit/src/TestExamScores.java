import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mrunit.mapreduce.MapDriver;
import org.apache.hadoop.mrunit.mapreduce.MapReduceDriver;
import org.apache.hadoop.mrunit.mapreduce.ReduceDriver;
import org.junit.Before;
import org.junit.Test;

public class TestExamScores {

	// Input and output types of map class
	MapDriver<LongWritable, Text, Text, AverageWritable> mapDriver;
	// Input and output types of reduce class
	ReduceDriver<Text, AverageWritable, Text, DoubleWritable> reduceDriver;
	// Input of map class, Input of reduce or combine class, Output of reduce class 
	MapReduceDriver<LongWritable, Text, Text, AverageWritable, Text, DoubleWritable> mapReduceDriver;
	// Input and output types of combiner class
	ReduceDriver<Text, AverageWritable,Text,AverageWritable> combinerDriver;
	@Before
	public void setUp() throws Exception {
		mapDriver = MapDriver.newMapDriver(new ExamScoreAnalysis.MapClass());
		reduceDriver = ReduceDriver.newReduceDriver(new ExamScoreAnalysis.Reduce());
		mapReduceDriver = MapReduceDriver.newMapReduceDriver(new ExamScoreAnalysis.MapClass(), new ExamScoreAnalysis.Reduce());
		combinerDriver = ReduceDriver.newReduceDriver(new ExamScoreAnalysis.Combine());
	}

	@Test
	public void testMapper() throws IOException {
		mapDriver.addInput(new LongWritable(2), new Text("4,abc,chemistry,20"));
		mapDriver.addOutput(new Text("chemistry"), new AverageWritable(20, 1));
		mapDriver.runTest();
	}

	@Test
	public void testReducer() throws IOException {
		List<AverageWritable> values = new ArrayList<>();
		AverageWritable aw1 = new AverageWritable(20, 2);
		AverageWritable aw2 = new AverageWritable(50, 3);
		AverageWritable aw3 = new AverageWritable(30, 2);
		AverageWritable aw4 = new AverageWritable(40, 3);
		values.add(aw1);
		values.add(aw2);
		values.add(aw3);
		values.add(aw4);
		reduceDriver.addInput(new Text("chemistry"), values);
		reduceDriver.addOutput(new Text("chemistry"), new DoubleWritable(14));
		reduceDriver.runTest();
	}
	
	@Test
	public void testMapReduce() throws IOException {
		mapReduceDriver.addInput(new LongWritable(2), new Text("4,abc,chemistry,20"));
		mapReduceDriver.addInput(new LongWritable(3), new Text("4,abc,chemistry,30"));
		mapReduceDriver.addInput(new LongWritable(5), new Text("4,abc,physics,20"));
		mapReduceDriver.addInput(new LongWritable(6), new Text("4,abc,physics,30"));
		mapReduceDriver.addInput(new LongWritable(7), new Text("4,abc,maths,100"));
		
		mapReduceDriver.addOutput(new Text("chemistry"), new DoubleWritable(25));
		mapReduceDriver.addOutput(new Text("maths"), new DoubleWritable(100));
		mapReduceDriver.addOutput(new Text("physics"), new DoubleWritable(25));
		mapReduceDriver.runTest();
	}
	
	@Test
	public void testCombiner() throws IOException {
		List<AverageWritable> values = new ArrayList<>();
		AverageWritable aw1 = new AverageWritable(20, 2);
		AverageWritable aw2 = new AverageWritable(50, 3);
		AverageWritable aw3 = new AverageWritable(30, 2);
		AverageWritable aw4 = new AverageWritable(40, 3);
		values.add(aw1);
		values.add(aw2);
		values.add(aw3);
		values.add(aw4);
		combinerDriver.addInput(new Text("chemistry"), values);
		combinerDriver.addOutput(new Text("chemistry"), new AverageWritable(140,10));
		combinerDriver.runTest();
	}
}
