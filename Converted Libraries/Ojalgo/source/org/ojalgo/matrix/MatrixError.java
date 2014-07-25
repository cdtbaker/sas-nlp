package org.ojalgo.matrix;
import org.ojalgo.ProgrammingError;
import org.ojalgo.access.Access2D;
/** 
 * MatrixError
 * @author apete
 */
public class MatrixError extends ProgrammingError {
  public static void throwIfMultiplicationNotPossible(  final Access2D<?> aMtrxLeft,  final Access2D<?> aMtrxRight){
    if (aMtrxLeft.getColDim() != aMtrxRight.getRowDim()) {
      throw new MatrixError("The column dimension of the left matrix does not match the row dimension of the right matrix!");
    }
  }
  public static void throwIfNotEqualColumnDimensions(  final Access2D<?> aMtrx1,  final Access2D<?> aMtrx2){
    if (aMtrx1.getColDim() != aMtrx2.getColDim()) {
      throw new MatrixError("Column dimensions are not equal!");
    }
  }
  public static void throwIfNotEqualDimensions(  final Access2D<?> aMtrx1,  final Access2D<?> aMtrx2){
    MatrixError.throwIfNotEqualRowDimensions(aMtrx1,aMtrx2);
    MatrixError.throwIfNotEqualColumnDimensions(aMtrx1,aMtrx2);
  }
  public static void throwIfNotEqualRowDimensions(  final Access2D<?> aMtrx1,  final Access2D<?> aMtrx2){
    if (aMtrx1.getRowDim() != aMtrx2.getRowDim()) {
      throw new MatrixError("Row dimensions are not equal!");
    }
  }
  public static void throwIfNotSquare(  final BasicMatrix aMtrx){
    if (aMtrx.getRowDim() != aMtrx.getColDim()) {
      throw new MatrixError("Matrix is not square!");
    }
  }
  public MatrixError(  final String aString){
    super(aString);
  }
}
