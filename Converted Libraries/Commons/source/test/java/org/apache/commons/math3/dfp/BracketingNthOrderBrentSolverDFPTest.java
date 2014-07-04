package org.apache.commons.math3.dfp;
import org.apache.commons.math3.analysis.solvers.AllowedSolution;
import org.apache.commons.math3.exception.MathInternalError;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
/** 
 * Test case for {@link BracketingNthOrderBrentSolverDFP bracketing n<sup>th</sup> order Brent} solver.
 * @version $Id: BracketingNthOrderBrentSolverDFPTest.java 1244107 2012-02-14 16:17:55Z erans $
 */
public final class BracketingNthOrderBrentSolverDFPTest {
  @Test(expected=NumberIsTooSmallException.class) public void testInsufficientOrder3(){
    new BracketingNthOrderBrentSolverDFP(relativeAccuracy,absoluteAccuracy,functionValueAccuracy,1);
  }
  @Test public void testConstructorOK(){
    BracketingNthOrderBrentSolverDFP solver=new BracketingNthOrderBrentSolverDFP(relativeAccuracy,absoluteAccuracy,functionValueAccuracy,2);
    Assert.assertEquals(2,solver.getMaximalOrder());
  }
  @Test public void testConvergenceOnFunctionAccuracy(){
    BracketingNthOrderBrentSolverDFP solver=new BracketingNthOrderBrentSolverDFP(relativeAccuracy,absoluteAccuracy,field.newDfp(1.0e-20),20);
    UnivariateDfpFunction f=new UnivariateDfpFunction(){
      public Dfp value(      Dfp x){
        Dfp one=field.getOne();
        Dfp oneHalf=one.divide(2);
        Dfp xMo=x.subtract(one);
        Dfp xMh=x.subtract(oneHalf);
        Dfp xPh=x.add(oneHalf);
        Dfp xPo=x.add(one);
        return xMo.multiply(xMh).multiply(x).multiply(xPh).multiply(xPo);
      }
    }
;
    Dfp result=solver.solve(20,f,field.newDfp(0.2),field.newDfp(0.9),field.newDfp(0.4),AllowedSolution.BELOW_SIDE);
    Assert.assertTrue(f.value(result).abs().lessThan(solver.getFunctionValueAccuracy()));
    Assert.assertTrue(f.value(result).negativeOrNull());
    Assert.assertTrue(result.subtract(field.newDfp(0.5)).subtract(solver.getAbsoluteAccuracy()).positiveOrNull());
    result=solver.solve(20,f,field.newDfp(-0.9),field.newDfp(-0.2),field.newDfp(-0.4),AllowedSolution.ABOVE_SIDE);
    Assert.assertTrue(f.value(result).abs().lessThan(solver.getFunctionValueAccuracy()));
    Assert.assertTrue(f.value(result).positiveOrNull());
    Assert.assertTrue(result.add(field.newDfp(0.5)).subtract(solver.getAbsoluteAccuracy()).negativeOrNull());
  }
  @Test public void testNeta(){
    for (    AllowedSolution allowed : AllowedSolution.values()) {
      check(new UnivariateDfpFunction(){
        public Dfp value(        Dfp x){
          return DfpMath.sin(x).subtract(x.divide(2));
        }
      }
,200,-2.0,2.0,allowed);
      check(new UnivariateDfpFunction(){
        public Dfp value(        Dfp x){
          return DfpMath.pow(x,5).add(x).subtract(field.newDfp(10000));
        }
      }
,200,-5.0,10.0,allowed);
      check(new UnivariateDfpFunction(){
        public Dfp value(        Dfp x){
          return x.sqrt().subtract(field.getOne().divide(x)).subtract(field.newDfp(3));
        }
      }
,200,0.001,10.0,allowed);
      check(new UnivariateDfpFunction(){
        public Dfp value(        Dfp x){
          return DfpMath.exp(x).add(x).subtract(field.newDfp(20));
        }
      }
,200,-5.0,5.0,allowed);
      check(new UnivariateDfpFunction(){
        public Dfp value(        Dfp x){
          return DfpMath.log(x).add(x.sqrt()).subtract(field.newDfp(5));
        }
      }
,200,0.001,10.0,allowed);
      check(new UnivariateDfpFunction(){
        public Dfp value(        Dfp x){
          return x.subtract(field.getOne()).multiply(x).multiply(x).subtract(field.getOne());
        }
      }
,200,-0.5,1.5,allowed);
    }
  }
  private void check(  UnivariateDfpFunction f,  int maxEval,  double min,  double max,  AllowedSolution allowedSolution){
    BracketingNthOrderBrentSolverDFP solver=new BracketingNthOrderBrentSolverDFP(relativeAccuracy,absoluteAccuracy,functionValueAccuracy,20);
    Dfp xResult=solver.solve(maxEval,f,field.newDfp(min),field.newDfp(max),allowedSolution);
    Dfp yResult=f.value(xResult);
switch (allowedSolution) {
case ANY_SIDE:
      Assert.assertTrue(yResult.abs().lessThan(functionValueAccuracy.multiply(2)));
    break;
case LEFT_SIDE:
{
    boolean increasing=f.value(xResult).add(absoluteAccuracy).greaterThan(yResult);
    Assert.assertTrue(increasing ? yResult.negativeOrNull() : yResult.positiveOrNull());
    break;
  }
case RIGHT_SIDE:
{
  boolean increasing=f.value(xResult).add(absoluteAccuracy).greaterThan(yResult);
  Assert.assertTrue(increasing ? yResult.positiveOrNull() : yResult.negativeOrNull());
  break;
}
case BELOW_SIDE:
Assert.assertTrue(yResult.negativeOrNull());
break;
case ABOVE_SIDE:
Assert.assertTrue(yResult.positiveOrNull());
break;
default :
throw new MathInternalError(null);
}
}
@Before public void setUp(){
field=new DfpField(50);
absoluteAccuracy=field.newDfp(1.0e-45);
relativeAccuracy=field.newDfp(1.0e-45);
functionValueAccuracy=field.newDfp(1.0e-45);
}
private DfpField field;
private Dfp absoluteAccuracy;
private Dfp relativeAccuracy;
private Dfp functionValueAccuracy;
}
