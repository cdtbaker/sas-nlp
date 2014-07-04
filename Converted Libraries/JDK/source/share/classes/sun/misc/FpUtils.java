package sun.misc;
import sun.misc.FloatConsts;
import sun.misc.DoubleConsts;
/** 
 * The class {@code FpUtils} contains static utility methods for
 * manipulating and inspecting {@code float} and{@code double} floating-point numbers.  These methods include
 * functionality recommended or required by the IEEE 754
 * floating-point standard.
 * @author Joseph D. Darcy
 */
public class FpUtils {
  /** 
 * Don't let anyone instantiate this class.
 */
  private FpUtils(){
  }
  static double twoToTheDoubleScaleUp=powerOfTwoD(512);
  static double twoToTheDoubleScaleDown=powerOfTwoD(-512);
  /** 
 * Returns unbiased exponent of a {@code double}.
 */
  public static int getExponent(  double d){
    return (int)(((Double.doubleToRawLongBits(d) & DoubleConsts.EXP_BIT_MASK) >> (DoubleConsts.SIGNIFICAND_WIDTH - 1)) - DoubleConsts.EXP_BIAS);
  }
  /** 
 * Returns unbiased exponent of a {@code float}.
 */
  public static int getExponent(  float f){
    return ((Float.floatToRawIntBits(f) & FloatConsts.EXP_BIT_MASK) >> (FloatConsts.SIGNIFICAND_WIDTH - 1)) - FloatConsts.EXP_BIAS;
  }
  /** 
 * Returns a floating-point power of two in the normal range.
 */
  static double powerOfTwoD(  int n){
    assert (n >= DoubleConsts.MIN_EXPONENT && n <= DoubleConsts.MAX_EXPONENT);
    return Double.longBitsToDouble((((long)n + (long)DoubleConsts.EXP_BIAS) << (DoubleConsts.SIGNIFICAND_WIDTH - 1)) & DoubleConsts.EXP_BIT_MASK);
  }
  /** 
 * Returns a floating-point power of two in the normal range.
 */
  static float powerOfTwoF(  int n){
    assert (n >= FloatConsts.MIN_EXPONENT && n <= FloatConsts.MAX_EXPONENT);
    return Float.intBitsToFloat(((n + FloatConsts.EXP_BIAS) << (FloatConsts.SIGNIFICAND_WIDTH - 1)) & FloatConsts.EXP_BIT_MASK);
  }
  /** 
 * Returns the first floating-point argument with the sign of the
 * second floating-point argument.  Note that unlike the {@link FpUtils#copySign(double,double) copySign} method, this method
 * does not require NaN {@code sign} arguments to be treated
 * as positive values; implementations are permitted to treat some
 * NaN arguments as positive and other NaN arguments as negative
 * to allow greater performance.
 * @param magnitude  the parameter providing the magnitude of the result
 * @param sign   the parameter providing the sign of the result
 * @return a value with the magnitude of {@code magnitude}and the sign of {@code sign}.
 * @author Joseph D. Darcy
 */
  public static double rawCopySign(  double magnitude,  double sign){
    return Double.longBitsToDouble((Double.doubleToRawLongBits(sign) & (DoubleConsts.SIGN_BIT_MASK)) | (Double.doubleToRawLongBits(magnitude) & (DoubleConsts.EXP_BIT_MASK | DoubleConsts.SIGNIF_BIT_MASK)));
  }
  /** 
 * Returns the first floating-point argument with the sign of the
 * second floating-point argument.  Note that unlike the {@link FpUtils#copySign(float,float) copySign} method, this method
 * does not require NaN {@code sign} arguments to be treated
 * as positive values; implementations are permitted to treat some
 * NaN arguments as positive and other NaN arguments as negative
 * to allow greater performance.
 * @param magnitude  the parameter providing the magnitude of the result
 * @param sign   the parameter providing the sign of the result
 * @return a value with the magnitude of {@code magnitude}and the sign of {@code sign}.
 * @author Joseph D. Darcy
 */
  public static float rawCopySign(  float magnitude,  float sign){
    return Float.intBitsToFloat((Float.floatToRawIntBits(sign) & (FloatConsts.SIGN_BIT_MASK)) | (Float.floatToRawIntBits(magnitude) & (FloatConsts.EXP_BIT_MASK | FloatConsts.SIGNIF_BIT_MASK)));
  }
  /** 
 * Returns {@code true} if the argument is a finite
 * floating-point value; returns {@code false} otherwise (for
 * NaN and infinity arguments).
 * @param d the {@code double} value to be tested
 * @return {@code true} if the argument is a finite
 * floating-point value, {@code false} otherwise.
 */
  public static boolean isFinite(  double d){
    return Math.abs(d) <= DoubleConsts.MAX_VALUE;
  }
  /** 
 * Returns {@code true} if the argument is a finite
 * floating-point value; returns {@code false} otherwise (for
 * NaN and infinity arguments).
 * @param f the {@code float} value to be tested
 * @return {@code true} if the argument is a finite
 * floating-point value, {@code false} otherwise.
 */
  public static boolean isFinite(  float f){
    return Math.abs(f) <= FloatConsts.MAX_VALUE;
  }
  /** 
 * Returns {@code true} if the specified number is infinitely
 * large in magnitude, {@code false} otherwise.
 * <p>Note that this method is equivalent to the {@link Double#isInfinite(double) Double.isInfinite} method; the
 * functionality is included in this class for convenience.
 * @param d   the value to be tested.
 * @return  {@code true} if the value of the argument is positive
 * infinity or negative infinity; {@code false} otherwise.
 */
  public static boolean isInfinite(  double d){
    return Double.isInfinite(d);
  }
  /** 
 * Returns {@code true} if the specified number is infinitely
 * large in magnitude, {@code false} otherwise.
 * <p>Note that this method is equivalent to the {@link Float#isInfinite(float) Float.isInfinite} method; the
 * functionality is included in this class for convenience.
 * @param f   the value to be tested.
 * @return  {@code true} if the argument is positive infinity or
 * negative infinity; {@code false} otherwise.
 */
  public static boolean isInfinite(  float f){
    return Float.isInfinite(f);
  }
  /** 
 * Returns {@code true} if the specified number is a
 * Not-a-Number (NaN) value, {@code false} otherwise.
 * <p>Note that this method is equivalent to the {@link Double#isNaN(double) Double.isNaN} method; the functionality is
 * included in this class for convenience.
 * @param d   the value to be tested.
 * @return  {@code true} if the value of the argument is NaN;{@code false} otherwise.
 */
  public static boolean isNaN(  double d){
    return Double.isNaN(d);
  }
  /** 
 * Returns {@code true} if the specified number is a
 * Not-a-Number (NaN) value, {@code false} otherwise.
 * <p>Note that this method is equivalent to the {@link Float#isNaN(float) Float.isNaN} method; the functionality is
 * included in this class for convenience.
 * @param f   the value to be tested.
 * @return  {@code true} if the argument is NaN;{@code false} otherwise.
 */
  public static boolean isNaN(  float f){
    return Float.isNaN(f);
  }
  /** 
 * Returns {@code true} if the unordered relation holds
 * between the two arguments.  When two floating-point values are
 * unordered, one value is neither less than, equal to, nor
 * greater than the other.  For the unordered relation to be true,
 * at least one argument must be a {@code NaN}.
 * @param arg1      the first argument
 * @param arg2      the second argument
 * @return {@code true} if at least one argument is a NaN,{@code false} otherwise.
 */
  public static boolean isUnordered(  double arg1,  double arg2){
    return isNaN(arg1) || isNaN(arg2);
  }
  /** 
 * Returns {@code true} if the unordered relation holds
 * between the two arguments.  When two floating-point values are
 * unordered, one value is neither less than, equal to, nor
 * greater than the other.  For the unordered relation to be true,
 * at least one argument must be a {@code NaN}.
 * @param arg1      the first argument
 * @param arg2      the second argument
 * @return {@code true} if at least one argument is a NaN,{@code false} otherwise.
 */
  public static boolean isUnordered(  float arg1,  float arg2){
    return isNaN(arg1) || isNaN(arg2);
  }
  /** 
 * Returns unbiased exponent of a {@code double}; for
 * subnormal values, the number is treated as if it were
 * normalized.  That is for all finite, non-zero, positive numbers
 * <i>x</i>, <code>scalb(<i>x</i>, -ilogb(<i>x</i>))</code> is
 * always in the range [1, 2).
 * <p>
 * Special cases:
 * <ul>
 * <li> If the argument is NaN, then the result is 2<sup>30</sup>.
 * <li> If the argument is infinite, then the result is 2<sup>28</sup>.
 * <li> If the argument is zero, then the result is -(2<sup>28</sup>).
 * </ul>
 * @param d floating-point number whose exponent is to be extracted
 * @return unbiased exponent of the argument.
 * @author Joseph D. Darcy
 */
  public static int ilogb(  double d){
    int exponent=getExponent(d);
switch (exponent) {
case DoubleConsts.MAX_EXPONENT + 1:
      if (isNaN(d))       return (1 << 30);
 else       return (1 << 28);
case DoubleConsts.MIN_EXPONENT - 1:
    if (d == 0.0) {
      return -(1 << 28);
    }
 else {
      long transducer=Double.doubleToRawLongBits(d);
      transducer&=DoubleConsts.SIGNIF_BIT_MASK;
      assert (transducer != 0L);
      while (transducer < (1L << (DoubleConsts.SIGNIFICAND_WIDTH - 1))) {
        transducer*=2;
        exponent--;
      }
      exponent++;
      assert (exponent >= DoubleConsts.MIN_EXPONENT - (DoubleConsts.SIGNIFICAND_WIDTH - 1) && exponent < DoubleConsts.MIN_EXPONENT);
      return exponent;
    }
default :
  assert (exponent >= DoubleConsts.MIN_EXPONENT && exponent <= DoubleConsts.MAX_EXPONENT);
return exponent;
}
}
/** 
 * Returns unbiased exponent of a {@code float}; for
 * subnormal values, the number is treated as if it were
 * normalized.  That is for all finite, non-zero, positive numbers
 * <i>x</i>, <code>scalb(<i>x</i>, -ilogb(<i>x</i>))</code> is
 * always in the range [1, 2).
 * <p>
 * Special cases:
 * <ul>
 * <li> If the argument is NaN, then the result is 2<sup>30</sup>.
 * <li> If the argument is infinite, then the result is 2<sup>28</sup>.
 * <li> If the argument is zero, then the result is -(2<sup>28</sup>).
 * </ul>
 * @param f floating-point number whose exponent is to be extracted
 * @return unbiased exponent of the argument.
 * @author Joseph D. Darcy
 */
public static int ilogb(float f){
int exponent=getExponent(f);
switch (exponent) {
case FloatConsts.MAX_EXPONENT + 1:
if (isNaN(f)) return (1 << 30);
 else return (1 << 28);
case FloatConsts.MIN_EXPONENT - 1:
if (f == 0.0f) {
return -(1 << 28);
}
 else {
int transducer=Float.floatToRawIntBits(f);
transducer&=FloatConsts.SIGNIF_BIT_MASK;
assert (transducer != 0);
while (transducer < (1 << (FloatConsts.SIGNIFICAND_WIDTH - 1))) {
transducer*=2;
exponent--;
}
exponent++;
assert (exponent >= FloatConsts.MIN_EXPONENT - (FloatConsts.SIGNIFICAND_WIDTH - 1) && exponent < FloatConsts.MIN_EXPONENT);
return exponent;
}
default :
assert (exponent >= FloatConsts.MIN_EXPONENT && exponent <= FloatConsts.MAX_EXPONENT);
return exponent;
}
}
/** 
 * Return {@code d} &times;
 * 2<sup>{@code scale_factor}</sup> rounded as if performed
 * by a single correctly rounded floating-point multiply to a
 * member of the double value set.  See section 4.2.3 of
 * <cite>The Java&trade; Language Specification</cite>
 * for a discussion of floating-point
 * value sets.  If the exponent of the result is between the{@code double}'s minimum exponent and maximum exponent,
 * the answer is calculated exactly.  If the exponent of the
 * result would be larger than {@code doubles}'s maximum
 * exponent, an infinity is returned.  Note that if the result is
 * subnormal, precision may be lost; that is, when {@code scalb(x,
 * n)} is subnormal, {@code scalb(scalb(x, n), -n)} may
 * not equal <i>x</i>.  When the result is non-NaN, the result has
 * the same sign as {@code d}.
 * <p>
 * Special cases:
 * <ul>
 * <li> If the first argument is NaN, NaN is returned.
 * <li> If the first argument is infinite, then an infinity of the
 * same sign is returned.
 * <li> If the first argument is zero, then a zero of the same
 * sign is returned.
 * </ul>
 * @param d number to be scaled by a power of two.
 * @param scale_factor power of 2 used to scale {@code d}
 * @return {@code d * }2<sup>{@code scale_factor}</sup>
 * @author Joseph D. Darcy
 */
public static double scalb(double d,int scale_factor){
final int MAX_SCALE=DoubleConsts.MAX_EXPONENT + -DoubleConsts.MIN_EXPONENT + DoubleConsts.SIGNIFICAND_WIDTH+ 1;
int exp_adjust=0;
int scale_increment=0;
double exp_delta=Double.NaN;
if (scale_factor < 0) {
scale_factor=Math.max(scale_factor,-MAX_SCALE);
scale_increment=-512;
exp_delta=twoToTheDoubleScaleDown;
}
 else {
scale_factor=Math.min(scale_factor,MAX_SCALE);
scale_increment=512;
exp_delta=twoToTheDoubleScaleUp;
}
int t=(scale_factor >> 9 - 1) >>> 32 - 9;
exp_adjust=((scale_factor + t) & (512 - 1)) - t;
d*=powerOfTwoD(exp_adjust);
scale_factor-=exp_adjust;
while (scale_factor != 0) {
d*=exp_delta;
scale_factor-=scale_increment;
}
return d;
}
/** 
 * Return {@code f} &times;
 * 2<sup>{@code scale_factor}</sup> rounded as if performed
 * by a single correctly rounded floating-point multiply to a
 * member of the float value set.  See section 4.2.3 of
 * <cite>The Java&trade; Language Specification</cite>
 * for a discussion of floating-point
 * value sets. If the exponent of the result is between the{@code float}'s minimum exponent and maximum exponent, the
 * answer is calculated exactly.  If the exponent of the result
 * would be larger than {@code float}'s maximum exponent, an
 * infinity is returned.  Note that if the result is subnormal,
 * precision may be lost; that is, when {@code scalb(x, n)}is subnormal, {@code scalb(scalb(x, n), -n)} may not equal
 * <i>x</i>.  When the result is non-NaN, the result has the same
 * sign as {@code f}.
 * <p>
 * Special cases:
 * <ul>
 * <li> If the first argument is NaN, NaN is returned.
 * <li> If the first argument is infinite, then an infinity of the
 * same sign is returned.
 * <li> If the first argument is zero, then a zero of the same
 * sign is returned.
 * </ul>
 * @param f number to be scaled by a power of two.
 * @param scale_factor power of 2 used to scale {@code f}
 * @return {@code f * }2<sup>{@code scale_factor}</sup>
 * @author Joseph D. Darcy
 */
public static float scalb(float f,int scale_factor){
final int MAX_SCALE=FloatConsts.MAX_EXPONENT + -FloatConsts.MIN_EXPONENT + FloatConsts.SIGNIFICAND_WIDTH+ 1;
scale_factor=Math.max(Math.min(scale_factor,MAX_SCALE),-MAX_SCALE);
return (float)((double)f * powerOfTwoD(scale_factor));
}
/** 
 * Returns the floating-point number adjacent to the first
 * argument in the direction of the second argument.  If both
 * arguments compare as equal the second argument is returned.
 * <p>
 * Special cases:
 * <ul>
 * <li> If either argument is a NaN, then NaN is returned.
 * <li> If both arguments are signed zeros, {@code direction}is returned unchanged (as implied by the requirement of
 * returning the second argument if the arguments compare as
 * equal).
 * <li> If {@code start} is
 * &plusmn;{@code Double.MIN_VALUE} and {@code direction}has a value such that the result should have a smaller
 * magnitude, then a zero with the same sign as {@code start}is returned.
 * <li> If {@code start} is infinite and{@code direction} has a value such that the result should
 * have a smaller magnitude, {@code Double.MAX_VALUE} with the
 * same sign as {@code start} is returned.
 * <li> If {@code start} is equal to &plusmn;{@code Double.MAX_VALUE} and {@code direction} has a
 * value such that the result should have a larger magnitude, an
 * infinity with same sign as {@code start} is returned.
 * </ul>
 * @param start     starting floating-point value
 * @param direction value indicating which of{@code start}'s neighbors or {@code start} should
 * be returned
 * @return The floating-point number adjacent to {@code start} in the
 * direction of {@code direction}.
 * @author Joseph D. Darcy
 */
public static double nextAfter(double start,double direction){
if (isNaN(start) || isNaN(direction)) {
return start + direction;
}
 else if (start == direction) {
return direction;
}
 else {
long transducer=Double.doubleToRawLongBits(start + 0.0d);
if (direction > start) {
transducer=transducer + (transducer >= 0L ? 1L : -1L);
}
 else {
assert direction < start;
if (transducer > 0L) --transducer;
 else if (transducer < 0L) ++transducer;
 else transducer=DoubleConsts.SIGN_BIT_MASK | 1L;
}
return Double.longBitsToDouble(transducer);
}
}
/** 
 * Returns the floating-point number adjacent to the first
 * argument in the direction of the second argument.  If both
 * arguments compare as equal, the second argument is returned.
 * <p>
 * Special cases:
 * <ul>
 * <li> If either argument is a NaN, then NaN is returned.
 * <li> If both arguments are signed zeros, a {@code float}zero with the same sign as {@code direction} is returned
 * (as implied by the requirement of returning the second argument
 * if the arguments compare as equal).
 * <li> If {@code start} is
 * &plusmn;{@code Float.MIN_VALUE} and {@code direction}has a value such that the result should have a smaller
 * magnitude, then a zero with the same sign as {@code start}is returned.
 * <li> If {@code start} is infinite and{@code direction} has a value such that the result should
 * have a smaller magnitude, {@code Float.MAX_VALUE} with the
 * same sign as {@code start} is returned.
 * <li> If {@code start} is equal to &plusmn;{@code Float.MAX_VALUE} and {@code direction} has a
 * value such that the result should have a larger magnitude, an
 * infinity with same sign as {@code start} is returned.
 * </ul>
 * @param start     starting floating-point value
 * @param direction value indicating which of{@code start}'s neighbors or {@code start} should
 * be returned
 * @return The floating-point number adjacent to {@code start} in the
 * direction of {@code direction}.
 * @author Joseph D. Darcy
 */
public static float nextAfter(float start,double direction){
if (isNaN(start) || isNaN(direction)) {
return start + (float)direction;
}
 else if (start == direction) {
return (float)direction;
}
 else {
int transducer=Float.floatToRawIntBits(start + 0.0f);
if (direction > start) {
transducer=transducer + (transducer >= 0 ? 1 : -1);
}
 else {
assert direction < start;
if (transducer > 0) --transducer;
 else if (transducer < 0) ++transducer;
 else transducer=FloatConsts.SIGN_BIT_MASK | 1;
}
return Float.intBitsToFloat(transducer);
}
}
/** 
 * Returns the floating-point value adjacent to {@code d} in
 * the direction of positive infinity.  This method is
 * semantically equivalent to {@code nextAfter(d,
 * Double.POSITIVE_INFINITY)}; however, a {@code nextUp}implementation may run faster than its equivalent{@code nextAfter} call.
 * <p>Special Cases:
 * <ul>
 * <li> If the argument is NaN, the result is NaN.
 * <li> If the argument is positive infinity, the result is
 * positive infinity.
 * <li> If the argument is zero, the result is{@code Double.MIN_VALUE}</ul>
 * @param d  starting floating-point value
 * @return The adjacent floating-point value closer to positive
 * infinity.
 * @author Joseph D. Darcy
 */
public static double nextUp(double d){
if (isNaN(d) || d == Double.POSITIVE_INFINITY) return d;
 else {
d+=0.0d;
return Double.longBitsToDouble(Double.doubleToRawLongBits(d) + ((d >= 0.0d) ? +1L : -1L));
}
}
/** 
 * Returns the floating-point value adjacent to {@code f} in
 * the direction of positive infinity.  This method is
 * semantically equivalent to {@code nextAfter(f,
 * Double.POSITIVE_INFINITY)}; however, a {@code nextUp}implementation may run faster than its equivalent{@code nextAfter} call.
 * <p>Special Cases:
 * <ul>
 * <li> If the argument is NaN, the result is NaN.
 * <li> If the argument is positive infinity, the result is
 * positive infinity.
 * <li> If the argument is zero, the result is{@code Float.MIN_VALUE}</ul>
 * @param f  starting floating-point value
 * @return The adjacent floating-point value closer to positive
 * infinity.
 * @author Joseph D. Darcy
 */
public static float nextUp(float f){
if (isNaN(f) || f == FloatConsts.POSITIVE_INFINITY) return f;
 else {
f+=0.0f;
return Float.intBitsToFloat(Float.floatToRawIntBits(f) + ((f >= 0.0f) ? +1 : -1));
}
}
/** 
 * Returns the floating-point value adjacent to {@code d} in
 * the direction of negative infinity.  This method is
 * semantically equivalent to {@code nextAfter(d,
 * Double.NEGATIVE_INFINITY)}; however, a{@code nextDown} implementation may run faster than its
 * equivalent {@code nextAfter} call.
 * <p>Special Cases:
 * <ul>
 * <li> If the argument is NaN, the result is NaN.
 * <li> If the argument is negative infinity, the result is
 * negative infinity.
 * <li> If the argument is zero, the result is{@code -Double.MIN_VALUE}</ul>
 * @param d  starting floating-point value
 * @return The adjacent floating-point value closer to negative
 * infinity.
 * @author Joseph D. Darcy
 */
public static double nextDown(double d){
if (isNaN(d) || d == Double.NEGATIVE_INFINITY) return d;
 else {
if (d == 0.0) return -Double.MIN_VALUE;
 else return Double.longBitsToDouble(Double.doubleToRawLongBits(d) + ((d > 0.0d) ? -1L : +1L));
}
}
/** 
 * Returns the floating-point value adjacent to {@code f} in
 * the direction of negative infinity.  This method is
 * semantically equivalent to {@code nextAfter(f,
 * Float.NEGATIVE_INFINITY)}; however, a{@code nextDown} implementation may run faster than its
 * equivalent {@code nextAfter} call.
 * <p>Special Cases:
 * <ul>
 * <li> If the argument is NaN, the result is NaN.
 * <li> If the argument is negative infinity, the result is
 * negative infinity.
 * <li> If the argument is zero, the result is{@code -Float.MIN_VALUE}</ul>
 * @param f  starting floating-point value
 * @return The adjacent floating-point value closer to negative
 * infinity.
 * @author Joseph D. Darcy
 */
public static double nextDown(float f){
if (isNaN(f) || f == Float.NEGATIVE_INFINITY) return f;
 else {
if (f == 0.0f) return -Float.MIN_VALUE;
 else return Float.intBitsToFloat(Float.floatToRawIntBits(f) + ((f > 0.0f) ? -1 : +1));
}
}
/** 
 * Returns the first floating-point argument with the sign of the
 * second floating-point argument.  For this method, a NaN{@code sign} argument is always treated as if it were
 * positive.
 * @param magnitude  the parameter providing the magnitude of the result
 * @param sign   the parameter providing the sign of the result
 * @return a value with the magnitude of {@code magnitude}and the sign of {@code sign}.
 * @author Joseph D. Darcy
 * @since 1.5
 */
public static double copySign(double magnitude,double sign){
return rawCopySign(magnitude,(isNaN(sign) ? 1.0d : sign));
}
/** 
 * Returns the first floating-point argument with the sign of the
 * second floating-point argument.  For this method, a NaN{@code sign} argument is always treated as if it were
 * positive.
 * @param magnitude  the parameter providing the magnitude of the result
 * @param sign   the parameter providing the sign of the result
 * @return a value with the magnitude of {@code magnitude}and the sign of {@code sign}.
 * @author Joseph D. Darcy
 */
public static float copySign(float magnitude,float sign){
return rawCopySign(magnitude,(isNaN(sign) ? 1.0f : sign));
}
/** 
 * Returns the size of an ulp of the argument.  An ulp of a{@code double} value is the positive distance between this
 * floating-point value and the {@code double} value next
 * larger in magnitude.  Note that for non-NaN <i>x</i>,
 * <code>ulp(-<i>x</i>) == ulp(<i>x</i>)</code>.
 * <p>Special Cases:
 * <ul>
 * <li> If the argument is NaN, then the result is NaN.
 * <li> If the argument is positive or negative infinity, then the
 * result is positive infinity.
 * <li> If the argument is positive or negative zero, then the result is{@code Double.MIN_VALUE}.
 * <li> If the argument is &plusmn;{@code Double.MAX_VALUE}, then
 * the result is equal to 2<sup>971</sup>.
 * </ul>
 * @param d the floating-point value whose ulp is to be returned
 * @return the size of an ulp of the argument
 * @author Joseph D. Darcy
 * @since 1.5
 */
public static double ulp(double d){
int exp=getExponent(d);
switch (exp) {
case DoubleConsts.MAX_EXPONENT + 1:
return Math.abs(d);
case DoubleConsts.MIN_EXPONENT - 1:
return Double.MIN_VALUE;
default :
assert exp <= DoubleConsts.MAX_EXPONENT && exp >= DoubleConsts.MIN_EXPONENT;
exp=exp - (DoubleConsts.SIGNIFICAND_WIDTH - 1);
if (exp >= DoubleConsts.MIN_EXPONENT) {
return powerOfTwoD(exp);
}
 else {
return Double.longBitsToDouble(1L << (exp - (DoubleConsts.MIN_EXPONENT - (DoubleConsts.SIGNIFICAND_WIDTH - 1))));
}
}
}
/** 
 * Returns the size of an ulp of the argument.  An ulp of a{@code float} value is the positive distance between this
 * floating-point value and the {@code float} value next
 * larger in magnitude.  Note that for non-NaN <i>x</i>,
 * <code>ulp(-<i>x</i>) == ulp(<i>x</i>)</code>.
 * <p>Special Cases:
 * <ul>
 * <li> If the argument is NaN, then the result is NaN.
 * <li> If the argument is positive or negative infinity, then the
 * result is positive infinity.
 * <li> If the argument is positive or negative zero, then the result is{@code Float.MIN_VALUE}.
 * <li> If the argument is &plusmn;{@code Float.MAX_VALUE}, then
 * the result is equal to 2<sup>104</sup>.
 * </ul>
 * @param f the floating-point value whose ulp is to be returned
 * @return the size of an ulp of the argument
 * @author Joseph D. Darcy
 * @since 1.5
 */
public static float ulp(float f){
int exp=getExponent(f);
switch (exp) {
case FloatConsts.MAX_EXPONENT + 1:
return Math.abs(f);
case FloatConsts.MIN_EXPONENT - 1:
return FloatConsts.MIN_VALUE;
default :
assert exp <= FloatConsts.MAX_EXPONENT && exp >= FloatConsts.MIN_EXPONENT;
exp=exp - (FloatConsts.SIGNIFICAND_WIDTH - 1);
if (exp >= FloatConsts.MIN_EXPONENT) {
return powerOfTwoF(exp);
}
 else {
return Float.intBitsToFloat(1 << (exp - (FloatConsts.MIN_EXPONENT - (FloatConsts.SIGNIFICAND_WIDTH - 1))));
}
}
}
/** 
 * Returns the signum function of the argument; zero if the argument
 * is zero, 1.0 if the argument is greater than zero, -1.0 if the
 * argument is less than zero.
 * <p>Special Cases:
 * <ul>
 * <li> If the argument is NaN, then the result is NaN.
 * <li> If the argument is positive zero or negative zero, then the
 * result is the same as the argument.
 * </ul>
 * @param d the floating-point value whose signum is to be returned
 * @return the signum function of the argument
 * @author Joseph D. Darcy
 * @since 1.5
 */
public static double signum(double d){
return (d == 0.0 || isNaN(d)) ? d : copySign(1.0,d);
}
/** 
 * Returns the signum function of the argument; zero if the argument
 * is zero, 1.0f if the argument is greater than zero, -1.0f if the
 * argument is less than zero.
 * <p>Special Cases:
 * <ul>
 * <li> If the argument is NaN, then the result is NaN.
 * <li> If the argument is positive zero or negative zero, then the
 * result is the same as the argument.
 * </ul>
 * @param f the floating-point value whose signum is to be returned
 * @return the signum function of the argument
 * @author Joseph D. Darcy
 * @since 1.5
 */
public static float signum(float f){
return (f == 0.0f || isNaN(f)) ? f : copySign(1.0f,f);
}
}
