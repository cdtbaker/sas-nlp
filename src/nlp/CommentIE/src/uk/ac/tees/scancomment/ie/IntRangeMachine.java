package uk.ac.tees.scancomment.ie;

import java.util.Collection;

public class IntRangeMachine extends Machine {

	public IntRangeMachine(Collection<String> variableNames) {
		
		super();
		
		State q00 = new State(0);
		State q10 = new State(10);
		State q20 = new State(20);
		State q30 = new State(30);
		State q40 = new State(40);
		State q50 = new State(50, true);
		State q60 = new State(60);
		State q70 = new State(70);
		State q80 = new State(80);
		State q90 = new State(90);
		State q100 = new State(100);
		State q110 = new State(110,true);
		
		// recognise first argument
		q00.add(new VariableConsumingTransition(q10, "a0", variableNames));
		
		// expressions of modality
		//q10.setEpsilon(new EpsilonTransition(q40));
		q10.add(new Transition(q40, "is"));
		q10.add(new Transition(q20, "has"));
		q10.add(new Transition(q20, "ought"));
		q10.add(new Transition(q30, "should"));
		q10.add(new Transition(q30, "must"));
		q10.add(new Transition(q30, "will"));
		q20.add(new Transition(q30, "to"));
		q30.add(new Transition(q40, "be"));
		
		// expressions of polarity
		q40.add(new Transition(q50, "positive", new Action("op", "gt")));
		q40.add(new Transition(q50, "negative", new Action("op", "lt")));
		q40.add(new Transition(q50, "non-positive", new Action("op", "le")));
		q40.add(new Transition(q50, "non-negative", new Action("op", "ge")));
		
		// recognise equalities
		q40.add(new Transition(q50, "zero", new Action("a1", "0")));
		q40.add(new NumberConsumingTransition(q50, "a1"));
		
		
		// expression of relations
		q10.add(new Transition(q100, "equals", new Action("op", "eq")));
		q10.add(new Transition(q100, "does not equal", new Action("op", "ne")));
		q40.add(new Transition(q60, "greater"));
		q40.add(new Transition(q60, "higher"));
		q40.add(new Transition(q60, "larger"));
		q40.add(new Transition(q60, "more"));
		q40.add(new Transition(q70, "less"));
		q40.add(new Transition(q70, "lower"));
		q40.add(new Transition(q70, "smaller"));
		q40.add(new Transition(q80, "above"));
		q40.add(new Transition(q90, "below"));
		q40.add(new Transition(q100, "!=", new Action("op", "ne")));
		q40.add(new Transition(q100, "=", new Action("op", "eq")));
		q40.add(new Transition(q100, "==", new Action("op", "eq")));
		q40.add(new Transition(q100, "equal to", new Action("op", "eq")));
		q40.add(new Transition(q100, "not equal to", new Action("op", "ne")));
		q40.add(new Transition(q100, "equals", new Action("op", "eq")));
		q40.add(new Transition(q100, ">", new Action("op", "gt")));
		q40.add(new Transition(q100, ">=", new Action("op", "ge")));
		q40.add(new Transition(q100, "<", new Action("op", "lt")));
		q40.add(new Transition(q100, "<=", new Action("op", "le")));
		q60.add(new Transition(q80, "than"));
		q70.add(new Transition(q90, "than"));
		q80.setEpsilon(new EpsilonTransition(q100, new Action("op", "gt")));
		q80.add(new Transition(q100, "or equal to", new Action("op", "ge")));
		q90.setEpsilon(new EpsilonTransition(q100, new Action("op", "lt")));
		q90.add(new Transition(q100, "or equal to", new Action("op", "le")));
		
		// recognise second argument
		q100.add(new NumberConsumingTransition(q110, "a1"));
		q100.add(new VariableConsumingTransition(q110, "a1", variableNames));
		 
		initial = q00;
	}

	
}
