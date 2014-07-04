package org.ejml.alg.block.decomposition.chol;
import org.ejml.data.D1Submatrix64F;
/** 
 * Performs a cholesky decomposition on an individual inner block.
 * @author Peter Abeles
 */
public class BlockInnerCholesky {
  public static boolean upper(  D1Submatrix64F T){
    int n=T.row1 - T.row0;
    int indexT=T.row0 * T.original.numCols + T.col0 * n;
    return upper(T.original.data,indexT,n);
  }
  public static boolean lower(  D1Submatrix64F T){
    int n=T.row1 - T.row0;
    int indexT=T.row0 * T.original.numCols + T.col0 * n;
    return lower(T.original.data,indexT,n);
  }
  /** 
 * Performs an inline upper Cholesky decomposition on an inner row-major matrix.  Only
 * the upper triangular portion of the matrix is read or written to.
 * @param T  Array containing an inner row-major matrix.  Modified.
 * @param indexT First index of the inner row-major matrix.
 * @param n Number of rows and columns of the matrix.
 * @return If the decomposition succeeded.
 */
  public static boolean upper(  double[] T,  int indexT,  int n){
    double el_ii;
    double div_el_ii=0;
    for (int i=0; i < n; i++) {
      for (int j=i; j < n; j++) {
        double sum=T[indexT + i * n + j];
        for (int k=0; k < i; k++) {
          sum-=T[indexT + k * n + i] * T[indexT + k * n + j];
        }
        if (i == j) {
          if (sum <= 0.0)           return false;
          el_ii=Math.sqrt(sum);
          T[indexT + i * n + i]=el_ii;
          div_el_ii=1.0 / el_ii;
        }
 else {
          T[indexT + i * n + j]=sum * div_el_ii;
        }
      }
    }
    return true;
  }
  /** 
 * Performs an inline lower Cholesky decomposition on an inner row-major matrix.  Only
 * the lower triangular portion of the matrix is read or written to.
 * @param T  Array containing an inner row-major matrix.  Modified.
 * @param indexT First index of the inner row-major matrix.
 * @param n Number of rows and columns of the matrix.
 * @return If the decomposition succeeded.
 */
  public static boolean lower(  double[] T,  int indexT,  int n){
    double el_ii;
    double div_el_ii=0;
    for (int i=0; i < n; i++) {
      for (int j=i; j < n; j++) {
        double sum=T[indexT + j * n + i];
        for (int k=0; k < i; k++) {
          sum-=T[indexT + i * n + k] * T[indexT + j * n + k];
        }
        if (i == j) {
          if (sum <= 0.0)           return false;
          el_ii=Math.sqrt(sum);
          T[indexT + i * n + i]=el_ii;
          div_el_ii=1.0 / el_ii;
        }
 else {
          T[indexT + j * n + i]=sum * div_el_ii;
        }
      }
    }
    return true;
  }
}
