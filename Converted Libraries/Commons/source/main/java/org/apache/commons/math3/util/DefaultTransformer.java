package org.apache.commons.math3.util;
import java.io.Serializable;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import org.apache.commons.math3.exception.NullArgumentException;
/** 
 * A Default NumberTransformer for java.lang.Numbers and Numeric Strings. This
 * provides some simple conversion capabilities to turn any java.lang.Number
 * into a primitive double or to turn a String representation of a Number into
 * a double.
 * @version $Id: DefaultTransformer.java 1416643 2012-12-03 19:37:14Z tn $
 */
public class DefaultTransformer implements NumberTransformer, Serializable {
  /** 
 * Serializable version identifier 
 */
  private static final long serialVersionUID=4019938025047800455L;
  /** 
 * @param o  the object that gets transformed.
 * @return a double primitive representation of the Object o.
 * @throws NullArgumentException if Object <code>o</code> is {@code null}.
 * @throws MathIllegalArgumentException if Object <code>o</code>
 * cannot successfully be transformed
 * @see <a href="http://commons.apache.org/collections/api-release/org/apache/commons/collections/Transformer.html">Commons Collections Transformer</a>
 */
  public double transform(  Object o) throws NullArgumentException, MathIllegalArgumentException {
    if (o == null) {
      throw new NullArgumentException(LocalizedFormats.OBJECT_TRANSFORMATION);
    }
    if (o instanceof Number) {
      return ((Number)o).doubleValue();
    }
    try {
      return Double.valueOf(o.toString()).doubleValue();
    }
 catch (    NumberFormatException e) {
      throw new MathIllegalArgumentException(LocalizedFormats.CANNOT_TRANSFORM_TO_DOUBLE,o.toString());
    }
  }
  /** 
 * {@inheritDoc} 
 */
  @Override public boolean equals(  Object other){
    if (this == other) {
      return true;
    }
    return other instanceof DefaultTransformer;
  }
  /** 
 * {@inheritDoc} 
 */
  @Override public int hashCode(){
    return 401993047;
  }
}
