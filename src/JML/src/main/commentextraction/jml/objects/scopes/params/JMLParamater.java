package main.commentextraction.jml.objects.scopes.params;

import main.commentextraction.jml.objects.statement.JMLVariable;

public class JMLParamater extends JMLVariable {

	public JMLParamater(String name, String type) {
		super(name, type);
		this.name = "param";
	}

}
