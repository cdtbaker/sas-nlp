package no.uib.cipr.matrix;
import no.uib.cipr.matrix.Matrix.Norm;
import com.github.fommil.netlib.LAPACK;
import org.netlib.util.doubleW;
import org.netlib.util.intW;
/** 
 * Packed Cholesky decomposition
 */
public class PackCholesky {
  /** 
 * Matrix dimension
 */
  private final int n;
  /** 
 * Cholesky decomposition of a lower matrix
 */
  private LowerTriangPackMatrix Cl;
  /** 
 * Cholesky decomposition of an upper matrix
 */
  private UpperTriangPackMatrix Cu;
  /** 
 * If the matrix is SPD or not
 */
  private boolean notspd;
  /** 
 * True for upper part, else false
 */
  private final boolean upper;
  /** 
 * Constructor for DenseCholesky
 * @param nMatrix size
 * @param upperTrue for decomposing an upper symmetrical matrix, false for a
 * lower symmetrical matrix
 */
  public PackCholesky(  int n,  boolean upper){
    this.n=n;
    this.upper=upper;
    if (upper)     Cu=new UpperTriangPackMatrix(n);
 else     Cl=new LowerTriangPackMatrix(n);
  }
  /** 
 * Calculates a Cholesky decomposition
 * @param AMatrix to decompose. Not modified
 * @return The current decomposition
 */
  public static PackCholesky factorize(  Matrix A){
    return new PackCholesky(A.numRows(),true).factor(new UpperSPDPackMatrix(A));
  }
  /** 
 * Calculates a Cholesky decomposition
 * @param AMatrix to decompose. Overwritten on return
 * @return The current decomposition
 */
  public PackCholesky factor(  LowerSPDPackMatrix A){
    if (upper)     throw new IllegalArgumentException("Cholesky decomposition constructed for upper matrices");
    return decompose(A);
  }
  /** 
 * Calculates a Cholesky decomposition
 * @param AMatrix to decompose. Overwritten on return
 * @return The current decomposition
 */
  public PackCholesky factor(  UpperSPDPackMatrix A){
    if (!upper)     throw new IllegalArgumentException("Cholesky decomposition constructed for lower matrices");
    return decompose(A);
  }
  private PackCholesky decompose(  AbstractPackMatrix A){
    if (n != A.numRows())     throw new IllegalArgumentException("n != A.numRows()");
    notspd=false;
    intW info=new intW(0);
    if (upper)     LAPACK.getInstance().dpptrf(UpLo.Upper.netlib(),A.numRows(),A.getData(),info);
 else     LAPACK.getInstance().dpptrf(UpLo.Lower.netlib(),A.numRows(),A.getData(),info);
    if (info.val > 0)     notspd=true;
 else     if (info.val < 0)     throw new IllegalArgumentException();
    if (upper)     Cu.set(A);
 else     Cl.set(A);
    return this;
  }
  /** 
 * Returns true if the matrix decomposed is symmetrical, positive definite
 */
  public boolean isSPD(){
    return !notspd;
  }
  /** 
 * Returns the decomposition matrix. Only valid for decomposition of a lower
 * SPD matrix
 */
  public LowerTriangPackMatrix getL(){
    if (!upper)     return Cl;
 else     throw new UnsupportedOperationException();
  }
  /** 
 * Returns the decomposition matrix. Only valid for decomposition of a upper
 * SPD matrix
 */
  public UpperTriangPackMatrix getU(){
    if (upper)     return Cu;
 else     throw new UnsupportedOperationException();
  }
  /** 
 * Solves for <code>B</code>, overwriting it on return
 */
  public DenseMatrix solve(  DenseMatrix B) throws MatrixNotSPDException {
    if (notspd)     throw new MatrixNotSPDException();
    if (B.numRows() != n)     throw new IllegalArgumentException("B.numRows() != n");
    intW info=new intW(0);
    if (upper)     LAPACK.getInstance().dpptrs(UpLo.Upper.netlib(),Cu.numRows(),B.numColumns(),Cu.getData(),B.getData(),Matrices.ld(Cu.numRows()),info);
 else     LAPACK.getInstance().dpptrs(UpLo.Lower.netlib(),Cl.numRows(),B.numColumns(),Cl.getData(),B.getData(),Matrices.ld(Cl.numRows()),info);
    if (info.val < 0)     throw new IllegalArgumentException();
    return B;
  }
  /** 
 * Computes the reciprocal condition number
 * @param AThe matrix this is a decomposition of
 * @return The reciprocal condition number. Values close to unity indicate a
 * well-conditioned system, while numbers close to zero do not.
 */
  public double rcond(  Matrix A){
    if (A.numRows() != n)     throw new IllegalArgumentException("A.numRows() != n");
    if (!A.isSquare())     throw new IllegalArgumentException("!A.isSquare()");
    double anorm=A.norm(Norm.One);
    double[] work=new double[3 * n];
    int[] iwork=new int[n];
    intW info=new intW(0);
    doubleW rcond=new doubleW(0);
    if (upper)     LAPACK.getInstance().dppcon(UpLo.Upper.netlib(),n,Cu.getData(),anorm,rcond,work,iwork,info);
 else     LAPACK.getInstance().dppcon(UpLo.Lower.netlib(),n,Cl.getData(),anorm,rcond,work,iwork,info);
    if (info.val < 0)     throw new IllegalArgumentException();
    return rcond.val;
  }
}
