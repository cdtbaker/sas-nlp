package uk.ac.tees.spanlp.classification;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringEscapeUtils;

public class RegexCommentExtractor {

	
	
	public static void main(String[] arg) throws IOException {
	
		Pattern pattern = Pattern.compile("<comment line=\"(\\d+?)\">(.+?)</comment>", Pattern.DOTALL);
		
		File root = new File(arg[0]);
		for (File child : root.listFiles()) {
			BufferedWriter writer = new BufferedWriter(new FileWriter(child.getName() + ".comments"));
			if (child.isDirectory()) {
				for (File file : recursivelyList(child)) {
					
					writer.write(file.getAbsolutePath().substring(root.getAbsolutePath().length() + 1));
					writer.newLine();
					
					StringBuilder builder = new StringBuilder();
					BufferedReader reader = new BufferedReader(new FileReader(file));
					while (reader.ready()) {
						builder.append(reader.readLine().trim()).append(" ");
					}
					reader.close();
					
					Matcher matcher = pattern.matcher(builder.toString());
					while (matcher.find()) {
						
						String lineNumber = matcher.group(1);
						String comment = matcher.group(2).trim();
						
						if (!comment.replaceAll("<br/>", "").trim().isEmpty()) {
							writer.write(lineNumber);
							writer.write("\t");
							writer.write(StringEscapeUtils.unescapeXml(comment));
							writer.newLine();
						}
					}
					
					writer.newLine();
				
				}

			}
			writer.close();
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

}
