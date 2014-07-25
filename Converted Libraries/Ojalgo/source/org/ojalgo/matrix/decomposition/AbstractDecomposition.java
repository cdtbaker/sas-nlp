package org.ojalgo.matrix.decomposition;
import org.ojalgo.OjAlgoUtils;
import org.ojalgo.access.Access2D;
import org.ojalgo.access.AccessUtils;
import org.ojalgo.array.SimpleArray;
import org.ojalgo.function.FunctionSet;
import org.ojalgo.function.aggregator.AggregatorCollection;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.matrix.store.WrapperStore;
import org.ojalgo.matrix.transformation.Householder;
import org.ojalgo.matrix.transformation.Rotation;
import org.ojalgo.scalar.Scalar;
import org.ojalgo.type.context.NumberContext;
/** 
 * AbstractDecomposition
 * @author apete
 */
abstract class AbstractDecomposition<N extends Number> implements MatrixDecomposition<N> {
  private boolean myAspectRatioNormal=true;
  private boolean myComputed=false;
  private final DecompositionStore.Factory<N,? extends DecompositionStore<N>> myFactory;
  @SuppressWarnings("unused") private AbstractDecomposition(){
    this(null);
  }
  protected AbstractDecomposition(  final DecompositionStore.Factory<N,? extends DecompositionStore<N>> aFactory){
    super();
    myFactory=aFactory;
  }
  public final boolean equals(  final MatrixDecomposition<N> other,  final NumberContext context){
    return AccessUtils.equals(this.reconstruct(),other.reconstruct(),context);
  }
  @SuppressWarnings("unchecked") @Override public boolean equals(  final Object someObj){
    if (someObj instanceof MatrixStore) {
      return this.equals((MatrixStore<N>)someObj,NumberContext.getGeneral(6));
    }
 else {
      return super.equals(someObj);
    }
  }
  public final MatrixStore<N> invert(  final MatrixStore<N> original){
    this.compute(original);
    return this.getInverse();
  }
  public final MatrixStore<N> invert(  final MatrixStore<N> original,  final DecompositionStore<N> preallocated){
    this.compute(original);
    return this.getInverse(preallocated);
  }
  public boolean isAspectRatioNormal(){
    return myAspectRatioNormal;
  }
  public final boolean isComputed(){
    return myComputed;
  }
  public final DecompositionStore<N> preallocate(  final Access2D<N> template){
    return this.preallocate(template,template);
  }
  public void reset(){
    myAspectRatioNormal=true;
    myComputed=false;
  }
  public final MatrixStore<N> solve(  final Access2D<N> body,  final Access2D<N> rhs){
    this.compute(body);
    return this.solve(rhs);
  }
  public final MatrixStore<N> solve(  final Access2D<N> body,  final Access2D<N> rhs,  final DecompositionStore<N> preallocated){
    this.compute(body);
    return this.solve(rhs,preallocated);
  }
  protected final boolean aspectRatioNormal(  final boolean aFlag){
    return (myAspectRatioNormal=aFlag);
  }
  protected final boolean computed(  final boolean aState){
    return (myComputed=aState);
  }
  protected final DecompositionStore<N> copy(  final Access2D<?> aSource){
    return myFactory.copy(aSource);
  }
  protected final AggregatorCollection<N> getAggregatorCollection(){
    return myFactory.aggregator();
  }
  protected final FunctionSet<N> getFunctionSet(){
    return myFactory.function();
  }
  protected final int getMaxDimToFitInCache(){
    return OjAlgoUtils.ENVIRONMENT.getCacheDim2D(8L) / 3;
  }
  protected final N getStaticOne(){
    return myFactory.getStaticOne().getNumber();
  }
  protected final N getStaticZero(){
    return myFactory.getStaticZero().getNumber();
  }
  protected final SimpleArray<N> makeArray(  final int aLength){
    return myFactory.makeArray(aLength);
  }
  protected final DecompositionStore<N> makeEye(  final int aRowDim,  final int aColDim){
    return myFactory.makeEye(aRowDim,aColDim);
  }
  protected final Householder<N> makeHouseholder(  final int aDim){
    return myFactory.makeHouseholder(aDim);
  }
  protected final Rotation<N> makeRotation(  final int aLow,  final int aHigh,  final double aCos,  final double aSin){
    return myFactory.makeRotation(aLow,aHigh,aCos,aSin);
  }
  protected final Rotation<N> makeRotation(  final int aLow,  final int aHigh,  final N aCos,  final N aSin){
    return myFactory.makeRotation(aLow,aHigh,aCos,aSin);
  }
  protected final DecompositionStore<N> makeZero(  final int aRowDim,  final int aColDim){
    return myFactory.makeZero(aRowDim,aColDim);
  }
  protected final Scalar.Factory<N> scalar(){
    return myFactory.scalar();
  }
  protected final MatrixStore<N> wrap(  final Access2D<?> aSource){
    return new WrapperStore<N>(myFactory,aSource);
  }
}
