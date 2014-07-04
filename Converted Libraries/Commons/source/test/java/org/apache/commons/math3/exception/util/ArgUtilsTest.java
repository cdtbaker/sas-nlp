package org.apache.commons.math3.exception.util;
import java.util.List;
import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Test;
/** 
 * Test for {@link ArgUtils}.
 * @version $Id$
 */
public class ArgUtilsTest {
  @Test public void testFlatten(){
    final List<Object> orig=new ArrayList<Object>();
    final Object[] struct=new Object[]{new Object[]{new Object[]{create(orig),create(orig)},create(orig),new Object[]{create(orig)}},create(orig),new Object[]{create(orig),new Object[]{create(orig),create(orig)}},create(orig)};
    Object[] flat=ArgUtils.flatten(struct);
    Assert.assertEquals(flat.length,orig.size());
    for (int i=0, max=orig.size(); i < max; i++) {
      Assert.assertEquals(orig.get(i),flat[i]);
    }
  }
  /** 
 * Create and store an {@code Object}.
 * @param list List to store to.
 * @return the stored object.
 */
  private Object create(  List<Object> list){
    final Object o=new Object();
    list.add(o);
    return o;
  }
}
