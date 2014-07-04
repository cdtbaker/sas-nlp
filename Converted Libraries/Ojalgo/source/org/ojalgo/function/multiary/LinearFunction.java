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
import org.ojalgo.scalar.Scalar;
/** 
 * [c]<sup>T</sup>[x]
 * @author apete
 */
public final class LinearFunction<N extends Number> extends AbstractMultiary<N,LinearFunction<N>> implements MultiaryFunction.Linear<N> {
  public static LinearFunction<BigDecimal> makeBig(  final Access1D<? extends Number> factors){
    return new LinearFunction<BigDecimal>(BigDenseStore.FACTORY.rows(factors));
  }
  public static LinearFunction<BigDecimal> makeBig(  final int arity){
    return new LinearFunction<BigDecimal>(BigDenseStore.FACTORY.makeZero(1,arity));
  }
  public static LinearFunction<ComplexNumber> makeComplex(  final Access1D<? extends Number> factors){
    return new LinearFunction<ComplexNumber>(ComplexDenseStore.FACTORY.rows(factors));
  }
  public static LinearFunction<ComplexNumber> makeComplex(  final int arity){
    return new LinearFunction<ComplexNumber>(ComplexDenseStore.FACTORY.makeZero(1,arity));
  }
  public static LinearFunction<Double> makePrimitive(  final Access1D<? extends Number> factors){
    return new LinearFunction<Double>(PrimitiveDenseStore.FACTORY.rows(factors));
  }
  public static LinearFunction<Double> makePrimitive(  final int arity){
    return new LinearFunction<Double>(PrimitiveDenseStore.FACTORY.makeZero(1,arity));
  }
  private final MatrixStore<N> myFactors;
  @SuppressWarnings("unused") private LinearFunction(){
    this(null);
  }
  LinearFunction(  final MatrixStore<N> factors){
    super();
    myFactors=factors;
    if (myFactors.getRowDim() != 1) {
      throw new IllegalArgumentException("Must be a row vector!");
    }
  }
  public int arity(){
    return myFactors.getColDim();
  }
  @Override public MatrixStore<N> getGradient(  final Access1D<?> arg){
    return myFactors.builder().transpose().build();
  }
  @Override public MatrixStore<N> getHessian(  final Access1D<?> arg){
    return new ZeroStore<N>(myFactors.factory(),this.arity(),this.arity());
  }
  @Override public N invoke(  final Access1D<?> arg){
    Scalar<N> retVal=this.getScalarConstant();
    retVal=retVal.add(myFactors.multiplyRight(myFactors.factory().columns(arg)).get(0,0));
    return retVal.getNumber();
  }
  public PhysicalStore<N> linear(){
    return (PhysicalStore<N>)myFactors;
  }
  @Override protected Factory<N,?> factory(){
    return myFactors.factory();
  }
}
