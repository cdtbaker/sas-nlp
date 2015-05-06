package uk.ac.tees.scancomment.ie;

import java.util.List;
import java.util.Map;


public class EpsilonTransition extends Transition {
	
	public EpsilonTransition(State next) {
		super(next, null, null);
	}
	
	public EpsilonTransition(State next, Action action) {
		super(next, null, action);
	}
	
	public String getLabel() {
		return "";
	}
	
	public boolean follow(List<String> tokens, Map<String,String> frame) {
	
		if (action != null) action.apply(null, frame);
		
		return next.recognise(tokens, frame);
		
	}

	public String toDot(State origin) {
		if (this.action == null)
			return String.format("%s -> %s [label=\"&epsilon;\"];", origin.toString(), next.toString());
		else
			return String.format("%s -> %s [label=\"&epsilon; : %s\"];", origin.toString(), next.toString(), action.toString());
	}
	

}
