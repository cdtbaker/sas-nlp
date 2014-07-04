package no.uib.cipr.matrix;
import no.uib.cipr.matrix.BandLU;
import no.uib.cipr.matrix.BandMatrix;
import no.uib.cipr.matrix.DenseMatrix;
import no.uib.cipr.matrix.Matrices;
import no.uib.cipr.matrix.Matrix;
import junit.framework.TestCase;
/** 
 * Tests the banded LU decomposition
 */
public class BandLUTest extends TestCase {
  private BandMatrix A;
  private DenseMatrix I;
  private int kl, ku;
  private final int max=100, bmax=10;
  public BandLUTest(  String arg0){
    super(arg0);
  }
  @Override protected void setUp() throws Exception {
    int n=Utilities.getInt(1,max);
    kl=Math.min(n,Utilities.getInt(bmax));
    ku=Math.min(n,Utilities.getInt(bmax));
    A=new BandMatrix(n,kl,kl + ku);
    Utilities.bandPopulate(A,kl,ku);
    Utilities.addDiagonal(A,1);
    while (Utilities.singular(A))     Utilities.addDiagonal(A,1);
    I=Matrices.identity(n);
  }
  @Override protected void tearDown() throws Exception {
    A=null;
    I=null;
  }
  public void testBandLU(){
    int n=A.numRows();
    BandLU lu=new BandLU(n,kl,ku);
    lu.factor(A.copy());
    lu.solve(I);
    Matrix J=I.mult(A,new DenseMatrix(n,n));
    for (int i=0; i < n; ++i)     for (int j=0; j < n; ++j)     if (i != j)     assertEquals(J.get(i,j),0,1e-10);
 else     assertEquals(J.get(i,j),1,1e-10);
  }
  public void testBandLUtranspose(){
    int n=A.numRows();
    BandLU lu=new BandLU(n,kl,ku);
    lu.factor(A.copy());
    lu.transSolve(I);
    Matrix J=I.transAmult(A,new DenseMatrix(n,n));
    for (int i=0; i < n; ++i)     for (int j=0; j < n; ++j)     if (i != j)     assertEquals(J.get(i,j),0,1e-10);
 else     assertEquals(J.get(i,j),1,1e-10);
  }
  public void testBandLUrcond(){
    int n=A.numRows();
    BandLU lu=new BandLU(n,kl,ku);
    lu.factor(A.copy());
    lu.rcond(A,Matrix.Norm.One);
    lu.rcond(A,Matrix.Norm.Infinity);
  }
}
