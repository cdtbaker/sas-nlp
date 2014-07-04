package org.apache.commons.math3.optimization.general;
/** 
 * Available choices of update formulas for the &beta; parameter
 * in {@link NonLinearConjugateGradientOptimizer}.
 * <p>
 * The &beta; parameter is used to compute the successive conjugate
 * search directions. For non-linear conjugate gradients, there are
 * two formulas to compute &beta;:
 * <ul>
 * <li>Fletcher-Reeves formula</li>
 * <li>Polak-Ribi&egrave;re formula</li>
 * </ul>
 * On the one hand, the Fletcher-Reeves formula is guaranteed to converge
 * if the start point is close enough of the optimum whether the
 * Polak-Ribi&egrave;re formula may not converge in rare cases. On the
 * other hand, the Polak-Ribi&egrave;re formula is often faster when it
 * does converge. Polak-Ribi&egrave;re is often used.
 * <p>
 * @see NonLinearConjugateGradientOptimizer
 * @version $Id: ConjugateGradientFormula.java 1422230 2012-12-15 12:11:13Z erans $
 * @deprecated As of 3.1 (to be removed in 4.0).
 * @since 2.0
 */
@Deprecated public enum ConjugateGradientFormula {/** 
 * Fletcher-Reeves formula. 
 */
FLETCHER_REEVES, /** 
 * Polak-Ribi&egrave;re formula. 
 */
POLAK_RIBIERE}
