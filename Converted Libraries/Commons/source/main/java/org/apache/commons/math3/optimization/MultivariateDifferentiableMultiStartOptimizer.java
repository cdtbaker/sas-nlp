package org.apache.commons.math3.optimization;
import org.apache.commons.math3.analysis.differentiation.MultivariateDifferentiableFunction;
import org.apache.commons.math3.random.RandomVectorGenerator;
/** 
 * Special implementation of the {@link MultivariateDifferentiableOptimizer}interface adding multi-start features to an existing optimizer.
 * This class wraps a classical optimizer to use it several times in
 * turn with different starting points in order to avoid being trapped
 * into a local extremum when looking for a global one.
 * @version $Id: MultivariateDifferentiableMultiStartOptimizer.java 1422230 2012-12-15 12:11:13Z erans $
 * @deprecated As of 3.1 (to be removed in 4.0).
 * @since 3.1
 */
@Deprecated public class MultivariateDifferentiableMultiStartOptimizer extends BaseMultivariateMultiStartOptimizer<MultivariateDifferentiableFunction> implements MultivariateDifferentiableOptimizer {
  /** 
 * Create a multi-start optimizer from a single-start optimizer.
 * @param optimizer Single-start optimizer to wrap.
 * @param starts Number of starts to perform (including the
 * first one), multi-start is disabled if value is less than or
 * equal to 1.
 * @param generator Random vector generator to use for restarts.
 */
  public MultivariateDifferentiableMultiStartOptimizer(  final MultivariateDifferentiableOptimizer optimizer,  final int starts,  final RandomVectorGenerator generator){
    super(optimizer,starts,generator);
  }
}
