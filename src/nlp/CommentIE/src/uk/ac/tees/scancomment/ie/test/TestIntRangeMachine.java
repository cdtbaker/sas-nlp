package uk.ac.tees.scancomment.ie.test;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import uk.ac.tees.scancomment.ie.IntRangeMachine;
import uk.ac.tees.scancomment.ie.Machine;

public class TestIntRangeMachine {

	private Machine intRangeMachine;
	
	@Before
	public void setup() {
		
		Set<String> variableNames = new HashSet<String>();
		variableNames.add("x");
		variableNames.add("y");
		variableNames.add("foo");
		variableNames.add("bar");
		
		intRangeMachine = new IntRangeMachine(variableNames);
	}
	
	@Test
	public void testZero() {
		Map<String,String> frame = intRangeMachine.recognise("foo is zero");
		assertNotNull(frame);
		assertEquals(frame.get("a0"), "foo");
		assertEquals(frame.get("a1"), "0");
	}

}
