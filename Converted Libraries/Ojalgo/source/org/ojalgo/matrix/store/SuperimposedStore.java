package org.ojalgo.matrix.store;
import org.ojalgo.ProgrammingError;
import org.ojalgo.scalar.Scalar;
/** 
 * SuperimposedStore
 * @author apete
 */
public final class SuperimposedStore<N extends Number> extends DelegatingStore<N> {
  private final int myColFirst;
  private final int myColLimit;
  private final MatrixStore<N> myDiff;
  private final int myRowFirst;
  private final int myRowLimit;
  public SuperimposedStore(  final MatrixStore<N> base,  final int row,  final int column,  final MatrixStore<N> diff){
    super((int)base.countRows(),(int)base.countColumns(),base);
    myRowFirst=row;
    myColFirst=column;
    final int tmpDiffRowDim=(int)diff.countRows();
    final int tmpDiffColDim=(int)diff.countColumns();
    myRowLimit=row + tmpDiffRowDim;
    myColLimit=column + tmpDiffColDim;
    myDiff=diff;
  }
  @SuppressWarnings("unused") private SuperimposedStore(  final int rowsCount,  final int columnsCount,  final MatrixStore<N> base){
    this(base,0,0,(MatrixStore<N>)null);
    ProgrammingError.throwForIllegalInvocation();
  }
  SuperimposedStore(  final MatrixStore<N> base,  final MatrixStore<N> diff){
    this(base,0,0,diff);
  }
  /** 
 * @see org.ojalgo.matrix.store.MatrixStore#doubleValue(long,long)
 */
  public double doubleValue(  final long row,  final long column){
    double retVal=this.getBase().doubleValue(row,column);
    if (this.isCovered((int)row,(int)column)) {
      retVal+=myDiff.doubleValue(row - myRowFirst,column - myColFirst);
    }
    return retVal;
  }
  public N get(  final long row,  final long column){
    N retVal=this.getBase().get(row,column);
    if (this.isCovered((int)row,(int)column)) {
      retVal=myDiff.toScalar((int)row - myRowFirst,(int)column - myColFirst).add(retVal).getNumber();
    }
    return retVal;
  }
  public boolean isLowerLeftShaded(){
    return false;
  }
  public boolean isUpperRightShaded(){
    return false;
  }
  public Scalar<N> toScalar(  final int row,  final int column){
    Scalar<N> retVal=this.getBase().toScalar(row,column);
    if (this.isCovered(row,column)) {
      retVal=retVal.add(myDiff.get(row - myRowFirst,column - myColFirst));
    }
    return retVal;
  }
  private final boolean isCovered(  final int row,  final int column){
    return (myRowFirst <= row) && (myColFirst <= column) && (row < myRowLimit)&& (column < myColLimit);
  }
}
