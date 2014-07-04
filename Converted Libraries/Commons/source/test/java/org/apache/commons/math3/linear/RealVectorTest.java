package org.apache.commons.math3.linear;
import java.util.Iterator;
import org.apache.commons.math3.linear.RealVector.Entry;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
/** 
 * Tests for {@link RealVector}.
 */
public class RealVectorTest extends RealVectorAbstractTest {
  @Override public RealVector create(  final double[] data){
    return new RealVectorTestImpl(data);
  }
  @Test @Ignore("Abstract class RealVector does not implement append(RealVector).") @Override public void testAppendVector(){
  }
  @Test @Ignore("Abstract class RealVector does not implement append(double)") @Override public void testAppendScalar(){
  }
  @Test @Ignore("Abstract class RealVector does not implement getSubvector(int, int)") @Override public void testGetSubVector(){
  }
  @Test @Ignore("Abstract class RealVector does not implement getSubvector(int, int)") @Override public void testGetSubVectorInvalidIndex1(){
  }
  @Test @Ignore("Abstract class RealVector does not implement getSubvector(int, int)") @Override public void testGetSubVectorInvalidIndex2(){
  }
  @Test @Ignore("Abstract class RealVector does not implement getSubvector(int, int)") @Override public void testGetSubVectorInvalidIndex3(){
  }
  @Test @Ignore("Abstract class RealVector does not implement getSubvector(int, int)") @Override public void testGetSubVectorInvalidIndex4(){
  }
  @Test @Ignore("Abstract class RealVector does not implement setSubvector(int, RealVector)") @Override public void testSetSubVectorSameType(){
  }
  @Test @Ignore("Abstract class RealVector does not implement setSubvector(int, RealVector)") @Override public void testSetSubVectorMixedType(){
  }
  @Test @Ignore("Abstract class RealVector does not implement setSubvector(int, RealVector)") @Override public void testSetSubVectorInvalidIndex1(){
  }
  @Test @Ignore("Abstract class RealVector does not implement setSubvector(int, RealVector)") @Override public void testSetSubVectorInvalidIndex2(){
  }
  @Test @Ignore("Abstract class RealVector does not implement setSubvector(int, RealVector)") @Override public void testSetSubVectorInvalidIndex3(){
  }
  @Test @Ignore("Abstract class RealVector does not implement isNaN()") @Override public void testIsNaN(){
  }
  @Test @Ignore("Abstract class RealVector does not implement isNaN()") @Override public void testIsInfinite(){
  }
  @Test @Ignore("Abstract class RealVector does not implement ebeMultiply(RealVector)") @Override public void testEbeMultiplySameType(){
  }
  @Test @Ignore("Abstract class RealVector does not implement ebeMultiply(RealVector)") @Override public void testEbeMultiplyMixedTypes(){
  }
  @Test @Ignore("Abstract class RealVector does not implement ebeMultiply(RealVector)") @Override public void testEbeMultiplyDimensionMismatch(){
  }
  @Test @Ignore("Abstract class RealVector does not implement ebeDivide(RealVector)") @Override public void testEbeDivideSameType(){
  }
  @Test @Ignore("Abstract class RealVector does not implement ebeDivide(RealVector)") @Override public void testEbeDivideMixedTypes(){
  }
  @Test @Ignore("Abstract class RealVector does not implement ebeDivide(RealVector)") @Override public void testEbeDivideDimensionMismatch(){
  }
  @Test @Ignore("Abstract class RealVector does not implement getL1Norm()") @Override public void testGetL1Norm(){
  }
  @Test @Ignore("Abstract class RealVector does not implement getLInfNorm()") @Override public void testGetLInfNorm(){
  }
  @Test public void testSparseIterator(){
    final double x=getPreferredEntryValue();
    final double[] data={x,x + 1d,x,x,x + 2d,x + 3d,x + 4d,x,x,x,x + 5d,x + 6d,x};
    RealVector v=create(data);
    Entry e;
    int i=0;
    final double[] nonDefault={x + 1d,x + 2d,x + 3d,x + 4d,x + 5d,x + 6d};
    for (Iterator<Entry> it=v.sparseIterator(); it.hasNext(); i++) {
      e=it.next();
      Assert.assertEquals(nonDefault[i],e.getValue(),0);
    }
    double[] onlyOne={x,x + 1d,x};
    v=create(onlyOne);
    for (Iterator<Entry> it=v.sparseIterator(); it.hasNext(); ) {
      e=it.next();
      Assert.assertEquals(onlyOne[1],e.getValue(),0);
    }
  }
  @Test @Ignore("Abstract class RealVector is not serializable.") @Override public void testSerial(){
  }
  @Test @Ignore("Abstract class RealVector does not override equals(Object).") @Override public void testEquals(){
  }
}
