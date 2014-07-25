package org.ojalgo.matrix.store;
import org.ojalgo.ProgrammingError;
abstract class FactoryStore<N extends Number> extends AbstractStore<N> {
  private final PhysicalStore.Factory<N,?> myFactory;
  @SuppressWarnings("unused") private FactoryStore(  final int aRowDim,  final int aColDim){
    this(aRowDim,aColDim,null);
    ProgrammingError.throwForIllegalInvocation();
  }
  protected FactoryStore(  final int aRowDim,  final int aColDim,  final PhysicalStore.Factory<N,?> aFactory){
    super(aRowDim,aColDim);
    myFactory=aFactory;
  }
  public final PhysicalStore.Factory<N,?> factory(){
    return myFactory;
  }
}
