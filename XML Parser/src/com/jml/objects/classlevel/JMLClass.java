package com.jml.objects.classlevel;

import com.jml.objects.framework.JMLElement;

public class JMLClass extends JMLElement{

	public JMLClass(String name) {
		super("class");
		this.addAttribute("name", name);
	}
	
	public void addSuperclass(String superClass){
		this.addAttribute("extends", superClass);
	}

}
