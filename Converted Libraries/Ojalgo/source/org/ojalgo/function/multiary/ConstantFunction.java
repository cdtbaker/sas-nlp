package org.ojalgo.function.multiary;
import java.math.BigDecimal;
import org.ojalgo.access.Access1D;
import org.ojalgo.matrix.store.BigDenseStore;
import org.ojalgo.matrix.store.ComplexDenseStore;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.matrix.store.PhysicalStore;
import org.ojalgo.matrix.store.PhysicalStore.Factory;
import org.ojalgo.matrix.store.PrimitiveDenseStore;
import org.ojalgo.matrix.store.ZeroStore;
import org.ojalgo.scalar.ComplexNumber;
public final class ConstantFunction<N extends Number> extends AbstractMultiary<N,ConstantFunction<N>> {
  public static ConstantFunction<BigDecimal> makeBig(  final int arity){
    return new ConstantFunction<BigDecimal>(arity,BigDenseStore.FACTORY,null);
  }
  public static ConstantFunction<BigDecimal> makeBig(  final int arity,  final Number constant){
    return new ConstantFunction<BigDecimal>(arity,BigDenseStore.FACTORY,constant);
  }
  public static ConstantFunction<ComplexNumber> makeComplex(  final int arity){
    return new ConstantFunction<ComplexNumber>(arity,ComplexDenseStore.FACTORY,null);
  }
  public static ConstantFunction<ComplexNumber> makeComplex(  final int arity,  final Number constant){
    return new ConstantFunction<ComplexNumber>(arity,ComplexDenseStore.FACTORY,constant);
  }
  public static ConstantFunction<Double> makePrimitive(  final int arity){
    return new ConstantFunction<Double>(arity,PrimitiveDenseStore.FACTORY,null);
  }
  public static ConstantFunction<Double> makePrimitive(  final int arity,  final Number constant){
    return new ConstantFunction<Double>(arity,PrimitiveDenseStore.FACTORY,constant);
  }
  private final int myArity;
  private final PhysicalStore.Factory<N,?> myFactory;
  @SuppressWarnings("unused") private ConstantFunction(){
    this(0,null,null);
  }
  ConstantFunction(  final int arity,  final PhysicalStore.Factory<N,?> factory,  final Number constant){
    super();
    myArity=arity;
    myFactory=factory;
    this.setConstant(constant);
  }
  public int arity(){
    return myArity;
  }
  public MatrixStore<N> getGradient(  final Access1D<?> arg){
    return new ZeroStore<>(this.factory(),this.arity(),1);
  }
  public MatrixStore<N> getHessian(  final Access1D<?> arg){
    return new ZeroStore<>(this.factory(),this.arity(),this.arity());
  }
  public N invoke(  final Access1D<?> arg){
    return this.getConstant();
  }
  @Override protected Factory<N,?> factory(){
    return myFactory;
  }
}
