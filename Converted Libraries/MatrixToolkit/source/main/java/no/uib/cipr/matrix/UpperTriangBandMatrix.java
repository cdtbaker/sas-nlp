package no.uib.cipr.matrix;
/** 
 * Upper triangular banded matrix. The same storage as{@link no.uib.cipr.matrix.BandMatrix BandMatrix}, but without subdiagonals.
 */
public class UpperTriangBandMatrix extends AbstractTriangBandMatrix {
  /** 
 * Constructor for UpperTriangBandMatrix
 * @param nSize of the matrix. Since the matrix must be square, this
 * equals both the number of rows and columns
 * @param kdNumber of bands above the main diagonal (superdiagonals)
 */
  public UpperTriangBandMatrix(  int n,  int kd){
    super(n,0,kd,UpLo.Upper,Diag.NonUnit);
  }
  /** 
 * Constructor for UpperTriangBandMatrix
 * @param AMatrix to copy contents from. Only the parts of <code>A</code>
 * that lie within the allocated band are copied over, the rest
 * is ignored
 * @param kdNumber of bands above the main diagonal (superdiagonals)
 */
  public UpperTriangBandMatrix(  Matrix A,  int kd){
    this(A,kd,true);
  }
  /** 
 * Constructor for UpperTriangBandMatrix
 * @param AMatrix to copy contents from. Only the parts of <code>A</code>
 * that lie within the allocated band are copied over, the rest
 * is ignored
 * @param kdNumber of bands above the main diagonal (superdiagonals)
 * @param deepTrue for a deep copy. For shallow copies, <code>A</code>
 * must be a banded matrix
 */
  public UpperTriangBandMatrix(  Matrix A,  int kd,  boolean deep){
    super(A,0,kd,deep,UpLo.Upper,Diag.NonUnit);
  }
  /** 
 * Constructor for UpperTriangBandMatrix
 * @param nSize of the matrix. Since the matrix must be square, this
 * equals both the number of rows and columns
 * @param kdNumber of bands above the main diagonal (superdiagonals)
 */
  UpperTriangBandMatrix(  int n,  int kd,  Diag diag){
    super(n,0,kd,UpLo.Upper,diag);
  }
  /** 
 * Constructor for UpperTriangBandMatrix
 * @param AMatrix to copy contents from. Only the parts of <code>A</code>
 * that lie within the allocated band are copied over, the rest
 * is ignored
 * @param kdNumber of bands above the main diagonal (superdiagonals)
 * @param deepTrue for a deep copy. For shallow copies, <code>A</code>
 * must be a banded matrix
 */
  UpperTriangBandMatrix(  Matrix A,  int kd,  boolean deep,  Diag diag){
    super(A,0,kd,deep,UpLo.Upper,diag);
  }
  @Override public UpperTriangBandMatrix copy(){
    return new UpperTriangBandMatrix(this,ku);
  }
}
