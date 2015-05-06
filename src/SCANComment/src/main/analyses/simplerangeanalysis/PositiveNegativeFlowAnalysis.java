package main.analyses.simplerangeanalysis;

import main.analyses.abstraction.AbstractCommentAnalysis;
import main.analyses.mainanalysis.data.CommentCollection;
import main.analyses.mainanalysis.data.RangeAnalysisComment;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import edu.cmu.cs.crystal.simple.AbstractingTransferFunction;
import edu.cmu.cs.crystal.simple.TupleLatticeElement;
import edu.cmu.cs.crystal.tac.model.Variable;

/**
 * Implementation of abstract comment analysis
 * @author daniel
 *
 */
public class PositiveNegativeFlowAnalysis extends
		AbstractCommentAnalysis<PositiveNegativeLattice, RangeAnalysisComment> {

	
	public PositiveNegativeFlowAnalysis(CommentCollection<PositiveNegativeLattice,RangeAnalysisComment> result) {
		super(result);
	}

	@Override
	protected ASTVisitor getVisitor() {
		return new ASTVisitor() {
			
			@Override
			public void endVisit(VariableDeclarationFragment node) {
				Expression exp = node.getInitializer();
				analyse(node, node.getName(), exp);
			}

			@Override
			public void endVisit(Assignment node) {
				Expression exp = node;
				analyse(node, node.getLeftHandSide(), exp);
			}
		};
	}

	
	@Override
	protected boolean toAnalyse(MethodDeclaration md) {
		return !current.isEmpty();
	}

	private boolean betweenScope(String varName, int line) {
		return line < current.get(varName).getLineTo() && line > current.get(varName).getLineFrom();
	}

	@Override
	protected boolean toReport(String variableName, int line, PositiveNegativeLattice element) {
		return current.containsKey(variableName) && betweenScope(variableName,line) && current.get(variableName).getExpected() != element;
	}


	@Override
	protected AbstractingTransferFunction<TupleLatticeElement<Variable, PositiveNegativeLattice>> getTF() {
		return new TransferFunctions();
	}

	@Override
	protected String getWarning(PositiveNegativeLattice e) {
		return e.desc;
	}
}
