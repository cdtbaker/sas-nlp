package org.ojalgo.matrix.transformation;
import java.math.BigDecimal;
import java.util.Iterator;
import org.ojalgo.access.Access1D;
import org.ojalgo.access.Iterator1D;
import org.ojalgo.constant.BigMath;
import org.ojalgo.constant.PrimitiveMath;
import org.ojalgo.function.BigFunction;
import org.ojalgo.scalar.ComplexNumber;
public interface Householder<N extends Number> extends Access1D<N> {
public static final class Big extends Object implements Householder<BigDecimal> {
    public BigDecimal beta;
    public int first;
    public final BigDecimal[] vector;
    public Big(    final Householder<BigDecimal> aTransf){
      this(aTransf.size());
      this.copy(aTransf);
    }
    public Big(    final int aDim){
      super();
      vector=new BigDecimal[aDim];
      beta=BigMath.ZERO;
      first=0;
    }
    @SuppressWarnings("unused") private Big(){
      this(0);
    }
    public final Householder.Big copy(    final Householder<BigDecimal> aSource){
      first=aSource.first();
      final BigDecimal[] tmpVector=vector;
      BigDecimal tmpVal, tmpVal2=BigMath.ZERO;
      final int tmpSize=aSource.size();
      for (int i=aSource.first(); i < tmpSize; i++) {
        tmpVal=aSource.get(i);
        tmpVal2=BigFunction.ADD.invoke(tmpVal2,BigFunction.MULTIPLY.invoke(tmpVal,tmpVal));
        tmpVector[i]=tmpVal;
      }
      beta=BigFunction.DIVIDE.invoke(BigMath.TWO,tmpVal2);
      return this;
    }
    public final Householder.Big copy(    final Householder<BigDecimal> aSource,    final BigDecimal precalculatedBeta){
      first=aSource.first();
      final BigDecimal[] tmpVector=vector;
      final int tmpSize=aSource.size();
      for (int i=aSource.first(); i < tmpSize; i++) {
        tmpVector[i]=aSource.get(i);
      }
      beta=precalculatedBeta;
      return this;
    }
    public long count(){
      return vector.length;
    }
    public double doubleValue(    final long anInd){
      return vector[(int)anInd].doubleValue();
    }
    public int first(){
      return first;
    }
    public BigDecimal get(    final int index){
      return vector[index];
    }
    public BigDecimal get(    final long index){
      return vector[(int)index];
    }
    public final Iterator<BigDecimal> iterator(){
      return new Iterator1D<BigDecimal>(this);
    }
    public int size(){
      return vector.length;
    }
    @Override public String toString(){
      final StringBuilder retVal=new StringBuilder("{");
      final int tmpFirst=first;
      final int tmpLength=vector.length;
      for (int i=0; i < tmpFirst; i++) {
        retVal.append(BigMath.ZERO);
        retVal.append(", ");
      }
      for (int i=first; i < tmpLength; i++) {
        retVal.append(vector[i]);
        if ((i + 1) < tmpLength) {
          retVal.append(", ");
        }
      }
      retVal.append("}");
      return retVal.toString();
    }
  }
public static final class Complex extends Object implements Householder<ComplexNumber> {
    public ComplexNumber beta;
    public int first;
    public final ComplexNumber[] vector;
    public Complex(    final Householder<ComplexNumber> aTransf){
      this(aTransf.size());
      this.copy(aTransf);
    }
    public Complex(    final int aDim){
      super();
      vector=new ComplexNumber[aDim];
      beta=ComplexNumber.ZERO;
      first=0;
    }
    @SuppressWarnings("unused") private Complex(){
      this(0);
    }
    public final Householder.Complex copy(    final Householder<ComplexNumber> aSource){
      first=aSource.first();
      final ComplexNumber[] tmpVector=vector;
      ComplexNumber tmpNmbr;
      double tmpVal, tmpVal2=PrimitiveMath.ZERO;
      final int tmpSize=aSource.size();
      for (int i=aSource.first(); i < tmpSize; i++) {
        tmpNmbr=aSource.get(i);
        tmpVal=tmpNmbr.norm();
        tmpVal2+=tmpVal * tmpVal;
        tmpVector[i]=tmpNmbr;
      }
      beta=ComplexNumber.makeReal(PrimitiveMath.TWO / tmpVal2);
      return this;
    }
    public final Householder.Complex copy(    final Householder<ComplexNumber> aSource,    final ComplexNumber precalculatedBeta){
      first=aSource.first();
      final ComplexNumber[] tmpVector=vector;
      final int tmpSize=aSource.size();
      for (int i=aSource.first(); i < tmpSize; i++) {
        tmpVector[i]=aSource.get(i);
      }
      beta=precalculatedBeta;
      return this;
    }
    public long count(){
      return vector.length;
    }
    public double doubleValue(    final long anInd){
      return vector[(int)anInd].doubleValue();
    }
    public int first(){
      return first;
    }
    public ComplexNumber get(    final int index){
      return vector[index];
    }
    public ComplexNumber get(    final long index){
      return vector[(int)index];
    }
    public final Iterator<ComplexNumber> iterator(){
      return new Iterator1D<ComplexNumber>(this);
    }
    public int size(){
      return vector.length;
    }
    @Override public String toString(){
      final StringBuilder retVal=new StringBuilder("{");
      final int tmpFirst=first;
      final int tmpLength=vector.length;
      for (int i=0; i < tmpFirst; i++) {
        retVal.append(ComplexNumber.ZERO);
        retVal.append(", ");
      }
      for (int i=first; i < tmpLength; i++) {
        retVal.append(vector[i]);
        if ((i + 1) < tmpLength) {
          retVal.append(", ");
        }
      }
      retVal.append("}");
      return retVal.toString();
    }
  }
public static final class Primitive extends Object implements Householder<Double> {
    public double beta;
    public int first;
    public final double[] vector;
    public Primitive(    final Householder<Double> aTransf){
      this(aTransf.size());
      this.copy(aTransf);
    }
    public Primitive(    final int aDim){
      super();
      vector=new double[aDim];
      beta=PrimitiveMath.ZERO;
      first=0;
    }
    @SuppressWarnings("unused") private Primitive(){
      this(0);
    }
    public final Householder.Primitive copy(    final Householder<Double> aSource){
      first=aSource.first();
      final double[] tmpVector=vector;
      double tmpVal, tmpVal2=PrimitiveMath.ZERO;
      final int tmpSize=aSource.size();
      for (int i=aSource.first(); i < tmpSize; i++) {
        tmpVal=aSource.doubleValue(i);
        tmpVal2+=tmpVal * tmpVal;
        tmpVector[i]=tmpVal;
      }
      beta=PrimitiveMath.TWO / tmpVal2;
      return this;
    }
    public final Householder.Primitive copy(    final Householder<Double> aSource,    final double precalculatedBeta){
      first=aSource.first();
      final double[] tmpVector=vector;
      final int tmpSize=aSource.size();
      for (int i=aSource.first(); i < tmpSize; i++) {
        tmpVector[i]=aSource.doubleValue(i);
      }
      beta=precalculatedBeta;
      return this;
    }
    public long count(){
      return vector.length;
    }
    public double doubleValue(    final long anInd){
      return vector[(int)anInd];
    }
    public int first(){
      return first;
    }
    public Double get(    final int index){
      return vector[index];
    }
    public Double get(    final long index){
      return vector[(int)index];
    }
    public final Iterator<Double> iterator(){
      return new Iterator1D<Double>(this);
    }
    public int size(){
      return vector.length;
    }
    @Override public String toString(){
      final StringBuilder retVal=new StringBuilder("{ ");
      final int tmpLastIndex=vector.length - 1;
      for (int i=0; i < tmpLastIndex; i++) {
        retVal.append(this.get(i));
        retVal.append(", ");
      }
      retVal.append(this.get(tmpLastIndex));
      retVal.append(" }");
      return retVal.toString();
    }
  }
  /** 
 * Regardless of what is actually returned by {@linkplain #doubleValue(long)} and/or {@linkplain #get(long)} vector
 * elements with indeces less than 'first' should be assumed to be, and treated as if they are, zero.
 */
  int first();
}
