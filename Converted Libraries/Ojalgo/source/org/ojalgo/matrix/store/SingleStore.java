package org.ojalgo.matrix.store;
import java.math.BigDecimal;
import org.ojalgo.ProgrammingError;
import org.ojalgo.access.Access1D;
import org.ojalgo.scalar.ComplexNumber;
import org.ojalgo.scalar.Scalar;
public final class SingleStore<N extends Number> extends FactoryStore<N> {
public static interface Factory<N extends Number> {
    SingleStore<N> make(    N element);
  }
  public static final Factory<BigDecimal> BIG=new Factory<BigDecimal>(){
    public SingleStore<BigDecimal> make(    final BigDecimal element){
      return SingleStore.makeBig(element);
    }
  }
;
  public static final Factory<ComplexNumber> COMPLEX=new Factory<ComplexNumber>(){
    public SingleStore<ComplexNumber> make(    final ComplexNumber element){
      return SingleStore.makeComplex(element);
    }
  }
;
  public static final Factory<Double> PRIMITIVE=new Factory<Double>(){
    public SingleStore<Double> make(    final Double element){
      return SingleStore.makePrimitive(element);
    }
  }
;
  public static SingleStore<BigDecimal> makeBig(  final BigDecimal aSingleElement){
    return new SingleStore<BigDecimal>(BigDenseStore.FACTORY,aSingleElement);
  }
  public static SingleStore<ComplexNumber> makeComplex(  final ComplexNumber aSingleElement){
    return new SingleStore<ComplexNumber>(ComplexDenseStore.FACTORY,aSingleElement);
  }
  public static SingleStore<Double> makePrimitive(  final double aSingleElement){
    return new SingleStore<Double>(PrimitiveDenseStore.FACTORY,aSingleElement);
  }
  private final N myNumber;
  private final double myValue;
  public SingleStore(  final PhysicalStore.Factory<N,?> aFactory,  final N anElement){
    super(1,1,aFactory);
    myNumber=anElement;
    myValue=anElement.doubleValue();
  }
  @SuppressWarnings("unused") private SingleStore(  final PhysicalStore.Factory<N,?> aFactory){
    this(aFactory,aFactory.getStaticZero().getNumber());
    ProgrammingError.throwForIllegalInvocation();
  }
  @Override public PhysicalStore<N> conjugate(){
    final PhysicalStore<N> retVal=this.factory().makeZero(1,1);
    retVal.set(0,0,this.factory().toScalar(myNumber).conjugate().getNumber());
    return retVal;
  }
  @Override public PhysicalStore<N> copy(){
    final PhysicalStore<N> retVal=this.factory().makeZero(1,1);
    retVal.set(0,0,myNumber);
    return retVal;
  }
  @Override public double doubleValue(  final long anInd){
    return myValue;
  }
  public double doubleValue(  final long aRow,  final long aCol){
    return myValue;
  }
  public N get(  final long aRow,  final long aCol){
    return myNumber;
  }
  public boolean isLowerLeftShaded(){
    return true;
  }
  public boolean isUpperRightShaded(){
    return true;
  }
  @Override public MatrixStore<N> multiplyLeft(  final Access1D<N> leftMtrx){
    final int tmpRowDim=(int)(leftMtrx.count() / this.getRowDim());
    final int tmpColDim=this.getColDim();
    final PhysicalStore.Factory<N,?> tmpFactory=this.factory();
    final PhysicalStore<N> retVal=tmpFactory.makeZero(tmpRowDim,tmpColDim);
    retVal.fillMatching(leftMtrx,tmpFactory.function().multiply(),myNumber);
    return retVal;
  }
  @Override public MatrixStore<N> multiplyRight(  final Access1D<N> rightMtrx){
    final int tmpRowDim=this.getRowDim();
    final int tmpColDim=(int)(rightMtrx.count() / this.getColDim());
    final PhysicalStore.Factory<N,?> tmpFactory=this.factory();
    final PhysicalStore<N> retVal=tmpFactory.makeZero(tmpRowDim,tmpColDim);
    retVal.fillMatching(myNumber,tmpFactory.function().multiply(),rightMtrx);
    return retVal;
  }
  public Scalar<N> toScalar(  final int row,  final int column){
    return this.factory().toScalar(myNumber);
  }
  @Override public PhysicalStore<N> transpose(){
    return this.copy();
  }
}
