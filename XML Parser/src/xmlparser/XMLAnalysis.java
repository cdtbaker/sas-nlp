package xmlparser;

import java.util.List;
import java.util.Stack;

import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ArrayCreation;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BlockComment;
import org.eclipse.jdt.core.dom.Comment;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.LineComment;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.NodeFinder;
import org.eclipse.jdt.core.dom.SimplePropertyDescriptor;
import org.eclipse.jdt.core.dom.TagElement;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.jdom2.*;
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

	// Called before all methods
	@Override
	public void beforeAllMethods(ITypeRoot rootNode, CompilationUnit compilationUnit) {
		List<ASTNode> commentList1 = compilationUnit.getCommentList();
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

		// Property: At this point, source exists and is not null.

		// initialize the cvisitor with the source
		parser = new XMLParser(compilationUnit, source.split("\n"));

		// To visite LineComment and BlockComment nodes, we need to call the
		// accept method on them
		// (see documentation of getCommentList() for more details)
		List<Comment> l = compilationUnit.getCommentList();
		for (Comment c : l) {
			if (!(c instanceof Javadoc)) {
				c.accept(parser);
			}
		}
		
		d = new Document();
		Element root = new Element("class");
		elements.add(root);
		d.setRootElement(root);
		compilationUnit.accept(parser);
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
		// prints time taken
		System.out.println("\nEnd of " + getName() + ", \nTime Taken: "
				+ (endTime - startTime) + "ms");
		// create and run a test analysis
		TestAnalysis analysis = new TestAnalysis();
		analysis.runAnalysis(getReporter(), getInput(), compUnit, rootNode);
		
		XMLOutputter xml = new XMLOutputter();
		xml.setFormat(Format.getPrettyFormat());
		System.out.println(xml.outputString(d));
		super.afterAllMethods(compUnit, rootNode);
	}

	@Override
	public void analyzeMethod(MethodDeclaration d) {
		//methodDec = d;
		//d.accept(parser);
	}

	@Override
	public String getName() {
		return "Comment Visitor";
	}

	/**
	 * Issue with Parsing line and block comments as the parser runs for  
	 * comments and then the rest of the class as accept() needs to be 
	 * called on each comment
	 */
	private class XMLParser extends ASTVisitor {

		CompilationUnit compUnit;
		String[] source;
	

		public XMLParser(CompilationUnit c, String[] s) {
			compUnit = c;
			source = s;
		}

		@Override
		public boolean visit(Block node) {
			Element e;
			if(methodDec != null){
				//if block is a method add appropriate attributes
				e = new Element("method");	
				Attribute type = new Attribute("type",methodDec.getReturnType2().toString());
				Attribute name = new Attribute("name", methodDec.getName().toString());
				e.setAttribute(type);
				e.setAttribute(name);
				methodDec = null;
			}else{
				 e = new Element("scope");
			}
			//adds block to previous top of stack
			elements.peek().addContent(e);
			//pushes block to top of stack
			elements.push(e);
			return super.visit(node);
		}
		
		@Override
		public void endVisit(Block node) {
			//removes block from stack
			elements.pop();
			super.endVisit(node);
		}
	
		@Override
		public boolean visit(MethodDeclaration node) {
			methodDec = node;
			return super.visit(node);
		}

		@Override
		public void endVisit(FieldDeclaration node) {
	
			Element e = new Element("declaration");
			Attribute type = new Attribute("type",node.getType().toString());
			String nameS = ((VariableDeclarationFragment)node.fragments().get(0)).getName().toString();
			Attribute name = new Attribute("name",nameS);
			e.setAttribute(type);
			e.setAttribute(name);
			elements.peek().addContent(e);
			super.endVisit(node);
		}
	
		@Override
		public boolean visit(VariableDeclarationStatement node) {
			//getType() ignores arrays so check for array added
			String typeS = node.getType().toString();
			if(node.fragments().get(0).toString().contains("[]")){
				typeS = typeS + "[]";
			}
			Element declaration = new Element("declaration");
			Attribute type = new Attribute("type" ,typeS);
			declaration.setAttribute(type);
			
			String nameS = ((VariableDeclarationFragment)node.fragments().get(0)).getName().toString();
			Attribute name = new Attribute("name" ,nameS);
			declaration.setAttribute(name);
			elements.peek().addContent(declaration);
			super.endVisit(node);
			return super.visit(node);
		}

		

		@Override
		public boolean visit(Javadoc node) {
			Element javadoc  = new Element("javadoc");
			List<TagElement> tags = node.tags() ;
			for(TagElement t : tags){
				
				if(t.getTagName() == null){
					Element tag = new Element("text");
					tag.addContent(t.toString().substring(3));
					javadoc.addContent(tag);
				}else{
					Element tag = new Element(t.getTagName().substring(1));
					tag.addContent(t.toString().substring(t.getTagName().toString().length()+4));
					
					javadoc.addContent(tag);
				}
			}
				
			elements.peek().addContent(javadoc);
			return super.visit(node);
		}


		@Override
		public boolean visit(LineComment node) {			
			int startLineNumber = compUnit.getLineNumber(node
					.getStartPosition()) - 1;
			String lineComment = source[startLineNumber].trim();
			lineComment = lineComment.substring(lineComment.indexOf("//"));
			
			int start = node.getStartPosition();
			ASTNode withoutSignature = NodeFinder.perform(
					node.getAlternateRoot(), start, 0);
			Element comment = new Element("comment");
			comment.addContent(lineComment.toString());
			elements.peek().addContent(comment);

			return true;

		}

		@Override
		public boolean visit(BlockComment node) {
			int startLineNumber = compUnit.getLineNumber(node
					.getStartPosition()) - 1;
			int endLineNumber = compUnit.getLineNumber(node.getStartPosition()
					+ node.getLength()) - 1;

			StringBuffer blockComment = new StringBuffer();

			for (int lineCount = startLineNumber; lineCount <= endLineNumber; lineCount++) {

				String blockCommentLine = source[lineCount].trim();
				blockComment.append(blockCommentLine);
				if (lineCount != endLineNumber) {
					blockComment.append("\n");
				}
			}
			Element comment =  new Element("comment");
			comment.addContent(blockComment.toString());
			elements.peek().addContent(comment);

			int start = node.getStartPosition();
			ASTNode withoutSignature = NodeFinder.perform(
					node.getAlternateRoot(), start, 0);
			
			return true;
		}

	}

}