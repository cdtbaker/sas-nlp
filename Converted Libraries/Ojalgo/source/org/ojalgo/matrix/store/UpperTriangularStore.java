package org.ojalgo.matrix.store;
import org.ojalgo.ProgrammingError;
import org.ojalgo.constant.PrimitiveMath;
import org.ojalgo.scalar.Scalar;
public final class UpperTriangularStore<N extends Number> extends ShadingStore<N> {
  private final boolean myAssumeOne;
  public UpperTriangularStore(  final MatrixStore<N> aBase,  final boolean assumeOne){
    super(aBase.getMinDim(),aBase.getColDim(),aBase);
    myAssumeOne=assumeOne;
  }
  @SuppressWarnings("unused") private UpperTriangularStore(  final int aRowDim,  final int aColDim,  final MatrixStore<N> aBase){
    this(aBase,true);
    ProgrammingError.throwForIllegalInvocation();
  }
  public double doubleValue(  final long aRow,  final long aCol){
    if (aRow > aCol) {
      return PrimitiveMath.ZERO;
    }
 else     if (myAssumeOne && (aRow == aCol)) {
      return PrimitiveMath.ONE;
    }
 else {
      return this.getBase().doubleValue(aRow,aCol);
    }
  }
  public N get(  final long aRow,  final long aCol){
    if (aRow > aCol) {
      return this.factory().getStaticZero().getNumber();
    }
 else     if (myAssumeOne && (aRow == aCol)) {
      return this.factory().getStaticOne().getNumber();
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
    if (row > column) {
      return this.factory().getStaticZero();
    }
 else     if (myAssumeOne && (row == column)) {
      return this.factory().getStaticOne();
    }
 else {
      return this.getBase().toScalar(row,column);
    }
  }
}
