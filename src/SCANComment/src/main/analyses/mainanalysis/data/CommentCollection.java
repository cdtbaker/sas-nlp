package main.analyses.mainanalysis.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import main.analyses.abstraction.AnnotationComment;

/**
 * Collection of comments of a certain type
 * @author daniel
 *
 * @param <LatticeType>
 * @param <CommentType>
 */
public class CommentCollection<LatticeType, CommentType extends AnnotationComment<LatticeType>> {

	private List<CommentType> comments;

	public CommentCollection() {
		comments = new ArrayList<>();
	}

	public void addComment(CommentType comment) {
		comments.add(comment);
	}

	public Map<String, VariableAnalysisData<LatticeType>> getVariablesForMethod(
			String methodName) {
		Map<String, VariableAnalysisData<LatticeType>> variablesInMethod = new HashMap<>();
		for (CommentType c : comments) {
			if (c.getMethodName().equals(methodName)) {
				variablesInMethod.put(
						c.getVarName(),
						new VariableAnalysisData<>(c.getLineFrom(), c
								.getLineTo(), c.getExpected()));
			}

		}
		return variablesInMethod;

	}

}
