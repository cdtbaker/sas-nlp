package no.uib.cipr.matrix;
import no.uib.cipr.matrix.LowerSymmPackMatrix;
/** 
 * Test of LowerSymmPackMatrix
 */
public class LowerSymmPackMatrixTest extends SymmetricMatrixTestAbstract {
  public LowerSymmPackMatrixTest(  String arg0){
    super(arg0);
  }
  @Override protected void createPrimary() throws Exception {
    int n=Utilities.getInt(1,max);
    A=new LowerSymmPackMatrix(n);
    Ad=Utilities.populate(A);
    Utilities.lowerSymmetrice(Ad);
  }
}
