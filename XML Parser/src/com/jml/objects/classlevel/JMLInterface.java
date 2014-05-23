package com.jml.objects.classlevel;

import com.jml.objects.framework.JMLElement;

public class JMLInterface extends JMLElement{

	
	public JMLInterface(String name) {
		super("interface");
		this.addAttribute("name", name);
	}
	
	public void addSuperInterface(String superInterface){
		this.addContent(new JMLSuperInterface(superInterface));
	}
}
