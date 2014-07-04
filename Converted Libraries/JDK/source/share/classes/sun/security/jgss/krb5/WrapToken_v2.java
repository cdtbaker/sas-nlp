package sun.security.jgss.krb5;
import org.ietf.jgss.*;
import sun.security.jgss.*;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import sun.security.krb5.Confounder;
/** 
 * This class represents the new format of GSS tokens, as specified in RFC
 * 4121, emitted by the GSSContext.wrap() call. It is a MessageToken except
 * that it also contains plaintext or encrypted data at the end. A WrapToken
 * has certain other rules that are peculiar to it and different from a
 * MICToken, which is another type of MessageToken. All data in a WrapToken is
 * prepended by a random confounder of 16 bytes. Thus, all application data
 * is replaced by (confounder || data || tokenHeader || checksum).
 * @author Seema Malkani
 */
class WrapToken_v2 extends MessageToken_v2 {
  byte[] confounder=null;
  private final boolean privacy;
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
  public WrapToken_v2(  Krb5Context context,  byte[] tokenBytes,  int tokenOffset,  int tokenLen,  MessageProp prop) throws GSSException {
    super(Krb5Token.WRAP_ID_v2,context,tokenBytes,tokenOffset,tokenLen,prop);
    this.privacy=prop.getPrivacy();
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
  public WrapToken_v2(  Krb5Context context,  InputStream is,  MessageProp prop) throws GSSException {
    super(Krb5Token.WRAP_ID_v2,context,is,prop);
    this.privacy=prop.getPrivacy();
  }
  /** 
 * Obtains the application data that was transmitted in this
 * WrapToken.
 * @return a byte array containing the application data
 * @throws GSSException if an error occurs while decrypting any
 * cipher text and checking for validity
 */
  public byte[] getData() throws GSSException {
    byte[] temp=new byte[tokenDataLen];
    int len=getData(temp,0);
    return Arrays.copyOf(temp,len);
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
    if (privacy) {
      cipherHelper.decryptData(this,tokenData,0,tokenDataLen,dataBuf,dataBufOffset,getKeyUsage());
      return tokenDataLen - CONFOUNDER_SIZE - TOKEN_HEADER_SIZE- cipherHelper.getChecksumLength();
    }
 else {
      int data_length=tokenDataLen - cipherHelper.getChecksumLength();
      System.arraycopy(tokenData,0,dataBuf,dataBufOffset,data_length);
      if (!verifySign(dataBuf,dataBufOffset,data_length)) {
        throw new GSSException(GSSException.BAD_MIC,-1,"Corrupt checksum in Wrap token");
      }
      return data_length;
    }
  }
  /** 
 * Writes a WrapToken_v2 object
 */
  public WrapToken_v2(  Krb5Context context,  MessageProp prop,  byte[] dataBytes,  int dataOffset,  int dataLen) throws GSSException {
    super(Krb5Token.WRAP_ID_v2,context);
    confounder=Confounder.bytes(CONFOUNDER_SIZE);
    genSignAndSeqNumber(prop,dataBytes,dataOffset,dataLen);
    if (!context.getConfState())     prop.setPrivacy(false);
    privacy=prop.getPrivacy();
    if (!privacy) {
      tokenData=new byte[dataLen + checksum.length];
      System.arraycopy(dataBytes,dataOffset,tokenData,0,dataLen);
      System.arraycopy(checksum,0,tokenData,dataLen,checksum.length);
    }
 else {
      tokenData=cipherHelper.encryptData(this,confounder,getTokenHeader(),dataBytes,dataOffset,dataLen,getKeyUsage());
    }
  }
  public void encode(  OutputStream os) throws IOException {
    encodeHeader(os);
    os.write(tokenData);
  }
  public byte[] encode() throws IOException {
    ByteArrayOutputStream bos=new ByteArrayOutputStream(MessageToken_v2.TOKEN_HEADER_SIZE + tokenData.length);
    encode(bos);
    return bos.toByteArray();
  }
  public int encode(  byte[] outToken,  int offset) throws IOException {
    byte[] token=encode();
    System.arraycopy(token,0,outToken,offset,token.length);
    return token.length;
  }
  static int getSizeLimit(  int qop,  boolean confReq,  int maxTokenSize,  CipherHelper ch) throws GSSException {
    return (GSSHeader.getMaxMechTokenSize(OID,maxTokenSize) - (TOKEN_HEADER_SIZE + ch.getChecksumLength() + CONFOUNDER_SIZE) - 8);
  }
}
