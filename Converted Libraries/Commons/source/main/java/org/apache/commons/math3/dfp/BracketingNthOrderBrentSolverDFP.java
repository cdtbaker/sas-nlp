package org.apache.commons.math3.dfp;
import org.apache.commons.math3.analysis.solvers.AllowedSolution;
import org.apache.commons.math3.exception.MathInternalError;
import org.apache.commons.math3.exception.NoBracketingException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.NumberIsTooSmallException;
import org.apache.commons.math3.util.Incrementor;
import org.apache.commons.math3.util.MathUtils;
/** 
 * This class implements a modification of the <a
 * href="http://mathworld.wolfram.com/BrentsMethod.html"> Brent algorithm</a>.
 * <p>
 * The changes with respect to the original Brent algorithm are:
 * <ul>
 * <li>the returned value is chosen in the current interval according
 * to user specified {@link AllowedSolution},</li>
 * <li>the maximal order for the invert polynomial root search is
 * user-specified instead of being invert quadratic only</li>
 * </ul>
 * </p>
 * The given interval must bracket the root.
 * @version $Id: BracketingNthOrderBrentSolverDFP.java 1416643 2012-12-03 19:37:14Z tn $
 */
public class BracketingNthOrderBrentSolverDFP {
  /** 
 * Maximal aging triggering an attempt to balance the bracketing interval. 
 */
  private static final int MAXIMAL_AGING=2;
  /** 
 * Maximal order. 
 */
  private final int maximalOrder;
  /** 
 * Function value accuracy. 
 */
  private final Dfp functionValueAccuracy;
  /** 
 * Absolute accuracy. 
 */
  private final Dfp absoluteAccuracy;
  /** 
 * Relative accuracy. 
 */
  private final Dfp relativeAccuracy;
  /** 
 * Evaluations counter. 
 */
  private final Incrementor evaluations=new Incrementor();
  /** 
 * Construct a solver.
 * @param relativeAccuracy Relative accuracy.
 * @param absoluteAccuracy Absolute accuracy.
 * @param functionValueAccuracy Function value accuracy.
 * @param maximalOrder maximal order.
 * @exception NumberIsTooSmallException if maximal order is lower than 2
 */
  public BracketingNthOrderBrentSolverDFP(  final Dfp relativeAccuracy,  final Dfp absoluteAccuracy,  final Dfp functionValueAccuracy,  final int maximalOrder) throws NumberIsTooSmallException {
    if (maximalOrder < 2) {
      throw new NumberIsTooSmallException(maximalOrder,2,true);
    }
    this.maximalOrder=maximalOrder;
    this.absoluteAccuracy=absoluteAccuracy;
    this.relativeAccuracy=relativeAccuracy;
    this.functionValueAccuracy=functionValueAccuracy;
  }
  /** 
 * Get the maximal order.
 * @return maximal order
 */
  public int getMaximalOrder(){
    return maximalOrder;
  }
  /** 
 * Get the maximal number of function evaluations.
 * @return the maximal number of function evaluations.
 */
  public int getMaxEvaluations(){
    return evaluations.getMaximalCount();
  }
  /** 
 * Get the number of evaluations of the objective function.
 * The number of evaluations corresponds to the last call to the{@code optimize} method. It is 0 if the method has not been
 * called yet.
 * @return the number of evaluations of the objective function.
 */
  public int getEvaluations(){
    return evaluations.getCount();
  }
  /** 
 * Get the absolute accuracy.
 * @return absolute accuracy
 */
  public Dfp getAbsoluteAccuracy(){
    return absoluteAccuracy;
  }
  /** 
 * Get the relative accuracy.
 * @return relative accuracy
 */
  public Dfp getRelativeAccuracy(){
    return relativeAccuracy;
  }
  /** 
 * Get the function accuracy.
 * @return function accuracy
 */
  public Dfp getFunctionValueAccuracy(){
    return functionValueAccuracy;
  }
  /** 
 * Solve for a zero in the given interval.
 * A solver may require that the interval brackets a single zero root.
 * Solvers that do require bracketing should be able to handle the case
 * where one of the endpoints is itself a root.
 * @param maxEval Maximum number of evaluations.
 * @param f Function to solve.
 * @param min Lower bound for the interval.
 * @param max Upper bound for the interval.
 * @param allowedSolution The kind of solutions that the root-finding algorithm may
 * accept as solutions.
 * @return a value where the function is zero.
 * @exception NullArgumentException if f is null.
 * @exception NoBracketingException if root cannot be bracketed
 */
  public Dfp solve(  final int maxEval,  final UnivariateDfpFunction f,  final Dfp min,  final Dfp max,  final AllowedSolution allowedSolution) throws NullArgumentException, NoBracketingException {
    return solve(maxEval,f,min,max,min.add(max).divide(2),allowedSolution);
  }
  /** 
 * Solve for a zero in the given interval, start at {@code startValue}.
 * A solver may require that the interval brackets a single zero root.
 * Solvers that do require bracketing should be able to handle the case
 * where one of the endpoints is itself a root.
 * @param maxEval Maximum number of evaluations.
 * @param f Function to solve.
 * @param min Lower bound for the interval.
 * @param max Upper bound for the interval.
 * @param startValue Start value to use.
 * @param allowedSolution The kind of solutions that the root-finding algorithm may
 * accept as solutions.
 * @return a value where the function is zero.
 * @exception NullArgumentException if f is null.
 * @exception NoBracketingException if root cannot be bracketed
 */
  public Dfp solve(  final int maxEval,  final UnivariateDfpFunction f,  final Dfp min,  final Dfp max,  final Dfp startValue,  final AllowedSolution allowedSolution) throws NullArgumentException, NoBracketingException {
    MathUtils.checkNotNull(f);
    evaluations.setMaximalCount(maxEval);
    evaluations.resetCount();
    Dfp zero=startValue.getZero();
    Dfp nan=zero.newInstance((byte)1,Dfp.QNAN);
    final Dfp[] x=new Dfp[maximalOrder + 1];
    final Dfp[] y=new Dfp[maximalOrder + 1];
    x[0]=min;
    x[1]=startValue;
    x[2]=max;
    evaluations.incrementCount();
    y[1]=f.value(x[1]);
    if (y[1].isZero()) {
      return x[1];
    }
    evaluations.incrementCount();
    y[0]=f.value(x[0]);
    if (y[0].isZero()) {
      return x[0];
    }
    int nbPoints;
    int signChangeIndex;
    if (y[0].multiply(y[1]).negativeOrNull()) {
      nbPoints=2;
      signChangeIndex=1;
    }
 else {
      evaluations.incrementCount();
      y[2]=f.value(x[2]);
      if (y[2].isZero()) {
        return x[2];
      }
      if (y[1].multiply(y[2]).negativeOrNull()) {
        nbPoints=3;
        signChangeIndex=2;
      }
 else {
        throw new NoBracketingException(x[0].toDouble(),x[2].toDouble(),y[0].toDouble(),y[2].toDouble());
      }
    }
    final Dfp[] tmpX=new Dfp[x.length];
    Dfp xA=x[signChangeIndex - 1];
    Dfp yA=y[signChangeIndex - 1];
    Dfp absXA=xA.abs();
    Dfp absYA=yA.abs();
    int agingA=0;
    Dfp xB=x[signChangeIndex];
    Dfp yB=y[signChangeIndex];
    Dfp absXB=xB.abs();
    Dfp absYB=yB.abs();
    int agingB=0;
    while (true) {
      Dfp maxX=absXA.lessThan(absXB) ? absXB : absXA;
      Dfp maxY=absYA.lessThan(absYB) ? absYB : absYA;
      final Dfp xTol=absoluteAccuracy.add(relativeAccuracy.multiply(maxX));
      if (xB.subtract(xA).subtract(xTol).negativeOrNull() || maxY.lessThan(functionValueAccuracy)) {
switch (allowedSolution) {
case ANY_SIDE:
          return absYA.lessThan(absYB) ? xA : xB;
case LEFT_SIDE:
        return xA;
case RIGHT_SIDE:
      return xB;
case BELOW_SIDE:
    return yA.lessThan(zero) ? xA : xB;
case ABOVE_SIDE:
  return yA.lessThan(zero) ? xB : xA;
default :
throw new MathInternalError(null);
}
}
Dfp targetY;
if (agingA >= MAXIMAL_AGING) {
targetY=yB.divide(16).negate();
}
 else if (agingB >= MAXIMAL_AGING) {
targetY=yA.divide(16).negate();
}
 else {
targetY=zero;
}
Dfp nextX;
int start=0;
int end=nbPoints;
do {
System.arraycopy(x,start,tmpX,start,end - start);
nextX=guessX(targetY,tmpX,y,start,end);
if (!(nextX.greaterThan(xA) && nextX.lessThan(xB))) {
if (signChangeIndex - start >= end - signChangeIndex) {
++start;
}
 else {
--end;
}
nextX=nan;
}
}
 while (nextX.isNaN() && (end - start > 1));
if (nextX.isNaN()) {
nextX=xA.add(xB.subtract(xA).divide(2));
start=signChangeIndex - 1;
end=signChangeIndex;
}
evaluations.incrementCount();
final Dfp nextY=f.value(nextX);
if (nextY.isZero()) {
return nextX;
}
if ((nbPoints > 2) && (end - start != nbPoints)) {
nbPoints=end - start;
System.arraycopy(x,start,x,0,nbPoints);
System.arraycopy(y,start,y,0,nbPoints);
signChangeIndex-=start;
}
 else if (nbPoints == x.length) {
nbPoints--;
if (signChangeIndex >= (x.length + 1) / 2) {
System.arraycopy(x,1,x,0,nbPoints);
System.arraycopy(y,1,y,0,nbPoints);
--signChangeIndex;
}
}
System.arraycopy(x,signChangeIndex,x,signChangeIndex + 1,nbPoints - signChangeIndex);
x[signChangeIndex]=nextX;
System.arraycopy(y,signChangeIndex,y,signChangeIndex + 1,nbPoints - signChangeIndex);
y[signChangeIndex]=nextY;
++nbPoints;
if (nextY.multiply(yA).negativeOrNull()) {
xB=nextX;
yB=nextY;
absYB=yB.abs();
++agingA;
agingB=0;
}
 else {
xA=nextX;
yA=nextY;
absYA=yA.abs();
agingA=0;
++agingB;
signChangeIndex++;
}
}
}
/** 
 * Guess an x value by n<sup>th</sup> order inverse polynomial interpolation.
 * <p>
 * The x value is guessed by evaluating polynomial Q(y) at y = targetY, where Q
 * is built such that for all considered points (x<sub>i</sub>, y<sub>i</sub>),
 * Q(y<sub>i</sub>) = x<sub>i</sub>.
 * </p>
 * @param targetY target value for y
 * @param x reference points abscissas for interpolation,
 * note that this array <em>is</em> modified during computation
 * @param y reference points ordinates for interpolation
 * @param start start index of the points to consider (inclusive)
 * @param end end index of the points to consider (exclusive)
 * @return guessed root (will be a NaN if two points share the same y)
 */
private Dfp guessX(final Dfp targetY,final Dfp[] x,final Dfp[] y,final int start,final int end){
for (int i=start; i < end - 1; ++i) {
final int delta=i + 1 - start;
for (int j=end - 1; j > i; --j) {
x[j]=x[j].subtract(x[j - 1]).divide(y[j].subtract(y[j - delta]));
}
}
Dfp x0=targetY.getZero();
for (int j=end - 1; j >= start; --j) {
x0=x[j].add(x0.multiply(targetY.subtract(y[j])));
}
return x0;
}
}
