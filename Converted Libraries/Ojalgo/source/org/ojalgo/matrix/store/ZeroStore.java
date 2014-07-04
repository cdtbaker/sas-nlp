package org.ojalgo.matrix.store;
import java.math.BigDecimal;
import org.ojalgo.ProgrammingError;
import org.ojalgo.access.Access1D;
import org.ojalgo.constant.PrimitiveMath;
import org.ojalgo.scalar.ComplexNumber;
import org.ojalgo.scalar.Scalar;
/** 
 * ZeroStore
 * @author apete
 */
public final class ZeroStore<N extends Number> extends FactoryStore<N> {
public static interface Factory<N extends Number> {
    ZeroStore<N> make(    int rows,    int columns);
  }
  public static final ZeroStore.Factory<Double> PRIMITIVE=new ZeroStore.Factory<Double>(){
    public ZeroStore<Double> make(    final int rows,    final int columns){
      return ZeroStore.makePrimitive(rows,columns);
    }
  }
;
  public static final ZeroStore.Factory<BigDecimal> BIG=new ZeroStore.Factory<BigDecimal>(){
    public ZeroStore<BigDecimal> make(    final int rows,    final int columns){
      return ZeroStore.makeBig(rows,columns);
    }
  }
;
  public static final ZeroStore.Factory<ComplexNumber> COMPLEX=new ZeroStore.Factory<ComplexNumber>(){
    public ZeroStore<ComplexNumber> make(    final int rows,    final int columns){
      return ZeroStore.makeComplex(rows,columns);
    }
  }
;
  public static ZeroStore<BigDecimal> makeBig(  final int rows,  final int columns){
    return new ZeroStore<>(BigDenseStore.FACTORY,rows,columns);
  }
  public static ZeroStore<ComplexNumber> makeComplex(  final int rows,  final int columns){
    return new ZeroStore<>(ComplexDenseStore.FACTORY,rows,columns);
  }
  public static ZeroStore<Double> makePrimitive(  final int rows,  final int columns){
    return new ZeroStore<>(PrimitiveDenseStore.FACTORY,rows,columns);
  }
  private final N myNumberZero;
  private final Scalar<N> myScalarZero;
  public ZeroStore(  final PhysicalStore.Factory<N,?> factory,  final int rows,  final int columns){
    super(rows,columns,factory);
    myScalarZero=factory.getStaticZero();
    myNumberZero=myScalarZero.getNumber();
  }
  @SuppressWarnings("unused") private ZeroStore(  final PhysicalStore.Factory<N,?> aFactory){
    this(aFactory,0,0);
    ProgrammingError.throwForIllegalInvocation();
  }
  @Override public PhysicalStore<N> conjugate(){
    return this.factory().makeZero(this.getColDim(),this.getRowDim());
  }
  @Override public PhysicalStore<N> copy(){
    return this.factory().makeZero(this.getRowDim(),this.getColDim());
  }
  @Override public double doubleValue(  final long anInd){
    return PrimitiveMath.ZERO;
  }
  public double doubleValue(  final long aRow,  final long aCol){
    return PrimitiveMath.ZERO;
  }
  public N get(  final long aRow,  final long aCol){
    return myNumberZero;
  }
  public boolean isLowerLeftShaded(){
    return true;
  }
  public boolean isUpperRightShaded(){
    return true;
  }
  @Override public ZeroStore<N> multiplyLeft(  final Access1D<N> leftMtrx){
    return new ZeroStore<N>(this.factory(),(int)(leftMtrx.count() / this.getRowDim()),this.getColDim());
  }
  @Override public ZeroStore<N> multiplyRight(  final Access1D<N> rightMtrx){
    return new ZeroStore<N>(this.factory(),this.getRowDim(),(int)(rightMtrx.count() / this.getColDim()));
  }
  public Scalar<N> toScalar(  final int row,  final int column){
    return myScalarZero;
  }
  @Override public PhysicalStore<N> transpose(){
    return this.factory().makeZero(this.getColDim(),this.getRowDim());
  }
}
