package main.analyses.SimpleRangeAnalysis;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Stack;

import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.InfixExpression.Operator;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

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

	private Map<String, PositiveNegativeLattice> mp = new HashMap<>();

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

		
		if (instr.getNode() instanceof VariableDeclarationFragment) {
			VariableDeclarationFragment vdf = (VariableDeclarationFragment) instr
					.getNode();

			if (vdf.getInitializer() instanceof MethodInvocation) {
				value.put(instr.getTarget(), PositiveNegativeLattice.NOT_SURE);
			} else {
				value.put(instr.getTarget(), eval(vdf.getInitializer()
						.toString()));
			}
		} else if (instr.getNode() instanceof Assignment) {

			Assignment assignment = ((Assignment) instr.getNode());
			// System.out
			// .println("TransferFunctions.transfer(AssignmentInstruction) variable = "
			// + instr.getTarget());
			// System.out
			// .println("TransferFunctions.transfer(AssignmentInstruction) variable.rhs() = "
			// + ((Assignment) instr.getNode()).getRightHandSide());
			// System.out
			// .println("TransferFunctions.transfer(AssignmentInstruction) variable.rhs.getClass = "
			// + ((Assignment) instr.getNode()).getRightHandSide()
			// .getClass());
			// System.out
			// .println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
			// Assignment assignment = ((Assignment) instr.getNode());
			// if (assignment.getRightHandSide() instanceof NumberLiteral
			// || assignment.getRightHandSide() instanceof PrefixExpression) {
			// System.out
			// .println("Prefix, infix?"
			// + (assignment.getRightHandSide() instanceof InfixExpression));
			// value.put(instr.getTarget(), basicAssignment(instr));
			// } else if (assignment.getRightHandSide() instanceof
			// org.eclipse.jdt.core.dom.SimpleName) {
			// System.out
			// .println("SimpleName, infix?"
			// + (assignment.getRightHandSide() instanceof InfixExpression));
			// value.put(instr.getTarget(), copy(instr, value));
			// } else if (assignment.getRightHandSide() instanceof
			// InfixExpression) {
			//
			// value.put(instr.getTarget(), binop(instr));
			// }

			if (assignment.getRightHandSide() instanceof MethodInvocation) {
				value.put(instr.getTarget(), PositiveNegativeLattice.NOT_SURE);
			} else {
				value.put(instr.getTarget(),
						eval(((Assignment) instr.getNode()).getRightHandSide()
								.toString()));
			}
		}
		mp.put(instr.getTarget().toString().trim(),
				value.get(instr.getTarget()));
		return super.transfer(instr, value);
	}

	// private PositiveNegativeLattice binop(AssignmentInstruction instr) {
	//
	// System.out
	// .println("TransferFunctions.binop(AssignmentInstruction) instr.rhs = "
	// + ((Assignment) instr.getNode()).getRightHandSide());
	// System.out
	// .println("TransferFunctions.binop(AssignmentInstruction) variable.instr.getClass() = "
	// + ((Assignment) instr.getNode()).getRightHandSide()
	// .getClass());
	// System.out
	// .println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
	//
	// InfixExpression exp = ((InfixExpression) ((Assignment) instr.getNode())
	// .getRightHandSide());
	// exp.getLeftOperand();
	// exp.getRightOperand();
	// System.out.println("TransferFunctions.binop(AssignmentInstruction)"
	// + exp.getOperator());
	//
	// return getBinOp(exp.getLeftOperand(), exp.getRightOperand(),
	// exp.getOperator());
	// }

	private PositiveNegativeLattice copy(AssignmentInstruction instr,
			TupleLatticeElement<Variable, PositiveNegativeLattice> value) {

		return mp.get(((Assignment) instr.getNode()).getRightHandSide()
				.toString().trim());

	}

	// private PositiveNegativeLattice basicAssignment(AssignmentInstruction
	// instr) {
	// System.out
	// .println("TransferFunctions.BasicAssignment(Assignment) instr ="
	// + instr.getNode());
	// System.out
	// .println("TransferFunctions.BasicAssignment(Assignment) instr.getClass() ="
	// + instr.getNode().getClass());
	// System.out
	// .println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
	// Assignment assign = ((Assignment) instr.getNode());
	//
	// String rhs = assign.getRightHandSide().toString().trim();
	//
	// try {
	// double d = Double.parseDouble(rhs);
	// if (d < 0) {
	// return PositiveNegativeLattice.NEG;
	// } else if (d > 0) {
	// return PositiveNegativeLattice.POS;
	// } else {
	// return PositiveNegativeLattice.ZERO;
	// }
	//
	// } catch (Exception e) {
	// return PositiveNegativeLattice.NOT_SURE;
	// }
	//
	// }

	private PositiveNegativeLattice getBinOp(Expression leftOp,
			Expression rightOp, Operator operator) {

		PositiveNegativeLattice op1 = getOperandState(leftOp);
		PositiveNegativeLattice op2 = getOperandState(rightOp);

		if (operator.toString().equals("*") || operator.toString().equals("/")) {
			return getMultDiv(op1, op2);
		}

		if (operator.toString().equals("%")) {
			return getMod(op1);
		}

		if (operator.toString().equals("+")) {
			return getPlus(op1, op2);
		}

		if (operator.toString().equals("-")) {
			return getNeg(op1, op2);
		}

		return PositiveNegativeLattice.NOT_SURE;

	}

	private PositiveNegativeLattice getMultDiv(PositiveNegativeLattice op1,
			PositiveNegativeLattice op2) {
		if (op1 == PositiveNegativeLattice.NOT_SURE
				|| op2 == PositiveNegativeLattice.NOT_SURE) {
			return PositiveNegativeLattice.NOT_SURE;
		} else if (op1 == PositiveNegativeLattice.ZERO
				|| op2 == PositiveNegativeLattice.ZERO) {
			return PositiveNegativeLattice.ZERO;
		} else if (op1 == PositiveNegativeLattice.NEG
				^ op2 == PositiveNegativeLattice.NEG) {
			return PositiveNegativeLattice.NEG;
		} else {
			return PositiveNegativeLattice.POS;
		}
	}

	private PositiveNegativeLattice getMod(PositiveNegativeLattice op1) {
		return op1;
	}

	private PositiveNegativeLattice getNeg(PositiveNegativeLattice op1,
			PositiveNegativeLattice op2) {
		if (op1 == PositiveNegativeLattice.NEG
				&& op2 == PositiveNegativeLattice.POS) {
			return PositiveNegativeLattice.NEG;
		} else if (op1 == PositiveNegativeLattice.POS
				&& op2 == PositiveNegativeLattice.NEG) {
			return PositiveNegativeLattice.POS;
		} else if (op1 == PositiveNegativeLattice.ZERO
				&& op2 == PositiveNegativeLattice.ZERO) {
			return PositiveNegativeLattice.ZERO;
		} else if ((op1 == PositiveNegativeLattice.ZERO ^ op2 == PositiveNegativeLattice.ZERO)
				&& (op1 == PositiveNegativeLattice.POS ^ op2 == PositiveNegativeLattice.POS)) {
			return PositiveNegativeLattice.NEG;
		} else if ((op1 == PositiveNegativeLattice.ZERO ^ op2 == PositiveNegativeLattice.ZERO)
				&& (op1 == PositiveNegativeLattice.NEG ^ op2 == PositiveNegativeLattice.NEG)) {
			return PositiveNegativeLattice.POS;
		}
		return PositiveNegativeLattice.NOT_SURE;
	}

	private PositiveNegativeLattice getPlus(PositiveNegativeLattice op1,
			PositiveNegativeLattice op2) {
		if (op1 == PositiveNegativeLattice.POS
				&& op2 == PositiveNegativeLattice.POS) {
			return PositiveNegativeLattice.POS;
		} else if (op1 == PositiveNegativeLattice.NEG
				&& op2 == PositiveNegativeLattice.NEG) {
			return PositiveNegativeLattice.NEG;
		} else if (op1 == PositiveNegativeLattice.ZERO
				&& op2 == PositiveNegativeLattice.ZERO) {
			return PositiveNegativeLattice.ZERO;
		} else if ((op1 == PositiveNegativeLattice.ZERO ^ op2 == PositiveNegativeLattice.ZERO)
				&& (op1 == PositiveNegativeLattice.POS ^ op2 == PositiveNegativeLattice.POS)) {
			return PositiveNegativeLattice.POS;
		} else if ((op1 == PositiveNegativeLattice.ZERO ^ op2 == PositiveNegativeLattice.ZERO)
				&& (op1 == PositiveNegativeLattice.NEG ^ op2 == PositiveNegativeLattice.NEG)) {
			return PositiveNegativeLattice.NEG;
		}
		return PositiveNegativeLattice.NOT_SURE;
	}

	private PositiveNegativeLattice getOperandState(Expression operand) {
		try {
			double d = Double.parseDouble(operand.toString());

			if (d > 0) {
				return PositiveNegativeLattice.POS;
			} else if (d < 0) {
				return PositiveNegativeLattice.NEG;
			} else {
				return PositiveNegativeLattice.ZERO;
			}

		} catch (Exception e) {
			return mp.get(operand.toString().trim());
		}
	}

	// ////////////////////////////////////////////
	/*
	 * Following evaluation has an issue with precedence
	 */
	private void getComp(Stack<String> ops, Stack<PositiveNegativeLattice> vals) {
		String op = ops.pop();

		if (op.equals("+")) {
			PositiveNegativeLattice s = vals.pop();
			PositiveNegativeLattice f = vals.pop();
			vals.push(getPlus(f, s));
		} else if (op.equals("*") || op.equals("/")) {
			vals.push(getMultDiv(vals.pop(), vals.pop()));
		} else if (op.equals("%")) {
			vals.push(getMod(vals.pop()));
			vals.pop();
		} else if (op.equals("-")) {
			PositiveNegativeLattice s = vals.pop();
			PositiveNegativeLattice f = vals.pop();
			vals.push(getNeg(f, s));
		}

	}

	public PositiveNegativeLattice eval(String st) {
		Scanner scanner = new Scanner(st);
		String exp[] = scanner.nextLine().split(" ");
		Stack<String> ops = new Stack<String>();
		Stack<PositiveNegativeLattice> vals = new Stack<PositiveNegativeLattice>();

		for (int i = 0; i < exp.length; i++) {
			String s = exp[i];
			if (s.equals("(")) {
			} else if (s.equals("+") || s.equals("*") || s.equals("/")
					|| s.equals("%") || s.equals("-")) {
				ops.push(s);
			} else if (s.equals(")")) {
				getComp(ops, vals);
			} else {
				vals.push(getOperandState(s));
			}

		}
		if (!ops.empty()) {
			getComp(ops, vals);
		}
		return vals.pop();
	}

	private PositiveNegativeLattice getOperandState(String operand) {
		try {
			double d = Double.parseDouble(operand.toString());

			if (d > 0) {
				return PositiveNegativeLattice.POS;
			} else if (d < 0) {
				return PositiveNegativeLattice.NEG;
			} else {
				return PositiveNegativeLattice.ZERO;
			}

		} catch (Exception e) {
			return mp.get(operand.toString().trim());
		}
	}

}
