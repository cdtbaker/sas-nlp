package no.uib.cipr.matrix;
/** 
 * Lower triangular banded matrix. The same storage as{@link no.uib.cipr.matrix.BandMatrix BandMatrix}, but without
 * superdiagonals.
 */
public class LowerTriangBandMatrix extends AbstractTriangBandMatrix {
  /** 
 * Constructor for LowerTriangBandMatrix
 * @param nSize of the matrix. Since the matrix must be square, this
 * equals both the number of rows and columns
 * @param kdNumber of bands below the main diagonal (subdiagonals)
 */
  public LowerTriangBandMatrix(  int n,  int kd){
    super(n,kd,0,UpLo.Lower,Diag.NonUnit);
  }
  /** 
 * Constructor for LowerTriangBandMatrix
 * @param AMatrix to copy contents from. Only the parts of <code>A</code>
 * that lie within the allocated band are copied over, the rest
 * is ignored
 * @param kdNumber of bands below the main diagonal (subdiagonals)
 */
  public LowerTriangBandMatrix(  Matrix A,  int kd){
    this(A,kd,true);
  }
  /** 
 * Constructor for LowerTriangBandMatrix
 * @param AMatrix to copy contents from. Only the parts of <code>A</code>
 * that lie within the allocated band are copied over, the rest
 * is ignored
 * @param kdNumber of bands below the main diagonal (subdiagonals)
 * @param deepTrue for a deep copy. For shallow copies, <code>A</code>
 * must be a banded matrix
 */
  public LowerTriangBandMatrix(  Matrix A,  int kd,  boolean deep){
    super(A,kd,0,deep,UpLo.Lower,Diag.NonUnit);
  }
  /** 
 * Constructor for LowerTriangBandMatrix
 * @param nSize of the matrix. Since the matrix must be square, this
 * equals both the number of rows and columns
 * @param kdNumber of bands below the main diagonal (subdiagonals)
 */
  LowerTriangBandMatrix(  int n,  int kd,  Diag diag){
    super(n,kd,0,UpLo.Lower,diag);
  }
  /** 
 * Constructor for LowerTriangBandMatrix
 * @param AMatrix to copy contents from. Only the parts of <code>A</code>
 * that lie within the allocated band are copied over, the rest
 * is ignored
 * @param kdNumber of bands below the main diagonal (subdiagonals)
 * @param deepTrue for a deep copy. For shallow copies, <code>A</code>
 * must be a banded matrix
 */
  LowerTriangBandMatrix(  Matrix A,  int kd,  boolean deep,  Diag diag){
    super(A,kd,0,deep,UpLo.Lower,diag);
  }
  @Override public LowerTriangBandMatrix copy(){
    return new LowerTriangBandMatrix(this,kl);
  }
}
