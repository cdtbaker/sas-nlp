package org.apache.commons.math3.stat.descriptive.rank;
import java.io.Serializable;
import org.apache.commons.math3.exception.NullArgumentException;
/** 
 * Returns the median of the available values.  This is the same as the 50th percentile.
 * See {@link Percentile} for a description of the algorithm used.
 * <p>
 * <strong>Note that this implementation is not synchronized.</strong> If
 * multiple threads access an instance of this class concurrently, and at least
 * one of the threads invokes the <code>increment()</code> or
 * <code>clear()</code> method, it must be synchronized externally.</p>
 * @version $Id: Median.java 1416643 2012-12-03 19:37:14Z tn $
 */
public class Median extends Percentile implements Serializable {
  /** 
 * Serializable version identifier 
 */
  private static final long serialVersionUID=-3961477041290915687L;
  /** 
 * Default constructor.
 */
  public Median(){
    super(50.0);
  }
  /** 
 * Copy constructor, creates a new {@code Median} identical
 * to the {@code original}
 * @param original the {@code Median} instance to copy
 * @throws NullArgumentException if original is null
 */
  public Median(  Median original) throws NullArgumentException {
    super(original);
  }
}
