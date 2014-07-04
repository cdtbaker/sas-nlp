package sun.io;
import java.io.*;
/** 
 * Convert byte arrays containing Unicode characters into arrays of actual
 * Unicode characters, assuming a little-endian byte order and requiring no
 * byte-order mark.
 * @author      Mark Reinhold
 */
public class ByteToCharUnicodeLittleUnmarked extends ByteToCharUnicode {
  public ByteToCharUnicodeLittleUnmarked(){
    super(LITTLE,false);
  }
}
