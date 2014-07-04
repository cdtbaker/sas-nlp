package com.sun.org.apache.xml.internal.security.c14n;
import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import com.sun.org.apache.xml.internal.security.exceptions.AlgorithmAlreadyRegisteredException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
/** 
 * @author Christian Geuer-Pollmann
 */
public class Canonicalizer {
  /** 
 * The output encoding of canonicalized data 
 */
  public static final String ENCODING="UTF8";
  /** 
 * XPath Expresion for selecting every node and continuous comments joined
 * in only one node
 */
  public static final String XPATH_C14N_WITH_COMMENTS_SINGLE_NODE="(.//. | .//@* | .//namespace::*)";
  /** 
 * The URL defined in XML-SEC Rec for inclusive c14n <b>without</b> comments.
 */
  public static final String ALGO_ID_C14N_OMIT_COMMENTS="http://www.w3.org/TR/2001/REC-xml-c14n-20010315";
  /** 
 * The URL defined in XML-SEC Rec for inclusive c14n <b>with</b> comments.
 */
  public static final String ALGO_ID_C14N_WITH_COMMENTS=ALGO_ID_C14N_OMIT_COMMENTS + "#WithComments";
  /** 
 * The URL defined in XML-SEC Rec for exclusive c14n <b>without</b> comments.
 */
  public static final String ALGO_ID_C14N_EXCL_OMIT_COMMENTS="http://www.w3.org/2001/10/xml-exc-c14n#";
  /** 
 * The URL defined in XML-SEC Rec for exclusive c14n <b>with</b> comments.
 */
  public static final String ALGO_ID_C14N_EXCL_WITH_COMMENTS=ALGO_ID_C14N_EXCL_OMIT_COMMENTS + "WithComments";
  /** 
 * The URI for inclusive c14n 1.1 <b>without</b> comments.
 */
  public static final String ALGO_ID_C14N11_OMIT_COMMENTS="http://www.w3.org/2006/12/xml-c14n11";
  /** 
 * The URI for inclusive c14n 1.1 <b>with</b> comments.
 */
  public static final String ALGO_ID_C14N11_WITH_COMMENTS=ALGO_ID_C14N11_OMIT_COMMENTS + "#WithComments";
  static boolean _alreadyInitialized=false;
  static Map _canonicalizerHash=null;
  protected CanonicalizerSpi canonicalizerSpi=null;
  /** 
 * Method init
 */
  public static void init(){
    if (!Canonicalizer._alreadyInitialized) {
      Canonicalizer._canonicalizerHash=new HashMap(10);
      Canonicalizer._alreadyInitialized=true;
    }
  }
  /** 
 * Constructor Canonicalizer
 * @param algorithmURI
 * @throws InvalidCanonicalizerException
 */
  private Canonicalizer(  String algorithmURI) throws InvalidCanonicalizerException {
    try {
      Class implementingClass=getImplementingClass(algorithmURI);
      this.canonicalizerSpi=(CanonicalizerSpi)implementingClass.newInstance();
      this.canonicalizerSpi.reset=true;
    }
 catch (    Exception e) {
      Object exArgs[]={algorithmURI};
      throw new InvalidCanonicalizerException("signature.Canonicalizer.UnknownCanonicalizer",exArgs);
    }
  }
  /** 
 * Method getInstance
 * @param algorithmURI
 * @return a Conicicalizer instance ready for the job
 * @throws InvalidCanonicalizerException
 */
  public static final Canonicalizer getInstance(  String algorithmURI) throws InvalidCanonicalizerException {
    Canonicalizer c14nizer=new Canonicalizer(algorithmURI);
    return c14nizer;
  }
  /** 
 * Method register
 * @param algorithmURI
 * @param implementingClass
 * @throws AlgorithmAlreadyRegisteredException
 */
  public static void register(  String algorithmURI,  String implementingClass) throws AlgorithmAlreadyRegisteredException {
    Class registeredClass=getImplementingClass(algorithmURI);
    if (registeredClass != null) {
      Object exArgs[]={algorithmURI,registeredClass};
      throw new AlgorithmAlreadyRegisteredException("algorithm.alreadyRegistered",exArgs);
    }
    try {
      _canonicalizerHash.put(algorithmURI,Class.forName(implementingClass));
    }
 catch (    ClassNotFoundException e) {
      throw new RuntimeException("c14n class not found");
    }
  }
  /** 
 * Method getURI
 * @return the URI defined for this c14n instance.
 */
  public final String getURI(){
    return this.canonicalizerSpi.engineGetURI();
  }
  /** 
 * Method getIncludeComments
 * @return true if the c14n respect the comments.
 */
  public boolean getIncludeComments(){
    return this.canonicalizerSpi.engineGetIncludeComments();
  }
  /** 
 * This method tries to canonicalize the given bytes. It's possible to even
 * canonicalize non-wellformed sequences if they are well-formed after being
 * wrapped with a <CODE>&gt;a&lt;...&gt;/a&lt;</CODE>.
 * @param inputBytes
 * @return the result of the conicalization.
 * @throws CanonicalizationException
 * @throws java.io.IOException
 * @throws javax.xml.parsers.ParserConfigurationException
 * @throws org.xml.sax.SAXException
 */
  public byte[] canonicalize(  byte[] inputBytes) throws javax.xml.parsers.ParserConfigurationException, java.io.IOException, org.xml.sax.SAXException, CanonicalizationException {
    ByteArrayInputStream bais=new ByteArrayInputStream(inputBytes);
    InputSource in=new InputSource(bais);
    DocumentBuilderFactory dfactory=DocumentBuilderFactory.newInstance();
    dfactory.setNamespaceAware(true);
    dfactory.setValidating(true);
    DocumentBuilder db=dfactory.newDocumentBuilder();
    db.setErrorHandler(new com.sun.org.apache.xml.internal.security.utils.IgnoreAllErrorHandler());
    Document document=db.parse(in);
    byte result[]=this.canonicalizeSubtree(document);
    return result;
  }
  /** 
 * Canonicalizes the subtree rooted by <CODE>node</CODE>.
 * @param node The node to canicalize
 * @return the result of the c14n.
 * @throws CanonicalizationException
 */
  public byte[] canonicalizeSubtree(  Node node) throws CanonicalizationException {
    return this.canonicalizerSpi.engineCanonicalizeSubTree(node);
  }
  /** 
 * Canonicalizes the subtree rooted by <CODE>node</CODE>.
 * @param node
 * @param inclusiveNamespaces
 * @return the result of the c14n.
 * @throws CanonicalizationException
 */
  public byte[] canonicalizeSubtree(  Node node,  String inclusiveNamespaces) throws CanonicalizationException {
    return this.canonicalizerSpi.engineCanonicalizeSubTree(node,inclusiveNamespaces);
  }
  /** 
 * Canonicalizes an XPath node set. The <CODE>xpathNodeSet</CODE> is treated
 * as a list of XPath nodes, not as a list of subtrees.
 * @param xpathNodeSet
 * @return the result of the c14n.
 * @throws CanonicalizationException
 */
  public byte[] canonicalizeXPathNodeSet(  NodeList xpathNodeSet) throws CanonicalizationException {
    return this.canonicalizerSpi.engineCanonicalizeXPathNodeSet(xpathNodeSet);
  }
  /** 
 * Canonicalizes an XPath node set. The <CODE>xpathNodeSet</CODE> is treated
 * as a list of XPath nodes, not as a list of subtrees.
 * @param xpathNodeSet
 * @param inclusiveNamespaces
 * @return the result of the c14n.
 * @throws CanonicalizationException
 */
  public byte[] canonicalizeXPathNodeSet(  NodeList xpathNodeSet,  String inclusiveNamespaces) throws CanonicalizationException {
    return this.canonicalizerSpi.engineCanonicalizeXPathNodeSet(xpathNodeSet,inclusiveNamespaces);
  }
  /** 
 * Canonicalizes an XPath node set.
 * @param xpathNodeSet
 * @return the result of the c14n.
 * @throws CanonicalizationException
 */
  public byte[] canonicalizeXPathNodeSet(  Set xpathNodeSet) throws CanonicalizationException {
    return this.canonicalizerSpi.engineCanonicalizeXPathNodeSet(xpathNodeSet);
  }
  /** 
 * Canonicalizes an XPath node set.
 * @param xpathNodeSet
 * @param inclusiveNamespaces
 * @return the result of the c14n.
 * @throws CanonicalizationException
 */
  public byte[] canonicalizeXPathNodeSet(  Set xpathNodeSet,  String inclusiveNamespaces) throws CanonicalizationException {
    return this.canonicalizerSpi.engineCanonicalizeXPathNodeSet(xpathNodeSet,inclusiveNamespaces);
  }
  /** 
 * Sets the writer where the canonicalization ends.  ByteArrayOutputStream
 * if none is set.
 * @param os
 */
  public void setWriter(  OutputStream os){
    this.canonicalizerSpi.setWriter(os);
  }
  /** 
 * Returns the name of the implementing {@link CanonicalizerSpi} class
 * @return the name of the implementing {@link CanonicalizerSpi} class
 */
  public String getImplementingCanonicalizerClass(){
    return this.canonicalizerSpi.getClass().getName();
  }
  /** 
 * Method getImplementingClass
 * @param URI
 * @return the name of the class that implements the given URI
 */
  private static Class getImplementingClass(  String URI){
    return (Class)_canonicalizerHash.get(URI);
  }
  /** 
 * Set the canonicalizer behaviour to not reset.
 */
  public void notReset(){
    this.canonicalizerSpi.reset=false;
  }
}
