package com.sun.org.apache.xml.internal.security.keys.content.x509;
import java.io.ByteArrayInputStream;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.utils.Constants;
import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
/** 
 * @author $Author: mullan $
 */
public class XMLX509Certificate extends SignatureElementProxy implements XMLX509DataContent {
  /** 
 * Field JCA_CERT_ID 
 */
  public static final String JCA_CERT_ID="X.509";
  /** 
 * Constructor X509Certificate
 * @param element
 * @param BaseURI
 * @throws XMLSecurityException
 */
  public XMLX509Certificate(  Element element,  String BaseURI) throws XMLSecurityException {
    super(element,BaseURI);
  }
  /** 
 * Constructor X509Certificate
 * @param doc
 * @param certificateBytes
 */
  public XMLX509Certificate(  Document doc,  byte[] certificateBytes){
    super(doc);
    this.addBase64Text(certificateBytes);
  }
  /** 
 * Constructor XMLX509Certificate
 * @param doc
 * @param x509certificate
 * @throws XMLSecurityException
 */
  public XMLX509Certificate(  Document doc,  X509Certificate x509certificate) throws XMLSecurityException {
    super(doc);
    try {
      this.addBase64Text(x509certificate.getEncoded());
    }
 catch (    java.security.cert.CertificateEncodingException ex) {
      throw new XMLSecurityException("empty",ex);
    }
  }
  /** 
 * Method getCertificateBytes
 * @return the certificate bytes
 * @throws XMLSecurityException
 */
  public byte[] getCertificateBytes() throws XMLSecurityException {
    return this.getBytesFromTextChild();
  }
  /** 
 * Method getX509Certificate
 * @return the x509 certificate
 * @throws XMLSecurityException
 */
  public X509Certificate getX509Certificate() throws XMLSecurityException {
    try {
      byte certbytes[]=this.getCertificateBytes();
      CertificateFactory certFact=CertificateFactory.getInstance(XMLX509Certificate.JCA_CERT_ID);
      X509Certificate cert=(X509Certificate)certFact.generateCertificate(new ByteArrayInputStream(certbytes));
      if (cert != null) {
        return cert;
      }
      return null;
    }
 catch (    CertificateException ex) {
      throw new XMLSecurityException("empty",ex);
    }
  }
  /** 
 * Method getPublicKey
 * @return teh publickey
 * @throws XMLSecurityException
 */
  public PublicKey getPublicKey() throws XMLSecurityException {
    X509Certificate cert=this.getX509Certificate();
    if (cert != null) {
      return cert.getPublicKey();
    }
    return null;
  }
  /** 
 * @inheritDoc 
 */
  public boolean equals(  Object obj){
    if (obj == null) {
      return false;
    }
    if (!this.getClass().getName().equals(obj.getClass().getName())) {
      return false;
    }
    XMLX509Certificate other=(XMLX509Certificate)obj;
    try {
      return java.security.MessageDigest.isEqual(other.getCertificateBytes(),this.getCertificateBytes());
    }
 catch (    XMLSecurityException ex) {
      return false;
    }
  }
  /** 
 * @inheritDoc 
 */
  public String getBaseLocalName(){
    return Constants._TAG_X509CERTIFICATE;
  }
}
