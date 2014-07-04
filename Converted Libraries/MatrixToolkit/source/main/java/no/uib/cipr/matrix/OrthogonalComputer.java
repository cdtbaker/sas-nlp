package no.uib.cipr.matrix;
/** 
 * Base class for the orthogonal matrix decompositions (QR, RQ, LQ, and QL)
 */
abstract class OrthogonalComputer {
  /** 
 * The orthogonal matrix
 */
  final DenseMatrix Q;
  /** 
 * Lower triangular factor. May not be present
 */
  final LowerTriangDenseMatrix L;
  /** 
 * Upper triangular factor. May not be present
 */
  final UpperTriangDenseMatrix R;
  /** 
 * Factorisation sizes
 */
  final int m, n, k;
  /** 
 * Work arrays
 */
  double[] work, workGen;
  /** 
 * Scales for the reflectors
 */
  final double[] tau;
  /** 
 * Constructor for OrthogonalComputer
 * @param mNumber of rows
 * @param nNumber of columns
 * @param upperTrue for storing an upper triangular factor, false for a lower
 * triangular factor
 */
  OrthogonalComputer(  int m,  int n,  boolean upper){
    this.m=m;
    this.n=n;
    this.k=Math.min(m,n);
    tau=new double[k];
    Q=new DenseMatrix(m,n);
    if (upper) {
      R=new UpperTriangDenseMatrix(Math.min(m,n));
      L=null;
    }
 else {
      L=new LowerTriangDenseMatrix(Math.min(m,n));
      R=null;
    }
  }
  /** 
 * Computes an orthogonal decomposition
 * @param AMatrix to decompose. Overwritten on exit. Pass a copy to avoid
 * this
 * @return The current decomposition
 */
  public abstract OrthogonalComputer factor(  DenseMatrix A);
  /** 
 * Returns the orthogonal part of the factorization
 */
  public DenseMatrix getQ(){
    return Q;
  }
}
