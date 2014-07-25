package org.ojalgo.matrix.store;
import org.ojalgo.ProgrammingError;
abstract class TransjugatedStore<N extends Number> extends LogicalStore<N> {
  @SuppressWarnings("unused") private TransjugatedStore(  final int aRowDim,  final int aColDim,  final MatrixStore<N> aBase){
    super(aRowDim,aColDim,aBase);
    ProgrammingError.throwForIllegalInvocation();
  }
  protected TransjugatedStore(  final MatrixStore<N> aBase){
    super(aBase.getColDim(),aBase.getRowDim(),aBase);
  }
  public final double doubleValue(  final long aRow,  final long aCol){
    return this.getBase().doubleValue(aCol,aRow);
  }
  public final MatrixStore<N> getOriginal(){
    return this.getBase();
  }
  public final boolean isLowerLeftShaded(){
    return this.getBase().isUpperRightShaded();
  }
  public final boolean isUpperRightShaded(){
    return this.getBase().isLowerLeftShaded();
  }
}
