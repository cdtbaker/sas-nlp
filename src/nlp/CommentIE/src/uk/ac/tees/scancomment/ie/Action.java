package uk.ac.tees.scancomment.ie;

import java.util.Map;

public class Action {
	public final String slot;
	public final String filler;
	
	public Action(String slot) {
		this(slot, null);
	}
	
	public Action(String slot, String filler) {
		this.slot = slot;
		this.filler = filler;
	}
	
	public void apply(String recognised, Map<String,String> frame) {
		if (this.filler.contains("$"))
			frame.put(slot, recognised);
		else
			frame.put(slot, filler);
	}
	
	public String toString() {
		return slot + "=" + filler;
	}
}
