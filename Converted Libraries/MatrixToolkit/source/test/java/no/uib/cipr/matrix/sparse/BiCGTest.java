package no.uib.cipr.matrix.sparse;
import no.uib.cipr.matrix.sparse.BiCG;
/** 
 * Test of BiCG
 */
public class BiCGTest extends IterativeSolverTestAbstract {
  public BiCGTest(  String arg0){
    super(arg0);
  }
  @Override protected void createSolver() throws Exception {
    solver=new BiCG(x);
  }
}
