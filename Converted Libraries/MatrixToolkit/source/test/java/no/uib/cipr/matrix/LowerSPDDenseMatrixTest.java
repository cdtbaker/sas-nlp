package no.uib.cipr.matrix;
import no.uib.cipr.matrix.LowerSPDDenseMatrix;
/** 
 * Test of LowerSPDDenseMatrix
 */
public class LowerSPDDenseMatrixTest extends SymmetricMatrixTestAbstract {
  public LowerSPDDenseMatrixTest(  String arg0){
    super(arg0);
  }
  @Override protected void createPrimary() throws Exception {
    int n=Utilities.getInt(1,max);
    A=new LowerSPDDenseMatrix(n);
    Ad=Utilities.populate(A);
    Utilities.lowerSymmetrice(Ad);
  }
}
