package no.uib.cipr.matrix.sparse;
import no.uib.cipr.matrix.sparse.QMR;
/** 
 * Test of QMR
 */
public class QMRTest extends IterativeSolverTestAbstract {
  public QMRTest(  String arg0){
    super(arg0);
  }
  @Override protected void createSolver() throws Exception {
    solver=new QMR(x);
  }
}
