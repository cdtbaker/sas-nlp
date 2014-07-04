package org.apache.commons.math3.geometry.euclidean.threed;
import java.io.Serializable;
import org.apache.commons.math3.geometry.Space;
import org.apache.commons.math3.geometry.euclidean.twod.Euclidean2D;
/** 
 * This class implements a three-dimensional space.
 * @version $Id: Euclidean3D.java 1416643 2012-12-03 19:37:14Z tn $
 * @since 3.0
 */
public class Euclidean3D implements Serializable, Space {
  /** 
 * Serializable version identifier. 
 */
  private static final long serialVersionUID=6249091865814886817L;
  /** 
 * Private constructor for the singleton.
 */
  private Euclidean3D(){
  }
  /** 
 * Get the unique instance.
 * @return the unique instance
 */
  public static Euclidean3D getInstance(){
    return LazyHolder.INSTANCE;
  }
  /** 
 * {@inheritDoc} 
 */
  public int getDimension(){
    return 3;
  }
  /** 
 * {@inheritDoc} 
 */
  public Euclidean2D getSubSpace(){
    return Euclidean2D.getInstance();
  }
  /** 
 * Holder for the instance.
 * <p>We use here the Initialization On Demand Holder Idiom.</p>
 */
private static class LazyHolder {
    /** 
 * Cached field instance. 
 */
    private static final Euclidean3D INSTANCE=new Euclidean3D();
  }
  /** 
 * Handle deserialization of the singleton.
 * @return the singleton instance
 */
  private Object readResolve(){
    return LazyHolder.INSTANCE;
  }
}
