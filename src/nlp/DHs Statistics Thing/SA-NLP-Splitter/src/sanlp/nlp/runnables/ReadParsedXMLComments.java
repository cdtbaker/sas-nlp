package sanlp.nlp.runnables;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import sanlp.nlp.core.CommentGlobalRecord;
import sanlp.nlp.core.PrintFiles;

public class ReadParsedXMLComments {
	
	public static void main(String ... args) throws IOException
	{
		System.out.println(readCommentCorpus("/Users/danhowden/2014-sas-nlp/public/XML Parser/Converted Libraries/Commons/main/java/org/apache/commons/math3"));
	}
	
	public static String readCommentCorpus(String fromLibraryPath) throws IOException
	{
		Path path = Paths.get(fromLibraryPath);
		
		PrintFiles pf = new PrintFiles();
		Files.walkFileTree(path, pf);
		
		return CommentGlobalRecord.generate();
	}
}
