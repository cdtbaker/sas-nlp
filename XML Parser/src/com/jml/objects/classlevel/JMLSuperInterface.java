package com.jml.objects.classlevel;

import com.jml.objects.framework.JMLElement;

public class JMLSuperInterface extends JMLElement{

	public JMLSuperInterface(String name) {
		super("implements");
		this.addAttribute("interface", name);
	}

	
}
