package org.ojalgo.random;
import static org.ojalgo.constant.PrimitiveMath.*;
/** 
 * The Poisson distribution is a discrete probability distribution that expresses
 * the probability of a given number of events occurring in a fixed interval of
 * time and/or space if these events occur with a known average rate and
 * independently of the time since the last event.
 * (The Poisson distribution can also be used for the number of events in other
 * specified intervals such as distance, area or volume.)
 * Distribution of number of points in random point process under certain simple
 * assumptions. Approximation to the binomial distribution when aCount is
 * large and aProbability is small. aLambda = aCount * aProbability.
 * @author apete
 */
public class Poisson extends AbstractDiscrete {
  private static final long serialVersionUID=-5382163736545207782L;
  private final double myLambda;
  public Poisson(){
    this(ONE);
  }
  public Poisson(  final double aLambda){
    super();
    myLambda=aLambda;
  }
  public double getExpected(){
    return myLambda;
  }
  public double getProbability(  final int aVal){
    return (Math.exp(-myLambda) * Math.pow(myLambda,aVal)) / RandomUtils.factorial(aVal);
  }
  @Override public double getVariance(){
    return myLambda;
  }
  @Override protected double generate(){
    int retVal=-1;
    double tmpVal=ZERO;
    while (tmpVal <= ONE) {
      retVal++;
      tmpVal-=Math.log(this.random().nextDouble()) / myLambda;
    }
    return retVal;
  }
}
