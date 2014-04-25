package xmlparser;

import java.util.Comparator;

import org.jdom2.Attribute;
import org.jdom2.Element;

public class XMLMethod extends Element{
	
	private Attribute name,type;
	private int startLine,endLine;
	private int methodNumber;
	
	public XMLMethod(int i){
		super("method");
		this.methodNumber = i;
	}
	public Attribute getMethodName() {
		return name;
	}
	public void setMethodName(Attribute name) {
		this.setAttribute(name);
		this.name = name;
	}
	public Attribute getType() {
		return type;
	}
	public void setType(Attribute type) {
		this.setAttribute(type);
		this.type = type;
	}
	public int getStartLine() {
		return startLine;
	}
	public void setStartLine(int startLine) {
		this.startLine = startLine;
	}

	public int getEndLine() {
		return endLine;
	}
	public void setEndLine(int endLine) {
		this.endLine = endLine;
	}
	public int getMethodNumber() {
		return methodNumber;
	}
	public void setMethodNumber(int methodNumber) {
		this.methodNumber = methodNumber;
	}



}
