package no.uib.cipr.matrix;
import no.uib.cipr.matrix.UpperSymmPackMatrix;
/** 
 * Test of UpperSymmPackMatrixTest
 */
public class UpperSymmPackMatrixTest extends SymmetricMatrixTestAbstract {
  public UpperSymmPackMatrixTest(  String arg0){
    super(arg0);
  }
  @Override protected void createPrimary() throws Exception {
    int n=Utilities.getInt(1,max);
    A=new UpperSymmPackMatrix(n);
    Ad=Utilities.populate(A);
    Utilities.upperSymmetrice(Ad);
  }
}
