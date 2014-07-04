package org.ojalgo.matrix.store.operation;
import java.math.BigDecimal;
import org.ojalgo.function.BigFunction;
import org.ojalgo.scalar.ComplexNumber;
public final class RotateRight extends MatrixOperation {
  public static int THRESHOLD=128;
  public static void invoke(  final BigDecimal[] aData,  final int aRowDim,  final int aColA,  final int aColB,  final BigDecimal aCos,  final BigDecimal aSin){
    BigDecimal tmpOldA;
    BigDecimal tmpOldB;
    int tmpIndexA=aColA * aRowDim;
    int tmpIndexB=aColB * aRowDim;
    for (int i=0; i < aRowDim; i++) {
      tmpOldA=aData[tmpIndexA];
      tmpOldB=aData[tmpIndexB];
      aData[tmpIndexA]=BigFunction.SUBTRACT.invoke(BigFunction.MULTIPLY.invoke(aCos,tmpOldA),BigFunction.MULTIPLY.invoke(aSin,tmpOldB));
      aData[tmpIndexB]=BigFunction.ADD.invoke(BigFunction.MULTIPLY.invoke(aCos,tmpOldB),BigFunction.MULTIPLY.invoke(aSin,tmpOldA));
      tmpIndexA++;
      tmpIndexB++;
    }
  }
  public static void invoke(  final ComplexNumber[] aData,  final int aRowDim,  final int aColA,  final int aColB,  final ComplexNumber aCos,  final ComplexNumber aSin){
    ComplexNumber tmpOldA;
    ComplexNumber tmpOldB;
    int tmpIndexA=aColA * aRowDim;
    int tmpIndexB=aColB * aRowDim;
    for (int i=0; i < aRowDim; i++) {
      tmpOldA=aData[tmpIndexA];
      tmpOldB=aData[tmpIndexB];
      aData[tmpIndexA]=aCos.multiply(tmpOldA).subtract(aSin.multiply(tmpOldB));
      aData[tmpIndexB]=aCos.multiply(tmpOldB).add(aSin.multiply(tmpOldA));
      tmpIndexA++;
      tmpIndexB++;
    }
  }
  public static void invoke(  final double[] aData,  final int aRowDim,  final int aColA,  final int aColB,  final double aCos,  final double aSin){
    double tmpOldA;
    double tmpOldB;
    int tmpIndexA=aColA * aRowDim;
    int tmpIndexB=aColB * aRowDim;
    for (int i=0; i < aRowDim; i++) {
      tmpOldA=aData[tmpIndexA];
      tmpOldB=aData[tmpIndexB];
      aData[tmpIndexA]=(aCos * tmpOldA) - (aSin * tmpOldB);
      aData[tmpIndexB]=(aCos * tmpOldB) + (aSin * tmpOldA);
      tmpIndexA++;
      tmpIndexB++;
    }
  }
  private RotateRight(){
    super();
  }
}
