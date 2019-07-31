package jUnitTests;
import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import joinObjects.LoanIDandTagKey;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mrunit.mapreduce.MapDriver;
import org.apache.hadoop.mrunit.mapreduce.ReduceDriver;
import org.junit.Before;
import org.junit.Test;

import dataTypes.LoanRecord;
import dataTypes.MetricsOutput;
import dataTypes.MetricsRecord;



public class TestJob2 {

	ReduceDriver<IntWritable, MetricsRecord,IntWritable, MetricsRecord> combineDriver;
	ReduceDriver<IntWritable, MetricsRecord,MetricsOutput,IntWritable> reduceDriver;
	
	@Before
	public void setUp() {
		combineDriver = ReduceDriver.newReduceDriver(new supportingJobs.Job2_CreateMetrics.Combine());
		reduceDriver = ReduceDriver.newReduceDriver(new supportingJobs.Job2_CreateMetrics.Reduce());
	}
	 
	@Test
	public void testCombine() throws IOException {
		IntWritable key = new IntWritable(1);
		Integer[] m1 = {1,1,1,1,1};
		Integer[] m2 = {2,3,4,5,6};
		Integer[] m3 = {3,4,5,6,7};
		
		MetricsRecord mr1 = new MetricsRecord();
		MetricsRecord mr2 = new MetricsRecord();
		MetricsRecord mr3 = new MetricsRecord();
		
		mr1.addMetrics(m1);
		mr2.addMetrics(m2);
		mr3.addMetrics(m3);	
		
		List<MetricsRecord> values = new ArrayList<MetricsRecord>();

		values.add(mr1);
		values.add(mr2);
		
		combineDriver.addInput(key,values);
		combineDriver.addOutput(key,mr3);
		combineDriver.runTest();
	}
	
	@Test
	public void testReduce() throws IOException {
		IntWritable key = new IntWritable(1);
		Integer[] m1 = {1,1,1,1,1};
		Integer[] m2 = {2,3,4,5,6};
		
		MetricsRecord mr1 = new MetricsRecord();
		MetricsRecord mr2 = new MetricsRecord();
		
		mr1.addMetrics(m1);
		mr2.addMetrics(m2);

		List<MetricsRecord> values = new ArrayList<MetricsRecord>();

		values.add(mr1);
		values.add(mr2);
		
		reduceDriver.addInput(key,values);
		reduceDriver.addOutput(new MetricsOutput(1,12d,16d,20d,24d,28d),key);
		reduceDriver.runTest();
	}

}
