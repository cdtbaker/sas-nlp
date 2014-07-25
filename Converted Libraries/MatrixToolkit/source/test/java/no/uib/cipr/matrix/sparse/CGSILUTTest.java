package no.uib.cipr.matrix.sparse;
import no.uib.cipr.matrix.sparse.FlexCompRowMatrix;
import no.uib.cipr.matrix.sparse.ILUT;
/** 
 * Test of CGS with ILUT
 */
public class CGSILUTTest extends CGSTest {
  public CGSILUTTest(  String arg0){
    super(arg0);
  }
  @Override protected void createSolver() throws Exception {
    super.createSolver();
    M=new ILUT(new FlexCompRowMatrix(A));
  }
}
