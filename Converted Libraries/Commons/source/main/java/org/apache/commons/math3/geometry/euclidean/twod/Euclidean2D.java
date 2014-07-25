package org.apache.commons.math3.geometry.euclidean.twod;
import java.io.Serializable;
import org.apache.commons.math3.geometry.Space;
import org.apache.commons.math3.geometry.euclidean.oned.Euclidean1D;
/** 
 * This class implements a three-dimensional space.
 * @version $Id: Euclidean2D.java 1416643 2012-12-03 19:37:14Z tn $
 * @since 3.0
 */
public class Euclidean2D implements Serializable, Space {
  /** 
 * Serializable version identifier. 
 */
  private static final long serialVersionUID=4793432849757649566L;
  /** 
 * Private constructor for the singleton.
 */
  private Euclidean2D(){
  }
  /** 
 * Get the unique instance.
 * @return the unique instance
 */
  public static Euclidean2D getInstance(){
    return LazyHolder.INSTANCE;
  }
  /** 
 * {@inheritDoc} 
 */
  public int getDimension(){
    return 2;
  }
  /** 
 * {@inheritDoc} 
 */
  public Euclidean1D getSubSpace(){
    return Euclidean1D.getInstance();
  }
  /** 
 * Holder for the instance.
 * <p>We use here the Initialization On Demand Holder Idiom.</p>
 */
private static class LazyHolder {
    /** 
 * Cached field instance. 
 */
    private static final Euclidean2D INSTANCE=new Euclidean2D();
  }
  /** 
 * Handle deserialization of the singleton.
 * @return the singleton instance
 */
  private Object readResolve(){
    return LazyHolder.INSTANCE;
  }
}
