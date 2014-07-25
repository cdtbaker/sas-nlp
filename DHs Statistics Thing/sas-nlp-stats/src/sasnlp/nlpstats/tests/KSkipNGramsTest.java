package sasnlp.nlpstats.tests;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import sasnlp.nlpstats.core.NGramParser;
import sasnlp.nlpstats.core.Stemmer;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;

public class KSkipNGramsTest {
	
	private static int NUMBER_OF_SKIPS = 2;
	private static int LENGTH_OF_N_GRAMS = 3;
	
	/**
	 * Entry point to be used for testing.
	 */
	public static void main(String... args) throws FileNotFoundException, IOException {
		HashMap<String, String> docs = new HashMap<String, String>();

		docs.put("doc1", "Insurgents killed in ongoing fighting");
		
		calculate(docs, LENGTH_OF_N_GRAMS);
	}
	
	/**
	 * This method requires two corpora, a GENERAL document and a DOMAIN document.
	 * It will then parse the documents to produce PMI values for all bi-grams, n-gram AND single word TF-IDF
	 * calculations, as well as calculating the domain specificity of all domain n-grams and single words.
	 */
	public static void calculate(HashMap<String, String> docs, int nGramLength) {
		Properties props = new Properties();
		props.put("tokenize.options", "normalizeParentheses=false,americanize=false,untokenizable=firstDelete");
		props.put("annotators", "tokenize");

		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		
		// Used to parse the n-grams of given length
		NGramParser biGramExtractor = new NGramParser(nGramLength);
		
		// Records all  words so that the n-gram parser operates on these words (sequentially).
		List<String> wordList = new ArrayList<String>();

		for (Entry<String, String> x : docs.entrySet()) {
			
			// Parse each document (should only be 2 of them)
			Annotation document = new Annotation(x.getValue().toLowerCase());
			pipeline.annotate(document);

			for (CoreLabel token : document.get(TokensAnnotation.class)) {
				
				// The stemmer is a little bit complicated to use, could probably be made cleaner!
				Stemmer stemmer = new Stemmer();
				stemmer.add(token.get(TextAnnotation.class).toLowerCase().toCharArray(), token.get(TextAnnotation.class).length());
				// stemmer.stem();
				String word = (token.get(TextAnnotation.class).toLowerCase());

				// Record this word occurrence for later use.
				wordList.add(word);
			}
		}

		biGramExtractor.parse(wordList, false, NUMBER_OF_SKIPS);  // 2 skip
		
		for(Entry<String, Integer> x : biGramExtractor.getNGramMap().entrySet())
		{
			String ngram =  String.format("%1$-" + 30 + "s", x.getKey());  
			System.out.println(ngram + ":\t\t" + x.getValue());
		}
	}
}
