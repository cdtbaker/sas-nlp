package sun.io;
/** 
 * A table to convert Cp858 to Unicode.  This converter differs from
 * Cp850 is one code point, 0xD5, which changes from \u0131 to \u20AC.
 * @author  Alan Liu
 */
public class ByteToCharCp858 extends ByteToCharCp850 {
  public ByteToCharCp858(){
  }
  public String getCharacterEncoding(){
    return "Cp858";
  }
  protected char getUnicode(  int byteIndex){
    return (byteIndex == (byte)0xD5) ? '\u20AC' : super.getUnicode(byteIndex);
  }
}
