package org.ojalgo.random;
import static org.ojalgo.constant.PrimitiveMath.*;
/** 
 * Certain waiting times.
 * Rounding errors.
 * @author apete
 */
public class Uniform extends AbstractContinuous {
  private static final long serialVersionUID=-8198257914507986404L;
  /** 
 * @return An integer: 0 <= ? < aLimit
 */
  public static int randomInteger(  final int aLimit){
    return (int)Math.floor(aLimit * Math.random());
  }
  /** 
 * @return An integer: aLower <= ? < aHigher
 */
  public static int randomInteger(  final int aLower,  final int aHigher){
    return aLower + Uniform.randomInteger(aHigher - aLower);
  }
  private final double myLower;
  private final double myRange;
  public Uniform(){
    this(ZERO,ONE);
  }
  public Uniform(  final double aLower,  final double aRange){
    super();
    if (aRange <= ZERO) {
      throw new IllegalArgumentException("The range must be larger than 0.0!");
    }
    myLower=aLower;
    myRange=aRange;
  }
  public double getDistribution(  final double aValue){
    double retVal=ZERO;
    if ((aValue <= (myLower + myRange)) && (myLower <= aValue)) {
      retVal=(aValue - myLower) / myRange;
    }
 else     if (myLower <= aValue) {
      retVal=ONE;
    }
    return retVal;
  }
  public double getExpected(){
    return myLower + (myRange / TWO);
  }
  public double getProbability(  final double aValue){
    double retVal=ZERO;
    if ((myLower <= aValue) && (aValue <= (myLower + myRange))) {
      retVal=ONE / myRange;
    }
    return retVal;
  }
  public double getQuantile(  final double aProbality){
    this.checkProbabilty(aProbality);
    return myLower + (aProbality * myRange);
  }
  @Override public double getVariance(){
    return (myRange * myRange) / TWELVE;
  }
  @Override protected double generate(){
    return myLower + (myRange * this.random().nextDouble());
  }
}
