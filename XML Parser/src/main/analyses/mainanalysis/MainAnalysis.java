package main.analyses.mainanalysis;

import java.util.ArrayList;
import java.util.List;

import main.analyses.mainanalysis.data.NLPResult;

import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import edu.cmu.cs.crystal.AbstractCrystalMethodAnalysis;

public class MainAnalysis extends AbstractCrystalMethodAnalysis{

	
	@Override
	public void beforeAllMethods(ITypeRoot root, CompilationUnit cUnit) {
		String comments = "";
		
		List<NLPResult>results = new ArrayList<>(); //will pass comments to NLP analyser to return results
		results.add(new NLPResult());
		for (NLPResult nlpResult : results) {
			nlpResult.getAnalysis().runAnalysis(getReporter(), getInput(), root, cUnit);
		}
		
	
	}
	
	@Override
	public void analyzeMethod(MethodDeclaration m) {
		// TODO Auto-generated method stub
		
	}

}
