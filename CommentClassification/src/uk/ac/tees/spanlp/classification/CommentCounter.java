package uk.ac.tees.spanlp.classification;

import java.io.File;
import java.io.IOException;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;


public class CommentCounter {

	private static final SAXBuilder builder = new SAXBuilder();
	private static final XPathExpression<Element> xpath = XPathFactory.instance().compile("//comment", Filters.element());
	
	private static int visit(File file, int depth) throws JDOMException, IOException {
		
		if (file.isDirectory()) {
			
			int total = 0;
			for (File child : file.listFiles())
				if (child.isDirectory() || (child.getName().endsWith(".xml") && !child.getName().startsWith(".")))
					total += visit(child, depth + 1);
			if (total > 0)
				println(file.getAbsolutePath() + " contains " + Integer.toString(total) + " comments", depth);
			return total;
			
			
		} else {
			try {
				Document document = builder.build(file);
				return xpath.evaluate(document.getRootElement()).size();
			} catch (JDOMException jde) {
				System.err.println(jde.getMessage());
				return 0;
			}
		}
		
		
	}
	
	private static void println(String line, int depth) {
		for (int i = 0; i < depth; i++) System.out.print("  ");
		System.out.println(line);
	}
	
	public static void main(String[] arg) throws JDOMException, IOException {
		visit(new File(arg[0]), 0);
	}
}
