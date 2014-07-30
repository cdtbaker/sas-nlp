package org.ojalgo.scalar;
import java.math.BigDecimal;
import org.ojalgo.type.context.NumberContext;
/** 
 * <p>
 * A {@linkplain Scalar} is:
 * <ol>
 * <li>An abstraction of a vector/matrix element.</li>
 * <li>A {@linkplain Number} decorator, increasing the number of things you can do with them.</li>
 * </ol>
 * </p>
 * <p>
 * Theoretically it is a Field or at least a Division ring.
 * </p>
 * <p>
 * A group is a set of elements paired with a binary operation. Four conditions called the group axioms must be
 * satisfied:
 * <ol>
 * <li>Closure: If A and B are both members of the set then the result of A op B is also a member.</li>
 * <li>Associativity: Invocation/execution order doesn't matter - ((A op B) op C) == (A op (B op C))</li>
 * <li>The identity property: There is an identity element in the set, I, so that I op A == A op I == A</li>
 * <li>The inverse property: For each element in the set there must be an inverse element (opposite or reciprocal) so
 * that A<sup>-1</sup> op A == A op A<sup>-1</sup> == I</li>
 * </ol>
 * Note that commutativity is not a requirement - A op B doesn't always have to be equal to B op A. If the operation is
 * commutative then the group is called an abelian group or simply a commutative group.
 * </p>
 * <p>
 * A ring is a commutative {@linkplain Group} (add operation) with a second binary operation (multiply) that is
 * distributive over the commutative group operation and is associative.
 * </p>
 * <p>
 * A field is a commutative ring (even the multiplication operation) with notions of addition, subtraction,
 * multiplication, and division. Any field may be used as the scalars for a vector space, which is the standard general
 * context for linear algebra.
 * </p>
 * <p>
 * A division ring is a ring in which division is possible. Division rings differ from fields only in that their
 * multiplication is not required to be commutative.
 * </p>
 * @author apete
 * @see <a href="http://en.wikipedia.org/wiki/Operation_%28mathematics%29">Operation</a>
 * @see <a href="http://en.wikipedia.org/wiki/Group_%28mathematics%29">Group</a>
 * @see <a href="http://en.wikipedia.org/wiki/Ring_%28mathematics%29">Ring</a>
 * @see <a href="http://en.wikipedia.org/wiki/Field_%28mathematics%29">Field</a>
 * @see <a href="http://en.wikipedia.org/wiki/Division_ring">Division ring</a>
 * @see <a href="http://en.wikipedia.org/wiki/Vector_space">Vector space</a>
 */
