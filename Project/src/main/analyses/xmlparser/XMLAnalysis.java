package main.analyses.xmlparser;

import main.commentextraction.com.jml.builder.XMLFromSource;
import main.commentextraction.com.jml.objects.framework.JMLElement;
import main.commentextraction.com.jml.output.XMLOutputter;

import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import edu.cmu.cs.crystal.AbstractCrystalMethodAnalysis;

public class XMLAnalysis extends AbstractCrystalMethodAnalysis {

	long startTime;
	long endTime;

	@Override
	public void beforeAllMethods(ITypeRoot rootNode,
			CompilationUnit compilationUnit) {

		// to file
		/*
		 * String path = ""; try {
		 * 
		 * JMLElement r = XMLFromSource.createXML(compilationUnit,
		 * rootNode.getSource(), true, true); XMLOutputter o = new
		 * XMLOutputter(r);
		 * 
		 * path = path +rootNode.getPath().toString().replaceAll("/src", "");
		 * path = path.substring(1, path.indexOf(".java")); path =
		 * path.replaceFirst("/", "/xml/"); path = "Converted Libraries/" +
		 * path;
		 * 
		 * File dir = new File(path.substring(0, path.lastIndexOf('/')));
		 * 
		 * if (!dir.exists()) { dir.mkdirs(); } try (BufferedWriter bw = new
		 * BufferedWriter(new FileWriter(path + ".xml"))) {
		 * bw.write(o.getString()); // bw.close();
		 * System.out.println("Written to " + path);
		 * 
		 * } catch (IOException e) { e.printStackTrace(); }
		 * 
		 * } catch (JavaModelException e) { e.printStackTrace(); }
		 * 
		 * path = path.replaceFirst("/xml/", "/source/"); File dir = new
		 * File(path.substring(0, path.lastIndexOf('/')));
		 * 
		 * if (!dir.exists()) { dir.mkdirs(); } try { BufferedWriter bw = new
		 * BufferedWriter(new FileWriter(path + ".java"));
		 * bw.write(compilationUnit.toString()); bw.close(); } catch
		 * (IOException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 */
		// to console

		toConsole(rootNode, compilationUnit);

	}

	private void toConsole(ITypeRoot rootNode, CompilationUnit compilationUnit) {
		JMLElement r = null;
		try {
			r = XMLFromSource.createXML(
					compilationUnit, rootNode.getSource(), true, true);
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		XMLOutputter o = new main.commentextraction.com.jml.output.XMLOutputter(
				r);
		
		System.out.println(o.getString());
	}

	@Override
	public void afterAllMethods(ITypeRoot compUnit, CompilationUnit rootNode) {

	}

	@Override
	public void analyzeMethod(MethodDeclaration d) {
		// methodDec = d;
		// d.accept(parser);
	}

	@Override
	public String getName() {
		return "XML Parser";
	}

}
