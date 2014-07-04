package sun.io;
/** 
 * A table to convert Cp1144 to Unicode.  This converter differs from
 * Cp280 is one code point, 0x9F, which changes from \u00A4 to \u20AC.
 * @author  Alan Liu
 */
public class ByteToCharCp1144 extends ByteToCharCp280 {
  public ByteToCharCp1144(){
  }
  public String getCharacterEncoding(){
    return "Cp1144";
  }
  protected char getUnicode(  int byteIndex){
    return (byteIndex == (byte)0x9F) ? '\u20AC' : super.getUnicode(byteIndex);
  }
}
