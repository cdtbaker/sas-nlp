package org.apache.commons.math3.linear;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
/** 
 * Test cases for the {@link OpenMapRealVector} class.
 * @version $Id: SparseRealVectorTest.java 1361170 2012-07-13 11:37:40Z celestin $
 */
public class SparseRealVectorTest extends RealVectorAbstractTest {
  @Override public RealVector create(  double[] data){
    return new OpenMapRealVector(data);
  }
  @Test public void testConstructors(){
    final double[] vec1={1d,2d,3d};
    final Double[] dvec1={1d,2d,3d,4d,5d,6d,7d,8d,9d};
    OpenMapRealVector v0=new OpenMapRealVector();
    Assert.assertEquals("testData len",0,v0.getDimension());
    OpenMapRealVector v1=new OpenMapRealVector(7);
    Assert.assertEquals("testData len",7,v1.getDimension());
    Assert.assertEquals("testData is 0.0 ",0.0,v1.getEntry(6),0);
    OpenMapRealVector v3=new OpenMapRealVector(vec1);
    Assert.assertEquals("testData len",3,v3.getDimension());
    Assert.assertEquals("testData is 2.0 ",2.0,v3.getEntry(1),0);
    RealVector v5_i=new OpenMapRealVector(dvec1);
    Assert.assertEquals("testData len",9,v5_i.getDimension());
    Assert.assertEquals("testData is 9.0 ",9.0,v5_i.getEntry(8),0);
    OpenMapRealVector v5=new OpenMapRealVector(dvec1);
    Assert.assertEquals("testData len",9,v5.getDimension());
    Assert.assertEquals("testData is 9.0 ",9.0,v5.getEntry(8),0);
    OpenMapRealVector v7=new OpenMapRealVector(v1);
    Assert.assertEquals("testData len",7,v7.getDimension());
    Assert.assertEquals("testData is 0.0 ",0.0,v7.getEntry(6),0);
    RealVectorTestImpl v7_i=new RealVectorTestImpl(vec1);
    OpenMapRealVector v7_2=new OpenMapRealVector(v7_i);
    Assert.assertEquals("testData len",3,v7_2.getDimension());
    Assert.assertEquals("testData is 0.0 ",2.0d,v7_2.getEntry(1),0);
    OpenMapRealVector v8=new OpenMapRealVector(v1);
    Assert.assertEquals("testData len",7,v8.getDimension());
    Assert.assertEquals("testData is 0.0 ",0.0,v8.getEntry(6),0);
  }
  @Test public void testConcurrentModification(){
    final RealVector u=new OpenMapRealVector(3,1e-6);
    u.setEntry(0,1);
    u.setEntry(1,0);
    u.setEntry(2,2);
    final RealVector v1=new OpenMapRealVector(3,1e-6);
    v1.setEntry(0,0);
    v1.setEntry(1,3);
    v1.setEntry(2,0);
    u.ebeMultiply(v1);
    u.ebeDivide(v1);
  }
  /** 
 * XXX This test is disabled because it currently fails.
 * The bug must still be fixed in the sparse vector implementation.
 * When this is done, this override should be deleted.
 */
  @Test @Override @Ignore("This test is skipped until MATH-821 is fixed") public void testMap(){
  }
  /** 
 * XXX This test is disabled because it currently fails.
 * The bug must still be fixed in the sparse vector implementation.
 * When this is done, this override should be deleted.
 */
  @Test @Override @Ignore("This test is skipped until MATH-821 is fixed") public void testMapToSelf(){
  }
}
