package no.uib.cipr.matrix;
import no.uib.cipr.matrix.Matrices;
import no.uib.cipr.matrix.Matrix;
import junit.framework.TestCase;
/** 
 * Orthogonal matrix decomposition tests
 */
public abstract class OrthogonalTestAbstract extends TestCase {
  /** 
 * Initial work-matrix, and non-square matrices
 */
  protected Matrix A, Ar, Ac;
  /** 
 * Maximum matrix size, to avoid too slow tests
 */
  private final int max=100;
  public OrthogonalTestAbstract(  String arg0){
    super(arg0);
  }
  @Override protected void setUp() throws Exception {
    int n=Utilities.getInt(1,max);
    int m=Utilities.getInt(1,n);
    A=Matrices.random(n,n);
    Ar=Matrices.random(n,m);
    Ac=Matrices.random(m,n);
  }
  @Override protected void tearDown() throws Exception {
    A=Ar=Ac=null;
  }
  protected void assertEquals(  Matrix A,  Matrix B){
    for (int i=0; i < A.numRows(); ++i)     for (int j=0; j < A.numColumns(); ++j)     assertEquals(A.get(i,j),B.get(i,j),1e-12);
  }
}
