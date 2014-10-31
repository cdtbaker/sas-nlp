package main.analyses.SimpleRangeAnalysis;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SimplePropertyDescriptor;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

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
		public void endVisit(VariableDeclarationFragment node) {
			System.out.println("node.getInitializer() ="
					+ node.getInitializer());
			
			Expression exp = node.getInitializer();
			
			

			System.out
					.println("FlowAnalysis.EndVisit(Assignment) Exp = " + exp);
			System.out.println("FlowAnalysis.EndVisit(Assignment) Node = "
					+ node);
			if (exp == null) {
				return;
			}

			TupleLatticeElement<Variable, PositiveNegativeLattice> beforeTuple = flowAnalysis
					.getResultsAfter(node);
			Variable varToCheck = flowAnalysis.getVariable(node.getName());
			PositiveNegativeLattice element = beforeTuple.get(varToCheck);
			System.out.println("FlowAnalysis.EndVisit(Assignment) element = "
					+ element);
			System.out
					.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
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

			System.out
					.println("FlowAnalysis.EndVisit(Assignment) Exp = " + exp);
			System.out.println("FlowAnalysis.EndVisit(Assignment) Node = "
					+ node);
			if (exp == null) {
				return;
			}

			TupleLatticeElement<Variable, PositiveNegativeLattice> beforeTuple = flowAnalysis
					.getResultsAfter(node);
			Variable varToCheck = flowAnalysis.getVariable(node
					.getLeftHandSide());
			PositiveNegativeLattice element = beforeTuple.get(varToCheck);
			System.out.println("FlowAnalysis.EndVisit(Assignment) element = "
					+ element);
			System.out
					.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
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

	}

}
