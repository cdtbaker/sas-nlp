package main.commentextraction.com.jml.objects.comments;

public class JMLJavadoc extends JMLComment {

	public JMLJavadoc(String text) {
		super(text);
		this.name = "javadoc";

	}

	public void addTag(String name, String text) {
		this.addContent(new JavadocTag(name, text));
	}

}
