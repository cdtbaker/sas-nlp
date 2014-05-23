package com.jml.output;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import com.jml.objects.framework.Attribute;
import com.jml.objects.framework.Element;
import com.jml.objects.framework.JMLElement;
import com.jml.objects.framework.StringElement;

public class XMLOutputter {

	private JMLElement root;
	private String indent = "    ";
	private static final String xmlS = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";

	public XMLOutputter(JMLElement e) {
		root = e;
	}

	public void print() {
		System.out.println(getTree(root, 0));
	}

	public String getString() {
		return xmlS + "\n" + (tab(1)) + getTree(root, 2);
	}

	private String getTree(Element e, int i) {
		StringBuilder s = new StringBuilder();

		JMLElement xmlE = null;
		if (e instanceof JMLElement) {
			xmlE = (JMLElement) e;

			s.append("<" + xmlE.getName());
			for (Attribute a : xmlE.attributes()) {
				s.append(" " + a.toString());
			}
			if (!xmlE.children().isEmpty()) {
				s.append(">");
				for (Element c : xmlE.children()) {
					s.append("\n");
					s.append(tab(i));
					s.append(getTree(c, i + 1));
				}
				s.append("\n");
				s.append(tab(i - 1));

				s.append("</" + xmlE.getName() + ">");
			} else {
				s.append("/>");
			}
		} else {
			StringElement se = (StringElement) e;
			String[] st = se.getString().trim().split("\n");
			for (int j = 0; j < st.length; j++) {
				
				s.append(st[j]);
				if (j < st.length - 1) {
					s.append("\n");
				}
				s.append(tab(i-1));
			}
		}

		return s.toString();
	}

	private String tab(int i) {
		StringBuilder sb = new StringBuilder();
		for (int j = 0; j < i; j++) {
			sb.append(indent);
		}
		return sb.toString();
	}

	public void setIndent(int i) {
		StringBuilder s = new StringBuilder();
		for (int j = 0; j < i; j++) {
			s.append(" ");
		}
		indent = s.toString();
	}

	public void toFile(String path) throws IOException {
		FileWriter fw = new FileWriter(path + ".xml");
		BufferedWriter bw = new BufferedWriter(fw);

		bw.write(getString());

		bw.close();
	}

}
