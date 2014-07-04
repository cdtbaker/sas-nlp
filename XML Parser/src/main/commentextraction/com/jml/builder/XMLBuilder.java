package main.commentextraction.com.jml.builder;

import java.util.Stack;

import main.commentextraction.com.jml.objects.framework.Element;
import main.commentextraction.com.jml.objects.framework.JMLElement;
import main.commentextraction.com.jml.objects.framework.StringElement;
import main.commentextraction.com.jml.objects.sourcelevel.JMLSource;


public class XMLBuilder {

	private Stack<JMLElement> eStack;
	private JMLElement root;

	public XMLBuilder(String packageName) {
		eStack = new Stack<JMLElement>();
		this.root = new JMLSource(packageName);
		eStack.push(root);
	}

	public JMLElement getRoot() {
		return root;
	}

	public void addBlock(JMLElement e) {
		eStack.peek().addContent(e);
		eStack.push(e);
	}

	public void endBlock() {
		eStack.pop();
	}

	public void addLine(Element e) {
		if (e instanceof JMLElement) {
			eStack.peek().addContent((JMLElement) e);
		} else {
			eStack.peek().addContent(((StringElement) e).getString());
		}
	}

}
