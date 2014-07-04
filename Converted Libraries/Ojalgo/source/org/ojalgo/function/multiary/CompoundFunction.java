package org.ojalgo.function.multiary;
import java.math.BigDecimal;
import org.ojalgo.access.Access1D;
import org.ojalgo.access.Access2D;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.matrix.store.PhysicalStore;
import org.ojalgo.matrix.store.PhysicalStore.Factory;
import org.ojalgo.scalar.ComplexNumber;
import org.ojalgo.scalar.Scalar;
/** 
 * [x]<sup>T</sup>[Q][x] + [c]<sup>T</sup>[x] + b
 * @author apete
 */
public final class CompoundFunction<N extends Number> extends AbstractMultiary<N,CompoundFunction<N>> implements MultiaryFunction.Linear<N>, MultiaryFunction.Quadratic<N> {
  public static CompoundFunction<BigDecimal> makeBig(  final Access2D<? extends Number> quadraticFactors,  final Access1D<? extends Number> linearFactors){
    final QuadraticFunction<BigDecimal> tmpQuadratic=QuadraticFunction.makeBig(quadraticFactors);
    final LinearFunction<BigDecimal> tmpLinear=LinearFunction.makeBig(linearFactors);
    return new CompoundFunction<BigDecimal>(tmpQuadratic,tmpLinear);
  }
  public static CompoundFunction<BigDecimal> makeBig(  final int arity){
    final QuadraticFunction<BigDecimal> tmpQuadratic=QuadraticFunction.makeBig(arity);
    final LinearFunction<BigDecimal> tmpLinear=LinearFunction.makeBig(arity);
    return new CompoundFunction<BigDecimal>(tmpQuadratic,tmpLinear);
  }
  public static CompoundFunction<ComplexNumber> makeComplex(  final Access2D<? extends Number> quadraticFactors,  final Access1D<? extends Number> linearFactors){
    final QuadraticFunction<ComplexNumber> tmpQuadratic=QuadraticFunction.makeComplex(quadraticFactors);
    final LinearFunction<ComplexNumber> tmpLinear=LinearFunction.makeComplex(linearFactors);
    return new CompoundFunction<ComplexNumber>(tmpQuadratic,tmpLinear);
  }
  public static CompoundFunction<ComplexNumber> makeComplex(  final int arity){
    final QuadraticFunction<ComplexNumber> tmpQuadratic=QuadraticFunction.makeComplex(arity);
    final LinearFunction<ComplexNumber> tmpLinear=LinearFunction.makeComplex(arity);
    return new CompoundFunction<ComplexNumber>(tmpQuadratic,tmpLinear);
  }
  public static CompoundFunction<Double> makePrimitive(  final Access2D<? extends Number> quadraticFactors,  final Access1D<? extends Number> linearFactors){
    final QuadraticFunction<Double> tmpQuadratic=QuadraticFunction.makePrimitive(quadraticFactors);
    final LinearFunction<Double> tmpLinear=LinearFunction.makePrimitive(linearFactors);
    return new CompoundFunction<Double>(tmpQuadratic,tmpLinear);
  }
  public static CompoundFunction<Double> makePrimitive(  final int arity){
    final QuadraticFunction<Double> tmpQuadratic=QuadraticFunction.makePrimitive(arity);
    final LinearFunction<Double> tmpLinear=LinearFunction.makePrimitive(arity);
    return new CompoundFunction<Double>(tmpQuadratic,tmpLinear);
  }
  private final LinearFunction<N> myLinear;
  private final QuadraticFunction<N> myQuadratic;
  @SuppressWarnings("unused") private CompoundFunction(){
    this((QuadraticFunction<N>)null,(LinearFunction<N>)null);
  }
  CompoundFunction(  final QuadraticFunction<N> quadratic,  final LinearFunction<N> linear){
    super();
    myQuadratic=quadratic;
    myLinear=linear;
    if (myQuadratic.arity() != myLinear.arity()) {
      throw new IllegalArgumentException("Must have the same dim()!");
    }
  }
  public int arity(){
    return myLinear.arity();
  }
  @Override public MatrixStore<N> getGradient(  final Access1D<?> arg){
    return myQuadratic.getGradient(arg).builder().superimpose(0,0,myLinear.getGradient(arg)).build();
  }
  @Override public MatrixStore<N> getHessian(  final Access1D<?> arg){
    return myQuadratic.getHessian(arg);
  }
  @Override public N invoke(  final Access1D<?> arg){
    Scalar<N> retVal=this.getScalarConstant();
    retVal=retVal.add(myLinear.invoke(arg));
    retVal=retVal.add(myQuadratic.invoke(arg));
    return retVal.getNumber();
  }
  public PhysicalStore<N> linear(){
    return myLinear.linear();
  }
  public PhysicalStore<N> quadratic(){
    return myQuadratic.quadratic();
  }
  @Override protected Factory<N,?> factory(){
    return myLinear.factory();
  }
}
