package cern.colt.buffer;
import cern.colt.list.IntArrayList;
/** 
 * Target of a streaming <tt>IntBuffer3D</tt> into which data is flushed upon buffer overflow.
 * @author wolfgang.hoschek@cern.ch
 * @version 1.0, 09/24/99
 */
public interface IntBuffer3DConsumer {
  /** 
 * Adds all specified (x,y,z) points to the receiver.
 * @param x the x-coordinates of the points to be added.
 * @param y the y-coordinates of the points to be added. 
 * @param z the z-coordinates of the points to be added.
 */
  public void addAllOf(  IntArrayList x,  IntArrayList y,  IntArrayList z);
}
