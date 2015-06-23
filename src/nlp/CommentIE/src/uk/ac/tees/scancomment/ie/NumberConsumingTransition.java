package uk.ac.tees.scancomment.ie;

import java.util.List;
import java.util.Map;

public class NumberConsumingTransition extends Transition {

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
		try {
			Double.parseDouble(tokens.get(0));
			return true;
		} catch (NumberFormatException nfe) {
			return false;
		}
	}
	
	
}
