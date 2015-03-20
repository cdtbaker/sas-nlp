package uk.ac.tees.spanlp.classification.svmlight;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class FeatureIndex {

	private final Map<String,Integer> indices;
	private final Map<Integer,String> features;
	
	public FeatureIndex() {
		indices = new HashMap<String,Integer>();
		features = new HashMap<Integer,String>();
	}
	
	public Integer indexOf(String feature) {
	
		Integer index = indices.get(feature);
		if (index == null) {
			index = indices.size() + 1;
			indices.put(feature, index);
			features.put(index, feature);
		}
	
		return index;
		
	}
	
	public String featureAt(Integer index) {
		return features.get(index);
	}
	
	public void commit(File file) throws IOException {
		
		List<Entry<Integer,String>> entries = new LinkedList<Entry<Integer,String>>(features.entrySet());
		Collections.sort(entries, new Comparator<Entry<Integer,String>>() {
			@Override
			public int compare(Entry<Integer, String> a, Entry<Integer, String> b) {
				return a.getKey().compareTo(b.getKey());
			}
		});
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		for (Entry<Integer,String> entry : entries) {
			writer.write(entry.getKey().toString());
			writer.write("\t");
			writer.write(entry.getValue().toString());
			writer.newLine();
		}
		writer.close();
		
	}
}
