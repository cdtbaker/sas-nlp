package main.analyses.mainanalysis.data;

import main.analyses.SimpleRangeAnalysis.PositiveNegativeFlowAnalysis;
import main.analyses.abstraction.AbstractCommentAnalysis;

public class NLPResult {
	
	public  AbstractCommentAnalysis getAnalysis(){
		return new PositiveNegativeFlowAnalysis(this);
	}
	
	public String getMethodName(){
		return "method";
	}
	
	public String getVarName(){
		return "x";
	}
	
	public int[] getAnalysisScope(){
		return new int [] {1,10};
	}

}
