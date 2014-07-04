package tools.standalonexml;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import main.commentextraction.com.jml.builder.XMLFromSource;
import main.commentextraction.com.jml.output.XMLOutputter;


public class StandaloneXmlConverter {

	public static void main (String[] args){
		String path = args[0];
		for(File f : getJavaFiles(path)){
			XMLOutputter output = new XMLOutputter(XMLFromSource.getXMLFromFile(f.getAbsolutePath(), true));
			try {
				output.toFile(path+"/"+f.getName().substring(0, f.getName().indexOf('.')));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static List<File> getJavaFiles(String path) {
		return getJavaFiles(path, new ArrayList<File>());

	}

	public static List<File> getJavaFiles(String path, List<File> javaFiles) {
		File dir = new File(path);

		File[] files = dir.listFiles();

		for (File f : files) {
			if (f.isFile() && f.getName().endsWith(".java")) {
				javaFiles.add(f);
			} else if (f.isDirectory()) {
				getJavaFiles(f.getAbsolutePath(), javaFiles);
			}
		}

		return javaFiles;

	}

}
