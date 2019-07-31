import static org.junit.Assert.*;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mrunit.mapreduce.MapDriver;
import org.junit.Before;
import org.junit.Test;

public class TestCommonPhrases {

	MapDriver<Text, Text,Text, Text> ppMapDriver;
	MapDriver<Text, Text,Text, LongWritable> mapDriver;
	
	@Before
	public void setUp() throws Exception {
		ppMapDriver = MapDriver.newMapDriver(new CommonPhrases.PreProcessing());
		mapDriver = MapDriver.newMapDriver(new CommonPhrases.MapClass());
	}

	@Test
	public void testNoCommonWords() throws IOException {
		ppMapDriver.addInput(new Text("key"), new Text("I like to eat Cookies"));
		ppMapDriver.addOutput(new Text("key"), new Text("i like to eat cookies"));
		ppMapDriver.runTest();
	}

	@Test
	public void testOneCommonWord() throws Exception {
		ppMapDriver.addInput(new Text("key"), new Text("I like to eat that Cookies"));
		ppMapDriver.addOutput(new Text("key"), new Text("i like to eat  cookies"));
		ppMapDriver.runTest();
	}
	
	@Test
	public void testMapClass() throws IOException {
		mapDriver.addInput(new Text("key"), new Text("1,,some date,0,MRUnit is an easy to use testing framework for Hadoop"));
		mapDriver.addOutput(new Text("testing framework"), new LongWritable(1));
		mapDriver.runTest();
		assertEquals(3, mapDriver.getCounters().findCounter(CommonPhrases.MapClass.Removed_Words.TWO_LETTERS).getValue());
	}
}
