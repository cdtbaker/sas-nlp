package sanlp.nlp.core;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class CommentGlobalRecord {
	
	public static StringBuilder allComments = new StringBuilder();
	
	public static void appendClassComments(Document doc)
	{	
		if(doc != null)
		{
			String s = "";
			NodeList nodeList = doc.getElementsByTagName("javadoc");
		    for (int i = 0; i < nodeList.getLength(); i++) {
		        Node node = nodeList.item(i);
		        if (node.getNodeType() == Node.ELEMENT_NODE && node instanceof Element) {
		        	Element e = (Element)node;
		        	
		        	NodeList javadocElements = e.getElementsByTagName("text");
		        	
		        	if(javadocElements.getLength() > 0)
		        	{
		        		s = (e.getElementsByTagName("text").item(0).getTextContent());
		        		System.out.println(s);
		        		s = s.replace("* ", " ");
		        		//s = s.replace("\n", " ");
		        		s = s.replace("  ", " ");
		        	}
		        }
		    }
		    
			allComments.append(s);
		}
	}
	
	public static String generate()
	{
		return allComments.toString();
	}
}
