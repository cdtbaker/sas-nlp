package org.ojalgo.matrix.store;
import org.ojalgo.ProgrammingError;
import org.ojalgo.scalar.Scalar;
/** 
 * A selection (re-ordering) of columns.
 * @author apete
 */
public final class ColumnsStore<N extends Number> extends SelectingStore<N> {
  private final int[] myColumns;
  private final int myFirst;
  public ColumnsStore(  final int aFirst,  final int aLimit,  final MatrixStore<N> aBase){
    super((int)aBase.countRows(),aLimit - aFirst,aBase);
    myColumns=null;
    myFirst=aFirst;
  }
  public ColumnsStore(  final MatrixStore<N> aBase,  final int... someColumns){
    super((int)aBase.countRows(),someColumns.length,aBase);
    myColumns=someColumns;
    myFirst=0;
  }
  @SuppressWarnings("unused") private ColumnsStore(  final MatrixStore<N> aBase){
    this(aBase,null);
    ProgrammingError.throwForIllegalInvocation();
  }
  /** 
 * @see org.ojalgo.matrix.store.MatrixStore#doubleValue(long,long)
 */
  public double doubleValue(  final long row,  final long column){
    if (myColumns != null) {
      return this.getBase().doubleValue(row,myColumns[(int)column]);
    }
 else {
      return this.getBase().doubleValue(row,myFirst + column);
    }
  }
  public N get(  final long row,  final long column){
    if (myColumns != null) {
      return this.getBase().get(row,myColumns[(int)column]);
    }
 else {
      return this.getBase().get(row,myFirst + column);
    }
  }
  public boolean isLowerLeftShaded(){
    return false;
  }
  public boolean isUpperRightShaded(){
    return false;
  }
  public Scalar<N> toScalar(  final int row,  final int column){
    if (myColumns != null) {
      return this.getBase().toScalar(row,myColumns[column]);
    }
 else {
      return this.getBase().toScalar(row,myFirst + column);
    }
  }
}
