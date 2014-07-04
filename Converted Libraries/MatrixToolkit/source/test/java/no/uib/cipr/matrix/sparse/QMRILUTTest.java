package no.uib.cipr.matrix.sparse;
import no.uib.cipr.matrix.sparse.FlexCompRowMatrix;
import no.uib.cipr.matrix.sparse.ILUT;
/** 
 * Test of QMR with ILUT
 */
public class QMRILUTTest extends QMRTest {
  public QMRILUTTest(  String arg0){
    super(arg0);
  }
  @Override protected void createSolver() throws Exception {
    super.createSolver();
    M=new ILUT(new FlexCompRowMatrix(A));
  }
}
