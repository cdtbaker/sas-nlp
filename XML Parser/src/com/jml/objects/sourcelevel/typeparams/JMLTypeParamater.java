package com.jml.objects.sourcelevel.typeparams;

import com.jml.objects.framework.JMLElement;

public class JMLTypeParamater extends JMLElement {

	public JMLTypeParamater(String name) {
		super("type_param");
		this.addAttribute("name", name);
	}

}
