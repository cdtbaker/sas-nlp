package no.uib.cipr.matrix.sparse;
import no.uib.cipr.matrix.sparse.GMRES;
/** 
 * Test of GMRES
 */
public class GMRESTest extends IterativeSolverTestAbstract {
  public GMRESTest(  String arg0){
    super(arg0);
  }
  @Override protected void createSolver() throws Exception {
    solver=new GMRES(x);
  }
}
