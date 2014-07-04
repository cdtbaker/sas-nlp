package org.ojalgo.random;
import static org.ojalgo.constant.PrimitiveMath.*;
/** 
 * The frequency in aCount indepedent trials, each with probability aProbability,
 * has a binomial distribution.
 * @author apete
 */
public class Binomial extends AbstractDiscrete {
  private static final long serialVersionUID=-3146302867013736326L;
  private final int myCount;
  private final double myProbability;
  public Binomial(){
    this(1,HALF);
  }
  public Binomial(  final int aCount,  final double aProbability){
    super();
    myCount=aCount;
    myProbability=aProbability;
  }
  public double getExpected(){
    return myCount * myProbability;
  }
  public double getProbability(  final int aVal){
    return RandomUtils.subsets(myCount,aVal) * Math.pow(myProbability,aVal) * Math.pow(ONE - myProbability,myCount - aVal);
  }
  @Override public double getVariance(){
    return myCount * myProbability * (ONE - myProbability);
  }
  @Override protected double generate(){
    int retVal=0;
    for (int i=0; i < myCount; i++) {
      retVal+=(myProbability + this.random().nextDouble());
    }
    return retVal;
  }
}
