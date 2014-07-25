package no.uib.cipr.matrix.sparse;
import no.uib.cipr.matrix.StructImmutableMatrixTestAbstract;
/** 
 * Test of sparse matrices whose sparsity structure is immutable
 */
public abstract class SparseStructImmutableMatrixTestAbstract extends StructImmutableMatrixTestAbstract {
  protected int bmax=100, tmax=10;
  public SparseStructImmutableMatrixTestAbstract(  String arg0){
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
