package com.sun.beans.decoder;
import com.sun.beans.finder.ClassFinder;
import java.beans.ExceptionListener;
import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
/** 
 * The main class to parse JavaBeans XML archive.
 * @since 1.7
 * @author Sergey A. Malenkov
 * @see ElementHandler
 */
public final class DocumentHandler extends DefaultHandler {
  private final Map<String,Class<? extends ElementHandler>> handlers=new HashMap<String,Class<? extends ElementHandler>>();
  private final Map<String,Object> environment=new HashMap<String,Object>();
  private final List<Object> objects=new ArrayList<Object>();
  private Reference<ClassLoader> loader;
  private ExceptionListener listener;
  private Object owner;
  private ElementHandler handler;
  /** 
 * Creates new instance of document handler.
 */
  public DocumentHandler(){
    setElementHandler("java",JavaElementHandler.class);
    setElementHandler("null",NullElementHandler.class);
    setElementHandler("array",ArrayElementHandler.class);
    setElementHandler("class",ClassElementHandler.class);
    setElementHandler("string",StringElementHandler.class);
    setElementHandler("object",ObjectElementHandler.class);
    setElementHandler("void",VoidElementHandler.class);
    setElementHandler("char",CharElementHandler.class);
    setElementHandler("byte",ByteElementHandler.class);
    setElementHandler("short",ShortElementHandler.class);
    setElementHandler("int",IntElementHandler.class);
    setElementHandler("long",LongElementHandler.class);
    setElementHandler("float",FloatElementHandler.class);
    setElementHandler("double",DoubleElementHandler.class);
    setElementHandler("boolean",BooleanElementHandler.class);
    setElementHandler("new",NewElementHandler.class);
    setElementHandler("var",VarElementHandler.class);
    setElementHandler("true",TrueElementHandler.class);
    setElementHandler("false",FalseElementHandler.class);
    setElementHandler("field",FieldElementHandler.class);
    setElementHandler("method",MethodElementHandler.class);
    setElementHandler("property",PropertyElementHandler.class);
  }
  /** 
 * Returns the class loader used to instantiate objects.
 * If the class loader has not been explicitly set
 * then {@code null} is returned.
 * @return the class loader used to instantiate objects
 */
  public ClassLoader getClassLoader(){
    return (this.loader != null) ? this.loader.get() : null;
  }
  /** 
 * Sets the class loader used to instantiate objects.
 * If the class loader is not set
 * then default class loader will be used.
 * @param loader  a classloader to use
 */
  public void setClassLoader(  ClassLoader loader){
    this.loader=new WeakReference<ClassLoader>(loader);
  }
  /** 
 * Returns the exception listener for parsing.
 * The exception listener is notified
 * when handler catches recoverable exceptions.
 * If the exception listener has not been explicitly set
 * then default exception listener is returned.
 * @return the exception listener for parsing
 */
  public ExceptionListener getExceptionListener(){
    return this.listener;
  }
  /** 
 * Sets the exception listener for parsing.
 * The exception listener is notified
 * when handler catches recoverable exceptions.
 * @param listener  the exception listener for parsing
 */
  public void setExceptionListener(  ExceptionListener listener){
    this.listener=listener;
  }
  /** 
 * Returns the owner of this document handler.
 * @return the owner of this document handler
 */
  public Object getOwner(){
    return this.owner;
  }
  /** 
 * Sets the owner of this document handler.
 * @param owner  the owner of this document handler
 */
  public void setOwner(  Object owner){
    this.owner=owner;
  }
  /** 
 * Returns the handler for the element with specified name.
 * @param name  the name of the element
 * @return the corresponding element handler
 */
  public Class<? extends ElementHandler> getElementHandler(  String name){
    Class<? extends ElementHandler> type=this.handlers.get(name);
    if (type == null) {
      throw new IllegalArgumentException("Unsupported element: " + name);
    }
    return type;
  }
  /** 
 * Sets the handler for the element with specified name.
 * @param name     the name of the element
 * @param handler  the corresponding element handler
 */
  public void setElementHandler(  String name,  Class<? extends ElementHandler> handler){
    this.handlers.put(name,handler);
  }
  /** 
 * Indicates whether the variable with specified identifier is defined.
 * @param id  the identifier
 * @return @{code true} if the variable is defined;
 * @{code false} otherwise
 */
  public boolean hasVariable(  String id){
    return this.environment.containsKey(id);
  }
  /** 
 * Returns the value of the variable with specified identifier.
 * @param id  the identifier
 * @return the value of the variable
 */
  public Object getVariable(  String id){
    if (!this.environment.containsKey(id)) {
      throw new IllegalArgumentException("Unbound variable: " + id);
    }
    return this.environment.get(id);
  }
  /** 
 * Sets new value of the variable with specified identifier.
 * @param id     the identifier
 * @param value  new value of the variable
 */
  public void setVariable(  String id,  Object value){
    this.environment.put(id,value);
  }
  /** 
 * Returns the array of readed objects.
 * @return the array of readed objects
 */
  public Object[] getObjects(){
    return this.objects.toArray();
  }
  /** 
 * Adds the object to the list of readed objects.
 * @param object  the object that is readed from XML document
 */
  void addObject(  Object object){
    this.objects.add(object);
  }
  /** 
 * Prepares this handler to read objects from XML document.
 */
  @Override public void startDocument(){
    this.objects.clear();
    this.handler=null;
  }
  /** 
 * Parses opening tag of XML element
 * using corresponding element handler.
 * @param uri         the namespace URI, or the empty string
 * if the element has no namespace URI or
 * if namespace processing is not being performed
 * @param localName   the local name (without prefix), or the empty string
 * if namespace processing is not being performed
 * @param qName       the qualified name (with prefix), or the empty string
 * if qualified names are not available
 * @param attributes  the attributes attached to the element
 */
  @Override public void startElement(  String uri,  String localName,  String qName,  Attributes attributes) throws SAXException {
    ElementHandler parent=this.handler;
    try {
      this.handler=getElementHandler(qName).newInstance();
      this.handler.setOwner(this);
      this.handler.setParent(parent);
    }
 catch (    Exception exception) {
      throw new SAXException(exception);
    }
    for (int i=0; i < attributes.getLength(); i++)     try {
      String name=attributes.getQName(i);
      String value=attributes.getValue(i);
      this.handler.addAttribute(name,value);
    }
 catch (    RuntimeException exception) {
      handleException(exception);
    }
    this.handler.startElement();
  }
  /** 
 * Parses closing tag of XML element
 * using corresponding element handler.
 * @param uri        the namespace URI, or the empty string
 * if the element has no namespace URI or
 * if namespace processing is not being performed
 * @param localName  the local name (without prefix), or the empty string
 * if namespace processing is not being performed
 * @param qName      the qualified name (with prefix), or the empty string
 * if qualified names are not available
 */
  @Override public void endElement(  String uri,  String localName,  String qName){
    try {
      this.handler.endElement();
    }
 catch (    RuntimeException exception) {
      handleException(exception);
    }
 finally {
      this.handler=this.handler.getParent();
    }
  }
  /** 
 * Parses character data inside XML element.
 * @param chars   the array of characters
 * @param start   the start position in the character array
 * @param length  the number of characters to use
 */
  @Override public void characters(  char[] chars,  int start,  int length){
    if (this.handler != null) {
      try {
        while (0 < length--) {
          this.handler.addCharacter(chars[start++]);
        }
      }
 catch (      RuntimeException exception) {
        handleException(exception);
      }
    }
  }
  /** 
 * Handles an exception using current exception listener.
 * @param exception  an exception to handle
 * @see #setExceptionListener
 */
  public void handleException(  Exception exception){
    if (this.listener == null) {
      throw new IllegalStateException(exception);
    }
    this.listener.exceptionThrown(exception);
  }
  /** 
 * Starts parsing of the specified input source.
 * @param input  the input source to parse
 */
  public void parse(  InputSource input){
    try {
      SAXParserFactory.newInstance().newSAXParser().parse(input,this);
    }
 catch (    ParserConfigurationException exception) {
      handleException(exception);
    }
catch (    SAXException wrapper) {
      Exception exception=wrapper.getException();
      if (exception == null) {
        exception=wrapper;
      }
      handleException(exception);
    }
catch (    IOException exception) {
      handleException(exception);
    }
  }
  /** 
 * Resolves class by name using current class loader.
 * This method handles exception using current exception listener.
 * @param name  the name of the class
 * @return the object that represents the class
 */
  public Class<?> findClass(  String name){
    try {
      return ClassFinder.resolveClass(name,getClassLoader());
    }
 catch (    ClassNotFoundException exception) {
      handleException(exception);
      return null;
    }
  }
}
