package main.analyses.SimpleRangeAnalysis;

import edu.cmu.cs.crystal.util.Copyable;

public enum PositiveNegativeLattice implements Copyable{

	POS(">0"),NEG("<0"),ZERO("==0"), NOT_SURE("Not sure"),BOTTOM("");

	@Override
	public Copyable copy() {
		return this;
	}
	
	public String desc;
	
	PositiveNegativeLattice(String desc){
		this.desc = desc;
	}
	
}
