package org.ojalgo.matrix.store.operation;
import java.math.BigDecimal;
import org.ojalgo.function.BigFunction;
import org.ojalgo.scalar.ComplexNumber;
/** 
 * y -= ax
 * @param data y-data
 * @param dataBaseIndex y-column base index
 * @param vector x-data
 * @param vectorBaseIndex x-column base index
 * @param scalar a
 * @param first First index
 * @param limit Index limit
 */
public final class SubtractScaledVector extends MatrixOperation {
  public static int THRESHOLD=128;
  public static void invoke(  final BigDecimal[] data,  final int dataBaseIndex,  final BigDecimal[] vector,  final int vectorBaseIndex,  final BigDecimal scalar,  final int first,  final int limit){
    for (int i=first; i < limit; i++) {
      data[dataBaseIndex + i]=BigFunction.SUBTRACT.invoke(data[dataBaseIndex + i],BigFunction.MULTIPLY.invoke(scalar,vector[vectorBaseIndex + i]));
    }
  }
  public static void invoke(  final ComplexNumber[] data,  final int dataBaseIndex,  final ComplexNumber[] vector,  final int vectorBaseIndex,  final ComplexNumber scalar,  final int first,  final int limit){
    for (int i=first; i < limit; i++) {
      data[dataBaseIndex + i]=data[dataBaseIndex + i].subtract(scalar.multiply(vector[vectorBaseIndex + i]));
    }
  }
  public static void invoke(  final double[] data,  final int dataBaseIndex,  final double[] vector,  final int vectorBaseIndex,  final double scalar,  final int first,  final int limit){
    for (int i=first; i < limit; i++) {
      data[dataBaseIndex + i]-=scalar * vector[vectorBaseIndex + i];
    }
  }
  private SubtractScaledVector(){
    super();
  }
}
