package com.sun.crypto.provider;
import java.io.*;
import sun.security.x509.AlgorithmId;
import sun.security.util.*;
/** 
 * This class implements the <code>EncryptedPrivateKeyInfo</code> type,
 * which is defined in PKCS #8 as follows:
 * <pre>
 * EncryptedPrivateKeyInfo ::=  SEQUENCE {
 * encryptionAlgorithm   AlgorithmIdentifier,
 * encryptedData   OCTET STRING }
 * </pre>
 * @author Jan Luehe
 */
final class EncryptedPrivateKeyInfo {
  private AlgorithmId algid;
  private byte[] encryptedData;
  private byte[] encoded;
  /** 
 * Constructs (i.e., parses) an <code>EncryptedPrivateKeyInfo</code> from
 * its encoding.
 */
  EncryptedPrivateKeyInfo(  byte[] encoded) throws IOException {
    DerValue val=new DerValue(encoded);
    DerValue[] seq=new DerValue[2];
    seq[0]=val.data.getDerValue();
    seq[1]=val.data.getDerValue();
    if (val.data.available() != 0) {
      throw new IOException("overrun, bytes = " + val.data.available());
    }
    this.algid=AlgorithmId.parse(seq[0]);
    if (seq[0].data.available() != 0) {
      throw new IOException("encryptionAlgorithm field overrun");
    }
    this.encryptedData=seq[1].getOctetString();
    if (seq[1].data.available() != 0)     throw new IOException("encryptedData field overrun");
    this.encoded=(byte[])encoded.clone();
  }
  /** 
 * Constructs an <code>EncryptedPrivateKeyInfo</code> from the
 * encryption algorithm and the encrypted data.
 */
  EncryptedPrivateKeyInfo(  AlgorithmId algid,  byte[] encryptedData){
    this.algid=algid;
    this.encryptedData=(byte[])encryptedData.clone();
    this.encoded=null;
  }
  /** 
 * Returns the encryption algorithm.
 */
  AlgorithmId getAlgorithm(){
    return this.algid;
  }
  /** 
 * Returns the encrypted data.
 */
  byte[] getEncryptedData(){
    return (byte[])this.encryptedData.clone();
  }
  /** 
 * Returns the ASN.1 encoding of this class.
 */
  byte[] getEncoded() throws IOException {
    if (this.encoded != null)     return (byte[])this.encoded.clone();
    DerOutputStream out=new DerOutputStream();
    DerOutputStream tmp=new DerOutputStream();
    algid.encode(tmp);
    tmp.putOctetString(encryptedData);
    out.write(DerValue.tag_Sequence,tmp);
    this.encoded=out.toByteArray();
    return (byte[])this.encoded.clone();
  }
}
