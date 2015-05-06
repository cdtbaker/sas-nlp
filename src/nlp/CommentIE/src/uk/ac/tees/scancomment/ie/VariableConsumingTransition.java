package uk.ac.tees.scancomment.ie;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class VariableConsumingTransition extends Transition {

	private static final Set<String> dummyVariables = new HashSet<String>();
	static {
		dummyVariables.add("x");
		dummyVariables.add("y");
		dummyVariables.add("z");
	}
	
	public VariableConsumingTransition(State next, String slot) {
		super(next, null, new Action(slot, "$var"));
	}

	@Override
	public String getLabel() {
		return "$var : " + action.toString();
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
		return dummyVariables.contains(token);
	}
}
