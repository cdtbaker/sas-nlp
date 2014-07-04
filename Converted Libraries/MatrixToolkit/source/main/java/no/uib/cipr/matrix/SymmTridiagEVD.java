package no.uib.cipr.matrix;
import com.github.fommil.netlib.LAPACK;
import org.netlib.util.intW;
/** 
 * Computes eigenvalues of symmetrical, tridiagonal matrices
 */
public class SymmTridiagEVD extends SymmEVD {
  /** 
 * Double work array
 */
  private final double[] work;
  /** 
 * Integer work array
 */
  private final int[] iwork;
  /** 
 * Range of eigenvalues to compute
 */
  private final JobEigRange range;
  /** 
 * Eigenvector supports
 */
  private final int[] isuppz;
  /** 
 * Tolerance criteria
 */
  private final double abstol;
  /** 
 * Sets up an eigenvalue decomposition for symmetrical, tridiagonal
 * matrices. Computes all eigenvalues and eigenvectors, and uses a low
 * default tolerance criteria
 * @param nSize of the matrix
 */
  public SymmTridiagEVD(  int n){
    this(n,true);
  }
  /** 
 * Sets up an eigenvalue decomposition for symmetrical, tridiagonal
 * matrices. Computes all eigenvalues and eigenvectors
 * @param nSize of the matrix
 * @param abstolAbsolute tolerance criteria
 */
  public SymmTridiagEVD(  int n,  double abstol){
    this(n,true,abstol);
  }
  /** 
 * Sets up an eigenvalue decomposition for symmetrical, tridiagonal
 * matrices. Uses a low default tolerance criteria
 * @param nSize of the matrix
 * @param vectorsTrue to compute the eigenvectors, false for just the
 * eigenvalues
 */
  public SymmTridiagEVD(  int n,  boolean vectors){
    this(n,vectors,LAPACK.getInstance().dlamch("Safe minimum"));
  }
  /** 
 * Sets up an eigenvalue decomposition for symmetrical, tridiagonal matrices
 * @param nSize of the matrix
 * @param vectorsTrue to compute the eigenvectors, false for just the
 * eigenvalues
 * @param abstolAbsolute tolerance criteria
 */
  public SymmTridiagEVD(  int n,  boolean vectors,  double abstol){
    super(n,vectors);
    this.abstol=abstol;
    range=JobEigRange.All;
    isuppz=new int[2 * Math.max(1,n)];
    double[] worksize=new double[1];
    int[] iworksize=new int[1];
    intW info=new intW(0);
    LAPACK.getInstance().dstevr(job.netlib(),range.netlib(),n,new double[0],new double[0],0,0,0,0,abstol,new intW(1),new double[0],new double[0],Matrices.ld(n),isuppz,worksize,-1,iworksize,-1,info);
    int lwork=0, liwork=0;
    if (info.val != 0) {
      lwork=20 * n;
      liwork=10 * n;
    }
 else {
      lwork=(int)worksize[0];
      liwork=iworksize[0];
    }
    lwork=Math.max(1,lwork);
    liwork=Math.max(1,liwork);
    work=new double[lwork];
    iwork=new int[liwork];
  }
  /** 
 * Convenience method for computing the full eigenvalue decomposition of the
 * given matrix
 * @param AMatrix to factorize. Main diagonal and superdiagonal is
 * copied, and the matrix is not modified
 * @return Newly allocated decomposition
 * @throws NotConvergedException
 */
  public static SymmTridiagEVD factorize(  Matrix A) throws NotConvergedException {
    return new SymmTridiagEVD(A.numRows()).factor(new SymmTridiagMatrix(A));
  }
  /** 
 * Computes the eigenvalue decomposition of the given matrix
 * @param AMatrix to factorize. Overwritten on return
 * @return The current eigenvalue decomposition
 * @throws NotConvergedException
 */
  public SymmTridiagEVD factor(  SymmTridiagMatrix A) throws NotConvergedException {
    if (A.numRows() != n)     throw new IllegalArgumentException("A.numRows() != n");
    intW info=new intW(0);
    LAPACK.getInstance().dstevr(job.netlib(),range.netlib(),n,A.getDiagonal(),A.getOffDiagonal(),0,0,0,0,abstol,new intW(1),w,job == JobEig.All ? Z.getData() : new double[0],Matrices.ld(n),isuppz,work,work.length,iwork,iwork.length,info);
    if (info.val > 0)     throw new NotConvergedException(NotConvergedException.Reason.Iterations);
 else     if (info.val < 0)     throw new IllegalArgumentException();
    return this;
  }
}
