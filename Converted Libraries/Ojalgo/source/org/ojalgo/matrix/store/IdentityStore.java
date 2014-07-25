package org.ojalgo.matrix.store;
import java.math.BigDecimal;
import org.ojalgo.ProgrammingError;
import org.ojalgo.access.Access1D;
import org.ojalgo.constant.PrimitiveMath;
import org.ojalgo.scalar.ComplexNumber;
import org.ojalgo.scalar.Scalar;
/** 
 * IdentityStore
 * @author apete
 */
public final class IdentityStore<N extends Number> extends FactoryStore<N> {
public static interface Factory<N extends Number> {
    IdentityStore<N> make(    int dimension);
  }
  public static final Factory<BigDecimal> BIG=new Factory<BigDecimal>(){
    public IdentityStore<BigDecimal> make(    final int dimension){
      return IdentityStore.makeBig(dimension);
    }
  }
;
  public static final Factory<ComplexNumber> COMPLEX=new Factory<ComplexNumber>(){
    public IdentityStore<ComplexNumber> make(    final int dimension){
      return IdentityStore.makeComplex(dimension);
    }
  }
;
  public static final Factory<Double> PRIMITIVE=new Factory<Double>(){
    public IdentityStore<Double> make(    final int dimension){
      return IdentityStore.makePrimitive(dimension);
    }
  }
;
  public static IdentityStore<BigDecimal> makeBig(  final int dimension){
    return new IdentityStore<BigDecimal>(BigDenseStore.FACTORY,dimension);
  }
  public static IdentityStore<ComplexNumber> makeComplex(  final int dimension){
    return new IdentityStore<ComplexNumber>(ComplexDenseStore.FACTORY,dimension);
  }
  public static IdentityStore<Double> makePrimitive(  final int dimension){
    return new IdentityStore<Double>(PrimitiveDenseStore.FACTORY,dimension);
  }
  private final int myDim;
  public IdentityStore(  final PhysicalStore.Factory<N,?> aFactory,  final int dimension){
    super(dimension,dimension,aFactory);
    myDim=dimension;
  }
  @SuppressWarnings("unused") private IdentityStore(  final PhysicalStore.Factory<N,?> aFactory){
    this(aFactory,0);
    ProgrammingError.throwForIllegalInvocation();
  }
  @Override public PhysicalStore<N> conjugate(){
    return this.factory().makeEye(myDim,myDim);
  }
  @Override public PhysicalStore<N> copy(){
    return this.factory().makeEye(myDim,myDim);
  }
  public double doubleValue(  final long aRow,  final long aCol){
    if (aRow == aCol) {
      return PrimitiveMath.ONE;
    }
 else {
      return PrimitiveMath.ZERO;
    }
  }
  public N get(  final long aRow,  final long aCol){
    if (aRow == aCol) {
      return this.factory().getStaticOne().getNumber();
    }
 else {
      return this.factory().getStaticZero().getNumber();
    }
  }
  public boolean isLowerLeftShaded(){
    return true;
  }
  public boolean isUpperRightShaded(){
    return true;
  }
  @Override public MatrixStore<N> multiplyLeft(  final Access1D<N> leftMtrx){
    if (leftMtrx.count() == this.getRowDim()) {
      return this.factory().rows(leftMtrx);
    }
 else     if (leftMtrx instanceof MatrixStore<?>) {
      return ((MatrixStore<N>)leftMtrx).copy();
    }
 else {
      return super.multiplyLeft(leftMtrx);
    }
  }
  @Override public MatrixStore<N> multiplyRight(  final Access1D<N> rightMtrx){
    if (this.getColDim() == rightMtrx.count()) {
      return this.factory().columns(rightMtrx);
    }
 else     if (rightMtrx instanceof MatrixStore<?>) {
      return ((MatrixStore<N>)rightMtrx).copy();
    }
 else {
      return super.multiplyRight(rightMtrx);
    }
  }
  public Scalar<N> toScalar(  final int row,  final int column){
    if (row == column) {
      return this.factory().getStaticOne();
    }
 else {
      return this.factory().getStaticZero();
    }
  }
  @Override public PhysicalStore<N> transpose(){
    return this.factory().makeEye(myDim,myDim);
  }
}
