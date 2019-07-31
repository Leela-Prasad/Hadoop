package jUnitTests;

import static org.junit.Assert.*;

import org.junit.Test;

import dataTypes.MetricsOutput;
import dataTypes.MetricsRecord;

public class TestMetricsOutput {

	@Test
	public void testConstructor() {
		MetricsRecord mr = new MetricsRecord();
		Integer[] metrics = {3,4,5,6,7};
		mr.addMetrics(metrics);
		MetricsOutput mo = new MetricsOutput(1, mr);
		assertTrue(1 == mo.getRating());
		assertTrue(12 ==  mo.getFull());
		assertTrue(16 == mo.getLate1());
		assertTrue(20 == mo.getLate2());
		assertTrue(24 == mo.getLate3());
		assertTrue(28 == mo.getNotrepaid());
	}

}
