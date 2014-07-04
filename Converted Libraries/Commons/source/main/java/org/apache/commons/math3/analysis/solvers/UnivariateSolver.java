package org.apache.commons.math3.analysis.solvers;
import org.apache.commons.math3.analysis.UnivariateFunction;
/** 
 * Interface for (univariate real) root-finding algorithms.
 * Implementations will search for only one zero in the given interval.
 * @version $Id: UnivariateSolver.java 1364387 2012-07-22 18:14:11Z tn $
 */
public interface UnivariateSolver extends BaseUnivariateSolver<UnivariateFunction> {
}
