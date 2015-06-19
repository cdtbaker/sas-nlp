package main.analyses.mainanalysis.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import main.analyses.abstraction.AbstractCommentAnalysis;
import main.analyses.abstraction.AnnotationComment;

/**
 * Results from NLP
 * @author daniel
 *
 */
public class NLPResult {

	private Map<Class<? extends AbstractCommentAnalysis<?, ?>>, CommentCollection> analysisAnnotations;

	public NLPResult() {
		analysisAnnotations = new HashMap<>();
	}

	public void addComment(Class<? extends AbstractCommentAnalysis<?, ?>> type, AnnotationComment comment) {
		if (analysisAnnotations.containsKey(type)) {
			this.analysisAnnotations.get(type).addComment(comment);
		} else {
			CommentCollection c = new CommentCollection();
			c.addComment(comment);
			this.analysisAnnotations.put(type, c);
		}

	}

	public Set<Class<? extends AbstractCommentAnalysis<?, ?>>> getTypes() {
		return analysisAnnotations.keySet();
	}

	public CommentCollection getAnnotations(Class<?> t) {
		return analysisAnnotations.get(t);
	}

}
