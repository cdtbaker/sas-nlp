package sasnlp.nlpstats.core;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * This class will parse given strings and produce n-grams, including
 * k-skip-n-grams if necessary and provide a final count of the instances
 * of both of these metrics.
 *
 */
public class NGramParser {

	private static final String CARDINAL_PLACEHOLDER = "<CARDINAL>";
	private int ngramLength = 2;
	private int ngramCount = 0;
	private Map<String, Integer> ngrams = new HashMap<String, Integer>();

	public NGramParser(int nGramSize) {
		this.ngramLength = nGramSize;
	}

	public void parse(List<String> wl, boolean withSentenceEndsStarts, int skips) {
		this.addListOfWords(wl, 0, withSentenceEndsStarts, skips);
	}

	public static boolean isInteger(String s) {
		try {
			Integer.parseInt(s);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	public void addListOfWords(List<String> list, int toSkip, boolean withSentenceEndsStarts, int skips) {
			for (int l = 0; l <= skips; l++) {
				loopK : for (int i = ngramLength - 1; i < list.size(); i++) {
					
					if(i - (ngramLength + l - 1) < 0)
					{
						continue loopK;
					}
					String ngram = "";

					for (int j = (ngramLength + l - 1); j + l >= 0 && j >= 0; j--) {
						 
						if(i - j >= 0)
						{
							String s = list.get(i - j);
							if (!isInteger(s))
								ngram += s + " ";
							else
								ngram += CARDINAL_PLACEHOLDER + " ";
						}
						else
						{
							// If this happens, this is an invalid state (trying to get
							// an out of bounds word in this ngram/sentence).
							ngram = "";
						}
					}
					
					if(ngram.split(" ").length == ngramLength)
						storeNGram(ngram);
					
					for(int startingword = 1; startingword <= (ngramLength + l - 2); startingword++)
					{
						if(l == 1)
							storeNGram(   cloneAndSkipNGram(ngram, startingword)   );
						
						for(int remwords = startingword + 1; remwords <= (ngramLength + l - 2); remwords++)
						{
							if(l > 1)
								storeNGram(   cloneAndSkipNGram(ngram, startingword, remwords)   );
						}
					}
				}
			
		}
	}
	
	public String cloneAndSkipNGram(String ngram, Integer...skips)
	{
		String newNGram = "";
		
		int index = 0;
		String[] splits = ngram.split(" ");
		
		for(int i = 0; i < splits.length; i++)
		{
			if(!Arrays.asList(skips).contains(index))
			{
				if(index == 0)
					newNGram += splits[index];
				else
					newNGram += " " + splits[index];
			}
			
			index++;
		}
		
		return newNGram;
	}
	
	public void storeNGram(String ngram) {
		if (!ngrams.containsKey(ngram)) 
		{
			ngrams.put(ngram, new Integer(0));
		} else 
		{
			ngrams.put(ngram, ngrams.get(ngram) + 1);
		}

		ngramCount++;
	}

	public int getNgramCount() {
		return ngramCount;
	}

	public Map<String, Integer> getNGramMap() {
		Map<String, Integer> result = new HashMap<String, Integer>();

		for (Entry<String, Integer> x : ngrams.entrySet()) {
			result.put(x.getKey().toString(), x.getValue() + 1);
		}

		return result;
	}
}