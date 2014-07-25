package com.sun.org.apache.xml.internal.security.signature;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;
import com.sun.org.apache.xml.internal.security.utils.Constants;
import com.sun.org.apache.xml.internal.security.utils.IdResolver;
import com.sun.org.apache.xml.internal.security.utils.SignatureElementProxy;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
/** 
 * Handles <code>&lt;ds:SignatureProperty&gt;</code> elements
 * Additional information item concerning the generation of the signature(s) can
 * be placed in this Element
 * @author Christian Geuer-Pollmann
 */
public class SignatureProperty extends SignatureElementProxy {
  /** 
 * Constructs{@link SignatureProperty} using specified <code>Target</code> attribute
 * @param doc the {@link Document} in which <code>XMLsignature</code> is placed
 * @param Target the <code>Target</code> attribute references the <code>Signature</code> element to which the property applies SignatureProperty
 */
  public SignatureProperty(  Document doc,  String Target){
    this(doc,Target,null);
  }
  /** 
 * Constructs {@link SignatureProperty} using sepcified <code>Target</code> attribute and <code>Id</code> attribute
 * @param doc the {@link Document} in which <code>XMLsignature</code> is placed
 * @param Target the <code>Target</code> attribute references the <code>Signature</code> element to which the property applies
 * @param Id the <code>Id</code> will be specified by {@link Reference#getURI} in validation
 */
  public SignatureProperty(  Document doc,  String Target,  String Id){
    super(doc);
    this.setTarget(Target);
    this.setId(Id);
  }
  /** 
 * Constructs a {@link SignatureProperty} from an {@link Element}
 * @param element <code>SignatureProperty</code> element
 * @param BaseURI the URI of the resource where the XML instance was stored
 * @throws XMLSecurityException
 */
  public SignatureProperty(  Element element,  String BaseURI) throws XMLSecurityException {
    super(element,BaseURI);
  }
  /** 
 * Sets the <code>Id</code> attribute
 * @param Id the <code>Id</code> attribute
 */
  public void setId(  String Id){
    if ((Id != null)) {
      this._constructionElement.setAttributeNS(null,Constants._ATT_ID,Id);
      IdResolver.registerElementById(this._constructionElement,Id);
    }
  }
  /** 
 * Returns the <code>Id</code> attribute
 * @return the <code>Id</code> attribute
 */
  public String getId(){
    return this._constructionElement.getAttributeNS(null,Constants._ATT_ID);
  }
  /** 
 * Sets the <code>Target</code> attribute
 * @param Target the <code>Target</code> attribute
 */
  public void setTarget(  String Target){
    if ((Target != null)) {
      this._constructionElement.setAttributeNS(null,Constants._ATT_TARGET,Target);
    }
  }
  /** 
 * Returns the <code>Target</code> attribute
 * @return the <code>Target</code> attribute
 */
  public String getTarget(){
    return this._constructionElement.getAttributeNS(null,Constants._ATT_TARGET);
  }
  /** 
 * Method appendChild
 * @param node
 * @return the node in this element.
 */
  public Node appendChild(  Node node){
    return this._constructionElement.appendChild(node);
  }
  /** 
 * @inheritDoc 
 */
  public String getBaseLocalName(){
    return Constants._TAG_SIGNATUREPROPERTY;
  }
}
