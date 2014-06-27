package com.jml.objects.scopes.params;

import com.jml.objects.statement.JMLVariable;

public class JMLParamater extends JMLVariable {

	public JMLParamater(String name, String type) {
		super(name, type);
		this.name = "param";
	}

}
