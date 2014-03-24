package commentvisitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.BlockComment;
import org.eclipse.jdt.core.dom.Comment;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.LineComment;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.NodeFinder;

import testanalysis.TestAnalysis;
import edu.cmu.cs.crystal.AbstractCrystalMethodAnalysis;

public class CommentAnalysis extends AbstractCrystalMethodAnalysis {

	// lists to hold comments
	List<JavadocContext> javadocList;
	List<CommentContext> commentList;
	long startTime;
	long endTime;

	CommentVisitor cvisitor; // This is the comment visitor used to visit all
								// the AST Nodes; it is initialised in
								// beforeAllMethods()

	// Called before all methods
	@Override
	public void beforeAllMethods(ITypeRoot compUnit, CompilationUnit rootNode) {
		List<ASTNode> commentList1 = rootNode.getCommentList();

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

		// Initialises these lists
		javadocList = new ArrayList<JavadocContext>();
		commentList = new ArrayList<CommentContext>();

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
		super.afterAllMethods(compUnit, rootNode);
	}

	@Override
	public void analyzeMethod(MethodDeclaration d) {
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

		// called whenever javadoc comment is found
		@Override
		public void endVisit(Javadoc node) {
			javadocList.add(new JavadocContext(node));
			super.endVisit(node);
		}

		@Override
		public boolean visit(LineComment node) {

			System.out.println("BEGIN LINE COMMENT");
			int startLineNumber = compUnit.getLineNumber(node
					.getStartPosition()) - 1;
			String lineComment = source[startLineNumber].trim();

			System.out.println("@@@@@");
			System.out.println(lineComment);

			int start = node.getStartPosition();
			ASTNode withoutSignature = NodeFinder.perform(
					node.getAlternateRoot(), start, 0);
			System.out.println(withoutSignature.getParent());

			System.out.println("@@@@@");
			System.out.println("END LINE COMMENT");

			return true;

		}

		@Override
		public boolean visit(BlockComment node) {
			System.out.println("BEGIN BLOCK COMMENT");

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

			System.out.println("@@@@@");
			System.out.println(blockComment.toString());

			int start = node.getStartPosition();
			ASTNode withoutSignature = NodeFinder.perform(
					node.getAlternateRoot(), start, 0);
			System.out.println(withoutSignature.getParent());

			System.out.println("@@@@@");

			System.out.println("END BLOCK COMMENT");

			return true;
		}

	}

}