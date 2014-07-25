package org.ojalgo.access;
import org.ojalgo.random.RandomNumber;
import org.ojalgo.scalar.Scalar;
public interface AccessAnyD<N extends Number> extends StructureAnyD, Access1D<N> {
  /** 
 * This interface mimics {@linkplain Fillable}, but methods return the builder
 * instance instead, and then adds the {@link #build()} method.
 * @author apete
 */
public interface Builder<I extends AccessAnyD<?>> extends StructureAnyD, Access1D.Builder<I> {
    I build();
    Builder<I> set(    long[] reference,    double value);
    Builder<I> set(    long[] reference,    Number value);
  }
public interface Elements extends StructureAnyD, Access1D.Elements {
    /** 
 * @see Scalar#isAbsolute()
 */
    boolean isAbsolute(    long[] reference);
    /** 
 * @see Scalar#isInfinite()
 */
    boolean isInfinite(    long[] reference);
    /** 
 * @see Scalar#isNaN()
 */
    boolean isNaN(    long[] reference);
    /** 
 * @see Scalar#isPositive()
 */
    boolean isPositive(    long[] reference);
    /** 
 * @see Scalar#isReal()
 */
    boolean isReal(    long[] reference);
    /** 
 * @see Scalar#isZero()
 */
    boolean isZero(    long[] reference);
  }
public interface Factory<I extends AccessAnyD<?>> {
    I copy(    AccessAnyD<?> source);
    I makeRandom(    long[] structure,    RandomNumber distribution);
    I makeZero(    long... structure);
  }
public interface Fillable<N extends Number> extends StructureAnyD, Access1D.Fillable<N> {
    void set(    long[] reference,    double value);
    void set(    long[] reference,    Number value);
  }
public interface Modifiable<N extends Number> extends StructureAnyD, Access1D.Modifiable<N> {
  }
public interface Visitable<N extends Number> extends StructureAnyD, Access1D.Visitable<N> {
  }
  double doubleValue(  long[] reference);
  N get(  long[] reference);
}
