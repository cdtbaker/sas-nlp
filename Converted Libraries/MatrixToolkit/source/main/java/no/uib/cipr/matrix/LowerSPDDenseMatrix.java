package no.uib.cipr.matrix;
/** 
 * Lower symmetrical positive definite dense matrix. Same layout as{@link no.uib.cipr.matrix.LowerSymmDenseMatrix LowerSymmDenseMatrix}. This
 * class does not enforce the SPD property, but serves as a tag so that more
 * efficient algorithms can be used in the solvers.
 */
public class LowerSPDDenseMatrix extends LowerSymmDenseMatrix {
  /** 
 * Constructor for LowerSPDDenseMatrix
 * @param nSize of the matrix. Since the matrix must be square, this
 * equals both the number of rows and columns
 */
  public LowerSPDDenseMatrix(  int n){
    super(n);
  }
  /** 
 * Constructor for LowerSPDDenseMatrix
 * @param AMatrix to copy. It must be a square matrix, and only the lower
 * triangular part is copied
 */
  public LowerSPDDenseMatrix(  Matrix A){
    this(A,true);
  }
  /** 
 * Constructor for LowerSPDDenseMatrix
 * @param AMatrix to copy. It must be a square matrix, and only the lower
 * triangular part is copied
 * @param deepFalse for a shallow copy, else it'll be a deep copy. For
 * shallow copies, <code>A</code> must be a dense matrix
 */
  public LowerSPDDenseMatrix(  Matrix A,  boolean deep){
    super(A,deep);
  }
  @Override public LowerSPDDenseMatrix copy(){
    return new LowerSPDDenseMatrix(this);
  }
  @Override public Matrix solve(  Matrix B,  Matrix X){
    return SPDsolve(B,X);
  }
}
