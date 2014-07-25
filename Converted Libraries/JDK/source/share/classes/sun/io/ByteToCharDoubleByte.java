package sun.io;
/** 
 * @author Limin Shi
 * Sean Jiang
 */
public abstract class ByteToCharDoubleByte extends ByteToCharConverter {
  protected byte savedByte;
  protected short index1[];
  protected String index2[];
  protected int start;
  protected int end;
  protected int badInputLength;
  public ByteToCharDoubleByte(){
    super();
    savedByte=0;
  }
  public short[] getIndex1(){
    return (index1);
  }
  public String[] getIndex2(){
    return (index2);
  }
  public int flush(  char[] output,  int outStart,  int outEnd) throws MalformedInputException {
    if (savedByte != 0) {
      reset();
      badInputLength=0;
      throw new MalformedInputException();
    }
    reset();
    return 0;
  }
  /** 
 * Converts sequences of bytes to characters.
 * Conversions that result in Exceptions can be restarted by calling
 * convert again, with appropriately modified parameters.
 * @return the characters written to output.
 * @param input byte array containing text in Double/single Byte
 * @param inStart offset in input array
 * @param inEnd offset of last byte to be converted
 * @param output character array to receive conversion result
 * @param outStart starting offset
 * @param outEnd offset of last byte to be written to
 * @throw UnsupportedCharacterException for any bytes
 * that cannot be converted to the external character set.
 */
  public int convert(  byte[] input,  int inOff,  int inEnd,  char[] output,  int outOff,  int outEnd) throws UnknownCharacterException, MalformedInputException, ConversionBufferFullException {
    char outputChar=REPLACE_CHAR;
    int inputSize=0;
    charOff=outOff;
    byteOff=inOff;
    while (byteOff < inEnd) {
      int byte1, byte2;
      if (savedByte == 0) {
        byte1=input[byteOff];
        inputSize=1;
      }
 else {
        byte1=savedByte;
        savedByte=0;
        inputSize=0;
      }
      outputChar=convSingleByte(byte1);
      if (outputChar == REPLACE_CHAR) {
        if (byteOff + inputSize >= inEnd) {
          savedByte=(byte)byte1;
          byteOff+=inputSize;
          break;
        }
        byte1&=0xff;
        byte2=input[byteOff + inputSize] & 0xff;
        inputSize++;
        outputChar=getUnicode(byte1,byte2);
      }
      if (outputChar == REPLACE_CHAR) {
        if (subMode)         outputChar=subChars[0];
 else {
          badInputLength=inputSize;
          throw new UnknownCharacterException();
        }
      }
      if (charOff >= outEnd)       throw new ConversionBufferFullException();
      output[charOff++]=outputChar;
      byteOff+=inputSize;
    }
    return charOff - outOff;
  }
  /** 
 * Resets the converter.
 * Call this method to reset the converter to its initial state
 */
  public void reset(){
    byteOff=charOff=0;
    savedByte=0;
  }
  protected char convSingleByte(  int b){
    if (b >= 0)     return (char)b;
    return REPLACE_CHAR;
  }
  protected char getUnicode(  int byte1,  int byte2){
    if (((byte1 < 0) || (byte1 > index1.length)) || ((byte2 < start) || (byte2 > end)))     return REPLACE_CHAR;
    int n=(index1[byte1] & 0xf) * (end - start + 1) + (byte2 - start);
    return index2[index1[byte1] >> 4].charAt(n);
  }
  protected final static char REPLACE_CHAR='\uFFFD';
}
