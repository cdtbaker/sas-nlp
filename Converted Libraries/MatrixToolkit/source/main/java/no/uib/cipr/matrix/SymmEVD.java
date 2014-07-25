package no.uib.cipr.matrix;
/** 
 * Symmetric eigenvalue decomposition
 */
abstract class SymmEVD {
  /** 
 * Size of the matrix
 */
  final int n;
  /** 
 * The eigenvalues
 */
  final double[] w;
  /** 
 * The eigenvectors stored columnwise
 */
  final DenseMatrix Z;
  /** 
 * Job to do
 */
  final JobEig job;
  /** 
 * Allocates storage for an eigenvalue computation
 * @param nSize of the matrix
 * @param vectorsTrue to compute the eigenvectors, false for just the
 * eigenvalues
 */
  public SymmEVD(  int n,  boolean vectors){
    this.n=n;
    w=new double[n];
    job=vectors ? JobEig.All : JobEig.Eigenvalues;
    if (vectors)     Z=new DenseMatrix(n,n);
 else     Z=null;
  }
  /** 
 * Allocates storage for an eigenvalue computation. Includes eigenvectors
 * @param nSize of the matrix
 */
  public SymmEVD(  int n){
    this(n,true);
  }
  /** 
 * Gets the eigenvalues (stored in ascending order)
 */
  public double[] getEigenvalues(){
    return w;
  }
  /** 
 * Gets the eigenvectors, if available
 */
  public DenseMatrix getEigenvectors(){
    return Z;
  }
  /** 
 * True if the eigenvectors have been computed
 */
  public boolean hasEigenvectors(){
    return Z != null;
  }
}
