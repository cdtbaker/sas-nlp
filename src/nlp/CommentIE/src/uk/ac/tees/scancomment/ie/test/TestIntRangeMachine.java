package uk.ac.tees.scancomment.ie.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

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
	public void testModalitiesAndZero() {
		
		String[] covered = new String[] {
			"is",
			"will be",
			"has to be",
			"ought to be",
			"should be",
			"must be",
		};
		
		
		for (String modality : covered) {
			String expression = String.format("foo %s zero", modality);
			Map<String,String> frame = intRangeMachine.recognise(expression);
			assertNotNull(expression, frame);
			assertEquals(expression, "foo", frame.get("a0"));
			assertEquals(expression, "0", frame.get("a1"));
		}
		
		String[] notCovered = new String[] {
				"can be",
				"could be",
				"might be"
		};
		
		for (String modality : notCovered) {
			String expression = String.format("foo %s zero", modality);
			Map<String,String> frame = intRangeMachine.recognise(expression);
			assertNull(expression, frame);
		}
		
	}
	
}
