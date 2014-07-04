package org.ejml.alg.dense.misc;
import org.ejml.data.Matrix64F;
/** 
 * Implementations of common ops routines for {@link org.ejml.data.DenseMatrix64F}.  In general
 * there is no need to directly invoke these functions.
 * @author Peter Abeles
 */
public class ImplCommonOps_Matrix64F {
  public static void extract(  Matrix64F src,  int srcY0,  int srcX0,  Matrix64F dst,  int dstY0,  int dstX0,  int numRows,  int numCols){
    for (int y=0; y < numRows; y++) {
      for (int x=0; x < numCols; x++) {
        double v=src.get(y + srcY0,x + srcX0);
        dst.set(dstY0 + y,dstX0 + x,v);
      }
    }
  }
}
