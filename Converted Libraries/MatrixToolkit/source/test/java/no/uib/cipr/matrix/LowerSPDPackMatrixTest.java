package no.uib.cipr.matrix;
import no.uib.cipr.matrix.LowerSPDPackMatrix;
/** 
 * Test of LowerSPDPackMatrix
 */
public class LowerSPDPackMatrixTest extends SymmetricMatrixTestAbstract {
  public LowerSPDPackMatrixTest(  String arg0){
    super(arg0);
  }
  @Override protected void createPrimary() throws Exception {
    int n=Utilities.getInt(1,max);
    A=new LowerSPDPackMatrix(n);
    Ad=Utilities.populate(A);
    Utilities.lowerSymmetrice(Ad);
  }
}
