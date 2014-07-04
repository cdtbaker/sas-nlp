package sun.io;
/** 
 * @author Limin Shi
 */
public abstract class CharToByteDoubleByte extends CharToByteConverter {
  protected short index1[];
  protected String index2[];
  protected char highHalfZoneCode;
  public short[] getIndex1(){
    return index1;
  }
  public String[] getIndex2(){
    return index2;
  }
  public int flush(  byte[] output,  int outStart,  int outEnd) throws MalformedInputException, ConversionBufferFullException {
    if (highHalfZoneCode != 0) {
      highHalfZoneCode=0;
      badInputLength=0;
      throw new MalformedInputException();
    }
    byteOff=charOff=0;
    return 0;
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
    byte[] tmpbuf=new byte[2];
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
            outputByte[0]=(byte)((ncode & 0xff00) >> 8);
            outputByte[1]=(byte)(ncode & 0xff);
            outputSize=2;
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
    return 2;
  }
  /** 
 * Resets the converter.
 * Call this method to reset the converter to its initial state
 */
  public void reset(){
    byteOff=charOff=0;
    highHalfZoneCode=0;
  }
  /** 
 * Return whether a character is mappable or not
 * @return true if a character is mappable
 */
  public boolean canConvert(  char ch){
    byte[] outByte=new byte[2];
    if ((ch == (char)0) || (convSingleByte(ch,outByte) != 0))     return true;
    if (this.getNative(ch) != 0)     return true;
    return false;
  }
  protected int convSingleByte(  char inputChar,  byte[] outputByte){
    if (inputChar < 0x80) {
      outputByte[0]=(byte)(inputChar & 0x7f);
      return 1;
    }
    return 0;
  }
  protected int getNative(  char ch){
    int offset=index1[((ch & 0xff00) >> 8)] << 8;
    return index2[offset >> 12].charAt((offset & 0xfff) + (ch & 0xff));
  }
}
