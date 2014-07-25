package sun.security.ec;
import java.io.IOException;
import java.security.*;
import java.security.interfaces.*;
import java.security.spec.*;
import sun.security.util.*;
import sun.security.x509.*;
/** 
 * Key implementation for EC public keys.
 * @since   1.6
 * @author  Andreas Sterbenz
 */
public final class ECPublicKeyImpl extends X509Key implements ECPublicKey {
  private static final long serialVersionUID=-2462037275160462289L;
  private ECPoint w;
  private ECParameterSpec params;
  /** 
 * Construct a key from its components. Used by the
 * ECKeyFactory and SunPKCS11.
 */
  public ECPublicKeyImpl(  ECPoint w,  ECParameterSpec params) throws InvalidKeyException {
    this.w=w;
    this.params=params;
    algid=new AlgorithmId(AlgorithmId.EC_oid,ECParameters.getAlgorithmParameters(params));
    key=ECParameters.encodePoint(w,params.getCurve());
  }
  /** 
 * Construct a key from its encoding. Used by RSAKeyFactory.
 */
  public ECPublicKeyImpl(  byte[] encoded) throws InvalidKeyException {
    decode(encoded);
  }
  public String getAlgorithm(){
    return "EC";
  }
  public ECPoint getW(){
    return w;
  }
  public ECParameterSpec getParams(){
    return params;
  }
  public byte[] getEncodedPublicValue(){
    return key.clone();
  }
  /** 
 * Parse the key. Called by X509Key.
 */
  protected void parseKeyBits() throws InvalidKeyException {
    try {
      AlgorithmParameters algParams=this.algid.getParameters();
      params=algParams.getParameterSpec(ECParameterSpec.class);
      w=ECParameters.decodePoint(key,params.getCurve());
    }
 catch (    IOException e) {
      throw new InvalidKeyException("Invalid EC key",e);
    }
catch (    InvalidParameterSpecException e) {
      throw new InvalidKeyException("Invalid EC key",e);
    }
  }
  public String toString(){
    return "Sun EC public key, " + params.getCurve().getField().getFieldSize() + " bits\n  public x coord: "+ w.getAffineX()+ "\n  public y coord: "+ w.getAffineY()+ "\n  parameters: "+ params;
  }
  protected Object writeReplace() throws java.io.ObjectStreamException {
    return new KeyRep(KeyRep.Type.PUBLIC,getAlgorithm(),getFormat(),getEncoded());
  }
}
