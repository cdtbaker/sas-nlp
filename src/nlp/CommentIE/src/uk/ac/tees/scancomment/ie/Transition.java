package uk.ac.tees.scancomment.ie;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Transition {

	protected final String[] recognise;
	protected final Action action;
	protected final State next;
	
	public Transition(State next, String recognise) {
		this(next, recognise, null);
	}
	
	public Transition(State next, String recognise, Action action) {
		this.recognise = recognise == null ?  null : recognise.split(" ");
		this.action = action;
		this.next = next;
	}
	
	public boolean follow(List<String> tokens, Map<String,String> frame) {
	
		// consume input tokens
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < recognise.length; i++) builder.append(tokens.remove(0) + " ");
		String recognised = builder.toString().trim();
		
		if (action != null) action.apply(recognised, frame);
		
		return next.recognise(tokens, frame);
		
	}
	
	
	public void generate(String history, List<String> strings) {
		next.generate(history + " " + getLabel(), strings);	
	}
	
	public String getLabel() {
		StringBuilder builder = new StringBuilder();
		builder.append(recognise[0]);
		for (int i = 1; i < recognise.length; i++) {
			builder.append(' ');
			builder.append(recognise[i]);
		}
		if (action != null) {
			builder.append(" : ");
			builder.append(action.toString());
		}
		return builder.toString();
	}
	
	public boolean matches(List<String> tokens) {
		
		if (tokens.size() < recognise.length) return false;
		
		Iterator<String> tokensToMatch = tokens.subList(0, recognise.length).iterator();
		for (int i = 0; i < recognise.length; i++)
			if (!recognise[i].equals(tokensToMatch.next()))
				return false;
		
		return true;
		
	}

	public String toDot(State origin) {
		return String.format("%s -> %s [label=\"%s\"];", origin.toString(), next.toString(), getLabel());
	}
	

}
