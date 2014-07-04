package sun.io;
/** 
 * A table to convert Cp1141 to Unicode.  This converter differs from
 * Cp273 is one code point, 0x9F, which changes from \u00A4 to \u20AC.
 * @author  Alan Liu
 */
public class ByteToCharCp1141 extends ByteToCharCp273 {
  public ByteToCharCp1141(){
  }
  public String getCharacterEncoding(){
    return "Cp1141";
  }
  protected char getUnicode(  int byteIndex){
    return (byteIndex == (byte)0x9F) ? '\u20AC' : super.getUnicode(byteIndex);
  }
}
