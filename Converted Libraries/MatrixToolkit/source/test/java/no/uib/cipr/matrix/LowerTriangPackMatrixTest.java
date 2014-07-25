package no.uib.cipr.matrix;
import no.uib.cipr.matrix.LowerTriangPackMatrix;
/** 
 * Test of LowerTriangPackMatrix
 */
public class LowerTriangPackMatrixTest extends TriangMatrixTestAbstract {
  public LowerTriangPackMatrixTest(  String arg0){
    super(arg0);
  }
  @Override protected void createPrimary() throws Exception {
    int n=Utilities.getInt(1,max);
    A=new LowerTriangPackMatrix(n);
    Ad=Utilities.lowerPopulate(A);
    Utilities.addDiagonal(A,Ad,1);
  }
}
