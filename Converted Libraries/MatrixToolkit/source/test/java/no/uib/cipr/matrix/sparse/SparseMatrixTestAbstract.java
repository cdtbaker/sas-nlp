package no.uib.cipr.matrix.sparse;
import no.uib.cipr.matrix.MatrixTestAbstract;
/** 
 * Test of sparse matrices
 */
public abstract class SparseMatrixTestAbstract extends MatrixTestAbstract {
  protected int bmax=100, tmax=10;
  public SparseMatrixTestAbstract(  String arg0){
    super(arg0);
  }
  @Override public void testMatrixSolve(){
  }
  @Override public void testTransMatrixSolve(){
  }
  @Override public void testTransVectorSolve(){
  }
  @Override public void testVectorSolve(){
  }
}
