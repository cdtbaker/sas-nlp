package org.ojalgo.matrix.store;
import java.io.Serializable;
import java.util.List;
import org.ojalgo.access.Access1D;
import org.ojalgo.access.Access2D;
import org.ojalgo.array.SimpleArray;
import org.ojalgo.function.BinaryFunction;
import org.ojalgo.function.FunctionSet;
import org.ojalgo.function.UnaryFunction;
import org.ojalgo.function.aggregator.AggregatorCollection;
import org.ojalgo.matrix.transformation.Householder;
import org.ojalgo.matrix.transformation.Rotation;
import org.ojalgo.random.RandomNumber;
import org.ojalgo.scalar.Scalar;
/** 
 * <p>
 * PhysicalStore:s, as opposed to MatrixStore:s, are mutable. The vast majorty of the methods defined here return void
 * and none return {@linkplain PhysicalStore} or {@linkplain MatrixStore}.
 * </p>
 * <p>
 * This interface and its implementations are central to ojAlgo.
 * </p>
 * @author apete
 */
public interface PhysicalStore<N extends Number> extends MatrixStore<N>, Access2D.Elements, Access2D.Fillable<N>, Access2D.Modifiable<N>, Access2D.Visitable<N> {
public static interface Factory<N extends Number,I extends PhysicalStore<N>> extends Access2D.Factory<I>, Serializable {
    AggregatorCollection<N> aggregator();
    I conjugate(    Access2D<?> source);
    FunctionSet<N> function();
    /** 
 * @deprecated v35 Use {@link #aggregator()} instead
 */
    @Deprecated AggregatorCollection<N> getAggregatorCollection();
    /** 
 * @deprecated v35 Use {@link #function()} instead
 */
    @Deprecated FunctionSet<N> getFunctionSet();
    /** 
 * @deprecated v35 Use {@link #scalar()} instead.
 */
    @Deprecated N getNumber(    double value);
    /** 
 * @deprecated v35 Use {@link #scalar()} instead.
 */
    @Deprecated N getNumber(    Number value);
    /** 
 * @deprecated v35 Use {@link #scalar()} instead.
 */
    @Deprecated Scalar<N> getStaticOne();
    /** 
 * @deprecated v35 Use {@link #scalar()} instead.
 */
    @Deprecated Scalar<N> getStaticZero();
    SimpleArray<N> makeArray(    int length);
    Householder<N> makeHouseholder(    int length);
    Rotation<N> makeRotation(    int low,    int high,    double cos,    double sin);
    Rotation<N> makeRotation(    int low,    int high,    N cos,    N sin);
    Scalar.Factory<N> scalar();
    /** 
 * @deprecated v35 Use {@link #scalar()} instead.
 */
    @Deprecated Scalar<N> toScalar(    double value);
    /** 
 * @deprecated v35 Use {@link #scalar()} instead.
 */
    @Deprecated Scalar<N> toScalar(    Number value);
    I transpose(    Access2D<?> source);
  }
  public static final Factory<Double,PhysicalStore<Double>> PRIMITIVE=new Factory<Double,PhysicalStore<Double>>(){
    public AggregatorCollection<Double> aggregator(){
      return PrimitiveDenseStore.FACTORY.aggregator();
    }
    public PhysicalStore<Double> columns(    final Access1D<?>... source){
      return PrimitiveDenseStore.FACTORY.columns(source);
    }
    public PhysicalStore<Double> columns(    final double[]... source){
      return null;
    }
    public PhysicalStore<Double> columns(    final List<? extends Number>... source){
      return null;
    }
    public PhysicalStore<Double> columns(    final Number[]... source){
      return null;
    }
    public PhysicalStore<Double> conjugate(    final Access2D<?> source){
      return null;
    }
    public PhysicalStore<Double> copy(    final Access2D<?> source){
      return null;
    }
    public FunctionSet<Double> function(){
      return null;
    }
    public AggregatorCollection<Double> getAggregatorCollection(){
      return null;
    }
    public FunctionSet<Double> getFunctionSet(){
      return null;
    }
    public Double getNumber(    final double value){
      return null;
    }
    public Double getNumber(    final Number value){
      return null;
    }
    public Scalar<Double> getStaticOne(){
      return null;
    }
    public Scalar<Double> getStaticZero(){
      return null;
    }
    public SimpleArray<Double> makeArray(    final int length){
      return null;
    }
    public PhysicalStore<Double> makeEye(    final long rows,    final long columns){
      return null;
    }
    public Householder<Double> makeHouseholder(    final int length){
      return null;
    }
    public PhysicalStore<Double> makeRandom(    final long rows,    final long columns,    final RandomNumber distribution){
      return null;
    }
    public Rotation<Double> makeRotation(    final int low,    final int high,    final double cos,    final double sin){
      return null;
    }
    public Rotation<Double> makeRotation(    final int low,    final int high,    final Double cos,    final Double sin){
      return null;
    }
    public PhysicalStore<Double> makeZero(    final long rows,    final long columns){
      return null;
    }
    public PhysicalStore<Double> rows(    final Access1D<?>... source){
      return null;
    }
    public PhysicalStore<Double> rows(    final double[]... source){
      return null;
    }
    public PhysicalStore<Double> rows(    final List<? extends Number>... source){
      return null;
    }
    public PhysicalStore<Double> rows(    final Number[]... source){
      return null;
    }
    public org.ojalgo.scalar.Scalar.Factory<Double> scalar(){
      return null;
    }
    public Scalar<Double> toScalar(    final double value){
      return null;
    }
    public Scalar<Double> toScalar(    final Number value){
      return null;
    }
    public PhysicalStore<Double> transpose(    final Access2D<?> source){
      return null;
    }
  }
;
  /** 
 * @return The elements of the physical store as a fixed size (1 dimensional) list. The elements may be accessed
 * either row or colomn major.
 */
  List<N> asList();
  /** 
 * <p>
 * <b>c</b>olumn <b>a</b> * <b>x</b> <b>p</b>lus <b>y</b>
 * </p>
 * [this(*,aColY)] = aSclrA [this(*,aColX)] + [this(*,aColY)]
 * @deprecated v32 Let me know if you need this
 */
  @Deprecated void caxpy(  final N aSclrA,  final int aColX,  final int aColY,  final int aFirstRow);
  void exchangeColumns(  int aColA,  int aColB);
  void exchangeRows(  int aRowA,  int aRowB);
  void fillByMultiplying(  final Access1D<N> leftMtrx,  final Access1D<N> rightMtrx);
  void fillConjugated(  Access2D<? extends Number> source);
  void fillMatching(  Access1D<? extends Number> source);
  /** 
 * <p>
 * Will replace the elements of [this] with the results of element wise invocation of the input binary funtion:
 * </p>
 * <code>this(i,j) = aFunc.invoke(aLeftArg(i,j),aRightArg(i,j))</code>
 */
  void fillMatching(  Access1D<N> leftArg,  BinaryFunction<N> func,  Access1D<N> rightArg);
  /** 
 * <p>
 * Will replace the elements of [this] with the results of element wise invocation of the input binary funtion:
 * </p>
 * <code>this(i,j) = aFunc.invoke(aLeftArg(i,j),aRightArg))</code>
 */
  void fillMatching(  Access1D<N> leftArg,  BinaryFunction<N> func,  N rightArg);
  /** 
 * <p>
 * Will replace the elements of [this] with the results of element wise invocation of the input binary funtion:
 * </p>
 * <code>this(i,j) = aFunc.invoke(aLeftArg,aRightArg(i,j))</code>
 */
  void fillMatching(  N leftArg,  BinaryFunction<N> func,  Access1D<N> rightArg);
  void fillTransposed(  Access2D<? extends Number> source);
  /** 
 * <p>
 * <b>m</b>atrix <b>a</b> * <b>x</b> <b>p</b>lus <b>y</b>
 * </p>
 * [this] = aSclrA [aMtrxX] + [this]
 * @deprecated v32 Let me know if you need this
 */
  @Deprecated void maxpy(  final N aSclrA,  final MatrixStore<N> aMtrxX);
  void modifyOne(  int aRow,  int aCol,  UnaryFunction<N> aFunc);
  /** 
 * <p>
 * <b>r</b>ow <b>a</b> * <b>x</b> <b>p</b>lus <b>y</b>
 * </p>
 * [this(aRowY,*)] = aSclrA [this(aRowX,*)] + [this(aRowY,*)]
 * @deprecated v32 Let me know if you need this
 */
  @Deprecated void raxpy(  final N aSclrA,  final int aRowX,  final int aRowY,  final int aFirstCol);
  void transformLeft(  Householder<N> aTransf,  int aFirstCol);
  /** 
 * <p>
 * As in {@link MatrixStore#multiplyLeft(MatrixStore)} where the left/parameter matrix is a plane rotation.
 * </p>
 * <p>
 * Multiplying by a plane rotation from the left means that [this] gets two of its rows updated to new combinations
 * of those two (current) rows.
 * </p>
 * <p>
 * There are two ways to transpose/invert a rotation. Either you negate the angle or you interchange the two indeces
 * that define the rotation plane.
 * </p>
 * @see #transformRight(Rotation)
 */
  void transformLeft(  Rotation<N> aTransf);
  void transformRight(  Householder<N> aTransf,  int aFirstRow);
  /** 
 * <p>
 * As in {@link MatrixStore#multiplyRight(MatrixStore)} where the right/parameter matrix is a plane rotation.
 * </p>
 * <p>
 * Multiplying by a plane rotation from the right means that [this] gets two of its columns updated to new
 * combinations of those two (current) columns.
 * </p>
 * <p>
 * There result is undefined if the two input indeces are the same (in which case the rotation plane is undefined).
 * </p>
 * @see #transformLeft(Rotation)
 */
  void transformRight(  Rotation<N> aTransf);
}
