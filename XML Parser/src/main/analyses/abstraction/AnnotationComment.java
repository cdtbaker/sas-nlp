package main.analyses.abstraction;

/**
 * Supposedly abstract annotation of comment but needs further abstraction
 * @author parallels
 *
 * @param <LatticeType>
 */
public abstract class AnnotationComment<LatticeType> {

	private int lineNo;
	private int[] scope;
	private String varName, methodName;
	
	public AnnotationComment(int commentLineNo, int lineFrom, int lineTo, String varName, String methodName) {
		this.lineNo = commentLineNo;
		this.scope = new int[2];
		scope[0] = lineFrom;
		scope[1] = lineTo;
		this.varName = varName;
		this.methodName = methodName;
	}
	
	public int getLineFrom(){
		return scope[0];
	}
	
	public int getLineTo(){
		return scope[1];
	}
	
	public int getCommentLine(){
		return lineNo;
	}
	
	public String getMethodName(){
		return methodName;
	}
	
	public abstract LatticeType getExpected();
	
	public String getVarName(){
		return varName;
	}

}
