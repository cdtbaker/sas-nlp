package no.uib.cipr.matrix.sparse;
import no.uib.cipr.matrix.sparse.CompDiagMatrix;
import no.uib.cipr.matrix.Utilities;
/** 
 * Test of CompDiagMatrix
 */
public class CompDiagMatrixTest extends SparseMatrixTestAbstract {
  public CompDiagMatrixTest(  String arg0){
    super(arg0);
  }
  @Override protected void createPrimary() throws Exception {
    int n=Utilities.getInt(1,max);
    int m=Utilities.getInt(1,max);
    int b=Utilities.getInt(Math.min(bmax,m));
    A=new CompDiagMatrix(n,m);
    Ad=Utilities.rowPopulate(A,b);
  }
}
