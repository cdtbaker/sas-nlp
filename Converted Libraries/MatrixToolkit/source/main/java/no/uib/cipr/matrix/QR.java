package no.uib.cipr.matrix;
import com.github.fommil.netlib.LAPACK;
import org.netlib.util.intW;
/** 
 * Computers QR decompositions
 */
public class QR extends OrthogonalComputer {
  /** 
 * Constructs an empty QR decomposition
 * @param mNumber of rows. Must be larger than or equal the number of
 * columns
 * @param nNumber of columns
 */
  public QR(  int m,  int n){
    super(m,n,true);
    if (n > m)     throw new IllegalArgumentException("n > m");
    int lwork;
{
      work=new double[1];
      intW info=new intW(0);
      LAPACK.getInstance().dgeqrf(m,n,new double[0],Matrices.ld(m),new double[0],work,-1,info);
      if (info.val != 0)       lwork=n;
 else       lwork=(int)work[0];
      lwork=Math.max(1,lwork);
      work=new double[lwork];
    }
{
      workGen=new double[1];
      intW info=new intW(0);
      LAPACK.getInstance().dorgqr(m,n,k,new double[0],Matrices.ld(m),new double[0],workGen,-1,info);
      if (info.val != 0)       lwork=n;
 else       lwork=(int)workGen[0];
      lwork=Math.max(1,lwork);
      workGen=new double[lwork];
    }
  }
  /** 
 * Convenience method to compute a QR decomposition
 * @param AMatrix to decompose. Not modified
 * @return Newly allocated decomposition
 */
  public static QR factorize(  Matrix A){
    return new QR(A.numRows(),A.numColumns()).factor(new DenseMatrix(A));
  }
  @Override public QR factor(  DenseMatrix A){
    if (Q.numRows() != A.numRows())     throw new IllegalArgumentException("Q.numRows() != A.numRows()");
 else     if (Q.numColumns() != A.numColumns())     throw new IllegalArgumentException("Q.numColumns() != A.numColumns()");
 else     if (R == null)     throw new IllegalArgumentException("R == null");
    intW info=new intW(0);
    LAPACK.getInstance().dgeqrf(m,n,A.getData(),Matrices.ld(m),tau,work,work.length,info);
    if (info.val < 0)     throw new IllegalArgumentException();
    R.zero();
    for (    MatrixEntry e : A)     if (e.row() <= e.column())     R.set(e.row(),e.column(),e.get());
    info.val=0;
    LAPACK.getInstance().dorgqr(m,n,k,A.getData(),Matrices.ld(m),tau,workGen,workGen.length,info);
    if (info.val < 0)     throw new IllegalArgumentException();
    Q.set(A);
    return this;
  }
  /** 
 * Returns the upper triangular factor
 */
  public UpperTriangDenseMatrix getR(){
    return R;
  }
}
