package main.analyses.mainanalysis;

import java.util.ArrayList;
import java.util.List;

import main.analyses.mainanalysis.data.NLPResult;
import main.commentextraction.com.jml.builder.XMLFromSource;
import main.commentextraction.com.jml.objects.framework.JMLElement;
import main.commentextraction.com.jml.output.XMLOutputter;

import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import edu.cmu.cs.crystal.AbstractCrystalMethodAnalysis;

public class MainAnalysis extends AbstractCrystalMethodAnalysis{

	
	@Override
	public void beforeAllMethods(ITypeRoot root, CompilationUnit cUnit) {
		JMLElement rootXML = null;
		try {
			rootXML = XMLFromSource.createXML(cUnit, root.getSource(), true, true);
		} catch (JavaModelException e) {}
		
		XMLOutputter output = new XMLOutputter(rootXML);
		String sourceAsXML = output.getString();
		
		//pass XMLString to root analyser to return list of results
		List<NLPResult> results = new ArrayList<>();
				
		for (NLPResult nlpResult : results) {
		//	nlpResult.getAnalysis().runAnalysis(getReporter(), getInput(), root, cUnit);
		}
		
	
	}
	
	//Unused abstract method
	@Override
	public void analyzeMethod(MethodDeclaration m) {
		// TODO Auto-generated method stub
		
	}

}
