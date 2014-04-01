package commentvisitor;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.MethodDeclaration;

public class JavadocContext extends CommentContext {

	String[] tags;
	Javadoc javadoc;
	
	public JavadocContext(Javadoc javadoc) {
		
		//sets default comment text to empty string in case of null
		super("",javadoc);
		
		// set the name of the method containing the comment
		ASTNode parent = javadoc.getParent();
		if(parent.getNodeType() == ASTNode.METHOD_DECLARATION) {
			// JFF: I am assuming that getNodeType() is coherent with the type. 
			//      Otherwise, the following cast may be unsafe.
			String methodName = ((MethodDeclaration)parent).getName().getIdentifier();
			this.setParentName(methodName);
		}

		//if not null set comment text to the text
		if (javadoc.tags().size() >0) {
				this.commentText = javadoc.tags().get(0).toString().trim();
		}
		this.javadoc = javadoc;
		//tags added to an array as string
		tags = new String[javadoc.tags().size()];
		for(int i=0;i<tags.length;i++){
			tags[i] = javadoc.tags().get(i).toString();
		}
		
	}
	
	public String toString() {
		String ret = "\nParent name:\t" + this.parentName + "\n";
		ret = ret + "Javadoc comment:\n";
		for(String s: tags) {
			ret = ret + s;
		}
		ret = ret + "\n";
		return ret;
	}
}
