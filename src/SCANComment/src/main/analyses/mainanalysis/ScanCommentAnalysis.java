package main.analyses.mainanalysis;

import java.lang.reflect.InvocationTargetException;

import main.analyses.SimpleRangeAnalysis.PositiveNegativeLattice;
import main.analyses.mainanalysis.data.AnalysisType;
import main.analyses.mainanalysis.data.CommentCollection;
import main.analyses.mainanalysis.data.NLPResult;
import main.analyses.mainanalysis.data.RangeAnalysisComment;
import main.commentextraction.jml.builder.XMLFromSource;
import main.commentextraction.jml.objects.framework.JMLElement;

import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import edu.cmu.cs.crystal.AbstractCrystalMethodAnalysis;

/**
 * Starting point to start the NLP and relevant analyses
 * 
 * @author parallels
 *
 */
public class ScanCommentAnalysis extends AbstractCrystalMethodAnalysis {

	@Override
	public void beforeAllMethods(ITypeRoot root, CompilationUnit cUnit) {
		JMLElement rootXML = null;
		try {
			rootXML = XMLFromSource.createXML(cUnit, root.getSource(), true,
					true);
		} catch (JavaModelException e) {
		}

		// for use of NLP
		// XMLOutputter output = new XMLOutputter(rootXML);
		// String sourceAsXML = output.getString();

		NLPResult result = testCases();
		

		for (AnalysisType c : result.getTypes()) {
			try {

				c.classFile.getConstructor(CommentCollection.class)
						.newInstance(result.getAnnotations(c))
						.runAnalysis(getReporter(), getInput(), root, cUnit);

			} catch (InstantiationException | IllegalAccessException
					| IllegalArgumentException | InvocationTargetException
					| NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
			}
		}

	}

	// Unused abstract method
	@Override
	public void analyzeMethod(MethodDeclaration m) {
		// TODO Auto-generated method stub

	}

	private NLPResult testCases() {
		NLPResult res = new NLPResult();

		res.addComment(AnalysisType.SRA, new RangeAnalysisComment(-1, 5, 14,
				PositiveNegativeLattice.POS, "x", "testPos"));
		res.addComment(AnalysisType.SRA, new RangeAnalysisComment(-1, 5, 14,
				PositiveNegativeLattice.POS, "y", "testPos"));
		
		res.addComment(AnalysisType.SRA, new RangeAnalysisComment(-1, 16, 25,
				PositiveNegativeLattice.NEG, "x", "testNeg"));
		res.addComment(AnalysisType.SRA, new RangeAnalysisComment(-1, 16, 25,
				PositiveNegativeLattice.NEG, "y", "testNeg"));
		
		res.addComment(AnalysisType.SRA, new RangeAnalysisComment(-1, 27, 36,
				PositiveNegativeLattice.ZERO, "x", "testZero"));
		res.addComment(AnalysisType.SRA, new RangeAnalysisComment(-1, 27, 36,
				PositiveNegativeLattice.ZERO, "y", "testZero"));
		
		res.addComment(AnalysisType.SRA, new RangeAnalysisComment(-1, 38, 50,
				PositiveNegativeLattice.POS, "x", "testEvaluation"));
		res.addComment(AnalysisType.SRA, new RangeAnalysisComment(-1, 38, 50,
				PositiveNegativeLattice.POS, "y", "testEvaluation"));
		
		return res;
	}

}
