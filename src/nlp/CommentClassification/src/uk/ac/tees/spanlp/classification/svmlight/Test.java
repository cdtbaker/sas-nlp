package uk.ac.tees.spanlp.classification.svmlight;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import uk.ac.tees.spanlp.classification.svmlight.FeatureVector.Target;

public class Test {

	public static void main(String[] arg) throws Exception {
		
		FeatureIndex featureIndex = new FeatureIndex();
		List<FeatureVector> training = buildDataset(new File("Comments/800Samples"), featureIndex);
		List<FeatureVector> testing = buildDataset(new File("Comments/200Samples"), featureIndex);
		
		featureIndex.commit(new File("features.txt"));
		writeDataset(training, new File("training.txt"));
		writeDataset(testing, new File("testing.txt"));
		
	}
	
	private static List<FeatureVector> buildDataset(File root, FeatureIndex featureIndex) throws IOException {
		List<FeatureVector> vectors = new LinkedList<FeatureVector>();
		vectors.addAll(buildVectors(Target.POSITIVE, new File(root.getAbsolutePath() + "/pos"), featureIndex));
		vectors.addAll(buildVectors(Target.NEGATIVE, new File(root.getAbsolutePath() + "/neg"), featureIndex));
		return vectors;
	}
	
	private static List<FeatureVector> buildVectors(Target target, File subdir, FeatureIndex featureIndex) throws IOException {
		List<FeatureVector> vectors = new LinkedList<FeatureVector>();
		for (File child : subdir.listFiles()) {
			if (!child.getName().startsWith(".") && child.getName().endsWith(".txt")) {
				System.out.println(child.getAbsolutePath());
				
				List<String> features = new LinkedList<String>();
				BufferedReader reader = new BufferedReader(new FileReader(child));
				while (reader.ready()) {
					for (String feature : reader.readLine().split(" ")) {
						// todo: ignore certain features like punctuation etc. here
						features.add(feature.toLowerCase());
					}
				}
				reader.close();
				
				vectors.add(new FeatureVector(target, features, featureIndex));
				
			}
		}	
		return vectors;
	}
	
	private static void writeDataset(List<FeatureVector> vectors, File outputFile) throws IOException {
		BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
		for (FeatureVector vector : vectors) {
			writer.write(vector.toString());
			writer.newLine();
		}
		writer.close();
	}
}
