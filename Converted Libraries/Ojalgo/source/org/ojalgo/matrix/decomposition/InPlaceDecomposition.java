package org.ojalgo.matrix.decomposition;
import org.ojalgo.access.Access2D;
import org.ojalgo.matrix.store.MatrixStore;
abstract class InPlaceDecomposition<N extends Number> extends AbstractDecomposition<N> {
  private int myColDim;
  private DecompositionStore<N> myInPlace;
  private int myRowDim;
  protected InPlaceDecomposition(  final DecompositionStore.Factory<N,? extends DecompositionStore<N>> aFactory){
    super(aFactory);
  }
  public MatrixStore<N> getInverse(){
    throw new UnsupportedOperationException();
  }
  public MatrixStore<N> getInverse(  final DecompositionStore<N> preallocated){
    throw new UnsupportedOperationException();
  }
  public DecompositionStore<N> preallocate(  final Access2D<N> templateBody,  final Access2D<N> templateRHS){
    return this.makeZero((int)templateRHS.countRows(),(int)templateRHS.countColumns());
  }
  public final MatrixStore<N> solve(  final Access2D<N> rhs){
    return this.solve(rhs,this.preallocate(myInPlace,rhs));
  }
  public MatrixStore<N> solve(  final Access2D<N> rhs,  final DecompositionStore<N> preallocated){
    throw new UnsupportedOperationException();
  }
  protected final int getColDim(){
    return myColDim;
  }
  protected final DecompositionStore<N> getInPlace(){
    return myInPlace;
  }
  protected final int getMinDim(){
    return Math.min(myRowDim,myColDim);
  }
  protected final int getRowDim(){
    return myRowDim;
  }
  final DecompositionStore<N> setInPlace(  final Access2D<?> aStore){
    final int tmpRowDim=aStore.getRowDim();
    final int tmpColDim=aStore.getColDim();
    if ((myInPlace != null) && (myRowDim == tmpRowDim) && (myColDim == tmpColDim)) {
      myInPlace.fillMatching(aStore);
    }
 else {
      myInPlace=this.copy(aStore);
      myRowDim=tmpRowDim;
      myColDim=tmpColDim;
    }
    this.aspectRatioNormal(tmpRowDim >= tmpColDim);
    this.computed(false);
    return myInPlace;
  }
}
