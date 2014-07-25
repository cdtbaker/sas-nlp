package org.ojalgo.matrix.store.operation;
import java.math.BigDecimal;
import org.ojalgo.function.BigFunction;
import org.ojalgo.scalar.ComplexNumber;
public final class RotateLeft extends MatrixOperation {
  public static int THRESHOLD=128;
  public static void invoke(  final BigDecimal[] aData,  final int aColDim,  final int aRowA,  final int aRowB,  final BigDecimal aCos,  final BigDecimal aSin){
    BigDecimal tmpOldA;
    BigDecimal tmpOldB;
    int tmpIndexA=aRowA;
    int tmpIndexB=aRowB;
    final int tmpIndexStep=aData.length / aColDim;
    for (int j=0; j < aColDim; j++) {
      tmpOldA=aData[tmpIndexA];
      tmpOldB=aData[tmpIndexB];
      aData[tmpIndexA]=BigFunction.ADD.invoke(BigFunction.MULTIPLY.invoke(aCos,tmpOldA),BigFunction.MULTIPLY.invoke(aSin,tmpOldB));
      aData[tmpIndexB]=BigFunction.SUBTRACT.invoke(BigFunction.MULTIPLY.invoke(aCos,tmpOldB),BigFunction.MULTIPLY.invoke(aSin,tmpOldA));
      tmpIndexA+=tmpIndexStep;
      tmpIndexB+=tmpIndexStep;
    }
  }
  public static void invoke(  final ComplexNumber[] aData,  final int aColDim,  final int aRowA,  final int aRowB,  final ComplexNumber aCos,  final ComplexNumber aSin){
    ComplexNumber tmpOldA;
    ComplexNumber tmpOldB;
    int tmpIndexA=aRowA;
    int tmpIndexB=aRowB;
    final int tmpIndexStep=aData.length / aColDim;
    for (int j=0; j < aColDim; j++) {
      tmpOldA=aData[tmpIndexA];
      tmpOldB=aData[tmpIndexB];
      aData[tmpIndexA]=aCos.multiply(tmpOldA).add(aSin.multiply(tmpOldB));
      aData[tmpIndexB]=aCos.multiply(tmpOldB).subtract(aSin.multiply(tmpOldA));
      tmpIndexA+=tmpIndexStep;
      tmpIndexB+=tmpIndexStep;
    }
  }
  public static void invoke(  final double[] aData,  final int aColDim,  final int aRowA,  final int aRowB,  final double aCos,  final double aSin){
    double tmpOldA;
    double tmpOldB;
    int tmpIndexA=aRowA;
    int tmpIndexB=aRowB;
    final int tmpIndexStep=aData.length / aColDim;
    for (int j=0; j < aColDim; j++) {
      tmpOldA=aData[tmpIndexA];
      tmpOldB=aData[tmpIndexB];
      aData[tmpIndexA]=(aCos * tmpOldA) + (aSin * tmpOldB);
      aData[tmpIndexB]=(aCos * tmpOldB) - (aSin * tmpOldA);
      tmpIndexA+=tmpIndexStep;
      tmpIndexB+=tmpIndexStep;
    }
  }
  private RotateLeft(){
    super();
  }
}
