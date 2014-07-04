package no.uib.cipr.matrix;
import no.uib.cipr.matrix.UpperSPDDenseMatrix;
/** 
 * Test of UpperSPDDenseMatrix
 */
public class UpperSPDDenseMatrixTest extends SymmetricMatrixTestAbstract {
  public UpperSPDDenseMatrixTest(  String arg0){
    super(arg0);
  }
  @Override protected void createPrimary() throws Exception {
    int n=Utilities.getInt(1,max);
    A=new UpperSPDDenseMatrix(n);
    Ad=Utilities.populate(A);
    Utilities.upperSymmetrice(Ad);
  }
}
