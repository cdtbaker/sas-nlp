package org.ojalgo.matrix.store.operation;
import java.math.BigDecimal;
import org.ojalgo.access.Access2D;
import org.ojalgo.constant.BigMath;
import org.ojalgo.constant.PrimitiveMath;
import org.ojalgo.function.BigFunction;
import org.ojalgo.scalar.ComplexNumber;
public final class SubstituteForwards extends MatrixOperation {
  public static int THRESHOLD=16;
  public static void invoke(  final BigDecimal[] aData,  final int aRowDim,  final int aFirstCol,  final int aColLimit,  final Access2D<BigDecimal> aBody,  final boolean onesOnDiagonal,  final boolean zerosAboveDiagonal){
    final int tmpDiagDim=Math.min(aBody.getRowDim(),aBody.getColDim());
    final BigDecimal[] tmpBodyRow=new BigDecimal[tmpDiagDim];
    BigDecimal tmpVal;
    int tmpColBaseIndex;
    for (int i=0; i < tmpDiagDim; i++) {
      for (int j=0; j <= i; j++) {
        tmpBodyRow[j]=aBody.get(i,j);
      }
      for (int s=aFirstCol; s < aColLimit; s++) {
        tmpColBaseIndex=s * aRowDim;
        tmpVal=BigMath.ZERO;
        for (int j=zerosAboveDiagonal ? s : 0; j < i; j++) {
          tmpVal=tmpVal.add(tmpBodyRow[j].multiply(aData[j + tmpColBaseIndex]));
        }
        tmpVal=aData[i + tmpColBaseIndex].subtract(tmpVal);
        if (!onesOnDiagonal) {
          tmpVal=BigFunction.DIVIDE.invoke(tmpVal,tmpBodyRow[i]);
        }
        aData[i + tmpColBaseIndex]=tmpVal;
      }
    }
  }
  public static void invoke(  final ComplexNumber[] aData,  final int aRowDim,  final int aFirstCol,  final int aColLimit,  final Access2D<ComplexNumber> aBody,  final boolean onesOnDiagonal,  final boolean zerosAboveDiagonal){
    final int tmpDiagDim=Math.min(aBody.getRowDim(),aBody.getColDim());
    final ComplexNumber[] tmpBodyRow=new ComplexNumber[tmpDiagDim];
    ComplexNumber tmpVal;
    int tmpColBaseIndex;
    for (int i=0; i < tmpDiagDim; i++) {
      for (int j=0; j <= i; j++) {
        tmpBodyRow[j]=aBody.get(i,j);
      }
      for (int s=aFirstCol; s < aColLimit; s++) {
        tmpColBaseIndex=s * aRowDim;
        tmpVal=ComplexNumber.ZERO;
        for (int j=zerosAboveDiagonal ? s : 0; j < i; j++) {
          tmpVal=tmpVal.add(tmpBodyRow[j].multiply(aData[j + tmpColBaseIndex]));
        }
        tmpVal=aData[i + tmpColBaseIndex].subtract(tmpVal);
        if (!onesOnDiagonal) {
          tmpVal=tmpVal.divide(tmpBodyRow[i]);
        }
        aData[i + tmpColBaseIndex]=tmpVal;
      }
    }
  }
  public static void invoke(  final double[] aData,  final int aRowDim,  final int aFirstCol,  final int aColLimit,  final Access2D<Double> aBody,  final boolean onesOnDiagonal,  final boolean zerosAboveDiagonal){
    final int tmpDiagDim=Math.min(aBody.getRowDim(),aBody.getColDim());
    final double[] tmpBodyRow=new double[tmpDiagDim];
    double tmpVal;
    int tmpColBaseIndex;
    for (int i=0; i < tmpDiagDim; i++) {
      for (int j=0; j <= i; j++) {
        tmpBodyRow[j]=aBody.doubleValue(i,j);
      }
      for (int s=aFirstCol; s < aColLimit; s++) {
        tmpColBaseIndex=s * aRowDim;
        tmpVal=PrimitiveMath.ZERO;
        for (int j=zerosAboveDiagonal ? s : 0; j < i; j++) {
          tmpVal+=tmpBodyRow[j] * aData[j + tmpColBaseIndex];
        }
        tmpVal=aData[i + tmpColBaseIndex] - tmpVal;
        if (!onesOnDiagonal) {
          tmpVal/=tmpBodyRow[i];
        }
        aData[i + tmpColBaseIndex]=tmpVal;
      }
    }
  }
  private SubstituteForwards(){
    super();
  }
}
