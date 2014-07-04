package sun.io;
/** 
 * Convert arrays containing Unicode characters into arrays of bytes, using
 * big-endian byte order.
 * @author      Mark Reinhold
 */
public class CharToByteUnicodeBig extends CharToByteUnicode {
  public CharToByteUnicodeBig(){
    byteOrder=BIG;
  }
}
