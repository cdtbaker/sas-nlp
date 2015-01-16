package main.analyses.abstraction;

import main.analyses.SimpleRangeAnalysis.PositiveNegativeFlowAnalysis;

public enum AnalysisType {

	SRA(PositiveNegativeFlowAnalysis.class);
	
	public Class<? extends AbstractCommentAnalysis<?>> classFile;
	
	
	AnalysisType(Class<? extends AbstractCommentAnalysis<?>> classFile){
		this.classFile = classFile;
	}
	
}
