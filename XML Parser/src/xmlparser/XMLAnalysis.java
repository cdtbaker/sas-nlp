package xmlparser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.apache.commons.lang3.StringEscapeUtils;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BlockComment;
import org.eclipse.jdt.core.dom.Comment;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.LineComment;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.NodeFinder;
import org.eclipse.jdt.core.dom.TagElement;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.jdom2.Attribute;
import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import testanalysis.TestAnalysis;
import edu.cmu.cs.crystal.AbstractCrystalMethodAnalysis;

public class XMLAnalysis extends AbstractCrystalMethodAnalysis {

	Stack<Element> elements;
	MethodDeclaration methodDec;
	Document d;
	long startTime;
	long endTime;
	XMLParser parser; // This is the comment visitor used to visit all
						// the AST Nodes; it is initialised in
						// beforeAllMethods()

	List<Element> eList;
	Element root;
	Element sourceS;

	// Called before all methods
	@Override
	public void beforeAllMethods(ITypeRoot rootNode,
			CompilationUnit compilationUnit) {

		// List<ASTNode> commentList1 = compilationUnit.getCommentList();
		elements = new Stack<Element>();
		methodDec = null;
		String source = null;
		try {
			source = rootNode.getSource();
		} catch (JavaModelException e) {
			// TODO: what is the best way of terminating the analysis?
			System.exit(-1);
		}

		if (source == null) {
			// TODO: what is the best way of terminating the analysis?
			System.exit(-1);
		}

		sourceS = new Element("source");
		sourceS.setAttribute("package", StringEscapeUtils
				.unescapeXml(compilationUnit.getPackage().toString()));
		elements.push(sourceS);

		List<ImportDeclaration> ids = (List<ImportDeclaration>) compilationUnit
				.imports();
		for (ImportDeclaration id : ids) {
			Element el = new Element("import");
			el.setAttribute("name", id.toString());
			sourceS.addContent(el);
		}
		// Property: At this point, source exists and is not null.

		// initialize the cvisitor with the source
		parser = new XMLParser(compilationUnit, source.split("\n"));

		d = new Document();
		root = new Element("class");

		String classDef = (compilationUnit.getJavaElement().getElementName());
		classDef = classDef.substring(0, classDef.indexOf('.'));

		root.setAttribute("name", classDef);

		// elements.add(root);
		d.setRootElement(sourceS);
		compilationUnit.accept(parser);

		// getList of methods
		List<Element> el = sourceS.getChildren("class");

		eList = new ArrayList<Element>();
		for (Element e : el) {
			if (e instanceof Element) {
				eList.add((Element) e);

			}

		}

		// To visite LineComment and BlockComment nodes, we need to call the
		// accept method on them
		// (see documentation of getCommentList() for more details)
		List<Comment> l = compilationUnit.getCommentList();
		for (Comment c : l) {
			if (c instanceof Javadoc && c.getParent() == null) {
				c.accept(parser);
			}
			if (!(c instanceof Javadoc)) {
				c.accept(parser);

			}
		}

		// records start time of analysis
		startTime = System.currentTimeMillis();
		System.out.println("Running: " + this.getName());
		super.beforeAllMethods(rootNode, compilationUnit);
	}

