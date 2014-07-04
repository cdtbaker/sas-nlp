package no.uib.cipr.matrix;
import no.uib.cipr.matrix.DenseMatrix;
import no.uib.cipr.matrix.Matrix;
import no.uib.cipr.matrix.NotConvergedException;
import no.uib.cipr.matrix.SVD;
import no.uib.cipr.matrix.TridiagMatrix;
import junit.framework.TestCase;
/** 
 * Test the singular value solver
 */
public class SingularvalueTest extends TestCase {
  /** 
 * Matrix to decompose
 */
  private DenseMatrix A;
  /** 
 * Maximum matrix size, to avoid too slow tests
 */
  private final int max=100;
  public SingularvalueTest(  String arg0){
    super(arg0);
  }
  @Override protected void setUp() throws Exception {
    int n=Utilities.getInt(1,max);
    A=new DenseMatrix(n,n);
  }
  @Override protected void tearDown() throws Exception {
    A=null;
  }
  public void testStaticFactorize() throws NotConvergedException {
    assertEquals(A,SVD.factorize(A));
  }
  public void testFactor() throws NotConvergedException {
    SVD svd=new SVD(A.numRows(),A.numColumns());
    assertEquals(A,svd.factor(A.copy()));
  }
  private void assertEquals(  Matrix A,  SVD svd){
    TridiagMatrix S=new TridiagMatrix(svd.getS().length);
    System.arraycopy(svd.getS(),0,S.getDiagonal(),0,svd.getS().length);
    DenseMatrix U=svd.getU();
    DenseMatrix Vt=svd.getVt();
    Matrix s=U.mult(S.mult(Vt,new DenseMatrix(S.numRows(),Vt.numColumns())),new DenseMatrix(A.numRows(),A.numColumns()));
    for (int i=0; i < A.numRows(); ++i)     for (int j=0; j < A.numColumns(); ++j)     assertEquals(A.get(i,j),s.get(i,j),1e-12);
  }
}
