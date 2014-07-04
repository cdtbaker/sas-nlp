package org.ojalgo.random;
import static org.ojalgo.constant.PrimitiveMath.*;
/** 
 * Under general conditions, the sum of a large number of random variables is
 * approximately normally distributed (the central limit theorem).
 * @author apete
 */
public class Normal extends AbstractContinuous {
  private static final long serialVersionUID=7164712313114018919L;
  private final double myLocation;
  private final double myScale;
  public Normal(){
    this(ZERO,ONE);
  }
  public Normal(  final double aLocation,  final double aScale){
    super();
    myLocation=aLocation;
    myScale=aScale;
  }
  public double getDistribution(  final double aValue){
    return (ONE + RandomUtils.erf((aValue - myLocation) / (myScale * SQRT_TWO))) / TWO;
  }
  public double getExpected(){
    return myLocation;
  }
  public double getProbability(  final double aValue){
    final double tmpVal=(aValue - myLocation) / myScale;
    return Math.exp((tmpVal * tmpVal) / -TWO) / (myScale * SQRT_TWO_PI);
  }
  public double getQuantile(  final double aProbality){
    this.checkProbabilty(aProbality);
    return (myScale * SQRT_TWO * RandomUtils.erfi((TWO * aProbality) - ONE)) + myLocation;
  }
  @Override public double getStandardDeviation(){
    return myScale;
  }
  @Override protected double generate(){
    return (this.random().nextGaussian() * myScale) + myLocation;
  }
}
