package com.sun.org.apache.xml.internal.security.c14n.implementations;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.xml.parsers.ParserConfigurationException;
import com.sun.org.apache.xml.internal.security.c14n.CanonicalizationException;
import com.sun.org.apache.xml.internal.security.c14n.helper.C14nHelper;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.transforms.params.InclusiveNamespaces;
import com.sun.org.apache.xml.internal.security.utils.Constants;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
/** 
 * Implements &quot; <A
 * HREF="http://www.w3.org/TR/2002/REC-xml-exc-c14n-20020718/">Exclusive XML
 * Canonicalization, Version 1.0 </A>&quot; <BR />
 * Credits: During restructuring of the Canonicalizer framework, Ren??
 * Kollmorgen from Software AG submitted an implementation of ExclC14n which
 * fitted into the old architecture and which based heavily on my old (and slow)
 * implementation of "Canonical XML". A big "thank you" to Ren?? for this.
 * <BR />
 * <i>THIS </i> implementation is a complete rewrite of the algorithm.
 * @author Christian Geuer-Pollmann <geuerp@apache.org>
 * @version $Revision: 1.5 $
 * @see <a href="http://www.w3.org/TR/2002/REC-xml-exc-c14n-20020718/ Exclusive#">
 *          XML Canonicalization, Version 1.0</a>
 */
