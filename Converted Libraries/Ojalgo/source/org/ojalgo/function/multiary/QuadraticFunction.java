package org.ojalgo.function.multiary;
import java.math.BigDecimal;
import org.ojalgo.access.Access1D;
import org.ojalgo.access.Access2D;
import org.ojalgo.matrix.store.BigDenseStore;
import org.ojalgo.matrix.store.ComplexDenseStore;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.matrix.store.PhysicalStore;
import org.ojalgo.matrix.store.PrimitiveDenseStore;
import org.ojalgo.scalar.ComplexNumber;
import org.ojalgo.scalar.Scalar;
/** 
 * [x]<sup>T</sup>[Q][x]
 * @author apete
 */
public final class QuadraticFunction<N extends Number> extends AbstractMultiary<N,QuadraticFunction<N>> implements MultiaryFunction.Quadratic<N> {
  public static QuadraticFunction<BigDecimal> makeBig(  final Access2D<? extends Number> factors){
    return new QuadraticFunction<BigDecimal>(BigDenseStore.FACTORY.copy(factors));
  }
  public static QuadraticFunction<BigDecimal> makeBig(  final int arity){
    return new QuadraticFunction<BigDecimal>(BigDenseStore.FACTORY.makeZero(arity,arity));
  }
  public static QuadraticFunction<ComplexNumber> makeComplex(  final Access2D<? extends Number> factors){
    return new QuadraticFunction<ComplexNumber>(ComplexDenseStore.FACTORY.copy(factors));
  }
  public static QuadraticFunction<ComplexNumber> makeComplex(  final int arity){
    return new QuadraticFunction<ComplexNumber>(ComplexDenseStore.FACTORY.makeZero(arity,arity));
  }
  public static QuadraticFunction<Double> makePrimitive(  final Access2D<? extends Number> factors){
    return new QuadraticFunction<Double>(PrimitiveDenseStore.FACTORY.copy(factors));
  }
  public static QuadraticFunction<Double> makePrimitive(  final int arity){
    return new QuadraticFunction<Double>(PrimitiveDenseStore.FACTORY.makeZero(arity,arity));
  }
  private final MatrixStore<N> myFactors;
  QuadraticFunction(  final MatrixStore<N> factors){
    super();
    myFactors=factors;
    if (myFactors.getRowDim() != myFactors.getColDim()) {
      throw new IllegalArgumentException("Must be sqaure!");
    }
  }
  public int arity(){
    return myFactors.getMinDim();
  }
  @Override public MatrixStore<N> getGradient(  final Access1D<?> arg){
    return this.getHessian(arg).multiplyRight(this.quadratic().factory().columns(arg));
  }
  @Override public MatrixStore<N> getHessian(  final Access1D<?> arg){
    return myFactors.builder().superimpose(0,0,myFactors.builder().conjugate().build()).build();
  }
  @Override public N invoke(  final Access1D<?> arg){
    Scalar<N> retVal=this.getScalarConstant();
    retVal=retVal.add(myFactors.multiplyRight(this.quadratic().factory().columns(arg)).multiplyLeft(this.quadratic().factory().rows(arg)).get(0,0));
    return retVal.getNumber();
  }
  public PhysicalStore<N> quadratic(){
    return (PhysicalStore<N>)myFactors;
  }
  @Override protected org.ojalgo.matrix.store.PhysicalStore.Factory<N,?> factory(){
    return myFactors.factory();
  }
}
