package uk.ac.tees.scancomment.ie;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class NumberConsumingTransition extends Transition {

	private static final Set<String> dummyNumbers = new HashSet<String>();
	static {
		dummyNumbers.add("0");
		dummyNumbers.add("1");
		dummyNumbers.add("zero");
		dummyNumbers.add("one");
	}
	
	public NumberConsumingTransition(State next, String slot) {
		super(next, null, new Action(slot, "$num"));
	}

	@Override
	public String getLabel() {
		return "$num : " + action.slot + "=$num";
	}
	
	@Override
	public boolean follow(List<String> tokens, Map<String, String> frame) {
		
		String recognised = tokens.remove(0);
		if (action != null) action.apply(recognised, frame);
		return next.recognise(tokens, frame);

	}

	@Override
	public boolean matches(List<String> tokens) {
		String token = tokens.get(0);
		return dummyNumbers.contains(token);
	}
	
	
}
