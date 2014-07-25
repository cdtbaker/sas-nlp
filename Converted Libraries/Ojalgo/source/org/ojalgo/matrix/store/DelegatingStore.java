package org.ojalgo.matrix.store;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import org.ojalgo.access.Access1D;
import org.ojalgo.concurrent.DaemonPoolExecutor;
abstract class DelegatingStore<N extends Number> extends LogicalStore<N> {
private static final class MultiplyLeft<N extends Number> implements Callable<MatrixStore<N>> {
    private Access1D<N> myLeftStore;
    private MatrixStore<N> myThisStore;
    public MultiplyLeft(    final MatrixStore<N> thisStore,    final Access1D<N> leftStore){
      super();
      myThisStore=thisStore;
      myLeftStore=leftStore;
    }
    @SuppressWarnings("unused") private MultiplyLeft(){
      this(null,null);
    }
    public MatrixStore<N> call() throws Exception {
      return myThisStore.multiplyLeft(myLeftStore);
    }
  }
private static final class MultiplyRight<N extends Number> implements Callable<MatrixStore<N>> {
    private Access1D<N> myRightStore;
    private MatrixStore<N> myThisStore;
    public MultiplyRight(    final MatrixStore<N> thisStore,    final Access1D<N> rightStore){
      super();
      myThisStore=thisStore;
      myRightStore=rightStore;
    }
    @SuppressWarnings("unused") private MultiplyRight(){
      this(null,null);
    }
    public MatrixStore<N> call() throws Exception {
      return myThisStore.multiplyRight(myRightStore);
    }
  }
  protected DelegatingStore(  final int rowsCount,  final int columnsCount,  final MatrixStore<N> base){
    super(rowsCount,columnsCount,base);
  }
  protected final Future<MatrixStore<N>> executeMultiplyLeftOnBase(  final Access1D<N> left){
    return DaemonPoolExecutor.INSTANCE.submit(new MultiplyLeft<N>(this.getBase(),left));
  }
  protected final Future<MatrixStore<N>> executeMultiplyRightOnBase(  final Access1D<N> right){
    return DaemonPoolExecutor.INSTANCE.submit(new MultiplyRight<N>(this.getBase(),right));
  }
}
