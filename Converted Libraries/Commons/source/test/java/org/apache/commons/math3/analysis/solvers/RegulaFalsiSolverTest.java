package org.apache.commons.math3.analysis.solvers;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.exception.ConvergenceException;
import org.junit.Test;
import org.junit.Assert;
/** 
 * Test case for {@link RegulaFalsiSolver Regula Falsi} solver.
 * @version $Id$
 */
public final class RegulaFalsiSolverTest extends BaseSecantSolverAbstractTest {
  /** 
 * {@inheritDoc} 
 */
  @Override protected UnivariateSolver getSolver(){
    return new RegulaFalsiSolver();
  }
  /** 
 * {@inheritDoc} 
 */
  @Override protected int[] getQuinticEvalCounts(){
    return new int[]{3,7,8,19,18,11,67,55,288,151,-1};
  }
  @Test(expected=ConvergenceException.class) public void testIssue631(){
    final UnivariateFunction f=new UnivariateFunction(){
      /** 
 * {@inheritDoc} 
 */
      public double value(      double x){
        return Math.exp(x) - Math.pow(Math.PI,3.0);
      }
    }
;
    final UnivariateSolver solver=new RegulaFalsiSolver();
    final double root=solver.solve(3624,f,1,10);
    Assert.assertEquals(3.4341896575482003,root,1e-15);
  }
}
