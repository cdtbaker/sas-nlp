package no.uib.cipr.matrix;
import no.uib.cipr.matrix.UpperSymmDenseMatrix;
/** 
 * Test of UpperSymmDenseMatrix
 */
public class UpperSymmDenseMatrixTest extends SymmetricMatrixTestAbstract {
  public UpperSymmDenseMatrixTest(  String arg0){
    super(arg0);
  }
  @Override protected void createPrimary() throws Exception {
    int n=Utilities.getInt(1,max);
    A=new UpperSymmDenseMatrix(n);
    Ad=Utilities.populate(A);
    Utilities.upperSymmetrice(Ad);
  }
}
