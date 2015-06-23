package uk.ac.tees.scancomment.ie;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public abstract class Machine {

	protected State initial;
	
	protected Machine() {
		initial = null;
	}
	
	public String generate() {
		StringBuilder builder = new StringBuilder();
		for (String string : initial.generate()) {
			builder.append(string);
			builder.append("\n");
		}
		return builder.toString();
	}

	public Map<String,String> recognise(String input) {	
		List<String> tokens = new LinkedList<String>();
		for (String token : input.split("\\s")) tokens.add(token);
		return recognise(tokens);
	}
	
	protected Map<String,String> recognise(List<String> tokens) {
		Map<String,String> frame = new HashMap<String,String>();
		if (initial.recognise(tokens, frame))
			return frame;
		else
			return null;
	}

	public String toDigraph() {
		return initial.toDigraph();
	}
	
}
