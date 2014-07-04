package org.ojalgo.finance.portfolio;
import java.util.List;
import org.ojalgo.ProgrammingError;
import org.ojalgo.matrix.BasicMatrix;
public final class FixedReturnsPortfolio extends EquilibriumModel {
  private final BasicMatrix myReturns;
  public FixedReturnsPortfolio(  final Context aContext){
    super(aContext);
    myReturns=aContext.getAssetReturns();
  }
  public FixedReturnsPortfolio(  final MarketEquilibrium aMarketEquilibrium,  final BasicMatrix returnsVector){
    super(aMarketEquilibrium);
    myReturns=returnsVector;
  }
  @SuppressWarnings("unused") private FixedReturnsPortfolio(  final MarketEquilibrium aMarketEquilibrium){
    super(aMarketEquilibrium);
    myReturns=null;
    ProgrammingError.throwForIllegalInvocation();
  }
  public void calibrate(  final FinancePortfolio targetWeights){
    this.calibrate(targetWeights.getWeights());
  }
  public void calibrate(  final List<? extends Number> targetWeights){
    this.calibrate(FinancePortfolio.MATRIX_FACTORY.columns(targetWeights),myReturns);
  }
  @Override protected BasicMatrix calculateAssetReturns(){
    return myReturns;
  }
  @Override protected BasicMatrix calculateAssetWeights(){
    return this.calculateAssetWeights(myReturns);
  }
}
