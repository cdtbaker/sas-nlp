package main.commentextraction.com.jml.objects.comments;

import main.commentextraction.com.jml.objects.framework.JMLElement;

public class JavadocTag extends JMLElement {

	public JavadocTag(String name, String text) {
		super(name);
		this.addContent(text);

		

	}

}
