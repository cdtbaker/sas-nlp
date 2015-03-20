package sanlp.nlp.core;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import edu.stanford.nlp.*;
import edu.stanford.nlp.dcoref.CorefChain;
import edu.stanford.nlp.dcoref.CorefCoreAnnotations.CorefChainAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.*;
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.process.Morphology;
import edu.stanford.nlp.semgraph.*;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.stats.ClassicCounter;
import edu.stanford.nlp.stats.Counter;
import edu.stanford.nlp.stats.Counters;
import edu.stanford.nlp.trees.*;
import edu.stanford.nlp.trees.TreeCoreAnnotations.*;
import edu.stanford.nlp.util.*;

public class OriginalTestHack {
	
	//private static final String customStopWordList = "start,starts,period,periods,a,an,and,are,as,at,be,but,by,for,if,in,into,is,it,no,not,of,on,or,such,that,the,their,then,there,these,they,this,to,was,will,with";

	private static final String customStopWordList = ".;,;?";
	
	
	public static void main(String... args) {
		// creates a StanfordCoreNLP object, with POS tagging, lemmatization,
		// NER, parsing, and coreference resolution
		Properties props = new Properties();
		props.put("tokenize.options", "normalizeParentheses=false,americanize=false");
		props.put("annotators",
				"tokenize, ssplit, stopword, pos, lemma, parse");
		props.setProperty("customAnnotatorClass.stopword", "StopwordAnnotator");
		//props.setProperty(StopwordAnnotator.STOPWORDS_LIST, customStopWordList);
		
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

		String test0 = "This class builds a similarity matrix between two separate execution spectras (sets of trace). "
				+ "So comparing two 6-line-long traces would produce a 6x6 matrix with 36 entries. EACH line is scored "
				+ "and then the 'phases' are extracted from this information in a seperate (but inside this class) step. "
				+ "To visualise what the similarity matrix IS, enable verbose mode "
				+ "and check the output. ";
		
		String test1 = "This is step 1 of the similarity matrix computation. This would build a matrix of similarity "
				+ "between two traces with each line scored "
				+ "against each other line. ";
		
		String test2 = "This is step 2 of the matrix computation. Iterate through the SimilarityMatrix to "
				+ "identify (sequentially) the most likely matches for each line of trace. So for each "
				+ "line pair (1 vs 1) (1 vs 2) (2 vs 1) etc., on each line identify the highest scoring match"
				+ " and mark this as the 'matched' item on this line. A line can only be matched once. ";
		
		String test3 = test0 + test1 + test2;
		
		// read some text in the text variable
		String text = ""
				+ "A ShingleFilter constructs shingles (token n-grams) from a token stream. In other words, it creates combinations of tokens as a single token. "
				+ "Construct a ShingleFilter with default shingle size: 2. "
				+ "Constructs a ShingleFilter with the specified shingle size from the TokenStream input. "
				+ "Constructs a ShingleFilter with the specified shingle size from the TokenStream input. "
				+ "Construct a ShingleFilter with the specified token type for shingle tokens and the default shingle size: 2. "
				+ "Sets the string to insert for each position at which there is no token "
				+ "Set the max shingle size. "
				+ "Set the min shingle size. "
				+ "Shall the output stream contain the input tokens (unigrams) as well as shingles?. "
				+ "Shall we override the behavior of outputUnigrams==false for those times when no shingles are available (because there are fewer than minShingleSize tokens in the input stream)? (default: false). "
				+ "Sets the string to use when joining adjacent tokens to form a shingle. "
				+ "Set the type of the shingle tokens produced by this filter. "
				+ "This is a informal comment that doesnt make sense and includes the variable xterm as it is related to the method testDescriber."
				+ "Other variables include ngramextractor and an arraylist and an Integer."
				+ "This class controls the flow of the Test execution. This is the central "
				+ "component of the tool as it is the 'event handler' that deals with events as"
				+ "triggered by the JUnit listener(s).";
		
		String lorem = "Sediment perspiciatis unde ut omnis iste natus error unde sit voluptatem doloremque, totam rem aperiam, eaque ipsa quae ab illo quia inventore veritatis et ut architecto beatae vitae dicta sunt explicabo. Nemo enim ipsam voluptatem quia ut voluptas sit aspernatur aut odit aut fugit, sed quia ut consequuntur magni dolores ut qui ratione voluptatem sequi nesciunt. Neque porro quisquam est, qui ut ipsum quia ut dolor sit amet, consectetur, adipisci velit, sed quia non numquam eius modi tempora incidunt ut labore et dolore magnam aliquam quaerat voluptatem. Ut enim ad minima veniam, quis nostrum ullam suscipit, nisi ut aliquid ex ea commodi quia ut? Quis autem vel eum iure ut qui in ea voluptate velit esse quam nihil consequatur, vel iure.";
		
		
		// create an empty Annotation just with the given text
		Annotation document = new Annotation(lorem.toLowerCase());
		
		Morphology mo = new Morphology();

		// run all Annotators on this text
		pipeline.annotate(document);
		
		
		Counter<String> words = new ClassicCounter<String>();

		NGramParser nge = new NGramParser(2);
		NGramParser biGramExtractor = new NGramParser(2);

		List<String> wordList = new ArrayList<String>();
		List<String> nonStemWordList = new ArrayList<String>();

		// these are all the sentences in this document
		// a CoreMap is essentially a Map that uses class objects as keys and
		// has values with custom types
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);

