package main.analyses.simplerangeanalysis;

import java.util.ArrayList;
import java.util.List;

import main.analyses.abstraction.AbstractCommentAnalysis;
import main.analyses.mainanalysis.ScanCommentAnalysis;
import main.analyses.mainanalysis.data.AnalysisType;

public class Start extends ScanCommentAnalysis{
	
	@Override
	protected List<AnalysisType> analysesToUse() {
		List<AnalysisType> l = new ArrayList<AnalysisType>();
		AnalysisType at = new AnalysisType(PositiveNegativeFlowAnalysis.class, null);

		l.add(at);
	
		return l;
	}


}
