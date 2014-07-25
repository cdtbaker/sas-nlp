package no.uib.cipr.matrix.sparse;
import no.uib.cipr.matrix.sparse.DiagonalPreconditioner;
/** 
 * Test of CG with diagonal preconditioning
 */
public class CGDiagonalTest extends CGTest {
  public CGDiagonalTest(  String arg0){
    super(arg0);
  }
  @Override protected void createSolver() throws Exception {
    super.createSolver();
    M=new DiagonalPreconditioner(A.numRows());
  }
}
