package com.jml.objects.framework;

public class StringElement extends Element{

	protected String s;
	
	public StringElement(String s){
		this.s = s;
	}
	
	public String getString(){
		return s;
	}
}
