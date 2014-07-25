package org.apache.commons.math3.linear;
import java.util.Random;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.junit.Test;
import org.junit.Assert;
public class EigenSolverTest {
  /** 
 * test non invertible matrix 
 */
  @Test public void testNonInvertible(){
    Random r=new Random(9994100315209l);
    RealMatrix m=EigenDecompositionTest.createTestMatrix(r,new double[]{1.0,0.0,-1.0,-2.0,-3.0});
    DecompositionSolver es=new EigenDecomposition(m).getSolver();
    Assert.assertFalse(es.isNonSingular());
    try {
      es.getInverse();
      Assert.fail("an exception should have been thrown");
    }
 catch (    SingularMatrixException ime) {
    }
  }
  /** 
 * test invertible matrix 
 */
  @Test public void testInvertible(){
    Random r=new Random(9994100315209l);
    RealMatrix m=EigenDecompositionTest.createTestMatrix(r,new double[]{1.0,0.5,-1.0,-2.0,-3.0});
    DecompositionSolver es=new EigenDecomposition(m).getSolver();
    Assert.assertTrue(es.isNonSingular());
    RealMatrix inverse=es.getInverse();
    RealMatrix error=m.multiply(inverse).subtract(MatrixUtils.createRealIdentityMatrix(m.getRowDimension()));
    Assert.assertEquals(0,error.getNorm(),4.0e-15);
  }
  /** 
 * test solve dimension errors 
 */
  @Test public void testSolveDimensionErrors(){
    final double[] refValues=new double[]{2.003,2.002,2.001,1.001,1.000,0.001};
    final RealMatrix matrix=EigenDecompositionTest.createTestMatrix(new Random(35992629946426l),refValues);
    DecompositionSolver es=new EigenDecomposition(matrix).getSolver();
    RealMatrix b=MatrixUtils.createRealMatrix(new double[2][2]);
    try {
      es.solve(b);
      Assert.fail("an exception should have been thrown");
    }
 catch (    MathIllegalArgumentException iae) {
    }
    try {
      es.solve(b.getColumnVector(0));
      Assert.fail("an exception should have been thrown");
    }
 catch (    MathIllegalArgumentException iae) {
    }
    try {
      es.solve(new ArrayRealVectorTest.RealVectorTestImpl(b.getColumn(0)));
      Assert.fail("an exception should have been thrown");
    }
 catch (    MathIllegalArgumentException iae) {
    }
  }
  /** 
 * test solve 
 */
  @Test public void testSolve(){
    RealMatrix m=MatrixUtils.createRealMatrix(new double[][]{{91,5,29,32,40,14},{5,34,-1,0,2,-1},{29,-1,12,9,21,8},{32,0,9,14,9,0},{40,2,21,9,51,19},{14,-1,8,0,19,14}});
    DecompositionSolver es=new EigenDecomposition(m).getSolver();
    RealMatrix b=MatrixUtils.createRealMatrix(new double[][]{{1561,269,188},{69,-21,70},{739,108,63},{324,86,59},{1624,194,107},{796,69,36}});
    RealMatrix xRef=MatrixUtils.createRealMatrix(new double[][]{{1,2,1},{2,-1,2},{4,2,3},{8,-1,0},{16,2,0},{32,-1,0}});
    RealMatrix solution=es.solve(b);
    Assert.assertEquals(0,solution.subtract(xRef).getNorm(),2.5e-12);
    for (int i=0; i < b.getColumnDimension(); ++i) {
      Assert.assertEquals(0,es.solve(b.getColumnVector(i)).subtract(xRef.getColumnVector(i)).getNorm(),2.0e-11);
    }
    for (int i=0; i < b.getColumnDimension(); ++i) {
      ArrayRealVectorTest.RealVectorTestImpl v=new ArrayRealVectorTest.RealVectorTestImpl(b.getColumn(i));
      Assert.assertEquals(0,es.solve(v).subtract(xRef.getColumnVector(i)).getNorm(),2.0e-11);
    }
  }
}
