package main.analyses.abstraction;

import main.analyses.SimpleRangeAnalysis.PositiveNegativeLattice;

public class RangeAnalysisComment extends AnnotationComment{

	private PositiveNegativeLattice expected;
	private String varName;
	private String methodName;
	
	public RangeAnalysisComment(int commentLineNo, int lineFrom, int lineTo, PositiveNegativeLattice expected, String varName, String methodName) {
		super(commentLineNo, lineFrom, lineTo);
		this.expected = expected;
		this.varName = varName;
		this.methodName = methodName;
	}
	
	
	

}
