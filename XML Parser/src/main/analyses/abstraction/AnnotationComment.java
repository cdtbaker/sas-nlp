package main.analyses.abstraction;

public abstract class AnnotationComment {

	private int lineNo;
	private int[] scope;

	public AnnotationComment(int commentLineNo, int lineFrom, int lineTo) {
		this.lineNo = commentLineNo;
		this.scope = new int[2];
		scope[0] = lineFrom;
		scope[1] = lineTo;
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

}
