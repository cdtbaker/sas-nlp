package no.uib.cipr.matrix.sparse;
import no.uib.cipr.matrix.sparse.FlexCompRowMatrix;
import no.uib.cipr.matrix.sparse.ILUT;
/** 
 * Test of GMRES with ILUT
 */
public class GMRESILUTTest extends GMRESTest {
  public GMRESILUTTest(  String arg0){
    super(arg0);
  }
  @Override protected void createSolver() throws Exception {
    super.createSolver();
    M=new ILUT(new FlexCompRowMatrix(A));
  }
}
