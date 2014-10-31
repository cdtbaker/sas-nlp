import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Stack;

import main.analyses.SimpleRangeAnalysis.PositiveNegativeLattice;

import org.eclipse.jdt.core.dom.InfixExpression.Operator;

public class ExpressionTest {

	private static Map<String, PositiveNegativeLattice> mp = new HashMap<>();
	
	public static void main(String[] args) {
		//System.out.println(getOperandState("5"));
		mp.put("a", PositiveNegativeLattice.NEG);
		eval("5");
	}

	private static void getComp(Stack<String> ops, Stack<PositiveNegativeLattice> vals) {

		String op = ops.pop();
		if (op.equals("+")) {
			vals.push(getPlus(vals.pop(), vals.pop()));
		} else if (op.equals("*")) {
					
		}
	}
	
	public static void eval(String st){
		Scanner scanner = new Scanner(st);
		String exp[] = scanner.nextLine().split(" ");
		Stack<String> ops = new Stack<String>();
		Stack<PositiveNegativeLattice> vals = new Stack<PositiveNegativeLattice>();

		for (int i = 0; i < exp.length; i++) {
			String s = exp[i];
			if (s.equals("(")) {
			} else if (s.equals("+") || s.equals("*")) {
				ops.push(s);
			} else if (s.equals(")")) {
				getComp(ops, vals);
			} else {
				vals.push(getOperandState(s));	
			}
		}
		if(!ops.empty()){
		getComp(ops, vals);
		}
		System.out.println(vals.pop());
	}
	
	private static PositiveNegativeLattice getOperandState(String operand) {
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
	
	private static PositiveNegativeLattice getPlus(PositiveNegativeLattice op1,
			PositiveNegativeLattice op2){
		
		/*PositiveNegativeLattice op1 = getOperandState(leftOp);
		PositiveNegativeLattice op2 = getOperandState(rightOp);*/
		
		
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
	
	
	private static PositiveNegativeLattice getBinOp(String leftOp,
			String rightOp, Operator operator) {

		PositiveNegativeLattice op1 = getOperandState(leftOp);
		PositiveNegativeLattice op2 = getOperandState(rightOp);

		if (operator.toString().equals("*") || operator.toString().equals("/")) {
			return getMult(op1, op2);
		}

		if (operator.toString().equals("%")) {
			if (op1 == PositiveNegativeLattice.POS) {
				return PositiveNegativeLattice.POS;
			} else if (op1 == PositiveNegativeLattice.NEG) {
				return PositiveNegativeLattice.NEG;
			} else {
				return PositiveNegativeLattice.ZERO;
			}
		}

		if (operator.toString().equals("+")) {
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
		}
		
		if (operator.toString().equals("-")) {
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
		}

		return PositiveNegativeLattice.NOT_SURE;

	}

	private static PositiveNegativeLattice getMult(PositiveNegativeLattice op1,
			PositiveNegativeLattice op2) {
		if (op1 == PositiveNegativeLattice.ZERO
				|| op2 == PositiveNegativeLattice.ZERO) {
			return PositiveNegativeLattice.ZERO;
		} else if (op1 == PositiveNegativeLattice.NEG
				^ op2 == PositiveNegativeLattice.NEG) {
			return PositiveNegativeLattice.NEG;
		} else {
			return PositiveNegativeLattice.POS;
		}
	}
}
