package org.ojalgo.matrix.store.operation;
import java.math.BigDecimal;
import org.ojalgo.function.BigFunction;
import org.ojalgo.scalar.ComplexNumber;
public final class RAXPY extends MatrixOperation {
  public static int THRESHOLD=128;
  public static void invoke(  final BigDecimal[] aData,  final int aDataRow,  final BigDecimal[] aMultipliers,  final int aMultiplierRow,  final BigDecimal aScalar,  final int aFirst,  final int aLimit){
    final int tmpRowDim=aData.length / aLimit;
    int tmpDataIndex=aDataRow + (aFirst * tmpRowDim);
    int tmpMultiplierIndex=aMultiplierRow + (aFirst * tmpRowDim);
    for (int i=aFirst; i < aLimit; i++) {
      aData[tmpDataIndex]=BigFunction.ADD.invoke(BigFunction.MULTIPLY.invoke(aScalar,aMultipliers[tmpMultiplierIndex]),aData[tmpDataIndex]);
      tmpDataIndex+=tmpRowDim;
      tmpMultiplierIndex+=tmpRowDim;
    }
  }
  public static void invoke(  final ComplexNumber[] aData,  final int aDataRow,  final ComplexNumber[] aMultipliers,  final int aMultiplierRow,  final ComplexNumber aScalar,  final int aFirst,  final int aLimit){
    final int tmpRowDim=aData.length / aLimit;
    int tmpDataIndex=aDataRow + (aFirst * tmpRowDim);
    int tmpMultiplierIndex=aMultiplierRow + (aFirst * tmpRowDim);
    for (int i=aFirst; i < aLimit; i++) {
      aData[tmpDataIndex]=aScalar.multiply(aMultipliers[tmpMultiplierIndex]).add(aData[tmpDataIndex]);
      tmpDataIndex+=tmpRowDim;
      tmpMultiplierIndex+=tmpRowDim;
    }
  }
  public static void invoke(  final double[] aData,  final int aDataRow,  final double[] aMultipliers,  final int aMultiplierRow,  final double aScalar,  final int aFirst,  final int aLimit){
    final int tmpRowDim=aData.length / aLimit;
    int tmpDataIndex=aDataRow + (aFirst * tmpRowDim);
    int tmpMultiplierIndex=aMultiplierRow + (aFirst * tmpRowDim);
    for (int i=aFirst; i < aLimit; i++) {
      aData[tmpDataIndex]+=aScalar * aMultipliers[tmpMultiplierIndex];
      tmpDataIndex+=tmpRowDim;
      tmpMultiplierIndex+=tmpRowDim;
    }
  }
  private RAXPY(){
    super();
  }
}
