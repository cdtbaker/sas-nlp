package main.commentextraction.com.jml.objects.statement;

import main.commentextraction.com.jml.objects.framework.JMLElement;

public class JMLVariable extends JMLElement {

	public JMLVariable(String name, String type) {
		super("declaration");
		this.addAttribute("name", name);
		this.addAttribute("type", type);
	}

}