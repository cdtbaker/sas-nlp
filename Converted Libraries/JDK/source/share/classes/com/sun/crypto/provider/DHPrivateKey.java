package com.sun.crypto.provider;
import java.io.*;
import java.math.BigInteger;
import java.security.KeyRep;
import java.security.PrivateKey;
import java.security.InvalidKeyException;
import java.security.ProviderException;
import javax.crypto.*;
import javax.crypto.spec.DHParameterSpec;
import sun.security.util.*;
/** 
 * A private key in PKCS#8 format for the Diffie-Hellman key agreement
 * algorithm.
 * @author Jan Luehe
 * @see DHPublicKey
 * @see java.security.KeyAgreement
 */
final class DHPrivateKey implements PrivateKey, javax.crypto.interfaces.DHPrivateKey, Serializable {
  static final long serialVersionUID=7565477590005668886L;
  private static final BigInteger PKCS8_VERSION=BigInteger.ZERO;
  private BigInteger x;
  private byte[] key;
  private byte[] encodedKey;
  private BigInteger p;
  private BigInteger g;
  private int l;
  private int DH_data[]={1,2,840,113549,1,3,1};
  /** 
 * Make a DH private key out of a private value <code>x</code>, a prime
 * modulus <code>p</code>, and a base generator <code>g</code>.
 * @param x the private value
 * @param p the prime modulus
 * @param g the base generator
 * @exception ProviderException if the key cannot be encoded
 */
  DHPrivateKey(  BigInteger x,  BigInteger p,  BigInteger g) throws InvalidKeyException {
    this(x,p,g,0);
  }
  /** 
 * Make a DH private key out of a private value <code>x</code>, a prime
 * modulus <code>p</code>, a base generator <code>g</code>, and a
 * private-value length <code>l</code>.
 * @param x the private value
 * @param p the prime modulus
 * @param g the base generator
 * @param l the private-value length
 * @exception InvalidKeyException if the key cannot be encoded
 */
  DHPrivateKey(  BigInteger x,  BigInteger p,  BigInteger g,  int l){
    this.x=x;
    this.p=p;
    this.g=g;
    this.l=l;
    try {
      this.key=new DerValue(DerValue.tag_Integer,this.x.toByteArray()).toByteArray();
      this.encodedKey=getEncoded();
    }
 catch (    IOException e) {
      throw new ProviderException("Cannot produce ASN.1 encoding",e);
    }
  }
  /** 
 * Make a DH private key from its DER encoding (PKCS #8).
 * @param encodedKey the encoded key
 * @exception InvalidKeyException if the encoded key does not represent
 * a Diffie-Hellman private key
 */
  DHPrivateKey(  byte[] encodedKey) throws InvalidKeyException {
    InputStream inStream=new ByteArrayInputStream(encodedKey);
    try {
      DerValue val=new DerValue(inStream);
      if (val.tag != DerValue.tag_Sequence) {
        throw new InvalidKeyException("Key not a SEQUENCE");
      }
      BigInteger parsedVersion=val.data.getBigInteger();
      if (!parsedVersion.equals(PKCS8_VERSION)) {
        throw new IOException("version mismatch: (supported: " + PKCS8_VERSION + ", parsed: "+ parsedVersion);
      }
      DerValue algid=val.data.getDerValue();
      if (algid.tag != DerValue.tag_Sequence) {
        throw new InvalidKeyException("AlgId is not a SEQUENCE");
      }
      DerInputStream derInStream=algid.toDerInputStream();
      ObjectIdentifier oid=derInStream.getOID();
      if (oid == null) {
        throw new InvalidKeyException("Null OID");
      }
      if (derInStream.available() == 0) {
        throw new InvalidKeyException("Parameters missing");
      }
      DerValue params=derInStream.getDerValue();
      if (params.tag == DerValue.tag_Null) {
        throw new InvalidKeyException("Null parameters");
      }
      if (params.tag != DerValue.tag_Sequence) {
        throw new InvalidKeyException("Parameters not a SEQUENCE");
      }
      params.data.reset();
      this.p=params.data.getBigInteger();
      this.g=params.data.getBigInteger();
      if (params.data.available() != 0) {
        this.l=params.data.getInteger();
      }
      if (params.data.available() != 0) {
        throw new InvalidKeyException("Extra parameter data");
      }
      this.key=val.data.getOctetString();
      parseKeyBits();
      this.encodedKey=(byte[])encodedKey.clone();
    }
 catch (    NumberFormatException e) {
      InvalidKeyException ike=new InvalidKeyException("Private-value length too big");
      ike.initCause(e);
      throw ike;
    }
catch (    IOException e) {
      InvalidKeyException ike=new InvalidKeyException("Error parsing key encoding: " + e.getMessage());
      ike.initCause(e);
      throw ike;
    }
  }
  /** 
 * Returns the encoding format of this key: "PKCS#8"
 */
  public String getFormat(){
    return "PKCS#8";
  }
  /** 
 * Returns the name of the algorithm associated with this key: "DH"
 */
  public String getAlgorithm(){
    return "DH";
  }
  /** 
 * Get the encoding of the key.
 */
  public synchronized byte[] getEncoded(){
    if (this.encodedKey == null) {
      try {
        DerOutputStream tmp=new DerOutputStream();
        tmp.putInteger(PKCS8_VERSION);
        DerOutputStream algid=new DerOutputStream();
        algid.putOID(new ObjectIdentifier(DH_data));
        DerOutputStream params=new DerOutputStream();
        params.putInteger(this.p);
        params.putInteger(this.g);
        if (this.l != 0)         params.putInteger(this.l);
        DerValue paramSequence=new DerValue(DerValue.tag_Sequence,params.toByteArray());
        algid.putDerValue(paramSequence);
        tmp.write(DerValue.tag_Sequence,algid);
        tmp.putOctetString(this.key);
        DerOutputStream derKey=new DerOutputStream();
        derKey.write(DerValue.tag_Sequence,tmp);
        this.encodedKey=derKey.toByteArray();
      }
 catch (      IOException e) {
        return null;
      }
    }
    return (byte[])this.encodedKey.clone();
  }
  /** 
 * Returns the private value, <code>x</code>.
 * @return the private value, <code>x</code>
 */
  public BigInteger getX(){
    return this.x;
  }
  /** 
 * Returns the key parameters.
 * @return the key parameters
 */
  public DHParameterSpec getParams(){
    if (this.l != 0)     return new DHParameterSpec(this.p,this.g,this.l);
 else     return new DHParameterSpec(this.p,this.g);
  }
  public String toString(){
    String LINE_SEP=System.getProperty("line.separator");
    StringBuffer strbuf=new StringBuffer("SunJCE Diffie-Hellman Private Key:" + LINE_SEP + "x:"+ LINE_SEP+ Debug.toHexString(this.x)+ LINE_SEP+ "p:"+ LINE_SEP+ Debug.toHexString(this.p)+ LINE_SEP+ "g:"+ LINE_SEP+ Debug.toHexString(this.g));
    if (this.l != 0)     strbuf.append(LINE_SEP + "l:" + LINE_SEP+ "    "+ this.l);
    return strbuf.toString();
  }
  private void parseKeyBits() throws InvalidKeyException {
    try {
      DerInputStream in=new DerInputStream(this.key);
      this.x=in.getBigInteger();
    }
 catch (    IOException e) {
      InvalidKeyException ike=new InvalidKeyException("Error parsing key encoding: " + e.getMessage());
      ike.initCause(e);
      throw ike;
    }
  }
  /** 
 * Calculates a hash code value for the object.
 * Objects that are equal will also have the same hashcode.
 */
  public int hashCode(){
    int retval=0;
    byte[] enc=getEncoded();
    for (int i=1; i < enc.length; i++) {
      retval+=enc[i] * i;
    }
    return (retval);
  }
  public boolean equals(  Object obj){
    if (this == obj)     return true;
    if (!(obj instanceof PrivateKey))     return false;
    byte[] thisEncoded=this.getEncoded();
    byte[] thatEncoded=((PrivateKey)obj).getEncoded();
    return java.util.Arrays.equals(thisEncoded,thatEncoded);
  }
  /** 
 * Replace the DH private key to be serialized.
 * @return the standard KeyRep object to be serialized
 * @throws java.io.ObjectStreamException if a new object representing
 * this DH private key could not be created
 */
  private Object writeReplace() throws java.io.ObjectStreamException {
    return new KeyRep(KeyRep.Type.PRIVATE,getAlgorithm(),getFormat(),getEncoded());
  }
}
