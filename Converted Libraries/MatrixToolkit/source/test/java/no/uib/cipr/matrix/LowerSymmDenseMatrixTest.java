package no.uib.cipr.matrix;
import no.uib.cipr.matrix.LowerSymmDenseMatrix;
/** 
 * Test of LowerSymmDenseMatrix
 */
public class LowerSymmDenseMatrixTest extends SymmetricMatrixTestAbstract {
  public LowerSymmDenseMatrixTest(  String arg0){
    super(arg0);
  }
  @Override protected void createPrimary() throws Exception {
    int n=Utilities.getInt(1,max);
    A=new LowerSymmDenseMatrix(n);
    Ad=Utilities.populate(A);
    Utilities.lowerSymmetrice(Ad);
  }
}
