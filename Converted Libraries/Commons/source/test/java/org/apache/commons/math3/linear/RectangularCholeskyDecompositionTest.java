package org.apache.commons.math3.linear;
import org.junit.Test;
import org.junit.Assert;
public class RectangularCholeskyDecompositionTest {
  @Test public void testDecomposition3x3(){
    RealMatrix m=MatrixUtils.createRealMatrix(new double[][]{{1,9,9},{9,225,225},{9,225,625}});
    RectangularCholeskyDecomposition d=new RectangularCholeskyDecomposition(m,1.0e-6);
    Assert.assertEquals(0.8,d.getRootMatrix().getEntry(0,2),1.0e-15);
    Assert.assertEquals(25.0,d.getRootMatrix().getEntry(2,0),1.0e-15);
    Assert.assertEquals(0.0,d.getRootMatrix().getEntry(2,2),1.0e-15);
    RealMatrix root=d.getRootMatrix();
    RealMatrix rebuiltM=root.multiply(root.transpose());
    Assert.assertEquals(0.0,m.subtract(rebuiltM).getNorm(),1.0e-15);
  }
  @Test public void testFullRank(){
    RealMatrix base=MatrixUtils.createRealMatrix(new double[][]{{0.1159548705,0.,0.,0.},{0.0896442724,0.1223540781,0.,0.},{0.0852155322,4.558668e-3,0.1083577299,0.},{0.0905486674,0.0213768077,0.0128878333,0.1014155693}});
    RealMatrix m=base.multiply(base.transpose());
    RectangularCholeskyDecomposition d=new RectangularCholeskyDecomposition(m,1.0e-10);
    RealMatrix root=d.getRootMatrix();
    RealMatrix rebuiltM=root.multiply(root.transpose());
    Assert.assertEquals(0.0,m.subtract(rebuiltM).getNorm(),1.0e-15);
    Assert.assertTrue(root.subtract(base).getNorm() > 0.3);
  }
  @Test public void testMath789(){
    final RealMatrix m1=MatrixUtils.createRealMatrix(new double[][]{{0.013445532,0.010394690,0.009881156,0.010499559},{0.010394690,0.023006616,0.008196856,0.010732709},{0.009881156,0.008196856,0.019023866,0.009210099},{0.010499559,0.010732709,0.009210099,0.019107243}});
    composeAndTest(m1,4);
    final RealMatrix m2=MatrixUtils.createRealMatrix(new double[][]{{0.0,0.0,0.0,0.0,0.0},{0.0,0.013445532,0.010394690,0.009881156,0.010499559},{0.0,0.010394690,0.023006616,0.008196856,0.010732709},{0.0,0.009881156,0.008196856,0.019023866,0.009210099},{0.0,0.010499559,0.010732709,0.009210099,0.019107243}});
    composeAndTest(m2,4);
    final RealMatrix m3=MatrixUtils.createRealMatrix(new double[][]{{0.013445532,0.010394690,0.0,0.009881156,0.010499559},{0.010394690,0.023006616,0.0,0.008196856,0.010732709},{0.0,0.0,0.0,0.0,0.0},{0.009881156,0.008196856,0.0,0.019023866,0.009210099},{0.010499559,0.010732709,0.0,0.009210099,0.019107243}});
    composeAndTest(m3,4);
  }
  private void composeAndTest(  RealMatrix m,  int expectedRank){
    RectangularCholeskyDecomposition r=new RectangularCholeskyDecomposition(m);
    Assert.assertEquals(expectedRank,r.getRank());
    RealMatrix root=r.getRootMatrix();
    RealMatrix rebuiltMatrix=root.multiply(root.transpose());
    Assert.assertEquals(0.0,m.subtract(rebuiltMatrix).getNorm(),1.0e-16);
  }
}
