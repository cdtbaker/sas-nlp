package sun.io;
/** 
 * @author Limin Shi
 */
public class CharToByteEUC_JP extends CharToByteJIS0208 {
  CharToByteJIS0201 cbJIS0201=new CharToByteJIS0201();
  CharToByteJIS0212 cbJIS0212=new CharToByteJIS0212();
  public String getCharacterEncoding(){
    return "EUC_JP";
  }
  protected int convSingleByte(  char inputChar,  byte[] outputByte){
    byte b;
    if (inputChar == 0) {
      outputByte[0]=(byte)0;
      return 1;
    }
    if ((b=cbJIS0201.getNative(inputChar)) == 0)     return 0;
    if (b > 0 && b < 128) {
      outputByte[0]=b;
      return 1;
    }
    outputByte[0]=(byte)0x8E;
    outputByte[1]=b;
    return 2;
  }
  protected int getNative(  char ch){
    int offset=index1[((ch & 0xff00) >> 8)] << 8;
    int r=index2[offset >> 12].charAt((offset & 0xfff) + (ch & 0xff));
    if (r != 0)     return r + 0x8080;
    r=cbJIS0212.getNative(ch);
    if (r == 0)     return r;
    return r + 0x8F8080;
  }
  /** 
 * Converts characters to sequences of bytes.
 * Conversions that result in Exceptions can be restarted by calling
 * convert again, with appropriately modified parameters.
 * @return the characters written to output.
 * @param input char array containing text in Unicode
 * @param inStart offset in input array
 * @param inEnd offset of last byte to be converted
 * @param output byte array to receive conversion result
 * @param outStart starting offset
 * @param outEnd offset of last byte to be written to
 * @throw UnsupportedCharacterException for any character
 * that cannot be converted to the external character set.
 */
  public int convert(  char[] input,  int inOff,  int inEnd,  byte[] output,  int outOff,  int outEnd) throws MalformedInputException, UnknownCharacterException, ConversionBufferFullException {
    char inputChar;
    byte[] outputByte;
    int inputSize=0;
    int outputSize=0;
    byte[] tmpbuf=new byte[4];
    charOff=inOff;
    byteOff=outOff;
    if (highHalfZoneCode != 0) {
      inputChar=highHalfZoneCode;
      highHalfZoneCode=0;
      if (input[inOff] >= 0xdc00 && input[inOff] <= 0xdfff) {
        badInputLength=1;
        throw new UnknownCharacterException();
      }
 else {
        badInputLength=0;
        throw new MalformedInputException();
      }
    }
    while (charOff < inEnd) {
      inputSize=1;
      outputByte=tmpbuf;
      inputChar=input[charOff];
      if (inputChar >= '\uD800' && inputChar <= '\uDBFF') {
        if (charOff + 1 >= inEnd) {
          highHalfZoneCode=inputChar;
          break;
        }
        inputChar=input[charOff + 1];
        if (inputChar >= '\uDC00' && inputChar <= '\uDFFF') {
          if (subMode) {
            outputByte=subBytes;
            outputSize=subBytes.length;
            inputSize=2;
          }
 else {
            badInputLength=2;
            throw new UnknownCharacterException();
          }
        }
 else {
          badInputLength=1;
          throw new MalformedInputException();
        }
      }
 else       if (inputChar >= '\uDC00' && inputChar <= '\uDFFF') {
        badInputLength=1;
        throw new MalformedInputException();
      }
 else {
        outputSize=convSingleByte(inputChar,outputByte);
        if (outputSize == 0) {
          int ncode=getNative(inputChar);
          if (ncode != 0) {
            if ((ncode & 0xFF0000) == 0) {
              outputByte[0]=(byte)((ncode & 0xff00) >> 8);
              outputByte[1]=(byte)(ncode & 0xff);
              outputSize=2;
            }
 else {
              outputByte[0]=(byte)0x8F;
              outputByte[1]=(byte)((ncode & 0xff00) >> 8);
              outputByte[2]=(byte)(ncode & 0xff);
              outputSize=3;
            }
          }
 else {
            if (subMode) {
              outputByte=subBytes;
              outputSize=subBytes.length;
            }
 else {
              badInputLength=1;
              throw new UnknownCharacterException();
            }
          }
        }
      }
      if (byteOff + outputSize > outEnd)       throw new ConversionBufferFullException();
      for (int i=0; i < outputSize; i++) {
        output[byteOff++]=outputByte[i];
      }
      charOff+=inputSize;
    }
    return byteOff - outOff;
  }
  /** 
 * the maximum number of bytes needed to hold a converted char
 * @returns the maximum number of bytes needed for a converted char
 */
  public int getMaxBytesPerChar(){
    return 3;
  }
}
