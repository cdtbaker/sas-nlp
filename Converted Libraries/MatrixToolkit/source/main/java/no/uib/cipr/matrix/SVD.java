package no.uib.cipr.matrix;
import com.github.fommil.netlib.LAPACK;
import org.netlib.util.intW;
/** 
 * Computes singular value decompositions
 */
public class SVD {
  /** 
 * Work array
 */
  private final double[] work;
  /** 
 * Work array
 */
  private final int[] iwork;
  /** 
 * Matrix dimension
 */
  private final int m, n;
  /** 
 * Compute the singular vectors fully?
 */
  private final boolean vectors;
  /** 
 * Job to do
 */
  private final JobSVD job;
  /** 
 * The singular values
 */
  private final double[] S;
  /** 
 * Singular vectors
 */
  private final DenseMatrix U, Vt;
  /** 
 * Creates an empty SVD which will compute all singular values and vectors
 * @param mNumber of rows
 * @param nNumber of columns
 */
  public SVD(  int m,  int n){
    this(m,n,true);
  }
  /** 
 * Creates an empty SVD
 * @param mNumber of rows
 * @param nNumber of columns
 * @param vectorsTrue to compute the singular vectors, false for just the
 * singular values
 */
  public SVD(  int m,  int n,  boolean vectors){
    this.m=m;
    this.n=n;
    this.vectors=vectors;
    S=new double[Math.min(m,n)];
    if (vectors) {
      U=new DenseMatrix(m,m);
      Vt=new DenseMatrix(n,n);
    }
 else     U=Vt=null;
    job=vectors ? JobSVD.All : JobSVD.None;
    iwork=new int[8 * Math.min(m,n)];
    double[] worksize=new double[1];
    intW info=new intW(0);
    LAPACK.getInstance().dgesdd(job.netlib(),m,n,new double[0],Matrices.ld(m),new double[0],new double[0],Matrices.ld(m),new double[0],Matrices.ld(n),worksize,-1,iwork,info);
    int lwork=-1;
    if (info.val != 0) {
      if (vectors)       lwork=3 * Math.min(m,n) * Math.min(m,n) + Math.max(Math.max(m,n),4 * Math.min(m,n) * Math.min(m,n) + 4 * Math.min(m,n));
 else       lwork=3 * Math.min(m,n) * Math.min(m,n) + Math.max(Math.max(m,n),5 * Math.min(m,n) * Math.min(m,n) + 4 * Math.min(m,n));
    }
 else     lwork=(int)worksize[0];
    lwork=Math.max(lwork,1);
    work=new double[lwork];
  }
  /** 
 * Convenience method for computing a full SVD
 * @param AMatrix to decompose, not modified
 * @return Newly allocated factorization
 * @throws NotConvergedException
 */
  public static SVD factorize(  Matrix A) throws NotConvergedException {
    return new SVD(A.numRows(),A.numColumns()).factor(new DenseMatrix(A));
  }
  /** 
 * Computes an SVD
 * @param AMatrix to decompose. Size must conform, and it will be
 * overwritten on return. Pass a copy to avoid this
 * @return The current decomposition
 * @throws NotConvergedException
 */
  public SVD factor(  DenseMatrix A) throws NotConvergedException {
    if (A.numRows() != m)     throw new IllegalArgumentException("A.numRows() != m");
 else     if (A.numColumns() != n)     throw new IllegalArgumentException("A.numColumns() != n");
    intW info=new intW(0);
    LAPACK.getInstance().dgesdd(job.netlib(),m,n,A.getData(),Matrices.ld(m),S,vectors ? U.getData() : new double[0],Matrices.ld(m),vectors ? Vt.getData() : new double[0],Matrices.ld(n),work,work.length,iwork,info);
    if (info.val > 0)     throw new NotConvergedException(NotConvergedException.Reason.Iterations);
 else     if (info.val < 0)     throw new IllegalArgumentException();
    return this;
  }
  /** 
 * True if singular vectors are stored
 */
  public boolean hasSingularVectors(){
    return U != null;
  }
  /** 
 * Returns the left singular vectors, column-wise. Not available for partial
 * decompositions
 * @return Matrix of size m*m
 */
  public DenseMatrix getU(){
    return U;
  }
  /** 
 * Returns the right singular vectors, row-wise. Not available for partial
 * decompositions
 * @return Matrix of size n*n
 */
  public DenseMatrix getVt(){
    return Vt;
  }
  /** 
 * Returns the singular values (stored in descending order)
 * @return Array of size min(m,n)
 */
  public double[] getS(){
    return S;
  }
}
