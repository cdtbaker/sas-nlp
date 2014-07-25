package no.uib.cipr.matrix.sparse;
import no.uib.cipr.matrix.sparse.AMG;
/** 
 * Test of Chebyshev with AMG
 */
public class ChebyshevAMGTest extends ChebyshevTest {
  public ChebyshevAMGTest(  String arg0){
    super(arg0);
  }
  @Override protected void createSolver() throws Exception {
    super.createSolver();
    M=new AMG();
  }
}
