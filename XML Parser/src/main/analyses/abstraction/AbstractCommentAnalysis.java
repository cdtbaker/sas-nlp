package main.analyses.abstraction;

import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.dom.CompilationUnit;

import main.analyses.SimpleRangeAnalysis.PositiveNegativeLattice;
import main.analyses.mainanalysis.data.NLPResult;
import edu.cmu.cs.crystal.AbstractCrystalMethodAnalysis;

public abstract class AbstractCommentAnalysis extends AbstractCrystalMethodAnalysis{

	protected final NLPResult nlpResult;
	protected final PositiveNegativeLattice expectedLattice;
	protected CompilationUnit compUnit;
	
	public AbstractCommentAnalysis(NLPResult result){
		this.nlpResult = result;
		expectedLattice = determineExpected();
	}
	@Override
	public void beforeAllMethods(ITypeRoot compUnit, CompilationUnit rootNode) {
		this.compUnit = rootNode;
	}
	
	protected abstract PositiveNegativeLattice determineExpected();
	protected boolean sameVar(String e, String v) {
		return e.equals(v);

	}
	
	public boolean betweenScope(int lineNo){
		
		return lineNo>=nlpResult.getAnalysisScope()[0] && lineNo <= nlpResult.getAnalysisScope()[1]; 
		
	}
	
}
