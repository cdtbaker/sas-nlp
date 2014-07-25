package no.uib.cipr.matrix;
import no.uib.cipr.matrix.DenseMatrix;
import no.uib.cipr.matrix.Matrices;
import no.uib.cipr.matrix.Matrix;
import junit.framework.TestCase;
/** 
 * Tests the symmetric eigenvalue computers
 */
public abstract class SymmEigenvalueTestAbstract extends TestCase {
  /** 
 * Initial work-matrix
 */
  protected Matrix A;
  /** 
 * Maximum matrix size, to avoid too slow tests
 */
  private final int max=100;
  public SymmEigenvalueTestAbstract(  String arg0){
    super(arg0);
  }
  @Override protected void setUp() throws Exception {
    int n=Utilities.getInt(max);
    A=Matrices.random(n,n);
  }
  @Override protected void tearDown() throws Exception {
    A=null;
  }
  protected void assertEquals(  Matrix A,  double[] w,  DenseMatrix Z){
    Matrix left=A.mult(Z,new DenseMatrix(A.numRows(),A.numColumns()));
    Matrix right=new DenseMatrix(Z);
    for (int i=0; i < w.length; ++i)     for (int j=0; j < w.length; ++j)     right.set(i,j,w[j] * right.get(i,j));
    for (int i=0; i < A.numRows(); ++i)     for (int j=0; j < A.numColumns(); ++j)     assertEquals(left.get(i,j),right.get(i,j),1e-12);
  }
}
