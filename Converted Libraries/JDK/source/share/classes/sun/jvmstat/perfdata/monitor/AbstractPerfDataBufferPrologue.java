package sun.jvmstat.perfdata.monitor;
import sun.jvmstat.monitor.*;
import java.nio.ByteOrder;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
/** 
 * Abstraction representing the HotSpot PerfData instrumentation buffer
 * header. This class represents only the fixed portion of the header.
 * Version specific classes represent the portion of the header that
 * may change from release to release.
 * <p>
 * The PerfDataBufferProlog class supports parsing of the following
 * C structure:
 * <pre>
 * typedef struct {
 * jint magic;             // magic number - 0xcafec0c0
 * jbyte byte_order;       // byte order of the buffer
 * jbyte major_version;    // major and minor version numbers
 * jbyte minor_version;
 * jbyte reserved_byte1;   // reserved - see concrete implementations for
 * // possible definition.
 * ...                     // remainder is handled by the subclasses.
 * } PerfDataPrologue
 * </pre>
 * @author Brian Doherty
 * @since 1.5
 */
public abstract class AbstractPerfDataBufferPrologue {
  protected ByteBuffer byteBuffer;
  final static int PERFDATA_PROLOG_OFFSET=0;
  final static int PERFDATA_PROLOG_MAGIC_OFFSET=0;
  final static int PERFDATA_PROLOG_BYTEORDER_OFFSET=4;
  final static int PERFDATA_PROLOG_BYTEORDER_SIZE=1;
  final static int PERFDATA_PROLOG_MAJOR_OFFSET=5;
  final static int PERFDATA_PROLOG_MAJOR_SIZE=1;
  final static int PERFDATA_PROLOG_MINOR_OFFSET=6;
  final static int PERFDATA_PROLOG_MINOR_SIZE=1;
  final static int PERFDATA_PROLOG_RESERVEDB1_OFFSET=7;
  final static int PERFDATA_PROLOG_RESERVEDB1_SIZE=1;
  final static int PERFDATA_PROLOG_SIZE=8;
  final static byte PERFDATA_BIG_ENDIAN=0;
  final static byte PERFDATA_LITTLE_ENDIAN=1;
  final static int PERFDATA_MAGIC=0xcafec0c0;
  public final static String PERFDATA_MAJOR_NAME="sun.perfdata.majorVersion";
  public final static String PERFDATA_MINOR_NAME="sun.perfdata.minorVersion";
  /** 
 * Construct a PerfDataBufferPrologue instance.
 * @param byteBuffer buffer containing the instrumentation data
 */
  public AbstractPerfDataBufferPrologue(  ByteBuffer byteBuffer) throws MonitorException {
    this.byteBuffer=byteBuffer.duplicate();
    if (getMagic() != PERFDATA_MAGIC) {
      throw new MonitorVersionException("Bad Magic: " + Integer.toHexString(getMagic()));
    }
    this.byteBuffer.order(getByteOrder());
  }
  /** 
 * Get the magic number.
 * @return int - the magic number
 */
  public int getMagic(){
    ByteOrder order=byteBuffer.order();
    byteBuffer.order(ByteOrder.BIG_ENDIAN);
    byteBuffer.position(PERFDATA_PROLOG_MAGIC_OFFSET);
    int magic=byteBuffer.getInt();
    byteBuffer.order(order);
    return magic;
  }
  /** 
 * Get the byte order.
 * @return int - the byte order of the instrumentation buffer
 */
  public ByteOrder getByteOrder(){
    byteBuffer.position(PERFDATA_PROLOG_BYTEORDER_OFFSET);
    byte byte_order=byteBuffer.get();
    if (byte_order == PERFDATA_BIG_ENDIAN) {
      return ByteOrder.BIG_ENDIAN;
    }
 else {
      return ByteOrder.LITTLE_ENDIAN;
    }
  }
  /** 
 * Get the major version.
 * @return int - the major version
 */
  public int getMajorVersion(){
    byteBuffer.position(PERFDATA_PROLOG_MAJOR_OFFSET);
    return (int)byteBuffer.get();
  }
  /** 
 * Get the minor version.
 * @return int - the minor version
 */
  public int getMinorVersion(){
    byteBuffer.position(PERFDATA_PROLOG_MINOR_OFFSET);
    return (int)byteBuffer.get();
  }
  /** 
 * Get the accessible flag. If supported, it indicates that the shared
 * memory region is sufficiently initialized for client acccess.
 * @return boolean - the initialized status
 * @see #supportsAccessible()
 */
  public abstract boolean isAccessible();
  /** 
 * Test if the accessible flag is supported by this version of
 * the PerfDataBufferPrologue. Although not an abstract method, this
 * method should be overridden by version specific subclasses.
 * @return boolean - the initialized flag support status.
 * @see #isAccessible()
 */
  public abstract boolean supportsAccessible();
  /** 
 * Get the size of the header portion of the instrumentation buffer.
 * @return int - the size of the header
 */
  public int getSize(){
    return PERFDATA_PROLOG_SIZE;
  }
  /** 
 * Return an IntBuffer that accesses the major version number.
 * This is used to create a Monitor object for this value.
 * @return IntBuffer - a ByteBuffer that accesses the major version number
 * in the instrumentation buffer header.
 */
  public IntBuffer majorVersionBuffer(){
    int[] holder=new int[1];
    holder[0]=getMajorVersion();
    IntBuffer ib=IntBuffer.wrap(holder);
    ib.limit(1);
    return ib;
  }
  /** 
 * Return an IntBuffer that accesses the minor version number.
 * This is used to create a Monitor object for this value.
 * @return IntBuffer - a ByteBuffer that accesses the minor version number
 * in the instrumentation buffer header.
 */
  public IntBuffer minorVersionBuffer(){
    int[] holder=new int[1];
    holder[0]=getMinorVersion();
    IntBuffer ib=IntBuffer.wrap(holder);
    ib.limit(1);
    return ib;
  }
  /** 
 * Get the magic number from the given byteBuffer.
 * @return int - the magic number
 */
  public static int getMagic(  ByteBuffer bb){
    int position=bb.position();
    ByteOrder order=bb.order();
    bb.order(ByteOrder.BIG_ENDIAN);
    bb.position(PERFDATA_PROLOG_MAGIC_OFFSET);
    int magic=bb.getInt();
    bb.order(order);
    bb.position(position);
    return magic;
  }
  /** 
 * Get the major version number from the given ByteBuffer.
 * @return int - the major version
 */
  public static int getMajorVersion(  ByteBuffer bb){
    int position=bb.position();
    bb.position(PERFDATA_PROLOG_MAJOR_OFFSET);
    int major=(int)bb.get();
    bb.position(position);
    return major;
  }
  /** 
 * Get the minor version number from the given ByteBuffer.
 * @return int - the minor version
 */
  public static int getMinorVersion(  ByteBuffer bb){
    int position=bb.position();
    bb.position(PERFDATA_PROLOG_MINOR_OFFSET);
    int minor=(int)bb.get();
    bb.position(position);
    return minor;
  }
  /** 
 * Get the byte order for the given ByteBuffer.
 * @return int - the byte order of the instrumentation buffer
 */
  public static ByteOrder getByteOrder(  ByteBuffer bb){
    int position=bb.position();
    bb.position(PERFDATA_PROLOG_BYTEORDER_OFFSET);
    ByteOrder order=(bb.get() == PERFDATA_BIG_ENDIAN) ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN;
    bb.position(position);
    return order;
  }
}