package uk.ac.tees.scancomment.ie;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class Test {

	public static void main(String[] arg) throws IOException {
		
		Set<String> variableNames = new HashSet<String>();
		variableNames.add("x");
		variableNames.add("y");
		variableNames.add("foo");
		variableNames.add("bar");
		
		Machine intRange = new IntRangeMachine(variableNames);
		
		FileWriter writer = new FileWriter("test.gv");
		writer.write(intRange.toDigraph());
		writer.close();

		System.out.println(intRange.recognise("x is not equal to 0"));

		System.out.println(intRange.recognise("foo is greater than 2.22"));

		System.out.println(intRange.recognise("x is probably 0"));

	}
}
