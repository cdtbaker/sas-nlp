package org.ojalgo.access;
import java.util.List;
import org.ojalgo.function.UnaryFunction;
import org.ojalgo.function.VoidFunction;
import org.ojalgo.random.RandomNumber;
import org.ojalgo.scalar.Scalar;
public interface Access2D<N extends Number> extends Structure2D, Access1D<N> {
  /** 
 * This interface mimics {@linkplain Fillable}, but methods return the builder instance instead, and then adds the{@link #build()} method.
 * @author apete
 */
public interface Builder<I extends Access2D<?>> extends Structure2D, Access1D.Builder<I> {
    I build();
    Builder<I> fillColumn(    long row,    long column,    Number value);
    Builder<I> fillDiagonal(    long row,    long column,    Number value);
    Builder<I> fillRow(    long row,    long column,    Number value);
    Builder<I> set(    long row,    long column,    double value);
    Builder<I> set(    long row,    long column,    Number value);
  }
public interface Elements extends Structure2D, Access1D.Elements {
    /** 
 * @see Scalar#isAbsolute()
 */
    boolean isAbsolute(    long row,    long column);
    /** 
 * @see Scalar#isInfinite()
 */
    boolean isInfinite(    long row,    long column);
    /** 
 * @see Scalar#isNaN()
 */
    boolean isNaN(    long row,    long column);
    /** 
 * @see Scalar#isPositive()
 */
    boolean isPositive(    long row,    long column);
    /** 
 * @see Scalar#isReal()
 */
    boolean isReal(    long row,    long column);
    /** 
 * @see Scalar#isZero()
 */
    boolean isZero(    long row,    long column);
  }
public interface Factory<I extends Access2D<?>> {
    I columns(    Access1D<?>... source);
    I columns(    double[]... source);
    @SuppressWarnings("unchecked") I columns(    List<? extends Number>... source);
    I columns(    Number[]... source);
    I copy(    Access2D<?> source);
    I makeEye(    long rows,    long columns);
    I makeRandom(    long rows,    long columns,    RandomNumber distribution);
    I makeZero(    long rows,    long columns);
    I rows(    Access1D<?>... source);
    I rows(    double[]... source);
    @SuppressWarnings("unchecked") I rows(    List<? extends Number>... source);
    I rows(    Number[]... source);
  }
public interface Fillable<N extends Number> extends Structure2D, Access1D.Fillable<N> {
    void fillColumn(    long row,    long column,    N value);
    void fillDiagonal(    long row,    long column,    N value);
    void fillRow(    long row,    long column,    N value);
    void set(    long row,    long column,    double value);
    void set(    long row,    long column,    Number value);
  }
public interface Iterable2D<N extends Number> {
    Iterable<Access1D<N>> columns();
    Iterable<Access1D<N>> rows();
  }
public interface Modifiable<N extends Number> extends Structure2D, Access1D.Modifiable<N> {
    void modifyColumn(    long row,    long column,    UnaryFunction<N> function);
    void modifyDiagonal(    long row,    long column,    UnaryFunction<N> function);
    void modifyRow(    long row,    long column,    UnaryFunction<N> function);
  }
public interface Visitable<N extends Number> extends Structure2D, Access1D.Visitable<N> {
    void visitColumn(    long row,    long column,    VoidFunction<N> visitor);
    void visitDiagonal(    long row,    long column,    VoidFunction<N> visitor);
    void visitRow(    long row,    long column,    VoidFunction<N> visitor);
  }
  /** 
 * Extracts one element of this matrix as a double.
 * @param row A row index.
 * @param column A column index.
 * @return One matrix element
 */
  double doubleValue(  long row,  long column);
  N get(  long row,  long column);
}
