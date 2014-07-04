package sun.io;
import java.io.*;
/** 
 * Convert byte arrays containing Unicode characters into arrays of actual
 * Unicode characters, assuming a little-endian byte order.
 * @author      Mark Reinhold
 */
public class ByteToCharUnicodeLittle extends ByteToCharUnicode {
  public ByteToCharUnicodeLittle(){
    super(LITTLE,true);
  }
}
