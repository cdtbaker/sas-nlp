package commentvisitor;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.LineComment;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import testanalysis.TestAnalysis;
import edu.cmu.cs.crystal.AbstractCrystalMethodAnalysis;

public class CommentAnalysis extends AbstractCrystalMethodAnalysis {

	// lists to hold comments
	List<JavadocContext> javadocList;
	List<CommentContext> commentList;
	long startTime;
	long endTime;

	// Called before all methods
	@Override
	public void beforeAllMethods(ITypeRoot compUnit, CompilationUnit rootNode) {
		List<ASTNode> commentList1 = rootNode.getCommentList();
		
		for(int i=0;i<commentList1.size();i++){
			//print each comment in the list
		System.out.println(commentList1.get(i));
		try {
			//print the name of the method associated with the comment
			System.out.println(compUnit.getElementAt(commentList1.get(i).getStartPosition()).getJavaModel());
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
		
		
		
		
		// Initialises this lists
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
		d.accept(new CommentVisitor());
	}

	@Override
	public String getName() {
		return "Comment Visitor";
	}

	// visitor to find comments
	private class CommentVisitor extends ASTVisitor {
		// called whenever javadoc comment is found
		@Override
		public void endVisit(Javadoc node) {
			javadocList.add(new JavadocContext(node));
			super.endVisit(node);
		}

		@Override
		public boolean visit(LineComment node) {
			System.out.println("LINE COMMENT");
			return true;
		}
		
		

	}
	
}