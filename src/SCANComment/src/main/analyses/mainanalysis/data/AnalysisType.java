package main.analyses.mainanalysis.data;

import main.analyses.abstraction.AbstractCommentAnalysis;
import main.analyses.simplerangeanalysis.PositiveNegativeFlowAnalysis;

public enum AnalysisType {

	SRA(PositiveNegativeFlowAnalysis.class);
	
	public Class<? extends AbstractCommentAnalysis<?,?>> classFile;
	
	AnalysisType(Class<? extends AbstractCommentAnalysis<? ,?>> classFile){
		this.classFile = classFile;
	}
	
}
