package org.ojalgo.array;
import java.io.Serializable;
import java.util.Iterator;
import java.util.RandomAccess;
import org.ojalgo.access.Access1D;
import org.ojalgo.access.Iterator1D;
import org.ojalgo.function.BinaryFunction;
import org.ojalgo.function.ParameterFunction;
import org.ojalgo.function.UnaryFunction;
import org.ojalgo.function.VoidFunction;
import org.ojalgo.netio.ASCII;
import org.ojalgo.scalar.Scalar;
/** 
 * <p>
 * A BasicArray is one-dimensional, but designed to easily be extended or encapsulated, and then treated as
 * arbitrary-dimensional. It stores/handles (any subclass of) {@linkplain java.lang.Number} elements depending on the
 * subclass/implementation.
 * </p>
 * <p>
 * This abstract class defines a set of methods to access and modify array elements. It does not "know" anything about
 * linear algebra or similar.
 * </p>
 * @author apete
 */
abstract class BasicArray<N extends Number> implements Access1D<N>, Access1D.Elements, Access1D.Fillable<N>, Access1D.Modifiable<N>, Access1D.Visitable<N>, RandomAccess, Serializable {
  public final int length;
  @SuppressWarnings("unused") private BasicArray(){
    this(0);
  }
  protected BasicArray(  final int aLength){
    super();
    length=aLength;
  }
  public long count(){
    return length;
  }
  public Iterator<N> iterator(){
    return new Iterator1D<N>(this);
  }
  public void modifyAll(  final UnaryFunction<N> function){
    this.modify(0,length,1,function);
  }
  public void modifyRange(  final long first,  final long limit,  final UnaryFunction<N> function){
    this.modify((int)first,(int)limit,1,function);
  }
  public int size(){
    return length;
  }
  @Override public String toString(){
    final StringBuilder retVal=new StringBuilder();
    retVal.append(ASCII.LCB);
    retVal.append(ASCII.SP);
    final int tmpLength=length;
    if (tmpLength >= 1) {
      retVal.append(this.get(0).toString());
      for (int i=1; i < tmpLength; i++) {
        retVal.append(ASCII.COMMA);
        retVal.append(ASCII.SP);
        retVal.append(this.get(i).toString());
      }
      retVal.append(ASCII.SP);
    }
    retVal.append(ASCII.RCB);
    return retVal.toString();
  }
  public void visitAll(  final VoidFunction<N> visitor){
    this.visit(0,length,1,visitor);
  }
  public void visitRange(  final long first,  final long limit,  final VoidFunction<N> visitor){
    this.visit((int)first,(int)limit,1,visitor);
  }
  /** 
 * <p>
 * A utility facade that conveniently/consistently presents the {@linkplain org.ojalgo.array.BasicArray} as a
 * one-dimensional array. Note that you will modify the actual array by accessing it through this facade.
 * </p>
 * <p>
 * Disregards the array structure, and simply treats it as one-domensional.
 * </p>
 */
  protected final Array1D<N> asArray1D(){
    return new Array1D<N>(this);
  }
  /** 
 * <p>
 * A utility facade that conveniently/consistently presents the {@linkplain org.ojalgo.array.BasicArray} as a
 * two-dimensional array. Note that you will modify the actual array by accessing it through this facade.
 * </p>
 * <p>
 * If "this" has more than two dimensions then only the first plane of the first cube of the first... is
 * used/accessed. If this only has one dimension then everything is assumed to be in the first column of the first
 * plane of the first cube...
 * </p>
 */
  protected final Array2D<N> asArray2D(  final int aRowDim,  final int aColDim){
    return new Array2D<N>(this,aRowDim,aColDim);
  }
  /** 
 * <p>
 * A utility facade that conveniently/consistently presents the {@linkplain org.ojalgo.array.BasicArray} as a
 * multi-dimensional array. Note that you will modify the actual array by accessing it through this facade.
 * </p>
 */
  protected final ArrayAnyD<N> asArrayAnyD(  final int[] aStructure){
    return new ArrayAnyD<N>(this,aStructure);
  }
  /** 
 * <p>
 * A utility facade that conveniently/consistently presents the {@linkplain org.ojalgo.array.BasicArray} as a
 * multi-dimensional array. Note that you will modify the actual array by accessing it through this facade.
 * </p>
 */
  protected final ArrayAnyD<N> asArrayAnyD(  final long[] structure){
    final int[] tmpStructure=new int[structure.length];
    for (int i=0; i < tmpStructure.length; i++) {
      tmpStructure[i]=(int)structure[i];
    }
    return new ArrayAnyD<N>(this,tmpStructure);
  }
  protected abstract void exchange(  int aFirstA,  int aFirstB,  int aStep,  int aCount);
  protected abstract void fill(  Access1D<?> anArg);
  protected abstract void fill(  final int aFirst,  final int aLimit,  final Access1D<N> aLeftArg,  final BinaryFunction<N> aFunc,  final Access1D<N> aRightArg);
  protected abstract void fill(  final int aFirst,  final int aLimit,  final Access1D<N> aLeftArg,  final BinaryFunction<N> aFunc,  final N aRightArg);
  protected abstract void fill(  int aFirst,  int aLimit,  int aStep,  N aNmbr);
  protected abstract void fill(  final int aFirst,  final int aLimit,  final N aLeftArg,  final BinaryFunction<N> aFunc,  final Access1D<N> aRightArg);
  protected abstract int getIndexOfLargest(  int aFirst,  int aLimit,  int aStep);
  /** 
 * @see Scalar#isReal()
 */
  protected abstract boolean isReal(  int anInd);
  /** 
 * @see Scalar#isZero()
 */
  protected abstract boolean isZero(  int anInd);
  protected abstract boolean isZeros(  int aFirst,  int aLimit,  int aStep);
  protected abstract void modify(  int aFirst,  int aLimit,  int aStep,  Access1D<N> aLeftArg,  BinaryFunction<N> aFunc);
  protected abstract void modify(  int aFirst,  int aLimit,  int aStep,  BinaryFunction<N> aFunc,  Access1D<N> aRightArg);
  protected abstract void modify(  int aFirst,  int aLimit,  int aStep,  BinaryFunction<N> aFunc,  N aRightArg);
  protected abstract void modify(  int aFirst,  int aLimit,  int aStep,  N aLeftArg,  BinaryFunction<N> aFunc);
  protected abstract void modify(  int aFirst,  int aLimit,  int aStep,  ParameterFunction<N> aFunc,  int aParam);
  protected abstract void modify(  int aFirst,  int aLimit,  int aStep,  UnaryFunction<N> aFunc);
  /** 
 * @see java.util.Arrays#binarySearch(Object[],Object)
 * @see #sortAscending()
 * @throws UnsupportedOperationException if the this operation is not supported by this implementation/subclass
 */
  protected abstract int searchAscending(  N aNmbr);
  /** 
 * @see java.util.Arrays#sort(Object[])
 * @see #searchAscending(Number)
 * @throws UnsupportedOperationException if the this operation is not supported by this implementation/subclass
 */
  protected abstract void sortAscending();
  protected abstract Scalar<N> toScalar(  int anInd);
  protected abstract void visit(  int aFirst,  int aLimit,  int aStep,  VoidFunction<N> aVisitor);
}
