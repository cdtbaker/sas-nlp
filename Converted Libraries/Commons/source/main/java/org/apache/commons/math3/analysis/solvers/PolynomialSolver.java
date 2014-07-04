package org.apache.commons.math3.analysis.solvers;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
/** 
 * Interface for (polynomial) root-finding algorithms.
 * Implementations will search for only one zero in the given interval.
 * @since 3.0
 * @version $Id: PolynomialSolver.java 1364387 2012-07-22 18:14:11Z tn $
 */
public interface PolynomialSolver extends BaseUnivariateSolver<PolynomialFunction> {
}
