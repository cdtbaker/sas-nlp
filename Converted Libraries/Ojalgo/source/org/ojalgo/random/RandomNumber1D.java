package org.ojalgo.random;
import org.ojalgo.access.Access2D;
import org.ojalgo.array.Array1D;
abstract class RandomNumber1D {
  private final Random1D myRandom;
  protected RandomNumber1D(  final Access2D<?> correlations){
    super();
    myRandom=new Random1D(correlations);
  }
  public abstract Array1D<Double> getExpected();
  /** 
 * Subclasses must override either getStandardDeviation() or getVariance()!
 * @see org.ojalgo.random.Distribution#getStandardDeviation()
 * @see org.ojalgo.random.Distribution#getVariance()
 */
  public Array1D<Double> getStandardDeviation(){
    final Array1D<Double> tmpVar=this.getVariance();
    final int tmpLength=tmpVar.length;
    final Array1D<Double> retVal=Array1D.PRIMITIVE.makeZero(tmpLength);
    for (int i=0; i < tmpLength; i++) {
      retVal.set(i,Math.sqrt(tmpVar.doubleValue(i)));
    }
    return retVal;
  }
  /** 
 * Subclasses must override either getStandardDeviation() or getVariance()!
 * @see org.ojalgo.random.Distribution#getStandardDeviation()
 * @see org.ojalgo.random.Distribution#getVariance()
 */
  public Array1D<Double> getVariance(){
    final Array1D<Double> tmpStdDev=this.getStandardDeviation();
    final int tmpLength=tmpStdDev.length;
    final Array1D<Double> retVal=Array1D.PRIMITIVE.makeZero(tmpLength);
    double tmpVal;
    for (int i=0; i < tmpLength; i++) {
      tmpVal=tmpStdDev.doubleValue(i);
      retVal.set(i,tmpVal * tmpVal);
    }
    return retVal;
  }
  protected final Random1D random(){
    return myRandom;
  }
}
