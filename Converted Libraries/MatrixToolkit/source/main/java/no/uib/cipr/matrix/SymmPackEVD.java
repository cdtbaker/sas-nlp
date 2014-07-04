package no.uib.cipr.matrix;
import com.github.fommil.netlib.LAPACK;
import org.netlib.util.intW;
/** 
 * Computes eigenvalues of symmetrical, packed matrices
 */
public class SymmPackEVD extends SymmEVD {
  /** 
 * Double work array
 */
  private final double[] work;
  /** 
 * Integer work array
 */
  private final int[] iwork;
  /** 
 * Upper or lower part stored
 */
  private final UpLo uplo;
  /** 
 * Sets up an eigenvalue decomposition for symmetrical, packed matrices.
 * Computes all eigenvalues and eigenvectors
 * @param nSize of the matrix
 * @param upperTrue if the upper part of the matrix is stored, and false if
 * the lower part of the matrix is stored instead
 */
  public SymmPackEVD(  int n,  boolean upper){
    this(n,upper,true);
  }
  /** 
 * Sets up an eigenvalue decomposition for symmetrical, packed matrices
 * @param nSize of the matrix
 * @param upperTrue if the upper part of the matrix is stored, and false if
 * the lower part of the matrix is stored instead
 * @param vectorsTrue to compute the eigenvectors, false for just the
 * eigenvalues
 */
  public SymmPackEVD(  int n,  boolean upper,  boolean vectors){
    super(n,vectors);
    uplo=upper ? UpLo.Upper : UpLo.Lower;
    double[] worksize=new double[1];
    int[] iworksize=new int[1];
    intW info=new intW(0);
    LAPACK.getInstance().dspevd(job.netlib(),uplo.netlib(),n,new double[0],new double[0],new double[0],Matrices.ld(n),worksize,-1,iworksize,-1,info);
    int lwork=0, liwork=0;
    if (info.val != 0) {
      if (job == JobEig.All) {
        lwork=1 + 6 * n + n * n;
        liwork=3 + 5 * n;
      }
 else {
        lwork=2 * n;
        liwork=1;
      }
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
 * @param AMatrix to factorize. Upper part extracted, and the matrix is
 * not modified
 * @return Newly allocated decomposition
 * @throws NotConvergedException
 */
  public static SymmPackEVD factorize(  Matrix A) throws NotConvergedException {
    return new SymmPackEVD(A.numRows(),true).factor(new UpperSymmPackMatrix(A));
  }
  /** 
 * Computes the eigenvalue decomposition of the given matrix
 * @param AMatrix to factorize. Overwritten on return
 * @return The current eigenvalue decomposition
 * @throws NotConvergedException
 */
  public SymmPackEVD factor(  LowerSymmPackMatrix A) throws NotConvergedException {
    if (uplo != UpLo.Lower)     throw new IllegalArgumentException("Eigenvalue computer configured for lower-symmetrical matrices");
    return factor(A,A.getData());
  }
  /** 
 * Computes the eigenvalue decomposition of the given matrix
 * @param AMatrix to factorize. Overwritten on return
 * @return The current eigenvalue decomposition
 * @throws NotConvergedException
 */
  public SymmPackEVD factor(  UpperSymmPackMatrix A) throws NotConvergedException {
    if (uplo != UpLo.Upper)     throw new IllegalArgumentException("Eigenvalue computer configured for upper-symmetrical matrices");
    return factor(A,A.getData());
  }
  private SymmPackEVD factor(  Matrix A,  double[] data) throws NotConvergedException {
    if (A.numRows() != n)     throw new IllegalArgumentException("A.numRows() != n");
    intW info=new intW(0);
    LAPACK.getInstance().dspevd(job.netlib(),uplo.netlib(),n,data,w,job == JobEig.All ? Z.getData() : new double[0],Matrices.ld(n),work,work.length,iwork,iwork.length,info);
    if (info.val > 0)     throw new NotConvergedException(NotConvergedException.Reason.Iterations);
 else     if (info.val < 0)     throw new IllegalArgumentException();
    return this;
  }
}
