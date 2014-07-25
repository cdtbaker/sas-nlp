package no.uib.cipr.matrix;
import no.uib.cipr.matrix.DenseCholesky;
import no.uib.cipr.matrix.DenseMatrix;
import no.uib.cipr.matrix.LowerSPDDenseMatrix;
import no.uib.cipr.matrix.Matrices;
import no.uib.cipr.matrix.Matrix;
import no.uib.cipr.matrix.UpperSPDDenseMatrix;
import junit.framework.TestCase;
/** 
 * Tests the dense Cholesky decomposition
 */
public class DenseCholeskyTest extends TestCase {
  private LowerSPDDenseMatrix L;
  private UpperSPDDenseMatrix U;
  private DenseMatrix I;
  private final int max=50;
  public DenseCholeskyTest(  String arg0){
    super(arg0);
  }
  @Override protected void setUp() throws Exception {
    int n=Utilities.getInt(1,max);
    L=new LowerSPDDenseMatrix(n);
    Utilities.lowerPopulate(L);
    Utilities.addDiagonal(L,1);
    while (!Utilities.spd(L))     Utilities.addDiagonal(L,1);
    U=new UpperSPDDenseMatrix(n);
    Utilities.upperPopulate(U);
    Utilities.addDiagonal(U,1);
    while (!Utilities.spd(U))     Utilities.addDiagonal(U,1);
    I=Matrices.identity(n);
  }
  @Override protected void tearDown() throws Exception {
    L=null;
    U=null;
    I=null;
  }
  public void testLowerDenseCholesky(){
    int n=L.numRows();
    DenseCholesky c=new DenseCholesky(n,false);
    c.factor(L.copy());
    assert I != null;
    c.solve(I);
    Matrix J=I.mult(L,new DenseMatrix(n,n));
    for (int i=0; i < n; ++i)     for (int j=0; j < n; ++j)     if (i != j)     assertEquals(J.get(i,j),0,1e-10);
 else     assertEquals(J.get(i,j),1,1e-10);
  }
  public void testUpperDenseCholesky(){
    int n=U.numRows();
    DenseCholesky c=new DenseCholesky(n,true);
    c.factor(U.copy());
    c.solve(I);
    Matrix J=I.mult(U,new DenseMatrix(n,n));
    for (int i=0; i < n; ++i)     for (int j=0; j < n; ++j)     if (i != j)     assertEquals(J.get(i,j),0,1e-10);
 else     assertEquals(J.get(i,j),1,1e-10);
  }
  public void testLowerDenseCholeskyrcond(){
    int n=L.numRows();
    DenseCholesky c=new DenseCholesky(n,false);
    c.factor(L.copy());
    c.rcond(L);
  }
  public void testUpperDenseCholeskyrcond(){
    int n=U.numRows();
    DenseCholesky c=new DenseCholesky(n,true);
    c.factor(U.copy());
    c.rcond(U);
  }
}
