package commentvisitor;

public class CommentContext {

	protected String commentText;
	protected String parentName;
	
	public CommentContext(String commentText){
		this.commentText = commentText;
	}
	
	public CommentContext(String commentText, String parentName){
		this.commentText = commentText;
		this.parentName = parentName;
	}
	
	public void setParentName(String parentName) {
		this.parentName = parentName;
	}
}
