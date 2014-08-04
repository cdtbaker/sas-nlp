package tools.standalonexml;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import main.commentextraction.com.jml.builder.XMLFromSource;
import main.commentextraction.com.jml.output.XMLOutputter;


public class StandaloneXmlConverter {

	public static void main (String[] args){
		String path ="C:/Users/Danny/Desktop/cv";
		
		for(File f : getJavaFiles(path)){
			XMLOutputter output = new XMLOutputter(XMLFromSource.getXMLFromFile(f.getAbsolutePath(), true,true));//change last boolean to false if comments do 
																												 //not have to be in line to be a block
			try {
				String pathO = f.getAbsolutePath().substring(0, f.getAbsolutePath().indexOf('.'));
				pathO = pathO.replace("\\source\\", "\\xml\\");

				output.toFile(pathO);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		System.out.println("Converted");
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
