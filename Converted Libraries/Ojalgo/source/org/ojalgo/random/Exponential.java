package org.ojalgo.random;
import static org.ojalgo.constant.PrimitiveMath.*;
/** 
 * Distribution of length of life when no aging.
 * Describes the time between events in a Poisson process, i.e. a process in
 * which events occur continuously and independently at a constant average rate.
 * It is the continuous analogue of the geometric distribution.
 * @author apete
 */
public class Exponential extends AbstractContinuous {
  private static final long serialVersionUID=-720007692511649669L;
  private final double myRate;
  public Exponential(){
    this(ONE);
  }
  public Exponential(  final double aRate){
    super();
    myRate=aRate;
  }
  public double getDistribution(  final double aValue){
    if (aValue < ZERO) {
      return ZERO;
    }
 else {
      return ONE - Math.exp(-myRate * aValue);
    }
  }
  public double getExpected(){
    return ONE / myRate;
  }
  public double getProbability(  final double aValue){
    if (aValue < ZERO) {
      return ZERO;
    }
 else {
      return myRate * Math.exp(-myRate * aValue);
    }
  }
  public double getQuantile(  final double aProbality){
    this.checkProbabilty(aProbality);
    return Math.log(ONE - aProbality) / -myRate;
  }
  @Override public double getStandardDeviation(){
    return ONE / myRate;
  }
  @Override protected double generate(){
    return -Math.log(this.random().nextDouble()) / myRate;
  }
}
