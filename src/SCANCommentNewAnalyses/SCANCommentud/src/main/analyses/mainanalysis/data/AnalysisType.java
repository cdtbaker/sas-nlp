package main.analyses.mainanalysis.data;

import main.analyses.abstraction.AbstractCommentAnalysis;
import main.analyses.simplerangeanalysis.PositiveNegativeFlowAnalysis;


public class AnalysisType{
	
	private Class<? extends AbstractCommentAnalysis<?, ?>> c;
	private Object nlpAnalysis;
	
	public AnalysisType(Class<? extends AbstractCommentAnalysis<?, ?>> analysis, Object nlpAnalysis) {
		this.c = analysis;
		this.nlpAnalysis = nlpAnalysis;
	}
	
	public Class<?> getAnalysis() {
		return c;
	}
	
	public Object getNLPAnalysis() {
		return nlpAnalysis;
	}

	@Override
	public String toString() {
		return "AnalysisType [commentanalysis=" + c + ", nlpAnalysis=" + nlpAnalysis + "]";
	}
	
	
	
	
	
	
}