package com.sun.org.apache.xml.internal.security.c14n;
import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
/** 
 * Base class which all Caninicalization algorithms extend.
 * $todo$ cange JavaDoc
 * @author Christian Geuer-Pollmann
 */
public abstract class CanonicalizerSpi {
  /** 
 * Method canonicalize
 * @param inputBytes
 * @return the c14n bytes.
 * @throws CanonicalizationException
 * @throws java.io.IOException
 * @throws javax.xml.parsers.ParserConfigurationException
 * @throws org.xml.sax.SAXException
 */
  public byte[] engineCanonicalize(  byte[] inputBytes) throws javax.xml.parsers.ParserConfigurationException, java.io.IOException, org.xml.sax.SAXException, CanonicalizationException {
    java.io.ByteArrayInputStream bais=new ByteArrayInputStream(inputBytes);
    InputSource in=new InputSource(bais);
    DocumentBuilderFactory dfactory=DocumentBuilderFactory.newInstance();
    dfactory.setNamespaceAware(true);
    DocumentBuilder db=dfactory.newDocumentBuilder();
    Document document=db.parse(in);
    byte result[]=this.engineCanonicalizeSubTree(document);
    return result;
  }
  /** 
 * Method engineCanonicalizeXPathNodeSet
 * @param xpathNodeSet
 * @return the c14n bytes
 * @throws CanonicalizationException
 */
  public byte[] engineCanonicalizeXPathNodeSet(  NodeList xpathNodeSet) throws CanonicalizationException {
    return this.engineCanonicalizeXPathNodeSet(XMLUtils.convertNodelistToSet(xpathNodeSet));
  }
  /** 
 * Method engineCanonicalizeXPathNodeSet
 * @param xpathNodeSet
 * @param inclusiveNamespaces
 * @return the c14n bytes
 * @throws CanonicalizationException
 */
  public byte[] engineCanonicalizeXPathNodeSet(  NodeList xpathNodeSet,  String inclusiveNamespaces) throws CanonicalizationException {
    return this.engineCanonicalizeXPathNodeSet(XMLUtils.convertNodelistToSet(xpathNodeSet),inclusiveNamespaces);
  }
  /** 
 * Returns the URI of this engine.
 * @return the URI
 */
  public abstract String engineGetURI();
  /** 
 * Returns the URI if include comments
 * @return true if include.
 */
  public abstract boolean engineGetIncludeComments();
  /** 
 * C14n a nodeset
 * @param xpathNodeSet
 * @return the c14n bytes
 * @throws CanonicalizationException
 */
  public abstract byte[] engineCanonicalizeXPathNodeSet(  Set xpathNodeSet) throws CanonicalizationException ;
  /** 
 * C14n a nodeset
 * @param xpathNodeSet
 * @param inclusiveNamespaces
 * @return the c14n bytes
 * @throws CanonicalizationException
 */
  public abstract byte[] engineCanonicalizeXPathNodeSet(  Set xpathNodeSet,  String inclusiveNamespaces) throws CanonicalizationException ;
  /** 
 * C14n a node tree.
 * @param rootNode
 * @return the c14n bytes
 * @throws CanonicalizationException
 */
  public abstract byte[] engineCanonicalizeSubTree(  Node rootNode) throws CanonicalizationException ;
  /** 
 * C14n a node tree.
 * @param rootNode
 * @param inclusiveNamespaces
 * @return the c14n bytes
 * @throws CanonicalizationException
 */
  public abstract byte[] engineCanonicalizeSubTree(  Node rootNode,  String inclusiveNamespaces) throws CanonicalizationException ;
  /** 
 * Sets the writter where the cannocalization ends. ByteArrayOutputStream if
 * none is setted.
 * @param os
 */
  public abstract void setWriter(  OutputStream os);
  /** 
 * Reset the writter after a c14n 
 */
  protected boolean reset=false;
}
