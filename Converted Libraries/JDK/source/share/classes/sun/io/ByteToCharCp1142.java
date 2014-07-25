package sun.io;
/** 
 * A table to convert Cp1142 to Unicode.  This converter differs from
 * Cp277 is one code point, 0x5A, which changes from \u00A4 to \u20AC.
 * @author  Alan Liu
 */
public class ByteToCharCp1142 extends ByteToCharCp277 {
  public ByteToCharCp1142(){
  }
  public String getCharacterEncoding(){
    return "Cp1142";
  }
  protected char getUnicode(  int byteIndex){
    return (byteIndex == 0x5A) ? '\u20AC' : super.getUnicode(byteIndex);
  }
}
