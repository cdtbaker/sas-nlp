package org.ojalgo.matrix.store.operation;
import java.math.BigDecimal;
import org.ojalgo.scalar.ComplexNumber;
/** 
 * [A] -= ([a][b]<sup>c</sup>+[b][a]<sup>c</sup>)
 * <br>
 * [A] is assumed to be hermitian (square symmetric) [A] = [A]<sup>C</sup>.
 * <br>
 * <sup>C</sup> == conjugate transpose
 * @author apete
 */
public final class HermitianRank2Update extends MatrixOperation {
  public static int THRESHOLD=64;
  public static void invoke(  final BigDecimal[] aData,  final int aFirstCol,  final int aColLimit,  final BigDecimal[] aVector1,  final BigDecimal[] aVector2){
    final int tmpLength=aVector1.length;
    BigDecimal tmpVal1j;
    BigDecimal tmpVal2j;
    int tmpIndex;
    for (int j=aFirstCol; j < aColLimit; j++) {
      tmpVal1j=aVector1[j];
      tmpVal2j=aVector2[j];
      tmpIndex=j + (j * tmpLength);
      for (int i=j; i < tmpLength; i++) {
        aData[tmpIndex]=aData[tmpIndex].subtract(aVector2[i].multiply(tmpVal1j).add(aVector1[i].multiply(tmpVal2j)));
        tmpIndex++;
      }
    }
  }
  public static void invoke(  final ComplexNumber[] aData,  final int aFirstCol,  final int aColLimit,  final ComplexNumber[] aVector1,  final ComplexNumber[] aVector2){
    final int tmpLength=aVector1.length;
    ComplexNumber tmpVal1j;
    ComplexNumber tmpVal2j;
    int tmpIndex;
    for (int j=aFirstCol; j < aColLimit; j++) {
      tmpVal1j=aVector1[j].conjugate();
      tmpVal2j=aVector2[j].conjugate();
      tmpIndex=j + (j * tmpLength);
      for (int i=j; i < tmpLength; i++) {
        aData[tmpIndex]=aData[tmpIndex].subtract(aVector2[i].multiply(tmpVal1j).add(aVector1[i].multiply(tmpVal2j)));
        tmpIndex++;
      }
    }
  }
  public static void invoke(  final double[] aData,  final int aFirstCol,  final int aColLimit,  final double[] aVector1,  final double[] aVector2){
    final int tmpLength=aVector1.length;
    double tmpVal1j;
    double tmpVal2j;
    int tmpIndex;
    for (int j=aFirstCol; j < aColLimit; j++) {
      tmpVal1j=aVector1[j];
      tmpVal2j=aVector2[j];
      tmpIndex=j + (j * tmpLength);
      for (int i=j; i < tmpLength; i++) {
        aData[tmpIndex++]-=((aVector2[i] * tmpVal1j) + (aVector1[i] * tmpVal2j));
      }
    }
  }
  private HermitianRank2Update(){
    super();
  }
}
