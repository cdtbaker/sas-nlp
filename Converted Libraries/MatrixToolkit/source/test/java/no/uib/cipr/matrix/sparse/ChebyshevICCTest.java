package no.uib.cipr.matrix.sparse;
import no.uib.cipr.matrix.sparse.CompRowMatrix;
import no.uib.cipr.matrix.sparse.ICC;
/** 
 * Test of Chebyshev with ICC
 */
public class ChebyshevICCTest extends ChebyshevTest {
  public ChebyshevICCTest(  String arg0){
    super(arg0);
  }
  @Override protected void createSolver() throws Exception {
    super.createSolver();
    M=new ICC(new CompRowMatrix(A));
  }
}
