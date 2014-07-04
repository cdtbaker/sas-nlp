package com.sun.crypto.provider;
import java.math.*;
import java.io.*;
import sun.security.x509.AlgorithmId;
import sun.security.util.*;
/** 
 * This class implements the <code>PrivateKeyInfo</code> type,
 * which is defined in PKCS #8 as follows:
 * <pre>
 * PrivateKeyInfo ::=  SEQUENCE {
 * version   INTEGER,
 * privateKeyAlgorithm   AlgorithmIdentifier,
 * privateKey   OCTET STRING,
 * attributes   [0] IMPLICIT Attributes OPTIONAL }
 * </pre>
 * @author Jan Luehe
 */
final class PrivateKeyInfo {
  private static final BigInteger VERSION=BigInteger.ZERO;
  private AlgorithmId algid;
  private byte[] privkey;
  /** 
 * Constructs a PKCS#8 PrivateKeyInfo from its ASN.1 encoding.
 */
  PrivateKeyInfo(  byte[] encoded) throws IOException {
    DerValue val=new DerValue(encoded);
    if (val.tag != DerValue.tag_Sequence)     throw new IOException("private key parse error: not a sequence");
    BigInteger parsedVersion=val.data.getBigInteger();
    if (!parsedVersion.equals(VERSION)) {
      throw new IOException("version mismatch: (supported: " + VERSION + ", parsed: "+ parsedVersion);
    }
    this.algid=AlgorithmId.parse(val.data.getDerValue());
    this.privkey=val.data.getOctetString();
  }
  /** 
 * Returns the private-key algorithm.
 */
  AlgorithmId getAlgorithm(){
    return this.algid;
  }
}
