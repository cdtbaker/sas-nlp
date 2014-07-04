package org.ojalgo.finance.portfolio;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.ojalgo.ProgrammingError;
import org.ojalgo.matrix.BasicMatrix;
import org.ojalgo.scalar.Scalar;
import org.ojalgo.type.TypeUtils;
abstract class EquilibriumModel extends FinancePortfolio implements FinancePortfolio.Context {
  private transient BasicMatrix myAssetReturns;
  private transient BasicMatrix myAssetVolatilities;
  private transient BasicMatrix myAssetWeights;
  private final MarketEquilibrium myMarketEquilibrium;
  private transient Scalar<?> myMeanReturn;
  private transient Scalar<?> myReturnVariance;
  @SuppressWarnings("unused") private EquilibriumModel(){
    this((MarketEquilibrium)null);
    ProgrammingError.throwForIllegalInvocation();
  }
  protected EquilibriumModel(  final Context aPortfolio){
    super();
    myMarketEquilibrium=new MarketEquilibrium(aPortfolio.getCovariances());
  }
  protected EquilibriumModel(  final MarketEquilibrium aMarketEquilibrium){
    super();
    myMarketEquilibrium=aMarketEquilibrium.copy();
  }
  public final double calculatePortfolioReturn(  final FinancePortfolio weightsPortfolio){
    final List<BigDecimal> tmpWeights=weightsPortfolio.getWeights();
    final BasicMatrix tmpAssetWeights=FinancePortfolio.MATRIX_FACTORY.columns(tmpWeights);
    final BasicMatrix tmpAssetReturns=this.getAssetReturns();
    return this.calculatePortfolioReturn(tmpAssetWeights,tmpAssetReturns).doubleValue();
  }
  public final double calculatePortfolioVariance(  final FinancePortfolio weightsPortfolio){
    final List<BigDecimal> tmpWeights=weightsPortfolio.getWeights();
    final BasicMatrix tmpAssetWeights=FinancePortfolio.MATRIX_FACTORY.columns(tmpWeights);
    return this.calculatePortfolioVariance(tmpAssetWeights).doubleValue();
  }
  public final BasicMatrix getAssetReturns(){
    if (myAssetReturns == null) {
      myAssetReturns=this.calculateAssetReturns();
    }
    return myAssetReturns;
  }
  public final BasicMatrix getAssetVolatilities(){
    if (myAssetVolatilities == null) {
      myAssetVolatilities=myMarketEquilibrium.toCorrelations();
    }
    return myAssetVolatilities;
  }
  public final BasicMatrix getAssetWeights(){
    if (myAssetWeights == null) {
      final BasicMatrix tmpAssetWeights=this.calculateAssetWeights();
      if (tmpAssetWeights != null) {
        myAssetWeights=tmpAssetWeights.round(WEIGHT_CONTEXT);
      }
    }
    return myAssetWeights;
  }
  public final BasicMatrix getCorrelations(){
    return myMarketEquilibrium.toCorrelations();
  }
  public final BasicMatrix getCovariances(){
    return myMarketEquilibrium.getCovariances();
  }
  public final MarketEquilibrium getMarketEquilibrium(){
    return myMarketEquilibrium.copy();
  }
  @Override public final double getMeanReturn(){
    if (myMeanReturn == null) {
      final BasicMatrix tmpAssetWeights=this.getAssetWeights();
      final BasicMatrix tmpAssetReturns=this.getAssetReturns();
      if ((tmpAssetWeights != null) && (tmpAssetReturns != null)) {
        myMeanReturn=this.calculatePortfolioReturn(tmpAssetWeights,tmpAssetReturns);
      }
    }
    return myMeanReturn.doubleValue();
  }
  @Override public final double getReturnVariance(){
    if (myReturnVariance == null) {
      myReturnVariance=this.calculatePortfolioVariance(this.getAssetWeights());
    }
    return myReturnVariance.doubleValue();
  }
  public final Scalar<?> getRiskAversion(){
    return myMarketEquilibrium.getRiskAversion();
  }
  public final String[] getSymbols(){
    return myMarketEquilibrium.getAssetKeys();
  }
  @Override public final List<BigDecimal> getWeights(){
    final BasicMatrix tmpAssetWeights=this.getAssetWeights();
    if (tmpAssetWeights != null) {
      return tmpAssetWeights.toBigStore().asList();
    }
 else {
      return null;
    }
  }
  public final void setRiskAversion(  final Number aFactor){
    myMarketEquilibrium.setRiskAversion(aFactor);
    this.reset();
  }
  public int size(){
    return myMarketEquilibrium.size();
  }
  public final List<SimpleAsset> toSimpleAssets(){
    final BasicMatrix tmpReturns=this.getAssetReturns();
    final BasicMatrix tmpCovariances=this.getCovariances();
    final List<BigDecimal> tmpWeights=this.getWeights();
    final ArrayList<SimpleAsset> retVal=new ArrayList<SimpleAsset>(tmpWeights.size());
    for (int i=0; i < tmpWeights.size(); i++) {
      final double tmpMeanReturn=tmpReturns.doubleValue(i,0);
      final double tmpVolatility=Math.sqrt(tmpCovariances.doubleValue(i,i));
      final BigDecimal tmpWeight=tmpWeights.get(i);
      retVal.add(new SimpleAsset(tmpMeanReturn,tmpVolatility,tmpWeight));
    }
    return retVal;
  }
  public final SimplePortfolio toSimplePortfolio(){
    return new SimplePortfolio(this.getCorrelations(),this.toSimpleAssets());
  }
  @Override public String toString(){
    return TypeUtils.format("RAF={} {}",this.getRiskAversion().toString(),super.toString());
  }
  protected abstract BasicMatrix calculateAssetReturns();
  protected final BasicMatrix calculateAssetReturns(  final BasicMatrix aWeightsVctr){
    return myMarketEquilibrium.calculateAssetReturns(aWeightsVctr);
  }
  protected abstract BasicMatrix calculateAssetWeights();
  protected final BasicMatrix calculateAssetWeights(  final BasicMatrix aReturnsVctr){
    return myMarketEquilibrium.calculateAssetWeights(aReturnsVctr);
  }
  protected final Scalar<?> calculatePortfolioReturn(  final BasicMatrix aWeightsVctr,  final BasicMatrix aReturnsVctr){
    return MarketEquilibrium.calculatePortfolioReturn(aWeightsVctr,aReturnsVctr);
  }
  protected final Scalar<?> calculatePortfolioVariance(  final BasicMatrix aWeightsVctr){
    return myMarketEquilibrium.calculatePortfolioVariance(aWeightsVctr);
  }
  protected final void calibrate(  final BasicMatrix aWeightsVctr,  final BasicMatrix aReturnsVctr){
    final Scalar<?> tmpRiskAvesrion=myMarketEquilibrium.calculateImpliedRiskAversion(aWeightsVctr,aReturnsVctr);
    this.setRiskAversion(tmpRiskAvesrion.getNumber());
  }
  @Override protected void reset(){
    myAssetWeights=null;
    myAssetReturns=null;
    myMeanReturn=null;
    myReturnVariance=null;
  }
}
