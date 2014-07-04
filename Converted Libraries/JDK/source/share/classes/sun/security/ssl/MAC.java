package sun.security.ssl;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.nio.ByteBuffer;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import sun.security.ssl.CipherSuite.MacAlg;
import static sun.security.ssl.CipherSuite.*;
/** 
 * This class computes the "Message Authentication Code" (MAC) for each
 * SSL message.  This is essentially a shared-secret signature, used to
 * provide integrity protection for SSL messages.  The MAC is actually
 * one of several keyed hashes, as associated with the cipher suite and
 * protocol version.  (SSL v3.0 uses one construct, TLS uses another.)
 * <P>NOTE: MAC computation is the only place in the SSL protocol that the
 * sequence number is used.  It's also reset to zero with each change of
 * a cipher spec, so this is the only place this state is needed.
 * @author David Brownell
 * @author Andreas Sterbenz
 */
final class MAC {
  final static MAC NULL=new MAC();
  private static final byte nullMAC[]=new byte[0];
  private final MacAlg macAlg;
  private final int macSize;
  private final Mac mac;
  private final byte[] block;
  private static final int BLOCK_SIZE_SSL=8 + 1 + 2;
  private static final int BLOCK_SIZE_TLS=8 + 1 + 2+ 2;
  private static final int BLOCK_OFFSET_TYPE=8;
  private static final int BLOCK_OFFSET_VERSION=8 + 1;
  private MAC(){
    macSize=0;
    macAlg=M_NULL;
    mac=null;
    block=null;
  }
  /** 
 * Set up, configured for the given SSL/TLS MAC type and version.
 */
  MAC(  MacAlg macAlg,  ProtocolVersion protocolVersion,  SecretKey key) throws NoSuchAlgorithmException, InvalidKeyException {
    this.macAlg=macAlg;
    this.macSize=macAlg.size;
    String algorithm;
    boolean tls=(protocolVersion.v >= ProtocolVersion.TLS10.v);
    if (macAlg == M_MD5) {
      algorithm=tls ? "HmacMD5" : "SslMacMD5";
    }
 else     if (macAlg == M_SHA) {
      algorithm=tls ? "HmacSHA1" : "SslMacSHA1";
    }
 else     if (macAlg == M_SHA256) {
      algorithm="HmacSHA256";
    }
 else     if (macAlg == M_SHA384) {
      algorithm="HmacSHA384";
    }
 else {
      throw new RuntimeException("Unknown Mac " + macAlg);
    }
    mac=JsseJce.getMac(algorithm);
    mac.init(key);
    if (tls) {
      block=new byte[BLOCK_SIZE_TLS];
      block[BLOCK_OFFSET_VERSION]=protocolVersion.major;
      block[BLOCK_OFFSET_VERSION + 1]=protocolVersion.minor;
    }
 else {
      block=new byte[BLOCK_SIZE_SSL];
    }
  }
  /** 
 * Returns the length of the MAC.
 */
  int MAClen(){
    return macSize;
  }
  /** 
 * Computes and returns the MAC for the data in this byte array.
 * @param type record type
 * @param buf compressed record on which the MAC is computed
 * @param offset start of compressed record data
 * @param len the size of the compressed record
 */
  final byte[] compute(  byte type,  byte buf[],  int offset,  int len){
    return compute(type,null,buf,offset,len);
  }
  /** 
 * Compute and returns the MAC for the remaining data
 * in this ByteBuffer.
 * On return, the bb position == limit, and limit will
 * have not changed.
 * @param type record type
 * @param bb a ByteBuffer in which the position and limit
 * demarcate the data to be MAC'd.
 */
  final byte[] compute(  byte type,  ByteBuffer bb){
    return compute(type,bb,null,0,bb.remaining());
  }
  /** 
 * Check whether the sequence number is close to wrap
 * Sequence numbers are of type uint64 and may not exceed 2^64-1.
 * Sequence numbers do not wrap. When the sequence number is near
 * to wrap, we need to close the connection immediately.
 */
  final boolean seqNumOverflow(){
    return (block != null && mac != null && block[0] == 0xFF && block[1] == 0xFF && block[2] == 0xFF && block[3] == 0xFF && block[4] == 0xFF && block[5] == 0xFF && block[6] == 0xFF);
  }
  final boolean seqNumIsHuge(){
    return (block != null && mac != null && block[0] == 0xFF && block[1] == 0xFF);
  }
  private void incrementSequenceNumber(){
    int k=7;
    while ((k >= 0) && (++block[k] == 0)) {
      k--;
    }
  }
  private byte[] compute(  byte type,  ByteBuffer bb,  byte[] buf,  int offset,  int len){
    if (macSize == 0) {
      return nullMAC;
    }
    block[BLOCK_OFFSET_TYPE]=type;
    block[block.length - 2]=(byte)(len >> 8);
    block[block.length - 1]=(byte)(len);
    mac.update(block);
    incrementSequenceNumber();
    if (bb != null) {
      mac.update(bb);
    }
 else {
      mac.update(buf,offset,len);
    }
    return mac.doFinal();
  }
}