		for (CoreMap sentence : sentences) {
			System.out.println("\n\nSentence: " + sentence.toString());
			// traversing the words in the current sentence
			// a CoreLabel is a CoreMap with additional token-specific methods
			for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
				// this is the text of the token
				String word = token.get(TextAnnotation.class);
					System.out.print("\t" + word);
					
					Stemmer stemmer = new Stemmer();
					stemmer.add(token.get(TextAnnotation.class).toLowerCase().toCharArray(), token.get(TextAnnotation.class).length());
					stemmer.stem();
					System.out.print(" (st: " + stemmer.toString() +") ");
					

					//System.out.print("\t\t\t"  + token.get(StopwordAnnotator.class));

					wordList.add(stemmer.toString());

					//if(token.get(StopwordAnnotator.class).first != true && token.get(StopwordAnnotator.class).second != true) {
						nonStemWordList.add(word);
						words.incrementCount(word);
					//}
					//else
					//{
					//	System.out.print(" EXCL");
					//}
					
					System.out.println(" ");
			
				//String pos = token.get(PartOfSpeechAnnotation.class);
					//System.out.print("\t\t: " + pos);
				//String ne = token.get(NamedEntityTagAnnotation.class);
					//System.out.println(": " + ne);
			}

			// this is the parse tree of the current sentence
			Tree tree = sentence.get(TreeAnnotation.class);

			// this is the Stanford dependency graph of the current sentence
			SemanticGraph dependencies = sentence.get(CollapsedCCProcessedDependenciesAnnotation.class);
			
			//System.out.println(dependencies.toFormattedString());
		}
		
		
		//nge.parse(wordList, true, true);
		biGramExtractor.parse(nonStemWordList, false, false);
		
		System.out.println("\n\n\nNGRAMS: there are " + biGramExtractor.getNgramCount() + " ngrams");
			
		int n = 1;
		for(Entry<String, Integer> i : biGramExtractor.getNGramMap().entrySet())
		{
			if(i.getValue() > 2)
			{
				String[] s = i.getKey().split(" ");
				
				double pw1w2 = ((double)i.getValue() / (double)biGramExtractor.getNgramCount());
				double pw1 = (words.getCount(s[0]) / words.totalCount());
				double pw2 = (words.getCount(s[1]) / words.totalCount());
				
				double pmi = log2(pw1w2) - (log2(pw1) + log2(pw2));
				
				//double pmi = log2(  pw1w2    /    (pw1 * pw2)  );
				
				System.out.println(i.getKey() + "\t\t times:" + i.getValue() + " \tPMI: " + pmi     + "\t" + pw1w2 + " " + pw1 + " " + pw2 + " " + words.getCount(s[0]) + " " + words.getCount(s[1]) + " " + words.totalCount());
			}
		}
	}
	
	public static double log2(double a)
	{
		return Math.log(a)/Math.log(2);
	}
}
