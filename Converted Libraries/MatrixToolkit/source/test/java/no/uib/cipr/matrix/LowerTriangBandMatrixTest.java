package no.uib.cipr.matrix;
import no.uib.cipr.matrix.LowerTriangBandMatrix;
/** 
 * Test of LowerTriangBandMatrix
 */
public class LowerTriangBandMatrixTest extends TriangMatrixTestAbstract {
  public LowerTriangBandMatrixTest(  String arg0){
    super(arg0);
  }
  @Override protected void createPrimary() throws Exception {
    int n=Utilities.getInt(1,max);
    int kd=Math.min(Utilities.getInt(max),Math.max(n - 1,0));
    A=new LowerTriangBandMatrix(n,kd);
    Ad=Utilities.bandPopulate(A,kd,0);
    Utilities.addDiagonal(A,Ad,1);
  }
}
