package org.ojalgo.random.process;
import static org.ojalgo.constant.PrimitiveMath.*;
import org.ojalgo.random.Exponential;
import org.ojalgo.random.Poisson;
/** 
 * A Poisson process is a stochastic process which counts the number of events
 * in a given time interval. The time between each pair of consecutive events
 * has an exponential distribution with parameter λ and each of these
 * inter-arrival times is assumed to be independent of other inter-arrival times.
 * The process is a good model of radioactive decay, telephone calls and requests
 * for a particular document on a web server, among many other phenomena.
 * @author apete
 */
public final class PoissonProcess extends AbstractProcess<Poisson> {
  private final double myRate;
  private static final Poisson GENERATOR=new Poisson();
  protected PoissonProcess(  final double aRate){
    super();
    this.setValue(ZERO);
    myRate=aRate;
  }
  public Poisson getDistribution(  final double evaluationPoint){
    return new Poisson(myRate * evaluationPoint);
  }
  public Exponential getTimeBetweenConsecutiveEvents(){
    return new Exponential(myRate);
  }
  @Override protected double getNormalisedRandomIncrement(){
    return GENERATOR.doubleValue();
  }
  @Override protected double step(  final double currentValue,  final double stepSize,  final double normalisedRandomIncrement){
    final double retVal=currentValue + ((myRate * stepSize) * normalisedRandomIncrement);
    this.setValue(retVal);
    return retVal;
  }
  @Override double getExpected(  final double aStepSize){
    return myRate * aStepSize;
  }
  @Override double getLowerConfidenceQuantile(  final double aStepSize,  final double aConfidence){
    throw new UnsupportedOperationException();
  }
  @Override double getStandardDeviation(  final double aStepSize){
    return Math.sqrt(myRate * aStepSize);
  }
  @Override double getUpperConfidenceQuantile(  final double aStepSize,  final double aConfidence){
    throw new UnsupportedOperationException();
  }
  @Override double getVariance(  final double aStepSize){
    return myRate * aStepSize;
  }
}
