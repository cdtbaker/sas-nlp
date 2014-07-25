package org.ojalgo.matrix.store.operation;
import java.math.BigDecimal;
import org.ojalgo.function.BigFunction;
import org.ojalgo.scalar.ComplexNumber;
/** 
 * <b>c</b>olumn <b>a</b> <b>x</b> <b>p</b>lus <b>y</b>
 * @param aData y-data
 * @param aBaseDataIndex y-column base index
 * @param aMultipliers x-data
 * @param aBaseMultiplierIndex x-column base index
 * @param aScalar a
 * @param first First index
 * @param aLimit Index limit 
 */
public final class CAXPY extends MatrixOperation {
  public static int THRESHOLD=Integer.MAX_VALUE;
  public static void invoke(  final BigDecimal[] aData,  final int aBaseDataIndex,  final BigDecimal[] aMultipliers,  final int aBaseMultiplierIndex,  final BigDecimal aScalar,  final int aFirst,  final int aLimit){
    for (int i=aFirst; i < aLimit; i++) {
      aData[aBaseDataIndex + i]=BigFunction.ADD.invoke(BigFunction.MULTIPLY.invoke(aScalar,aMultipliers[aBaseMultiplierIndex + i]),aData[aBaseDataIndex + i]);
    }
  }
  public static void invoke(  final ComplexNumber[] aData,  final int aBaseDataIndex,  final ComplexNumber[] aMultipliers,  final int aBaseMultiplierIndex,  final ComplexNumber aScalar,  final int aFirst,  final int aLimit){
    for (int i=aFirst; i < aLimit; i++) {
      aData[aBaseDataIndex + i]=aScalar.multiply(aMultipliers[aBaseMultiplierIndex + i]).add(aData[aBaseDataIndex + i]);
    }
  }
  public static void invoke(  final double[] aData,  final int aBaseDataIndex,  final double[] aMultipliers,  final int aBaseMultiplierIndex,  final double aScalar,  final int aFirst,  final int aLimit){
    for (int i=aFirst; i < aLimit; i++) {
      aData[aBaseDataIndex + i]+=aScalar * aMultipliers[aBaseMultiplierIndex + i];
    }
  }
  private CAXPY(){
    super();
  }
}
