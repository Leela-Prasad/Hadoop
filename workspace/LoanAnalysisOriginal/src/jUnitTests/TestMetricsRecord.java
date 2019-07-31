package jUnitTests;

import static org.junit.Assert.*;

import org.junit.Test;

import dataTypes.MetricsRecord;

public class TestMetricsRecord {

	@Test
	public void testAddLoan() {
		MetricsRecord mr = new MetricsRecord();
		int result = mr.getMetrics()[2];
		assertEquals(0,result);
	
		mr.addLoan(2);
		result = mr.getMetrics()[2];
		assertEquals(1,result);
	}

	@Test
	public void testAddMetrics() {
		MetricsRecord mr = new MetricsRecord();
		Integer[] m1 = {1,1,1,1,1};
		Integer[] m2 = {2,3,4,5,6};
		
		mr.addMetrics(m1);
		mr.addMetrics(m2);
	
		for (int i = 0; i <=4; i++) {
			int result = mr.getMetrics()[i];
			assertEquals(i+3,result);
		}
		
	}
}
