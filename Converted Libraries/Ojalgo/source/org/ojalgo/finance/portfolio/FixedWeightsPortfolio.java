package org.ojalgo.finance.portfolio;
import java.util.List;
import org.ojalgo.ProgrammingError;
import org.ojalgo.matrix.BasicMatrix;
public final class FixedWeightsPortfolio extends EquilibriumModel {
  private final BasicMatrix myWeights;
  public FixedWeightsPortfolio(  final Context aContext,  final FinancePortfolio weightsPortfolio){
    super(aContext);
    myWeights=FinancePortfolio.MATRIX_FACTORY.columns(weightsPortfolio.getWeights());
  }
  public FixedWeightsPortfolio(  final MarketEquilibrium aMarketEquilibrium,  final BasicMatrix assetWeightsInColumn){
    super(aMarketEquilibrium);
    myWeights=assetWeightsInColumn;
  }
  @SuppressWarnings("unused") private FixedWeightsPortfolio(  final MarketEquilibrium aMarketEquilibrium){
    super(aMarketEquilibrium);
    myWeights=null;
    ProgrammingError.throwForIllegalInvocation();
  }
  public void calibrate(  final FinancePortfolio.Context targetReturns){
    this.calibrate(myWeights,targetReturns.getAssetReturns());
  }
  public void calibrate(  final List<? extends Number> targetReturns){
    this.calibrate(myWeights,FinancePortfolio.MATRIX_FACTORY.columns(targetReturns));
  }
  @Override protected BasicMatrix calculateAssetReturns(){
    return this.calculateAssetReturns(myWeights);
  }
  @Override protected BasicMatrix calculateAssetWeights(){
    return myWeights;
  }
}
