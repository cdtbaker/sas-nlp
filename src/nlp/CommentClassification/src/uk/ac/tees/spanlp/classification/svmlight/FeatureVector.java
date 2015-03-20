package uk.ac.tees.spanlp.classification.svmlight;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class FeatureVector {
	
	private final Target target;
	private final Map<Integer,Integer> elements;
	
	public FeatureVector(Target target, List<String> features, FeatureIndex featureIndex) {
		this.target = target;
		this.elements = new HashMap<Integer,Integer>();
		for (String feature : features) {
			Integer index = featureIndex.indexOf(feature);
			Integer oldValue = elements.get(index);
			if (oldValue == null) oldValue = 0;
			elements.put(index, oldValue + 1);
		}
	}
	
	public String toString() {
		
		List<Entry<Integer,Integer>> sortedElements = new LinkedList<Entry<Integer,Integer>>(elements.entrySet());
		Collections.sort(sortedElements, new Comparator<Entry<Integer,Integer>>() {
			@Override
			public int compare(Entry<Integer, Integer> a, Entry<Integer, Integer> b) {
				return a.getKey().compareTo(b.getKey());
			}
		});
		
		StringBuilder builder = new StringBuilder();
		
		builder.append(target.toString());
		
		for (Entry<Integer,Integer> element : sortedElements) {
			builder.append(" ");
			builder.append(element.getKey().toString());
			builder.append(":");
			builder.append(element.getValue().toString());
		}
		
		return builder.toString();
	}
	
	public enum Target {
		POSITIVE("+1"), NEGATIVE("-1");
		
		private String label;
		
		Target(String label) {
			this.label = label;
		}
		
		public String toString() {
			return label;
		}
	}

}
