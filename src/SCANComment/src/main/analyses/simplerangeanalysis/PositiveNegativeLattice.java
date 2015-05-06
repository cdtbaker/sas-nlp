package main.analyses.simplerangeanalysis;

import main.analyses.abstraction.ILattice;
import edu.cmu.cs.crystal.util.Copyable;

public enum PositiveNegativeLattice implements ILattice, Copyable{

	POS(">0"),NEG("<0"),ZERO("==0"), NOT_SURE(" cannot guarentee state"),BOTTOM("");

	@Override
	public Copyable copy() {
		return this;
	}
	
	public String desc;
	
	PositiveNegativeLattice(String desc){
		this.desc = desc;
	}

	@Override
	public Enum<? extends ILattice> unknown() {
		return NOT_SURE;
	}
	
}
