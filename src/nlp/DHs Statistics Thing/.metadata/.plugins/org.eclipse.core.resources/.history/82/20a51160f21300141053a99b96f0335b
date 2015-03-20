package sasnlp.nlpstats.core;

/**
 * Calculate the Pointwise-Mutual-Information metric of the given variables.
 *
 */
public class PMI {

	private static double log2(double a) 
	{
		return Math.log(a) / Math.log(2);
	}

	public static double calculate(double probabilityFirst, double probabilitySecond, double probabilityJoint) 
	{
		return log2(probabilityJoint) - (log2(probabilityFirst) + log2(probabilitySecond));
	}
}
