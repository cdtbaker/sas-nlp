package cern.colt.buffer;
import cern.colt.list.DoubleArrayList;
/** 
 * Target of a streaming <tt>DoubleBuffer</tt> into which data is flushed upon buffer overflow.
 * @author wolfgang.hoschek@cern.ch
 * @version 1.0, 09/24/99
 */
public interface DoubleBufferConsumer {
  /** 
 * Adds all elements of the specified list to the receiver.
 * @param list the list of which all elements shall be added.
 */
  public void addAllOf(  DoubleArrayList list);
}
