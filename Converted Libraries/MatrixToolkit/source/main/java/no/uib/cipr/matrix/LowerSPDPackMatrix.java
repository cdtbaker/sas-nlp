package no.uib.cipr.matrix;
/** 
 * Lower symmetrical positive definite packed matrix. Same layout as{@link no.uib.cipr.matrix.LowerSymmPackMatrix LowerSymmPackMatrix}. This
 * class does not enforce the SPD property, but serves as a tag so that more
 * efficient algorithms can be used in the solvers.
 */
public class LowerSPDPackMatrix extends LowerSymmPackMatrix {
  /** 
 * Constructor for LowerSPDPackMatrix
 * @param nSize of the matrix. Since the matrix must be square, this
 * equals both the number of rows and columns
 */
  public LowerSPDPackMatrix(  int n){
    super(n);
  }
  /** 
 * Constructor for LowerSPDPackMatrix
 * @param AMatrix to copy contents from. Only the entries of the relevant
 * part are copied
 */
  public LowerSPDPackMatrix(  Matrix A){
    this(A,true);
  }
  /** 
 * Constructor for LowerSPDPackMatrix
 * @param AMatrix to copy contents from. Only the entries of the relevant
 * part are copied
 * @param deepTrue if the copy is deep, else false (giving a shallow copy).
 * For shallow copies, <code>A</code> must be a packed matrix
 */
  public LowerSPDPackMatrix(  Matrix A,  boolean deep){
    super(A,deep);
  }
  @Override public LowerSPDPackMatrix copy(){
    return new LowerSPDPackMatrix(this);
  }
  @Override public Matrix solve(  Matrix B,  Matrix X){
    return SPDsolve(B,X);
  }
}
