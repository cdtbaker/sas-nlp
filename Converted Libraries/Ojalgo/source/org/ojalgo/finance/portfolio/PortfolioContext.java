package org.ojalgo.finance.portfolio;
import org.ojalgo.access.Access1D;
import org.ojalgo.access.Access2D;
import org.ojalgo.finance.FinanceUtils;
import org.ojalgo.matrix.PrimitiveMatrix;
public class PortfolioContext implements FinancePortfolio.Context {
  private final PrimitiveMatrix myAssetReturns;
  private PrimitiveMatrix myAssetVolatilities=null;
  private PrimitiveMatrix myCorrelations=null;
  private PrimitiveMatrix myCovariances=null;
  public PortfolioContext(  final Access1D<?> assetReturns,  final Access1D<?> assetVolatilities,  final Access2D<?> correlations){
    super();
    myAssetReturns=FinancePortfolio.MATRIX_FACTORY.columns(assetReturns);
    myAssetVolatilities=FinancePortfolio.MATRIX_FACTORY.columns(assetVolatilities);
    myCorrelations=FinancePortfolio.MATRIX_FACTORY.copy(correlations);
  }
  public PortfolioContext(  final Access1D<?> assetReturns,  final Access2D<?> covariances){
    super();
    myAssetReturns=FinancePortfolio.MATRIX_FACTORY.columns(assetReturns);
    myCovariances=FinancePortfolio.MATRIX_FACTORY.copy(covariances);
  }
  @SuppressWarnings("unused") private PortfolioContext(){
    super();
    myAssetReturns=null;
  }
  @SuppressWarnings("unchecked") public double calculatePortfolioReturn(  final FinancePortfolio weightsPortfolio){
    return FinancePortfolio.MATRIX_FACTORY.rows(weightsPortfolio.getWeights()).multiplyRight(this.getAssetReturns()).doubleValue(0);
  }
  @SuppressWarnings("unchecked") public double calculatePortfolioVariance(  final FinancePortfolio weightsPortfolio){
    final PrimitiveMatrix tmpWeights=FinancePortfolio.MATRIX_FACTORY.columns(weightsPortfolio.getWeights());
    return this.getCovariances().multiplyRight(tmpWeights).multiplyLeft(tmpWeights.transpose()).doubleValue(0);
  }
  public PrimitiveMatrix getAssetReturns(){
    return myAssetReturns;
  }
  public PrimitiveMatrix getAssetVolatilities(){
    if (myAssetVolatilities == null) {
      myAssetVolatilities=FinanceUtils.toAssetVolatilities(myCovariances);
    }
    return myAssetVolatilities;
  }
  public PrimitiveMatrix getCorrelations(){
    if (myCorrelations == null) {
      myCorrelations=FinanceUtils.toCorrelations(myCovariances,false);
    }
    return myCorrelations;
  }
  public PrimitiveMatrix getCovariances(){
    if (myCovariances == null) {
      myCovariances=FinanceUtils.toCovariances(myAssetVolatilities,myCorrelations);
    }
    return myCovariances;
  }
  public int size(){
    return myAssetReturns.size();
  }
}
