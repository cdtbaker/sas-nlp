package sun.io;
/** 
 * Convert arrays containing Unicode characters into arrays of bytes, using
 * big-endian byte order; do not write a byte-order mark before the first
 * converted character.
 * @author      Mark Reinhold
 */
public class CharToByteUnicodeBigUnmarked extends CharToByteUnicode {
  public CharToByteUnicodeBigUnmarked(){
    byteOrder=BIG;
    usesMark=false;
  }
}
