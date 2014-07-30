package org.apache.commons.math3.exception.util;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.Map;
import java.io.IOException;
import java.io.Serializable;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.text.MessageFormat;
import java.util.Locale;
/** 
 * Class that contains the actual implementation of the functionality mandated
 * by the {@link ExceptionContext} interface.
 * All Commons Math exceptions delegate the interface's methods to this class.
 * @since 3.0
 * @version $Id: ExceptionContext.java 1364388 2012-07-22 18:16:43Z tn $
 */
public class ExceptionContext implements Serializable {
  /** 
 * Serializable version Id. 
 */
  private static final long serialVersionUID=-6024911025449780478L;
  /** 
 * The throwable to which this context refers to.
 */
  private Throwable throwable;
  /** 
 * Various informations that enrich the informative message.
 */
  private List<Localizable> msgPatterns;
  /** 
 * Various informations that enrich the informative message.
 * The arguments will replace the corresponding place-holders in{@link #msgPatterns}.
 */
  private List<Object[]> msgArguments;
  /** 
 * Arbitrary context information.
 */
  private Map<String,Object> context;
  /** 
 * Simple constructor.
 * @param throwable the exception this context refers too
 */
  public ExceptionContext(  final Throwable throwable){
    this.throwable=throwable;
    msgPatterns=new ArrayList<Localizable>();
    msgArguments=new ArrayList<Object[]>();
    context=new HashMap<String,Object>();
  }
  /** 
 * Get a reference to the exception to which the context relates.
 * @return a reference to the exception to which the context relates
 */
  public Throwable getThrowable(){
    return throwable;
  }
  /** 
 * Adds a message.
 * @param pattern Message pattern.
 * @param arguments Values for replacing the placeholders in the message
 * pattern.
 */
  public void addMessage(  Localizable pattern,  Object... arguments){
    msgPatterns.add(pattern);
    msgArguments.add(ArgUtils.flatten(arguments));
  }
  /** 
 * Sets the context (key, value) pair.
 * Keys are assumed to be unique within an instance. If the same key is
 * assigned a new value, the previous one will be lost.
 * @param key Context key (not null).
 * @param value Context value.
 */
  public void setValue(  String key,  Object value){
    context.put(key,value);
  }
  /** 
 * Gets the value associated to the given context key.
 * @param key Context key.
 * @return the context value or {@code null} if the key does not exist.
 */
  public Object getValue(  String key){
    return context.get(key);
  }
  /** 
 * Gets all the keys stored in the exception
 * @return the set of keys.
 */
  public Set<String> getKeys(){
    return context.keySet();
  }
  /** 
 * Gets the default message.
 * @return the message.
 */
  public String getMessage(){
    return getMessage(Locale.US);
  }
  /** 
 * Gets the message in the default locale.
 * @return the localized message.
 */
  public String getLocalizedMessage(){
    return getMessage(Locale.getDefault());
  }
  /** 
 * Gets the message in a specified locale.
 * @param locale Locale in which the message should be translated.
 * @return the localized message.
 */
  public String getMessage(  final Locale locale){
    return buildMessage(locale,": ");
  }
  /** 
 * Gets the message in a specified locale.
 * @param locale Locale in which the message should be translated.
 * @param separator Separator inserted between the message parts.
 * @return the localized message.
 */
  public String getMessage(  final Locale locale,  final String separator){
    return buildMessage(locale,separator);
  }
  /** 
 * Builds a message string.
 * @param locale Locale in which the message should be translated.
 * @param separator Message separator.
 * @return a localized message string.
 */
  private String buildMessage(  Locale locale,  String separator){
    final StringBuilder sb=new StringBuilder();
    int count=0;
    final int len=msgPatterns.size();
    for (int i=0; i < len; i++) {
      final Localizable pat=msgPatterns.get(i);
      final Object[] args=msgArguments.get(i);
      final MessageFormat fmt=new MessageFormat(pat.getLocalizedString(locale),locale);
      sb.append(fmt.format(args));
      if (++count < len) {
        sb.append(separator);
      }
    }
    return sb.toString();
  }
  /** 
 * Serialize this object to the given stream.
 * @param out Stream.
 * @throws IOException This should never happen.
 */
  private void writeObject(  ObjectOutputStream out) throws IOException {
    out.writeObject(throwable);
    serializeMessages(out);
    serializeContext(out);
  }
  /** 
 * Deserialize this object from the given stream.
 * @param in Stream.
 * @throws IOException This should never happen.
 * @throws ClassNotFoundException This should never happen.
 */
  private void readObject(  ObjectInputStream in) throws IOException, ClassNotFoundException {
    throwable=(Throwable)in.readObject();
    deSerializeMessages(in);
    deSerializeContext(in);
  }
  /** 
 * Serialize  {@link #msgPatterns} and {@link #msgArguments}.
 * @param out Stream.
 * @throws IOException This should never happen.
 */
  private void serializeMessages(  ObjectOutputStream out) throws IOException {
    final int len=msgPatterns.size();
    out.writeInt(len);
    for (int i=0; i < len; i++) {
      final Localizable pat=msgPatterns.get(i);
      out.writeObject(pat);
      final Object[] args=msgArguments.get(i);
      final int aLen=args.length;
      out.writeInt(aLen);
      for (int j=0; j < aLen; j++) {
        if (args[j] instanceof Serializable) {
          out.writeObject(args[j]);
        }
 else {
          out.writeObject(nonSerializableReplacement(args[j]));
        }
      }
    }
  }
  /** 
 * Deserialize {@link #msgPatterns} and {@link #msgArguments}.
 * @param in Stream.
 * @throws IOException This should never happen.
 * @throws ClassNotFoundException This should never happen.
 */
  private void deSerializeMessages(  ObjectInputStream in) throws IOException, ClassNotFoundException {
    final int len=in.readInt();
    msgPatterns=new ArrayList<Localizable>(len);
    msgArguments=new ArrayList<Object[]>(len);
    for (int i=0; i < len; i++) {
      final Localizable pat=(Localizable)in.readObject();
      msgPatterns.add(pat);
      final int aLen=in.readInt();
      final Object[] args=new Object[aLen];
      for (int j=0; j < aLen; j++) {
        args[j]=in.readObject();
      }
      msgArguments.add(args);
    }
  }
  /** 
 * Serialize {@link #context}.
 * @param out Stream.
 * @throws IOException This should never happen.
 */
  private void serializeContext(  ObjectOutputStream out) throws IOException {
    final int len=context.keySet().size();
    out.writeInt(len);
    for (    String key : context.keySet()) {
      out.writeObject(key);
      final Object value=context.get(key);
      if (value instanceof Serializable) {
        out.writeObject(value);
      }
 else {
        out.writeObject(nonSerializableReplacement(value));
      }
    }
  }
  /** 
 * Deserialize {@link #context}.
 * @param in Stream.
 * @throws IOException This should never happen.
 * @throws ClassNotFoundException This should never happen.
 */
  private void deSerializeContext(  ObjectInputStream in) throws IOException, ClassNotFoundException {
    final int len=in.readInt();
    context=new HashMap<String,Object>();
    for (int i=0; i < len; i++) {
      final String key=(String)in.readObject();
      final Object value=in.readObject();
      context.put(key,value);
    }
  }
  /** 
 * Replaces a non-serializable object with an error message string.
 * @param obj Object that does not implement the {@code Serializable}interface.
 * @return a string that mentions which class could not be serialized.
 */
  private String nonSerializableReplacement(  Object obj){
    return "[Object could not be serialized: " + obj.getClass().getName() + "]";
  }
}