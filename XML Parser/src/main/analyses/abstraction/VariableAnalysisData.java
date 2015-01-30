package main.analyses.abstraction;

/**
 * Data to relate to a variable name for during analysis
 * @author daniel
 *
 * @param <LatticeType>
 */
public class VariableAnalysisData<LatticeType> {

	private final int lineFrom, lineTo;
	private final LatticeType expected;

	public VariableAnalysisData(int lineFrom, int lineTo,LatticeType expected) {
		super();
		this.lineFrom = lineFrom;
		this.lineTo = lineTo;
		this.expected = expected;
	}

	public int getLineFrom() {
		return lineFrom;
	}

	public int getLineTo() {
		return lineTo;
	}

	public LatticeType getExpected() {
		return expected;
	}

}
