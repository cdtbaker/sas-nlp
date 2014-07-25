package org.ojalgo.matrix.store;
import org.ojalgo.ProgrammingError;
import org.ojalgo.constant.PrimitiveMath;
import org.ojalgo.scalar.Scalar;
/** 
 * A Hessenberg matrix is one that is "almost" triangular. An upper
 * Hessenberg matrix has zero entries below the first subdiagonal.
 * @author apete
 */
public final class UpperHessenbergStore<N extends Number> extends ShadingStore<N> {
  public UpperHessenbergStore(  final MatrixStore<N> aBase){
    super(aBase.getMinDim(),aBase.getColDim(),aBase);
  }
  @SuppressWarnings("unused") private UpperHessenbergStore(  final int aRowDim,  final int aColDim,  final MatrixStore<N> aBase){
    this(aBase);
    ProgrammingError.throwForIllegalInvocation();
  }
  public double doubleValue(  final long aRow,  final long aCol){
    if (aRow > (aCol + 1)) {
      return PrimitiveMath.ZERO;
    }
 else {
      return this.getBase().doubleValue(aRow,aCol);
    }
  }
  public N get(  final long aRow,  final long aCol){
    if (aRow > (aCol + 1)) {
      return this.factory().getStaticZero().getNumber();
    }
 else {
      return this.getBase().get(aRow,aCol);
    }
  }
  public boolean isLowerLeftShaded(){
    return true;
  }
  public boolean isUpperRightShaded(){
    return false;
  }
  public Scalar<N> toScalar(  final int row,  final int column){
    if (row > (column + 1)) {
      return this.factory().getStaticZero();
    }
 else {
      return this.getBase().toScalar(row,column);
    }
  }
}
