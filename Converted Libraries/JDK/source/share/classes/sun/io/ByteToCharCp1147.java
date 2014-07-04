package sun.io;
/** 
 * A table to convert Cp1147 to Unicode.  This converter differs from
 * Cp297 is one code point, 0x9F, which changes from \u00A4 to \u20AC.
 * @author  Alan Liu
 */
public class ByteToCharCp1147 extends ByteToCharCp297 {
  public ByteToCharCp1147(){
  }
  public String getCharacterEncoding(){
    return "Cp1147";
  }
  protected char getUnicode(  int byteIndex){
    return (byteIndex == (byte)0x9F) ? '\u20AC' : super.getUnicode(byteIndex);
  }
}
