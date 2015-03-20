package main.commentextraction.jml.objects.sourcelevel.typeparams;

import main.commentextraction.jml.objects.framework.JMLElement;

public class JMLTypeParamater extends JMLElement {

	public JMLTypeParamater(String name) {
		super("type_param");
		this.addAttribute("name", name);
	}

}
