package no.uib.cipr.matrix;
import com.github.fommil.netlib.LAPACK;
import org.netlib.util.intW;
/** 
 * Symmetrical positive definite tridiagonal matrix. Same as{@link no.uib.cipr.matrix.SymmTridiagMatrix SymmTridiagMatrix}, and is used
 * as a marker class to allow for more efficient solvers.
 */
public class SPDTridiagMatrix extends SymmTridiagMatrix {
  /** 
 * Constructor for SPDTridiagMatrix
 * @param nSize of the matrix. Since the matrix must be square, this
 * equals both the number of rows and columns
 */
  public SPDTridiagMatrix(  int n){
    super(n);
  }
  /** 
 * Constructor for SPDTridiagMatrix
 * @param AMatrix to copy contents from. Only main and the superdiagonal
 * is copied over
 */
  public SPDTridiagMatrix(  Matrix A){
    super(A);
  }
  /** 
 * Constructor for SPDTridiagMatrix
 * @param AMatrix to copy contents from. Only main and the superdiagonal
 * is copied over
 * @param deepTrue for a deep copy. For shallow copies <code>A</code> must
 * be a <code>SymmTridiagMatrix</code>
 */
  public SPDTridiagMatrix(  Matrix A,  boolean deep){
    super(A,deep);
  }
  @Override public SPDTridiagMatrix copy(){
    return new SPDTridiagMatrix(this);
  }
  @Override public Matrix solve(  Matrix B,  Matrix X){
    if (!(X instanceof DenseMatrix))     throw new UnsupportedOperationException("X must be a DenseMatrix");
    checkSolve(B,X);
    double[] Xd=((DenseMatrix)X).getData();
    X.set(B);
    intW info=new intW(0);
    LAPACK.getInstance().dptsv(numRows,X.numColumns(),diag.clone(),offDiag.clone(),Xd,Matrices.ld(numRows),info);
    if (info.val > 0)     throw new MatrixNotSPDException();
 else     if (info.val < 0)     throw new IllegalArgumentException();
    return X;
  }
}
