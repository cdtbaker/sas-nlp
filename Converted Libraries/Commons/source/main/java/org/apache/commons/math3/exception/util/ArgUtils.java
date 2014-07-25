package org.apache.commons.math3.exception.util;
import java.util.List;
import java.util.ArrayList;
/** 
 * Utility class for transforming the list of arguments passed to
 * constructors of exceptions.
 * @version $Id: ArgUtils.java 1364388 2012-07-22 18:16:43Z tn $
 */
public class ArgUtils {
  /** 
 * Class contains only static methods.
 */
  private ArgUtils(){
  }
  /** 
 * Transform a multidimensional array into a one-dimensional list.
 * @param array Array (possibly multidimensional).
 * @return a list of all the {@code Object} instances contained in{@code array}.
 */
  public static Object[] flatten(  Object[] array){
    final List<Object> list=new ArrayList<Object>();
    if (array != null) {
      for (      Object o : array) {
        if (o instanceof Object[]) {
          for (          Object oR : flatten((Object[])o)) {
            list.add(oR);
          }
        }
 else {
          list.add(o);
        }
      }
    }
    return list.toArray();
  }
}
