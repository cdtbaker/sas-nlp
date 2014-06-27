package com.jml.objects.scopes;

import com.jml.objects.framework.JMLElement;
import com.jml.objects.scopes.params.*;

public class JMLMethod extends JMLElement {

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

	public void addParam(String type, String name) {
		if (this.children.size() < 1
				|| !(this.children.get(0) instanceof JMLParamaters)) {
			this.children.add(0, new JMLParamaters());
		}
		((JMLParamaters) this.children.get(0)).addContent(new JMLParamater(
				name, type));
	}

}
