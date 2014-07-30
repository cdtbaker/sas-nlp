package sun.security.util;
import java.io.IOException;
import java.util.ArrayList;
/** 
 * A package private utility class to convert indefinite length DER
 * encoded byte arrays to definite length DER encoded byte arrays.
 * This assumes that the basic data structure is "tag, length, value"
 * triplet. In the case where the length is "indefinite", terminating
 * end-of-contents bytes are expected.
 * @author Hemma Prafullchandra
 */
class DerIndefLenConverter {
  private static final int TAG_MASK=0x1f;
  private static final int FORM_MASK=0x20;
  private static final int CLASS_MASK=0xC0;
  private static final int LEN_LONG=0x80;
  private static final int LEN_MASK=0x7f;
  private static final int SKIP_EOC_BYTES=2;
  private byte[] data, newData;
  private int newDataPos, dataPos, dataSize, index;
  private int unresolved=0;
  private ArrayList<Object> ndefsList=new ArrayList<Object>();
  private int numOfTotalLenBytes=0;
  private boolean isEOC(  int tag){
    return (((tag & TAG_MASK) == 0x00) && ((tag & FORM_MASK) == 0x00) && ((tag & CLASS_MASK) == 0x00));
  }
  static boolean isLongForm(  int lengthByte){
    return ((lengthByte & LEN_LONG) == LEN_LONG);
  }
  DerIndefLenConverter(){
  }
  /** 
 * Checks whether the given length byte is of the form
 * <em>Indefinite</em>.
 * @param lengthByte the length byte from a DER encoded
 * object.
 * @return true if the byte is of Indefinite form otherwise
 * returns false.
 */
  static boolean isIndefinite(  int lengthByte){
    return (isLongForm(lengthByte) && ((lengthByte & LEN_MASK) == 0));
  }
  /** 
 * Parse the tag and if it is an end-of-contents tag then
 * add the current position to the <code>eocList</code> vector.
 */
  private void parseTag() throws IOException {
    if (dataPos == dataSize)     return;
    if (isEOC(data[dataPos]) && (data[dataPos + 1] == 0)) {
      int numOfEncapsulatedLenBytes=0;
      Object elem=null;
      int index;
      for (index=ndefsList.size() - 1; index >= 0; index--) {
        elem=ndefsList.get(index);
        if (elem instanceof Integer) {
          break;
        }
 else {
          numOfEncapsulatedLenBytes+=((byte[])elem).length - 3;
        }
      }
      if (index < 0) {
        throw new IOException("EOC does not have matching " + "indefinite-length tag");
      }
      int sectionLen=dataPos - ((Integer)elem).intValue() + numOfEncapsulatedLenBytes;
      byte[] sectionLenBytes=getLengthBytes(sectionLen);
      ndefsList.set(index,sectionLenBytes);
      unresolved--;
      numOfTotalLenBytes+=(sectionLenBytes.length - 3);
    }
    dataPos++;
  }
  /** 
 * Write the tag and if it is an end-of-contents tag
 * then skip the tag and its 1 byte length of zero.
 */
  private void writeTag(){
    if (dataPos == dataSize)     return;
    int tag=data[dataPos++];
    if (isEOC(tag) && (data[dataPos] == 0)) {
      dataPos++;
      writeTag();
    }
 else     newData[newDataPos++]=(byte)tag;
  }
  /** 
 * Parse the length and if it is an indefinite length then add
 * the current position to the <code>ndefsList</code> vector.
 */
  private int parseLength() throws IOException {
    int curLen=0;
    if (dataPos == dataSize)     return curLen;
    int lenByte=data[dataPos++] & 0xff;
    if (isIndefinite(lenByte)) {
      ndefsList.add(new Integer(dataPos));
      unresolved++;
      return curLen;
    }
    if (isLongForm(lenByte)) {
      lenByte&=LEN_MASK;
      if (lenByte > 4)       throw new IOException("Too much data");
      if ((dataSize - dataPos) < (lenByte + 1))       throw new IOException("Too little data");
      for (int i=0; i < lenByte; i++)       curLen=(curLen << 8) + (data[dataPos++] & 0xff);
    }
 else {
      curLen=(lenByte & LEN_MASK);
    }
    return curLen;
  }
  /** 
 * Write the length and if it is an indefinite length
 * then calculate the definite length from the positions
 * of the indefinite length and its matching EOC terminator.
 * Then, write the value.
 */
  private void writeLengthAndValue() throws IOException {
    if (dataPos == dataSize)     return;
    int curLen=0;
    int lenByte=data[dataPos++] & 0xff;
    if (isIndefinite(lenByte)) {
      byte[] lenBytes=(byte[])ndefsList.get(index++);
      System.arraycopy(lenBytes,0,newData,newDataPos,lenBytes.length);
      newDataPos+=lenBytes.length;
      return;
    }
    if (isLongForm(lenByte)) {
      lenByte&=LEN_MASK;
      for (int i=0; i < lenByte; i++)       curLen=(curLen << 8) + (data[dataPos++] & 0xff);
    }
 else     curLen=(lenByte & LEN_MASK);
    writeLength(curLen);
    writeValue(curLen);
  }
  private void writeLength(  int curLen){
    if (curLen < 128) {
      newData[newDataPos++]=(byte)curLen;
    }
 else     if (curLen < (1 << 8)) {
      newData[newDataPos++]=(byte)0x81;
      newData[newDataPos++]=(byte)curLen;
    }
 else     if (curLen < (1 << 16)) {
      newData[newDataPos++]=(byte)0x82;
      newData[newDataPos++]=(byte)(curLen >> 8);
      newData[newDataPos++]=(byte)curLen;
    }
 else     if (curLen < (1 << 24)) {
      newData[newDataPos++]=(byte)0x83;
      newData[newDataPos++]=(byte)(curLen >> 16);
      newData[newDataPos++]=(byte)(curLen >> 8);
      newData[newDataPos++]=(byte)curLen;
    }
 else {
      newData[newDataPos++]=(byte)0x84;
      newData[newDataPos++]=(byte)(curLen >> 24);
      newData[newDataPos++]=(byte)(curLen >> 16);
      newData[newDataPos++]=(byte)(curLen >> 8);
      newData[newDataPos++]=(byte)curLen;
    }
  }
  private byte[] getLengthBytes(  int curLen){
    byte[] lenBytes;
    int index=0;
    if (curLen < 128) {
      lenBytes=new byte[1];
      lenBytes[index++]=(byte)curLen;
    }
 else     if (curLen < (1 << 8)) {
      lenBytes=new byte[2];
      lenBytes[index++]=(byte)0x81;
      lenBytes[index++]=(byte)curLen;
    }
 else     if (curLen < (1 << 16)) {
      lenBytes=new byte[3];
      lenBytes[index++]=(byte)0x82;
      lenBytes[index++]=(byte)(curLen >> 8);
      lenBytes[index++]=(byte)curLen;
    }
 else     if (curLen < (1 << 24)) {
      lenBytes=new byte[4];
      lenBytes[index++]=(byte)0x83;
      lenBytes[index++]=(byte)(curLen >> 16);
      lenBytes[index++]=(byte)(curLen >> 8);
      lenBytes[index++]=(byte)curLen;
    }
 else {
      lenBytes=new byte[5];
      lenBytes[index++]=(byte)0x84;
      lenBytes[index++]=(byte)(curLen >> 24);
      lenBytes[index++]=(byte)(curLen >> 16);
      lenBytes[index++]=(byte)(curLen >> 8);
      lenBytes[index++]=(byte)curLen;
    }
    return lenBytes;
  }
  private int getNumOfLenBytes(  int len){
    int numOfLenBytes=0;
    if (len < 128) {
      numOfLenBytes=1;
    }
 else     if (len < (1 << 8)) {
      numOfLenBytes=2;
    }
 else     if (len < (1 << 16)) {
      numOfLenBytes=3;
    }
 else     if (len < (1 << 24)) {
      numOfLenBytes=4;
    }
 else {
      numOfLenBytes=5;
    }
    return numOfLenBytes;
  }
  /** 
 * Parse the value;
 */
  private void parseValue(  int curLen){
    dataPos+=curLen;
  }
  /** 
 * Write the value;
 */
  private void writeValue(  int curLen){
    for (int i=0; i < curLen; i++)     newData[newDataPos++]=data[dataPos++];
  }
  /** 
 * Converts a indefinite length DER encoded byte array to
 * a definte length DER encoding.
 * @param indefData the byte array holding the indefinite
 * length encoding.
 * @return the byte array containing the definite length
 * DER encoding.
 * @exception IOException on parsing or re-writing errors.
 */
  byte[] convert(  byte[] indefData) throws IOException {
    data=indefData;
    dataPos=0;
    index=0;
    dataSize=data.length;
    int len=0;
    int unused=0;
    while (dataPos < dataSize) {
      parseTag();
      len=parseLength();
      parseValue(len);
      if (unresolved == 0) {
        unused=dataSize - dataPos;
        dataSize=dataPos;
        break;
      }
    }
    newData=new byte[dataSize + numOfTotalLenBytes + unused];
    dataPos=0;
    newDataPos=0;
    index=0;
    while (dataPos < dataSize) {
      writeTag();
      writeLengthAndValue();
    }
    System.arraycopy(indefData,dataSize,newData,dataSize + numOfTotalLenBytes,unused);
    return newData;
  }
}