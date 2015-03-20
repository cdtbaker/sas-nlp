package uk.ac.tees.spanlp.classification;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;

import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

public class CommentReader {
	
	private static final XPathExpression<Element> xpath = XPathFactory.instance().compile("//comment", Filters.element());
	
	private final SAXBuilder saxBuilder; 
	private final StanfordCoreNLP corenlp;
	private final Iterator<File> fileIterator;
	private Iterator<List<String>> commentIterator;
	private final List<Error> errors;
	
	public CommentReader(File root) {
		saxBuilder = new SAXBuilder();
		
		Properties properties = new Properties();
		properties.put("annotators", "tokenize");
		properties.put("tokenize.options", "untokenizable=noneDelete");
		corenlp = new StanfordCoreNLP(properties);	
		
		List<File> files = recursivelyList(root);
		fileIterator = files.iterator();
		errors = new LinkedList<Error>();
		readNextFile();
	}
	
	public List<Error> getErrors() {
		return errors;
	}
	

	public List<String> next() {
		if (commentIterator != null && commentIterator.hasNext()) {
			return commentIterator.next();
		} else {
			readNextFile();
			return commentIterator == null ? null : next();
		}
	}

	private void readNextFile() {
		if (fileIterator.hasNext()) {
			File file = fileIterator.next();
			try {
				List<List<String>> comments = new LinkedList<List<String>>();
				Document document = saxBuilder.build(file);
				for (Element element : xpath.evaluate(document)) {
					
					// process the comment text with corenlp
					Annotation text = new Annotation(element.getText());
					corenlp.annotate(text);
					
					// convert the corenlp annotations to a simple list of strings
					List<String> tokens = new LinkedList<String>();
					for (CoreLabel token : text.get(TokensAnnotation.class))
						tokens.add(token.word().toLowerCase());
					
					// save the list for later iteration
					comments.add(tokens);
				}
				
				commentIterator = comments.iterator();
			} catch (Exception e) {
				// something went wrong reading the file; save the error for manual examination
				errors.add(new Error(file, e));
			}
		} else {
			// no more files to read
			commentIterator = null;
		}
	}

	private static List<File> recursivelyList(File directory) {
		List<File> files = new LinkedList<File>();
		for (File file : directory.listFiles())
			if (file.isDirectory())
				files.addAll(recursivelyList(file));
			else if (file.getName().endsWith(".xml") && !file.getName().startsWith("."))
				files.add(file);
		return files;
	}
	
	public static class Error {
		
		public final File file;
		public final Exception exception;
		
		private Error(File file, Exception exception) {
			this.file = file;
			this.exception = exception;
		}
		
	}

}
