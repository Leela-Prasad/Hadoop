package jUnitTests;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.Test;

import dataTypes.LoanRecord;

public class TestLoanRecord {

	@Test
	public void testFinalStatusOk() {
		LoanRecord lr = new LoanRecord(1, 1, new Date(), 6, 1000, new BigDecimal(1000), 0);
		assertEquals(0, lr.finalStatus());
	}
	
	@Test
	public void testFinalStatusNotRepaid() {
		LoanRecord lr = new LoanRecord(1, 1, new Date(), 6, 1000, new BigDecimal(900), 0);
		assertEquals(4, lr.finalStatus());
	}
	
	@Test
	public void testFinalStatusOther() {
		LoanRecord lr = new LoanRecord(1, 1, new Date(), 6, 1000, new BigDecimal(1000), 1);
		assertEquals(1, lr.finalStatus());
		lr.receivePayment(new BigDecimal(0), true);
		assertEquals(2, lr.finalStatus());
		lr.receivePayment(new BigDecimal(0), true);
		assertEquals(3, lr.finalStatus());
		lr.receivePayment(new BigDecimal(0), true);
		assertEquals(3, lr.finalStatus());
		
	}
	
	@Test
	public void testFinishesInFuture() {
		LoanRecord lr = new LoanRecord(1, 1, new GregorianCalendar(2011,1,1).getTime(), 6, 1000, new BigDecimal(1000), 1);
		assertEquals(true, lr.finishesInFuture(new GregorianCalendar(2011,3,1).getTime()));
		assertEquals(false, lr.finishesInFuture(new GregorianCalendar(2011,8,1).getTime()));
	}

}
