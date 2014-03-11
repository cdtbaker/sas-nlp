package commentvisitor;

import org.eclipse.jdt.core.dom.Javadoc;

public class JavadocContext extends CommentContext {

	String[] tags;
	Javadoc javadoc;
	
	public JavadocContext(Javadoc javadoc) {
		//sets default comment text to empty string in case of null
		super("");
		//if not null set comment text to the text
		if (javadoc.tags().get(0) != null) {
				this.commentText = javadoc.tags().get(0).toString().trim();
		}
		this.javadoc = javadoc;
		//tags added to an array as string
		tags = new String[javadoc.tags().size()];
		for(int i=0;i<tags.length;i++){
			tags[i] = javadoc.tags().get(i).toString();
		}
		
	}
}
