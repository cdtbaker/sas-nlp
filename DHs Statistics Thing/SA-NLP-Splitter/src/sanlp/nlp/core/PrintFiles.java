package sanlp.nlp.core;

import static java.nio.file.FileVisitResult.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

public class PrintFiles extends SimpleFileVisitor<Path> {

	public static Document readTextFile(String path) throws Exception {
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        Document doc = docBuilder.parse (new File(path));
		return doc;
	}
    // Print information about
    // each type of file.
    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attr) {
        if (attr.isSymbolicLink()) {
            System.out.format("Symbolic link: %s ", file);
        } else if (attr.isRegularFile()) {
            //System.out.format("Regular file: %s ", file);
            
            Document d = null;
			try {
				d = readTextFile(file.toAbsolutePath().toString());
			} catch (Exception e) {
				System.err.println("File: " + file + " could not be processed.");
			}
            CommentGlobalRecord.appendClassComments(d);
        } else {
            //System.out.format("Other: %s ", file);
        }
       // System.out.println("(" + attr.size() + "bytes)");
        return CONTINUE;
    }

    // Print each directory visited.
    @Override
    public FileVisitResult postVisitDirectory(Path dir,
                                          IOException exc) {
       // System.out.format("Directory: %s%n", dir);
        return CONTINUE;
    }

    // If there is some error accessing
    // the file, let the user know.
    // If you don't override this method
    // and an error occurs, an IOException 
    // is thrown.
    @Override
    public FileVisitResult visitFileFailed(Path file,
                                       IOException exc) {
       // System.err.println(exc);
        return CONTINUE;
    }
}
