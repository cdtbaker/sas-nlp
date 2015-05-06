package uk.ac.tees.scancomment.ie;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;



public class State implements Comparable<State> {

	private final Integer id;
	private final Collection<Transition> transitions;
	private final boolean isFinalState;
	private EpsilonTransition epsilon;
	
	public State(Integer id) {
		this(id, false);
	}
	
	public State(Integer id, boolean isFinalState) {
		this.id = id;
		this.transitions = new LinkedList<Transition>();
		this.isFinalState = isFinalState;
		this.epsilon = null;
	}
	
	public void add(Transition transition) {
		// TODO check for ambiguous transitions
		transitions.add(transition);
	}
	
	@Override
	public int compareTo(State other) {
		return this.id.compareTo(other.id);
	}
	
	
	public List<String> generate() {
		List<String> strings = new LinkedList<String>();
		generate("", strings);
		return strings;
	}
	
	public void generate(String history, List<String> strings) {
		if (isFinalState)
			strings.add(history);
		
		for (Transition transition : transitions) 
			transition.generate(history, strings);
		
		if (epsilon != null)
			epsilon.generate(history, strings);
	}
	
	public boolean recognise(List<String> tokens, Map<String,String> frame) {
		
		if (tokens.size() == 0)
			return isFinalState;
		
		for (Transition transition : transitions)
			if (transition.matches(tokens)) 
				return transition.follow(tokens, frame);
		
		return epsilon == null ? false : epsilon.follow(tokens, frame);
	}

	public void setEpsilon(EpsilonTransition transition) {
		this.epsilon = transition;
	}
	
	public String toDot() {
		String shape = isFinalState ? "doublecircle" : "circle";
		return String.format("node [shape=%s] %s", shape, toString());
	}
	
	public String toString() {
		return "q" + id.toString();
	}
	
	public String toDigraph() {
		StringBuilder builder = new StringBuilder();
		
		builder.append("digraph g {\n");
		builder.append("\trankdir = LR;\n");
		builder.append("\tgraph [fontname = \"gill sans light\"];");
		builder.append("\tnode [fontname = \"gill sans light\"];");
		builder.append("\tedge [fontname = \"gill sans light\"];");

		builder.append("\n");
		
		SortedSet<State> states = getStates();
		for (State state : states) {
			builder.append("\n");
			builder.append("\t");
			builder.append(state.toDot());
			builder.append(";\n");
		}
		
		builder.append("\n");
		
		for (State state : states) {
			
			Set<Transition> transitions = new HashSet<Transition>(state.transitions);
			if (state.epsilon != null) transitions.add(state.epsilon);
			
			builder.append("\n");
			for (Transition transition : transitions) {
				builder.append("\t");
				builder.append(transition.toDot(state));
				builder.append("\n");
			}

		}
	
		builder.append("}");
		
		return builder.toString();
	}
	
	private SortedSet<State> getStates() {
		SortedSet<State> states = new TreeSet<State>();
		recursivelyCollectStates(states);
		return states;
	}
	
	private void recursivelyCollectStates(SortedSet<State> states) {
		states.add(this);
		for (Transition transition : transitions)
			transition.next.recursivelyCollectStates(states);
		if (epsilon != null)
			epsilon.next.recursivelyCollectStates(states);
			
	}

	
}
