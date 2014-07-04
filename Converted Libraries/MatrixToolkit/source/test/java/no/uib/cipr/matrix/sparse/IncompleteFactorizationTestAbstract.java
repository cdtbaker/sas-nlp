package no.uib.cipr.matrix.sparse;
import no.uib.cipr.matrix.DenseMatrix;
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Matrix;
import no.uib.cipr.matrix.Vector;
import no.uib.cipr.matrix.Utilities;
import junit.framework.TestCase;
/** 
 * Test of incomplete factorizations
 */
public abstract class IncompleteFactorizationTestAbstract extends TestCase {
  @Override protected void setUp() throws Exception {
    super.setUp();
  }
  public void testTriDiagonal(){
    int n=Utilities.getInt(1,10);
    Matrix A=new DenseMatrix(n,n);
    Vector x=new DenseVector(n);
    for (int i=0; i < n; ++i) {
      A.set(i,i,2);
      x.set(i,1);
    }
    for (int i=0; i < n - 1; ++i) {
      A.set(i,i + 1,-1);
      A.set(i + 1,i,-1);
    }
    testFactorization(A,x);
  }
  public void testPentaDiagonal(){
    int n=Utilities.getInt(1,10);
    Matrix A=new DenseMatrix(n,n);
    Vector x=new DenseVector(n);
    for (int i=0; i < n; ++i) {
      A.set(i,i,4);
      x.set(i,1);
    }
    for (int i=0; i < n - 1; ++i) {
      A.set(i,i + 1,-1);
      A.set(i + 1,i,-1);
    }
    for (int i=0; i < n - 2; ++i) {
      A.set(i,i + 2,-1);
      A.set(i + 2,i,-1);
    }
    testFactorization(A,x);
  }
  abstract void testFactorization(  Matrix A,  Vector x);
}
