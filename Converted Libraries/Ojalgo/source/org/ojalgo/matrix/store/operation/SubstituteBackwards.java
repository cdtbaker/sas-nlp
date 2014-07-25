package org.ojalgo.matrix.store.operation;
import java.math.BigDecimal;
import org.ojalgo.access.Access2D;
import org.ojalgo.constant.BigMath;
import org.ojalgo.constant.PrimitiveMath;
import org.ojalgo.function.BigFunction;
import org.ojalgo.scalar.ComplexNumber;
public final class SubstituteBackwards extends MatrixOperation {
  public static int THRESHOLD=32;
  public static void invoke(  final BigDecimal[] aData,  final int aRowDim,  final int aFirstCol,  final int aColLimit,  final Access2D<BigDecimal> aBody,  final boolean conjugated){
    final int tmpDiagDim=Math.min(aBody.getRowDim(),aBody.getColDim());
    final BigDecimal[] tmpBodyRow=new BigDecimal[tmpDiagDim];
    BigDecimal tmpVal;
    int tmpColBaseIndex;
    for (int i=tmpDiagDim - 1; i >= 0; i--) {
      for (int j=i; j < tmpDiagDim; j++) {
        tmpBodyRow[j]=conjugated ? aBody.get(j,i) : aBody.get(i,j);
      }
      for (int s=aFirstCol; s < aColLimit; s++) {
        tmpColBaseIndex=s * aRowDim;
        tmpVal=BigMath.ZERO;
        for (int j=i + 1; j < tmpDiagDim; j++) {
          tmpVal=tmpVal.add(tmpBodyRow[j].multiply(aData[j + tmpColBaseIndex]));
        }
        tmpVal=aData[i + tmpColBaseIndex].subtract(tmpVal);
        aData[i + tmpColBaseIndex]=BigFunction.DIVIDE.invoke(tmpVal,tmpBodyRow[i]);
      }
    }
  }
  public static void invoke(  final ComplexNumber[] aData,  final int aRowDim,  final int aFirstCol,  final int aColLimit,  final Access2D<ComplexNumber> aBody,  final boolean conjugated){
    final int tmpDiagDim=Math.min(aBody.getRowDim(),aBody.getColDim());
    final ComplexNumber[] tmpBodyRow=new ComplexNumber[tmpDiagDim];
    ComplexNumber tmpVal;
    int tmpColBaseIndex;
    for (int i=tmpDiagDim - 1; i >= 0; i--) {
      for (int j=i; j < tmpDiagDim; j++) {
        tmpBodyRow[j]=conjugated ? aBody.get(j,i).conjugate() : aBody.get(i,j);
      }
      for (int s=aFirstCol; s < aColLimit; s++) {
        tmpColBaseIndex=s * aRowDim;
        tmpVal=ComplexNumber.ZERO;
        for (int j=i + 1; j < tmpDiagDim; j++) {
          tmpVal=tmpVal.add(tmpBodyRow[j].multiply(aData[j + tmpColBaseIndex]));
        }
        tmpVal=aData[i + tmpColBaseIndex].subtract(tmpVal);
        aData[i + tmpColBaseIndex]=tmpVal.divide(tmpBodyRow[i]);
      }
    }
  }
  public static void invoke(  final double[] aData,  final int aRowDim,  final int aFirstCol,  final int aColLimit,  final Access2D<Double> aBody,  final boolean conjugated){
    final int tmpDiagDim=Math.min(aBody.getRowDim(),aBody.getColDim());
    final double[] tmpBodyRow=new double[tmpDiagDim];
    double tmpVal;
    int tmpColBaseIndex;
    for (int i=tmpDiagDim - 1; i >= 0; i--) {
      for (int j=i; j < tmpDiagDim; j++) {
        tmpBodyRow[j]=conjugated ? aBody.doubleValue(j,i) : aBody.doubleValue(i,j);
      }
      for (int s=aFirstCol; s < aColLimit; s++) {
        tmpColBaseIndex=s * aRowDim;
        tmpVal=PrimitiveMath.ZERO;
        for (int j=i + 1; j < tmpDiagDim; j++) {
          tmpVal+=tmpBodyRow[j] * aData[j + tmpColBaseIndex];
        }
        tmpVal=aData[i + tmpColBaseIndex] - tmpVal;
        aData[i + tmpColBaseIndex]=tmpVal / tmpBodyRow[i];
      }
    }
  }
  private SubstituteBackwards(){
    super();
  }
}
