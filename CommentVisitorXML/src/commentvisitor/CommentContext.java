package commentvisitor;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.MethodDeclaration;

public class CommentContext {

	protected String commentText;
	protected String parentName;
	protected ASTNode commentNode;
	public CommentContext(String commentText, ASTNode commentNode){
		this.commentText = commentText;
		this.commentNode = commentNode;
		ASTNode parent = commentNode.getParent();
		if(parent.getNodeType() == ASTNode.METHOD_DECLARATION) {
			// JFF: I am assuming that getNodeType() is coherent with the type. 
			//      Otherwise, the following cast may be unsafe.
			String methodName = ((MethodDeclaration)parent).getName().getIdentifier();
			this.setParentName(methodName);
		}
		
	}
	
	public CommentContext(String commentText, String parentName){
		this.commentText = commentText;
		this.parentName = parentName;
	}
	
	public void setParentName(String parentName) {
		this.parentName = parentName;
	}
}
