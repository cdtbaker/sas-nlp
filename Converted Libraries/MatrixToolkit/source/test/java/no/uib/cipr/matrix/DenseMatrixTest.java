package no.uib.cipr.matrix;
import no.uib.cipr.matrix.DenseMatrix;
/** 
 * Test of a dense matrix
 */
public class DenseMatrixTest extends MatrixTestAbstract {
  public DenseMatrixTest(  String arg0){
    super(arg0);
  }
  @Override protected void createPrimary() throws Exception {
    int n=Utilities.getInt(1,max);
    int m=Utilities.getInt(1,max);
    A=new DenseMatrix(n,m);
    Ad=Utilities.populate(A);
  }
  @Override public void testMatrixSolve(){
    if (A.isSquare())     super.testMatrixSolve();
  }
  @Override public void testTransMatrixSolve(){
    if (A.isSquare())     super.testTransMatrixSolve();
  }
  @Override public void testTransVectorSolve(){
    if (A.isSquare())     super.testTransVectorSolve();
  }
  @Override public void testVectorSolve(){
    if (A.isSquare())     super.testVectorSolve();
  }
  public void testIssue13(){
    Vector bv=Matrices.random(100);
    Matrix am=Matrices.random(100,50);
    Vector xv=new DenseVector(am.numColumns());
    for (int x=0; x < am.numColumns(); x++) {
      xv.set(x,1);
    }
    xv=Matrices.random(xv.size());
    xv=am.solve(bv,xv);
  }
  public void testIssue32(){
    boolean exceptionThrown=false;
    try {
      Matrix m=new DenseMatrix(Integer.MAX_VALUE,2);
    }
 catch (    IllegalArgumentException e) {
      exceptionThrown=true;
    }
 finally {
      assertTrue(exceptionThrown);
    }
    exceptionThrown=false;
    try {
      Matrix m=new DenseMatrix(Integer.MAX_VALUE,3);
    }
 catch (    IllegalArgumentException e) {
      exceptionThrown=true;
    }
 finally {
      assertTrue(exceptionThrown);
    }
    exceptionThrown=false;
    try {
      Matrix m=new DenseMatrix(Integer.MAX_VALUE - 10,3);
    }
 catch (    IllegalArgumentException e) {
      exceptionThrown=true;
    }
 finally {
      assertTrue(exceptionThrown);
    }
  }
}
