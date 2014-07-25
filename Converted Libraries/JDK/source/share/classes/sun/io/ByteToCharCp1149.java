package sun.io;
/** 
 * A table to convert Cp1149 to Unicode.  This converter differs from
 * Cp871 is one code point, 0x9F, which changes from \u00A4 to \u20AC.
 * @author  Alan Liu
 */
public class ByteToCharCp1149 extends ByteToCharCp871 {
  public ByteToCharCp1149(){
  }
  public String getCharacterEncoding(){
    return "Cp1149";
  }
  protected char getUnicode(  int byteIndex){
    return (byteIndex == (byte)0x9F) ? '\u20AC' : super.getUnicode(byteIndex);
  }
}
