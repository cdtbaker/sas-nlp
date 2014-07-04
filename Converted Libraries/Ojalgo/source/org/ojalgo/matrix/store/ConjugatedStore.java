package org.ojalgo.matrix.store;
import org.ojalgo.access.Access1D;
import org.ojalgo.scalar.Scalar;
/** 
 * ConjugatedStore
 * @author apete
 */
public final class ConjugatedStore<N extends Number> extends TransjugatedStore<N> {
  public ConjugatedStore(  final MatrixStore<N> aBase){
    super(aBase);
  }
  @Override public PhysicalStore<N> conjugate(){
    return this.getBase().copy();
  }
  @Override public PhysicalStore<N> copy(){
    return this.getBase().conjugate();
  }
  public N get(  final long aRow,  final long aCol){
    return this.getBase().toScalar((int)aCol,(int)aRow).conjugate().getNumber();
  }
  @Override public MatrixStore<N> multiplyLeft(  final Access1D<N> leftMtrx){
    MatrixStore<N> retVal;
    if (leftMtrx instanceof ConjugatedStore<?>) {
      retVal=this.getBase().multiplyRight(((ConjugatedStore<N>)leftMtrx).getOriginal());
      retVal=new ConjugatedStore<N>(retVal);
    }
 else {
      retVal=super.multiplyLeft(leftMtrx);
    }
    return retVal;
  }
  @Override public MatrixStore<N> multiplyRight(  final Access1D<N> rightMtrx){
    MatrixStore<N> retVal;
    if (rightMtrx instanceof ConjugatedStore<?>) {
      retVal=this.getBase().multiplyLeft(((ConjugatedStore<N>)rightMtrx).getOriginal());
      retVal=new ConjugatedStore<N>(retVal);
    }
 else {
      retVal=super.multiplyRight(rightMtrx);
    }
    return retVal;
  }
  public Scalar<N> toScalar(  final int row,  final int column){
    return this.getBase().toScalar(column,row).conjugate();
  }
}
