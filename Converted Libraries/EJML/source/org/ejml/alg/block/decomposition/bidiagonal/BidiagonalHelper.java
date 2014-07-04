package org.ejml.alg.block.decomposition.bidiagonal;
import org.ejml.data.D1Submatrix64F;
import static org.ejml.alg.block.decomposition.qr.BlockHouseHolder.*;
/** 
 * @author Peter Abeles
 */
public class BidiagonalHelper {
  /** 
 * Performs a standard bidiagonal decomposition just on the outer blocks of the provided matrix
 * @param blockLength
 * @param A
 * @param gammasU
 */
  public static boolean bidiagOuterBlocks(  final int blockLength,  final D1Submatrix64F A,  final double gammasU[],  final double gammasV[]){
    int width=Math.min(blockLength,A.col1 - A.col0);
    int height=Math.min(blockLength,A.row1 - A.row0);
    int min=Math.min(width,height);
    for (int i=0; i < min; i++) {
      if (!computeHouseHolderCol(blockLength,A,gammasU,i))       return false;
      rank1UpdateMultR_Col(blockLength,A,i,gammasU[A.col0 + i]);
      rank1UpdateMultR_TopRow(blockLength,A,i,gammasU[A.col0 + i]);
      System.out.println("After column stuff");
      A.original.print();
      if (!computeHouseHolderRow(blockLength,A,gammasV,i))       return false;
      rank1UpdateMultL_Row(blockLength,A,i,i + 1,gammasV[A.row0 + i]);
      System.out.println("After update row");
      A.original.print();
      System.out.println("After row stuff");
      A.original.print();
    }
    return true;
  }
}
