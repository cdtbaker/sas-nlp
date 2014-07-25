package org.apache.commons.math3.util;
import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.exception.NotPositiveException;
import org.apache.commons.math3.exception.NumberIsTooLargeException;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.util.LocalizedFormats;
/** 
 * Some useful, arithmetics related, additions to the built-in functions in{@link Math}.
 * @version $Id: ArithmeticUtils.java 1422313 2012-12-15 18:53:41Z psteitz $
 */
public final class ArithmeticUtils {
  /** 
 * All long-representable factorials 
 */
  static final long[] FACTORIALS=new long[]{1l,1l,2l,6l,24l,120l,720l,5040l,40320l,362880l,3628800l,39916800l,479001600l,6227020800l,87178291200l,1307674368000l,20922789888000l,355687428096000l,6402373705728000l,121645100408832000l,2432902008176640000l};
  /** 
 * Stirling numbers of the second kind. 
 */
  static final AtomicReference<long[][]> STIRLING_S2=new AtomicReference<long[][]>(null);
  /** 
 * Private constructor. 
 */
  private ArithmeticUtils(){
    super();
  }
  /** 
 * Add two integers, checking for overflow.
 * @param x an addend
 * @param y an addend
 * @return the sum {@code x+y}
 * @throws MathArithmeticException if the result can not be represented
 * as an {@code int}.
 * @since 1.1
 */
  public static int addAndCheck(  int x,  int y) throws MathArithmeticException {
    long s=(long)x + (long)y;
    if (s < Integer.MIN_VALUE || s > Integer.MAX_VALUE) {
      throw new MathArithmeticException(LocalizedFormats.OVERFLOW_IN_ADDITION,x,y);
    }
    return (int)s;
  }
  /** 
 * Add two long integers, checking for overflow.
 * @param a an addend
 * @param b an addend
 * @return the sum {@code a+b}
 * @throws MathArithmeticException if the result can not be represented as an
 * long
 * @since 1.2
 */
  public static long addAndCheck(  long a,  long b) throws MathArithmeticException {
    return ArithmeticUtils.addAndCheck(a,b,LocalizedFormats.OVERFLOW_IN_ADDITION);
  }
  /** 
 * Returns an exact representation of the <a
 * href="http://mathworld.wolfram.com/BinomialCoefficient.html"> Binomial
 * Coefficient</a>, "{@code n choose k}", the number of{@code k}-element subsets that can be selected from an{@code n}-element set.
 * <p>
 * <Strong>Preconditions</strong>:
 * <ul>
 * <li> {@code 0 <= k <= n } (otherwise{@code IllegalArgumentException} is thrown)</li>
 * <li> The result is small enough to fit into a {@code long}. The
 * largest value of {@code n} for which all coefficients are{@code  < Long.MAX_VALUE} is 66. If the computed value exceeds{@code Long.MAX_VALUE} an {@code ArithMeticException} is
 * thrown.</li>
 * </ul></p>
 * @param n the size of the set
 * @param k the size of the subsets to be counted
 * @return {@code n choose k}
 * @throws NotPositiveException if {@code n < 0}.
 * @throws NumberIsTooLargeException if {@code k > n}.
 * @throws MathArithmeticException if the result is too large to be
 * represented by a long integer.
 */
  public static long binomialCoefficient(  final int n,  final int k) throws NotPositiveException, NumberIsTooLargeException, MathArithmeticException {
    ArithmeticUtils.checkBinomial(n,k);
    if ((n == k) || (k == 0)) {
      return 1;
    }
    if ((k == 1) || (k == n - 1)) {
      return n;
    }
    if (k > n / 2) {
      return binomialCoefficient(n,n - k);
    }
    long result=1;
    if (n <= 61) {
      int i=n - k + 1;
      for (int j=1; j <= k; j++) {
        result=result * i / j;
        i++;
      }
    }
 else     if (n <= 66) {
      int i=n - k + 1;
      for (int j=1; j <= k; j++) {
        final long d=gcd(i,j);
        result=(result / (j / d)) * (i / d);
        i++;
      }
    }
 else {
      int i=n - k + 1;
      for (int j=1; j <= k; j++) {
        final long d=gcd(i,j);
        result=mulAndCheck(result / (j / d),i / d);
        i++;
      }
    }
    return result;
  }
  /** 
 * Returns a {@code double} representation of the <a
 * href="http://mathworld.wolfram.com/BinomialCoefficient.html"> Binomial
 * Coefficient</a>, "{@code n choose k}", the number of{@code k}-element subsets that can be selected from an{@code n}-element set.
 * <p>
 * <Strong>Preconditions</strong>:
 * <ul>
 * <li> {@code 0 <= k <= n } (otherwise{@code IllegalArgumentException} is thrown)</li>
 * <li> The result is small enough to fit into a {@code double}. The
 * largest value of {@code n} for which all coefficients are <
 * Double.MAX_VALUE is 1029. If the computed value exceeds Double.MAX_VALUE,
 * Double.POSITIVE_INFINITY is returned</li>
 * </ul></p>
 * @param n the size of the set
 * @param k the size of the subsets to be counted
 * @return {@code n choose k}
 * @throws NotPositiveException if {@code n < 0}.
 * @throws NumberIsTooLargeException if {@code k > n}.
 * @throws MathArithmeticException if the result is too large to be
 * represented by a long integer.
 */
  public static double binomialCoefficientDouble(  final int n,  final int k) throws NotPositiveException, NumberIsTooLargeException, MathArithmeticException {
    ArithmeticUtils.checkBinomial(n,k);
    if ((n == k) || (k == 0)) {
      return 1d;
    }
    if ((k == 1) || (k == n - 1)) {
      return n;
    }
    if (k > n / 2) {
      return binomialCoefficientDouble(n,n - k);
    }
    if (n < 67) {
      return binomialCoefficient(n,k);
    }
    double result=1d;
    for (int i=1; i <= k; i++) {
      result*=(double)(n - k + i) / (double)i;
    }
    return FastMath.floor(result + 0.5);
  }
  /** 
 * Returns the natural {@code log} of the <a
 * href="http://mathworld.wolfram.com/BinomialCoefficient.html"> Binomial
 * Coefficient</a>, "{@code n choose k}", the number of{@code k}-element subsets that can be selected from an{@code n}-element set.
 * <p>
 * <Strong>Preconditions</strong>:
 * <ul>
 * <li> {@code 0 <= k <= n } (otherwise{@code IllegalArgumentException} is thrown)</li>
 * </ul></p>
 * @param n the size of the set
 * @param k the size of the subsets to be counted
 * @return {@code n choose k}
 * @throws NotPositiveException if {@code n < 0}.
 * @throws NumberIsTooLargeException if {@code k > n}.
 * @throws MathArithmeticException if the result is too large to be
 * represented by a long integer.
 */
  public static double binomialCoefficientLog(  final int n,  final int k) throws NotPositiveException, NumberIsTooLargeException, MathArithmeticException {
    ArithmeticUtils.checkBinomial(n,k);
    if ((n == k) || (k == 0)) {
      return 0;
    }
    if ((k == 1) || (k == n - 1)) {
      return FastMath.log(n);
    }
    if (n < 67) {
      return FastMath.log(binomialCoefficient(n,k));
    }
    if (n < 1030) {
      return FastMath.log(binomialCoefficientDouble(n,k));
    }
    if (k > n / 2) {
      return binomialCoefficientLog(n,n - k);
    }
    double logSum=0;
    for (int i=n - k + 1; i <= n; i++) {
      logSum+=FastMath.log(i);
    }
    for (int i=2; i <= k; i++) {
      logSum-=FastMath.log(i);
    }
    return logSum;
  }
  /** 
 * Returns n!. Shorthand for {@code n} <a
 * href="http://mathworld.wolfram.com/Factorial.html"> Factorial</a>, the
 * product of the numbers {@code 1,...,n}.
 * <p>
 * <Strong>Preconditions</strong>:
 * <ul>
 * <li> {@code n >= 0} (otherwise{@code IllegalArgumentException} is thrown)</li>
 * <li> The result is small enough to fit into a {@code long}. The
 * largest value of {@code n} for which {@code n!} <
 * Long.MAX_VALUE} is 20. If the computed value exceeds {@code Long.MAX_VALUE}an {@code ArithMeticException } is thrown.</li>
 * </ul>
 * </p>
 * @param n argument
 * @return {@code n!}
 * @throws MathArithmeticException if the result is too large to be represented
 * by a {@code long}.
 * @throws NotPositiveException if {@code n < 0}.
 * @throws MathArithmeticException if {@code n > 20}: The factorial value is too
 * large to fit in a {@code long}.
 */
  public static long factorial(  final int n) throws NotPositiveException, MathArithmeticException {
    if (n < 0) {
      throw new NotPositiveException(LocalizedFormats.FACTORIAL_NEGATIVE_PARAMETER,n);
    }
    if (n > 20) {
      throw new MathArithmeticException();
    }
    return FACTORIALS[n];
  }
  /** 
 * Compute n!, the<a href="http://mathworld.wolfram.com/Factorial.html">
 * factorial</a> of {@code n} (the product of the numbers 1 to n), as a{@code double}.
 * The result should be small enough to fit into a {@code double}: The
 * largest {@code n} for which {@code n! < Double.MAX_VALUE} is 170.
 * If the computed value exceeds {@code Double.MAX_VALUE},{@code Double.POSITIVE_INFINITY} is returned.
 * @param n Argument.
 * @return {@code n!}
 * @throws NotPositiveException if {@code n < 0}.
 */
  public static double factorialDouble(  final int n) throws NotPositiveException {
    if (n < 0) {
      throw new NotPositiveException(LocalizedFormats.FACTORIAL_NEGATIVE_PARAMETER,n);
    }
    if (n < 21) {
      return FACTORIALS[n];
    }
    return FastMath.floor(FastMath.exp(ArithmeticUtils.factorialLog(n)) + 0.5);
  }
  /** 
 * Compute the natural logarithm of the factorial of {@code n}.
 * @param n Argument.
 * @return {@code n!}
 * @throws NotPositiveException if {@code n < 0}.
 */
  public static double factorialLog(  final int n) throws NotPositiveException {
    if (n < 0) {
      throw new NotPositiveException(LocalizedFormats.FACTORIAL_NEGATIVE_PARAMETER,n);
    }
    if (n < 21) {
      return FastMath.log(FACTORIALS[n]);
    }
    double logSum=0;
    for (int i=2; i <= n; i++) {
      logSum+=FastMath.log(i);
    }
    return logSum;
  }
  /** 
 * Computes the greatest common divisor of the absolute value of two
 * numbers, using a modified version of the "binary gcd" method.
 * See Knuth 4.5.2 algorithm B.
 * The algorithm is due to Josef Stein (1961).
 * <br/>
 * Special cases:
 * <ul>
 * <li>The invocations{@code gcd(Integer.MIN_VALUE, Integer.MIN_VALUE)},{@code gcd(Integer.MIN_VALUE, 0)} and{@code gcd(0, Integer.MIN_VALUE)} throw an{@code ArithmeticException}, because the result would be 2^31, which
 * is too large for an int value.</li>
 * <li>The result of {@code gcd(x, x)}, {@code gcd(0, x)} and{@code gcd(x, 0)} is the absolute value of {@code x}, except
 * for the special cases above.</li>
 * <li>The invocation {@code gcd(0, 0)} is the only one which returns{@code 0}.</li>
 * </ul>
 * @param p Number.
 * @param q Number.
 * @return the greatest common divisor (never negative).
 * @throws MathArithmeticException if the result cannot be represented as
 * a non-negative {@code int} value.
 * @since 1.1
 */
  public static int gcd(  int p,  int q) throws MathArithmeticException {
    int a=p;
    int b=q;
    if (a == 0 || b == 0) {
      if (a == Integer.MIN_VALUE || b == Integer.MIN_VALUE) {
        throw new MathArithmeticException(LocalizedFormats.GCD_OVERFLOW_32_BITS,p,q);
      }
      return FastMath.abs(a + b);
    }
    long al=a;
    long bl=b;
    boolean useLong=false;
    if (a < 0) {
      if (Integer.MIN_VALUE == a) {
        useLong=true;
      }
 else {
        a=-a;
      }
      al=-al;
    }
    if (b < 0) {
      if (Integer.MIN_VALUE == b) {
        useLong=true;
      }
 else {
        b=-b;
      }
      bl=-bl;
    }
    if (useLong) {
      if (al == bl) {
        throw new MathArithmeticException(LocalizedFormats.GCD_OVERFLOW_32_BITS,p,q);
      }
      long blbu=bl;
      bl=al;
      al=blbu % al;
      if (al == 0) {
        if (bl > Integer.MAX_VALUE) {
          throw new MathArithmeticException(LocalizedFormats.GCD_OVERFLOW_32_BITS,p,q);
        }
        return (int)bl;
      }
      blbu=bl;
      b=(int)al;
      a=(int)(blbu % al);
    }
    return gcdPositive(a,b);
  }
  /** 
 * Computes the greatest common divisor of two <em>positive</em> numbers
 * (this precondition is <em>not</em> checked and the result is undefined
 * if not fulfilled) using the "binary gcd" method which avoids division
 * and modulo operations.
 * See Knuth 4.5.2 algorithm B.
 * The algorithm is due to Josef Stein (1961).
 * <br/>
 * Special cases:
 * <ul>
 * <li>The result of {@code gcd(x, x)}, {@code gcd(0, x)} and{@code gcd(x, 0)} is the value of {@code x}.</li>
 * <li>The invocation {@code gcd(0, 0)} is the only one which returns{@code 0}.</li>
 * </ul>
 * @param a Positive number.
 * @param b Positive number.
 * @return the greatest common divisor.
 */
  private static int gcdPositive(  int a,  int b){
    if (a == 0) {
      return b;
    }
 else     if (b == 0) {
      return a;
    }
    final int aTwos=Integer.numberOfTrailingZeros(a);
    a>>=aTwos;
    final int bTwos=Integer.numberOfTrailingZeros(b);
    b>>=bTwos;
    final int shift=Math.min(aTwos,bTwos);
    while (a != b) {
      final int delta=a - b;
      b=Math.min(a,b);
      a=Math.abs(delta);
      a>>=Integer.numberOfTrailingZeros(a);
    }
    return a << shift;
  }
  /** 
 * <p>
 * Gets the greatest common divisor of the absolute value of two numbers,
 * using the "binary gcd" method which avoids division and modulo
 * operations. See Knuth 4.5.2 algorithm B. This algorithm is due to Josef
 * Stein (1961).
 * </p>
 * Special cases:
 * <ul>
 * <li>The invocations{@code gcd(Long.MIN_VALUE, Long.MIN_VALUE)},{@code gcd(Long.MIN_VALUE, 0L)} and{@code gcd(0L, Long.MIN_VALUE)} throw an{@code ArithmeticException}, because the result would be 2^63, which
 * is too large for a long value.</li>
 * <li>The result of {@code gcd(x, x)}, {@code gcd(0L, x)} and{@code gcd(x, 0L)} is the absolute value of {@code x}, except
 * for the special cases above.
 * <li>The invocation {@code gcd(0L, 0L)} is the only one which returns{@code 0L}.</li>
 * </ul>
 * @param p Number.
 * @param q Number.
 * @return the greatest common divisor, never negative.
 * @throws MathArithmeticException if the result cannot be represented as
 * a non-negative {@code long} value.
 * @since 2.1
 */
  public static long gcd(  final long p,  final long q) throws MathArithmeticException {
    long u=p;
    long v=q;
    if ((u == 0) || (v == 0)) {
      if ((u == Long.MIN_VALUE) || (v == Long.MIN_VALUE)) {
        throw new MathArithmeticException(LocalizedFormats.GCD_OVERFLOW_64_BITS,p,q);
      }
      return FastMath.abs(u) + FastMath.abs(v);
    }
    if (u > 0) {
      u=-u;
    }
    if (v > 0) {
      v=-v;
    }
    int k=0;
    while ((u & 1) == 0 && (v & 1) == 0 && k < 63) {
      u/=2;
      v/=2;
      k++;
    }
    if (k == 63) {
      throw new MathArithmeticException(LocalizedFormats.GCD_OVERFLOW_64_BITS,p,q);
    }
    long t=((u & 1) == 1) ? v : -(u / 2);
    do {
      while ((t & 1) == 0) {
        t/=2;
      }
      if (t > 0) {
        u=-t;
      }
 else {
        v=t;
      }
      t=(v - u) / 2;
    }
 while (t != 0);
    return -u * (1L << k);
  }
  /** 
 * <p>
 * Returns the least common multiple of the absolute value of two numbers,
 * using the formula {@code lcm(a,b) = (a / gcd(a,b)) * b}.
 * </p>
 * Special cases:
 * <ul>
 * <li>The invocations {@code lcm(Integer.MIN_VALUE, n)} and{@code lcm(n, Integer.MIN_VALUE)}, where {@code abs(n)} is a
 * power of 2, throw an {@code ArithmeticException}, because the result
 * would be 2^31, which is too large for an int value.</li>
 * <li>The result of {@code lcm(0, x)} and {@code lcm(x, 0)} is{@code 0} for any {@code x}.
 * </ul>
 * @param a Number.
 * @param b Number.
 * @return the least common multiple, never negative.
 * @throws MathArithmeticException if the result cannot be represented as
 * a non-negative {@code int} value.
 * @since 1.1
 */
  public static int lcm(  int a,  int b) throws MathArithmeticException {
    if (a == 0 || b == 0) {
      return 0;
    }
    int lcm=FastMath.abs(ArithmeticUtils.mulAndCheck(a / gcd(a,b),b));
    if (lcm == Integer.MIN_VALUE) {
      throw new MathArithmeticException(LocalizedFormats.LCM_OVERFLOW_32_BITS,a,b);
    }
    return lcm;
  }
  /** 
 * <p>
 * Returns the least common multiple of the absolute value of two numbers,
 * using the formula {@code lcm(a,b) = (a / gcd(a,b)) * b}.
 * </p>
 * Special cases:
 * <ul>
 * <li>The invocations {@code lcm(Long.MIN_VALUE, n)} and{@code lcm(n, Long.MIN_VALUE)}, where {@code abs(n)} is a
 * power of 2, throw an {@code ArithmeticException}, because the result
 * would be 2^63, which is too large for an int value.</li>
 * <li>The result of {@code lcm(0L, x)} and {@code lcm(x, 0L)} is{@code 0L} for any {@code x}.
 * </ul>
 * @param a Number.
 * @param b Number.
 * @return the least common multiple, never negative.
 * @throws MathArithmeticException if the result cannot be represented
 * as a non-negative {@code long} value.
 * @since 2.1
 */
  public static long lcm(  long a,  long b) throws MathArithmeticException {
    if (a == 0 || b == 0) {
      return 0;
    }
    long lcm=FastMath.abs(ArithmeticUtils.mulAndCheck(a / gcd(a,b),b));
    if (lcm == Long.MIN_VALUE) {
      throw new MathArithmeticException(LocalizedFormats.LCM_OVERFLOW_64_BITS,a,b);
    }
    return lcm;
  }
  /** 
 * Multiply two integers, checking for overflow.
 * @param x Factor.
 * @param y Factor.
 * @return the product {@code x * y}.
 * @throws MathArithmeticException if the result can not be
 * represented as an {@code int}.
 * @since 1.1
 */
  public static int mulAndCheck(  int x,  int y) throws MathArithmeticException {
    long m=((long)x) * ((long)y);
    if (m < Integer.MIN_VALUE || m > Integer.MAX_VALUE) {
      throw new MathArithmeticException();
    }
    return (int)m;
  }
  /** 
 * Multiply two long integers, checking for overflow.
 * @param a Factor.
 * @param b Factor.
 * @return the product {@code a * b}.
 * @throws MathArithmeticException if the result can not be represented
 * as a {@code long}.
 * @since 1.2
 */
  public static long mulAndCheck(  long a,  long b) throws MathArithmeticException {
    long ret;
    if (a > b) {
      ret=mulAndCheck(b,a);
    }
 else {
      if (a < 0) {
        if (b < 0) {
          if (a >= Long.MAX_VALUE / b) {
            ret=a * b;
          }
 else {
            throw new MathArithmeticException();
          }
        }
 else         if (b > 0) {
          if (Long.MIN_VALUE / b <= a) {
            ret=a * b;
          }
 else {
            throw new MathArithmeticException();
          }
        }
 else {
          ret=0;
        }
      }
 else       if (a > 0) {
        if (a <= Long.MAX_VALUE / b) {
          ret=a * b;
        }
 else {
          throw new MathArithmeticException();
        }
      }
 else {
        ret=0;
      }
    }
    return ret;
  }
  /** 
 * Subtract two integers, checking for overflow.
 * @param x Minuend.
 * @param y Subtrahend.
 * @return the difference {@code x - y}.
 * @throws MathArithmeticException if the result can not be represented
 * as an {@code int}.
 * @since 1.1
 */
  public static int subAndCheck(  int x,  int y) throws MathArithmeticException {
    long s=(long)x - (long)y;
    if (s < Integer.MIN_VALUE || s > Integer.MAX_VALUE) {
      throw new MathArithmeticException(LocalizedFormats.OVERFLOW_IN_SUBTRACTION,x,y);
    }
    return (int)s;
  }
  /** 
 * Subtract two long integers, checking for overflow.
 * @param a Value.
 * @param b Value.
 * @return the difference {@code a - b}.
 * @throws MathArithmeticException if the result can not be represented as a{@code long}.
 * @since 1.2
 */
  public static long subAndCheck(  long a,  long b) throws MathArithmeticException {
    long ret;
    if (b == Long.MIN_VALUE) {
      if (a < 0) {
        ret=a - b;
      }
 else {
        throw new MathArithmeticException(LocalizedFormats.OVERFLOW_IN_ADDITION,a,-b);
      }
    }
 else {
      ret=addAndCheck(a,-b,LocalizedFormats.OVERFLOW_IN_ADDITION);
    }
    return ret;
  }
  /** 
 * Raise an int to an int power.
 * @param k Number to raise.
 * @param e Exponent (must be positive or zero).
 * @return k<sup>e</sup>
 * @throws NotPositiveException if {@code e < 0}.
 */
  public static int pow(  final int k,  int e) throws NotPositiveException {
    if (e < 0) {
      throw new NotPositiveException(LocalizedFormats.EXPONENT,e);
    }
    int result=1;
    int k2p=k;
    while (e != 0) {
      if ((e & 0x1) != 0) {
        result*=k2p;
      }
      k2p*=k2p;
      e=e >> 1;
    }
    return result;
  }
  /** 
 * Raise an int to a long power.
 * @param k Number to raise.
 * @param e Exponent (must be positive or zero).
 * @return k<sup>e</sup>
 * @throws NotPositiveException if {@code e < 0}.
 */
  public static int pow(  final int k,  long e) throws NotPositiveException {
    if (e < 0) {
      throw new NotPositiveException(LocalizedFormats.EXPONENT,e);
    }
    int result=1;
    int k2p=k;
    while (e != 0) {
      if ((e & 0x1) != 0) {
        result*=k2p;
      }
      k2p*=k2p;
      e=e >> 1;
    }
    return result;
  }
  /** 
 * Raise a long to an int power.
 * @param k Number to raise.
 * @param e Exponent (must be positive or zero).
 * @return k<sup>e</sup>
 * @throws NotPositiveException if {@code e < 0}.
 */
  public static long pow(  final long k,  int e) throws NotPositiveException {
    if (e < 0) {
      throw new NotPositiveException(LocalizedFormats.EXPONENT,e);
    }
    long result=1l;
    long k2p=k;
    while (e != 0) {
      if ((e & 0x1) != 0) {
        result*=k2p;
      }
      k2p*=k2p;
      e=e >> 1;
    }
    return result;
  }
  /** 
 * Raise a long to a long power.
 * @param k Number to raise.
 * @param e Exponent (must be positive or zero).
 * @return k<sup>e</sup>
 * @throws NotPositiveException if {@code e < 0}.
 */
  public static long pow(  final long k,  long e) throws NotPositiveException {
    if (e < 0) {
      throw new NotPositiveException(LocalizedFormats.EXPONENT,e);
    }
    long result=1l;
    long k2p=k;
    while (e != 0) {
      if ((e & 0x1) != 0) {
        result*=k2p;
      }
      k2p*=k2p;
      e=e >> 1;
    }
    return result;
  }
  /** 
 * Raise a BigInteger to an int power.
 * @param k Number to raise.
 * @param e Exponent (must be positive or zero).
 * @return k<sup>e</sup>
 * @throws NotPositiveException if {@code e < 0}.
 */
  public static BigInteger pow(  final BigInteger k,  int e) throws NotPositiveException {
    if (e < 0) {
      throw new NotPositiveException(LocalizedFormats.EXPONENT,e);
    }
    return k.pow(e);
  }
  /** 
 * Raise a BigInteger to a long power.
 * @param k Number to raise.
 * @param e Exponent (must be positive or zero).
 * @return k<sup>e</sup>
 * @throws NotPositiveException if {@code e < 0}.
 */
  public static BigInteger pow(  final BigInteger k,  long e) throws NotPositiveException {
    if (e < 0) {
      throw new NotPositiveException(LocalizedFormats.EXPONENT,e);
    }
    BigInteger result=BigInteger.ONE;
    BigInteger k2p=k;
    while (e != 0) {
      if ((e & 0x1) != 0) {
        result=result.multiply(k2p);
      }
      k2p=k2p.multiply(k2p);
      e=e >> 1;
    }
    return result;
  }
  /** 
 * Raise a BigInteger to a BigInteger power.
 * @param k Number to raise.
 * @param e Exponent (must be positive or zero).
 * @return k<sup>e</sup>
 * @throws NotPositiveException if {@code e < 0}.
 */
  public static BigInteger pow(  final BigInteger k,  BigInteger e) throws NotPositiveException {
    if (e.compareTo(BigInteger.ZERO) < 0) {
      throw new NotPositiveException(LocalizedFormats.EXPONENT,e);
    }
    BigInteger result=BigInteger.ONE;
    BigInteger k2p=k;
    while (!BigInteger.ZERO.equals(e)) {
      if (e.testBit(0)) {
        result=result.multiply(k2p);
      }
      k2p=k2p.multiply(k2p);
      e=e.shiftRight(1);
    }
    return result;
  }
  /** 
 * Returns the <a
 * href="http://mathworld.wolfram.com/StirlingNumberoftheSecondKind.html">
 * Stirling number of the second kind</a>, "{@code S(n,k)}", the number of
 * ways of partitioning an {@code n}-element set into {@code k} non-empty
 * subsets.
 * <p>
 * The preconditions are {@code 0 <= k <= n } (otherwise{@code NotPositiveException} is thrown)
 * </p>
 * @param n the size of the set
 * @param k the number of non-empty subsets
 * @return {@code S(n,k)}
 * @throws NotPositiveException if {@code k < 0}.
 * @throws NumberIsTooLargeException if {@code k > n}.
 * @throws MathArithmeticException if some overflow happens, typically for n exceeding 25 and
 * k between 20 and n-2 (S(n,n-1) is handled specifically and does not overflow)
 * @since 3.1
 */
  public static long stirlingS2(  final int n,  final int k) throws NotPositiveException, NumberIsTooLargeException, MathArithmeticException {
    if (k < 0) {
      throw new NotPositiveException(k);
    }
    if (k > n) {
      throw new NumberIsTooLargeException(k,n,true);
    }
    long[][] stirlingS2=STIRLING_S2.get();
    if (stirlingS2 == null) {
      final int maxIndex=26;
      stirlingS2=new long[maxIndex][];
      stirlingS2[0]=new long[]{1l};
      for (int i=1; i < stirlingS2.length; ++i) {
        stirlingS2[i]=new long[i + 1];
        stirlingS2[i][0]=0;
        stirlingS2[i][1]=1;
        stirlingS2[i][i]=1;
        for (int j=2; j < i; ++j) {
          stirlingS2[i][j]=j * stirlingS2[i - 1][j] + stirlingS2[i - 1][j - 1];
        }
      }
      STIRLING_S2.compareAndSet(null,stirlingS2);
    }
    if (n < stirlingS2.length) {
      return stirlingS2[n][k];
    }
 else {
      if (k == 0) {
        return 0;
      }
 else       if (k == 1 || k == n) {
        return 1;
      }
 else       if (k == 2) {
        return (1l << (n - 1)) - 1l;
      }
 else       if (k == n - 1) {
        return binomialCoefficient(n,2);
      }
 else {
        long sum=0;
        long sign=((k & 0x1) == 0) ? 1 : -1;
        for (int j=1; j <= k; ++j) {
          sign=-sign;
          sum+=sign * binomialCoefficient(k,j) * pow(j,n);
          if (sum < 0) {
            throw new MathArithmeticException(LocalizedFormats.ARGUMENT_OUTSIDE_DOMAIN,n,0,stirlingS2.length - 1);
          }
        }
        return sum / factorial(k);
      }
    }
  }
  /** 
 * Add two long integers, checking for overflow.
 * @param a Addend.
 * @param b Addend.
 * @param pattern Pattern to use for any thrown exception.
 * @return the sum {@code a + b}.
 * @throws MathArithmeticException if the result cannot be represented
 * as a {@code long}.
 * @since 1.2
 */
  private static long addAndCheck(  long a,  long b,  Localizable pattern) throws MathArithmeticException {
    long ret;
    if (a > b) {
      ret=addAndCheck(b,a,pattern);
    }
 else {
      if (a < 0) {
        if (b < 0) {
          if (Long.MIN_VALUE - b <= a) {
            ret=a + b;
          }
 else {
            throw new MathArithmeticException(pattern,a,b);
          }
        }
 else {
          ret=a + b;
        }
      }
 else {
        if (a <= Long.MAX_VALUE - b) {
          ret=a + b;
        }
 else {
          throw new MathArithmeticException(pattern,a,b);
        }
      }
    }
    return ret;
  }
  /** 
 * Check binomial preconditions.
 * @param n Size of the set.
 * @param k Size of the subsets to be counted.
 * @throws NotPositiveException if {@code n < 0}.
 * @throws NumberIsTooLargeException if {@code k > n}.
 */
  private static void checkBinomial(  final int n,  final int k) throws NumberIsTooLargeException, NotPositiveException {
    if (n < k) {
      throw new NumberIsTooLargeException(LocalizedFormats.BINOMIAL_INVALID_PARAMETERS_ORDER,k,n,true);
    }
    if (n < 0) {
      throw new NotPositiveException(LocalizedFormats.BINOMIAL_NEGATIVE_PARAMETER,n);
    }
  }
  /** 
 * Returns true if the argument is a power of two.
 * @param n the number to test
 * @return true if the argument is a power of two
 */
  public static boolean isPowerOfTwo(  long n){
    return (n > 0) && ((n & (n - 1)) == 0);
  }
}
