package com.jml.objects.scopes;

import com.jml.objects.framework.JMLElement;

public class JMLMethod extends JMLElement{

	public JMLMethod(String name, String returnType) {
		super("method");
		this.addAttribute("name", name);
		this.addAttribute("type", returnType);
	}
	
	public JMLMethod(String name) {
		super("method");
		this.addAttribute("name", name);
		this.addAttribute("type", "constructor");
	}

}
