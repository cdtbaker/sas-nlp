package no.uib.cipr.matrix.sparse;
import no.uib.cipr.matrix.sparse.DiagonalPreconditioner;
/** 
 * Test of BiCG with diagonal preconditioning
 */
public class BiCGDiagonalTest extends BiCGTest {
  public BiCGDiagonalTest(  String arg0){
    super(arg0);
  }
  @Override protected void createSolver() throws Exception {
    super.createSolver();
    M=new DiagonalPreconditioner(A.numRows());
  }
}
