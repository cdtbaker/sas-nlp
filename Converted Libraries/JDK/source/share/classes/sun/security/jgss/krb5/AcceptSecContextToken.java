package sun.security.jgss.krb5;
import org.ietf.jgss.*;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import sun.security.krb5.*;
class AcceptSecContextToken extends InitialToken {
  private KrbApRep apRep=null;
  /** 
 * Creates an AcceptSecContextToken for the context acceptor to send to
 * the context initiator.
 */
  public AcceptSecContextToken(  Krb5Context context,  KrbApReq apReq) throws KrbException, IOException {
    boolean useSubkey=false;
    boolean useSequenceNumber=true;
    apRep=new KrbApRep(apReq,useSequenceNumber,useSubkey);
    context.resetMySequenceNumber(apRep.getSeqNumber().intValue());
  }
  /** 
 * Creates an AcceptSecContextToken at the context initiator's side
 * using the bytes received from  the acceptor.
 */
  public AcceptSecContextToken(  Krb5Context context,  Credentials serviceCreds,  KrbApReq apReq,  InputStream is) throws IOException, GSSException, KrbException {
    int tokenId=((is.read() << 8) | is.read());
    if (tokenId != Krb5Token.AP_REP_ID)     throw new GSSException(GSSException.DEFECTIVE_TOKEN,-1,"AP_REP token id does not match!");
    byte[] apRepBytes=new sun.security.util.DerValue(is).toByteArray();
    KrbApRep apRep=new KrbApRep(apRepBytes,serviceCreds,apReq);
    EncryptionKey subKey=apRep.getSubKey();
    if (subKey != null) {
      context.setKey(subKey);
    }
    Integer apRepSeqNumber=apRep.getSeqNumber();
    int peerSeqNumber=(apRepSeqNumber != null ? apRepSeqNumber.intValue() : 0);
    context.resetPeerSequenceNumber(peerSeqNumber);
  }
  public final byte[] encode() throws IOException {
    byte[] apRepBytes=apRep.getMessage();
    byte[] retVal=new byte[2 + apRepBytes.length];
    writeInt(Krb5Token.AP_REP_ID,retVal,0);
    System.arraycopy(apRepBytes,0,retVal,2,apRepBytes.length);
    return retVal;
  }
}
