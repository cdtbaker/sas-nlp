package no.uib.cipr.matrix;
import no.uib.cipr.matrix.Matrix;
import no.uib.cipr.matrix.MatrixEntry;
/** 
 * Test of unit, triangular matrices
 */
public abstract class UnitTriangMatrixTestAbstract extends TriangMatrixTestAbstract {
  public UnitTriangMatrixTestAbstract(  String arg0){
    super(arg0);
  }
  public void testAddDiagonal(){
  }
  public void testAddOneDiagonal(){
  }
  public void testAddZeroDiagonal(){
  }
  @Override public void testIteratorSet(){
    double alpha=Math.random();
    for (    MatrixEntry e : A)     if (e.row() != e.column())     e.set(e.get() * alpha);
    assertEquals(Utilities.unitSet(scale(alpha)),A);
  }
  @Override public void testIteratorSetGet(){
  }
  @Override public void testScale(){
  }
  @Override public void testZero(){
  }
  @Override public void testZeroScale(){
  }
  /** 
 * We can't zero, so we do without
 */
  @Override public void testCopy(){
    Matrix Ac=A.copy();
    assertEquals(Ad,Ac);
  }
  @Override public void testAdd(){
    double alpha=Math.random();
    for (    MatrixEntry e : A)     if (e.row() != e.column()) {
      A.add(e.row(),e.column(),alpha);
      A.add(e.row(),e.column(),-alpha);
    }
    assertEquals(Ad,A);
  }
}
