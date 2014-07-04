package org.ojalgo.matrix.jama;
import org.ojalgo.access.Access2D;
import org.ojalgo.matrix.MatrixUtils;
import org.ojalgo.matrix.decomposition.Cholesky;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.scalar.Scalar;
import org.ojalgo.type.context.NumberContext;
/** 
 * This class adapts JAMA's CholeskyDecomposition to ojAlgo's {@linkplain Cholesky} interface.
 * @author apete
 */
public final class JamaCholesky extends JamaAbstractDecomposition implements Cholesky<Double> {
  private CholeskyDecomposition myDelegate;
  /** 
 * Not recommended to use this constructor directly. Consider using the static factory method{@linkplain org.ojalgo.matrix.decomposition.CholeskyDecomposition#makeJama()} instead.
 */
  public JamaCholesky(){
    super();
  }
  public Double calculateDeterminant(  final Access2D<Double> matrix){
    this.compute(matrix);
    return this.getDeterminant();
  }
  public boolean compute(  final Access2D<?> matrix,  final boolean checkHermitian){
    return this.compute(matrix);
  }
  public boolean computeWithCheck(  final MatrixStore<?> aStore){
    return this.compute(aStore);
  }
  public boolean equals(  final MatrixStore<Double> aStore,  final NumberContext context){
    return MatrixUtils.equals(aStore,this,context);
  }
  public JamaMatrix getD(){
    final Matrix tmpL=myDelegate.getL();
    final int tmpRowDim=tmpL.getRowDimension();
    final int tmpColDim=tmpL.getColumnDimension();
    final int tmpMinDim=Math.min(tmpRowDim,tmpColDim);
    final JamaMatrix retVal=new JamaMatrix(new Matrix(tmpRowDim,tmpColDim));
    double tmpVal;
    for (int ij=0; ij < tmpMinDim; ij++) {
      tmpVal=tmpL.get(ij,ij);
      retVal.update(ij,ij,tmpVal * tmpVal);
    }
    return retVal;
  }
  public Double getDeterminant(){
    final MatrixStore<Double> tmpD=this.getD();
    final int tmpMinDim=tmpD.getMinDim();
    Scalar<Double> retVal=tmpD.toScalar(0,0);
    for (int ij=1; ij < tmpMinDim; ij++) {
      retVal=retVal.multiply(tmpD.get(ij,ij));
    }
    return retVal.getNumber();
  }
  @Override public JamaMatrix getInverse(){
    return this.solve(this.makeEyeStore(myDelegate.getL().getRowDimension(),myDelegate.getL().getColumnDimension()));
  }
  public JamaMatrix getL(){
    return new JamaMatrix(myDelegate.getL());
  }
  public JamaMatrix getOldL(){
    final Matrix tmpL=myDelegate.getL();
    final int tmpRowDim=tmpL.getRowDimension();
    final int tmpColDim=tmpL.getColumnDimension();
    final JamaMatrix retVal=new JamaMatrix(new Matrix(tmpRowDim,tmpColDim));
    double tmpDiagVal;
    for (int j=0; j < tmpColDim; j++) {
      tmpDiagVal=tmpL.get(j,j);
      for (int i=j; i < tmpRowDim; i++) {
        retVal.update(i,j,tmpL.get(i,j) / tmpDiagVal);
      }
    }
    return retVal;
  }
  public JamaMatrix getOldU(){
    return this.getOldL().transpose();
  }
  public JamaMatrix getP(){
    return this.makeEyeStore(myDelegate.getL().getRowDimension(),myDelegate.getL().getRowDimension());
  }
  public int[] getPivotOrder(){
    return MatrixUtils.makeIncreasingRange(0,this.getOldL().getRowDim());
  }
  public JamaMatrix getR(){
    return new JamaMatrix(myDelegate.getL().transpose());
  }
  public int getRank(){
    int retVal=0;
    final MatrixStore<Double> tmpD=this.getD();
    final int tmpMinDim=tmpD.getMinDim();
    for (int ij=0; ij < tmpMinDim; ij++) {
      if (!tmpD.toScalar(ij,ij).isZero()) {
        retVal++;
      }
    }
    return retVal;
  }
  public JamaMatrix getRowEchelonForm(){
    return this.getOldU();
  }
  public boolean isAspectRatioNormal(){
    return true;
  }
  public boolean isComputed(){
    return myDelegate != null;
  }
  public boolean isFullSize(){
    return true;
  }
  public boolean isSingular(){
    boolean retVal=true;
    final MatrixStore<Double> tmpD=this.getD();
    final int tmpMinDim=tmpD.getMinDim();
    for (int ij=0; retVal && (ij < tmpMinDim); ij++) {
      retVal&=!tmpD.toScalar(ij,ij).isZero();
    }
    return !retVal;
  }
  public boolean isSolvable(){
    return (myDelegate != null) && myDelegate.isSPD();
  }
  public boolean isSPD(){
    return this.isSolvable();
  }
  public boolean isSquareAndNotSingular(){
    boolean retVal=true;
    final MatrixStore<Double> tmpD=this.getD();
    final int tmpMinDim=tmpD.getMinDim();
    for (int ij=0; retVal && (ij < tmpMinDim); ij++) {
      retVal&=!tmpD.toScalar(ij,ij).isZero();
    }
    return retVal;
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
    myDelegate=new CholeskyDecomposition(aDelegate);
    return myDelegate.isSPD();
  }
  @Override Matrix solve(  final Matrix aRHS){
    return myDelegate.solve(aRHS);
  }
}
