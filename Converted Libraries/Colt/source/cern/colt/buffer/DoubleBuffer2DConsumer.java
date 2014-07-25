package cern.colt.buffer;
import cern.colt.list.DoubleArrayList;
/** 
 * Target of a streaming <tt>DoubleBuffer2D</tt> into which data is flushed upon buffer overflow.
 * @author wolfgang.hoschek@cern.ch
 * @version 1.0, 09/24/99
 */
public interface DoubleBuffer2DConsumer {
  /** 
 * Adds all specified (x,y) points to the receiver.
 * @param x the x-coordinates of the points to be added.
 * @param y the y-coordinates of the points to be added.
 */
  public void addAllOf(  DoubleArrayList x,  DoubleArrayList y);
}
