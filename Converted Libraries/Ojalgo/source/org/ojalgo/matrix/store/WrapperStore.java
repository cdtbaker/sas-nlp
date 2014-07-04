package org.ojalgo.matrix.store;
import java.math.BigDecimal;
import org.ojalgo.ProgrammingError;
import org.ojalgo.access.Access2D;
import org.ojalgo.scalar.ComplexNumber;
import org.ojalgo.scalar.Scalar;
/** 
 * @author apete
 */
public final class WrapperStore<N extends Number> extends FactoryStore<N> {
public static interface Factory<N extends Number> {
    WrapperStore<N> make(    Access2D<?> access);
  }
  public static final Factory<BigDecimal> BIG=new Factory<BigDecimal>(){
    public WrapperStore<BigDecimal> make(    final Access2D<?> access){
      return WrapperStore.makeBig(access);
    }
  }
;
  public static final Factory<ComplexNumber> COMPLEX=new Factory<ComplexNumber>(){
    public WrapperStore<ComplexNumber> make(    final Access2D<?> access){
      return WrapperStore.makeComplex(access);
    }
  }
;
  public static final Factory<Double> PRIMITIVE=new Factory<Double>(){
    public WrapperStore<Double> make(    final Access2D<?> access){
      return WrapperStore.makePrimitive(access);
    }
  }
;
  public static WrapperStore<BigDecimal> makeBig(  final Access2D<?> access){
    return new WrapperStore<BigDecimal>(BigDenseStore.FACTORY,access);
  }
  public static WrapperStore<ComplexNumber> makeComplex(  final Access2D<?> access){
    return new WrapperStore<ComplexNumber>(ComplexDenseStore.FACTORY,access);
  }
  public static WrapperStore<Double> makePrimitive(  final Access2D<?> access){
    return new WrapperStore<Double>(PrimitiveDenseStore.FACTORY,access);
  }
  private final Access2D<?> myAccess;
  public WrapperStore(  final PhysicalStore.Factory<N,?> factory,  final Access2D<?> access){
    super((int)access.countRows(),(int)access.countColumns(),factory);
    myAccess=access;
  }
  @SuppressWarnings("unused") private WrapperStore(  final PhysicalStore.Factory<N,?> aFactory){
    this(aFactory,null);
    ProgrammingError.throwForIllegalInvocation();
  }
  public double doubleValue(  final long aRow,  final long aCol){
    return myAccess.doubleValue(aRow,aCol);
  }
  public N get(  final long aRow,  final long aCol){
    return this.factory().getNumber(myAccess.get(aRow,aCol));
  }
  public boolean isLowerLeftShaded(){
    return false;
  }
  public boolean isUpperRightShaded(){
    return false;
  }
  public Scalar<N> toScalar(  final int row,  final int column){
    return this.factory().toScalar(myAccess.get(row,column));
  }
}
