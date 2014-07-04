package no.uib.cipr.matrix.sparse;
import no.uib.cipr.matrix.sparse.CompRowMatrix;
import no.uib.cipr.matrix.sparse.ICC;
/** 
 * Test of CG with ICC
 */
public class CGICCTest extends CGTest {
  public CGICCTest(  String arg0){
    super(arg0);
  }
  @Override protected void createSolver() throws Exception {
    super.createSolver();
    M=new ICC(new CompRowMatrix(A));
  }
}
