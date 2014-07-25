package org.ojalgo.matrix.store;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.ojalgo.ProgrammingError;
import org.ojalgo.access.Access1D;
import org.ojalgo.scalar.Scalar;
/** 
 * A merger of two {@linkplain MatrixStore} instances by placing one store below the other. The two matrices must have
 * the same number of columns. The columns of the two matrices are logically merged to form new longer columns.
 * @author apete
 */
public final class AboveBelowStore<N extends Number> extends DelegatingStore<N> {
  private final MatrixStore<N> myLowerStore;
  private final int myRowSplit;
  public AboveBelowStore(  final MatrixStore<N> base,  final MatrixStore<N> aLowerStore){
    super((int)(base.countRows() + aLowerStore.countRows()),(int)base.countColumns(),base);
    myLowerStore=aLowerStore;
    myRowSplit=(int)base.countRows();
  }
  @SuppressWarnings("unused") private AboveBelowStore(  final MatrixStore<N> aBase){
    this(aBase,null);
    ProgrammingError.throwForIllegalInvocation();
  }
  /** 
 * @see org.ojalgo.matrix.store.MatrixStore#doubleValue(long,long)
 */
  public double doubleValue(  final long row,  final long column){
    return (row >= myRowSplit) ? myLowerStore.doubleValue(row - myRowSplit,column) : this.getBase().doubleValue(row,column);
  }
  public N get(  final long row,  final long column){
    return (row >= myRowSplit) ? myLowerStore.get(row - myRowSplit,column) : this.getBase().get(row,column);
  }
  public boolean isLowerLeftShaded(){
    return false;
  }
  public boolean isUpperRightShaded(){
    return this.getBase().isUpperRightShaded();
  }
  @Override public MatrixStore<N> multiplyRight(  final Access1D<N> rightMtrx){
    final Future<MatrixStore<N>> tmpBaseFuture=this.executeMultiplyRightOnBase(rightMtrx);
    final MatrixStore<N> tmpLower=myLowerStore.multiplyRight(rightMtrx);
    try {
      return new AboveBelowStore<N>(tmpBaseFuture.get(),tmpLower);
    }
 catch (    final InterruptedException ex) {
      return null;
    }
  }
  public Scalar<N> toScalar(  final int row,  final int column){
    return (row >= myRowSplit) ? myLowerStore.toScalar(row - myRowSplit,column) : this.getBase().toScalar(row,column);
  }
}
