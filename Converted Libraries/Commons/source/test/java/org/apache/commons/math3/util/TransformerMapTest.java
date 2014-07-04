package org.apache.commons.math3.util;
import org.apache.commons.math3.TestUtils;
import org.junit.Assert;
import org.junit.Test;
/** 
 * @version $Id: TransformerMapTest.java 1244107 2012-02-14 16:17:55Z erans $
 */
public class TransformerMapTest {
  /** 
 */
  @Test public void testPutTransformer(){
    NumberTransformer expected=new DefaultTransformer();
    TransformerMap map=new TransformerMap();
    map.putTransformer(TransformerMapTest.class,expected);
    Assert.assertEquals(expected,map.getTransformer(TransformerMapTest.class));
  }
  /** 
 */
  @Test public void testContainsClass(){
    NumberTransformer expected=new DefaultTransformer();
    TransformerMap map=new TransformerMap();
    map.putTransformer(TransformerMapTest.class,expected);
    Assert.assertTrue(map.containsClass(TransformerMapTest.class));
  }
  /** 
 */
  @Test public void testContainsTransformer(){
    NumberTransformer expected=new DefaultTransformer();
    TransformerMap map=new TransformerMap();
    map.putTransformer(TransformerMapTest.class,expected);
    Assert.assertTrue(map.containsTransformer(expected));
  }
  /** 
 */
  @Test public void testRemoveTransformer(){
    NumberTransformer expected=new DefaultTransformer();
    TransformerMap map=new TransformerMap();
    map.putTransformer(TransformerMapTest.class,expected);
    Assert.assertTrue(map.containsClass(TransformerMapTest.class));
    Assert.assertTrue(map.containsTransformer(expected));
    map.removeTransformer(TransformerMapTest.class);
    Assert.assertFalse(map.containsClass(TransformerMapTest.class));
    Assert.assertFalse(map.containsTransformer(expected));
  }
  /** 
 */
  @Test public void testClear(){
    NumberTransformer expected=new DefaultTransformer();
    TransformerMap map=new TransformerMap();
    map.putTransformer(TransformerMapTest.class,expected);
    Assert.assertTrue(map.containsClass(TransformerMapTest.class));
    map.clear();
    Assert.assertFalse(map.containsClass(TransformerMapTest.class));
  }
  /** 
 */
  @Test public void testClasses(){
    NumberTransformer expected=new DefaultTransformer();
    TransformerMap map=new TransformerMap();
    map.putTransformer(TransformerMapTest.class,expected);
    Assert.assertTrue(map.classes().contains(TransformerMapTest.class));
  }
  /** 
 */
  @Test public void testTransformers(){
    NumberTransformer expected=new DefaultTransformer();
    TransformerMap map=new TransformerMap();
    map.putTransformer(TransformerMapTest.class,expected);
    Assert.assertTrue(map.transformers().contains(expected));
  }
  @Test public void testSerial(){
    NumberTransformer expected=new DefaultTransformer();
    TransformerMap map=new TransformerMap();
    map.putTransformer(TransformerMapTest.class,expected);
    Assert.assertEquals(map,TestUtils.serializeAndRecover(map));
  }
}
