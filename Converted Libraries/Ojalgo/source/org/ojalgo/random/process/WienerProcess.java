package org.ojalgo.random.process;
import static org.ojalgo.constant.PrimitiveMath.*;
import org.ojalgo.random.Normal;
import org.ojalgo.random.RandomUtils;
public final class WienerProcess extends AbstractProcess<Normal> {
  private static final Normal GENERATOR=new Normal();
  public WienerProcess(){
    super();
    this.setValue(ZERO);
  }
  @SuppressWarnings("unused") private WienerProcess(  final double initialValue){
    super();
    this.setValue(initialValue);
  }
  public Normal getDistribution(  final double evaluationPoint){
    return new Normal(this.getValue(),Math.sqrt(evaluationPoint));
  }
  @Override protected double getNormalisedRandomIncrement(){
    return GENERATOR.doubleValue();
  }
  @Override protected double step(  final double currentValue,  final double stepSize,  final double normalisedRandomIncrement){
    final double retVal=currentValue + (Math.sqrt(stepSize) * normalisedRandomIncrement);
    this.setValue(retVal);
    return retVal;
  }
  @Override double getExpected(  final double aStepSize){
    return this.getValue();
  }
  @Override double getLowerConfidenceQuantile(  final double aStepSize,  final double aConfidence){
    return this.getValue() - (Math.sqrt(aStepSize) * SQRT_TWO * RandomUtils.erfi(aConfidence));
  }
  @Override double getStandardDeviation(  final double aStepSize){
    return Math.sqrt(aStepSize);
  }
  @Override double getUpperConfidenceQuantile(  final double aStepSize,  final double aConfidence){
    return this.getValue() + (Math.sqrt(aStepSize) * SQRT_TWO * RandomUtils.erfi(aConfidence));
  }
  @Override double getVariance(  final double aStepSize){
    return aStepSize;
  }
}
