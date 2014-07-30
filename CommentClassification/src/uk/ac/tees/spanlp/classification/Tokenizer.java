package uk.ac.tees.spanlp.classification;

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

public class Tokenizer {
	
	private final StanfordCoreNLP corenlp;
	
	public Tokenizer() {
		Properties properties = new Properties();
		properties.put("annotators", "tokenize");
		properties.put("tokenize.options", "untokenizable=noneDelete");
		corenlp = new StanfordCoreNLP(properties);	
	}
	
	public List<String> tokenize(String input) {
		
		Annotation annotation = new Annotation(input);
		corenlp.annotate(annotation);
		
		List<String> tokens = new LinkedList<String>();
		for (CoreLabel token : annotation.get(TokensAnnotation.class))
			tokens.add(token.word().toLowerCase());
		return tokens;
		
	}
	
}
