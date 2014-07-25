package no.uib.cipr.matrix;
import junit.framework.TestCase;
import no.uib.cipr.matrix.DenseMatrix;
import no.uib.cipr.matrix.LowerSPDPackMatrix;
import no.uib.cipr.matrix.Matrices;
import no.uib.cipr.matrix.Matrix;
import no.uib.cipr.matrix.PackCholesky;
import no.uib.cipr.matrix.UpperSPDPackMatrix;
/** 
 * Tests the packed Cholesky decomposition
 */
public class PackCholeskyTest extends TestCase {
  private LowerSPDPackMatrix L;
  private UpperSPDPackMatrix U;
  private DenseMatrix I;
  private final int max=50;
  public PackCholeskyTest(  String arg0){
    super(arg0);
  }
  @Override protected void setUp() throws Exception {
    int n=Utilities.getInt(1,max);
    L=new LowerSPDPackMatrix(n);
    Utilities.lowerPopulate(L);
    Utilities.addDiagonal(L,1);
    while (!Utilities.spd(L))     Utilities.addDiagonal(L,1);
    U=new UpperSPDPackMatrix(n);
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
  public void testLowerPackCholesky(){
    int n=L.numRows();
    PackCholesky c=new PackCholesky(n,false);
    c.factor(L.copy());
    c.solve(I);
    Matrix J=I.mult(L,new DenseMatrix(n,n));
    for (int i=0; i < n; ++i)     for (int j=0; j < n; ++j)     if (i != j)     assertEquals(J.get(i,j),0,1e-10);
 else     assertEquals(J.get(i,j),1,1e-10);
  }
  public void testUpperPackCholesky(){
    int n=U.numRows();
    PackCholesky c=new PackCholesky(n,true);
    c.factor(U.copy());
    c.solve(I);
    Matrix J=I.mult(U,new DenseMatrix(n,n));
    for (int i=0; i < n; ++i)     for (int j=0; j < n; ++j)     if (i != j)     assertEquals(J.get(i,j),0,1e-10);
 else     assertEquals(J.get(i,j),1,1e-10);
  }
  public void testLowerPackCholeskyrcond(){
    int n=L.numRows();
    PackCholesky c=new PackCholesky(n,false);
    c.factor(L.copy());
    c.rcond(L);
  }
  public void testUpperPackCholeskyrcond(){
    int n=U.numRows();
    PackCholesky c=new PackCholesky(n,true);
    c.factor(U.copy());
    c.rcond(U);
  }
}
