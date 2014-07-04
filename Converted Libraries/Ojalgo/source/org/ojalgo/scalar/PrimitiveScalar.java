package org.ojalgo.scalar;
import java.math.BigDecimal;
import java.math.MathContext;
import org.ojalgo.constant.PrimitiveMath;
import org.ojalgo.type.TypeUtils;
import org.ojalgo.type.context.NumberContext;
import org.ojalgo.type.context.NumberContext.Enforceable;
public final class PrimitiveScalar extends AbstractScalar<Double> implements Enforceable<PrimitiveScalar> {
  public static final Scalar.Factory<Double> FACTORY=new Scalar.Factory<Double>(){
    public Double cast(    final double value){
      return value;
    }
    public Double cast(    final Number number){
      return number.doubleValue();
    }
    public PrimitiveScalar convert(    final double value){
      return new PrimitiveScalar(value);
    }
    public PrimitiveScalar convert(    final Number number){
      return new PrimitiveScalar(number.doubleValue());
    }
    public PrimitiveScalar one(){
      return ONE;
    }
    public PrimitiveScalar zero(){
      return ZERO;
    }
  }
;
  public static final boolean IS_REAL=true;
  public static final PrimitiveScalar NaN=new PrimitiveScalar(PrimitiveMath.NaN);
  public static final PrimitiveScalar NEGATIVE_INFINITY=new PrimitiveScalar(PrimitiveMath.NEGATIVE_INFINITY);
  public static final PrimitiveScalar ONE=new PrimitiveScalar(PrimitiveMath.ONE);
  public static final PrimitiveScalar POSITIVE_INFINITY=new PrimitiveScalar(PrimitiveMath.POSITIVE_INFINITY);
  public static final PrimitiveScalar ZERO=new PrimitiveScalar(PrimitiveMath.ZERO);
  public static boolean isAbsolute(  final double value){
    return value >= PrimitiveMath.ZERO;
  }
  public static boolean isInfinite(  final double value){
    return Double.isInfinite(value);
  }
  public static boolean isNaN(  final double value){
    return Double.isNaN(value);
  }
  public static boolean isPositive(  final double value){
    return value > PrimitiveMath.ZERO;
  }
  public static boolean isZero(  final double value){
    return TypeUtils.isZero(value);
  }
  private final double myValue;
  public PrimitiveScalar(  final double aVal){
    super();
    myValue=aVal;
  }
  public PrimitiveScalar(  final Number aNmbr){
    super();
    myValue=aNmbr.doubleValue();
  }
  @SuppressWarnings("unused") private PrimitiveScalar(){
    super();
    myValue=PrimitiveMath.ZERO;
  }
  public Scalar<Double> add(  final double arg){
    return new PrimitiveScalar(myValue + arg);
  }
  public PrimitiveScalar add(  final Double arg){
    return new PrimitiveScalar(myValue + arg);
  }
  public int compareTo(  final Double reference){
    return Double.compare(myValue,reference);
  }
  public PrimitiveScalar conjugate(){
    return this;
  }
  public PrimitiveScalar divide(  final double arg){
    return new PrimitiveScalar(myValue / arg);
  }
  public PrimitiveScalar divide(  final Double arg){
    return new PrimitiveScalar(myValue / arg);
  }
  @Override public double doubleValue(){
    return myValue;
  }
  public PrimitiveScalar enforce(  final NumberContext context){
    return new PrimitiveScalar(context.enforce(myValue));
  }
  @Override public boolean equals(  final Object obj){
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof Number)) {
      return false;
    }
    final double other=((Number)obj).doubleValue();
    if (Double.doubleToLongBits(myValue) != Double.doubleToLongBits(other)) {
      return false;
    }
    return true;
  }
  @Override public float floatValue(){
    return (float)myValue;
  }
  public Double getNumber(){
    return myValue;
  }
  @Override public int hashCode(){
    final int prime=31;
    int result=1;
    long temp;
    temp=Double.doubleToLongBits(myValue);
    result=(prime * result) + (int)(temp ^ (temp >>> 32));
    return result;
  }
  @Override public int intValue(){
    return (int)myValue;
  }
  public PrimitiveScalar invert(){
    return new PrimitiveScalar(PrimitiveMath.ONE / myValue);
  }
  public boolean isAbsolute(){
    return PrimitiveScalar.isAbsolute(myValue);
  }
  public boolean isInfinite(){
    return PrimitiveScalar.isInfinite(myValue);
  }
  public boolean isNaN(){
    return PrimitiveScalar.isNaN(myValue);
  }
  public boolean isPositive(){
    return PrimitiveScalar.isPositive(myValue);
  }
  public boolean isReal(){
    return PrimitiveScalar.IS_REAL;
  }
  public boolean isZero(){
    return PrimitiveScalar.isZero(myValue);
  }
  @Override public long longValue(){
    return (long)myValue;
  }
  public PrimitiveScalar multiply(  final double arg){
    return new PrimitiveScalar(myValue * arg);
  }
  public PrimitiveScalar multiply(  final Double arg){
    return new PrimitiveScalar(myValue * arg);
  }
  public PrimitiveScalar negate(){
    return new PrimitiveScalar(-myValue);
  }
  public double norm(){
    return Math.abs(myValue);
  }
  public PrimitiveScalar signum(){
    return new PrimitiveScalar(Math.signum(myValue));
  }
  public PrimitiveScalar subtract(  final double arg){
    return new PrimitiveScalar(myValue - arg);
  }
  public PrimitiveScalar subtract(  final Double arg){
    return new PrimitiveScalar(myValue - arg);
  }
  public BigDecimal toBigDecimal(){
    return new BigDecimal(myValue,MathContext.DECIMAL64);
  }
  @Override public String toString(){
    return Double.toString(myValue);
  }
  public String toString(  final NumberContext context){
    return context.enforce(this.toBigDecimal()).toString();
  }
}
