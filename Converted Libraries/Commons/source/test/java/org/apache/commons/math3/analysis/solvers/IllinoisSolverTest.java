package org.apache.commons.math3.analysis.solvers;
/** 
 * Test case for {@link IllinoisSolver Illinois} solver.
 * @version $Id$
 */
public final class IllinoisSolverTest extends BaseSecantSolverAbstractTest {
  /** 
 * {@inheritDoc} 
 */
  @Override protected UnivariateSolver getSolver(){
    return new IllinoisSolver();
  }
  /** 
 * {@inheritDoc} 
 */
  @Override protected int[] getQuinticEvalCounts(){
    return new int[]{3,7,9,10,10,10,12,12,14,15,20};
  }
}
