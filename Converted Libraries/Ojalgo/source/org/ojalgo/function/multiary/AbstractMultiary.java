package org.ojalgo.function.multiary;
import org.ojalgo.access.Access1D;
import org.ojalgo.matrix.store.PhysicalStore;
import org.ojalgo.scalar.Scalar;
abstract class AbstractMultiary<N extends Number,F extends AbstractMultiary<N,?>> implements MultiaryFunction<N>, MultiaryFunction.Constant<N,F> {
  private Scalar<N> myConstant=null;
  protected AbstractMultiary(){
    super();
  }
  public final F constant(  final Number constant){
    this.setConstant(constant);
    return (F)this;
  }
  /** 
 * @deprecated v35 Use {@link #arity()} instead
 */
  @Deprecated public final int dim(){
    return this.arity();
  }
  public final N getConstant(){
    return this.getScalarConstant().getNumber();
  }
  public final FirstOrderApproximation<N> getFirstOrderApproximation(  final Access1D<N> arg){
    return new FirstOrderApproximation<N>(this,arg);
  }
  public final SecondOrderApproximation<N> getSecondOrderApproximation(  final Access1D<N> arg){
    return new SecondOrderApproximation<N>(this,arg);
  }
  public final void setConstant(  final Number constant){
    myConstant=constant != null ? this.factory().scalar().convert(constant) : null;
  }
  protected abstract PhysicalStore.Factory<N,?> factory();
  protected final Scalar<N> getScalarConstant(){
    return myConstant != null ? myConstant : this.factory().scalar().zero();
  }
}
