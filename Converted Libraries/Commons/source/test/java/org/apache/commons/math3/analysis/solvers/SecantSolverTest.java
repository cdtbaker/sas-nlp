package org.apache.commons.math3.analysis.solvers;
/** 
 * Test case for {@link SecantSolver Secant} solver.
 * @version $Id$
 */
public final class SecantSolverTest extends BaseSecantSolverAbstractTest {
  /** 
 * {@inheritDoc} 
 */
  @Override protected UnivariateSolver getSolver(){
    return new SecantSolver();
  }
  /** 
 * {@inheritDoc} 
 */
  @Override protected int[] getQuinticEvalCounts(){
    return new int[]{3,7,-1,8,9,8,11,12,14,-1,16};
  }
}
