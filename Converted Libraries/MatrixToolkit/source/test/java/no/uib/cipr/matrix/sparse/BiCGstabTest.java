package no.uib.cipr.matrix.sparse;
import no.uib.cipr.matrix.sparse.BiCGstab;
/** 
 * Test of BiCGstab
 */
public class BiCGstabTest extends IterativeSolverTestAbstract {
  public BiCGstabTest(  String arg0){
    super(arg0);
  }
  @Override protected void createSolver() throws Exception {
    solver=new BiCGstab(x);
  }
}
