package main.analyses.SimpleRangeAnalysis;

import main.analyses.NPEBranchChecker.NullLatticeElement;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;

import edu.cmu.cs.crystal.AbstractCrystalMethodAnalysis;
import edu.cmu.cs.crystal.IAnalysisReporter.SEVERITY;
import edu.cmu.cs.crystal.simple.SimpleTACFlowAnalysis;
import edu.cmu.cs.crystal.simple.TupleLatticeElement;
import edu.cmu.cs.crystal.tac.model.Variable;

public class PositiveNegitiveFlowAnalysis extends AbstractCrystalMethodAnalysis{

	
	private SimpleTACFlowAnalysis<TupleLatticeElement<Variable,PositiveNegativeLattice>> flowAnalysis;
	
	@Override
	public void analyzeMethod(MethodDeclaration m) {
		// TODO Auto-generated method stub
		
	}
	
	private class Visitor extends ASTVisitor{

		@Override
		public void endVisit(MethodInvocation node){
			Expression exp = node.getExpression();

			if(exp == null){
				return;
			}
			
			TupleLatticeElement<Variable, PositiveNegativeLattice> beforeTuple = flowAnalysis.getResultsBefore(node);
			Variable varToCheck = flowAnalysis.getVariable(exp);
			PositiveNegativeLattice element = beforeTuple.get(varToCheck);
			
			if(element == PositiveNegativeLattice.POS){
				getReporter().reportUserProblem("The variable " + exp + "is > 0", node, getName(),SEVERITY.WARNING);
			}else if( element == PositiveNegativeLattice.NEG){
				getReporter().reportUserProblem("The variable " + exp + "is < 0", node, getName(),SEVERITY.WARNING);
			}
			
		}

	
		
	}

}
