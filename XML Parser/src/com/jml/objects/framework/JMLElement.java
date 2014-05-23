package com.jml.objects.framework;

import java.util.ArrayList;
import java.util.List;

public class JMLElement extends Element {

	protected List<Element> children;
	protected List<Attribute> attributes;
	protected String name;

	public JMLElement(String name) {
		this.name = name;
		children = new ArrayList<>();
		attributes = new ArrayList<>();
	}

	public void addContent(JMLElement e) {
		children.add(e);
	}

	public void addContent(String s) {
		StringElement string = new StringElement(s);
		children.add(string);
	}

	public void addAttribute(Attribute a) {
		attributes.add(a);
	}

	public void addAttribute(String k, String v) {
		attributes.add(new Attribute(k, v));
	}
	
	public String getName(){
		return name;
	}
	
	public List<Element> children(){
		return children;
	}
	
	public List<Attribute> attributes(){
		return attributes;
	}



}
