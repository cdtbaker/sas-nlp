package org.ojalgo.scalar;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Iterator;
import org.ojalgo.access.Access2D;
import org.ojalgo.access.Iterator1D;
import org.ojalgo.constant.PrimitiveMath;
import org.ojalgo.type.TypeUtils;
import org.ojalgo.type.context.NumberContext;
import org.ojalgo.type.context.NumberContext.Enforceable;
public final class Quaternion extends AbstractScalar<Quaternion> implements Enforceable<Quaternion>, Access2D<Double> {
  public static final Scalar.Factory<Quaternion> FACTORY=new Scalar.Factory<Quaternion>(){
    public Quaternion cast(    final double value){
      return new Quaternion(value);
    }
    public Quaternion cast(    final Number number){
      return TypeUtils.toQuaternion(number);
    }
    public Quaternion convert(    final double value){
      return new Quaternion(value);
    }
    public Quaternion convert(    final Number number){
      return TypeUtils.toQuaternion(number);
    }
    public Quaternion one(){
      return ONE;
    }
    public Quaternion zero(){
      return ZERO;
    }
  }
;
  public static final Quaternion I=new Quaternion(PrimitiveMath.ZERO,PrimitiveMath.ONE,PrimitiveMath.ZERO,PrimitiveMath.ZERO);
  public static final Quaternion J=new Quaternion(PrimitiveMath.ZERO,PrimitiveMath.ZERO,PrimitiveMath.ONE,PrimitiveMath.ZERO);
  public static final Quaternion K=new Quaternion(PrimitiveMath.ZERO,PrimitiveMath.ZERO,PrimitiveMath.ZERO,PrimitiveMath.ONE);
  public static final Quaternion NEG=new Quaternion(PrimitiveMath.NEG);
  public static final Quaternion ONE=new Quaternion(PrimitiveMath.ONE);
  public static final Quaternion ZERO=new Quaternion(PrimitiveMath.ZERO);
  public static boolean isAbsolute(  final Quaternion value){
    return value.isAbsolute();
  }
  public static boolean isInfinite(  final Quaternion value){
    return value.isInfinite();
  }
  public static boolean isNaN(  final Quaternion value){
    return value.isNaN();
  }
  public static boolean isPositive(  final Quaternion value){
    return value.isPositive();
  }
  public static boolean isReal(  final Quaternion value){
    return value.isReal();
  }
  public static boolean isZero(  final Quaternion value){
    return value.isZero();
  }
  public static Quaternion makeReal(  final double arg1){
    return new Quaternion(arg1);
  }
  public final double i;
  public final double j;
  public final double k;
  private final double myScalar;
  private final boolean myRealForSure;
  public Quaternion(  final double scalar){
    super();
    myScalar=scalar;
    myRealForSure=true;
    i=PrimitiveMath.ZERO;
    j=PrimitiveMath.ZERO;
    k=PrimitiveMath.ZERO;
  }
  public Quaternion(  final double scalar,  final double i){
    super();
    myScalar=scalar;
    myRealForSure=false;
    this.i=i;
    j=PrimitiveMath.ZERO;
    k=PrimitiveMath.ZERO;
  }
  public Quaternion(  final double scalar,  final double i,  final double j,  final double k){
    super();
    myScalar=scalar;
    myRealForSure=false;
    this.i=i;
    this.j=j;
    this.k=k;
  }
  Quaternion(){
    this(PrimitiveMath.ZERO);
  }
  public Quaternion add(  final double arg){
    final double tmpScalar=myScalar + arg;
    final double tmpI=i;
    final double tmpJ=j;
    final double tmpK=k;
    return new Quaternion(tmpScalar,tmpI,tmpJ,tmpK);
  }
  public Quaternion add(  final Quaternion arg){
    final double tmpScalar=myScalar + arg.scalar();
    final double tmpI=i + arg.i;
    final double tmpJ=j + arg.j;
    final double tmpK=k + arg.k;
    return new Quaternion(tmpScalar,tmpI,tmpJ,tmpK);
  }
  public int compareTo(  final Quaternion reference){
    int retVal=0;
    if ((retVal=Double.compare(this.norm(),reference.norm())) == 0) {
      if ((retVal=Double.compare(myScalar,reference.scalar())) == 0) {
        if ((retVal=Double.compare(i,reference.i)) == 0) {
          if ((retVal=Double.compare(j,reference.j)) == 0) {
            retVal=Double.compare(k,reference.k);
          }
        }
      }
    }
    return retVal;
  }
  public Quaternion conjugate(){
    final double tmpScalar=myScalar;
    final double tmpI=-i;
    final double tmpJ=-j;
    final double tmpK=-k;
    return new Quaternion(tmpScalar,tmpI,tmpJ,tmpK);
  }
  public long count(){
    return 16L;
  }
  public long countColumns(){
    return 4L;
  }
  public long countRows(){
    return 4L;
  }
  public Quaternion divide(  final double arg){
    final double tmpScalar=myScalar / arg;
    final double tmpI=i / arg;
    final double tmpJ=j / arg;
    final double tmpK=k / arg;
    return new Quaternion(tmpScalar,tmpI,tmpJ,tmpK);
  }
  /** 
 * Will calculate <code>this * reciprocal(arg)</code> which is <bold>not</bold> the same as
 * <code>reciprocal(arg) * this</code>.
 * @see org.ojalgo.scalar.Scalar#divide(java.lang.Number)
 */
  public Quaternion divide(  final Quaternion arg){
    final Quaternion tmpReciprocal=arg.invert();
    return this.multiply(tmpReciprocal);
  }
  @Override public double doubleValue(){
    return myScalar;
  }
  public double doubleValue(  final long index){
switch ((int)index) {
case 0:
      return myScalar;
case 1:
    return -i;
case 2:
  return -j;
case 3:
return -k;
case 4:
return i;
case 5:
return myScalar;
case 6:
return k;
case 7:
return -j;
case 8:
return j;
case 9:
return -k;
case 10:
return myScalar;
case 11:
return i;
case 12:
return k;
case 13:
return j;
case 14:
return -i;
case 15:
return myScalar;
default :
throw new ArrayIndexOutOfBoundsException();
}
}
public double doubleValue(final long row,final long column){
if (row == column) {
return myScalar;
}
 else {
return this.doubleValue(row + (column * 4L));
}
}
public Quaternion enforce(final NumberContext context){
final double tmpScalar=context.enforce(myScalar);
final double tmpI=context.enforce(i);
final double tmpJ=context.enforce(j);
final double tmpK=context.enforce(k);
return new Quaternion(tmpScalar,tmpI,tmpJ,tmpK);
}
@Override public boolean equals(final Object obj){
if (this == obj) {
return true;
}
if (obj == null) {
return false;
}
if (!(obj instanceof Quaternion)) {
return false;
}
final Quaternion other=(Quaternion)obj;
if (Double.doubleToLongBits(myScalar) != Double.doubleToLongBits(other.myScalar)) {
return false;
}
if (Double.doubleToLongBits(i) != Double.doubleToLongBits(other.i)) {
return false;
}
if (Double.doubleToLongBits(j) != Double.doubleToLongBits(other.j)) {
return false;
}
if (Double.doubleToLongBits(k) != Double.doubleToLongBits(other.k)) {
return false;
}
return true;
}
@Override public float floatValue(){
return (float)myScalar;
}
public Double get(final long index){
return this.doubleValue(index);
}
public Double get(final long row,final long column){
return this.doubleValue(row,column);
}
public int getColDim(){
return 4;
}
public Quaternion getNumber(){
return this;
}
public int getRowDim(){
return 4;
}
@Override public int hashCode(){
final int prime=31;
int result=1;
long temp;
temp=Double.doubleToLongBits(myScalar);
result=(prime * result) + (int)(temp ^ (temp >>> 32));
temp=Double.doubleToLongBits(i);
result=(prime * result) + (int)(temp ^ (temp >>> 32));
temp=Double.doubleToLongBits(j);
result=(prime * result) + (int)(temp ^ (temp >>> 32));
temp=Double.doubleToLongBits(k);
result=(prime * result) + (int)(temp ^ (temp >>> 32));
return result;
}
@Override public int intValue(){
return (int)myScalar;
}
public Quaternion invert(){
final Quaternion tmpConjugate=this.conjugate();
final double tmpSumOfSquares=this.calculateSumOfSquares();
return tmpConjugate.divide(tmpSumOfSquares);
}
public boolean isAbsolute(){
return this.isReal() && (this.scalar() >= PrimitiveMath.ZERO);
}
public boolean isInfinite(){
return Double.isInfinite(myScalar) || Double.isInfinite(i) || Double.isInfinite(j)|| Double.isInfinite(k)|| Double.isInfinite(this.norm());
}
public boolean isNaN(){
return Double.isNaN(myScalar) || Double.isNaN(i) || Double.isNaN(j)|| Double.isNaN(k);
}
public boolean isPositive(){
return this.isAbsolute() && !this.isZero();
}
public boolean isReal(){
return myRealForSure || (TypeUtils.isZero(i) && TypeUtils.isZero(j) && TypeUtils.isZero(k));
}
public boolean isZero(){
return TypeUtils.isZero(this.norm());
}
public Iterator<Double> iterator(){
return new Iterator1D<>(this);
}
@Override public long longValue(){
return (long)myScalar;
}
public Quaternion multiply(final double arg){
final double tmpScalar=myScalar * arg;
final double tmpI=i * arg;
final double tmpJ=j * arg;
final double tmpK=k * arg;
return new Quaternion(tmpScalar,tmpI,tmpJ,tmpK);
}
public Quaternion multiply(final Quaternion arg){
final double tmpScalar=(myScalar * arg.scalar()) - (i * arg.i) - (j * arg.j)- (k * arg.k);
final double tmpI=((myScalar * arg.i) + (i * arg.scalar()) + (j * arg.k)) - (k * arg.j);
final double tmpJ=((myScalar * arg.j) - (i * arg.k)) + (j * arg.scalar()) + (k * arg.i);
final double tmpK=(((myScalar * arg.k) + (i * arg.j)) - (j * arg.i)) + (k * arg.scalar());
return new Quaternion(tmpScalar,tmpI,tmpJ,tmpK);
}
public Quaternion negate(){
final double tmpScalar=-myScalar;
final double tmpI=-i;
final double tmpJ=-j;
final double tmpK=-k;
return new Quaternion(tmpScalar,tmpI,tmpJ,tmpK);
}
public double norm(){
return Math.sqrt(this.calculateSumOfSquares());
}
public double scalar(){
return myScalar;
}
public Quaternion signum(){
return this.versor();
}
public int size(){
return 16;
}
public Quaternion subtract(final double arg){
final double tmpScalar=myScalar - arg;
final double tmpI=i;
final double tmpJ=j;
final double tmpK=k;
return new Quaternion(tmpScalar,tmpI,tmpJ,tmpK);
}
public Quaternion subtract(final Quaternion arg){
final double tmpScalar=myScalar - arg.scalar();
final double tmpI=i - arg.i;
final double tmpJ=j - arg.j;
final double tmpK=k - arg.k;
return new Quaternion(tmpScalar,tmpI,tmpJ,tmpK);
}
public BigDecimal toBigDecimal(){
return new BigDecimal(myScalar,MathContext.DECIMAL64);
}
/** 
 * @see java.lang.Object#toString()
 */
@Override public String toString(){
final StringBuilder retVal=new StringBuilder("(");
retVal.append(Double.toString(myScalar));
if (i < PrimitiveMath.ZERO) {
retVal.append(" - ");
}
 else {
retVal.append(" + ");
}
retVal.append(Double.toString(Math.abs(i)));
retVal.append("i");
if (j < PrimitiveMath.ZERO) {
retVal.append(" - ");
}
 else {
retVal.append(" + ");
}
retVal.append(Double.toString(Math.abs(j)));
retVal.append("j");
if (k < PrimitiveMath.ZERO) {
retVal.append(" - ");
}
 else {
retVal.append(" + ");
}
retVal.append(Double.toString(Math.abs(k)));
retVal.append("k)");
return retVal.toString();
}
public String toString(final NumberContext context){
final StringBuilder retVal=new StringBuilder("(");
final BigDecimal tmpScalar=context.enforce(new BigDecimal(this.scalar(),MathContext.DECIMAL64));
final BigDecimal tmpI=context.enforce(new BigDecimal(i,MathContext.DECIMAL64));
final BigDecimal tmpJ=context.enforce(new BigDecimal(j,MathContext.DECIMAL64));
final BigDecimal tmpK=context.enforce(new BigDecimal(k,MathContext.DECIMAL64));
retVal.append(tmpScalar.toString());
if (tmpI.signum() < 0) {
retVal.append(" - ");
}
 else {
retVal.append(" + ");
}
retVal.append(tmpI.abs().toString());
retVal.append("i");
if (tmpJ.signum() < 0) {
retVal.append(" - ");
}
 else {
retVal.append(" + ");
}
retVal.append(tmpJ.abs().toString());
retVal.append("j");
if (tmpK.signum() < 0) {
retVal.append(" - ");
}
 else {
retVal.append(" + ");
}
retVal.append(tmpK.abs().toString());
retVal.append("k)");
return retVal.toString();
}
public double[] vector(){
return new double[]{i,j,k};
}
public Quaternion versor(){
return this.divide(this.norm());
}
private double calculateSumOfSquares(){
return (myScalar * myScalar) + (i * i) + (j * j)+ (k * k);
}
}