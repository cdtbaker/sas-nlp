package main.commentextraction.com.jml.objects.framework;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

public class Attribute {

	protected String key;
	protected String val;

	public Attribute(String key, String val) {
		this.key = key;
		this.val = val;
		this.val = StringEscapeUtils.escapeXml11(val);

	}

	public String toString() {
		return key + "=\"" + val + "\"";
	}

}
