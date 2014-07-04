package org.jcp.xml.dsig.internal.dom;
import javax.xml.crypto.*;
import javax.xml.crypto.dom.DOMCryptoContext;
import javax.xml.crypto.dsig.*;
import javax.xml.crypto.dsig.keyinfo.X509IssuerSerial;
import java.math.BigInteger;
import javax.security.auth.x500.X500Principal;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
/** 
 * DOM-based implementation of X509IssuerSerial.
 * @author Sean Mullan
 */
public final class DOMX509IssuerSerial extends DOMStructure implements X509IssuerSerial {
  private final String issuerName;
  private final BigInteger serialNumber;
  /** 
 * Creates a <code>DOMX509IssuerSerial</code> containing the specified
 * issuer distinguished name/serial number pair.
 * @param issuerName the X.509 issuer distinguished name in RFC 2253
 * String format
 * @param serialNumber the serial number
 * @throws IllegalArgumentException if the format of <code>issuerName</code>
 * is not RFC 2253 compliant
 * @throws NullPointerException if <code>issuerName</code> or
 * <code>serialNumber</code> is <code>null</code>
 */
  public DOMX509IssuerSerial(  String issuerName,  BigInteger serialNumber){
    if (issuerName == null) {
      throw new NullPointerException("issuerName cannot be null");
    }
    if (serialNumber == null) {
      throw new NullPointerException("serialNumber cannot be null");
    }
    new X500Principal(issuerName);
    this.issuerName=issuerName;
    this.serialNumber=serialNumber;
  }
  /** 
 * Creates a <code>DOMX509IssuerSerial</code> from an element.
 * @param isElem an X509IssuerSerial element
 */
  public DOMX509IssuerSerial(  Element isElem){
    Element iNElem=DOMUtils.getFirstChildElement(isElem);
    Element sNElem=DOMUtils.getNextSiblingElement(iNElem);
    issuerName=iNElem.getFirstChild().getNodeValue();
    serialNumber=new BigInteger(sNElem.getFirstChild().getNodeValue());
  }
  public String getIssuerName(){
    return issuerName;
  }
  public BigInteger getSerialNumber(){
    return serialNumber;
  }
  public void marshal(  Node parent,  String dsPrefix,  DOMCryptoContext context) throws MarshalException {
    Document ownerDoc=DOMUtils.getOwnerDocument(parent);
    Element isElem=DOMUtils.createElement(ownerDoc,"X509IssuerSerial",XMLSignature.XMLNS,dsPrefix);
    Element inElem=DOMUtils.createElement(ownerDoc,"X509IssuerName",XMLSignature.XMLNS,dsPrefix);
    Element snElem=DOMUtils.createElement(ownerDoc,"X509SerialNumber",XMLSignature.XMLNS,dsPrefix);
    inElem.appendChild(ownerDoc.createTextNode(issuerName));
    snElem.appendChild(ownerDoc.createTextNode(serialNumber.toString()));
    isElem.appendChild(inElem);
    isElem.appendChild(snElem);
    parent.appendChild(isElem);
  }
  public boolean equals(  Object obj){
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof X509IssuerSerial)) {
      return false;
    }
    X509IssuerSerial ois=(X509IssuerSerial)obj;
    return (issuerName.equals(ois.getIssuerName()) && serialNumber.equals(ois.getSerialNumber()));
  }
}
