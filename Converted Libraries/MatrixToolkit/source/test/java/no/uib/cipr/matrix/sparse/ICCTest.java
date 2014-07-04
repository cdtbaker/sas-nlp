package no.uib.cipr.matrix.sparse;
import no.uib.cipr.matrix.Matrix;
import no.uib.cipr.matrix.Vector;
import no.uib.cipr.matrix.sparse.CompRowMatrix;
import no.uib.cipr.matrix.sparse.ICC;
/** 
 * Test of ICC(0)
 */
public class ICCTest extends IncompleteFactorizationTestAbstract {
  @Override void testFactorization(  Matrix A,  Vector x){
    Vector b=A.mult(x,x.copy());
    ICC icc=new ICC(new CompRowMatrix(A));
    icc.setMatrix(A);
    icc.apply(b,x);
    Vector r=A.multAdd(-1,x,b.copy());
    assertEquals(0,r.norm(Vector.Norm.TwoRobust),1e-5);
  }
}
