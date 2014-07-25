package sun.io;
/** 
 * Convert arrays containing Unicode characters into arrays of bytes, using
 * little-endian byte order; do not write a byte-order mark before the first
 * converted character.
 * @author      Mark Reinhold
 */
public class CharToByteUnicodeLittleUnmarked extends CharToByteUnicode {
  public CharToByteUnicodeLittleUnmarked(){
    byteOrder=LITTLE;
    usesMark=false;
  }
}
