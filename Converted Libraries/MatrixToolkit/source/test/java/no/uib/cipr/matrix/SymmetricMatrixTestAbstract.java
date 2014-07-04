package no.uib.cipr.matrix;
/** 
 * Test of symmetrical matrices
 */
public abstract class SymmetricMatrixTestAbstract extends MatrixTestAbstract {
  public SymmetricMatrixTestAbstract(  String arg0){
    super(arg0);
  }
  @Override public void testMatrixAdd(){
  }
  @Override public void testMatrixSet(){
  }
  @Override public void testOneMatrixAdd(){
  }
  @Override public void testOneMatrixSet(){
  }
  @Override public void testRandomMatrixAdd(){
  }
  @Override public void testRandomMatrixSet(){
  }
  @Override public void testVectorRank1(){
    if (A.isSquare()) {
      double alpha=Math.random();
      assertEquals(rank1(alpha,xdR,xdR),A.rank1(alpha,xR,xR));
    }
  }
  @Override public void testVectorRank1Dense(){
    if (A.isSquare()) {
      double alpha=Math.random();
      assertEquals(rank1(alpha,xdR,xdR),A.rank1(alpha,xDenseR,xDenseR));
    }
  }
}
