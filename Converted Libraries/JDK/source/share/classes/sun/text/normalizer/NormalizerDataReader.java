package sun.text.normalizer;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.IOException;
/** 
 * @author        Ram Viswanadha
 */
final class NormalizerDataReader implements ICUBinary.Authenticate {
  /** 
 * <p>Protected constructor.</p>
 * @param inputStream ICU uprop.dat file input stream
 * @exception IOException throw if data file fails authentication
 * @draft 2.1
 */
  protected NormalizerDataReader(  InputStream inputStream) throws IOException {
    unicodeVersion=ICUBinary.readHeader(inputStream,DATA_FORMAT_ID,this);
    dataInputStream=new DataInputStream(inputStream);
  }
  protected int[] readIndexes(  int length) throws IOException {
    int[] indexes=new int[length];
    for (int i=0; i < length; i++) {
      indexes[i]=dataInputStream.readInt();
    }
    return indexes;
  }
  /** 
 * <p>Reads unorm.icu, parse it into blocks of data to be stored in
 * NormalizerImpl.</P
 * @param normBytes
 * @param fcdBytes
 * @param auxBytes
 * @param extraData
 * @param combiningTable
 * @exception thrown when data reading fails
 * @draft 2.1
 */
  protected void read(  byte[] normBytes,  byte[] fcdBytes,  byte[] auxBytes,  char[] extraData,  char[] combiningTable) throws IOException {
    dataInputStream.readFully(normBytes);
    for (int i=0; i < extraData.length; i++) {
      extraData[i]=dataInputStream.readChar();
    }
    for (int i=0; i < combiningTable.length; i++) {
      combiningTable[i]=dataInputStream.readChar();
    }
    dataInputStream.readFully(fcdBytes);
    dataInputStream.readFully(auxBytes);
  }
  public byte[] getDataFormatVersion(){
    return DATA_FORMAT_VERSION;
  }
  public boolean isDataVersionAcceptable(  byte version[]){
    return version[0] == DATA_FORMAT_VERSION[0] && version[2] == DATA_FORMAT_VERSION[2] && version[3] == DATA_FORMAT_VERSION[3];
  }
  public byte[] getUnicodeVersion(){
    return unicodeVersion;
  }
  /** 
 * ICU data file input stream
 */
  private DataInputStream dataInputStream;
  private byte[] unicodeVersion;
  /** 
 * File format version that this class understands.
 * No guarantees are made if a older version is used
 * see store.c of gennorm for more information and values
 */
  private static final byte DATA_FORMAT_ID[]={(byte)0x4E,(byte)0x6F,(byte)0x72,(byte)0x6D};
  private static final byte DATA_FORMAT_VERSION[]={(byte)0x2,(byte)0x2,(byte)0x5,(byte)0x2};
}
