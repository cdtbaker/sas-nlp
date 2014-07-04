package org.ojalgo.random;
import static org.ojalgo.constant.PrimitiveMath.*;
/** 
 * The number of required trials until an event with probability aProbability
 * occurs has a geometric distribution.
 * @author apete
 */
public class Geometric extends AbstractDiscrete {
  private static final long serialVersionUID=1324905651790774444L;
  private final double myProbability;
  public Geometric(){
    this(HALF);
  }
  public Geometric(  final double aProbability){
    super();
    myProbability=aProbability;
  }
  public double getExpected(){
    return ONE / myProbability;
  }
  public double getProbability(  final int aVal){
    return myProbability * Math.pow(ONE - myProbability,aVal - ONE);
  }
  @Override public double getVariance(){
    return (ONE - myProbability) / (myProbability * myProbability);
  }
  @Override protected double generate(){
    int retVal=1;
    while ((this.random().nextDouble() + myProbability) <= ONE) {
      retVal++;
    }
    return retVal;
  }
}
