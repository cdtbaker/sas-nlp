package no.uib.cipr.matrix;
import com.github.fommil.netlib.LAPACK;
import org.netlib.util.intW;
/** 
 * Computes eigenvalue decompositions of general matrices
 */
public class EVD {
  /** 
 * Double work array
 */
  private final double[] work;
  /** 
 * Size of the matrix
 */
  private final int n;
  /** 
 * Job to do on the left and right eigenvectors
 */
  private final JobEig jobLeft, jobRight;
  /** 
 * Contains the real and imaginary parts of the eigenvalues
 */
  private final double[] Wr, Wi;
  /** 
 * Contains the left and the right eigenvectors
 */
  private final DenseMatrix Vl, Vr;
  /** 
 * Creates an empty eigenvalue decomposition which will compute all the
 * eigenvalues and eigenvectors (left and right)
 * @param nSize of the matrix
 */
  public EVD(  int n){
    this(n,true,true);
  }
  /** 
 * Creates an empty eigenvalue decomposition
 * @param nSize of the matrix
 * @param leftWhether to compute the left eigenvectors or not
 * @param rightWhether to compute the right eigenvectors or not
 */
  public EVD(  int n,  boolean left,  boolean right){
    this.n=n;
    this.jobLeft=left ? JobEig.All : JobEig.Eigenvalues;
    this.jobRight=right ? JobEig.All : JobEig.Eigenvalues;
    Wr=new double[n];
    Wi=new double[n];
    if (left)     Vl=new DenseMatrix(n,n);
 else     Vl=null;
    if (right)     Vr=new DenseMatrix(n,n);
 else     Vr=null;
    double[] worksize=new double[1];
    intW info=new intW(0);
    LAPACK.getInstance().dgeev(jobLeft.netlib(),jobRight.netlib(),n,new double[0],Matrices.ld(n),new double[0],new double[0],new double[0],Matrices.ld(n),new double[0],Matrices.ld(n),worksize,-1,info);
    int lwork=0;
    if (info.val != 0) {
      if (jobLeft == JobEig.All || jobRight == JobEig.All)       lwork=4 * n;
 else       lwork=3 * n;
    }
 else     lwork=(int)worksize[0];
    lwork=Math.max(1,lwork);
    work=new double[lwork];
  }
  /** 
 * Convenience method for computing the complete eigenvalue decomposition of
 * the given matrix
 * @param AMatrix to factorize. Not modified
 * @return Newly allocated decomposition
 * @throws NotConvergedException
 */
  public static EVD factorize(  Matrix A) throws NotConvergedException {
    return new EVD(A.numRows()).factor(new DenseMatrix(A));
  }
  /** 
 * Computes the eigenvalue decomposition of the given matrix
 * @param AMatrix to factorize. Overwritten on return
 * @return The current decomposition
 * @throws NotConvergedException
 */
  public EVD factor(  DenseMatrix A) throws NotConvergedException {
    if (!A.isSquare())     throw new IllegalArgumentException("!A.isSquare()");
 else     if (A.numRows() != n)     throw new IllegalArgumentException("A.numRows() != n");
    intW info=new intW(0);
    LAPACK.getInstance().dgeev(jobLeft.netlib(),jobRight.netlib(),n,A.getData(),Matrices.ld(n),Wr,Wi,jobLeft == JobEig.All ? Vl.getData() : new double[0],Matrices.ld(n),jobRight == JobEig.All ? Vr.getData() : new double[0],Matrices.ld(n),work,work.length,info);
    if (info.val > 0)     throw new NotConvergedException(NotConvergedException.Reason.Iterations);
 else     if (info.val < 0)     throw new IllegalArgumentException();
    return this;
  }
  /** 
 * Gets the left eigenvectors, if available
 */
  public DenseMatrix getLeftEigenvectors(){
    return Vl;
  }
  /** 
 * Gets the right eigenvectors, if available
 */
  public DenseMatrix getRightEigenvectors(){
    return Vr;
  }
  /** 
 * Gets the real part of the eigenvalues
 */
  public double[] getRealEigenvalues(){
    return Wr;
  }
  /** 
 * Gets the imaginary part of the eigenvalues
 */
  public double[] getImaginaryEigenvalues(){
    return Wi;
  }
  /** 
 * True if the left eigenvectors have been computed
 */
  public boolean hasLeftEigenvectors(){
    return Vl != null;
  }
  /** 
 * True if the right eigenvectors have been computed
 */
  public boolean hasRightEigenvectors(){
    return Vr != null;
  }
}
