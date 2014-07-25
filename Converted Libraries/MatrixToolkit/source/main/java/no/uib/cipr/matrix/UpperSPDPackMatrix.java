package no.uib.cipr.matrix;
/** 
 * Upper symmetrical positive definite packed matrix. Same layout as{@link no.uib.cipr.matrix.UpperSymmPackMatrix UpperSymmPackMatrix}. This
 * class does not enforce the SPD property, but serves as a tag so that more
 * efficient algorithms can be used in the solvers.
 */
public class UpperSPDPackMatrix extends UpperSymmPackMatrix {
  /** 
 * Constructor for UpperSPDPackMatrix
 * @param nSize of the matrix. Since the matrix must be square, this
 * equals both the number of rows and columns
 */
  public UpperSPDPackMatrix(  int n){
    super(n);
  }
  /** 
 * Constructor for UpperSPDPackMatrix
 * @param AMatrix to copy contents from. Only the entries of the relevant
 * part are copied
 */
  public UpperSPDPackMatrix(  Matrix A){
    super(A);
  }
  /** 
 * Constructor for UpperSPDPackMatrix
 * @param AMatrix to copy contents from. Only the entries of the relevant
 * part are copied
 * @param deepTrue if the copy is deep, else false (giving a shallow copy).
 * For shallow copies, <code>A</code> must be a packed matrix
 */
  public UpperSPDPackMatrix(  Matrix A,  boolean deep){
    super(A,deep);
  }
  @Override public UpperSPDPackMatrix copy(){
    return new UpperSPDPackMatrix(this);
  }
  @Override public Matrix solve(  Matrix B,  Matrix X){
    return SPDsolve(B,X);
  }
}
