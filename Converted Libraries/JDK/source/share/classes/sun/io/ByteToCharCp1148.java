package sun.io;
/** 
 * A table to convert Cp1148 to Unicode.  This converter differs from
 * Cp500 is one code point, 0x9F, which changes from \u00A4 to \u20AC.
 * @author  Alan Liu
 */
public class ByteToCharCp1148 extends ByteToCharCp500 {
  public ByteToCharCp1148(){
  }
  public String getCharacterEncoding(){
    return "Cp1148";
  }
  protected char getUnicode(  int byteIndex){
    return (byteIndex == (byte)0x9F) ? '\u20AC' : super.getUnicode(byteIndex);
  }
}
