package no.uib.cipr.matrix.sparse;
import no.uib.cipr.matrix.sparse.CGS;
/** 
 * Test of CGS
 */
public class CGSTest extends IterativeSolverTestAbstract {
  public CGSTest(  String arg0){
    super(arg0);
  }
  @Override protected void createSolver() throws Exception {
    solver=new CGS(x);
  }
}