public interface Scalar<N extends Number> extends Comparable<N> {
public interface Factory<N extends Number> {
    N cast(    double value);
    N cast(    Number number);
    Scalar<N> convert(    double value);
    Scalar<N> convert(    Number number);
    /** 
 * @return The multiplicative identity element
 */
    Scalar<N> one();
    /** 
 * @return The additive identity element
 */
    Scalar<N> zero();
  }
  /** 
 * @see #add(double)
 * @see #add(Number)
 * @see #divide(double)
 * @see #divide(Number)
 * @see #multiply(double)
 * @see #multiply(Number)
 * @see #subtract(double)
 * @see #subtract(Number)
 */
  Scalar<N> add(  double arg);
  /** 
 * @see #add(double)
 * @see #add(Number)
 * @see #divide(double)
 * @see #divide(Number)
 * @see #multiply(double)
 * @see #multiply(Number)
 * @see #subtract(double)
 * @see #subtract(Number)
 */
  Scalar<N> add(  N arg);
  /** 
 * @see #conjugate()
 * @see #invert()
 * @see #negate()
 */
  Scalar<N> conjugate();
  /** 
 * @see #add(double)
 * @see #add(Number)
 * @see #divide(double)
 * @see #divide(Number)
 * @see #multiply(double)
 * @see #multiply(Number)
 * @see #subtract(double)
 * @see #subtract(Number)
 */
  Scalar<N> divide(  double arg);
  /** 
 * @see #add(double)
 * @see #add(Number)
 * @see #divide(double)
 * @see #divide(Number)
 * @see #multiply(double)
 * @see #multiply(Number)
 * @see #subtract(double)
 * @see #subtract(Number)
 */
  Scalar<N> divide(  N arg);
  double doubleValue();
  N getNumber();
  /** 
 * @see #conjugate()
 * @see #invert()
 * @see #negate()
 */
  Scalar<N> invert();
  /** 
 * @return true if this is equal to its own modulus (non-negative real part and no imaginary part); otherwise false.
 * @see #isAbsolute()
 * @see #isInfinite()
 * @see #isNaN()
 * @see #isReal()
 * @see #isPositive()
 * @see #isZero()
 */
  boolean isAbsolute();
  /** 
 * @see #isAbsolute()
 * @see #isInfinite()
 * @see #isNaN()
 * @see #isReal()
 * @see #isPositive()
 * @see #isZero()
 */
  boolean isInfinite();
  /** 
 * @see #isAbsolute()
 * @see #isInfinite()
 * @see #isNaN()
 * @see #isReal()
 * @see #isPositive()
 * @see #isZero()
 */
  boolean isNaN();
  /** 
 * Strictly Positive, and definately real. Real, as defined by {@link #isReal()}, not zero, as defined by{@link #isZero()}, and > 0.0.
 * @return true if the real part is strictly positive (not zero) and the imaginary part negligible; otherwise false.
 * @see #isAbsolute()
 * @see #isInfinite()
 * @see #isNaN()
 * @see #isReal()
 * @see #isPositive()
 * @see #isZero()
 */
  boolean isPositive();
  /** 
 * @return true if there is the imaginary part is negligible; otherwise false.
 * @see #isAbsolute()
 * @see #isInfinite()
 * @see #isNaN()
 * @see #isReal()
 * @see #isPositive()
 * @see #isZero()
 */
  boolean isReal();
  /** 
 * Intends to capture if a scalar is numerically/practically zero, and in a way that is concistent between different
 * implementations. The potential exactness of {@link BigScalar} and {@link RationalNumber} should not be reflected
 * here.
 * @return true if the modulus is (practically) zero; otherwise false.
 * @see #isAbsolute()
 * @see #isInfinite()
 * @see #isNaN()
 * @see #isReal()
 * @see #isPositive()
 * @see #isZero()
 */
  boolean isZero();
  /** 
 * @see #add(double)
 * @see #add(Number)
 * @see #divide(double)
 * @see #divide(Number)
 * @see #multiply(double)
 * @see #multiply(Number)
 * @see #subtract(double)
 * @see #subtract(Number)
 */
  Scalar<N> multiply(  double arg);
  /** 
 * @see #add(double)
 * @see #add(Number)
 * @see #divide(double)
 * @see #divide(Number)
 * @see #multiply(double)
 * @see #multiply(Number)
 * @see #subtract(double)
 * @see #subtract(Number)
 */
  Scalar<N> multiply(  N arg);
  /** 
 * @see #conjugate()
 * @see #invert()
 * @see #negate()
 */
  Scalar<N> negate();
  double norm();
  Scalar<N> signum();
  /** 
 * @see #add(double)
 * @see #add(Number)
 * @see #divide(double)
 * @see #divide(Number)
 * @see #multiply(double)
 * @see #multiply(Number)
 * @see #subtract(double)
 * @see #subtract(Number)
 */
  Scalar<N> subtract(  double arg);
  /** 
 * @see #add(double)
 * @see #add(Number)
 * @see #divide(double)
 * @see #divide(Number)
 * @see #multiply(double)
 * @see #multiply(Number)
 * @see #subtract(double)
 * @see #subtract(Number)
 */
  Scalar<N> subtract(  N arg);
  BigDecimal toBigDecimal();
  String toPlainString(  NumberContext context);
  String toString(  NumberContext context);
}