package no.uib.cipr.matrix.sparse;
import no.uib.cipr.matrix.sparse.CompColMatrix;
import no.uib.cipr.matrix.Utilities;
/** 
 * Test of CompColMatrix
 */
public class CompColMatrixTest extends SparseStructImmutableMatrixTestAbstract {
  public CompColMatrixTest(  String arg0){
    super(arg0);
  }
  @Override protected void createPrimary() throws Exception {
    int n=Utilities.getInt(1,max);
    int m=Utilities.getInt(1,max);
    int b=Utilities.getInt(Math.min(bmax,m));
    int[][] nz=Utilities.getColumnPattern(n,m,b);
    A=new CompColMatrix(n,m,nz);
    Ad=Utilities.columnPopulate(A,nz);
  }
}
