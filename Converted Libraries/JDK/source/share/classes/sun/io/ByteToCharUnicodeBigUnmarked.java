package sun.io;
import java.io.*;
/** 
 * Convert byte arrays containing Unicode characters into arrays of actual
 * Unicode characters, assuming a big-endian byte order and requiring no
 * byte-order mark.
 * @author      Mark Reinhold
 */
public class ByteToCharUnicodeBigUnmarked extends ByteToCharUnicode {
  public ByteToCharUnicodeBigUnmarked(){
    super(BIG,false);
  }
}
