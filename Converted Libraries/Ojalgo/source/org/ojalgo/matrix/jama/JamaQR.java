package org.ojalgo.matrix.jama;
import org.ojalgo.access.Access2D;
import org.ojalgo.function.aggregator.AggregatorFunction;
import org.ojalgo.function.aggregator.PrimitiveAggregator;
import org.ojalgo.matrix.MatrixUtils;
import org.ojalgo.matrix.decomposition.QR;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.type.context.NumberContext;
/** 
 * This class adapts JAMA's QRDecomposition to ojAlgo's {@linkplain QR} interface.
 * @author apete
 */
public final class JamaQR extends JamaAbstractDecomposition implements QR<Double> {
  private QRDecomposition myDelegate;
  /** 
 * Not recommended to use this constructor directly. Consider using the static factory method{@linkplain org.ojalgo.matrix.decomposition.QRDecomposition#makeJama()} instead.
 */
  public JamaQR(){
    super();
  }
  public Double calculateDeterminant(  final Access2D<Double> matrix){
    this.compute(matrix);
    return this.getDeterminant();
  }
  public boolean compute(  final Access2D<?> matrix,  final boolean fullSize){
    if (fullSize) {
      throw new IllegalArgumentException("Cannot do full size!");
    }
 else {
      return this.compute(matrix);
    }
  }
  public boolean equals(  final MatrixStore<Double> aStore,  final NumberContext context){
    return MatrixUtils.equals(aStore,this,context);
  }
  public Double getDeterminant(){
    final AggregatorFunction<Double> tmpAggrFunc=PrimitiveAggregator.getCollection().product();
    this.getR().visitDiagonal(0,0,tmpAggrFunc);
    return tmpAggrFunc.getNumber();
  }
  @Override public JamaMatrix getInverse(){
    return this.solve(this.makeEyeStore(myDelegate.getQ().getRowDimension(),myDelegate.getR().getColumnDimension()));
  }
  public JamaMatrix getQ(){
    return new JamaMatrix(myDelegate.getQ());
  }
  public JamaMatrix getR(){
    return new JamaMatrix(myDelegate.getR());
  }
  public int getRank(){
    int retVal=0;
    final MatrixStore<Double> tmpR=this.getR();
    final int tmpMinDim=tmpR.getMinDim();
    for (int ij=0; ij < tmpMinDim; ij++) {
      if (!tmpR.toScalar(ij,ij).isZero()) {
        retVal++;
      }
    }
    return retVal;
  }
  public boolean isAspectRatioNormal(){
    return myDelegate.getQ().getRowDimension() >= myDelegate.getR().getColumnDimension();
  }
  public boolean isComputed(){
    return myDelegate != null;
  }
  public boolean isFullColumnRank(){
    return this.isSolvable();
  }
  public boolean isFullSize(){
    return false;
  }
  public boolean isSolvable(){
    return (myDelegate != null) && myDelegate.isFullRank();
  }
  public MatrixStore<Double> reconstruct(){
    return MatrixUtils.reconstruct(this);
  }
  public void reset(){
    myDelegate=null;
  }
  @Override public String toString(){
    return myDelegate.toString();
  }
  @Override boolean compute(  final Matrix aDelegate){
    myDelegate=new QRDecomposition(aDelegate);
    return this.isComputed();
  }
  @Override Matrix solve(  final Matrix aRHS){
    return myDelegate.solve(aRHS);
  }
}
