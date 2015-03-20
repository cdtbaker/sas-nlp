package main.analyses.mainanalysis.data;

import main.analyses.SimpleRangeAnalysis.PositiveNegativeFlowAnalysis;
import main.analyses.abstraction.AbstractCommentAnalysis;

public enum AnalysisType {

	SRA(PositiveNegativeFlowAnalysis.class);
	
	public Class<? extends AbstractCommentAnalysis<?,?>> classFile;
	
	AnalysisType(Class<? extends AbstractCommentAnalysis<? ,?>> classFile){
		this.classFile = classFile;
	}
	
}
