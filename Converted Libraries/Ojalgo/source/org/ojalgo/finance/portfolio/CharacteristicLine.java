package org.ojalgo.finance.portfolio;
import org.ojalgo.ProgrammingError;
import org.ojalgo.function.PrimitiveFunction;
public final class CharacteristicLine {
  public double beta, alpha, epsilon;
  private final FinancePortfolio myMarketPortfolio;
  public CharacteristicLine(  final FinancePortfolio theMarketPortfolio){
    super();
    myMarketPortfolio=theMarketPortfolio;
  }
  @SuppressWarnings("unused") private CharacteristicLine(){
    this(null);
    ProgrammingError.throwForIllegalInvocation();
  }
  public double calculateBeta(  final FinancePortfolio anyAsset){
    return anyAsset.getMeanReturn() / myMarketPortfolio.getMeanReturn();
  }
  public double calculateCorrelation(  final FinancePortfolio anyAsset){
    final double tmpCovar=this.calculateCovariance(anyAsset);
    final double tmpVal=myMarketPortfolio.getReturnVariance() * anyAsset.getReturnVariance();
    return tmpCovar / PrimitiveFunction.SQRT.invoke(tmpVal);
  }
  public double calculateCovariance(  final FinancePortfolio anyAsset){
    final double tmpBeta=this.calculateBeta(anyAsset);
    return myMarketPortfolio.getReturnVariance() * tmpBeta;
  }
}
