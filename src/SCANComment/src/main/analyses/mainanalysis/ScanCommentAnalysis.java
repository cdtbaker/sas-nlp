package main.analyses.mainanalysis;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import main.analyses.mainanalysis.data.AnalysisType;
import main.analyses.mainanalysis.data.CommentCollection;
import main.analyses.mainanalysis.data.NLPResult;
import main.analyses.mainanalysis.data.RangeAnalysisComment;
import main.analyses.simplerangeanalysis.PositiveNegativeLattice;
import main.commentextraction.jml.builder.XMLFromSource;
import main.commentextraction.jml.objects.framework.JMLElement;
import main.commentextraction.jml.output.XMLOutputter;

import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import uk.ac.tees.scancomment.ie.IntRangeMachine;
import uk.ac.tees.scancomment.ie.Machine;
import edu.cmu.cs.crystal.AbstractCrystalMethodAnalysis;

/**
 * Starting point to start the NLP and relevant analyses
 * 
 * @author parallels
 *
 */
public class ScanCommentAnalysis extends AbstractCrystalMethodAnalysis {

	/*
	 * To make different projects for different analysis possible user passes enum, Type param, of all possible analyses extending AnalysisType
	 * user also passes relevant data for NLP to identify when this analysis should be used??
	 */
	
	@Override
	public void beforeAllMethods(ITypeRoot root, CompilationUnit cUnit) {
		JMLElement rootXML = null;
		try {
			rootXML = XMLFromSource.createXML(cUnit, root.getSource(), true, true);
		} catch (JavaModelException e) {
			e.printStackTrace();
		}

		// for use of NLP
		XMLOutputter output = new XMLOutputter(rootXML);
		String sourceAsXML = output.getString();
		

		// could make this pretty later - at the moment building XML just to parse it...
		try {
			Document jdoc = new SAXBuilder().build(sourceAsXML);
			
			NLPResult result = new NLPResult();
			
			for (Element classElement : jdoc.getRootElement().getChildren()) {
				for (Element methodElement : classElement.getChildren()) {
					
					// get the start and end lines of this method
					Integer startLine = new Integer(methodElement.getAttributeValue("line"));
					Integer endLine = new Integer(methodElement.getAttributeValue("endLine"));
					
					// build set of variable names in scope
					Set<String> variableNames = new HashSet<String>();
					for (Element paramElement : methodElement.getChild("params").getChildren())
						variableNames.add(paramElement.getAttributeValue("name"));
					for (Element declarationElement : methodElement.getChildren("declaration"))
						variableNames.add(declarationElement.getAttributeValue("name"));
					
					Machine intRangeMachine = new IntRangeMachine(variableNames);
					
					// analyse each of the comments
					for (Element commentElement : methodElement.getChildren("comment")) {
						
						Map<String,String> frame = intRangeMachine.recognise(commentElement.getTextNormalize());
						if (frame != null) {
							
							String op = frame.get("op");
							String a1 = frame.containsKey("a1") ? frame.get("a1") : "0";
							
							PositiveNegativeLattice expected = null;
							if (op.equals("eq") && a1.equals("0")) {
								expected = PositiveNegativeLattice.ZERO;
							} else if ((op.equals("gt") && a1.equals("0")) || (op.equals("ge") && a1.equals("1"))) {
								expected = PositiveNegativeLattice.POS;
							} else if ((op.equals("lt") && a1.equals("0")) || (op.equals("le") && a1.equals("1"))) {
								expected = PositiveNegativeLattice.NEG;
							}
							
							if (expected != null) {
								int commentLineNo = -1;
								int lineFrom = new Integer(methodElement.getAttributeValue("line"));
								int lineTo = new Integer(methodElement.getAttributeValue("endLine"));
								String varName = frame.get("a0");
								String methodName = methodElement.getAttributeValue("name");
								result.addComment(AnalysisType.SRA, new RangeAnalysisComment(commentLineNo, lineFrom, lineTo,
									expected, varName, methodName));
							}
						}
					}
					
					
				}
			}
			
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


			
		} catch (JDOMException | IOException e) {
			e.printStackTrace();
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
