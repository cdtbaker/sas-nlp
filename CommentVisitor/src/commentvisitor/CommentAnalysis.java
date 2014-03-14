package commentvisitor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Javadoc;
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
		
		System.out.println(d.getParent().toString());
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

	}

}
