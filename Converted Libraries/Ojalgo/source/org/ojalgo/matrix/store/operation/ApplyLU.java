package org.ojalgo.matrix.store.operation;
import java.math.BigDecimal;
import org.ojalgo.scalar.ComplexNumber;
public final class ApplyLU extends MatrixOperation {
  public static int THRESHOLD=256;
  public static void invoke(  final BigDecimal[] data,  final int structure,  final int firstColumn,  final int columnLimit,  final BigDecimal[] multipliers,  final int iterationPoint){
    for (int j=firstColumn; j < columnLimit; j++) {
      SubtractScaledVector.invoke(data,j * structure,multipliers,0,data[iterationPoint + (j * structure)],iterationPoint + 1,structure);
    }
  }
  public static void invoke(  final ComplexNumber[] data,  final int structure,  final int firstColumn,  final int columnLimit,  final ComplexNumber[] multipliers,  final int iterationPoint){
    for (int j=firstColumn; j < columnLimit; j++) {
      SubtractScaledVector.invoke(data,j * structure,multipliers,0,data[iterationPoint + (j * structure)],iterationPoint + 1,structure);
    }
  }
  public static void invoke(  final double[] data,  final int structure,  final int firstColumn,  final int columnLimit,  final double[] multipliers,  final int iterationPoint){
    for (int j=firstColumn; j < columnLimit; j++) {
      SubtractScaledVector.invoke(data,j * structure,multipliers,0,data[iterationPoint + (j * structure)],iterationPoint + 1,structure);
    }
  }
  private ApplyLU(){
    super();
  }
}
