package main.commentextraction.com.jml.objects.framework;

import org.apache.commons.lang3.StringEscapeUtils;

public class Attribute {

	protected String key;
	protected String val;

	public Attribute(String key, String val) {
		this.key = key;
		this.val = val;
		this.val = StringEscapeUtils.escapeXml(val); // TODO: escapeXml11 wasn't working with Eclipse Luna; fix this later?

	}

	public String toString() {
		return key + "=\"" + val + "\"";
	}

}
