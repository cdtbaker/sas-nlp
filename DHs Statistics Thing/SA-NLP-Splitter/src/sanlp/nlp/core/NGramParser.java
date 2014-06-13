package sanlp.nlp.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class NGramParser {
	
	private class Word {
		private List<String> words = new ArrayList<String>();
		private int type = 1; // 1 is text, 2 is number
		
		public Word(String word, int type)
		{
			this.words.add(word);
			this.type = type;
		}
		
		public void addValue(String value)
		{
			this.words.add(value);
		}
	}
	
	private class NGram {
		
		private HashMap<Integer, Word> ngram = new HashMap<Integer, Word>();
		
		public NGram()
		{
			
		}
		
		public String toString()
		{
			StringBuilder sb = new StringBuilder();
			for(Entry<Integer, Word> x : ngram.entrySet())
			{
				if(x.getKey() > 0)
				{
					sb.append(" ");
				}
				
				if(x.getValue().type == 1)
				{
					sb.append(x.getValue().words.get(0));
				}
				else if(x.getValue().type == 2)
				{
					sb.append("[num<");
					int c = 0;
					for(String s : x.getValue().words)
					{
						if(s != null)
						{
							sb.append(s);
							if(c++ != x.getValue().words.size() - 1)
								sb.append(",");
						}
					}
					sb.append(">]");
				}
			}
			
			return sb.toString();
		}
		
		public void add(Word w)
		{
			ngram.put(ngram.size(), w);
		}
	}
	
	private static NGram nGramExists(NGram needle, Map<NGram, Integer> ngrams)
	{
		loop1 : for(Entry<NGram, Integer> x : ngrams.entrySet())
		{
			int matches = 0;
			for(Entry<Integer, Word> w : x.getKey().ngram.entrySet())
			{	
				if(w.getValue().type == 2 && needle.ngram.get(w.getKey()).type == 2)
				{
					matches++;
				}
				else if(!w.getValue().words.get(0).equals(needle.ngram.get(w.getKey()).words.get(0)))
				{
					continue loop1;
				}
				else
				{
					matches++;
				}
			}
			
			if(needle.ngram.size() == matches)
				return x.getKey();
		}
		
		return null;
	}
	
	private int ngramLength = 2;
	private int ngramCount = 0;

	private Map<NGram, Integer> ngrams = new HashMap<NGram, Integer>();

	public NGramParser(int nGramSize) {
		this.ngramLength = nGramSize;
	}

	public void parse(List<String> wl, boolean withSkipNGrams,
			boolean withSentenceEndsStarts) {
		if (!withSkipNGrams) {
			this.addListOfWords(wl, -100, withSentenceEndsStarts);
			return;
		}

		for (int i = ngramLength; i >= 0; i--) {
			addListOfWords(wl, i, withSentenceEndsStarts);
		}
	}
	
	public static boolean isInteger(String s) {
	    try { 
	        Integer.parseInt(s); 
	    } catch(NumberFormatException e) { 
	        return false; 
	    }
	    // only got here if we didn't return false
	    return true;
	}

	public void addListOfWords(List<String> list, int toSkip,
			boolean withSentenceEndsStarts) {
		int modifier = 0;
		if (withSentenceEndsStarts)
			modifier = (ngramLength - 1);
		for (int i = ngramLength - 1 - modifier; i < list.size() + modifier; i++) {			
			NGram ngram = new NGram();

			for (int j = (ngramLength - 1); j >= 0; j--) {
				//ngram.add(new Word(" ", 1));

				if (j == toSkip)
					ngram.add(new Word("_", 1));
				else if (i - j < 0)
					ngram.add(new Word("<s>", 1));
				else if (i - j > list.size() - 1)
					ngram.add(new Word("</s>", 1));
				else {
					String s = list.get(i - j);
					if(!isInteger(s))
						ngram.add(new Word(s, 1));
					else
						ngram.add(new Word(s, 2));
				}
			}

			NGram existingngram = nGramExists(ngram, ngrams);
			if(existingngram == null)
			{
				ngrams.put(ngram, new Integer(0));
			}
			else
			{
				for(Entry<Integer, Word> x: existingngram.ngram.entrySet())
				{
					if(x.getValue().type == 2)
					{
						x.getValue().addValue(ngram.ngram.get(x.getKey()).words.get(0));
					}
				}
				
				ngrams.put(existingngram, ngrams.get(existingngram) + 1);
			}

			ngramCount++;
		}
	}

	public int getNgramCount() {
		return ngramCount;
	}

	public Map<String, Integer> getNGramMap() {
		Map<String, Integer> result = new HashMap<String, Integer>();
		
		for(Entry<NGram, Integer> x : ngrams.entrySet())
		{ 
			result.put(x.getKey().toString(), x.getValue() + 1);
		}
		
		return result;
	}
}