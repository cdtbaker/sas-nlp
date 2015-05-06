package uk.ac.tees.spanlp.classification;

import java.io.File;
import java.util.List;

import uk.ac.tees.spanlp.classification.CommentReader.Error;


public class CommentCounter {

	
	public static void main(String[] arg) {
		File root = new File(arg[0]);
		for (File child : root.listFiles()) {
			if (child.isDirectory()) {
				
				CommentReader commentReader = new CommentReader(child);
				int comments = 0;
				
				List<String> comment = null;
				while ((comment = commentReader.next()) != null) {
					comments++;
				}
				
				System.out.println(String.format("%s : found %d comments", child.getName(), comments));
				
				if (commentReader.getErrors().size() > 0) {
					System.out.println("Errors:");
					for (Error error : commentReader.getErrors()) {
						System.out.println(String.format("\t%s : %s", error.file.getAbsolutePath(), error.exception.getMessage()));
					}
				}
			}
		}
	}
}
