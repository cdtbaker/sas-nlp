package uk.ac.tees.scancomment.ie;

import java.io.FileWriter;
import java.io.IOException;

public class Test {

	public static void main(String[] arg) throws IOException {
		
		Machine intRange = new IntRangeMachine();
		
		FileWriter writer = new FileWriter("test.gv");
		writer.write(intRange.toDigraph());
		writer.close();

		System.out.println(intRange.recognise("x is not equal to 0"));

		System.out.println(intRange.recognise("x is equal to 2"));

		
		System.out.println(intRange.recognise("my name is joao"));

	}
}
