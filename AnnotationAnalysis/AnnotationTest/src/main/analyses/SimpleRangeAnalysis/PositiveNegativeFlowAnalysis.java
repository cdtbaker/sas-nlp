package main.analyses.SimpleRangeAnalysis;

import java.util.Arrays;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.MemberValuePair;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.NormalAnnotation;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import edu.cmu.cs.crystal.AbstractCrystalMethodAnalysis;
import edu.cmu.cs.crystal.IAnalysisReporter.SEVERITY;
import edu.cmu.cs.crystal.annotations.AnnotationSummary;
import edu.cmu.cs.crystal.simple.SimpleTACFlowAnalysis;
import edu.cmu.cs.crystal.simple.TupleLatticeElement;
import edu.cmu.cs.crystal.tac.model.Variable;

public class PositiveNegativeFlowAnalysis extends AbstractCrystalMethodAnalysis {

	private SimpleTACFlowAnalysis<TupleLatticeElement<Variable, PositiveNegativeLattice>> flowAnalysis;

	private NormalAnnotation anno;
	private PositiveNegativeLattice expected;
	
	public PositiveNegativeFlowAnalysis(NormalAnnotation node) {
		this.anno = node;
		if(node.getTypeName().toString().equals("Positive")){
			expected = PositiveNegativeLattice.POS;
		}else if(node.getTypeName().toString().equals("Negative")){
			expected = PositiveNegativeLattice.NEG;
		}else{
			expected = PositiveNegativeLattice.ZERO;
		}
		
	}
	

	@Override
	public void analyzeMethod(MethodDeclaration m) {
		TransferFunctions tf = new TransferFunctions();
		flowAnalysis = new SimpleTACFlowAnalysis<TupleLatticeElement<Variable, PositiveNegativeLattice>>(
				tf, getInput());
		
		if (m.equals(getMethod(anno))) {
			m.accept(new Visitor());
		}

	}

	public MethodDeclaration getMethod(ASTNode ast) {
		while (!(ast instanceof MethodDeclaration)) {
			ast = ast.getParent();
		}
		return (MethodDeclaration) ast;
	}

	private class Visitor extends ASTVisitor {

		@Override
		public void endVisit(VariableDeclarationFragment node) {
			Expression varName = ((MemberValuePair)anno.values().get(0)).getValue();
			
			System.out.println(node.getName());
			
			if (!node.getName().toString().equals(varName.toString().substring(1,varName.toString().length()-1))) {
				return;
				
			}

			Expression exp = node.getInitializer();
			
			analyse(node,node.getName(),exp);

		}
		
		public void analyse(ASTNode node, Expression varName, Expression exp){
			if (exp == null) {
				return;
			}

			TupleLatticeElement<Variable, PositiveNegativeLattice> beforeTuple = flowAnalysis
					.getResultsAfter(node);
			Variable varToCheck = flowAnalysis.getVariable(varName);
			PositiveNegativeLattice element = beforeTuple.get(varToCheck);

			for (Variable v : beforeTuple.getKeySet()) {
				System.out.println("Var " + v + " is in the tuple");
			}

			if (element != expected) {
				getReporter().reportUserProblem(
						"The variable " + varName.toString() + element.desc, node,
						getName(), SEVERITY.WARNING);
			}
		}

		@Override
		public void endVisit(Assignment node) {
			Expression exp = node;
			analyse(node,node.getLeftHandSide(),exp);
			
		}

		@Override
		public void endVisit(MethodInvocation node) {

			AnnotationSummary summary = getInput().getAnnoDB()
					.getSummaryForMethod(node.resolveMethodBinding());
			System.out.println(Arrays.asList(summary.getParameterNames()));

		}

	}

}
