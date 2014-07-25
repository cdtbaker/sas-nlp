package sun.io;
/** 
 * A algorithmic conversion from ASCII to Unicode
 * @author Limin Shi
 */
public class ByteToCharASCII extends ByteToCharConverter {
  public String getCharacterEncoding(){
    return "ASCII";
  }
  public int flush(  char[] output,  int outStart,  int outEnd){
    byteOff=charOff=0;
    return 0;
  }
  /** 
 * Algorithmic character conversion
 */
  public int convert(  byte[] input,  int inOff,  int inEnd,  char[] output,  int outOff,  int outEnd) throws ConversionBufferFullException, UnknownCharacterException {
    byte inputByte;
    charOff=outOff;
    byteOff=inOff;
    while (byteOff < inEnd) {
      if (charOff >= outEnd)       throw new ConversionBufferFullException();
      inputByte=input[byteOff++];
      if (inputByte >= 0)       output[charOff++]=(char)inputByte;
 else {
        if (subMode)         output[charOff++]='\uFFFD';
 else {
          badInputLength=1;
          throw new UnknownCharacterException();
        }
      }
    }
    return charOff - outOff;
  }
  public void reset(){
    byteOff=charOff=0;
  }
}
