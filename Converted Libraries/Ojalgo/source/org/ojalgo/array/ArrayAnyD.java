package org.ojalgo.array;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Iterator;
import org.ojalgo.access.AccessAnyD;
import org.ojalgo.access.AccessUtils;
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
 * ArrayAnyD
 * @author apete
 */
public final class ArrayAnyD<N extends Number> implements AccessAnyD<N>, AccessAnyD.Elements, AccessAnyD.Fillable<N>, AccessAnyD.Modifiable<N>, AccessAnyD.Visitable<N>, Serializable {
  public static final Factory<ArrayAnyD<BigDecimal>> BIG=new Factory<ArrayAnyD<BigDecimal>>(){
    public ArrayAnyD<BigDecimal> copy(    final AccessAnyD<?> aSource){
      final int tmpSize=aSource.size();
      final BigDecimal[] tmpData=new BigDecimal[tmpSize];
      final int[] tmpStructure=AccessUtils.structure(aSource);
      for (int i=0; i < tmpSize; i++) {
        tmpData[i]=TypeUtils.toBigDecimal(aSource.get(i));
      }
      return new BigArray(tmpData).asArrayAnyD(tmpStructure);
    }
    public ArrayAnyD<BigDecimal> makeRandom(    final long[] structure,    final RandomNumber distribution){
      final int tmpSize=(int)AccessUtils.count(structure);
      final BigDecimal[] tmpData=new BigDecimal[tmpSize];
      for (int i=0; i < tmpSize; i++) {
        tmpData[i]=TypeUtils.toBigDecimal(distribution.doubleValue());
      }
      return new BigArray(tmpData).asArrayAnyD(structure);
    }
    public ArrayAnyD<BigDecimal> makeZero(    final long... aStructure){
      final int tmpSize=(int)AccessUtils.count(aStructure);
      return new BigArray(tmpSize).asArrayAnyD(aStructure);
    }
  }
;
  public static final Factory<ArrayAnyD<ComplexNumber>> COMPLEX=new Factory<ArrayAnyD<ComplexNumber>>(){
    public ArrayAnyD<ComplexNumber> copy(    final AccessAnyD<?> aSource){
      final int tmpSize=aSource.size();
      final ComplexNumber[] tmpData=new ComplexNumber[tmpSize];
      final int[] tmpStructure=AccessUtils.structure(aSource);
      for (int i=0; i < tmpSize; i++) {
        tmpData[i]=TypeUtils.toComplexNumber(aSource.get(i));
      }
      return new ComplexArray(tmpData).asArrayAnyD(tmpStructure);
    }
    public ArrayAnyD<ComplexNumber> makeRandom(    final long[] structure,    final RandomNumber distribution){
      final int tmpSize=(int)AccessUtils.count(structure);
      final ComplexNumber[] tmpData=new ComplexNumber[tmpSize];
      for (int i=0; i < tmpSize; i++) {
        tmpData[i]=TypeUtils.toComplexNumber(distribution.doubleValue());
      }
      return new ComplexArray(tmpData).asArrayAnyD(structure);
    }
    public ArrayAnyD<ComplexNumber> makeZero(    final long... aStructure){
      final int tmpSize=(int)AccessUtils.count(aStructure);
      return new ComplexArray(tmpSize).asArrayAnyD(aStructure);
    }
  }
;
  public static final Factory<ArrayAnyD<Double>> PRIMITIVE=new Factory<ArrayAnyD<Double>>(){
    public ArrayAnyD<Double> copy(    final AccessAnyD<?> aSource){
      final int tmpSize=aSource.size();
      final double[] tmpData=new double[tmpSize];
      final int[] tmpStructure=AccessUtils.structure(aSource);
      for (int i=0; i < tmpSize; i++) {
        tmpData[i]=aSource.doubleValue(i);
      }
      return new PrimitiveArray(tmpData).asArrayAnyD(tmpStructure);
    }
    public ArrayAnyD<Double> makeRandom(    final long[] structure,    final RandomNumber aRndm){
      final int tmpSize=(int)AccessUtils.count(structure);
      final double[] tmpData=new double[tmpSize];
      for (int i=0; i < tmpSize; i++) {
        tmpData[i]=aRndm.doubleValue();
      }
      return new PrimitiveArray(tmpData).asArrayAnyD(structure);
    }
    public ArrayAnyD<Double> makeZero(    final long... aStructure){
      final int tmpSize=(int)AccessUtils.count(aStructure);
      return new PrimitiveArray(tmpSize).asArrayAnyD(aStructure);
    }
  }
;
  public static final Factory<ArrayAnyD<RationalNumber>> RATIONAL=new Factory<ArrayAnyD<RationalNumber>>(){
    public ArrayAnyD<RationalNumber> copy(    final AccessAnyD<?> aSource){
      final int tmpSize=aSource.size();
      final BasicArray<RationalNumber> tmpBase=new RationalArray(tmpSize);
      tmpBase.fill(aSource);
      final int[] tmpStructure=AccessUtils.structure(aSource);
      return tmpBase.asArrayAnyD(tmpStructure);
    }
    public ArrayAnyD<RationalNumber> makeRandom(    final long[] structure,    final RandomNumber distribution){
      final int tmpSize=(int)AccessUtils.count(structure);
      final RationalNumber[] tmpData=new RationalNumber[tmpSize];
      for (int i=0; i < tmpSize; i++) {
        tmpData[i]=TypeUtils.toRationalNumber(distribution.doubleValue());
      }
      return new RationalArray(tmpData).asArrayAnyD(structure);
    }
    public ArrayAnyD<RationalNumber> makeZero(    final long... aStructure){
      final int tmpSize=(int)AccessUtils.count(aStructure);
      return new RationalArray(tmpSize).asArrayAnyD(aStructure);
    }
  }
;
  private final BasicArray<N> myDelegate;
  private final int[] myStructure;
  @SuppressWarnings("unused") private ArrayAnyD(){
    this(null,new int[0]);
  }
  ArrayAnyD(  final BasicArray<N> aDelegate,  final int[] aStructure){
    super();
    myDelegate=aDelegate;
    myStructure=aStructure;
  }
  /** 
 * Flattens this abitrary dimensional array to a one dimensional array. The (internal/actual) array is not copied,
 * it is just accessed through a different adaptor.
 */
  public Array1D<N> asArray1D(){
    return myDelegate.asArray1D();
  }
  public long count(){
    return myDelegate.count();
  }
  public long count(  final int dimension){
    return AccessUtils.size(myStructure,dimension);
  }
  public double doubleValue(  final long index){
    return myDelegate.doubleValue(index);
  }
  public double doubleValue(  final long[] reference){
    return myDelegate.doubleValue(AccessUtils.index(myStructure,reference));
  }
  @SuppressWarnings("unchecked") @Override public boolean equals(  final Object obj){
    if (obj instanceof ArrayAnyD) {
      final ArrayAnyD<N> tmpObj=(ArrayAnyD<N>)obj;
      return Arrays.equals(myStructure,tmpObj.structure()) && myDelegate.equals(tmpObj.getDelegate());
    }
 else {
      return super.equals(obj);
    }
  }
  public void fillAll(  final N aNmbr){
    myDelegate.fill(0,myDelegate.length,1,aNmbr);
  }
  public void fillRange(  final long first,  final long limit,  final N value){
    myDelegate.fill((int)first,(int)limit,1,value);
  }
  public void fillSet(  final int[] aFirst,  final int aDim,  final N aNmbr){
    final int tmpCount=AccessUtils.size(myStructure,aDim) - aFirst[aDim];
    final int tmpFirst=AccessUtils.index(myStructure,aFirst);
    final int tmpStep=AccessUtils.step(myStructure,aDim);
    final int tmpLimit=tmpFirst * tmpStep * tmpCount;
    myDelegate.fill(tmpFirst,tmpLimit,tmpStep,aNmbr);
  }
  public N get(  final long index){
    return myDelegate.get(index);
  }
  public N get(  final long[] reference){
    return myDelegate.get(AccessUtils.index(myStructure,reference));
  }
  @Override public int hashCode(){
    return myDelegate.hashCode();
  }
  public boolean isAbsolute(  final long index){
    return myDelegate.isAbsolute(index);
  }
  /** 
 * @see Scalar#isAbsolute()
 */
  public boolean isAbsolute(  final long[] reference){
    return myDelegate.isAbsolute(AccessUtils.index(myStructure,reference));
  }
  public boolean isAllZeros(){
    return myDelegate.isZeros(0,myDelegate.length,1);
  }
  public boolean isInfinite(  final long index){
    return myDelegate.isInfinite(index);
  }
  public boolean isInfinite(  final long[] reference){
    return myDelegate.isInfinite(AccessUtils.index(myStructure,reference));
  }
  public boolean isNaN(  final long index){
    return myDelegate.isNaN(index);
  }
  public boolean isNaN(  final long[] reference){
    return myDelegate.isNaN(AccessUtils.index(myStructure,reference));
  }
  public boolean isPositive(  final long index){
    return myDelegate.isPositive(index);
  }
  public boolean isPositive(  final long[] reference){
    return myDelegate.isPositive(AccessUtils.index(myStructure,reference));
  }
  /** 
 * @see Scalar#isReal()
 */
  public boolean isReal(  final int[] reference){
    return myDelegate.isReal(AccessUtils.index(myStructure,reference));
  }
  public boolean isReal(  final long index){
    return myDelegate.isReal(index);
  }
  public boolean isReal(  final long[] reference){
    return myDelegate.isReal(AccessUtils.index(myStructure,reference));
  }
  /** 
 * @see Scalar#isZero()
 */
  public boolean isZero(  final int[] reference){
    return myDelegate.isZero(AccessUtils.index(myStructure,reference));
  }
  public boolean isZero(  final long index){
    return myDelegate.isZero(index);
  }
  public boolean isZero(  final long[] reference){
    return myDelegate.isZero(AccessUtils.index(myStructure,reference));
  }
  public boolean isZeros(  final int[] aFirst,  final int aDim){
    final int tmpCount=AccessUtils.size(myStructure,aDim) - aFirst[aDim];
    final int tmpFirst=AccessUtils.index(myStructure,aFirst);
    final int tmpStep=AccessUtils.step(myStructure,aDim);
    final int tmpLimit=tmpFirst * tmpStep * tmpCount;
    return myDelegate.isZeros(tmpFirst,tmpLimit,tmpStep);
  }
  public Iterator<N> iterator(){
    return new Iterator1D<N>(this);
  }
  public void modifyAll(  final BinaryFunction<N> aFunc,  final N aNmbr){
    myDelegate.modify(0,myDelegate.length,1,aFunc,aNmbr);
  }
  public void modifyAll(  final N aNmbr,  final BinaryFunction<N> aFunc){
    myDelegate.modify(0,myDelegate.length,1,aNmbr,aFunc);
  }
  public void modifyAll(  final ParameterFunction<N> aFunc,  final int aParam){
    myDelegate.modify(0,myDelegate.length,1,aFunc,aParam);
  }
  public void modifyAll(  final UnaryFunction<N> aFunc){
    myDelegate.modify(0,myDelegate.length,1,aFunc);
  }
  public void modifyMatching(  final ArrayAnyD<N> aLeftArg,  final BinaryFunction<N> aFunc){
    myDelegate.modify(0,myDelegate.length,1,aLeftArg.getDelegate(),aFunc);
  }
  public void modifyMatching(  final BinaryFunction<N> aFunc,  final ArrayAnyD<N> aRightArg){
    myDelegate.modify(0,myDelegate.length,1,aFunc,aRightArg.getDelegate());
  }
  public void modifyRange(  final long first,  final long limit,  final UnaryFunction<N> function){
  }
  public void modifySet(  final int[] aFirst,  final int aDim,  final BinaryFunction<N> aFunc,  final N aNmbr){
    final int tmpCount=AccessUtils.size(myStructure,aDim) - aFirst[aDim];
    final int tmpFirst=AccessUtils.index(myStructure,aFirst);
    final int tmpStep=AccessUtils.step(myStructure,aDim);
    final int tmpLimit=tmpFirst * tmpStep * tmpCount;
    myDelegate.modify(tmpFirst,tmpLimit,tmpStep,aFunc,aNmbr);
  }
  public void modifySet(  final int[] aFirst,  final int aDim,  final N aNmbr,  final BinaryFunction<N> aFunc){
    final int tmpCount=AccessUtils.size(myStructure,aDim) - aFirst[aDim];
    final int tmpFirst=AccessUtils.index(myStructure,aFirst);
    final int tmpStep=AccessUtils.step(myStructure,aDim);
    final int tmpLimit=tmpFirst * tmpStep * tmpCount;
    myDelegate.modify(tmpFirst,tmpLimit,tmpStep,aNmbr,aFunc);
  }
  public void modifySet(  final int[] aFirst,  final int aDim,  final ParameterFunction<N> aFunc,  final int aParam){
    final int tmpCount=AccessUtils.size(myStructure,aDim) - aFirst[aDim];
    final int tmpFirst=AccessUtils.index(myStructure,aFirst);
    final int tmpStep=AccessUtils.step(myStructure,aDim);
    final int tmpLimit=tmpFirst * tmpStep * tmpCount;
    myDelegate.modify(tmpFirst,tmpLimit,tmpStep,aFunc,aParam);
  }
  public void modifySet(  final int[] aFirst,  final int aDim,  final UnaryFunction<N> aFunc){
    final int tmpCount=AccessUtils.size(myStructure,aDim) - aFirst[aDim];
    final int tmpFirst=AccessUtils.index(myStructure,aFirst);
    final int tmpStep=AccessUtils.step(myStructure,aDim);
    final int tmpLimit=tmpFirst * tmpStep * tmpCount;
    myDelegate.modify(tmpFirst,tmpLimit,tmpStep,aFunc);
  }
  public int rank(){
    return myStructure.length;
  }
  public N set(  final int index,  final Number value){
    return myDelegate.set(index,value);
  }
  public void set(  final long index,  final double value){
    myDelegate.set(index,value);
  }
  public void set(  final long index,  final Number value){
    myDelegate.set(index,value);
  }
  public void set(  final long[] reference,  final double value){
    myDelegate.set(AccessUtils.index(myStructure,reference),value);
  }
  public void set(  final long[] reference,  final Number value){
    myDelegate.set(AccessUtils.index(myStructure,reference),value);
  }
  public int size(){
    return myDelegate.length;
  }
  public int size(  final int aDim){
    return AccessUtils.size(myStructure,aDim);
  }
  public Array1D<N> slice(  final int[] aFirst,  final int aDim){
    final int tmpCount=AccessUtils.size(myStructure,aDim) - aFirst[aDim];
    final int tmpFirst=AccessUtils.index(myStructure,aFirst);
    final int tmpStep=AccessUtils.step(myStructure,aDim);
    final int tmpLimit=tmpFirst * tmpStep * tmpCount;
    return new Array1D<N>(myDelegate,tmpFirst,tmpLimit,tmpStep);
  }
  public int[] structure(){
    return myStructure;
  }
  public Scalar<N> toScalar(  final int... reference){
    return myDelegate.toScalar(AccessUtils.index(myStructure,reference));
  }
  @Override public String toString(){
    final StringBuilder retVal=new StringBuilder();
    retVal.append('<');
    retVal.append(myStructure[0]);
    for (int i=1; i < myStructure.length; i++) {
      retVal.append('x');
      retVal.append(myStructure[i]);
    }
    retVal.append('>');
    final int tmpLength=this.size();
    if ((tmpLength >= 1) && (tmpLength <= 100)) {
      retVal.append(' ');
      retVal.append(myDelegate.toString());
    }
    return retVal.toString();
  }
  public void visitAll(  final VoidFunction<N> visitor){
    myDelegate.visit(0,myDelegate.length,1,visitor);
  }
  public void visitRange(  final long first,  final long limit,  final VoidFunction<N> visitor){
  }
  public void visitSet(  final int[] aFirst,  final int aDim,  final VoidFunction<N> aVisitor){
    final int tmpCount=AccessUtils.size(myStructure,aDim) - aFirst[aDim];
    final int tmpFirst=AccessUtils.index(myStructure,aFirst);
    final int tmpStep=AccessUtils.step(myStructure,aDim);
    final int tmpLimit=tmpFirst * tmpStep * tmpCount;
    myDelegate.visit(tmpFirst,tmpLimit,tmpStep,aVisitor);
  }
  final BasicArray<N> getDelegate(){
    return myDelegate;
  }
}