public abstract class Canonicalizer20010315Excl extends CanonicalizerBase {
  /** 
 * This Set contains the names (Strings like "xmlns" or "xmlns:foo") of
 * the inclusive namespaces.
 */
  TreeSet _inclusiveNSSet=new TreeSet();
  static final String XMLNS_URI=Constants.NamespaceSpecNS;
  final SortedSet result=new TreeSet(COMPARE);
  /** 
 * Constructor Canonicalizer20010315Excl
 * @param includeComments
 */
  public Canonicalizer20010315Excl(  boolean includeComments){
    super(includeComments);
  }
  /** 
 * Method engineCanonicalizeSubTree
 * @inheritDoc
 * @param rootNode
 * @throws CanonicalizationException
 */
  public byte[] engineCanonicalizeSubTree(  Node rootNode) throws CanonicalizationException {
    return this.engineCanonicalizeSubTree(rootNode,"",null);
  }
  /** 
 * Method engineCanonicalizeSubTree
 * @inheritDoc
 * @param rootNode
 * @param inclusiveNamespaces
 * @throws CanonicalizationException
 */
  public byte[] engineCanonicalizeSubTree(  Node rootNode,  String inclusiveNamespaces) throws CanonicalizationException {
    return this.engineCanonicalizeSubTree(rootNode,inclusiveNamespaces,null);
  }
  /** 
 * Method engineCanonicalizeSubTree
 * @param rootNode
 * @param inclusiveNamespaces
 * @param excl A element to exclude from the c14n process.
 * @return the rootNode c14n.
 * @throws CanonicalizationException
 */
  public byte[] engineCanonicalizeSubTree(  Node rootNode,  String inclusiveNamespaces,  Node excl) throws CanonicalizationException {
    this._inclusiveNSSet=(TreeSet)InclusiveNamespaces.prefixStr2Set(inclusiveNamespaces);
    return super.engineCanonicalizeSubTree(rootNode,excl);
  }
  /** 
 * @param rootNode
 * @param inclusiveNamespaces
 * @return the rootNode c14n.
 * @throws CanonicalizationException
 */
  public byte[] engineCanonicalize(  XMLSignatureInput rootNode,  String inclusiveNamespaces) throws CanonicalizationException {
    this._inclusiveNSSet=(TreeSet)InclusiveNamespaces.prefixStr2Set(inclusiveNamespaces);
    return super.engineCanonicalize(rootNode);
  }
  /** 
 * Method handleAttributesSubtree
 * @inheritDoc
 * @param E
 * @throws CanonicalizationException
 */
  Iterator handleAttributesSubtree(  Element E,  NameSpaceSymbTable ns) throws CanonicalizationException {
    SortedSet result=this.result;
    result.clear();
    NamedNodeMap attrs=null;
    int attrsLength=0;
    if (E.hasAttributes()) {
      attrs=E.getAttributes();
      attrsLength=attrs.getLength();
    }
    SortedSet visiblyUtilized=(SortedSet)_inclusiveNSSet.clone();
    for (int i=0; i < attrsLength; i++) {
      Attr N=(Attr)attrs.item(i);
      if (XMLNS_URI != N.getNamespaceURI()) {
        String prefix=N.getPrefix();
        if ((prefix != null) && (!prefix.equals(XML) && !prefix.equals(XMLNS))) {
          visiblyUtilized.add(prefix);
        }
        result.add(N);
        continue;
      }
      String NName=N.getLocalName();
      String NNodeValue=N.getNodeValue();
      if (ns.addMapping(NName,NNodeValue,N)) {
        if (C14nHelper.namespaceIsRelative(NNodeValue)) {
          Object exArgs[]={E.getTagName(),NName,N.getNodeValue()};
          throw new CanonicalizationException("c14n.Canonicalizer.RelativeNamespace",exArgs);
        }
      }
    }
    String prefix;
    if (E.getNamespaceURI() != null) {
      prefix=E.getPrefix();
      if ((prefix == null) || (prefix.length() == 0)) {
        prefix=XMLNS;
      }
    }
 else {
      prefix=XMLNS;
    }
    visiblyUtilized.add(prefix);
    Iterator it=visiblyUtilized.iterator();
    while (it.hasNext()) {
      String s=(String)it.next();
      Attr key=ns.getMapping(s);
      if (key == null) {
        continue;
      }
      result.add(key);
    }
    return result.iterator();
  }
  /** 
 * Method engineCanonicalizeXPathNodeSet
 * @inheritDoc
 * @param xpathNodeSet
 * @param inclusiveNamespaces
 * @throws CanonicalizationException
 */
  public byte[] engineCanonicalizeXPathNodeSet(  Set xpathNodeSet,  String inclusiveNamespaces) throws CanonicalizationException {
    this._inclusiveNSSet=(TreeSet)InclusiveNamespaces.prefixStr2Set(inclusiveNamespaces);
    return super.engineCanonicalizeXPathNodeSet(xpathNodeSet);
  }
  /** 
 * @inheritDoc
 * @param E
 * @throws CanonicalizationException
 */
  final Iterator handleAttributes(  Element E,  NameSpaceSymbTable ns) throws CanonicalizationException {
    SortedSet result=this.result;
    result.clear();
    NamedNodeMap attrs=null;
    int attrsLength=0;
    if (E.hasAttributes()) {
      attrs=E.getAttributes();
      attrsLength=attrs.getLength();
    }
    Set visiblyUtilized=null;
    boolean isOutputElement=isVisibleDO(E,ns.getLevel()) == 1;
    if (isOutputElement) {
      visiblyUtilized=(Set)this._inclusiveNSSet.clone();
    }
    for (int i=0; i < attrsLength; i++) {
      Attr N=(Attr)attrs.item(i);
      if (XMLNS_URI != N.getNamespaceURI()) {
        if (!isVisible(N)) {
          continue;
        }
        if (isOutputElement) {
          String prefix=N.getPrefix();
          if ((prefix != null) && (!prefix.equals(XML) && !prefix.equals(XMLNS))) {
            visiblyUtilized.add(prefix);
          }
          result.add(N);
        }
        continue;
      }
      String NName=N.getLocalName();
      if (isOutputElement && !isVisible(N) && NName != XMLNS) {
        ns.removeMappingIfNotRender(NName);
        continue;
      }
      String NNodeValue=N.getNodeValue();
      if (!isOutputElement && isVisible(N) && _inclusiveNSSet.contains(NName)&& !ns.removeMappingIfRender(NName)) {
        Node n=ns.addMappingAndRender(NName,NNodeValue,N);
        if (n != null) {
          result.add(n);
          if (C14nHelper.namespaceIsRelative(N)) {
            Object exArgs[]={E.getTagName(),NName,N.getNodeValue()};
            throw new CanonicalizationException("c14n.Canonicalizer.RelativeNamespace",exArgs);
          }
        }
      }
      if (ns.addMapping(NName,NNodeValue,N)) {
        if (C14nHelper.namespaceIsRelative(NNodeValue)) {
          Object exArgs[]={E.getTagName(),NName,N.getNodeValue()};
          throw new CanonicalizationException("c14n.Canonicalizer.RelativeNamespace",exArgs);
        }
      }
    }
    if (isOutputElement) {
      Attr xmlns=E.getAttributeNodeNS(XMLNS_URI,XMLNS);
      if ((xmlns != null) && (!isVisible(xmlns))) {
        ns.addMapping(XMLNS,"",nullNode);
      }
      if (E.getNamespaceURI() != null) {
        String prefix=E.getPrefix();
        if ((prefix == null) || (prefix.length() == 0)) {
          visiblyUtilized.add(XMLNS);
        }
 else {
          visiblyUtilized.add(prefix);
        }
      }
 else {
        visiblyUtilized.add(XMLNS);
      }
      Iterator it=visiblyUtilized.iterator();
      while (it.hasNext()) {
        String s=(String)it.next();
        Attr key=ns.getMapping(s);
        if (key == null) {
          continue;
        }
        result.add(key);
      }
    }
    return result.iterator();
  }
  void circumventBugIfNeeded(  XMLSignatureInput input) throws CanonicalizationException, ParserConfigurationException, IOException, SAXException {
    if (!input.isNeedsToBeExpanded() || _inclusiveNSSet.isEmpty())     return;
    Document doc=null;
    if (input.getSubNode() != null) {
      doc=XMLUtils.getOwnerDocument(input.getSubNode());
    }
 else {
      doc=XMLUtils.getOwnerDocument(input.getNodeSet());
    }
    XMLUtils.circumventBug2650(doc);
  }
}
