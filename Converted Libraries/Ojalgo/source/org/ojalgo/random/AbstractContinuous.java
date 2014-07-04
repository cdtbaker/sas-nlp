package org.ojalgo.random;
import static org.ojalgo.constant.PrimitiveMath.*;
abstract class AbstractContinuous extends RandomNumber implements ContinuousDistribution {
  AbstractContinuous(){
    super();
  }
  public final double getLowerConfidenceQuantile(  final double aConfidence){
    return this.getQuantile((ONE - aConfidence) / TWO);
  }
  public final double getUpperConfidenceQuantile(  final double aConfidence){
    return this.getQuantile(ONE - ((ONE - aConfidence) / TWO));
  }
}
