package main.commentextraction.jml.objects.sourcelevel;

import main.commentextraction.jml.objects.framework.JMLElement;

public class JMLSource extends JMLElement {

	public JMLSource(String packageName) {
		super("source");
		this.addAttribute("package", packageName);
	}

}
