package org.apache.commons.math3.linear;
import org.junit.Test;
import org.junit.Assert;
import org.apache.commons.math3.TestUtils;
import org.apache.commons.math3.fraction.Fraction;
import org.apache.commons.math3.fraction.FractionField;
public class FieldLUDecompositionTest {
  private Fraction[][] testData={{new Fraction(1),new Fraction(2),new Fraction(3)},{new Fraction(2),new Fraction(5),new Fraction(3)},{new Fraction(1),new Fraction(0),new Fraction(8)}};
  private Fraction[][] testDataMinus={{new Fraction(-1),new Fraction(-2),new Fraction(-3)},{new Fraction(-2),new Fraction(-5),new Fraction(-3)},{new Fraction(-1),new Fraction(0),new Fraction(-8)}};
  private Fraction[][] luData={{new Fraction(2),new Fraction(3),new Fraction(3)},{new Fraction(2),new Fraction(3),new Fraction(7)},{new Fraction(6),new Fraction(6),new Fraction(8)}};
  private Fraction[][] singular={{new Fraction(2),new Fraction(3)},{new Fraction(2),new Fraction(3)}};
  private Fraction[][] bigSingular={{new Fraction(1),new Fraction(2),new Fraction(3),new Fraction(4)},{new Fraction(2),new Fraction(5),new Fraction(3),new Fraction(4)},{new Fraction(7),new Fraction(3),new Fraction(256),new Fraction(1930)},{new Fraction(3),new Fraction(7),new Fraction(6),new Fraction(8)}};
  /** 
 * test dimensions 
 */
  @Test public void testDimensions(){
    FieldMatrix<Fraction> matrix=new Array2DRowFieldMatrix<Fraction>(FractionField.getInstance(),testData);
    FieldLUDecomposition<Fraction> LU=new FieldLUDecomposition<Fraction>(matrix);
    Assert.assertEquals(testData.length,LU.getL().getRowDimension());
    Assert.assertEquals(testData.length,LU.getL().getColumnDimension());
    Assert.assertEquals(testData.length,LU.getU().getRowDimension());
    Assert.assertEquals(testData.length,LU.getU().getColumnDimension());
    Assert.assertEquals(testData.length,LU.getP().getRowDimension());
    Assert.assertEquals(testData.length,LU.getP().getColumnDimension());
  }
  /** 
 * test non-square matrix 
 */
  @Test public void testNonSquare(){
    try {
      new FieldLUDecomposition<Fraction>(new Array2DRowFieldMatrix<Fraction>(new Fraction[][]{{Fraction.ZERO,Fraction.ZERO},{Fraction.ZERO,Fraction.ZERO},{Fraction.ZERO,Fraction.ZERO}}));
      Assert.fail("Expected NonSquareMatrixException");
    }
 catch (    NonSquareMatrixException ime) {
    }
  }
  /** 
 * test PA = LU 
 */
  @Test public void testPAEqualLU(){
    FieldMatrix<Fraction> matrix=new Array2DRowFieldMatrix<Fraction>(FractionField.getInstance(),testData);
    FieldLUDecomposition<Fraction> lu=new FieldLUDecomposition<Fraction>(matrix);
    FieldMatrix<Fraction> l=lu.getL();
    FieldMatrix<Fraction> u=lu.getU();
    FieldMatrix<Fraction> p=lu.getP();
    TestUtils.assertEquals(p.multiply(matrix),l.multiply(u));
    matrix=new Array2DRowFieldMatrix<Fraction>(FractionField.getInstance(),testDataMinus);
    lu=new FieldLUDecomposition<Fraction>(matrix);
    l=lu.getL();
    u=lu.getU();
    p=lu.getP();
    TestUtils.assertEquals(p.multiply(matrix),l.multiply(u));
    matrix=new Array2DRowFieldMatrix<Fraction>(FractionField.getInstance(),17,17);
    for (int i=0; i < matrix.getRowDimension(); ++i) {
      matrix.setEntry(i,i,Fraction.ONE);
    }
    lu=new FieldLUDecomposition<Fraction>(matrix);
    l=lu.getL();
    u=lu.getU();
    p=lu.getP();
    TestUtils.assertEquals(p.multiply(matrix),l.multiply(u));
    matrix=new Array2DRowFieldMatrix<Fraction>(FractionField.getInstance(),singular);
    lu=new FieldLUDecomposition<Fraction>(matrix);
    Assert.assertFalse(lu.getSolver().isNonSingular());
    Assert.assertNull(lu.getL());
    Assert.assertNull(lu.getU());
    Assert.assertNull(lu.getP());
    matrix=new Array2DRowFieldMatrix<Fraction>(FractionField.getInstance(),bigSingular);
    lu=new FieldLUDecomposition<Fraction>(matrix);
    Assert.assertFalse(lu.getSolver().isNonSingular());
    Assert.assertNull(lu.getL());
    Assert.assertNull(lu.getU());
    Assert.assertNull(lu.getP());
  }
  /** 
 * test that L is lower triangular with unit diagonal 
 */
  @Test public void testLLowerTriangular(){
    FieldMatrix<Fraction> matrix=new Array2DRowFieldMatrix<Fraction>(FractionField.getInstance(),testData);
    FieldMatrix<Fraction> l=new FieldLUDecomposition<Fraction>(matrix).getL();
    for (int i=0; i < l.getRowDimension(); i++) {
      Assert.assertEquals(Fraction.ONE,l.getEntry(i,i));
      for (int j=i + 1; j < l.getColumnDimension(); j++) {
        Assert.assertEquals(Fraction.ZERO,l.getEntry(i,j));
      }
    }
  }
  /** 
 * test that U is upper triangular 
 */
  @Test public void testUUpperTriangular(){
    FieldMatrix<Fraction> matrix=new Array2DRowFieldMatrix<Fraction>(FractionField.getInstance(),testData);
    FieldMatrix<Fraction> u=new FieldLUDecomposition<Fraction>(matrix).getU();
    for (int i=0; i < u.getRowDimension(); i++) {
      for (int j=0; j < i; j++) {
        Assert.assertEquals(Fraction.ZERO,u.getEntry(i,j));
      }
    }
  }
  /** 
 * test that P is a permutation matrix 
 */
  @Test public void testPPermutation(){
    FieldMatrix<Fraction> matrix=new Array2DRowFieldMatrix<Fraction>(FractionField.getInstance(),testData);
    FieldMatrix<Fraction> p=new FieldLUDecomposition<Fraction>(matrix).getP();
    FieldMatrix<Fraction> ppT=p.multiply(p.transpose());
    FieldMatrix<Fraction> id=new Array2DRowFieldMatrix<Fraction>(FractionField.getInstance(),p.getRowDimension(),p.getRowDimension());
    for (int i=0; i < id.getRowDimension(); ++i) {
      id.setEntry(i,i,Fraction.ONE);
    }
    TestUtils.assertEquals(id,ppT);
    for (int i=0; i < p.getRowDimension(); i++) {
      int zeroCount=0;
      int oneCount=0;
      int otherCount=0;
      for (int j=0; j < p.getColumnDimension(); j++) {
        final Fraction e=p.getEntry(i,j);
        if (e.equals(Fraction.ZERO)) {
          ++zeroCount;
        }
 else         if (e.equals(Fraction.ONE)) {
          ++oneCount;
        }
 else {
          ++otherCount;
        }
      }
      Assert.assertEquals(p.getColumnDimension() - 1,zeroCount);
      Assert.assertEquals(1,oneCount);
      Assert.assertEquals(0,otherCount);
    }
    for (int j=0; j < p.getColumnDimension(); j++) {
      int zeroCount=0;
      int oneCount=0;
      int otherCount=0;
      for (int i=0; i < p.getRowDimension(); i++) {
        final Fraction e=p.getEntry(i,j);
        if (e.equals(Fraction.ZERO)) {
          ++zeroCount;
        }
 else         if (e.equals(Fraction.ONE)) {
          ++oneCount;
        }
 else {
          ++otherCount;
        }
      }
      Assert.assertEquals(p.getRowDimension() - 1,zeroCount);
      Assert.assertEquals(1,oneCount);
      Assert.assertEquals(0,otherCount);
    }
  }
  /** 
 * test singular 
 */
  @Test public void testSingular(){
    FieldLUDecomposition<Fraction> lu=new FieldLUDecomposition<Fraction>(new Array2DRowFieldMatrix<Fraction>(FractionField.getInstance(),testData));
    Assert.assertTrue(lu.getSolver().isNonSingular());
    lu=new FieldLUDecomposition<Fraction>(new Array2DRowFieldMatrix<Fraction>(FractionField.getInstance(),singular));
    Assert.assertFalse(lu.getSolver().isNonSingular());
    lu=new FieldLUDecomposition<Fraction>(new Array2DRowFieldMatrix<Fraction>(FractionField.getInstance(),bigSingular));
    Assert.assertFalse(lu.getSolver().isNonSingular());
  }
  /** 
 * test matrices values 
 */
  @Test public void testMatricesValues1(){
    FieldLUDecomposition<Fraction> lu=new FieldLUDecomposition<Fraction>(new Array2DRowFieldMatrix<Fraction>(FractionField.getInstance(),testData));
    FieldMatrix<Fraction> lRef=new Array2DRowFieldMatrix<Fraction>(FractionField.getInstance(),new Fraction[][]{{new Fraction(1),new Fraction(0),new Fraction(0)},{new Fraction(2),new Fraction(1),new Fraction(0)},{new Fraction(1),new Fraction(-2),new Fraction(1)}});
    FieldMatrix<Fraction> uRef=new Array2DRowFieldMatrix<Fraction>(FractionField.getInstance(),new Fraction[][]{{new Fraction(1),new Fraction(2),new Fraction(3)},{new Fraction(0),new Fraction(1),new Fraction(-3)},{new Fraction(0),new Fraction(0),new Fraction(-1)}});
    FieldMatrix<Fraction> pRef=new Array2DRowFieldMatrix<Fraction>(FractionField.getInstance(),new Fraction[][]{{new Fraction(1),new Fraction(0),new Fraction(0)},{new Fraction(0),new Fraction(1),new Fraction(0)},{new Fraction(0),new Fraction(0),new Fraction(1)}});
    int[] pivotRef={0,1,2};
    FieldMatrix<Fraction> l=lu.getL();
    TestUtils.assertEquals(lRef,l);
    FieldMatrix<Fraction> u=lu.getU();
    TestUtils.assertEquals(uRef,u);
    FieldMatrix<Fraction> p=lu.getP();
    TestUtils.assertEquals(pRef,p);
    int[] pivot=lu.getPivot();
    for (int i=0; i < pivotRef.length; ++i) {
      Assert.assertEquals(pivotRef[i],pivot[i]);
    }
    Assert.assertTrue(l == lu.getL());
    Assert.assertTrue(u == lu.getU());
    Assert.assertTrue(p == lu.getP());
  }
  /** 
 * test matrices values 
 */
  @Test public void testMatricesValues2(){
    FieldLUDecomposition<Fraction> lu=new FieldLUDecomposition<Fraction>(new Array2DRowFieldMatrix<Fraction>(FractionField.getInstance(),luData));
    FieldMatrix<Fraction> lRef=new Array2DRowFieldMatrix<Fraction>(FractionField.getInstance(),new Fraction[][]{{new Fraction(1),new Fraction(0),new Fraction(0)},{new Fraction(3),new Fraction(1),new Fraction(0)},{new Fraction(1),new Fraction(0),new Fraction(1)}});
    FieldMatrix<Fraction> uRef=new Array2DRowFieldMatrix<Fraction>(FractionField.getInstance(),new Fraction[][]{{new Fraction(2),new Fraction(3),new Fraction(3)},{new Fraction(0),new Fraction(-3),new Fraction(-1)},{new Fraction(0),new Fraction(0),new Fraction(4)}});
    FieldMatrix<Fraction> pRef=new Array2DRowFieldMatrix<Fraction>(FractionField.getInstance(),new Fraction[][]{{new Fraction(1),new Fraction(0),new Fraction(0)},{new Fraction(0),new Fraction(0),new Fraction(1)},{new Fraction(0),new Fraction(1),new Fraction(0)}});
    int[] pivotRef={0,2,1};
    FieldMatrix<Fraction> l=lu.getL();
    TestUtils.assertEquals(lRef,l);
    FieldMatrix<Fraction> u=lu.getU();
    TestUtils.assertEquals(uRef,u);
    FieldMatrix<Fraction> p=lu.getP();
    TestUtils.assertEquals(pRef,p);
    int[] pivot=lu.getPivot();
    for (int i=0; i < pivotRef.length; ++i) {
      Assert.assertEquals(pivotRef[i],pivot[i]);
    }
    Assert.assertTrue(l == lu.getL());
    Assert.assertTrue(u == lu.getU());
    Assert.assertTrue(p == lu.getP());
  }
}
