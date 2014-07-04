package no.uib.cipr.matrix.sparse;
import no.uib.cipr.matrix.sparse.DiagonalPreconditioner;
/** 
 * Test of CGS with diagonal preconditioning
 */
public class CGSDiagonalTest extends CGSTest {
  public CGSDiagonalTest(  String arg0){
    super(arg0);
  }
  @Override protected void createSolver() throws Exception {
    super.createSolver();
    M=new DiagonalPreconditioner(A.numRows());
  }
}
