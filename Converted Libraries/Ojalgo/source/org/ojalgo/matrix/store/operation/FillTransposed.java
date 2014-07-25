package org.ojalgo.matrix.store.operation;
import java.math.BigDecimal;
import org.ojalgo.access.Access2D;
import org.ojalgo.scalar.ComplexNumber;
import org.ojalgo.type.TypeUtils;
public final class FillTransposed extends MatrixOperation {
  public static int THRESHOLD=128;
  public static void invoke(  final BigDecimal[] data,  final int structure,  final int firstColumn,  final int limitColumn,  final Access2D<?> source){
    int tmpIndex=structure * firstColumn;
    for (int j=firstColumn; j < limitColumn; j++) {
      for (int i=0; i < structure; i++) {
        data[tmpIndex++]=TypeUtils.toBigDecimal(source.get(j,i));
      }
    }
  }
  public static void invoke(  final ComplexNumber[] data,  final int structure,  final int firstColumn,  final int limitColumn,  final Access2D<?> source){
    int tmpIndex=structure * firstColumn;
    for (int j=firstColumn; j < limitColumn; j++) {
      for (int i=0; i < structure; i++) {
        data[tmpIndex++]=TypeUtils.toComplexNumber(source.get(j,i));
      }
    }
  }
  public static void invoke(  final double[] data,  final int structure,  final int firstColumn,  final int limitColumn,  final Access2D<?> source){
    int tmpIndex=structure * firstColumn;
    for (int j=firstColumn; j < limitColumn; j++) {
      for (int i=0; i < structure; i++) {
        data[tmpIndex++]=source.doubleValue(j,i);
      }
    }
  }
  private FillTransposed(){
    super();
  }
}
