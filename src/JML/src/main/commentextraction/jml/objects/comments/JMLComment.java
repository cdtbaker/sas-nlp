package main.commentextraction.jml.objects.comments;

import main.commentextraction.jml.objects.framework.JMLElement;

public class JMLComment extends JMLElement {

	public JMLComment(String text) {
		super("comment");
		this.addContent((text));

	}

}
