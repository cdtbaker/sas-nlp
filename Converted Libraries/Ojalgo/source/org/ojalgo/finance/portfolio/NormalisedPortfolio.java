package org.ojalgo.finance.portfolio;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.ojalgo.ProgrammingError;
import org.ojalgo.constant.BigMath;
import org.ojalgo.function.BigFunction;
/** 
 * Normalised weights Portfolio
 * @author apete
 */
final class NormalisedPortfolio extends FinancePortfolio {
  private final FinancePortfolio myPortfolio;
  private transient BigDecimal myTotalWeight;
  public NormalisedPortfolio(  final FinancePortfolio aPortfolio){
    super();
    myPortfolio=aPortfolio;
  }
  @SuppressWarnings("unused") private NormalisedPortfolio(){
    this(null);
    ProgrammingError.throwForIllegalInvocation();
  }
  @Override public double getMeanReturn(){
    return myPortfolio.getMeanReturn() / this.getTotalWeight().doubleValue();
  }
  @Override public double getVolatility(){
    return myPortfolio.getVolatility() / this.getTotalWeight().doubleValue();
  }
  @Override public List<BigDecimal> getWeights(){
    final List<BigDecimal> retVal=new ArrayList<BigDecimal>();
    final BigDecimal tmpTotalWeight=this.getTotalWeight();
    BigDecimal tmpSum=BigMath.ZERO;
    BigDecimal tmpLargest=BigMath.ZERO;
    int tmpIndex=-1;
    final List<BigDecimal> tmpWeights=myPortfolio.getWeights();
    BigDecimal tmpWeight;
    for (int i=0; i < tmpWeights.size(); i++) {
      tmpWeight=tmpWeights.get(i);
      tmpWeight=BigFunction.DIVIDE.invoke(tmpWeight,tmpTotalWeight);
      tmpWeight=WEIGHT_CONTEXT.enforce(tmpWeight);
      retVal.add(tmpWeight);
      tmpSum=tmpSum.add(tmpWeight);
      if (tmpWeight.abs().compareTo(tmpLargest) == 1) {
        tmpLargest=tmpWeight.abs();
        tmpIndex=i;
      }
    }
    if ((tmpSum.compareTo(BigMath.ONE) != 0) && (tmpIndex != -1)) {
      retVal.set(tmpIndex,retVal.get(tmpIndex).subtract(tmpSum.subtract(BigMath.ONE)));
    }
    return retVal;
  }
  private final BigDecimal getTotalWeight(){
    if (myTotalWeight == null) {
      myTotalWeight=BigMath.ZERO;
      for (      final BigDecimal tmpWeight : myPortfolio.getWeights()) {
        myTotalWeight=myTotalWeight.add(tmpWeight);
      }
      if (myTotalWeight.signum() == 0) {
        myTotalWeight=BigMath.ONE;
      }
    }
    return myTotalWeight;
  }
  @Override protected void reset(){
    myPortfolio.reset();
    myTotalWeight=null;
  }
}
