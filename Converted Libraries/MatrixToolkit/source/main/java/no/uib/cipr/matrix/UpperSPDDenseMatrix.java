package no.uib.cipr.matrix;
/** 
 * Upper symmetrical positive definite dense matrix. Same layout as{@link no.uib.cipr.matrix.UpperSymmDenseMatrix UpperSymmDenseMatrix}. This
 * class does not enforce the SPD property, but serves as a tag so that more
 * efficient algorithms can be used in the solvers.
 */
public class UpperSPDDenseMatrix extends UpperSymmDenseMatrix {
  /** 
 * Constructor for UpperSPDDenseMatrix
 * @param nSize of the matrix. Since the matrix must be square, this
 * equals both the number of rows and columns
 */
  public UpperSPDDenseMatrix(  int n){
    super(n);
  }
  /** 
 * Constructor for UpperSPDDenseMatrix
 * @param AMatrix to copy. It must be a square matrix, and only the upper
 * triangular part is copied
 */
  public UpperSPDDenseMatrix(  Matrix A){
    this(A,true);
  }
  /** 
 * Constructor for UpperSPDDenseMatrix
 * @param AMatrix to copy. It must be a square matrix, and only the upper
 * triangular part is copied
 * @param deepTrue for a deep copy, else shallow. In that case,
 * <code>A</code> must be a dense matrix
 */
  public UpperSPDDenseMatrix(  Matrix A,  boolean deep){
    super(A,deep);
  }
  @Override public UpperSPDDenseMatrix copy(){
    return new UpperSPDDenseMatrix(this);
  }
  @Override public Matrix solve(  Matrix B,  Matrix X){
    return SPDsolve(B,X);
  }
}
