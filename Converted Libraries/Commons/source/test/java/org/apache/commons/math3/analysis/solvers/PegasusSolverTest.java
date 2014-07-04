package org.apache.commons.math3.analysis.solvers;
/** 
 * Test case for {@link PegasusSolver Pegasus} solver.
 * @version $Id$
 */
public final class PegasusSolverTest extends BaseSecantSolverAbstractTest {
  /** 
 * {@inheritDoc} 
 */
  @Override protected UnivariateSolver getSolver(){
    return new PegasusSolver();
  }
  /** 
 * {@inheritDoc} 
 */
  @Override protected int[] getQuinticEvalCounts(){
    return new int[]{3,7,9,8,9,8,10,10,12,16,18};
  }
}
