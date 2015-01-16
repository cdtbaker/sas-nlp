package main.analyses.SimpleRangeAnalysis;

import main.analyses.abstraction.AbstractCommentAnalysis;
import main.analyses.mainanalysis.data.NLPResult;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import edu.cmu.cs.crystal.IAnalysisReporter.SEVERITY;
import edu.cmu.cs.crystal.simple.SimpleTACFlowAnalysis;
import edu.cmu.cs.crystal.simple.TupleLatticeElement;
import edu.cmu.cs.crystal.tac.model.Variable;

public class PositiveNegativeFlowAnalysis extends AbstractCommentAnalysis<PositiveNegativeLattice> {

	private SimpleTACFlowAnalysis<TupleLatticeElement<Variable, PositiveNegativeLattice>> flowAnalysis;

	public PositiveNegativeFlowAnalysis(NLPResult res) {
		super(res);

	}

	@Override
	public void analyzeMethod(MethodDeclaration m) {
		TransferFunctions tf = new TransferFunctions();
		flowAnalysis = new SimpleTACFlowAnalysis<TupleLatticeElement<Variable, PositiveNegativeLattice>>(
				tf, getInput());

		/*if (m.getName().toString().equals(nlpResult.getMethodName())) {
			m.accept(new Visitor());
		}*/

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

			/*TupleLatticeElement<Variable, PositiveNegativeLattice> beforeTuple = flowAnalysis
					.getResultsAfter(node);
			Variable varToCheck = flowAnalysis.getVariable(varName);
			PositiveNegativeLattice element = beforeTuple.get(varToCheck);

			if (sameVar(varName.toString(), nlpResult.getVarName())
					&& betweenScope(compUnit.getLineNumber(node
							.getStartPosition()))) {
				if (element != expectedLattice) {
					getReporter()
							.reportUserProblem(
									"The variable " + varName.toString()
											+ element.desc, node, getName(),
									SEVERITY.WARNING);
					System.out.println(varName + " " + element.desc);
				}*/
			//}
		}

		@Override
		public void endVisit(Assignment node) {
			Expression exp = node;

			analyse(node, node.getLeftHandSide(), exp);

		}

	}

	@Override
	protected PositiveNegativeLattice determineExpected() {
		return PositiveNegativeLattice.POS; 
	}

}
