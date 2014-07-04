package no.uib.cipr.matrix;
/** 
 * Upper symmetrical positive definite banded matrix. It does not enforce this
 * property (except for symmetry), and has the same storage layout as{@link no.uib.cipr.matrix.UpperSymmBandMatrix UpperSymmBandMatrix}.
 */
public class UpperSPDBandMatrix extends UpperSymmBandMatrix {
  /** 
 * Constructor for UpperSPDBandMatrix
 * @param nSize of the matrix. Since the matrix must be square, this
 * equals both the number of rows and columns
 * @param kdNumber of bands off the main diagonal (off diagonals)
 */
  public UpperSPDBandMatrix(  int n,  int kd){
    super(n,kd);
  }
  /** 
 * Constructor for UpperSPDBandMatrix
 * @param AMatrix to copy contents from. Only the parts of <code>A</code>
 * that lie within the allocated band are copied over, the rest
 * is ignored
 * @param kdNumber of bands off the main diagonal (off diagonals)
 */
  public UpperSPDBandMatrix(  Matrix A,  int kd){
    super(A,kd);
  }
  /** 
 * Constructor for UpperSPDBandMatrix
 * @param AMatrix to copy contents from. Only the parts of <code>A</code>
 * that lie within the allocated band are copied over, the rest
 * is ignored
 * @param kdNumber of bands off the main diagonal (off diagonals)
 * @param deepTrue for a deep copy. For shallow copies, <code>A</code>
 * must be a banded matrix
 */
  public UpperSPDBandMatrix(  Matrix A,  int kd,  boolean deep){
    super(A,kd,deep);
  }
  @Override public UpperSPDBandMatrix copy(){
    return new UpperSPDBandMatrix(this,kd);
  }
  @Override public Matrix solve(  Matrix B,  Matrix X){
    return SPDsolve(B,X);
  }
}
