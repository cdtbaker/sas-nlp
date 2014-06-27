package com.jml.objects.classlevel;

import com.jml.objects.framework.JMLElement;
import com.jml.objects.sourcelevel.typeparams.JMLTypeParamater;
import com.jml.objects.sourcelevel.typeparams.JMLTypeParamaters;

public class JMLInterface extends JMLElement {

	public JMLInterface(String name) {
		super("interface");
		this.addAttribute("name", name);
	}

	public void addTypeParam(String name) {
		if (this.children.size() < 1
				|| !(this.children.get(0) instanceof JMLTypeParamaters)) {
			this.children.add(0, new JMLTypeParamaters());
		}
		((JMLTypeParamaters) this.children.get(0))
				.addContent(new JMLTypeParamater(name));
	}
}
