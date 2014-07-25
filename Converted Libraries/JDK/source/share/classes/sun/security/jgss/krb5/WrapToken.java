package sun.security.jgss.krb5;
import org.ietf.jgss.*;
import sun.security.jgss.*;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import sun.security.krb5.Confounder;
/** 
 * This class represents a token emitted by the GSSContext.wrap()
 * call. It is a MessageToken except that it also contains plaintext
 * or encrypted data at the end. A wrapToken has certain other rules
 * that are peculiar to it and different from a MICToken, which is
 * another type of MessageToken. All data in a WrapToken is prepended
 * by a random counfounder of 8 bytes. All data in a WrapToken is
 * also padded with one to eight bytes where all bytes are equal in
 * value to the number of bytes being padded. Thus, all application
 * data is replaced by (confounder || data || padding).
 * @author Mayank Upadhyay
 */
class WrapToken extends MessageToken {
  /** 
 * The size of the random confounder used in a WrapToken.
 */
  static final int CONFOUNDER_SIZE=8;
  static final byte[][] pads={null,{0x01},{0x02,0x02},{0x03,0x03,0x03},{0x04,0x04,0x04,0x04},{0x05,0x05,0x05,0x05,0x05},{0x06,0x06,0x06,0x06,0x06,0x06},{0x07,0x07,0x07,0x07,0x07,0x07,0x07},{0x08,0x08,0x08,0x08,0x08,0x08,0x08,0x08}};
  private boolean readTokenFromInputStream=true;
  private InputStream is=null;
  private byte[] tokenBytes=null;
  private int tokenOffset=0;
  private int tokenLen=0;
  private byte[] dataBytes=null;
  private int dataOffset=0;
  private int dataLen=0;
  private int dataSize=0;
  byte[] confounder=null;
  byte[] padding=null;
  private boolean privacy=false;
  /** 
 * Constructs a WrapToken from token bytes obtained from the
 * peer.
 * @param context the mechanism context associated with this
 * token
 * @param tokenBytes the bytes of the token
 * @param tokenOffset the offset of the token
 * @param tokenLen the length of the token
 * @param prop the MessageProp into which characteristics of the
 * parsed token will be stored.
 * @throws GSSException if the token is defective
 */
  public WrapToken(  Krb5Context context,  byte[] tokenBytes,  int tokenOffset,  int tokenLen,  MessageProp prop) throws GSSException {
    super(Krb5Token.WRAP_ID,context,tokenBytes,tokenOffset,tokenLen,prop);
    this.readTokenFromInputStream=false;
    this.tokenBytes=tokenBytes;
    this.tokenOffset=tokenOffset;
    this.tokenLen=tokenLen;
    this.privacy=prop.getPrivacy();
    dataSize=getGSSHeader().getMechTokenLength() - getKrb5TokenSize();
  }
  /** 
 * Constructs a WrapToken from token bytes read on the fly from
 * an InputStream.
 * @param context the mechanism context associated with this
 * token
 * @param is the InputStream containing the token bytes
 * @param prop the MessageProp into which characteristics of the
 * parsed token will be stored.
 * @throws GSSException if the token is defective or if there is
 * a problem reading from the InputStream
 */
  public WrapToken(  Krb5Context context,  InputStream is,  MessageProp prop) throws GSSException {
    super(Krb5Token.WRAP_ID,context,is,prop);
    this.is=is;
    this.privacy=prop.getPrivacy();
    dataSize=getGSSHeader().getMechTokenLength() - getTokenSize();
  }
  /** 
 * Obtains the application data that was transmitted in this
 * WrapToken.
 * @return a byte array containing the application data
 * @throws GSSException if an error occurs while decrypting any
 * cipher text and checking for validity
 */
  public byte[] getData() throws GSSException {
    byte[] temp=new byte[dataSize];
    getData(temp,0);
    byte[] retVal=new byte[dataSize - confounder.length - padding.length];
    System.arraycopy(temp,0,retVal,0,retVal.length);
    return retVal;
  }
  /** 
 * Obtains the application data that was transmitted in this
 * WrapToken, writing it into an application provided output
 * array.
 * @param dataBuf the output buffer into which the data must be
 * written
 * @param dataBufOffset the offset at which to write the data
 * @return the size of the data written
 * @throws GSSException if an error occurs while decrypting any
 * cipher text and checking for validity
 */
  public int getData(  byte[] dataBuf,  int dataBufOffset) throws GSSException {
    if (readTokenFromInputStream)     getDataFromStream(dataBuf,dataBufOffset);
 else     getDataFromBuffer(dataBuf,dataBufOffset);
    return (dataSize - confounder.length - padding.length);
  }
  /** 
 * Helper routine to obtain the application data transmitted in
 * this WrapToken. It is called if the WrapToken was constructed
 * with a byte array as input.
 * @param dataBuf the output buffer into which the data must be
 * written
 * @param dataBufOffset the offset at which to write the data
 * @throws GSSException if an error occurs while decrypting any
 * cipher text and checking for validity
 */
  private void getDataFromBuffer(  byte[] dataBuf,  int dataBufOffset) throws GSSException {
    GSSHeader gssHeader=getGSSHeader();
    int dataPos=tokenOffset + gssHeader.getLength() + getTokenSize();
    if (dataPos + dataSize > tokenOffset + tokenLen)     throw new GSSException(GSSException.DEFECTIVE_TOKEN,-1,"Insufficient data in " + getTokenName(getTokenId()));
    confounder=new byte[CONFOUNDER_SIZE];
    if (privacy) {
      cipherHelper.decryptData(this,tokenBytes,dataPos,dataSize,dataBuf,dataBufOffset);
    }
 else {
      System.arraycopy(tokenBytes,dataPos,confounder,0,CONFOUNDER_SIZE);
      int padSize=tokenBytes[dataPos + dataSize - 1];
      if (padSize < 0)       padSize=0;
      if (padSize > 8)       padSize%=8;
      padding=pads[padSize];
      System.arraycopy(tokenBytes,dataPos + CONFOUNDER_SIZE,dataBuf,dataBufOffset,dataSize - CONFOUNDER_SIZE - padSize);
    }
    if (!verifySignAndSeqNumber(confounder,dataBuf,dataBufOffset,dataSize - CONFOUNDER_SIZE - padding.length,padding))     throw new GSSException(GSSException.BAD_MIC,-1,"Corrupt checksum or sequence number in Wrap token");
  }
  /** 
 * Helper routine to obtain the application data transmitted in
 * this WrapToken. It is called if the WrapToken was constructed
 * with an Inputstream.
 * @param dataBuf the output buffer into which the data must be
 * written
 * @param dataBufOffset the offset at which to write the data
 * @throws GSSException if an error occurs while decrypting any
 * cipher text and checking for validity
 */
  private void getDataFromStream(  byte[] dataBuf,  int dataBufOffset) throws GSSException {
    GSSHeader gssHeader=getGSSHeader();
    confounder=new byte[CONFOUNDER_SIZE];
    try {
      if (privacy) {
        cipherHelper.decryptData(this,is,dataSize,dataBuf,dataBufOffset);
      }
 else {
        readFully(is,confounder);
        if (cipherHelper.isArcFour()) {
          padding=pads[1];
          readFully(is,dataBuf,dataBufOffset,dataSize - CONFOUNDER_SIZE - 1);
        }
 else {
          int numBlocks=(dataSize - CONFOUNDER_SIZE) / 8 - 1;
          int offset=dataBufOffset;
          for (int i=0; i < numBlocks; i++) {
            readFully(is,dataBuf,offset,8);
            offset+=8;
          }
          byte[] finalBlock=new byte[8];
          readFully(is,finalBlock);
          int padSize=finalBlock[7];
          padding=pads[padSize];
          System.arraycopy(finalBlock,0,dataBuf,offset,finalBlock.length - padSize);
        }
      }
    }
 catch (    IOException e) {
      throw new GSSException(GSSException.DEFECTIVE_TOKEN,-1,getTokenName(getTokenId()) + ": " + e.getMessage());
    }
    if (!verifySignAndSeqNumber(confounder,dataBuf,dataBufOffset,dataSize - CONFOUNDER_SIZE - padding.length,padding))     throw new GSSException(GSSException.BAD_MIC,-1,"Corrupt checksum or sequence number in Wrap token");
  }
  /** 
 * Helper routine to pick the right padding for a certain length
 * of application data. Every application message has some
 * padding between 1 and 8 bytes.
 * @param len the length of the application data
 * @return the padding to be applied
 */
  private byte[] getPadding(  int len){
    int padSize=0;
    if (cipherHelper.isArcFour()) {
      padSize=1;
    }
 else {
      padSize=len % 8;
      padSize=8 - padSize;
    }
    return pads[padSize];
  }
  public WrapToken(  Krb5Context context,  MessageProp prop,  byte[] dataBytes,  int dataOffset,  int dataLen) throws GSSException {
    super(Krb5Token.WRAP_ID,context);
    confounder=Confounder.bytes(CONFOUNDER_SIZE);
    padding=getPadding(dataLen);
    dataSize=confounder.length + dataLen + padding.length;
    this.dataBytes=dataBytes;
    this.dataOffset=dataOffset;
    this.dataLen=dataLen;
    genSignAndSeqNumber(prop,confounder,dataBytes,dataOffset,dataLen,padding);
    if (!context.getConfState())     prop.setPrivacy(false);
    privacy=prop.getPrivacy();
  }
  public void encode(  OutputStream os) throws IOException, GSSException {
    super.encode(os);
    if (!privacy) {
      os.write(confounder);
      os.write(dataBytes,dataOffset,dataLen);
      os.write(padding);
    }
 else {
      cipherHelper.encryptData(this,confounder,dataBytes,dataOffset,dataLen,padding,os);
    }
  }
  public byte[] encode() throws IOException, GSSException {
    ByteArrayOutputStream bos=new ByteArrayOutputStream(dataSize + 50);
    encode(bos);
    return bos.toByteArray();
  }
  public int encode(  byte[] outToken,  int offset) throws IOException, GSSException {
    ByteArrayOutputStream bos=new ByteArrayOutputStream();
    super.encode(bos);
    byte[] header=bos.toByteArray();
    System.arraycopy(header,0,outToken,offset,header.length);
    offset+=header.length;
    if (!privacy) {
      System.arraycopy(confounder,0,outToken,offset,confounder.length);
      offset+=confounder.length;
      System.arraycopy(dataBytes,dataOffset,outToken,offset,dataLen);
      offset+=dataLen;
      System.arraycopy(padding,0,outToken,offset,padding.length);
    }
 else {
      cipherHelper.encryptData(this,confounder,dataBytes,dataOffset,dataLen,padding,outToken,offset);
    }
    return (header.length + confounder.length + dataLen+ padding.length);
  }
  protected int getKrb5TokenSize() throws GSSException {
    return (getTokenSize() + dataSize);
  }
  protected int getSealAlg(  boolean conf,  int qop) throws GSSException {
    if (!conf) {
      return SEAL_ALG_NONE;
    }
    return cipherHelper.getSealAlg();
  }
  static int getSizeLimit(  int qop,  boolean confReq,  int maxTokenSize,  CipherHelper ch) throws GSSException {
    return (GSSHeader.getMaxMechTokenSize(OID,maxTokenSize) - (getTokenSize(ch) + CONFOUNDER_SIZE) - 8);
  }
}
