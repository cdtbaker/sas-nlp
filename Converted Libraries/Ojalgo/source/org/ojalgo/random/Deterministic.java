package org.ojalgo.random;
import static org.ojalgo.constant.PrimitiveMath.*;
/** 
 * @author apete
 */
public class Deterministic extends RandomNumber {
  private static final long serialVersionUID=6544837857838057678L;
  private final double myValue;
  public Deterministic(){
    super();
    myValue=ZERO;
  }
  public Deterministic(  final double aValue){
    super();
    myValue=aValue;
  }
  public Deterministic(  final Number aValue){
    super();
    myValue=aValue.doubleValue();
  }
  public double getExpected(){
    return myValue;
  }
  @Override public double getStandardDeviation(){
    return ZERO;
  }
  @Override public double getVariance(){
    return ZERO;
  }
  @Override protected double generate(){
    return myValue;
  }
}
