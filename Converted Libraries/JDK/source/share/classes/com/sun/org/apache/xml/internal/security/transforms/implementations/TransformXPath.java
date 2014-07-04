package com.sun.org.apache.xml.internal.security.transforms.implementations;
import javax.xml.transform.TransformerException;
import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityRuntimeException;
import com.sun.org.apache.xml.internal.security.signature.NodeFilter;
import com.sun.org.apache.xml.internal.security.signature.XMLSignatureInput;
import com.sun.org.apache.xml.internal.security.transforms.Transform;
import com.sun.org.apache.xml.internal.security.transforms.TransformSpi;
import com.sun.org.apache.xml.internal.security.transforms.TransformationException;
import com.sun.org.apache.xml.internal.security.transforms.Transforms;
import com.sun.org.apache.xml.internal.security.utils.CachedXPathAPIHolder;
import com.sun.org.apache.xml.internal.security.utils.CachedXPathFuncHereAPI;
import com.sun.org.apache.xml.internal.security.utils.Constants;
import com.sun.org.apache.xml.internal.security.utils.XMLUtils;
import com.sun.org.apache.xml.internal.utils.PrefixResolverDefault;
import com.sun.org.apache.xpath.internal.objects.XObject;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
/** 
 * Class TransformXPath
 * Implements the <CODE>http://www.w3.org/TR/1999/REC-xpath-19991116</CODE>
 * transform.
 * @author Christian Geuer-Pollmann
 * @see <a href="http://www.w3.org/TR/1999/REC-xpath-19991116">XPath</a>
 */
public class TransformXPath extends TransformSpi {
  /** 
 * Field implementedTransformURI 
 */
  public static final String implementedTransformURI=Transforms.TRANSFORM_XPATH;
  /** 
 * Method engineGetURI
 * @inheritDoc
 */
  protected String engineGetURI(){
    return implementedTransformURI;
  }
  /** 
 * Method enginePerformTransform
 * @inheritDoc
 * @param input
 * @throws TransformationException
 */
  protected XMLSignatureInput enginePerformTransform(  XMLSignatureInput input,  Transform _transformObject) throws TransformationException {
    try {
      CachedXPathAPIHolder.setDoc(_transformObject.getElement().getOwnerDocument());
      Element xpathElement=XMLUtils.selectDsNode(_transformObject.getElement().getFirstChild(),Constants._TAG_XPATH,0);
      if (xpathElement == null) {
        Object exArgs[]={"ds:XPath","Transform"};
        throw new TransformationException("xml.WrongContent",exArgs);
      }
      Node xpathnode=xpathElement.getChildNodes().item(0);
      String str=CachedXPathFuncHereAPI.getStrFromNode(xpathnode);
      input.setNeedsToBeExpanded(needsCircunvent(str));
      if (xpathnode == null) {
        throw new DOMException(DOMException.HIERARCHY_REQUEST_ERR,"Text must be in ds:Xpath");
      }
      input.addNodeFilter(new XPathNodeFilter(xpathElement,xpathnode,str));
      input.setNodeSet(true);
      return input;
    }
 catch (    DOMException ex) {
      throw new TransformationException("empty",ex);
    }
  }
  /** 
 * @param str
 * @return true if needs to be circunvent for bug.
 */
  private boolean needsCircunvent(  String str){
    return (str.indexOf("namespace") != -1) || (str.indexOf("name()") != -1);
  }
static class XPathNodeFilter implements NodeFilter {
    PrefixResolverDefault prefixResolver;
    CachedXPathFuncHereAPI xPathFuncHereAPI=new CachedXPathFuncHereAPI(CachedXPathAPIHolder.getCachedXPathAPI());
    Node xpathnode;
    String str;
    XPathNodeFilter(    Element xpathElement,    Node xpathnode,    String str){
      this.xpathnode=xpathnode;
      this.str=str;
      prefixResolver=new PrefixResolverDefault(xpathElement);
    }
    /** 
 * @see com.sun.org.apache.xml.internal.security.signature.NodeFilter#isNodeInclude(org.w3c.dom.Node)
 */
    public int isNodeInclude(    Node currentNode){
      XObject includeInResult;
      try {
        includeInResult=xPathFuncHereAPI.eval(currentNode,xpathnode,str,prefixResolver);
        if (includeInResult.bool())         return 1;
        return 0;
      }
 catch (      TransformerException e) {
        Object[] eArgs={currentNode};
        throw new XMLSecurityRuntimeException("signature.Transform.node",eArgs,e);
      }
catch (      Exception e) {
        Object[] eArgs={currentNode,new Short(currentNode.getNodeType())};
        throw new XMLSecurityRuntimeException("signature.Transform.nodeAndType",eArgs,e);
      }
    }
    public int isNodeIncludeDO(    Node n,    int level){
      return isNodeInclude(n);
    }
  }
}
