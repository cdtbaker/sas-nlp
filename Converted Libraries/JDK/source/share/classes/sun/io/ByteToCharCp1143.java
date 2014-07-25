package sun.io;
/** 
 * A table to convert Cp1143 to Unicode.  This converter differs from
 * Cp278 is one code point, 0x5A, which changes from \u00A4 to \u20AC.
 * @author  Alan Liu
 */
public class ByteToCharCp1143 extends ByteToCharCp278 {
  public ByteToCharCp1143(){
  }
  public String getCharacterEncoding(){
    return "Cp1143";
  }
  protected char getUnicode(  int byteIndex){
    return (byteIndex == 0x5A) ? '\u20AC' : super.getUnicode(byteIndex);
  }
}
