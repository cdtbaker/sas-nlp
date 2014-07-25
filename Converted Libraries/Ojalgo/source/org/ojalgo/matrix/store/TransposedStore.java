package org.ojalgo.matrix.store;
import org.ojalgo.access.Access1D;
import org.ojalgo.scalar.Scalar;
public final class TransposedStore<N extends Number> extends TransjugatedStore<N> {
  public TransposedStore(  final MatrixStore<N> aBase){
    super(aBase);
  }
  @Override public PhysicalStore<N> copy(){
    return this.getBase().transpose();
  }
  public N get(  final long aRow,  final long aCol){
    return this.getBase().get(aCol,aRow);
  }
  /** 
 * @see org.ojalgo.matrix.store.MatrixStore#multiplyLeft(org.ojalgo.matrix.store.MatrixStore)
 */
  @Override public MatrixStore<N> multiplyLeft(  final Access1D<N> leftMtrx){
    MatrixStore<N> retVal;
    if (leftMtrx instanceof TransposedStore<?>) {
      retVal=this.getBase().multiplyRight(((TransposedStore<N>)leftMtrx).getOriginal());
      retVal=new TransposedStore<N>(retVal);
    }
 else {
      retVal=super.multiplyLeft(leftMtrx);
    }
    return retVal;
  }
  /** 
 * @see org.ojalgo.matrix.store.MatrixStore#multiplyRight(org.ojalgo.matrix.store.MatrixStore)
 */
  @Override public MatrixStore<N> multiplyRight(  final Access1D<N> rightMtrx){
    MatrixStore<N> retVal;
    if (rightMtrx instanceof TransposedStore<?>) {
      retVal=this.getBase().multiplyLeft(((TransposedStore<N>)rightMtrx).getOriginal());
      retVal=new TransposedStore<N>(retVal);
    }
 else {
      retVal=super.multiplyRight(rightMtrx);
    }
    return retVal;
  }
  public Scalar<N> toScalar(  final int row,  final int column){
    return this.getBase().toScalar(column,row);
  }
  @Override public PhysicalStore<N> transpose(){
    return this.getBase().copy();
  }
}
