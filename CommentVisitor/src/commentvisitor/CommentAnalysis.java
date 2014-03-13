package commentvisitor;

import java.util.ArrayList;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import edu.cmu.cs.crystal.AbstractCrystalMethodAnalysis;

public class CommentAnalysis extends AbstractCrystalMethodAnalysis {	

	//lists to hold comments
	ArrayList<JavadocContext> javadocList = new ArrayList<JavadocContext>();
	ArrayList<CommentContext> commentList = new ArrayList<CommentContext>();
	
	@Override
	public void analyzeMethod(MethodDeclaration d) {
		d.accept(new CommentVisitor());
		System.out.println("Javadoc comments:" + javadocList.get(0).toString());
	}

	@Override
	public String getName() {
		return "Comment Visitor";
	}
	//visitor to find comments
	private class CommentVisitor extends ASTVisitor{
		
		//called whenever javadoc comment is found
		@Override
		public void endVisit(Javadoc node) {
			//System.out.println("Adding javadoc to list");
			javadocList.add(new JavadocContext(node));
			super.endVisit(node);
		}
		
		
	}
	
}
