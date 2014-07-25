package no.uib.cipr.matrix.sparse;
import no.uib.cipr.matrix.sparse.CompRowMatrix;
import no.uib.cipr.matrix.sparse.SSOR;
/** 
 * Test of Chebyshev with SSOR preconditioning
 */
public class ChebyshevSSORTest extends ChebyshevTest {
  public ChebyshevSSORTest(  String arg0){
    super(arg0);
  }
  @Override protected void createSolver() throws Exception {
    super.createSolver();
    double omega=Math.random() + 1;
    M=new SSOR(new CompRowMatrix(A),true,omega,omega);
  }
}
