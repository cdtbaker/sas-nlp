package no.uib.cipr.matrix;
import no.uib.cipr.matrix.UpperTriangBandMatrix;
/** 
 * Test of UpperTriangBandMatrix
 */
public class UpperTriangBandMatrixTest extends TriangMatrixTestAbstract {
  public UpperTriangBandMatrixTest(  String arg0){
    super(arg0);
  }
  @Override protected void createPrimary() throws Exception {
    int n=Utilities.getInt(1,max);
    int kd=Math.min(Utilities.getInt(max),Math.max(n - 1,0));
    A=new UpperTriangBandMatrix(n,kd);
    Ad=Utilities.bandPopulate(A,0,kd);
    Utilities.addDiagonal(A,Ad,1);
  }
}
