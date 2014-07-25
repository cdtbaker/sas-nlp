package org.ojalgo.random;
import static org.ojalgo.constant.PrimitiveMath.*;
import java.util.Random;
import org.ojalgo.function.NullaryFunction;
import org.ojalgo.type.Alternator;
/** 
 * RandomNumber
 * @author apete
 */
public abstract class RandomNumber extends Number implements Distribution, NullaryFunction<Double> {
  private static final Random SEED=new Random();
  private static final long serialVersionUID=-5871398825698010936L;
  static Alternator<Random> makeRandomAlternator(){
    return new Alternator<Random>(new Random(SEED.nextLong()),new Random(SEED.nextLong()));
  }
  private final Alternator<Random> myAlternator=RandomNumber.makeRandomAlternator();
  protected RandomNumber(){
    super();
  }
  @Override public final double doubleValue(){
    return this.generate();
  }
  @Override public final float floatValue(){
    return (float)this.generate();
  }
  /** 
 * Subclasses must override either getStandardDeviation() or getVariance()!
 * @see org.ojalgo.random.Distribution#getStandardDeviation()
 * @see org.ojalgo.random.Distribution#getVariance()
 */
  public double getStandardDeviation(){
    return Math.sqrt(this.getVariance());
  }
  /** 
 * Subclasses must override either getStandardDeviation() or getVariance()!
 * @see org.ojalgo.random.Distribution#getStandardDeviation()
 * @see org.ojalgo.random.Distribution#getVariance()
 */
  public double getVariance(){
    final double tmpStandardDeviation=this.getStandardDeviation();
    return tmpStandardDeviation * tmpStandardDeviation;
  }
  @Override public final int intValue(){
    return (int)this.generate();
  }
  public final Double invoke(){
    return this.generate();
  }
  @Override public final long longValue(){
    return (long)this.generate();
  }
  @Override public String toString(){
    return this.getExpected() + "±" + this.getStandardDeviation();
  }
  protected void checkProbabilty(  final double aProbabilty){
    if ((aProbabilty < ZERO) || (ONE < aProbabilty)) {
      throw new IllegalArgumentException("Probabilty must be [0,1]");
    }
  }
  protected abstract double generate();
  protected final Random random(){
    return myAlternator.get();
  }
}
