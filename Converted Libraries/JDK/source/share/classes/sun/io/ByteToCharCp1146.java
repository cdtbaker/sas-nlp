package sun.io;
/** 
 * A table to convert Cp1146 to Unicode.  This converter differs from
 * Cp285 is one code point, 0x9F, which changes from \u00A4 to \u20AC.
 * @author  Alan Liu
 */
public class ByteToCharCp1146 extends ByteToCharCp285 {
  public ByteToCharCp1146(){
  }
  public String getCharacterEncoding(){
    return "Cp1146";
  }
  protected char getUnicode(  int byteIndex){
    return (byteIndex == (byte)0x9F) ? '\u20AC' : super.getUnicode(byteIndex);
  }
}
