package no.uib.cipr.matrix.sparse;
import no.uib.cipr.matrix.sparse.CompRowMatrix;
import no.uib.cipr.matrix.sparse.ILU;
/** 
 * Test of CGS with ILU
 */
public class CGSILUTest extends CGSTest {
  public CGSILUTest(  String arg0){
    super(arg0);
  }
  @Override protected void createSolver() throws Exception {
    super.createSolver();
    M=new ILU(new CompRowMatrix(A));
  }
}
