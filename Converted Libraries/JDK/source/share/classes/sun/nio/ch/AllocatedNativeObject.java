package sun.nio.ch;
class AllocatedNativeObject extends NativeObject {
  /** 
 * Allocates a memory area of at least <tt>size</tt> bytes outside of the
 * Java heap and creates a native object for that area.
 * @param sizeNumber of bytes to allocate
 * @param pageAlignedIf <tt>true</tt> then the area will be aligned on a hardware
 * page boundary
 * @throws OutOfMemoryErrorIf the request cannot be satisfied
 */
  AllocatedNativeObject(  int size,  boolean pageAligned){
    super(size,pageAligned);
  }
  /** 
 * Frees the native memory area associated with this object.
 */
  synchronized void free(){
    if (allocationAddress != 0) {
      unsafe.freeMemory(allocationAddress);
      allocationAddress=0;
    }
  }
}
