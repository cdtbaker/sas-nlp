package main.analyses.SimpleRangeAnalysis;

import org.eclipse.jdt.core.dom.MethodDeclaration;

import edu.cmu.cs.crystal.flow.ILatticeOperations;
import edu.cmu.cs.crystal.simple.AbstractingTransferFunction;
import edu.cmu.cs.crystal.simple.TupleLatticeElement;
import edu.cmu.cs.crystal.simple.TupleLatticeOperations;
import edu.cmu.cs.crystal.tac.model.AssignmentInstruction;
import edu.cmu.cs.crystal.tac.model.Variable;

public class TransferFunctions
		extends
		AbstractingTransferFunction<TupleLatticeElement<Variable, PositiveNegativeLattice>> {

	private TupleLatticeOperations<Variable, PositiveNegativeLattice> ops = new TupleLatticeOperations<Variable, PositiveNegativeLattice>(
			new PostiveNegativeLatticeOperations(),
			PositiveNegativeLattice.NOT_SURE);

	@Override
	public TupleLatticeElement<Variable, PositiveNegativeLattice> createEntryValue(
			MethodDeclaration m) {
		TupleLatticeElement<Variable, PositiveNegativeLattice> def = ops
				.getDefault();
		Variable thisVar = getAnalysisContext().getThisVariable();
		def.put(thisVar, PositiveNegativeLattice.POS);
		return def;
	}

	@Override
	public ILatticeOperations<TupleLatticeElement<Variable, PositiveNegativeLattice>> getLatticeOperations() {

		return ops;
	}

	@Override
	public TupleLatticeElement<Variable, PositiveNegativeLattice> transfer(
			AssignmentInstruction instr,
			TupleLatticeElement<Variable, PositiveNegativeLattice> value) {
		
		return super.transfer(instr, value);
	}



	
	
	

}
