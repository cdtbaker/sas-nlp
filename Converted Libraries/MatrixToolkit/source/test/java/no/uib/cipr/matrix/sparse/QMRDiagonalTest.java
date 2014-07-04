package no.uib.cipr.matrix.sparse;
import no.uib.cipr.matrix.sparse.DiagonalPreconditioner;
/** 
 * Test QMR with diagonal preconditioning
 */
public class QMRDiagonalTest extends QMRTest {
  public QMRDiagonalTest(  String arg0){
    super(arg0);
  }
  @Override protected void createSolver() throws Exception {
    super.createSolver();
    M=new DiagonalPreconditioner(A.numRows());
  }
}