	// Called after all methods
	@Override
	public void afterAllMethods(ITypeRoot compUnit, CompilationUnit rootNode) {

		// records end time
		endTime = System.currentTimeMillis();

		XMLOutputter xml = new XMLOutputter();
		xml.setFormat(Format.getPrettyFormat());
		System.out.println(xml.outputString(d));
		String path = compUnit.getPath().toString();
		path = path.substring(path.lastIndexOf("src/"), path.indexOf(".java"));

		/*
		 * File dir = new File(path.substring(0, path.lastIndexOf('/')));
		 * 
		 * if (!dir.exists()) { dir.mkdirs(); } FileWriter fw = null; try { fw =
		 * new FileWriter(path + ".xml");
		 * 
		 * BufferedWriter bw = new BufferedWriter(fw);
		 * bw.write(xml.outputString(d)); bw.close(); fw.close();
		 * 
		 * } catch (IOException e) { System.out.println("Error");
		 * e.printStackTrace(); }
		 */

		// prints time taken

		System.out.println("\nEnd of " + getName() + ", \nTime Taken: "
				+ (endTime - startTime) + "ms");
		// create and run a test analysis
		TestAnalysis analysis = new TestAnalysis();
		// analysis.runAnalysis(getReporter(), getInput(), compUnit, rootNode);

		super.afterAllMethods(compUnit, rootNode);
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

	/**
	 * Comments other than method or field javadocs are added at the bottom of
	 * their containing method or class
	 */
	private class XMLParser extends ASTVisitor {

		CompilationUnit compUnit;
		String[] source;
		int methodCount = 0;
		int lastLine = 0;

		public XMLParser(CompilationUnit c, String[] s) {
			compUnit = c;
			source = s;
		}

		@Override
		public boolean visit(TypeDeclaration node) {
			Element e;
			int startLine = compUnit.getLineNumber(node.getStartPosition());
			int endLineNumber = compUnit.getLineNumber(node.getStartPosition()
					+ node.getRoot().getLength());
			
			if (node.isInterface()) {
				e = new Element("interface");
			} else {
				e = new Element("class");
			}

			e.setAttribute("name", node.getName().toString());

			if (node.getSuperclassType() != null) {
				e.setAttribute("extends", node.getSuperclassType().toString());
			}
			
			e.setAttribute("startLine", String.valueOf(startLine));
			e.setAttribute("endLine", String.valueOf(endLineNumber));
			if (node.superInterfaceTypes() != null
					&& node.superInterfaceTypes().size() > 0) {
				for (Type t : (List<Type>) node.superInterfaceTypes()) {
					Element i = new Element("implements");
					i.setAttribute("name", t.toString());
					e.addContent(i);
				}
			}
			elements.peek().addContent(e);
			elements.push(e);
			return super.visit(node);

		}

		@Override
		public boolean visit(Block node) {
			Element e;
			int startLine = compUnit.getLineNumber(node.getStartPosition());
			int endLineNumber = compUnit.getLineNumber(node.getStartPosition()
					+ node.getLength());
			if (methodDec != null) {

				e = new XMLMethod(methodCount);
				Attribute type;
				if (methodDec.getReturnType2() != null) {
					type = new Attribute("type", methodDec.getReturnType2()
							.toString());
				} else {
					type = new Attribute("type", "constructor");
				}
				Attribute name = new Attribute("name", methodDec.getName()
						.toString());

				((XMLMethod) e).setType(type);
				((XMLMethod) e).setMethodName(name);
				((XMLMethod) e).setStartLine(startLine);
				((XMLMethod) e).setEndLine(compUnit.getLineNumber(node
						.getStartPosition() + node.getLength()));
				methodDec = null;
			} else {
				e = new Element("scope");
				e.setAttribute("startLine", String.valueOf(startLine));
				e.setAttribute("endLine", String.valueOf(endLineNumber));
			}
			// adds block to previous top of stack
			elements.peek().addContent(e);
			// pushes block to top of stack
			elements.push(e);
			return super.visit(node);
		}

		@Override
		public void endVisit(Block node) {
			// removes block from stack
			elements.pop();

			super.endVisit(node);
		}

		@Override
		public boolean visit(MethodDeclaration node) {
			methodCount++;
			if (node.getBody() != null) {
				methodDec = node;
			} else {
				XMLMethod m = new XMLMethod(methodCount);
				Attribute name = new Attribute("name", node.getName()
						.toString());
				Attribute type = null;

				if (node.getReturnType2() != null) {
					type = new Attribute("type", node.getReturnType2()
							.toString());
				} else {
					type = new Attribute("type", "constructor");
				}
				m.setMethodName(name);
				m.setType(type);
				int startLine = compUnit.getLineNumber(node.getStartPosition());
				m.setStartLine(startLine);
				m.setEndLine(compUnit.getLineNumber(node.getStartPosition()
						+ node.getLength()));
				elements.peek().addContent(m);
			}

			return super.visit(node);
		}

		@Override
		public void endVisit(FieldDeclaration node) {

			Element e = new Element("declaration");
			Attribute type = new Attribute("type", node.getType().toString());
			String nameS = ((VariableDeclarationFragment) node.fragments().get(
					0)).getName().toString();
			Attribute name = new Attribute("name", nameS);
			e.setAttribute(type);
			e.setAttribute(name);
			elements.peek().addContent(e);
			super.endVisit(node);
		}

		@Override
		public boolean visit(VariableDeclarationStatement node) {
			// getType() ignores arrays so check for array added
			String typeS = node.getType().toString();
			if (node.fragments().get(0).toString().contains("[]")) {
				typeS = typeS + "[]";
			}
			Element declaration = new Element("declaration");
			Attribute type = new Attribute("type", typeS);
			declaration.setAttribute(type);

			String nameS = ((VariableDeclarationFragment) node.fragments().get(
					0)).getName().toString();
			Attribute name = new Attribute("name", nameS);
			declaration.setAttribute(name);
			elements.peek().addContent(declaration);
			super.endVisit(node);
			return super.visit(node);
		}

		public List<Element> getScopes() {
			List<Element> scopes = new ArrayList<Element>();

			for (Content c : sourceS.getDescendants()) {
				if (c instanceof Element
						&& ((Element) c).getName().equals("scope")) {
					scopes.add((Element) c);
				}
			}

			return scopes;

		}

		public List<Element> getMethods() {
			List<Element> methods = new ArrayList<Element>();

			for (Content c : sourceS.getDescendants()) {
				if (c instanceof Element
						&& ((Element) c).getName().equals("method")) {
					methods.add((Element) c);
				}
			}

			return methods;

		}

		public List<Element> getClasses() {
			List<Element> classes = new ArrayList<Element>();

			for (Content c : sourceS.getDescendants()) {
				if (c instanceof Element
						&& ((Element) c).getName().equals("class")) {
					classes.add((Element) c);
				}
			}

			return classes;

		}

		public void addComment(Element comment, int start) {
			boolean added = false;
			for (Element e : getScopes()) {

				if (start >= Integer.parseInt(e.getAttribute("startLine")
						.getValue())
						&& start <= Integer.parseInt(e.getAttribute("endLine")
								.getValue())) {
					e.addContent(comment);
					added = true;
					break;
				}
			}

			if (!added) {
				for (Element e : getMethods()) {

					if (start >= Integer.parseInt(e.getAttribute("startLine")
							.getValue())
							&& start <= Integer.parseInt(e.getAttribute(
									"endLine").getValue())) {
						e.addContent(comment);
						added = true;
						break;
					}
				}
			}

			if (!added) {
				for (Element e : getClasses()) {

					if (start >= Integer.parseInt(e.getAttribute("startLine")
							.getValue())
							&& start <= Integer.parseInt(e.getAttribute(
									"endLine").getValue())) {
						e.addContent(comment);
						added = true;
						break;
					}
				}
			}
		}

		@Override
		public boolean visit(Javadoc node) {

			Element javadoc = new Element("javadoc");
			List<TagElement> tags = node.tags();
			for (TagElement t : tags) {

				if (t.getTagName() == null) {
					Element tag = new Element("text");
					tag.addContent(StringEscapeUtils.unescapeXml(t.toString()
							.substring(3)));
					javadoc.addContent(tag);
				} else {
					Element tag = new Element(t.getTagName().substring(1));
					tag.addContent(t.toString().substring(
							t.getTagName().toString().length() + 4));

					javadoc.addContent(tag);
				}
			}
			if (node.getParent() == null) {

				int startLineNumber = compUnit.getLineNumber(node
						.getStartPosition());
				addComment(javadoc, startLineNumber);

			} else {
				elements.peek().addContent(javadoc);
			}
			return super.visit(node);
		}

		@Override
		public boolean visit(LineComment node) {
			int startLineNumber = compUnit.getLineNumber(node
					.getStartPosition());
			String lineComment = source[startLineNumber - 1].trim();
			lineComment = lineComment.substring(lineComment.indexOf("//"));

			Element comment = new Element("comment");
			comment.addContent(lineComment.toString().replaceAll("//", ""));
			boolean added = false;

			addComment(comment, startLineNumber);

			int start = node.getStartPosition();
			ASTNode withoutSignature = NodeFinder.perform(
					node.getAlternateRoot(), start, 0);

			return true;

		}

		@Override
		public boolean visit(BlockComment node) {

			int startLineNumber = compUnit.getLineNumber(node
					.getStartPosition());
			int endLineNumber = compUnit.getLineNumber(node.getStartPosition()
					+ node.getLength());

			StringBuffer blockComment = new StringBuffer();

			for (int lineCount = startLineNumber - 1; lineCount <= endLineNumber - 1; lineCount++) {

				String blockCommentLine = source[lineCount].trim();
				blockComment.append(blockCommentLine);
				if (lineCount != endLineNumber) {
					blockComment.append("\n");
				}
			}
			Element comment = new Element("comment");
			comment.addContent(blockComment.toString().replaceAll("\\*", "")
					.replaceAll("/", ""));

			addComment(comment, startLineNumber);

			int start = node.getStartPosition();
			ASTNode withoutSignature = NodeFinder.perform(
					node.getAlternateRoot(), start, 0);

			return true;
		}

	}

}
