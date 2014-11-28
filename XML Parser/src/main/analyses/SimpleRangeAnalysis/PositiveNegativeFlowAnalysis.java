package main.analyses.SimpleRangeAnalysis;

import java.security.PublicKey;
import java.util.Arrays;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import edu.cmu.cs.crystal.AbstractCrystalMethodAnalysis;
import edu.cmu.cs.crystal.IAnalysisReporter.SEVERITY;
import edu.cmu.cs.crystal.annotations.AnnotationSummary;
import edu.cmu.cs.crystal.simple.SimpleTACFlowAnalysis;
import edu.cmu.cs.crystal.simple.TupleLatticeElement;
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
		public void endVisit(VariableDeclarationFragment node) {

			Expression exp = node.getInitializer();
			
			

			if (exp == null) {
				return;
			}

			TupleLatticeElement<Variable, PositiveNegativeLattice> beforeTuple = flowAnalysis
					.getResultsAfter(node);
			Variable varToCheck = flowAnalysis.getVariable(node.getName());
			PositiveNegativeLattice element = beforeTuple.get(varToCheck);

			for (Variable v : beforeTuple.getKeySet()) {
				System.out.println("Var " + v + " is in the tuple");
			}

			if (element == PositiveNegativeLattice.POS) {
				getReporter().reportUserProblem(
						"The variable " + node.getName() + " > 0 ",
						node, getName(), SEVERITY.WARNING);
			} else if (element == PositiveNegativeLattice.NEG) {
				getReporter().reportUserProblem(
						"The variable " + node.getName() + " < 0",
						node, getName(), SEVERITY.WARNING);
			} else if (element == PositiveNegativeLattice.ZERO) {
				getReporter().reportUserProblem(
						"The variable " + node.getName() + " = 0",
						node, getName(), SEVERITY.WARNING);
			}



		}

		@Override
		public void endVisit(Assignment node) {
			Expression exp = node;


			if (exp == null) {
				return;
			}

			TupleLatticeElement<Variable, PositiveNegativeLattice> beforeTuple = flowAnalysis
					.getResultsAfter(node);
			Variable varToCheck = flowAnalysis.getVariable(node
					.getLeftHandSide());
			PositiveNegativeLattice element = beforeTuple.get(varToCheck);

			for (Variable v : beforeTuple.getKeySet()) {
				System.out.println("Var " + v + " is in the tuple");
			}

			if (element == PositiveNegativeLattice.POS) {
				getReporter().reportUserProblem(
						"The variable " + node.getLeftHandSide() + " > 0 ",
						node, getName(), SEVERITY.WARNING);
			} else if (element == PositiveNegativeLattice.NEG) {
				getReporter().reportUserProblem(
						"The variable " + node.getLeftHandSide() + " < 0",
						node, getName(), SEVERITY.WARNING);
			} else if (element == PositiveNegativeLattice.ZERO) {
				getReporter().reportUserProblem(
						"The variable " + node.getLeftHandSide() + " = 0",
						node, getName(), SEVERITY.WARNING);
			}
		}
		
		@Override
		public void endVisit(MethodInvocation node) {
			
			
			
			
			AnnotationSummary summary = getInput().getAnnoDB().getSummaryForMethod(node.resolveMethodBinding());
			System.out.println(Arrays.asList(summary.getParameterNames()));
			
		}

	}

}
