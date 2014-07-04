package org.ojalgo.matrix.store;
import org.ojalgo.ProgrammingError;
import org.ojalgo.scalar.Scalar;
/** 
 * A selection (re-ordering) of rows.
 * @author apete
 */
public final class RowsStore<N extends Number> extends SelectingStore<N> {
  private final int[] myRows;
  private final int myFirst;
  public RowsStore(  final int aFirst,  final int aLimit,  final MatrixStore<N> aBase){
    super(aLimit - aFirst,(int)aBase.countColumns(),aBase);
    myRows=null;
    myFirst=aFirst;
  }
  public RowsStore(  final MatrixStore<N> aBase,  final int... someRows){
    super(someRows.length,(int)aBase.countColumns(),aBase);
    myRows=someRows;
    myFirst=0;
  }
  @SuppressWarnings("unused") private RowsStore(  final MatrixStore<N> aBase){
    this(aBase,null);
    ProgrammingError.throwForIllegalInvocation();
  }
  /** 
 * @see org.ojalgo.matrix.store.MatrixStore#doubleValue(long,long)
 */
  public double doubleValue(  final long row,  final long column){
    if (myRows != null) {
      return this.getBase().doubleValue(myRows[(int)row],column);
    }
 else {
      return this.getBase().doubleValue(myFirst + row,column);
    }
  }
  public N get(  final long row,  final long column){
    if (myRows != null) {
      return this.getBase().get(myRows[(int)row],column);
    }
 else {
      return this.getBase().get(myFirst + row,column);
    }
  }
  public boolean isLowerLeftShaded(){
    return false;
  }
  public boolean isUpperRightShaded(){
    return false;
  }
  public Scalar<N> toScalar(  final int row,  final int column){
    if (myRows != null) {
      return this.getBase().toScalar(myRows[row],column);
    }
 else {
      return this.getBase().toScalar(myFirst + row,column);
    }
  }
}
