package org.ojalgo.matrix.store.operation;
import java.math.BigDecimal;
import org.ojalgo.function.BigFunction;
import org.ojalgo.matrix.store.MatrixStore;
import org.ojalgo.scalar.ComplexNumber;
public final class MAXPY extends MatrixOperation {
  public static int THRESHOLD=128;
  public static void invoke(  final BigDecimal[] aData,  final int aRowDim,  final int aFirstCol,  final int aColLimit,  final BigDecimal aScale,  final MatrixStore<BigDecimal> aStore){
    int tmpIndex=aRowDim * aFirstCol;
    for (int j=aFirstCol; j < aColLimit; j++) {
      for (int i=0; i < aRowDim; i++) {
        aData[tmpIndex]=BigFunction.ADD.invoke(BigFunction.MULTIPLY.invoke(aScale,aStore.get(i,j)),aData[tmpIndex]);
        tmpIndex++;
      }
    }
  }
  public static void invoke(  final ComplexNumber[] aData,  final int aRowDim,  final int aFirstCol,  final int aColLimit,  final ComplexNumber aScale,  final MatrixStore<ComplexNumber> aStore){
    int tmpIndex=aRowDim * aFirstCol;
    for (int j=aFirstCol; j < aColLimit; j++) {
      for (int i=0; i < aRowDim; i++) {
        aData[tmpIndex]=aScale.multiply(aStore.get(i,j)).add(aData[tmpIndex]);
        tmpIndex++;
      }
    }
  }
  public static void invoke(  final double[] aData,  final int aRowDim,  final int aFirstCol,  final int aColLimit,  final double aScale,  final MatrixStore<Double> aStore){
    int tmpIndex=aRowDim * aFirstCol;
    for (int j=aFirstCol; j < aColLimit; j++) {
      for (int i=0; i < aRowDim; i++) {
        aData[tmpIndex++]+=aScale * aStore.doubleValue(i,j);
      }
    }
  }
  private MAXPY(){
    super();
  }
}
