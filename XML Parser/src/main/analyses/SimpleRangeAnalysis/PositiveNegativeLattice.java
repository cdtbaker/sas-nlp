package main.analyses.SimpleRangeAnalysis;

import edu.cmu.cs.crystal.util.Copyable;

public enum PositiveNegativeLattice implements Copyable{

	POS,NEG, NOT_SURE,BOTTOM;

	@Override
	public Copyable copy() {
		return this;
	}
	
}
