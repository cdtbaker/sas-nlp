package org.ojalgo.function.multiary;
import org.ojalgo.access.Access1D;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.matrix.store.PhysicalStore.Factory;
public final class FirstOrderApproximation<N extends Number> extends ApproximateFunction<N> {
  private final LinearFunction<N> myDelegate;
  public FirstOrderApproximation(  final MultiaryFunction<N> function,  final Access1D<N> point){
    super(function,point);
    final MatrixStore<N> tmpGradient=function.getGradient(point).builder().transpose().build();
    myDelegate=new LinearFunction<N>(tmpGradient);
    myDelegate.setConstant(function.invoke(point));
  }
  public int arity(){
    return myDelegate.arity();
  }
  public int dim(){
    return myDelegate.arity();
  }
  @Override public boolean equals(  final Object obj){
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof FirstOrderApproximation)) {
      return false;
    }
    final FirstOrderApproximation<?> other=(FirstOrderApproximation<?>)obj;
    if (myDelegate == null) {
      if (other.myDelegate != null) {
        return false;
      }
    }
 else     if (!myDelegate.equals(other.myDelegate)) {
      return false;
    }
    return true;
  }
  public MatrixStore<N> getGradient(  final Access1D<?> arg){
    return myDelegate.getGradient(null);
  }
  public MatrixStore<N> getHessian(  final Access1D<?> arg){
    return myDelegate.getHessian(null);
  }
  @Override public int hashCode(){
    final int prime=31;
    int result=1;
    result=(prime * result) + ((myDelegate == null) ? 0 : myDelegate.hashCode());
    return result;
  }
  public N invoke(  final Access1D<?> arg){
    return myDelegate.invoke(this.shift(arg));
  }
  @Override public String toString(){
    return myDelegate.toString();
  }
  @Override protected Factory<N,?> factory(){
    return myDelegate.factory();
  }
}
