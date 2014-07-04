package main.commentextraction.com.jml.objects.classlevel;

import main.commentextraction.com.jml.objects.framework.JMLElement;
import main.commentextraction.com.jml.objects.sourcelevel.typeparams.JMLTypeParamater;
import main.commentextraction.com.jml.objects.sourcelevel.typeparams.JMLTypeParamaters;


public class JMLSuperInterface extends JMLElement {

	public JMLSuperInterface(String name) {
		super("implements");
		this.addAttribute("interface", name);
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
