package org.ojalgo.random.process;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.random.Normal;
import org.ojalgo.random.Normal1D;
/** 
 * A Gaussian process is a stochastic process whose realizations consist of
 * random values associated with every point in a range of times (or of space)
 * such that each such random variable has a normal distribution. Moreover,
 * every finite collection of those random variables has a multivariate normal
 * distribution.
 * Prior to calling {@linkplain #getDistribution(double)} or{@linkplain #simulate(int,int,double)} you must call{@linkplain #addObservation(double,double)} one or more times.
 * @author apete
 */
public final class GaussianProcess extends AbstractProcess<Normal> {
  private static final Normal GENERATOR=new Normal();
  private final GaussianField<Double> myDelegate;
  public GaussianProcess(  final GaussianField.Covariance<Double> covarFunc){
    super();
    myDelegate=new GaussianField<Double>(covarFunc,this.getObservations());
  }
  public GaussianProcess(  final GaussianField.Mean<Double> meanFunc,  final GaussianField.Covariance<Double> covarFunc){
    super();
    myDelegate=new GaussianField<Double>(meanFunc,covarFunc,this.getObservations());
  }
  @SuppressWarnings("unused") private GaussianProcess(){
    this(null,null);
  }
  public void calibrate(){
    myDelegate.calibrate();
  }
  public Normal getDistribution(  final double evaluationPoint){
    final Normal1D tmpVal=this.getDistribution(new Double[]{evaluationPoint});
    final double tmpLocation=tmpVal.getExpected().doubleValue(0);
    final double tmpScale=tmpVal.getStandardDeviation().doubleValue(0);
    return new Normal(tmpLocation,tmpScale);
  }
  public Normal1D getDistribution(  final Double... evaluationPoint){
    return myDelegate.getDistribution(false,evaluationPoint);
  }
  @Override protected double getNormalisedRandomIncrement(){
    return GENERATOR.doubleValue();
  }
  @Override protected double step(  final double currentValue,  final double stepSize,  final double normalisedRandomIncrement){
    final Normal tmpDistr=this.getDistribution(stepSize);
    final double retVal=(normalisedRandomIncrement * tmpDistr.getStandardDeviation()) + tmpDistr.getExpected();
    this.addObservation(this.getObservations().last().key + stepSize,retVal);
    return retVal;
  }
  MatrixStore<Double> getCovariances(){
    return myDelegate.getC22().reconstruct();
  }
  @Override double getExpected(  final double aStepSize){
    return this.getDistribution(aStepSize).getExpected();
  }
  @Override double getLowerConfidenceQuantile(  final double aStepSize,  final double aConfidence){
    return this.getDistribution(aStepSize).getLowerConfidenceQuantile(aConfidence);
  }
  @Override double getStandardDeviation(  final double aStepSize){
    return this.getDistribution(aStepSize).getStandardDeviation();
  }
  @Override double getUpperConfidenceQuantile(  final double aStepSize,  final double aConfidence){
    return this.getDistribution(aStepSize).getUpperConfidenceQuantile(aConfidence);
  }
  @Override double getVariance(  final double aStepSize){
    return this.getDistribution(aStepSize).getVariance();
  }
}
