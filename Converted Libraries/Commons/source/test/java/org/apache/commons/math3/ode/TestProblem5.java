package org.apache.commons.math3.ode;
/** 
 * This class is used in the junit tests for the ODE integrators.
 * <p>This is the same as problem 1 except integration is done
 * backward in time</p>
 */
public class TestProblem5 extends TestProblem1 {
  /** 
 * Simple constructor.
 */
  public TestProblem5(){
    super();
    setFinalConditions(2 * t0 - t1);
  }
  /** 
 * {@inheritDoc} 
 */
  @Override public TestProblem5 copy(){
    return new TestProblem5();
  }
}
