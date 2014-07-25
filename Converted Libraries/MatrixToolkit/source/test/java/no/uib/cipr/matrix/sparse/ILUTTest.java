package no.uib.cipr.matrix.sparse;
import no.uib.cipr.matrix.Matrix;
import no.uib.cipr.matrix.Vector;
import no.uib.cipr.matrix.sparse.CompRowMatrix;
import no.uib.cipr.matrix.sparse.ILU;
/** 
 * Test of ILU with thresholding
 */
public class ILUTTest extends IncompleteFactorizationTestAbstract {
  @Override void testFactorization(  Matrix A,  Vector x){
    Vector b=A.mult(x,x.copy());
    ILU ilut=new ILU(new CompRowMatrix(A));
    ilut.setMatrix(A);
    ilut.apply(b,x);
    Vector r=A.multAdd(-1,x,b.copy());
    assertEquals(0,r.norm(Vector.Norm.TwoRobust),1e-5);
  }
}
