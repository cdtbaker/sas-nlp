package main.commentextraction.com.jml.objects.sourcelevel;

import main.commentextraction.com.jml.objects.framework.JMLElement;

public class JMLSource extends JMLElement {

	public JMLSource(String packageName) {
		super("source");
		this.addAttribute("package", packageName);
	}

}
