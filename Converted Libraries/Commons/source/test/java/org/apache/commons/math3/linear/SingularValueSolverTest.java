package org.apache.commons.math3.linear;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.junit.Assert;
import org.junit.Test;
public class SingularValueSolverTest {
  private double[][] testSquare={{24.0 / 25.0,43.0 / 25.0},{57.0 / 25.0,24.0 / 25.0}};
  private static final double normTolerance=10e-14;
  /** 
 * test solve dimension errors 
 */
  @Test public void testSolveDimensionErrors(){
    DecompositionSolver solver=new SingularValueDecomposition(MatrixUtils.createRealMatrix(testSquare)).getSolver();
    RealMatrix b=MatrixUtils.createRealMatrix(new double[3][2]);
    try {
      solver.solve(b);
      Assert.fail("an exception should have been thrown");
    }
 catch (    MathIllegalArgumentException iae) {
    }
    try {
      solver.solve(b.getColumnVector(0));
      Assert.fail("an exception should have been thrown");
    }
 catch (    MathIllegalArgumentException iae) {
    }
    try {
      solver.solve(new ArrayRealVectorTest.RealVectorTestImpl(b.getColumn(0)));
      Assert.fail("an exception should have been thrown");
    }
 catch (    MathIllegalArgumentException iae) {
    }
  }
  /** 
 * test least square solve 
 */
  @Test public void testLeastSquareSolve(){
    RealMatrix m=MatrixUtils.createRealMatrix(new double[][]{{1.0,0.0},{0.0,0.0}});
    DecompositionSolver solver=new SingularValueDecomposition(m).getSolver();
    RealMatrix b=MatrixUtils.createRealMatrix(new double[][]{{11,12},{21,22}});
    RealMatrix xMatrix=solver.solve(b);
    Assert.assertEquals(11,xMatrix.getEntry(0,0),1.0e-15);
    Assert.assertEquals(12,xMatrix.getEntry(0,1),1.0e-15);
    Assert.assertEquals(0,xMatrix.getEntry(1,0),1.0e-15);
    Assert.assertEquals(0,xMatrix.getEntry(1,1),1.0e-15);
    RealVector xColVec=solver.solve(b.getColumnVector(0));
    Assert.assertEquals(11,xColVec.getEntry(0),1.0e-15);
    Assert.assertEquals(0,xColVec.getEntry(1),1.0e-15);
    RealVector xColOtherVec=solver.solve(new ArrayRealVectorTest.RealVectorTestImpl(b.getColumn(0)));
    Assert.assertEquals(11,xColOtherVec.getEntry(0),1.0e-15);
    Assert.assertEquals(0,xColOtherVec.getEntry(1),1.0e-15);
  }
  /** 
 * test solve 
 */
  @Test public void testSolve(){
    DecompositionSolver solver=new SingularValueDecomposition(MatrixUtils.createRealMatrix(testSquare)).getSolver();
    RealMatrix b=MatrixUtils.createRealMatrix(new double[][]{{1,2,3},{0,-5,1}});
    RealMatrix xRef=MatrixUtils.createRealMatrix(new double[][]{{-8.0 / 25.0,-263.0 / 75.0,-29.0 / 75.0},{19.0 / 25.0,78.0 / 25.0,49.0 / 25.0}});
    Assert.assertEquals(0,solver.solve(b).subtract(xRef).getNorm(),normTolerance);
    for (int i=0; i < b.getColumnDimension(); ++i) {
      Assert.assertEquals(0,solver.solve(b.getColumnVector(i)).subtract(xRef.getColumnVector(i)).getNorm(),1.0e-13);
    }
    for (int i=0; i < b.getColumnDimension(); ++i) {
      ArrayRealVectorTest.RealVectorTestImpl v=new ArrayRealVectorTest.RealVectorTestImpl(b.getColumn(i));
      Assert.assertEquals(0,solver.solve(v).subtract(xRef.getColumnVector(i)).getNorm(),1.0e-13);
    }
  }
  /** 
 * test condition number 
 */
  @Test public void testConditionNumber(){
    SingularValueDecomposition svd=new SingularValueDecomposition(MatrixUtils.createRealMatrix(testSquare));
    Assert.assertEquals(3.0,svd.getConditionNumber(),1.5e-15);
  }
  @Test public void testMath320B(){
    RealMatrix rm=new Array2DRowRealMatrix(new double[][]{{1.0,2.0},{1.0,2.0}});
    SingularValueDecomposition svd=new SingularValueDecomposition(rm);
    RealMatrix recomposed=svd.getU().multiply(svd.getS()).multiply(svd.getVT());
    Assert.assertEquals(0.0,recomposed.subtract(rm).getNorm(),2.0e-15);
  }
}
