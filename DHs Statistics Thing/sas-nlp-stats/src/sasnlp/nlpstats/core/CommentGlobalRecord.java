package sasnlp.nlpstats.core;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class is used when parsing XML comment files to build up a single
 * string containing all the text.
 *
 */
public class CommentGlobalRecord {
	
	public static StringBuilder allComments = new StringBuilder();
	public static CommentType MODE = CommentType.BOTH;
	
	/*
	 * this appends a comment that is encountered during a file walk
	 * to a single list of comments for this corpus.
	 */
	public static void appendClassComments(Document doc)
	{	
		if(doc != null)
		{
			String s = "";
			NodeList nodeList = doc.getElementsByTagName("javadoc");
			
			if(MODE == CommentType.BOTH || MODE == CommentType.JAVADOC)
			{
			    for (int i = 0; i < nodeList.getLength(); i++) {
			        Node node = nodeList.item(i);
			        if (node.getNodeType() == Node.ELEMENT_NODE && node instanceof Element) {
			        	Element e = (Element)node;
			        	
			        	NodeList javadocElements = e.getElementsByTagName("text");
			        	
			        	if(javadocElements.getLength() > 0)
			        	{
			        		s = (e.getElementsByTagName("text").item(0).getTextContent());
			        		allComments.append(" ");
			        		allComments.append(s);
			        	}
			        }
			    }
			}
			
			if(MODE == CommentType.BOTH || MODE == CommentType.INLINE)
			{
			    nodeList = doc.getElementsByTagName("comment");
			    for (int i = 0; i < nodeList.getLength(); i++) {
			    	Node node = nodeList.item(i);
			    	
		        	if(node.getTextContent() != null && !node.getTextContent().equals(""))
		        	{
		        		s = node.getTextContent();
		        		allComments.append(" ");
		        		allComments.append(s);
		        	}
			    }
			}
		}
	}
	
	public static String generate()
	{
		return allComments.toString();
	}
}
