package main.analyses.SimpleRangeAnalysis;

import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import edu.cmu.cs.crystal.flow.ILatticeOperations;
import edu.cmu.cs.crystal.simple.AbstractingTransferFunction;
import edu.cmu.cs.crystal.simple.TupleLatticeElement;
import edu.cmu.cs.crystal.simple.TupleLatticeOperations;
import edu.cmu.cs.crystal.tac.model.BinaryOperation;
import edu.cmu.cs.crystal.tac.model.BinaryOperator;
import edu.cmu.cs.crystal.tac.model.LoadLiteralInstruction;
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
		def.put(thisVar, PositiveNegativeLattice.NEG);
		return def;
	}

	@Override
	public ILatticeOperations<TupleLatticeElement<Variable, PositiveNegativeLattice>> getLatticeOperations() {

		return ops;
	}

	@Override
	public TupleLatticeElement<Variable, PositiveNegativeLattice> transfer(
			LoadLiteralInstruction instr,
			TupleLatticeElement<Variable, PositiveNegativeLattice> value) {

		if (instr.isNumber()) {
			if (Integer.parseInt((String) instr.getLiteral()) > 0) {
				value.put(instr.getTarget(), PositiveNegativeLattice.POS);
				System.out.println(instr.getTarget());

			}
		}

		return value;
	}

	@Override
	public TupleLatticeElement<Variable, PositiveNegativeLattice> transfer(
			BinaryOperation binop,
			TupleLatticeElement<Variable, PositiveNegativeLattice> value) {

		
		System.out.println("OP1=" + binop.getOperand1());
		System.out.println("OP2=" + binop.getOperand2());
		BinaryOperator operator = binop.getOperator();
		double op1 = Double.parseDouble(binop.getOperand1().toString());
		double op2 = Double.parseDouble(binop.getOperand2().toString());

		Variable target = binop.getTarget();

		if (operator == BinaryOperator.ARIT_MULTIPLY
				|| operator == BinaryOperator.ARIT_DIVIDE) {
			if (op1 < 0 ^ op2 < 0) {
				value.put(target, PositiveNegativeLattice.NEG);
			} else if (op1 > 0 ^ op2 > 0) {
				value.put(target, PositiveNegativeLattice.POS);
			} else {
				value.put(target, PositiveNegativeLattice.ZERO);
			}
		}

		if (operator == BinaryOperator.ARIT_MODULO) {
			if (op1 > 0) {
				value.put(binop.getTarget(), PositiveNegativeLattice.POS);
			} else if (op1 < 0) {
				value.put(binop.getTarget(), PositiveNegativeLattice.NEG);
			} else {
				value.put(binop.getTarget(), PositiveNegativeLattice.ZERO);
			}
		}

		if (binop.getOperator() == BinaryOperator.ARIT_ADD) {
			if (value.get(target) == PositiveNegativeLattice.POS) {
				if (op1 + op1 >= 0) {
					value.put(target, PositiveNegativeLattice.POS);
				}else{
					value.put(target, PositiveNegativeLattice.NOT_SURE);
				}
			}
		}

		return value;
	}

	/*
	 * @Override public TupleLatticeElement<Variable, PositiveNegativeLattice>
	 * transfer( CopyInstruction instr, TupleLatticeElement<Variable,
	 * PositiveNegativeLattice> value) {
	 * System.out.println("Copy instr: operand=" + instr.getOperand()+
	 * " value="+value.toString())); return super.transfer(instr, value); }
	 * 
	 * @Override public TupleLatticeElement<Variable, PositiveNegativeLattice>
	 * transfer( TACInstruction instr, TupleLatticeElement<Variable,
	 * PositiveNegativeLattice> value) {
	 * 
	 * 
	 * return super.transfer(instr, value); }
	 */

}
