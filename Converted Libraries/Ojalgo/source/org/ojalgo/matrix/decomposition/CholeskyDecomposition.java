package org.ojalgo.matrix.decomposition;
import java.math.BigDecimal;
import org.ojalgo.access.Access2D;
import org.ojalgo.array.SimpleArray;
import org.ojalgo.function.UnaryFunction;
import org.ojalgo.function.aggregator.AggregatorFunction;
import org.ojalgo.matrix.MatrixUtils;
import org.ojalgo.matrix.jama.JamaCholesky;
import org.ojalgo.matrix.store.BigDenseStore;
import org.ojalgo.matrix.store.ComplexDenseStore;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.matrix.store.PrimitiveDenseStore;
import org.ojalgo.scalar.ComplexNumber;
import org.ojalgo.type.context.NumberContext;
/** 
 * You create instances of (some subclass of) this class by calling one of the static factory methods:{@linkplain #makeBig()}, {@linkplain #makeComplex()}, {@linkplain #makePrimitive()} or {@linkplain #makeJama()}.
 * @author apete
 */
public abstract class CholeskyDecomposition<N extends Number> extends InPlaceDecomposition<N> implements Cholesky<N> {
static final class Big extends CholeskyDecomposition<BigDecimal> {
    Big(){
      super(BigDenseStore.FACTORY);
    }
  }
static final class Complex extends CholeskyDecomposition<ComplexNumber> {
    Complex(){
      super(ComplexDenseStore.FACTORY);
    }
  }
static final class Primitive extends CholeskyDecomposition<Double> {
    Primitive(){
      super(PrimitiveDenseStore.FACTORY);
    }
  }
  @SuppressWarnings("unchecked") public static final <N extends Number>Cholesky<N> make(  final Access2D<N> aTypical){
    final N tmpNumber=aTypical.get(0,0);
    if (tmpNumber instanceof BigDecimal) {
      return (Cholesky<N>)CholeskyDecomposition.makeBig();
    }
 else     if (tmpNumber instanceof ComplexNumber) {
      return (Cholesky<N>)CholeskyDecomposition.makeComplex();
    }
 else     if (tmpNumber instanceof Double) {
      if ((aTypical.getColDim() <= 32) || (aTypical.getColDim() >= 46340)) {
        return (Cholesky<N>)CholeskyDecomposition.makeJama();
      }
 else {
        return (Cholesky<N>)CholeskyDecomposition.makePrimitive();
      }
    }
 else {
      throw new IllegalArgumentException();
    }
  }
  public static final Cholesky<BigDecimal> makeBig(){
    return new CholeskyDecomposition.Big();
  }
  public static final Cholesky<ComplexNumber> makeComplex(){
    return new CholeskyDecomposition.Complex();
  }
  public static final Cholesky<Double> makeJama(){
    return new JamaCholesky();
  }
  public static final Cholesky<Double> makePrimitive(){
    return new CholeskyDecomposition.Primitive();
  }
  private boolean mySPD=false;
  protected CholeskyDecomposition(  final DecompositionStore.Factory<N,? extends DecompositionStore<N>> aFactory){
    super(aFactory);
  }
  public final N calculateDeterminant(  final Access2D<N> matrix){
    this.compute(matrix);
    return this.getDeterminant();
  }
  public final boolean compute(  final Access2D<?> aStore){
    return this.compute(aStore,false);
  }
  public final boolean compute(  final Access2D<?> matrix,  final boolean checkHermitian){
    this.reset();
    final DecompositionStore<N> tmpInPlace=this.setInPlace(matrix);
    final int tmpRowDim=this.getRowDim();
    final int tmpColDim=this.getColDim();
    final int tmpMinDim=Math.min(tmpRowDim,tmpColDim);
    boolean tmpPositiveDefinite=tmpRowDim == tmpColDim;
    final SimpleArray<N> tmpMultipliers=this.makeArray(tmpRowDim);
    if (tmpPositiveDefinite && checkHermitian) {
      tmpPositiveDefinite&=MatrixUtils.isHermitian((Access2D<?>)tmpInPlace);
    }
    final UnaryFunction<N> tmpSqrtFunc=this.getFunctionSet().sqrt();
    for (int ij=0; tmpPositiveDefinite && (ij < tmpMinDim); ij++) {
      if (tmpInPlace.isPositive(ij,ij)) {
        tmpInPlace.modifyOne(ij,ij,tmpSqrtFunc);
        tmpInPlace.divideAndCopyColumn(ij,ij,tmpMultipliers);
        tmpInPlace.applyCholesky(ij,tmpMultipliers);
      }
 else {
        tmpPositiveDefinite=false;
      }
    }
    return this.computed(mySPD=tmpPositiveDefinite);
  }
  public final boolean equals(  final MatrixStore<N> aStore,  final NumberContext context){
    return MatrixUtils.equals(aStore,this,context);
  }
  public N getDeterminant(){
    final AggregatorFunction<N> tmpAggrFunc=this.getAggregatorCollection().product2();
    this.getInPlace().visitDiagonal(0,0,tmpAggrFunc);
    return tmpAggrFunc.getNumber();
  }
  @Override public final MatrixStore<N> getInverse(){
    return this.invert(this.makeEye(this.getColDim(),this.getRowDim()));
  }
  @Override public final MatrixStore<N> getInverse(  final DecompositionStore<N> preallocated){
    preallocated.fillAll(this.getStaticZero());
    preallocated.fillDiagonal(0,0,this.getStaticOne());
    return this.invert(preallocated);
  }
  public MatrixStore<N> getL(){
    return this.getInPlace().builder().triangular(false,false).build();
  }
  public final boolean isFullSize(){
    return true;
  }
  public boolean isSolvable(){
    return this.isComputed() && mySPD;
  }
  public boolean isSPD(){
    return mySPD;
  }
  public MatrixStore<N> reconstruct(){
    return MatrixUtils.reconstruct(this);
  }
  @Override public void reset(){
    super.reset();
    mySPD=false;
  }
  /** 
 * Solves [this][X] = [aRHS] by first solving
 * <pre>
 * [L][Y] = [aRHS]
 * </pre>
 * and then
 * <pre>
 * [U][X] = [Y]
 * </pre>
 * .
 * @param rhs The right hand side
 * @return [X] The solution will be written to "preallocated" and then returned.
 * @see org.ojalgo.matrix.decomposition.AbstractDecomposition#solve(Access2D,org.ojalgo.matrix.decomposition.DecompositionStore)
 */
  @Override public final MatrixStore<N> solve(  final Access2D<N> rhs,  final DecompositionStore<N> preallocated){
    preallocated.fillMatching(rhs);
    final DecompositionStore<N> tmpBody=this.getInPlace();
    preallocated.substituteForwards(tmpBody,false,false);
    preallocated.substituteBackwards(tmpBody,true);
    return preallocated;
  }
  private final MatrixStore<N> invert(  final DecompositionStore<N> retVal){
    final DecompositionStore<N> tmpBody=this.getInPlace();
    retVal.substituteForwards(tmpBody,false,true);
    retVal.substituteBackwards(tmpBody,true);
    return retVal;
  }
}
