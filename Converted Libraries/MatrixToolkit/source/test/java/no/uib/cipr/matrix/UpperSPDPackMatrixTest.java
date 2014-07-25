package no.uib.cipr.matrix;
import no.uib.cipr.matrix.UpperSPDPackMatrix;
/** 
 * Test of UpperSPDPackMatrix
 */
public class UpperSPDPackMatrixTest extends SymmetricMatrixTestAbstract {
  public UpperSPDPackMatrixTest(  String arg0){
    super(arg0);
  }
  @Override protected void createPrimary() throws Exception {
    int n=Utilities.getInt(1,max);
    A=new UpperSPDPackMatrix(n);
    Ad=Utilities.populate(A);
    Utilities.upperSymmetrice(Ad);
  }
}
