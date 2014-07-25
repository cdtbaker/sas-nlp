package sun.io;
/** 
 * A table to convert Cp1140 to Unicode.  This converter differs from
 * Cp037 is one code point, 0x9F, which changes from \u00A4 to \u20AC.
 * @author  Alan Liu
 */
public class ByteToCharCp1140 extends ByteToCharCp037 {
  public ByteToCharCp1140(){
  }
  public String getCharacterEncoding(){
    return "Cp1140";
  }
  protected char getUnicode(  int byteIndex){
    return (byteIndex == (byte)0x9F) ? '\u20AC' : super.getUnicode(byteIndex);
  }
}
