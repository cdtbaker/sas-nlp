package org.ojalgo.function.multiary;
import org.ojalgo.access.Access1D;
import org.ojalgo.matrix.store.PhysicalStore;
abstract class ApproximateFunction<N extends Number> implements MultiaryFunction<N> {
  private final Access1D<N> myPoint;
  protected ApproximateFunction(  final MultiaryFunction<N> function,  final Access1D<N> point){
    super();
    myPoint=point;
  }
  @Override public boolean equals(  final Object obj){
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof ApproximateFunction)) {
      return false;
    }
    final ApproximateFunction<?> other=(ApproximateFunction<?>)obj;
    if (myPoint == null) {
      if (other.myPoint != null) {
        return false;
      }
    }
 else     if (!myPoint.equals(other.myPoint)) {
      return false;
    }
    return true;
  }
  @Override public int hashCode(){
    final int prime=31;
    int result=1;
    result=(prime * result) + ((myPoint == null) ? 0 : myPoint.hashCode());
    return result;
  }
  protected abstract PhysicalStore.Factory<N,?> factory();
  protected PhysicalStore<N> shift(  final Access1D<?> arg){
    final PhysicalStore<N> retVal=this.factory().columns(arg);
    retVal.fillMatching(retVal,this.factory().function().subtract(),myPoint);
    return retVal;
  }
}
