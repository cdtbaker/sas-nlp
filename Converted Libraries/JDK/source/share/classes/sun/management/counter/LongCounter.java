package sun.management.counter;
/** 
 * Interface for a performance counter wrapping a
 * <code>long</code> basic type.
 * @author   Brian Doherty
 */
public interface LongCounter extends Counter {
  /** 
 * Get the value of this Long performance counter
 */
  public long longValue();
}
