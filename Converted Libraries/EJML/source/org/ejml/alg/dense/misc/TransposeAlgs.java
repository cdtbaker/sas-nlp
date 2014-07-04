package org.ejml.alg.dense.misc;
import org.ejml.data.RowD1Matrix64F;
/** 
 * Low level transpose algorithms.  No sanity checks are performed.
 * @author Peter Abeles
 */
public class TransposeAlgs {
  /** 
 * In-place transpose for a square matrix.  The most efficient algorithm but can
 * only be used on square matrices.
 * @param mat The matrix that is transposed in-place.  Modified.
 */
  public static void square(  RowD1Matrix64F mat){
    int index=1;
    int indexEnd=mat.numCols;
    for (int i=0; i < mat.numRows; i++, index+=i + 1, indexEnd+=mat.numCols) {
      int indexOther=(i + 1) * mat.numCols + i;
      for (; index < indexEnd; index++, indexOther+=mat.numCols) {
        double val=mat.get(index);
        mat.set(index,mat.get(indexOther));
        mat.set(indexOther,val);
      }
    }
  }
  /** 
 * Performs a transpose across block sub-matrices.  Reduces
 * the number of cache misses on larger matrices.
 * *NOTE* If this is beneficial is highly dependent on the computer it is run on. e.g:
 * - Q6600 Almost twice as fast as standard.
 * - Pentium-M Same speed and some times a bit slower than standard.
 * @param A Original matrix.  Not modified.
 * @param A_tran Transposed matrix.  Modified.
 * @param blockLength Length of a block.
 */
  public static void block(  RowD1Matrix64F A,  RowD1Matrix64F A_tran,  final int blockLength){
    for (int i=0; i < A.numRows; i+=blockLength) {
      int blockHeight=Math.min(blockLength,A.numRows - i);
      int indexSrc=i * A.numCols;
      int indexDst=i;
      for (int j=0; j < A.numCols; j+=blockLength) {
        int blockWidth=Math.min(blockLength,A.numCols - j);
        int indexSrcEnd=indexSrc + blockWidth;
        for (; indexSrc < indexSrcEnd; indexSrc++) {
          int rowSrc=indexSrc;
          int rowDst=indexDst;
          int end=rowDst + blockHeight;
          for (; rowDst < end; rowSrc+=A.numCols) {
            A_tran.set(rowDst++,A.get(rowSrc));
          }
          indexDst+=A_tran.numCols;
        }
      }
    }
  }
  /** 
 * A straight forward transpose.  Good for small non-square matrices.
 * @param A Original matrix.  Not modified.
 * @param A_tran Transposed matrix.  Modified.
 */
  public static void standard(  RowD1Matrix64F A,  RowD1Matrix64F A_tran){
    int index=0;
    for (int i=0; i < A_tran.numRows; i++) {
      int index2=i;
      int end=index + A_tran.numCols;
      while (index < end) {
        A_tran.set(index++,A.get(index2));
        index2+=A.numCols;
      }
    }
  }
}
