package org.apache.commons.math3.util;
import org.apache.commons.math3.stat.StatUtils;
import org.junit.Assert;
import org.junit.Test;
/** 
 * This class contains test cases for the ExpandableDoubleArray.
 * @version $Id: DoubleArrayAbstractTest.java 1244107 2012-02-14 16:17:55Z erans $
 */
public abstract class DoubleArrayAbstractTest {
  protected DoubleArray da=null;
  protected DoubleArray ra=null;
  @Test public void testAdd1000(){
    for (int i=0; i < 1000; i++) {
      da.addElement(i);
    }
    Assert.assertEquals("Number of elements should be equal to 1000 after adding 1000 values",1000,da.getNumElements());
    Assert.assertEquals("The element at the 56th index should be 56",56.0,da.getElement(56),Double.MIN_VALUE);
  }
  @Test public void testGetValues(){
    double[] controlArray={2.0,4.0,6.0};
    da.addElement(2.0);
    da.addElement(4.0);
    da.addElement(6.0);
    double[] testArray=da.getElements();
    for (int i=0; i < da.getNumElements(); i++) {
      Assert.assertEquals("The testArray values should equal the controlArray values, index i: " + i + " does not match",testArray[i],controlArray[i],Double.MIN_VALUE);
    }
  }
  @Test public void testAddElementRolling(){
    ra.addElement(0.5);
    ra.addElement(1.0);
    ra.addElement(1.0);
    ra.addElement(1.0);
    ra.addElement(1.0);
    ra.addElement(1.0);
    ra.addElementRolling(2.0);
    Assert.assertEquals("There should be 6 elements in the eda",6,ra.getNumElements());
    Assert.assertEquals("The max element should be 2.0",2.0,StatUtils.max(ra.getElements()),Double.MIN_VALUE);
    Assert.assertEquals("The min element should be 1.0",1.0,StatUtils.min(ra.getElements()),Double.MIN_VALUE);
    for (int i=0; i < 1024; i++) {
      ra.addElementRolling(i);
    }
    Assert.assertEquals("We just inserted 1024 rolling elements, num elements should still be 6",6,ra.getNumElements());
  }
  @Test public void testMinMax(){
    da.addElement(2.0);
    da.addElement(22.0);
    da.addElement(-2.0);
    da.addElement(21.0);
    da.addElement(22.0);
    da.addElement(42.0);
    da.addElement(62.0);
    da.addElement(22.0);
    da.addElement(122.0);
    da.addElement(1212.0);
    Assert.assertEquals("Min should be -2.0",-2.0,StatUtils.min(da.getElements()),Double.MIN_VALUE);
    Assert.assertEquals("Max should be 1212.0",1212.0,StatUtils.max(da.getElements()),Double.MIN_VALUE);
  }
}
