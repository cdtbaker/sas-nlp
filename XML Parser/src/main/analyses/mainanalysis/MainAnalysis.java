package main.analyses.mainanalysis;

import java.lang.reflect.InvocationTargetException;

import main.analyses.SimpleRangeAnalysis.PositiveNegativeLattice;
import main.analyses.mainanalysis.data.AnalysisType;
import main.analyses.mainanalysis.data.CommentCollection;
import main.analyses.mainanalysis.data.NLPResult;
import main.analyses.mainanalysis.data.RangeAnalysisComment;
import main.commentextraction.com.jml.builder.XMLFromSource;
import main.commentextraction.com.jml.objects.framework.JMLElement;

import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import edu.cmu.cs.crystal.AbstractCrystalMethodAnalysis;

/**
 * Starting point to start the NLP and relevant analyses
 * @author parallels
 *
 */
public class MainAnalysis extends AbstractCrystalMethodAnalysis {

	@Override
	public void beforeAllMethods(ITypeRoot root, CompilationUnit cUnit) {
		JMLElement rootXML = null;
		try {
			rootXML = XMLFromSource.createXML(cUnit, root.getSource(), true,
					true);
		} catch (JavaModelException e) {
		}

		//for use of NLP
		// XMLOutputter output = new XMLOutputter(rootXML);
		// String sourceAsXML = output.getString();

		NLPResult result = new NLPResult();
		result.addComment(AnalysisType.SRA, new RangeAnalysisComment(10, 5, 9,
				PositiveNegativeLattice.NEG, "x", "main"));
		
		result.addComment(AnalysisType.SRA, new RangeAnalysisComment(10, 10, 15,
				PositiveNegativeLattice.NEG, "y", "main"));

		result.addComment(AnalysisType.SRA, new RangeAnalysisComment(10, 20, 30,
				PositiveNegativeLattice.NEG, "x", "method"));
		
		for (AnalysisType c : result.getTypes()) {
			try {

				
				c.classFile
						.getConstructor(CommentCollection.class)
						.newInstance(
								result.getAnnotations(c))
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

}
