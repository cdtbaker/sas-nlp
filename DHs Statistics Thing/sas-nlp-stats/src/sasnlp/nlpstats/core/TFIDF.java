package sasnlp.nlpstats.core;

public class TFIDF implements Comparable<TFIDF> {
	private Number numOfOccurrencesInDocument;
	private Number totalTermsInDocument;
	private Number totalDocuments;
	private Number numOfDocumentsWithTerm;

	public TFIDF(Number occ, Number totTerms, Number totDocs,
			Number docsWithTerms) {
		numOfOccurrencesInDocument = occ;
		totalTermsInDocument = totTerms;
		totalDocuments = totDocs;
		numOfDocumentsWithTerm = docsWithTerms;
	}

	/**
	 * Calculates the tf-idf value of the current term. For description of
	 * tf-idf refer to <a href="http://en.wikipedia.org/wiki/Tf??idf">^
	 * wikipedia.org/Tf??idf</a> <br />
	 * Because there can be many cases where the current term is not present in
	 * any other document in the repository, Float.MIN_VALUE is added to the
	 * denominator to avoid DivideByZero exception
	 * 
	 * @return
	 */
	public Float getValue() {

		System.err.println("numOfOccurrences : "
				+ this.numOfOccurrencesInDocument.intValue() + "\n"
				+ "totalTermsInDocument : "
				+ this.totalTermsInDocument.intValue() + "\n"
				+ "numOfDocumentsWithTerm : "
				+ this.numOfDocumentsWithTerm.intValue() + "\n"
				+ "totalDocuments : " + this.totalDocuments.intValue());

		float tf = numOfOccurrencesInDocument.floatValue()
				/ (Float.MIN_VALUE + totalTermsInDocument.floatValue());
		float idf = (float) Math.log10(totalDocuments.floatValue()
				/ (numOfDocumentsWithTerm.floatValue()));
		System.err.println("TF: " + tf);
		System.err.println("IDF: " + idf);

		return (tf * idf);
	}

	public int getNumOfOccurrences() {
		return this.numOfOccurrencesInDocument.intValue();
	}

	public String toString() {
		System.err.println("numOfOccurrences : "
				+ this.numOfOccurrencesInDocument.intValue() + "\n"
				+ "totalTermsInDocument : "
				+ this.totalTermsInDocument.intValue() + "\n"
				+ "numOfDocumentsWithTerm : "
				+ this.numOfDocumentsWithTerm.intValue() + "\n"
				+ "totalDocuments : " + this.totalDocuments.intValue() + "\n"
				+ "TFIDF : " + this.getValue());

		return this.getValue().toString();
	}

	@Override
	public int compareTo(TFIDF o) {
		return (int) ((this.getValue() - o.getValue()) * 100);
	}

}