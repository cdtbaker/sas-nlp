package cern.colt.buffer;
import cern.colt.list.IntArrayList;
/** 
 * Target of a streaming <tt>IntBuffer</tt> into which data is flushed upon buffer overflow.
 * @author wolfgang.hoschek@cern.ch
 * @version 1.0, 09/24/99
 */
public interface IntBufferConsumer {
  /** 
 * Adds all elements of the specified list to the receiver.
 * @param list the list of which all elements shall be added.
 */
  public void addAllOf(  IntArrayList list);
}
