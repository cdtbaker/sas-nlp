package main.analyses.mainanalysis.data;

import main.analyses.abstraction.AnnotationComment;
import main.analyses.simplerangeanalysis.PositiveNegativeLattice;

/**
 * Implementation of AnnotationComment but needs further abstraction
 * @author parallels
 *
 */
public class RangeAnalysisComment extends AnnotationComment<PositiveNegativeLattice>{

	private PositiveNegativeLattice expected;
	
	public RangeAnalysisComment(int commentLineNo, int lineFrom, int lineTo, PositiveNegativeLattice expected, String varName, String methodName) {
		super(commentLineNo, lineFrom, lineTo,varName,methodName);
		this.expected = expected;
		
	}

	public PositiveNegativeLattice getExpected() {
		return expected;
	}
	

	
	
	
	

}
