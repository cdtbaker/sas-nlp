package sun.misc;
public interface JavaNioAccess {
  /** 
 * Provides access to information on buffer usage.
 */
interface BufferPool {
    String getName();
    long getCount();
    long getTotalCapacity();
    long getMemoryUsed();
  }
  BufferPool getDirectBufferPool();
}
