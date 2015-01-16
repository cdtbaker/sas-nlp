package main.analyses.mainanalysis.data;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;

import edu.cmu.cs.crystal.annotations.AnnotationDatabase;

import main.analyses.abstraction.AnalysisType;
import main.analyses.abstraction.AnnotationComment;

public class NLPResult {
	
	private Map<AnalysisType, AnnotationComment> analysisAnnotations;
	
	public NLPResult() {
		analysisAnnotations = new HashMap<>();
	}
	
	public void addComment(AnalysisType type, AnnotationComment comment){
		this.analysisAnnotations.put(type, comment);
	}
	
	public Map<AnalysisType, AnnotationComment> getAnnotations(){
		return analysisAnnotations;
	}

}
