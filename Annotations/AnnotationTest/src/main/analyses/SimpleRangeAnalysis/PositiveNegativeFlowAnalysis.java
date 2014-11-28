package main.analyses.SimpleRangeAnalysis;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.MemberValuePair;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import edu.cmu.cs.crystal.AbstractCrystalMethodAnalysis;
import edu.cmu.cs.crystal.IAnalysisReporter.SEVERITY;
import edu.cmu.cs.crystal.simple.SimpleTACFlowAnalysis;
import edu.cmu.cs.crystal.simple.TupleLatticeElement;
import edu.cmu.cs.crystal.tac.model.Variable;

public class PositiveNegativeFlowAnalysis extends AbstractCrystalMethodAnalysis {

	private SimpleTACFlowAnalysis<TupleLatticeElement<Variable, PositiveNegativeLattice>> flowAnalysis;

	private final NormalAnnotation annotation;
	private final PositiveNegativeLattice expectedLattice;

	public PositiveNegativeFlowAnalysis(NormalAnnotation node) {
		this.annotation = node;

		if (node.getTypeName().toString().equals("Positive")) {
			expectedLattice = PositiveNegativeLattice.POS;
		} else if (node.getTypeName().toString().equals("Negative")) {
			expectedLattice = PositiveNegativeLattice.NEG;
		} else {
			expectedLattice = PositiveNegativeLattice.ZERO;
			System.out.println("ASUIAHSDPAOIS:DH");
		}

	}

	@Override
	public void analyzeMethod(MethodDeclaration m) {
		TransferFunctions tf = new TransferFunctions();
		flowAnalysis = new SimpleTACFlowAnalysis<TupleLatticeElement<Variable, PositiveNegativeLattice>>(
				tf, getInput());

		if (m.equals(getMethod(annotation))) {
			m.accept(new Visitor());
		}

	}

	public MethodDeclaration getMethod(ASTNode ast) {
		while (!(ast instanceof MethodDeclaration)) {
			ast = ast.getParent();
		}

		return (MethodDeclaration) ast;

	}

	private boolean sameVar(Expression e, Expression v) {
		return e.toString().equals(
				v.toString().substring(1, v.toString().length() - 1));

	}

	private Expression annoVarName() {
		return ((MemberValuePair) annotation.values().get(0)).getValue();
	}

	private class Visitor extends ASTVisitor {

		@Override
		public void endVisit(VariableDeclarationFragment node) {

			Expression exp = node.getInitializer();

			analyse(node, node.getName(), exp);

		}

		public void analyse(ASTNode node, Expression varName, Expression exp) {
			if (exp == null) {
				return;
			}

			TupleLatticeElement<Variable, PositiveNegativeLattice> beforeTuple = flowAnalysis
					.getResultsAfter(node);
			Variable varToCheck = flowAnalysis.getVariable(varName);
			PositiveNegativeLattice element = beforeTuple.get(varToCheck);

			if (sameVar(varName, annoVarName())) {
				if (element != expectedLattice) {
					getReporter()
							.reportUserProblem(
									"The variable " + varName.toString()
											+ element.desc, node, getName(),
									SEVERITY.WARNING);
					System.out.println(varName + " " + element.desc);
				}
			}
		}

		@Override
		public void endVisit(Assignment node) {
			Expression exp = node;

			analyse(node, node.getLeftHandSide(), exp);

		}

	}

}
