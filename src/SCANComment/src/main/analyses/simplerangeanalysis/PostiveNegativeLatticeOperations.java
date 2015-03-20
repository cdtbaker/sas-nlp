package main.analyses.simplerangeanalysis;

import edu.cmu.cs.crystal.simple.SimpleLatticeOperations;

public class PostiveNegativeLatticeOperations extends
		SimpleLatticeOperations<PositiveNegativeLattice> {

	@Override
	public boolean atLeastAsPrecise(PositiveNegativeLattice l,
			PositiveNegativeLattice r) {
		if (l == PositiveNegativeLattice.NOT_SURE) {
			
			return false;
		}
		return true;
	}

	@Override
	public PositiveNegativeLattice bottom() {
		return PositiveNegativeLattice.BOTTOM;
		 
	}

	@Override
	public PositiveNegativeLattice copy(PositiveNegativeLattice element) {
		return element;
	}

	@Override
	public PositiveNegativeLattice join(PositiveNegativeLattice l,
			PositiveNegativeLattice r) {
		
		if (l == r)
			return l;
		else if (l == PositiveNegativeLattice.BOTTOM)
			return r;
		else if (r == PositiveNegativeLattice.BOTTOM)
			return l;
		else 
			return PositiveNegativeLattice.NOT_SURE;
	}

}
