package org.ojalgo.random;
import static org.ojalgo.constant.PrimitiveMath.*;
/** 
 * Useful as length of life distribution in reliability theory.
 * @author apete
 */
public class Weibull extends RandomNumber {
  private static final long serialVersionUID=7315696913427382955L;
  private final double myShape;
  private final double myRate;
  public Weibull(){
    this(ONE,ONE);
  }
  public Weibull(  final double aLambda,  final double aBeta){
    super();
    myRate=aLambda;
    myShape=aBeta;
  }
  public double getExpected(){
    return RandomUtils.gamma(ONE + (ONE / myShape)) / myRate;
  }
  @Override public double getVariance(){
    final double tmpA=RandomUtils.gamma(ONE + (TWO / myShape));
    final double tmpB=RandomUtils.gamma(ONE + (ONE / myShape));
    return (tmpA - (tmpB * tmpB)) / (myRate * myRate);
  }
  @Override protected double generate(){
    return Math.pow(-Math.log(this.random().nextDouble()),ONE / myShape) / myRate;
  }
}
