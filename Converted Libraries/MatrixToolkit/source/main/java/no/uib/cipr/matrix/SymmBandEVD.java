package no.uib.cipr.matrix;
import com.github.fommil.netlib.LAPACK;
import org.netlib.util.intW;
/** 
 * Computes eigenvalues of symmetrical, banded matrices
 */
public class SymmBandEVD extends SymmEVD {
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
 * Sets up an eigenvalue decomposition for symmetrical, banded matrices.
 * Computes all eigenvalues and eigenvectors
 * @param nSize of the matrix
 * @param upperTrue if the upper part of the matrix is stored, and false if
 * the lower part of the matrix is stored instead
 */
  public SymmBandEVD(  int n,  boolean upper){
    this(n,upper,true);
  }
  /** 
 * Sets up an eigenvalue decomposition for symmetrical, banded matrices
 * @param nSize of the matrix
 * @param upperTrue if the upper part of the matrix is stored, and false if
 * the lower part of the matrix is stored instead
 * @param vectorsTrue to compute the eigenvectors, false for just the
 * eigenvalues
 */
  public SymmBandEVD(  int n,  boolean upper,  boolean vectors){
    super(n,vectors);
    uplo=upper ? UpLo.Upper : UpLo.Lower;
    double[] worksize=new double[1];
    int[] iworksize=new int[1];
    intW info=new intW(0);
    LAPACK.getInstance().dsbevd(job.netlib(),uplo.netlib(),n,0,new double[0],1,new double[0],new double[0],Matrices.ld(n),worksize,-1,iworksize,-1,info);
    int lwork=0, liwork=0;
    if (info.val != 0) {
      if (job == JobEig.All) {
        lwork=1 + 6 * n + 2 * n * n;
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
 * @param AMatrix to factorize. The upper triangular part is extracted,
 * and the matrix is not modified
 * @param kdNumber of diagonals to extract
 * @return Newly allocated decomposition
 * @throws NotConvergedException
 */
  public static SymmBandEVD factorize(  Matrix A,  int kd) throws NotConvergedException {
    return new SymmBandEVD(A.numRows(),true).factor(new UpperSymmBandMatrix(A,kd));
  }
  /** 
 * Computes the eigenvalue decomposition of the given matrix
 * @param AMatrix to factorize. Overwritten on return
 * @return The current eigenvalue decomposition
 * @throws NotConvergedException
 */
  public SymmBandEVD factor(  LowerSymmBandMatrix A) throws NotConvergedException {
    if (uplo != UpLo.Lower)     throw new IllegalArgumentException("Eigenvalue computer configured for lower-symmetrical matrices");
    return factor(A,A.getData(),A.numSubDiagonals());
  }
  /** 
 * Computes the eigenvalue decomposition of the given matrix
 * @param AMatrix to factorize. Overwritten on return
 * @return The current eigenvalue decomposition
 * @throws NotConvergedException
 */
  public SymmBandEVD factor(  UpperSymmBandMatrix A) throws NotConvergedException {
    if (uplo != UpLo.Upper)     throw new IllegalArgumentException("Eigenvalue computer configured for upper-symmetrical matrices");
    return factor(A,A.getData(),A.numSuperDiagonals());
  }
  private SymmBandEVD factor(  Matrix A,  double[] data,  int kd) throws NotConvergedException {
    if (A.numRows() != n)     throw new IllegalArgumentException("A.numRows() != n");
    intW info=new intW(0);
    LAPACK.getInstance().dsbevd(job.netlib(),uplo.netlib(),n,kd,data,Matrices.ld(kd + 1),w,job == JobEig.All ? Z.getData() : new double[0],Matrices.ld(n),work,work.length,iwork,iwork.length,info);
    if (info.val > 0)     throw new NotConvergedException(NotConvergedException.Reason.Iterations);
 else     if (info.val < 0)     throw new IllegalArgumentException();
    return this;
  }
}
