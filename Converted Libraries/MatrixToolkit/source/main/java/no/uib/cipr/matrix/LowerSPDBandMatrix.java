package no.uib.cipr.matrix;
/** 
 * Lower symmetrical positive definite banded matrix. It does not enforce this
 * property (except for symmetry), and has the same storage layout as{@link no.uib.cipr.matrix.LowerSymmBandMatrix LowerSymmBandMatrix}.
 */
public class LowerSPDBandMatrix extends LowerSymmBandMatrix {
  /** 
 * Constructor for LowerSPDBandMatrix
 * @param nSize of the matrix. Since the matrix must be square, this
 * equals both the number of rows and columns
 * @param kdNumber of bands off the main diagonal (off diagonals)
 */
  public LowerSPDBandMatrix(  int n,  int kd){
    super(n,kd);
  }
  /** 
 * Constructor for LowerSPDBandMatrix
 * @param AMatrix to copy contents from. Only the parts of <code>A</code>
 * that lie within the allocated band are copied over, the rest
 * is ignored
 * @param kdNumber of bands off the main diagonal (off diagonals)
 */
  public LowerSPDBandMatrix(  Matrix A,  int kd){
    super(A,kd);
  }
  /** 
 * Constructor for LowerSPDBandMatrix
 * @param AMatrix to copy contents from. Only the parts of <code>A</code>
 * that lie within the allocated band are copied over, the rest
 * is ignored
 * @param kdNumber of bands off the main diagonal (off diagonals)
 * @param deepTrue for a deep copy. For shallow copies, <code>A</code>
 * must be a banded matrix
 */
  public LowerSPDBandMatrix(  Matrix A,  int kd,  boolean deep){
    super(A,kd,deep);
  }
  @Override public LowerSPDBandMatrix copy(){
    return new LowerSPDBandMatrix(this,kd);
  }
  @Override public Matrix solve(  Matrix B,  Matrix X){
    return SPDsolve(B,X);
  }
}
