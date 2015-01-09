package main.analyses.mainanalysis.data;

import java.lang.reflect.InvocationTargetException;

import main.analyses.abstraction.AbstractCommentAnalysis;

public class NLPResult {
	
	private AbstractCommentAnalysis<?> analysisToRun;
	private String methodName, varName;
	private int lineFrom, lineTo;

	public NLPResult(Class<? extends AbstractCommentAnalysis<?>> analysisToRun, String methodName,
			String varName, int lineFrom, int lineTo) {
		super();
		try {
			this.analysisToRun = analysisToRun.getConstructor(String.class).newInstance(this);
		} catch (InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.methodName = methodName;
		this.varName = varName;
		this.lineFrom = lineFrom;
		this.lineTo = lineTo;
	}

	public  AbstractCommentAnalysis<?> getAnalysis(){
		return analysisToRun;
	}
	
	public String getMethodName(){
		return methodName;
	}
	
	public String getVarName(){
		return varName;
	}
	
	public int[] getAnalysisScope(){
		return new int [] {lineFrom,lineTo};
	}

}
