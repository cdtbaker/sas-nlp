package org.apache.commons.math3.analysis.solvers;
import org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunction;
/** 
 * Interface for (univariate real) rootfinding algorithms.
 * Implementations will search for only one zero in the given interval.
 * @since 3.1
 * @version $Id: UnivariateDifferentiableSolver.java 1383441 2012-09-11 14:56:39Z luc $
 */
public interface UnivariateDifferentiableSolver extends BaseUnivariateSolver<UnivariateDifferentiableFunction> {
}
