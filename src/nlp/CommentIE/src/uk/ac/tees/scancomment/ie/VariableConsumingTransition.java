package uk.ac.tees.scancomment.ie;

import java.util.Collection;
import java.util.List;
import java.util.Map;


public class VariableConsumingTransition extends Transition {
	
	private final Collection<String> variableNames;
	
	public VariableConsumingTransition(State next, String slot, Collection<String> variableNames) {
		super(next, null, new Action(slot, "$var"));
		this.variableNames = variableNames;
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
		return variableNames.contains(token);
	}
}
