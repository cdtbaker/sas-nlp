package com.sun.org.apache.xml.internal.security.utils;
import com.sun.org.apache.xml.internal.dtm.DTMManager;
import com.sun.org.apache.xml.internal.security.transforms.implementations.FuncHere;
import com.sun.org.apache.xml.internal.security.transforms.implementations.FuncHereContext;
import com.sun.org.apache.xml.internal.utils.PrefixResolver;
import com.sun.org.apache.xml.internal.utils.PrefixResolverDefault;
import com.sun.org.apache.xpath.internal.CachedXPathAPI;
import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.XPath;
import com.sun.org.apache.xpath.internal.XPathContext;
import com.sun.org.apache.xpath.internal.compiler.FunctionTable;
import com.sun.org.apache.xpath.internal.objects.XObject;
import org.w3c.dom.*;
import org.w3c.dom.traversal.NodeIterator;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
/** 
 * @author $Author: mullan $
 */
public class CachedXPathFuncHereAPI {
  static java.util.logging.Logger log=java.util.logging.Logger.getLogger(CachedXPathFuncHereAPI.class.getName());
  /** 
 * XPathContext, and thus DTMManager and DTMs, persists through multiple
 * calls to this object.
 */
  FuncHereContext _funcHereContext=null;
  /** 
 * Field _dtmManager 
 */
  DTMManager _dtmManager=null;
  XPathContext _context=null;
  String xpathStr=null;
  XPath xpath=null;
  static FunctionTable _funcTable=null;
static {
    fixupFunctionTable();
  }
  /** 
 * Method getFuncHereContext
 * @return the context for this object
 */
  public FuncHereContext getFuncHereContext(){
    return this._funcHereContext;
  }
  /** 
 * Constructor CachedXPathFuncHereAPI
 */
  private CachedXPathFuncHereAPI(){
  }
  /** 
 * Constructor CachedXPathFuncHereAPI
 * @param existingXPathContext
 */
  public CachedXPathFuncHereAPI(  XPathContext existingXPathContext){
    this._dtmManager=existingXPathContext.getDTMManager();
    this._context=existingXPathContext;
  }
  /** 
 * Constructor CachedXPathFuncHereAPI
 * @param previouslyUsed
 */
  public CachedXPathFuncHereAPI(  CachedXPathAPI previouslyUsed){
    this._dtmManager=previouslyUsed.getXPathContext().getDTMManager();
    this._context=previouslyUsed.getXPathContext();
  }
  /** 
 * Use an XPath string to select a single node. XPath namespace
 * prefixes are resolved from the context node, which may not
 * be what you want (see the next method).
 * @param contextNode The node to start searching from.
 * @param xpathnode A Node containing a valid XPath string.
 * @return The first node found that matches the XPath, or null.
 * @throws TransformerException
 */
  public Node selectSingleNode(  Node contextNode,  Node xpathnode) throws TransformerException {
    return selectSingleNode(contextNode,xpathnode,contextNode);
  }
  /** 
 * Use an XPath string to select a single node.
 * XPath namespace prefixes are resolved from the namespaceNode.
 * @param contextNode The node to start searching from.
 * @param xpathnode
 * @param namespaceNode The node from which prefixes in the XPath will be resolved to namespaces.
 * @return The first node found that matches the XPath, or null.
 * @throws TransformerException
 */
  public Node selectSingleNode(  Node contextNode,  Node xpathnode,  Node namespaceNode) throws TransformerException {
    NodeIterator nl=selectNodeIterator(contextNode,xpathnode,namespaceNode);
    return nl.nextNode();
  }
  /** 
 * Use an XPath string to select a nodelist.
 * XPath namespace prefixes are resolved from the contextNode.
 * @param contextNode The node to start searching from.
 * @param xpathnode
 * @return A NodeIterator, should never be null.
 * @throws TransformerException
 */
  public NodeIterator selectNodeIterator(  Node contextNode,  Node xpathnode) throws TransformerException {
    return selectNodeIterator(contextNode,xpathnode,contextNode);
  }
  /** 
 * Use an XPath string to select a nodelist.
 * XPath namespace prefixes are resolved from the namespaceNode.
 * @param contextNode The node to start searching from.
 * @param xpathnode
 * @param namespaceNode The node from which prefixes in the XPath will be resolved to namespaces.
 * @return A NodeIterator, should never be null.
 * @throws TransformerException
 * @deprecated
 */
  public NodeIterator selectNodeIterator(  Node contextNode,  Node xpathnode,  Node namespaceNode) throws TransformerException {
    XObject list=eval(contextNode,xpathnode,getStrFromNode(xpathnode),namespaceNode);
    return list.nodeset();
  }
  /** 
 * Use an XPath string to select a nodelist.
 * XPath namespace prefixes are resolved from the contextNode.
 * @param contextNode The node to start searching from.
 * @param xpathnode
 * @return A NodeIterator, should never be null.
 * @throws TransformerException
 * @deprecated
 */
  public NodeList selectNodeList(  Node contextNode,  Node xpathnode) throws TransformerException {
    return selectNodeList(contextNode,xpathnode,getStrFromNode(xpathnode),contextNode);
  }
  /** 
 * Use an XPath string to select a nodelist.
 * XPath namespace prefixes are resolved from the namespaceNode.
 * @param contextNode The node to start searching from.
 * @param xpathnode
 * @param str
 * @param namespaceNode The node from which prefixes in the XPath will be resolved to namespaces.
 * @return A NodeIterator, should never be null.
 * @throws TransformerException
 */
  public NodeList selectNodeList(  Node contextNode,  Node xpathnode,  String str,  Node namespaceNode) throws TransformerException {
    XObject list=eval(contextNode,xpathnode,str,namespaceNode);
    return list.nodelist();
  }
  /** 
 * Evaluate XPath string to an XObject.  Using this method,
 * XPath namespace prefixes will be resolved from the namespaceNode.
 * @param contextNode The node to start searching from.
 * @param xpathnode
 * @return An XObject, which can be used to obtain a string, number, nodelist, etc, should never be null.
 * @see com.sun.org.apache.xpath.internal.objects.XObject
 * @see com.sun.org.apache.xpath.internal.objects.XNull
 * @see com.sun.org.apache.xpath.internal.objects.XBoolean
 * @see com.sun.org.apache.xpath.internal.objects.XNumber
 * @see com.sun.org.apache.xpath.internal.objects.XString
 * @see com.sun.org.apache.xpath.internal.objects.XRTreeFrag
 * @throws TransformerException
 * @deprecated
 */
  public XObject eval(  Node contextNode,  Node xpathnode) throws TransformerException {
    return eval(contextNode,xpathnode,getStrFromNode(xpathnode),contextNode);
  }
  /** 
 * Evaluate XPath string to an XObject.
 * XPath namespace prefixes are resolved from the namespaceNode.
 * The implementation of this is a little slow, since it creates
 * a number of objects each time it is called.  This could be optimized
 * to keep the same objects around, but then thread-safety issues would arise.
 * @param contextNode The node to start searching from.
 * @param xpathnode
 * @param str
 * @param namespaceNode The node from which prefixes in the XPath will be resolved to namespaces.
 * @return An XObject, which can be used to obtain a string, number, nodelist, etc, should never be null.
 * @see com.sun.org.apache.xpath.internal.objects.XObject
 * @see com.sun.org.apache.xpath.internal.objects.XNull
 * @see com.sun.org.apache.xpath.internal.objects.XBoolean
 * @see com.sun.org.apache.xpath.internal.objects.XNumber
 * @see com.sun.org.apache.xpath.internal.objects.XString
 * @see com.sun.org.apache.xpath.internal.objects.XRTreeFrag
 * @throws TransformerException
 */
  public XObject eval(  Node contextNode,  Node xpathnode,  String str,  Node namespaceNode) throws TransformerException {
    if (this._funcHereContext == null) {
      this._funcHereContext=new FuncHereContext(xpathnode,this._dtmManager);
    }
    PrefixResolverDefault prefixResolver=new PrefixResolverDefault((namespaceNode.getNodeType() == Node.DOCUMENT_NODE) ? ((Document)namespaceNode).getDocumentElement() : namespaceNode);
    if (str != xpathStr) {
      if (str.indexOf("here()") > 0) {
        _context.reset();
        _dtmManager=_context.getDTMManager();
      }
      xpath=createXPath(str,prefixResolver);
      xpathStr=str;
    }
    int ctxtNode=this._funcHereContext.getDTMHandleFromNode(contextNode);
    return xpath.execute(this._funcHereContext,ctxtNode,prefixResolver);
  }
  /** 
 * Evaluate XPath string to an XObject.
 * XPath namespace prefixes are resolved from the namespaceNode.
 * The implementation of this is a little slow, since it creates
 * a number of objects each time it is called.  This could be optimized
 * to keep the same objects around, but then thread-safety issues would arise.
 * @param contextNode The node to start searching from.
 * @param xpathnode
 * @param str
 * @param prefixResolver Will be called if the parser encounters namespace
 * prefixes, to resolve the prefixes to URLs.
 * @return An XObject, which can be used to obtain a string, number, nodelist, etc, should never be null.
 * @see com.sun.org.apache.xpath.internal.objects.XObject
 * @see com.sun.org.apache.xpath.internal.objects.XNull
 * @see com.sun.org.apache.xpath.internal.objects.XBoolean
 * @see com.sun.org.apache.xpath.internal.objects.XNumber
 * @see com.sun.org.apache.xpath.internal.objects.XString
 * @see com.sun.org.apache.xpath.internal.objects.XRTreeFrag
 * @throws TransformerException
 */
  public XObject eval(  Node contextNode,  Node xpathnode,  String str,  PrefixResolver prefixResolver) throws TransformerException {
    if (str != xpathStr) {
      if (str.indexOf("here()") > 0) {
        _context.reset();
        _dtmManager=_context.getDTMManager();
      }
      try {
        xpath=createXPath(str,prefixResolver);
      }
 catch (      TransformerException ex) {
        Throwable th=ex.getCause();
        if (th instanceof ClassNotFoundException) {
          if (th.getMessage().indexOf("FuncHere") > 0) {
            throw new RuntimeException(I18n.translate("endorsed.jdk1.4.0") + ex);
          }
        }
        throw ex;
      }
      xpathStr=str;
    }
    if (this._funcHereContext == null) {
      this._funcHereContext=new FuncHereContext(xpathnode,this._dtmManager);
    }
    int ctxtNode=this._funcHereContext.getDTMHandleFromNode(contextNode);
    return xpath.execute(this._funcHereContext,ctxtNode,prefixResolver);
  }
  private XPath createXPath(  String str,  PrefixResolver prefixResolver) throws TransformerException {
    XPath xpath=null;
    Class[] classes=new Class[]{String.class,SourceLocator.class,PrefixResolver.class,int.class,ErrorListener.class,FunctionTable.class};
    Object[] objects=new Object[]{str,null,prefixResolver,new Integer(XPath.SELECT),null,_funcTable};
    try {
      Constructor constructor=XPath.class.getConstructor(classes);
      xpath=(XPath)constructor.newInstance(objects);
    }
 catch (    Throwable t) {
    }
    if (xpath == null) {
      xpath=new XPath(str,null,prefixResolver,XPath.SELECT,null);
    }
    return xpath;
  }
  /** 
 * Method getStrFromNode
 * @param xpathnode
 * @return the string for the node.
 */
  public static String getStrFromNode(  Node xpathnode){
    if (xpathnode.getNodeType() == Node.TEXT_NODE) {
      StringBuffer sb=new StringBuffer();
      for (Node currentSibling=xpathnode.getParentNode().getFirstChild(); currentSibling != null; currentSibling=currentSibling.getNextSibling()) {
        if (currentSibling.getNodeType() == Node.TEXT_NODE) {
          sb.append(((Text)currentSibling).getData());
        }
      }
      return sb.toString();
    }
 else     if (xpathnode.getNodeType() == Node.ATTRIBUTE_NODE) {
      return ((Attr)xpathnode).getNodeValue();
    }
 else     if (xpathnode.getNodeType() == Node.PROCESSING_INSTRUCTION_NODE) {
      return ((ProcessingInstruction)xpathnode).getNodeValue();
    }
    return null;
  }
  private static void fixupFunctionTable(){
    boolean installed=false;
    log.log(java.util.logging.Level.INFO,"Registering Here function");
    try {
      Class[] args={String.class,Expression.class};
      Method installFunction=FunctionTable.class.getMethod("installFunction",args);
      if ((installFunction.getModifiers() & Modifier.STATIC) != 0) {
        Object[] params={"here",new FuncHere()};
        installFunction.invoke(null,params);
        installed=true;
      }
    }
 catch (    Throwable t) {
      log.log(java.util.logging.Level.FINE,"Error installing function using the static installFunction method",t);
    }
    if (!installed) {
      try {
        _funcTable=new FunctionTable();
        Class[] args={String.class,Class.class};
        Method installFunction=FunctionTable.class.getMethod("installFunction",args);
        Object[] params={"here",FuncHere.class};
        installFunction.invoke(_funcTable,params);
        installed=true;
      }
 catch (      Throwable t) {
        log.log(java.util.logging.Level.FINE,"Error installing function using the static installFunction method",t);
      }
    }
    if (log.isLoggable(java.util.logging.Level.FINE)) {
      if (installed) {
        log.log(java.util.logging.Level.FINE,"Registered class " + FuncHere.class.getName() + " for XPath function 'here()' function in internal table");
      }
 else {
        log.log(java.util.logging.Level.FINE,"Unable to register class " + FuncHere.class.getName() + " for XPath function 'here()' function in internal table");
      }
    }
  }
}