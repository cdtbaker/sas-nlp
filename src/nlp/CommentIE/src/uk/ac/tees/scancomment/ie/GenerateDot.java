package uk.ac.tees.scancomment.ie;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class GenerateDot {

	public static void main(String[] arg) throws IOException {
		
		Set<String> variableNames = new HashSet<String>();
		variableNames.add("x");
		variableNames.add("y");
		variableNames.add("foo");
		variableNames.add("bar");
		
		Machine[] machines = new Machine[] {
			new IntRangeMachine(variableNames),
		};
		
		for (Machine machine : machines) {
			FileWriter writer = new FileWriter(machine.getClass().getSimpleName() + ".gv");
			writer.write(machine.toDigraph());
			writer.close();
		}
		
	}
}
