package org.ojalgo.array;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.RandomAccess;
import org.ojalgo.access.Access1D;
import org.ojalgo.access.Iterator1D;
import org.ojalgo.function.BinaryFunction;
import org.ojalgo.function.ParameterFunction;
import org.ojalgo.function.UnaryFunction;
import org.ojalgo.function.VoidFunction;
import org.ojalgo.random.RandomNumber;
import org.ojalgo.scalar.ComplexNumber;
import org.ojalgo.scalar.RationalNumber;
import org.ojalgo.scalar.Scalar;
import org.ojalgo.type.TypeUtils;
/** 
 * Array1D
 * @author apete
 */
public final class Array1D<N extends Number> extends AbstractList<N> implements Access1D<N>, Access1D.Elements, Access1D.Fillable<N>, Access1D.Modifiable<N>, Access1D.Visitable<N>, RandomAccess, Serializable {
public static interface Factory<N extends Number> extends Access1D.Factory<Array1D<N>> {
    Array1D<N> wrap(    final SimpleArray<N> aSimple);
  }
  public static final Array1D.Factory<BigDecimal> BIG=new Array1D.Factory<BigDecimal>(){
    public Array1D<BigDecimal> copy(    final Access1D<?> source){
      final int tmpSize=source.size();
      final BigDecimal[] tmpArray=new BigDecimal[tmpSize];
      for (int i=0; i < tmpSize; i++) {
        tmpArray[i]=TypeUtils.toBigDecimal(source.get(i));
      }
      return new BigArray(tmpArray).asArray1D();
    }
    public Array1D<BigDecimal> copy(    final double... source){
      final int tmpSize=source.length;
      final BigDecimal[] tmpArray=new BigDecimal[tmpSize];
      for (int i=0; i < tmpSize; i++) {
        tmpArray[i]=TypeUtils.toBigDecimal(source[i]);
      }
      return new BigArray(tmpArray).asArray1D();
    }
    public Array1D<BigDecimal> copy(    final List<? extends Number> source){
      final int tmpSize=source.size();
      final BigDecimal[] tmpArray=new BigDecimal[tmpSize];
      for (int i=0; i < tmpSize; i++) {
        tmpArray[i]=TypeUtils.toBigDecimal(source.get(i));
      }
      return new BigArray(tmpArray).asArray1D();
    }
    public Array1D<BigDecimal> copy(    final Number... source){
      final int tmpSize=source.length;
      final BigDecimal[] tmpArray=new BigDecimal[tmpSize];
      for (int i=0; i < tmpSize; i++) {
        tmpArray[i]=TypeUtils.toBigDecimal(source[i]);
      }
      return new BigArray(tmpArray).asArray1D();
    }
    public Array1D<BigDecimal> makeRandom(    final long count,    final RandomNumber distribution){
      final BigDecimal[] tmpArray=new BigDecimal[(int)count];
      for (int i=0; i < count; i++) {
        tmpArray[i]=TypeUtils.toBigDecimal(distribution);
      }
      return new BigArray(tmpArray).asArray1D();
    }
    public Array1D<BigDecimal> makeZero(    final long count){
      return new BigArray((int)count).asArray1D();
    }
    public Array1D<BigDecimal> wrap(    final SimpleArray<BigDecimal> aSimple){
      return new BigArray(((SimpleArray.Big)aSimple).data).asArray1D();
    }
  }
;
  public static final Array1D.Factory<ComplexNumber> COMPLEX=new Array1D.Factory<ComplexNumber>(){
    public Array1D<ComplexNumber> copy(    final Access1D<?> source){
      final int tmpSize=source.size();
      final ComplexNumber[] tmpArray=new ComplexNumber[tmpSize];
      for (int i=0; i < tmpSize; i++) {
        tmpArray[i]=TypeUtils.toComplexNumber(source.get(i));
      }
      return new ComplexArray(tmpArray).asArray1D();
    }
    public Array1D<ComplexNumber> copy(    final double... source){
      final int tmpSize=source.length;
      final ComplexNumber[] tmpArray=new ComplexNumber[tmpSize];
      for (int i=0; i < tmpSize; i++) {
        tmpArray[i]=TypeUtils.toComplexNumber(source[i]);
      }
      return new ComplexArray(tmpArray).asArray1D();
    }
    public Array1D<ComplexNumber> copy(    final List<? extends Number> source){
      final int tmpSize=source.size();
      final ComplexNumber[] tmpArray=new ComplexNumber[tmpSize];
      for (int i=0; i < tmpSize; i++) {
        tmpArray[i]=TypeUtils.toComplexNumber(source.get(i));
      }
      return new ComplexArray(tmpArray).asArray1D();
    }
    public Array1D<ComplexNumber> copy(    final Number... source){
      final int tmpSize=source.length;
      final ComplexNumber[] tmpArray=new ComplexNumber[tmpSize];
      for (int i=0; i < tmpSize; i++) {
        tmpArray[i]=TypeUtils.toComplexNumber(source[i]);
      }
      return new ComplexArray(tmpArray).asArray1D();
    }
    public Array1D<ComplexNumber> makeRandom(    final long count,    final RandomNumber distribution){
      final ComplexNumber[] tmpArray=new ComplexNumber[(int)count];
      for (int i=0; i < count; i++) {
        tmpArray[i]=TypeUtils.toComplexNumber(distribution);
      }
      return new ComplexArray(tmpArray).asArray1D();
    }
    public Array1D<ComplexNumber> makeZero(    final long count){
      return new ComplexArray((int)count).asArray1D();
    }
    public Array1D<ComplexNumber> wrap(    final SimpleArray<ComplexNumber> aSimple){
      return new ComplexArray(((SimpleArray.Complex)aSimple).data).asArray1D();
    }
  }
;
  public static final Array1D.Factory<Double> PRIMITIVE=new Array1D.Factory<Double>(){
    public Array1D<Double> copy(    final Access1D<?> source){
      final int tmpSize=source.size();
      final double[] tmpArray=new double[tmpSize];
      for (int i=0; i < tmpSize; i++) {
        tmpArray[i]=source.doubleValue(i);
      }
      return new PrimitiveArray(tmpArray).asArray1D();
    }
    public Array1D<Double> copy(    final double... source){
      return new PrimitiveArray(ArrayUtils.copyOf(source)).asArray1D();
    }
    public Array1D<Double> copy(    final List<? extends Number> source){
      final int tmpSize=source.size();
      final double[] tmpArray=new double[tmpSize];
      for (int i=0; i < tmpSize; i++) {
        tmpArray[i]=source.get(i).doubleValue();
      }
      return new PrimitiveArray(tmpArray).asArray1D();
    }
    public Array1D<Double> copy(    final Number... source){
      final int tmpSize=source.length;
      final double[] tmpArray=new double[tmpSize];
      for (int i=0; i < tmpSize; i++) {
        tmpArray[i]=source[i].doubleValue();
      }
      return new PrimitiveArray(tmpArray).asArray1D();
    }
    public Array1D<Double> makeRandom(    final long count,    final RandomNumber distribution){
      final double[] tmpArray=new double[(int)count];
      for (int i=0; i < count; i++) {
        tmpArray[i]=distribution.doubleValue();
      }
      return new PrimitiveArray(tmpArray).asArray1D();
    }
    public Array1D<Double> makeZero(    final long count){
      return new PrimitiveArray((int)count).asArray1D();
    }
    public Array1D<Double> wrap(    final SimpleArray<Double> aSimple){
      return new PrimitiveArray(((SimpleArray.Primitive)aSimple).data).asArray1D();
    }
  }
;
  public static final Array1D.Factory<RationalNumber> RATIONAL=new Array1D.Factory<RationalNumber>(){
    public Array1D<RationalNumber> copy(    final Access1D<?> source){
      final int tmpSize=source.size();
      final RationalNumber[] tmpArray=new RationalNumber[tmpSize];
      for (int i=0; i < tmpSize; i++) {
        tmpArray[i]=TypeUtils.toRationalNumber(source.get(i));
      }
      return new RationalArray(tmpArray).asArray1D();
    }
    public Array1D<RationalNumber> copy(    final double... source){
      final int tmpSize=source.length;
      final RationalNumber[] tmpArray=new RationalNumber[tmpSize];
      for (int i=0; i < tmpSize; i++) {
        tmpArray[i]=TypeUtils.toRationalNumber(source[i]);
      }
      return new RationalArray(tmpArray).asArray1D();
    }
    public Array1D<RationalNumber> copy(    final List<? extends Number> source){
      final int tmpSize=source.size();
      final RationalNumber[] tmpArray=new RationalNumber[tmpSize];
      for (int i=0; i < tmpSize; i++) {
        tmpArray[i]=TypeUtils.toRationalNumber(source.get(i));
      }
      return new RationalArray(tmpArray).asArray1D();
    }
    public Array1D<RationalNumber> copy(    final Number... source){
      final int tmpSize=source.length;
      final RationalNumber[] tmpArray=new RationalNumber[tmpSize];
      for (int i=0; i < tmpSize; i++) {
        tmpArray[i]=TypeUtils.toRationalNumber(source[i]);
      }
      return new RationalArray(tmpArray).asArray1D();
    }
    public Array1D<RationalNumber> makeRandom(    final long count,    final RandomNumber distribution){
      final RationalNumber[] tmpArray=new RationalNumber[(int)count];
      for (int i=0; i < count; i++) {
        tmpArray[i]=TypeUtils.toRationalNumber(distribution);
      }
      return new RationalArray(tmpArray).asArray1D();
    }
    public Array1D<RationalNumber> makeZero(    final long count){
      return new RationalArray((int)count).asArray1D();
    }
    public Array1D<RationalNumber> wrap(    final SimpleArray<RationalNumber> aSimple){
      return new RationalArray(((SimpleArray.Rational)aSimple).data).asArray1D();
    }
  }
;
  @SuppressWarnings("unchecked") private static <T extends Number>T[] copyAndSort(  final Array1D<T> anArray){
    final int tmpLength=anArray.length;
    final T[] retVal=(T[])new Number[tmpLength];
    for (int i=0; i < tmpLength; i++) {
      retVal[i]=anArray.get(i);
    }
    Arrays.sort(retVal);
    return retVal;
  }
  public final int length;
  private final BasicArray<N> myDelegate;
  private final int myFirst;
  private final int myLimit;
  private final int myStep;
  @SuppressWarnings("unused") private Array1D(){
    this(null);
  }
  Array1D(  final BasicArray<N> aDelegate){
    this(aDelegate,0,aDelegate.length,1);
  }
  Array1D(  final BasicArray<N> aDelegate,  final int first,  final int limit,  final int aStep){
    super();
    myDelegate=aDelegate;
    myFirst=first;
    myLimit=limit;
    myStep=aStep;
    length=(myLimit - myFirst) / myStep;
  }
  @Override public boolean contains(  final Object anObj){
    return this.indexOf(anObj) != -1;
  }
  @SuppressWarnings("unchecked") public Array1D<N> copy(){
    BasicArray<N> retVal=null;
    if (myDelegate instanceof PrimitiveArray) {
      retVal=(BasicArray<N>)new PrimitiveArray(length);
      for (long i=0; i < length; i++) {
        retVal.set(i,this.doubleValue(i));
      }
      return new Array1D<N>(retVal);
    }
 else     if (myDelegate instanceof ComplexArray) {
      retVal=(BasicArray<N>)new ComplexArray(length);
      for (long i=0; i < length; i++) {
        retVal.set(i,this.get(i));
      }
      return new Array1D<N>(retVal);
    }
 else     if (myDelegate instanceof BigArray) {
      retVal=(BasicArray<N>)new BigArray(length);
      for (long i=0; i < length; i++) {
        retVal.set(i,this.get(i));
      }
      return new Array1D<N>(retVal);
    }
 else {
      return null;
    }
  }
  /** 
 * Creates a copy of this containing only the selected elements, in the specified order.
 */
  @SuppressWarnings("unchecked") public Array1D<N> copy(  final int... indices){
    BasicArray<N> retVal=null;
    final int tmpLength=indices.length;
    if (myDelegate instanceof PrimitiveArray) {
      retVal=(BasicArray<N>)new PrimitiveArray(tmpLength);
      for (int i=0; i < tmpLength; i++) {
        retVal.set(i,this.doubleValue(indices[i]));
      }
      return new Array1D<N>(retVal);
    }
 else     if (myDelegate instanceof ComplexArray) {
      retVal=(BasicArray<N>)new ComplexArray(tmpLength);
      for (int i=0; i < tmpLength; i++) {
        retVal.set(i,this.get(indices[i]));
      }
      return new Array1D<N>(retVal);
    }
 else     if (myDelegate instanceof BigArray) {
      retVal=(BasicArray<N>)new BigArray(tmpLength);
      for (int i=0; i < tmpLength; i++) {
        retVal.set(i,this.get(indices[i]));
      }
      return new Array1D<N>(retVal);
    }
 else {
      return null;
    }
  }
  public long count(){
    return length;
  }
  public double doubleValue(  final long index){
    return myDelegate.doubleValue(myFirst + (myStep * index));
  }
  public void fillAll(  final N aNmbr){
    myDelegate.fill(myFirst,myLimit,myStep,aNmbr);
  }
  public void fillRange(  final long first,  final long limit,  final N value){
    final int tmpFirst=(int)(myFirst + (myStep * first));
    final int tmpLimit=(int)(myFirst + (myStep * limit));
    myDelegate.fill(tmpFirst,tmpLimit,myStep,value);
  }
  @Override public N get(  final int index){
    return myDelegate.get(myFirst + (myStep * index));
  }
  public N get(  final long index){
    return myDelegate.get(myFirst + (myStep * index));
  }
  public int getIndexOfLargestInRange(  final int first,  final int limit){
    return (myDelegate.getIndexOfLargest(myFirst + (myStep * first),myFirst + (myStep * limit),myStep) - myFirst) / myStep;
  }
  @Override public int indexOf(  final Object anObj){
    final int tmpLength=length;
    if (anObj == null) {
      for (int i=0; i < tmpLength; i++) {
        if (this.get(i) == null) {
          return i;
        }
      }
    }
 else     if (anObj instanceof Number) {
      for (int i=0; i < tmpLength; i++) {
        if (anObj.equals(this.get(i))) {
          return i;
        }
      }
    }
    return -1;
  }
  /** 
 * @see Scalar#isAbsolute()
 */
  public boolean isAbsolute(  final long index){
    return myDelegate.isAbsolute(myFirst + (myStep * index));
  }
  public boolean isAllZeros(){
    return myDelegate.isZeros(myFirst,myLimit,myStep);
  }
  @Override public boolean isEmpty(){
    return length == 0;
  }
  public boolean isInfinite(  final long index){
    return myDelegate.isInfinite(myFirst + (myStep * index));
  }
  public boolean isNaN(  final long index){
    return myDelegate.isNaN(myFirst + (myStep * index));
  }
  /** 
 * @see Scalar#isPositive()
 */
  public boolean isPositive(  final long index){
    return myDelegate.isPositive(myFirst + (myStep * index));
  }
  public boolean isRangeZeros(  final int first,  final int limit){
    return myDelegate.isZeros(myFirst + (myStep * first),myFirst + (myStep * limit),myStep);
  }
  /** 
 * @see Scalar#isReal()
 */
  public boolean isReal(  final int index){
    return myDelegate.isReal(myFirst + (myStep * index));
  }
  public boolean isReal(  final long index){
    return myDelegate.isReal(myFirst + (myStep * index));
  }
  /** 
 * @see Scalar#isZero()
 */
  public boolean isZero(  final int index){
    return myDelegate.isZero(myFirst + (myStep * index));
  }
  public boolean isZero(  final long index){
    return myDelegate.isZero(myFirst + (myStep * index));
  }
  @Override public final Iterator<N> iterator(){
    return new Iterator1D<N>(this);
  }
  /** 
 * @deprecated v35 Use {@link #modifyAll(UnaryFunction)} instead
 */
  @Deprecated public void modifyAll(  final BinaryFunction<N> function,  final N rightArg){
    this.modifyAll(function.second(rightArg));
  }
  /** 
 * @deprecated v35 Use {@link #modifyAll(UnaryFunction)} instead
 */
  @Deprecated public void modifyAll(  final N leftArg,  final BinaryFunction<N> function){
    this.modifyAll(function.first(leftArg));
  }
  /** 
 * @deprecated v35 Use {@link #modifyAll(UnaryFunction)} instead
 */
  @Deprecated public void modifyAll(  final ParameterFunction<N> function,  final int parameter){
    this.modifyAll(function.parameter(parameter));
  }
  public void modifyAll(  final UnaryFunction<N> function){
    myDelegate.modify(myFirst,myLimit,myStep,function);
  }
  public void modifyMatching(  final Access1D<N> anArray,  final BinaryFunction<N> function){
    final int tmpLength=Math.min(length,anArray.size());
    if (myDelegate instanceof PrimitiveArray) {
      for (int i=0; i < tmpLength; i++) {
        this.set(i,function.invoke(anArray.doubleValue(i),this.doubleValue(i)));
      }
    }
 else {
      for (int i=0; i < tmpLength; i++) {
        this.set(i,function.invoke(anArray.get(i),this.get(i)));
      }
    }
  }
  public void modifyMatching(  final BinaryFunction<N> function,  final Access1D<N> anArray){
    final int tmpLength=Math.min(length,anArray.size());
    if (myDelegate instanceof PrimitiveArray) {
      for (int i=0; i < tmpLength; i++) {
        this.set(i,function.invoke(this.doubleValue(i),anArray.doubleValue(i)));
      }
    }
 else {
      for (int i=0; i < tmpLength; i++) {
        this.set(i,function.invoke(this.get(i),anArray.get(i)));
      }
    }
  }
  /** 
 * @deprecated v35 Use {@link #modifyRange(long,long,UnaryFunction)} instead
 */
  @Deprecated public void modifyRange(  final int first,  final int limit,  final BinaryFunction<N> function,  final N rightArg){
    this.modifyRange(first,limit,function.second(rightArg));
  }
  /** 
 * @deprecated v35 Use {@link #modifyRange(long,long,UnaryFunction)} instead
 */
  @Deprecated public void modifyRange(  final int first,  final int limit,  final N leftArg,  final BinaryFunction<N> function){
    this.modifyRange(first,limit,function.first(leftArg));
  }
  /** 
 * @deprecated v35 Use {@link #modifyRange(long,long,UnaryFunction)} instead
 */
  @Deprecated public void modifyRange(  final int first,  final int limit,  final ParameterFunction<N> function,  final int parameter){
    this.modifyRange(first,limit,function.parameter(parameter));
  }
  public void modifyRange(  final long first,  final long limit,  final UnaryFunction<N> function){
    final int tmpFirst=(int)(myFirst + (myStep * first));
    final int tmpLimit=(int)(myFirst + (myStep * limit));
    myDelegate.modify(tmpFirst,tmpLimit,myStep,function);
  }
  /** 
 * Assumes you have first called {@linkplain #sortAscending()}.
 */
  public int searchAscending(  final N aKey){
    final int tmpLength=length;
    if (tmpLength != myDelegate.length) {
      final Number[] tmpArray=new Number[tmpLength];
      for (int i=0; i < tmpLength; i++) {
        tmpArray[i]=this.get(i);
      }
      return Arrays.binarySearch(tmpArray,aKey);
    }
 else {
      return myDelegate.searchAscending(aKey);
    }
  }
  /** 
 * Asssumes you have first called {@linkplain #sortDescending()}.
 */
  public int searchDescending(  final N aKey){
    final int tmpLength=length;
    final Number[] tmpArray=new Number[tmpLength];
    for (int i=0; i < tmpLength; i++) {
      tmpArray[i]=this.get(tmpLength - 1 - i);
    }
    final int tmpInd=Arrays.binarySearch(tmpArray,aKey);
    if (tmpInd >= 0) {
      return tmpLength - 1 - tmpInd;
    }
 else     if (tmpInd < -1) {
      return -tmpLength - tmpInd - 1;
    }
 else {
      return -1;
    }
  }
  @Override public N set(  final int index,  final Number value){
    return myDelegate.set(myFirst + (myStep * index),value);
  }
  public void set(  final long index,  final double value){
    myDelegate.set(myFirst + (myStep * index),value);
  }
  public void set(  final long index,  final Number value){
    myDelegate.set(myFirst + (myStep * index),value);
  }
  @Override public int size(){
    return length;
  }
  public void sortAscending(){
    final int tmpLength=length;
    if (tmpLength != myDelegate.length) {
      final N[] tmpArray=Array1D.copyAndSort(this);
      for (int i=0; i < tmpLength; i++) {
        this.set(i,tmpArray[i]);
      }
    }
 else {
      myDelegate.sortAscending();
    }
  }
  public void sortDescending(){
    final N[] tmpArray=Array1D.copyAndSort(this);
    final int tmpLength=length;
    for (int i=0; i < tmpLength; i++) {
      this.set(i,tmpArray[tmpLength - 1 - i]);
    }
  }
  @Override public Array1D<N> subList(  final int first,  final int limit){
    return new Array1D<N>(myDelegate,myFirst + (myStep * first),myFirst + (myStep * limit),myStep);
  }
  public double[] toRawCopy(){
    final int tmpLength=length;
    final double[] retVal=new double[tmpLength];
    for (int i=0; i < tmpLength; i++) {
      retVal[i]=this.doubleValue(i);
    }
    return retVal;
  }
  public Scalar<N> toScalar(  final int index){
    return myDelegate.toScalar(myFirst + (myStep * index));
  }
  public void visitAll(  final VoidFunction<N> visitor){
    myDelegate.visit(myFirst,myLimit,myStep,visitor);
  }
  public void visitRange(  final long first,  final long limit,  final VoidFunction<N> visitor){
    final int tmpFirst=(int)(myFirst + (myStep * first));
    final int tmpLimit=(int)(myFirst + (myStep * limit));
    myDelegate.visit(tmpFirst,tmpLimit,myStep,visitor);
  }
  BasicArray<N> getDelegate(){
    return myDelegate;
  }
}
