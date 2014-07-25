package no.uib.cipr.matrix.sparse;
import no.uib.cipr.matrix.sparse.AMG;
/** 
 * Test of CG with AMG
 */
public class CGAMGTest extends CGTest {
  public CGAMGTest(  String arg0){
    super(arg0);
  }
  @Override protected void createSolver() throws Exception {
    super.createSolver();
    M=new AMG();
  }
}
