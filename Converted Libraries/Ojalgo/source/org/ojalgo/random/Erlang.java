package org.ojalgo.random;
import static org.ojalgo.constant.PrimitiveMath.*;
/** 
 * Distribution of the sum of aCount random variables with an exponential
 * distribution with parameter aLambda.
 * @author apete
 */
public class Erlang extends RandomNumber {
  private static final long serialVersionUID=6544837857838057678L;
  private final int myCount;
  private final double myRate;
  public Erlang(){
    this((int)ONE,ONE);
  }
  public Erlang(  final int aCount,  final double aRate){
    super();
    myCount=aCount;
    myRate=aRate;
  }
  public double getExpected(){
    return myCount / myRate;
  }
  @Override public double getVariance(){
    return myCount / (myRate * myRate);
  }
  @Override protected double generate(){
    double tmpVal=ZERO;
    for (int i=0; i < myCount; i++) {
      tmpVal-=Math.log(this.random().nextDouble());
    }
    return tmpVal / myRate;
  }
}
