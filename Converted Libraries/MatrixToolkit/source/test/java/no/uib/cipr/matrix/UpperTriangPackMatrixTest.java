package no.uib.cipr.matrix;
import no.uib.cipr.matrix.UpperTriangPackMatrix;
/** 
 * Test of UpperTriangPackMatrix
 */
public class UpperTriangPackMatrixTest extends TriangMatrixTestAbstract {
  public UpperTriangPackMatrixTest(  String arg0){
    super(arg0);
  }
  @Override protected void createPrimary() throws Exception {
    int n=Utilities.getInt(1,max);
    A=new UpperTriangPackMatrix(n);
    Ad=Utilities.upperPopulate(A);
    Utilities.addDiagonal(A,Ad,1);
  }
}
