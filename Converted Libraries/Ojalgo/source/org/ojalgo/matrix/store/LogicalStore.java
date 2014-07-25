package org.ojalgo.matrix.store;
import org.ojalgo.ProgrammingError;
/** 
 * Logical stores are (intended to be) immutable. Therefore LogicalStore
 * subclasses should be made .
 * @author apete
 */
abstract class LogicalStore<N extends Number> extends AbstractStore<N> {
  private MatrixStore<N> myBase;
  @SuppressWarnings("unused") private LogicalStore(  final int aRowDim,  final int aColDim){
    this(aRowDim,aColDim,null);
    ProgrammingError.throwForIllegalInvocation();
  }
  protected LogicalStore(  final int rowCount,  final int columnCount,  final MatrixStore<N> base){
    super(rowCount,columnCount);
    myBase=base;
    if (myBase == null) {
      throw new IllegalArgumentException(this.getClass().getName() + " cannot have a null 'base'!");
    }
  }
  public final PhysicalStore.Factory<N,?> factory(){
    return myBase.factory();
  }
  protected final MatrixStore<N> getBase(){
    return myBase;
  }
}
