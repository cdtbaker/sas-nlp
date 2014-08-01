package main.analyses.SimpleRangeAnalysis;

import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import edu.cmu.cs.crystal.bridge.LatticeElement;
import edu.cmu.cs.crystal.flow.ILabel;
import edu.cmu.cs.crystal.flow.ILatticeOperations;
import edu.cmu.cs.crystal.flow.IResult;
import edu.cmu.cs.crystal.simple.AbstractingTransferFunction;
import edu.cmu.cs.crystal.simple.TupleLatticeElement;
import edu.cmu.cs.crystal.simple.TupleLatticeOperations;
import edu.cmu.cs.crystal.tac.ITACBranchSensitiveTransferFunction;
import edu.cmu.cs.crystal.tac.ITACTransferFunction;
import edu.cmu.cs.crystal.tac.model.AssignmentInstruction;
import edu.cmu.cs.crystal.tac.model.BinaryOperation;
import edu.cmu.cs.crystal.tac.model.BinaryOperator;
import edu.cmu.cs.crystal.tac.model.CopyInstruction;
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
		// def.put(thisVar, PositiveNegativeLattice.NEG);
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

	private PositiveNegativeLattice copyInstr(CopyInstruction copy,
			TupleLatticeElement<Variable, PositiveNegativeLattice> le) {
		return le.get(copy.getOperand());
	}

	private PositiveNegativeLattice basicAssignment(AssignmentInstruction instr) {
		String expression = ((Assignment) instr.getNode()).getRightHandSide()
				.toString().trim();

		try {
			double d = Double.parseDouble(expression);
			if (d < 0) {
				return PositiveNegativeLattice.NEG;
			} else if (d > 0) {
				return PositiveNegativeLattice.POS;
			} else {
				return PositiveNegativeLattice.ZERO;
			}

		} catch (Exception e) {
			return PositiveNegativeLattice.NOT_SURE;
		}
	}

	private PositiveNegativeLattice getBinOp(Variable v, BinaryOperation binop,
			TupleLatticeElement<Variable, PositiveNegativeLattice> le) {

		String expression = binop.getNode().toString();
		BinaryOperator operator = binop.getOperator();

		PositiveNegativeLattice op1 = getOperandState(operator,
				binop.getOperand1(), expression, le, true);
		PositiveNegativeLattice op2 = getOperandState(operator,
				binop.getOperand2(), expression, le, false);

		if (operator == BinaryOperator.ARIT_DIVIDE
				|| operator == BinaryOperator.ARIT_MULTIPLY) {
			if (op1 == PositiveNegativeLattice.NEG
					^ op2 == PositiveNegativeLattice.NEG) {
				return PositiveNegativeLattice.NEG;
			}
		} else if ((op1 == PositiveNegativeLattice.POS && op2 == PositiveNegativeLattice.POS)
				|| (op1 == PositiveNegativeLattice.NEG && op2 == PositiveNegativeLattice.NEG)) {
			return PositiveNegativeLattice.POS;
		} else {
			return PositiveNegativeLattice.ZERO;
		}

		if (operator == BinaryOperator.ARIT_MODULO) {
			if (op1 == PositiveNegativeLattice.POS) {
				return PositiveNegativeLattice.POS;
			} else if (op1 == PositiveNegativeLattice.NEG) {
				return PositiveNegativeLattice.NEG;
			} else {
				return PositiveNegativeLattice.ZERO;
			}
		}

		if (binop.getOperator() == BinaryOperator.ARIT_ADD) {
			if (op1 == PositiveNegativeLattice.POS
					&& op2 == PositiveNegativeLattice.POS) {
				return PositiveNegativeLattice.POS;
			} else if (op1 == PositiveNegativeLattice.NEG
					&& op2 == PositiveNegativeLattice.NEG) {
				return PositiveNegativeLattice.NEG;
			}
		}

		return PositiveNegativeLattice.NOT_SURE;

	}

	private PositiveNegativeLattice getOperandState(BinaryOperator b,
			Variable v, String expression,
			TupleLatticeElement<Variable, PositiveNegativeLattice> le,
			boolean first) {
		try {
			double d;
			if (first) {
				d = Double.parseDouble(expression.substring(0,
						expression.indexOf(b.token)).trim());
			} else {
				d = Double.parseDouble(expression.substring(
						expression.indexOf(b.token), expression.length())
						.trim());
			}

			if (d > 0) {
				return PositiveNegativeLattice.POS;
			} else if (d < 0) {
				return PositiveNegativeLattice.NEG;
			} else {
				return PositiveNegativeLattice.ZERO;
			}

		} catch (Exception e) {
			return le.get(v);
		}
	}

}
