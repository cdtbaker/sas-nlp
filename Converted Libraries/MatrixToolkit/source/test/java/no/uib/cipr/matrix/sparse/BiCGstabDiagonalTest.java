package no.uib.cipr.matrix.sparse;
import no.uib.cipr.matrix.sparse.DiagonalPreconditioner;
/** 
 * Test of BiCGstab with diagonal preconditioning
 */
public class BiCGstabDiagonalTest extends BiCGstabTest {
  public BiCGstabDiagonalTest(  String arg0){
    super(arg0);
  }
  @Override protected void createSolver() throws Exception {
    super.createSolver();
    M=new DiagonalPreconditioner(A.numRows());
  }
}
