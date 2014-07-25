package org.ojalgo.finance.portfolio;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import org.ojalgo.constant.BigMath;
import org.ojalgo.constant.PrimitiveMath;
import org.ojalgo.type.TypeUtils;
/** 
 * SimpleAsset is used to describe 1 asset (portfolio member).
 * @author apete
 */
public final class SimpleAsset extends FinancePortfolio {
  private final double myMeanReturn;
  private final double myVolatility;
  private final BigDecimal myWeight;
  public SimpleAsset(  final FinancePortfolio aPortfolio){
    this(aPortfolio.getMeanReturn(),aPortfolio.getVolatility(),BigMath.ONE);
  }
  public SimpleAsset(  final FinancePortfolio aPortfolio,  final Number aWeight){
    this(aPortfolio.getMeanReturn(),aPortfolio.getVolatility(),aWeight);
  }
  public SimpleAsset(  final Number aWeight){
    this(PrimitiveMath.ZERO,PrimitiveMath.ZERO,aWeight);
  }
  public SimpleAsset(  final Number aMeanReturn,  final Number aVolatility){
    this(aMeanReturn,aVolatility,BigMath.ONE);
  }
  public SimpleAsset(  final Number aMeanReturn,  final Number aVolatility,  final Number aWeight){
    super();
    myMeanReturn=aMeanReturn.doubleValue();
    myVolatility=aVolatility.doubleValue();
    myWeight=TypeUtils.toBigDecimal(aWeight,WEIGHT_CONTEXT);
  }
  @SuppressWarnings("unused") private SimpleAsset(){
    this(BigMath.ZERO,BigMath.ZERO,BigMath.ONE);
  }
  @Override public double getMeanReturn(){
    return myMeanReturn;
  }
  @Override public double getVolatility(){
    return myVolatility;
  }
  /** 
 * Assuming there is precisely 1 weight - this class is used to
 * describe 1 asset (portfolio member).
 */
  public BigDecimal getWeight(){
    return myWeight;
  }
  @Override public List<BigDecimal> getWeights(){
    return Collections.singletonList(myWeight);
  }
  @Override protected void reset(){
    ;
  }
}
