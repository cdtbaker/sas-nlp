package org.ojalgo.matrix.store;
import org.ojalgo.ProgrammingError;
import org.ojalgo.constant.PrimitiveMath;
import org.ojalgo.scalar.Scalar;
/** 
 * A Hessenberg matrix is one that is "almost" triangular. A lower
 * Hessenberg matrix has zero entries above the first superdiagonal.
 * @author apete
 */
public final class LowerHessenbergStore<N extends Number> extends ShadingStore<N> {
  public LowerHessenbergStore(  final MatrixStore<N> aBase){
    super(aBase.getRowDim(),aBase.getMinDim(),aBase);
  }
  @SuppressWarnings("unused") private LowerHessenbergStore(  final int aRowDim,  final int aColDim,  final MatrixStore<N> aBase){
    this(aBase);
    ProgrammingError.throwForIllegalInvocation();
  }
  public double doubleValue(  final long aRow,  final long aCol){
    if ((aRow + 1) < aCol) {
      return PrimitiveMath.ZERO;
    }
 else {
      return this.getBase().doubleValue(aRow,aCol);
    }
  }
  public N get(  final long aRow,  final long aCol){
    if ((aRow + 1) < aCol) {
      return this.factory().getStaticZero().getNumber();
    }
 else {
      return this.getBase().get(aRow,aCol);
    }
  }
  public boolean isLowerLeftShaded(){
    return false;
  }
  public boolean isUpperRightShaded(){
    return true;
  }
  public Scalar<N> toScalar(  final int row,  final int column){
    if ((row + 1) < column) {
      return this.factory().getStaticZero();
    }
 else {
      return this.getBase().toScalar(row,column);
    }
  }
}
