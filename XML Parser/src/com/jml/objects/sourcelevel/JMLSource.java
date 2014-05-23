package com.jml.objects.sourcelevel;

import com.jml.objects.framework.JMLElement;

public class JMLSource extends JMLElement{

	public JMLSource(String packageName) {
		super("source");
		this.addAttribute("package",packageName);
	}

}
