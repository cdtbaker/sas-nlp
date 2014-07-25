package no.uib.cipr.matrix.sparse;
import no.uib.cipr.matrix.sparse.CompRowMatrix;
import no.uib.cipr.matrix.sparse.ILU;
/** 
 * Test of GMRES with ILU
 */
public class GMRESILUTest extends GMRESTest {
  public GMRESILUTest(  String arg0){
    super(arg0);
  }
  @Override protected void createSolver() throws Exception {
    super.createSolver();
    M=new ILU(new CompRowMatrix(A));
  }
}
