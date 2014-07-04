package sun.security.ec;
import java.io.IOException;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.*;
import sun.security.util.*;
/** 
 * This class implements encoding and decoding of Elliptic Curve parameters
 * as specified in RFC 3279.
 * However, only named curves are currently supported.
 * ASN.1 from RFC 3279 follows. Note that X9.62 (2005) has added some additional
 * options.
 * <pre>
 * EcpkParameters ::= CHOICE {
 * ecParameters  ECParameters,
 * namedCurve    OBJECT IDENTIFIER,
 * implicitlyCA  NULL }
 * ECParameters ::= SEQUENCE {
 * version   ECPVer,          -- version is always 1
 * fieldID   FieldID,         -- identifies the finite field over
 * -- which the curve is defined
 * curve     Curve,           -- coefficients a and b of the
 * -- elliptic curve
 * base      ECPoint,         -- specifies the base point P
 * -- on the elliptic curve
 * order     INTEGER,         -- the order n of the base point
 * cofactor  INTEGER OPTIONAL -- The integer h = #E(Fq)/n
 * }
 * ECPVer ::= INTEGER {ecpVer1(1)}
 * Curve ::= SEQUENCE {
 * a         FieldElement,
 * b         FieldElement,
 * seed      BIT STRING OPTIONAL }
 * FieldElement ::= OCTET STRING
 * ECPoint ::= OCTET STRING
 * </pre>
 * @since   1.6
 * @author  Andreas Sterbenz
 */
public final class ECParameters extends AlgorithmParametersSpi {
  public ECParameters(){
  }
  public static ECPoint decodePoint(  byte[] data,  EllipticCurve curve) throws IOException {
    if ((data.length == 0) || (data[0] != 4)) {
      throw new IOException("Only uncompressed point format supported");
    }
    int n=(curve.getField().getFieldSize() + 7) >> 3;
    if (data.length != (n * 2) + 1) {
      throw new IOException("Point does not match field size");
    }
    byte[] xb=new byte[n];
    byte[] yb=new byte[n];
    System.arraycopy(data,1,xb,0,n);
    System.arraycopy(data,n + 1,yb,0,n);
    return new ECPoint(new BigInteger(1,xb),new BigInteger(1,yb));
  }
  public static byte[] encodePoint(  ECPoint point,  EllipticCurve curve){
    int n=(curve.getField().getFieldSize() + 7) >> 3;
    byte[] xb=trimZeroes(point.getAffineX().toByteArray());
    byte[] yb=trimZeroes(point.getAffineY().toByteArray());
    if ((xb.length > n) || (yb.length > n)) {
      throw new RuntimeException("Point coordinates do not match field size");
    }
    byte[] b=new byte[1 + (n << 1)];
    b[0]=4;
    System.arraycopy(xb,0,b,n - xb.length + 1,xb.length);
    System.arraycopy(yb,0,b,b.length - yb.length,yb.length);
    return b;
  }
  static byte[] trimZeroes(  byte[] b){
    int i=0;
    while ((i < b.length - 1) && (b[i] == 0)) {
      i++;
    }
    if (i == 0) {
      return b;
    }
    byte[] t=new byte[b.length - i];
    System.arraycopy(b,i,t,0,t.length);
    return t;
  }
  public static NamedCurve getNamedCurve(  ECParameterSpec params){
    if ((params instanceof NamedCurve) || (params == null)) {
      return (NamedCurve)params;
    }
    int fieldSize=params.getCurve().getField().getFieldSize();
    for (    ECParameterSpec namedCurve : NamedCurve.knownECParameterSpecs()) {
      if (namedCurve.getCurve().getField().getFieldSize() != fieldSize) {
        continue;
      }
      if (namedCurve.getCurve().equals(params.getCurve()) == false) {
        continue;
      }
      if (namedCurve.getGenerator().equals(params.getGenerator()) == false) {
        continue;
      }
      if (namedCurve.getOrder().equals(params.getOrder()) == false) {
        continue;
      }
      if (namedCurve.getCofactor() != params.getCofactor()) {
        continue;
      }
      return (NamedCurve)namedCurve;
    }
    return null;
  }
  public static String getCurveName(  ECParameterSpec params){
    NamedCurve curve=getNamedCurve(params);
    return (curve == null) ? null : curve.getObjectIdentifier().toString();
  }
  public static byte[] encodeParameters(  ECParameterSpec params){
    NamedCurve curve=getNamedCurve(params);
    if (curve == null) {
      throw new RuntimeException("Not a known named curve: " + params);
    }
    return curve.getEncoded();
  }
  public static ECParameterSpec decodeParameters(  byte[] params) throws IOException {
    DerValue encodedParams=new DerValue(params);
    if (encodedParams.tag == DerValue.tag_ObjectId) {
      ObjectIdentifier oid=encodedParams.getOID();
      ECParameterSpec spec=NamedCurve.getECParameterSpec(oid);
      if (spec == null) {
        throw new IOException("Unknown named curve: " + oid);
      }
      return spec;
    }
    throw new IOException("Only named ECParameters supported");
  }
  static AlgorithmParameters getAlgorithmParameters(  ECParameterSpec spec) throws InvalidKeyException {
    try {
      AlgorithmParameters params=AlgorithmParameters.getInstance("EC",ECKeyFactory.ecInternalProvider);
      params.init(spec);
      return params;
    }
 catch (    GeneralSecurityException e) {
      throw new InvalidKeyException("EC parameters error",e);
    }
  }
  private ECParameterSpec paramSpec;
  protected void engineInit(  AlgorithmParameterSpec paramSpec) throws InvalidParameterSpecException {
    if (paramSpec instanceof ECParameterSpec) {
      this.paramSpec=getNamedCurve((ECParameterSpec)paramSpec);
      if (this.paramSpec == null) {
        throw new InvalidParameterSpecException("Not a supported named curve: " + paramSpec);
      }
    }
 else     if (paramSpec instanceof ECGenParameterSpec) {
      String name=((ECGenParameterSpec)paramSpec).getName();
      ECParameterSpec spec=NamedCurve.getECParameterSpec(name);
      if (spec == null) {
        throw new InvalidParameterSpecException("Unknown curve: " + name);
      }
      this.paramSpec=spec;
    }
 else     if (paramSpec == null) {
      throw new InvalidParameterSpecException("paramSpec must not be null");
    }
 else {
      throw new InvalidParameterSpecException("Only ECParameterSpec and ECGenParameterSpec supported");
    }
  }
  protected void engineInit(  byte[] params) throws IOException {
    paramSpec=decodeParameters(params);
  }
  protected void engineInit(  byte[] params,  String decodingMethod) throws IOException {
    engineInit(params);
  }
  protected <T extends AlgorithmParameterSpec>T engineGetParameterSpec(  Class<T> spec) throws InvalidParameterSpecException {
    if (spec.isAssignableFrom(ECParameterSpec.class)) {
      return (T)paramSpec;
    }
 else     if (spec.isAssignableFrom(ECGenParameterSpec.class)) {
      return (T)new ECGenParameterSpec(getCurveName(paramSpec));
    }
 else {
      throw new InvalidParameterSpecException("Only ECParameterSpec and ECGenParameterSpec supported");
    }
  }
  protected byte[] engineGetEncoded() throws IOException {
    return encodeParameters(paramSpec);
  }
  protected byte[] engineGetEncoded(  String encodingMethod) throws IOException {
    return engineGetEncoded();
  }
  protected String engineToString(){
    return paramSpec.toString();
  }
}
