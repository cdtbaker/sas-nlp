package no.uib.cipr.matrix;
import no.uib.cipr.matrix.Matrix.Norm;
import com.github.fommil.netlib.LAPACK;
import org.netlib.util.doubleW;
import org.netlib.util.intW;
/** 
 * Banded LU decomposition
 */
public class BandLU {
  /** 
 * Matrix dimension
 */
  private final int n;
  /** 
 * Number of bands in the matrix A
 */
  private final int kl, ku;
  /** 
 * Holds the LU factors
 */
  private final BandMatrix LU;
  /** 
 * Row pivotations
 */
  private final int[] ipiv;
  /** 
 * True if the matrix was singular
 */
  private boolean singular;
  /** 
 * Constructor for BandLU
 * @param nMatrix size
 * @param klNumber of lower matrix bands
 * @param kuNumber of upper matrix bands
 */
  public BandLU(  int n,  int kl,  int ku){
    this.n=n;
    this.kl=kl;
    this.ku=ku;
    LU=new BandMatrix(n,kl,ku + kl);
    ipiv=new int[n];
  }
  /** 
 * Creates an LU decomposition of the given matrix
 * @param AMatrix to decompose. Not modified
 * @return A LU decomposition of the matrix
 */
  public static BandLU factorize(  BandMatrix A){
    return new BandLU(A.numRows(),A.kl,A.ku).factor(A,false);
  }
  /** 
 * Creates an LU decomposition of the given matrix
 * @param AMatrix to decompose. If the decomposition is in-place, its
 * number of superdiagonals must equal <code>kl+ku</code>
 * @param inplaceWheter or not the decomposition should overwrite the passed
 * matrix
 * @return The current decomposition
 */
  public BandLU factor(  BandMatrix A,  boolean inplace){
    if (inplace)     return factor(A);
 else     return factor(new BandMatrix(A,kl,kl + ku));
  }
  /** 
 * Creates an LU decomposition of the given matrix
 * @param AMatrix to decompose. It will be overwritten with the
 * decomposition. Its number of superdiagonals must equal
 * <code>kl+ku</code>
 * @return The current decomposition
 */
  public BandLU factor(  BandMatrix A){
    if (!(A.isSquare()))     throw new IllegalArgumentException("!A.isSquare()");
    if (n != A.numRows())     throw new IllegalArgumentException("n != A.numRows()");
    if (A.ku != ku + kl)     throw new IllegalArgumentException("A.ku != ku + kl");
    singular=false;
    intW info=new intW(0);
    LAPACK.getInstance().dgbtrf(n,n,kl,ku,A.getData(),2 * kl + ku + 1,ipiv,info);
    if (info.val > 0)     singular=true;
 else     if (info.val < 0)     throw new IllegalArgumentException();
    LU.set(A);
    return this;
  }
  /** 
 * Returns the lower triangular factor
 */
  public UnitLowerTriangBandMatrix getL(){
    return new UnitLowerTriangBandMatrix(LU,LU.numSubDiagonals(),false);
  }
  /** 
 * Returns the upper triangular factor
 */
  public UpperTriangBandMatrix getU(){
    return new UpperTriangBandMatrix(LU,LU.numSuperDiagonals(),false);
  }
  /** 
 * Returns the decomposition matrix
 */
  public BandMatrix getLU(){
    return LU;
  }
  /** 
 * Returns the row pivots
 */
  public int[] getPivots(){
    return ipiv;
  }
  /** 
 * Checks for singularity
 */
  public boolean isSingular(){
    return singular;
  }
  /** 
 * Computes the reciprocal condition number, using either the infinity norm
 * of the 1 norm.
 * @param AThe matrix this is a decomposition of
 * @param normEither <code>Norm.One</code> or <code>Norm.Infinity</code>
 * @return The reciprocal condition number. Values close to unity indicate a
 * well-conditioned system, while numbers close to zero do not.
 */
  public double rcond(  Matrix A,  Norm norm){
    if (norm != Norm.One && norm != Norm.Infinity)     throw new IllegalArgumentException("Only the 1 or the Infinity norms are supported");
    if (A.numRows() != n)     throw new IllegalArgumentException("A.numRows() != n");
    if (!A.isSquare())     throw new IllegalArgumentException("!A.isSquare()");
    double anorm=A.norm(norm);
    double[] work=new double[3 * n];
    int[] lwork=new int[n];
    intW info=new intW(0);
    doubleW rcond=new doubleW(0);
    LAPACK.getInstance().dgbcon(norm.netlib(),n,kl,ku,LU.getData(),Matrices.ld(2 * kl + ku + 1),ipiv,anorm,rcond,work,lwork,info);
    if (info.val < 0)     throw new IllegalArgumentException();
    return rcond.val;
  }
  /** 
 * Computes <code>A\B</code>, overwriting <code>B</code>
 */
  public DenseMatrix solve(  DenseMatrix B) throws MatrixSingularException {
    return solve(B,Transpose.NoTranspose);
  }
  /** 
 * Computes <code>A<sup>T</sup>\B</code>, overwriting <code>B</code>
 */
  public DenseMatrix transSolve(  DenseMatrix B) throws MatrixSingularException {
    return solve(B,Transpose.Transpose);
  }
  private DenseMatrix solve(  DenseMatrix B,  Transpose trans) throws MatrixSingularException {
    if (singular)     throw new MatrixSingularException();
    if (B.numRows() != n)     throw new IllegalArgumentException("B.numRows() != n");
    intW info=new intW(0);
    LAPACK.getInstance().dgbtrs(trans.netlib(),n,kl,ku,B.numColumns(),LU.getData(),2 * kl + ku + 1,ipiv,B.getData(),Matrices.ld(n),info);
    if (info.val < 0)     throw new IllegalArgumentException();
    return B;
  }
}
