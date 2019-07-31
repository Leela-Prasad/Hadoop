package jUnitTests;
import static org.junit.Assert.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import joinObjects.LoanIDandTagKey;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mrunit.mapreduce.MapDriver;
import org.apache.hadoop.mrunit.mapreduce.ReduceDriver;
import org.junit.Before;
import org.junit.Test;

import dataTypes.LoanRecord;

public class TestJob1 {

	MapDriver<LoanIDandTagKey,Text,LoanIDandTagKey,Text> preProcessDriver;
	ReduceDriver<LoanIDandTagKey, Text, IntWritable, LoanRecord> reduceDriver;
	
	@Before
	public void setup() throws Exception {
		preProcessDriver = MapDriver.newMapDriver(new supportingJobs.Job1_JoinAllData.PreProcessRating());
		reduceDriver = ReduceDriver.newReduceDriver(new supportingJobs.Job1_JoinAllData.Reduce());
	}
	
	@Test
	public void testPreProcessingInvalidRating() throws IOException {
		 LoanIDandTagKey key = new LoanIDandTagKey(1, 1);
		preProcessDriver.addInput(key,new Text("x"));
		preProcessDriver.addOutput(key,new Text("5"));
		preProcessDriver.runTest();
	}
	
	@Test
	public void testPreProcessingValidRating() throws IOException {
		 LoanIDandTagKey key = new LoanIDandTagKey(1, 1);
		preProcessDriver.addInput(key,new Text("7"));
		preProcessDriver.addOutput(key,new Text("7"));
		preProcessDriver.runTest();
	}
	
	@Test
	public void testReducerValid() throws IOException {
		LoanIDandTagKey key1 = new LoanIDandTagKey(1, 1);
		List<Text> values1 = new ArrayList<Text>();
		values1.add( new Text("2")); //rating 
		values1.add( new Text("2007-06-04,3,300")); //loanData
		values1.add( new Text("1,2007-07-04,100,2007-07-04,100"));
		values1.add( new Text("2,2007-08-04,100,2007-08-04,100"));
		values1.add( new Text("3,2007-09-04,100,2007-09-04,100"));
		reduceDriver.addInput(key1,values1);
		GregorianCalendar gcStart = new GregorianCalendar(2007,6,4);
		LoanRecord result = new LoanRecord(1,2,gcStart.getTime(),3,300,new BigDecimal(300),0);
		reduceDriver.addOutput(new IntWritable(1),result);
		reduceDriver.run();
	}
	
	@Test
	public void testReducerFuture() throws IOException {
		LoanIDandTagKey key1 = new LoanIDandTagKey(1, 1);
		List<Text> values1 = new ArrayList<Text>();
		values1.add( new Text("2")); //rating 
		values1.add( new Text("2015-06-04,3,300")); //loanData
		values1.add( new Text("1,2015-07-04,100,2015-07-04,100"));
		values1.add( new Text("2,2015-08-04,100,2015-08-04,100"));
		values1.add( new Text("3,2015-09-04,100,2015-09-04,100"));
		reduceDriver.addInput(key1,values1);
		//no output
		reduceDriver.run();
		assertEquals(1,reduceDriver.getCounters().findCounter(supportingJobs.Job1_JoinAllData.Reduce.RemovedLoans.FINISHES_IN_FUTURE).getValue());
	}

}
