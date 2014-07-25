package org.apache.commons.math3.analysis.solvers;
import org.apache.commons.math3.analysis.DifferentiableUnivariateFunction;
/** 
 * Interface for (univariate real) rootfinding algorithms.
 * Implementations will search for only one zero in the given interval.
 * @version $Id: DifferentiableUnivariateSolver.java 1377245 2012-08-25 10:06:00Z luc $
 * @deprecated as of 3.1, replaced by {@link UnivariateDifferentiableSolver}
 */
public interface DifferentiableUnivariateSolver extends BaseUnivariateSolver<DifferentiableUnivariateFunction> {
}
