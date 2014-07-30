package no.uib.cipr.matrix;
import com.github.fommil.netlib.LAPACK;
import org.netlib.util.intW;
/** 
 * Computes LQ decompositions
 */
public class LQ extends OrthogonalComputer {
  /** 
 * Constructs an empty LQ decomposition
 * @param mNumber of rows
 * @param nNumber of columns. Must be larger than or equal the number of
 * rows
 */
  public LQ(  int m,  int n){
    super(m,n,false);
    if (n < m)     throw new IllegalArgumentException("n < m");
    int lwork;
{
      work=new double[1];
      intW info=new intW(0);
      LAPACK.getInstance().dgelqf(m,n,new double[0],Matrices.ld(m),new double[0],work,-1,info);
      if (info.val != 0)       lwork=m;
 else       lwork=(int)work[0];
      lwork=Math.max(1,lwork);
      work=new double[lwork];
    }
{
      workGen=new double[1];
      intW info=new intW(0);
      LAPACK.getInstance().dorglq(m,n,m,new double[0],Matrices.ld(m),new double[0],workGen,-1,info);
      if (info.val != 0)       lwork=m;
 else       lwork=(int)workGen[0];
      lwork=Math.max(1,lwork);
      workGen=new double[lwork];
    }
  }
  /** 
 * Convenience method to compute a LQ decomposition
 * @param AMatrix to decompose. Not modified
 * @return Newly allocated decomposition
 */
  public static LQ factorize(  Matrix A){
    return new LQ(A.numRows(),A.numColumns()).factor(new DenseMatrix(A));
  }
  @Override public LQ factor(  DenseMatrix A){
    if (Q.numRows() != A.numRows())     throw new IllegalArgumentException("Q.numRows() != A.numRows()");
 else     if (Q.numColumns() != A.numColumns())     throw new IllegalArgumentException("Q.numColumns() != A.numColumns()");
 else     if (L == null)     throw new IllegalArgumentException("L == null");
    intW info=new intW(0);
    LAPACK.getInstance().dgelqf(m,n,A.getData(),Matrices.ld(m),tau,work,work.length,info);
    if (info.val < 0)     throw new IllegalArgumentException();
    L.zero();
    for (    MatrixEntry e : A)     if (e.row() >= e.column())     L.set(e.row(),e.column(),e.get());
    info.val=0;
    LAPACK.getInstance().dorglq(m,n,k,A.getData(),Matrices.ld(m),tau,workGen,workGen.length,info);
    if (info.val < 0)     throw new IllegalArgumentException();
    Q.set(A);
    return this;
  }
  /** 
 * Returns the lower triangular factor
 */
  public LowerTriangDenseMatrix getL(){
    return L;
  }
}