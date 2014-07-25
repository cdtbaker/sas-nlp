package org.ojalgo.matrix.store.operation;
import java.math.BigDecimal;
import org.ojalgo.scalar.ComplexNumber;
public final class ApplyCholesky extends MatrixOperation {
  public static int THRESHOLD=256;
  public static void invoke(  final BigDecimal[] aData,  final int aRowDim,  final int aFirstCol,  final int aColLimit,  final BigDecimal[] multipliers){
    for (int j=aFirstCol; j < aColLimit; j++) {
      SubtractScaledVector.invoke(aData,j * aRowDim,multipliers,0,multipliers[j],j,aRowDim);
    }
  }
  public static void invoke(  final ComplexNumber[] aData,  final int aRowDim,  final int aFirstCol,  final int aColLimit,  final ComplexNumber[] multipliers){
    for (int j=aFirstCol; j < aColLimit; j++) {
      SubtractScaledVector.invoke(aData,j * aRowDim,multipliers,0,multipliers[j].conjugate(),j,aRowDim);
    }
  }
  public static void invoke(  final double[] aData,  final int aRowDim,  final int aFirstCol,  final int aColLimit,  final double[] multipliers){
    for (int j=aFirstCol; j < aColLimit; j++) {
      SubtractScaledVector.invoke(aData,j * aRowDim,multipliers,0,multipliers[j],j,aRowDim);
    }
  }
  private ApplyCholesky(){
    super();
  }
}
