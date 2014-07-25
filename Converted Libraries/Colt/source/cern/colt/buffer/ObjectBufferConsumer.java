package cern.colt.buffer;
import cern.colt.list.ObjectArrayList;
/** 
 * Target of a streaming <tt>ObjectBuffer</tt> into which data is flushed upon buffer overflow.
 * @author wolfgang.hoschek@cern.ch
 * @version 1.0, 09/24/99
 */
public interface ObjectBufferConsumer {
  /** 
 * Adds all elements of the specified list to the receiver.
 * @param list the list of which all elements shall be added.
 */
  public void addAllOf(  ObjectArrayList list);
}
