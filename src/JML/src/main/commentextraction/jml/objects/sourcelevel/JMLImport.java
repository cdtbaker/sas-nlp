package main.commentextraction.jml.objects.sourcelevel;

import main.commentextraction.jml.objects.framework.JMLElement;

public class JMLImport extends JMLElement {

	public JMLImport(String name) {
		super("import");
		this.addAttribute("package", name);
	}

}
