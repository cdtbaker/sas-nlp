package no.uib.cipr.matrix.sparse;
import no.uib.cipr.matrix.sparse.CG;
/** 
 * Test of CG
 */
public class CGTest extends SPDIterativeSolverTestAbstract {
  public CGTest(  String arg0){
    super(arg0);
  }
  @Override protected void createSolver() throws Exception {
    solver=new CG(x);
  }
}
