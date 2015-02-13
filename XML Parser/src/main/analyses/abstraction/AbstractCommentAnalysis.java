package main.analyses.abstraction;

import java.util.Map;

import main.analyses.mainanalysis.data.CommentCollection;
import main.analyses.mainanalysis.data.VariableAnalysisData;

import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import edu.cmu.cs.crystal.AbstractCrystalMethodAnalysis;
import edu.cmu.cs.crystal.IAnalysisReporter.SEVERITY;
import edu.cmu.cs.crystal.simple.AbstractingTransferFunction;
import edu.cmu.cs.crystal.simple.SimpleTACFlowAnalysis;
import edu.cmu.cs.crystal.simple.TupleLatticeElement;
import edu.cmu.cs.crystal.tac.model.Variable;

/**
 * Abstraction of all comment analyses(PositiveNegativeFlowAnalysis e.g.)
 * 
 * @author daniel
 *
 * @param <LaticeType>
 * @param <CommentType>
 */
public abstract class AbstractCommentAnalysis<LaticeType, CommentType extends AnnotationComment<LaticeType>>
		extends AbstractCrystalMethodAnalysis {

	protected final CommentCollection<LaticeType,CommentType> commentsToAnalyse;
	protected Map<String, VariableAnalysisData<LaticeType>> current;
	protected CompilationUnit compUnit;
	protected ASTVisitor visitor;

	protected SimpleTACFlowAnalysis<TupleLatticeElement<Variable, LaticeType>> flowAnalysis;
	
	@Override
	public void analyzeMethod(MethodDeclaration m) {
		AbstractingTransferFunction<TupleLatticeElement<Variable, LaticeType>> tf = getTF();
	
		flowAnalysis = new SimpleTACFlowAnalysis<TupleLatticeElement<Variable, LaticeType>>(
				tf, getInput());

		current = commentsToAnalyse.getVariablesForMethod(m.getName()
				.toString());

		if (toAnalyse(m)) {
			m.accept(visitor);
		}
	}
	
	public void analyse(ASTNode node, Expression varName, Expression exp) {
		if (exp == null) {
			return;
		}

		TupleLatticeElement<Variable, LaticeType> beforeTuple = flowAnalysis
				.getResultsAfter(node);
		Variable varToCheck = flowAnalysis.getVariable(varName);
		LaticeType element = beforeTuple.get(varToCheck);

		if (toReport(varName.toString(),
				compUnit.getLineNumber(node.getStartPosition()),element)) {
			if (element != current.get(varName.toString()).getExpected()) {
				getReporter().reportUserProblem(
						"The variable " + varName.toString() + getWarning(element),
						node, getName(), SEVERITY.WARNING);
			}
		}

	}
	
	public AbstractCommentAnalysis(CommentCollection<LaticeType,CommentType> results) {
		this.commentsToAnalyse = results;
		visitor = getVisitor();
	}

	@Override
	public void beforeAllMethods(ITypeRoot compUnit, CompilationUnit rootNode) {
		this.compUnit = rootNode;
	}
	
	protected abstract String getWarning(LaticeType e);

	protected abstract ASTVisitor getVisitor();

	protected abstract boolean toAnalyse(MethodDeclaration md);

	protected abstract boolean toReport(String variableName, int line, LaticeType element);

	protected abstract AbstractingTransferFunction<TupleLatticeElement<Variable, LaticeType>> getTF();
}
