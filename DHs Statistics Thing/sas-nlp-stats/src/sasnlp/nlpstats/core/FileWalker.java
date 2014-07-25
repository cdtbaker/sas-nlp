package sasnlp.nlpstats.core;

import static java.nio.file.FileVisitResult.CONTINUE;

import java.io.File;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

public class FileWalker extends SimpleFileVisitor<Path> {

	public static Document readXMLFile(String path) throws Exception {
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        Document doc = docBuilder.parse (new File(path));
		return doc;
	}
	
	/**
	 * Used to iterate through all files within a directory.
	 */
    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes info) {
    	
    	// TODO add defensive stuff in case this isn't an XML file, though it would be caught
    	// in the exception that is present.
        if (info.isRegularFile()) 
        {
            Document d = null;
			try 
			{
				d = readXMLFile(file.toAbsolutePath().toString());
			} catch (Exception e) 
			{
				System.err.println("File: " + file + " could not be processed, it may not be an XML file.");
			}
			
			// Pass this document to the comment record, so it can extract any and all comments within
			// it and store it in a single string variable for future use.
            CommentGlobalRecord.appendClassComments(d);
        }
        return CONTINUE;
    }
}