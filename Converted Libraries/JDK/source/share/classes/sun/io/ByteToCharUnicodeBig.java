package sun.io;
import java.io.*;
/** 
 * Convert byte arrays containing Unicode characters into arrays of actual
 * Unicode characters, assuming a big-endian byte order.
 * @author      Mark Reinhold
 */
public class ByteToCharUnicodeBig extends ByteToCharUnicode {
  public ByteToCharUnicodeBig(){
    super(BIG,true);
  }
}
