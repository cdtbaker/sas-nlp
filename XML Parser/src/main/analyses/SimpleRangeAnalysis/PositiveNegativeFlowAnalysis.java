package main.analyses.SimpleRangeAnalysis;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;

import edu.cmu.cs.crystal.AbstractCrystalMethodAnalysis;
import edu.cmu.cs.crystal.IAnalysisReporter.SEVERITY;
import edu.cmu.cs.crystal.flow.FlowAnalysis;
import edu.cmu.cs.crystal.simple.SimpleTACFlowAnalysis;
import edu.cmu.cs.crystal.simple.TupleLatticeElement;
import edu.cmu.cs.crystal.tac.TACFlowAnalysis;
import edu.cmu.cs.crystal.tac.model.Variable;

public class PositiveNegativeFlowAnalysis extends AbstractCrystalMethodAnalysis {

	private SimpleTACFlowAnalysis<TupleLatticeElement<Variable, PositiveNegativeLattice>> flowAnalysis;

	@Override
	public void analyzeMethod(MethodDeclaration m) {
		TransferFunctions tf = new TransferFunctions();
		flowAnalysis = new SimpleTACFlowAnalysis<TupleLatticeElement<Variable, PositiveNegativeLattice>>(
				tf, getInput());

		m.accept(new Visitor());
		
	}

	private class Visitor extends ASTVisitor {
	

		@Override
		public void endVisit(Assignment node) {
			Expression exp = node;

			if (exp == null) {
				return;
			}

			TupleLatticeElement<Variable, PositiveNegativeLattice> beforeTuple = 
					flowAnalysis.getResultsAfter(node);
			Variable varToCheck = flowAnalysis.getVariable(node.getLeftHandSide());
			System.out.println(node.getLeftHandSide().toString());
			PositiveNegativeLattice element = beforeTuple.get(varToCheck);
			System.out.println(beforeTuple.toString());
			if (element == PositiveNegativeLattice.POS) {
				getReporter().reportUserProblem(
						"The variable " + exp + "is > 0", node, getName(),
						SEVERITY.WARNING);
			} else if (element == PositiveNegativeLattice.NEG) {
				getReporter().reportUserProblem(
						"The variable " + exp + "is < 0", node, getName(),
						SEVERITY.WARNING);
			}
		}
		
		

	}

}