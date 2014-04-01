package commentvisitor;

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
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.LineComment;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.NodeFinder;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.jdom2.*;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import testanalysis.TestAnalysis;
import edu.cmu.cs.crystal.AbstractCrystalMethodAnalysis;

public class CommentAnalysis extends AbstractCrystalMethodAnalysis {

	// lists to hold comments
	Stack<Element> elements;
	Document d;
	long startTime;
	long endTime;
	CommentVisitor cvisitor; // This is the comment visitor used to visit all
								// the AST Nodes; it is initialised in
								// beforeAllMethods()

	// Called before all methods
	@Override
	public void beforeAllMethods(ITypeRoot compUnit, CompilationUnit rootNode) {
		List<ASTNode> commentList1 = rootNode.getCommentList();
		elements = new Stack();
		String source = null;
		try {
			source = compUnit.getSource();
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
		cvisitor = new CommentVisitor(rootNode, source.split("\n"));

		// To visite LineComment and BlockComment nodes, we need to call the
		// accept method on them
		// (see documentation of getCommentList() for more details)
		List<Comment> l = rootNode.getCommentList();
		for (Comment c : l) {
			if (!(c instanceof Javadoc)) {
				c.accept(cvisitor);
			}
		}

		d = new Document();
		Element root = new Element("class");
		elements.add(root);
		d.setRootElement(root);
		// records start time of analysis
		startTime = System.currentTimeMillis();
		System.out.println("Running: " + this.getName());
		super.beforeAllMethods(compUnit, rootNode);
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
		Element method = new Element("method_return-" + d.getReturnType2() + "_name-" + d.getName().toString()+"");
		
		elements.peek().addContent(method);
		d.accept(cvisitor);
	}

	@Override
	public String getName() {
		return "Comment Visitor";
	}

	// visitor to find comments
	private class CommentVisitor extends ASTVisitor {

		CompilationUnit compUnit;
		String[] source;

		public CommentVisitor(CompilationUnit c, String[] s) {
			compUnit = c;
			source = s;
		}

		@Override
		public boolean visit(Block node) {
			Element e = new Element("scope");
			
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
		public void endVisit(ArrayCreation node) {
			elements.peek().addContent("array");
			super.endVisit(node);
		}

		@Override
		public boolean visit(VariableDeclarationExpression node) {
			elements.peek().addContent(node.getType().toString());
			return super.visit(node);
		}

		@Override
		public boolean visit(VariableDeclarationFragment node) {
			elements.peek().addContent(node.getName().toString());
			return super.visit(node);
		}

		@Override
		public boolean visit(VariableDeclarationStatement node) {
			elements.peek().addContent(node.getType().toString()+"_");
			return super.visit(node);
		}

		// called whenever javadoc comment is found
		@Override
		public void endVisit(Javadoc node) {
			org.jdom2.Comment c = new org.jdom2.Comment(node.tags().get(0).toString());
			elements.peek().addContent(c);
			super.endVisit(node);
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
			
			elements.peek().addContent(new org.jdom2.Comment(lineComment));

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

			elements.peek().addContent(new org.jdom2.Comment(blockComment.toString()));

			int start = node.getStartPosition();
			ASTNode withoutSignature = NodeFinder.perform(
					node.getAlternateRoot(), start, 0);
			
			return true;
		}

	}

}