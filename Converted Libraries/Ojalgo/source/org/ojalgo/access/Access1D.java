package org.ojalgo.access;
import java.util.List;
import org.ojalgo.function.UnaryFunction;
import org.ojalgo.function.VoidFunction;
import org.ojalgo.random.RandomNumber;
import org.ojalgo.scalar.Scalar;
public interface Access1D<N extends Number> extends Structure1D, Iterable<N> {
public interface Builder<I extends Access1D<?>> extends Structure1D {
    I build();
    Builder<I> fillAll(    final Number value);
    Builder<I> set(    long index,    double value);
    Builder<I> set(    long index,    Number value);
  }
public interface Elements extends Structure1D {
    /** 
 * @see Scalar#isAbsolute()
 */
    boolean isAbsolute(    long index);
    /** 
 * @see Scalar#isInfinite()
 */
    boolean isInfinite(    long index);
    /** 
 * @see Scalar#isNaN()
 */
    boolean isNaN(    long index);
    /** 
 * @see Scalar#isPositive()
 */
    boolean isPositive(    long index);
    /** 
 * @see Scalar#isReal()
 */
    boolean isReal(    long index);
    /** 
 * @see Scalar#isZero()
 */
    boolean isZero(    long index);
  }
public interface Factory<I extends Access1D<?>> {
    I copy(    Access1D<?> source);
    I copy(    double... source);
    I copy(    List<? extends Number> source);
    I copy(    Number... source);
    I makeRandom(    long count,    RandomNumber distribution);
    I makeZero(    long count);
  }
public interface Fillable<N extends Number> extends Structure1D {
    void fillAll(    N value);
    void fillRange(    long first,    long limit,    N value);
    /** 
 * Compatible with {@linkplain List#set(int,Object)}
 * @deprecated v35 Use {@link #set(long,double)} instead
 */
    @Deprecated N set(    int index,    Number value);
    void set(    long index,    double value);
    void set(    long index,    Number value);
  }
public interface Modifiable<N extends Number> extends Structure1D {
    void modifyAll(    UnaryFunction<N> function);
    void modifyRange(    long first,    long limit,    UnaryFunction<N> function);
  }
public interface Visitable<N extends Number> extends Structure1D {
    void visitAll(    VoidFunction<N> visitor);
    void visitRange(    long first,    long limit,    VoidFunction<N> visitor);
  }
  double doubleValue(  long index);
  N get(  long index);
}
