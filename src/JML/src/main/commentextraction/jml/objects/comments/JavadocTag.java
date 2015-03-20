package main.commentextraction.jml.objects.comments;

import main.commentextraction.jml.objects.framework.JMLElement;

public class JavadocTag extends JMLElement {

	public JavadocTag(String name, String text) {
		super(name);
		this.addContent(text);

		

	}

}
