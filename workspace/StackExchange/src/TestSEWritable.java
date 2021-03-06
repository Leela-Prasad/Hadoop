import static org.junit.Assert.*;

import org.junit.Test;


public class TestSEWritable {

	@Test
	public void testConstructorQuestion() {
		
		String value = "1,,2011-03-13T19:49:22.470,0,What's the difference?";
		SEWritable seWritable = new SEWritable(value);		
		assertEquals(1, seWritable.getPostType());
		assertEquals(0,seWritable.getNoOfComments());
		assertEquals("2011-03-13T19:49:22.470", seWritable.getDate());
		assertEquals(",What's the difference?", seWritable.getText());
	}
	
	@Test
	public void testConstructorAnswer() {
		
		String value = "2,6,2011-03-13T19:49:22.470,4,Squats, deadlifts, and bench";
		SEWritable seWritable = new SEWritable(value);		
		assertEquals(6, seWritable.getParentID());
		assertEquals(2, seWritable.getPostType());
		assertEquals(4,seWritable.getNoOfComments());
		assertEquals("2011-03-13T19:49:22.470", seWritable.getDate());
		assertEquals(",Squats, deadlifts, and bench", seWritable.getText());
	}

}
