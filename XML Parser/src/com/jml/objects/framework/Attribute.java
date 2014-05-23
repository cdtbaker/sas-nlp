package com.jml.objects.framework;

public class Attribute {
	
	protected String key;
	protected String val;
	
	public Attribute(String key, String val){
		this.key=key;
		this.val=val;
	}

	public String toString(){
		return key+"=\""  +val+  "\"";
	}
	
}
