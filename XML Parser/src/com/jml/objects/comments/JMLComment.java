package com.jml.objects.comments;

import com.jml.objects.framework.JMLElement;

public class JMLComment extends JMLElement {

	public JMLComment(String text) {
		super("comment");
		this.addContent((text));

	}

}
