package org.apache.commons.math3.geometry.euclidean.threed;
import java.lang.reflect.Field;
import org.apache.commons.math3.geometry.euclidean.threed.RotationOrder;
import org.junit.Assert;
import org.junit.Test;
public class RotationOrderTest {
  @Test public void testName(){
    RotationOrder[] orders={RotationOrder.XYZ,RotationOrder.XZY,RotationOrder.YXZ,RotationOrder.YZX,RotationOrder.ZXY,RotationOrder.ZYX,RotationOrder.XYX,RotationOrder.XZX,RotationOrder.YXY,RotationOrder.YZY,RotationOrder.ZXZ,RotationOrder.ZYZ};
    for (int i=0; i < orders.length; ++i) {
      Assert.assertEquals(getFieldName(orders[i]),orders[i].toString());
    }
  }
  private String getFieldName(  RotationOrder order){
    try {
      Field[] fields=RotationOrder.class.getFields();
      for (int i=0; i < fields.length; ++i) {
        if (fields[i].get(null) == order) {
          return fields[i].getName();
        }
      }
    }
 catch (    IllegalAccessException iae) {
    }
    return "unknown";
  }
}
