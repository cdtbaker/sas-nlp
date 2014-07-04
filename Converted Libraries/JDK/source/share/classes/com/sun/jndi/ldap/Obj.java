package com.sun.jndi.ldap;
import javax.naming.*;
import javax.naming.directory.*;
import javax.naming.spi.DirectoryManager;
import javax.naming.spi.DirStateFactory;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Vector;
import java.util.StringTokenizer;
import sun.misc.BASE64Encoder;
import sun.misc.BASE64Decoder;
import java.lang.reflect.Proxy;
import java.lang.reflect.Modifier;
/** 
 * Class containing static methods and constants for dealing with
 * encoding/decoding JNDI References and Serialized Objects
 * in LDAP.
 * @author Vincent Ryan
 * @author Rosanna Lee
 */
final class Obj {
  private Obj(){
  }
  static VersionHelper helper=VersionHelper.getVersionHelper();
  static final String[] JAVA_ATTRIBUTES={"objectClass","javaSerializedData","javaClassName","javaFactory","javaCodeBase","javaReferenceAddress","javaClassNames","javaRemoteLocation"};
  static final int OBJECT_CLASS=0;
  static final int SERIALIZED_DATA=1;
  static final int CLASSNAME=2;
  static final int FACTORY=3;
  static final int CODEBASE=4;
  static final int REF_ADDR=5;
  static final int TYPENAME=6;
  /** 
 * @deprecated
 */
  private static final int REMOTE_LOC=7;
  static final String[] JAVA_OBJECT_CLASSES={"javaContainer","javaObject","javaNamingReference","javaSerializedObject","javaMarshalledObject"};
  static final String[] JAVA_OBJECT_CLASSES_LOWER={"javacontainer","javaobject","javanamingreference","javaserializedobject","javamarshalledobject"};
  static final int STRUCTURAL=0;
  static final int BASE_OBJECT=1;
  static final int REF_OBJECT=2;
  static final int SER_OBJECT=3;
  static final int MAR_OBJECT=4;
  /** 
 * Encode an object in LDAP attributes.
 * Supports binding Referenceable or Reference, Serializable,
 * and DirContext.
 * If the object supports the Referenceable interface then encode
 * the reference to the object. See encodeReference() for details.
 * <p>
 * If the object is serializable, it is stored as follows:
 * javaClassName
 * value: Object.getClass();
 * javaSerializedData
 * value: serialized form of Object (in binary form).
 * javaTypeName
 * value: getTypeNames(Object.getClass());
 */
  private static Attributes encodeObject(  char separator,  Object obj,  Attributes attrs,  Attribute objectClass,  boolean cloned) throws NamingException {
    boolean structural=(objectClass.size() == 0 || (objectClass.size() == 1 && objectClass.contains("top")));
    if (structural) {
      objectClass.add(JAVA_OBJECT_CLASSES[STRUCTURAL]);
    }
    if (obj instanceof Referenceable) {
      objectClass.add(JAVA_OBJECT_CLASSES[BASE_OBJECT]);
      objectClass.add(JAVA_OBJECT_CLASSES[REF_OBJECT]);
      if (!cloned) {
        attrs=(Attributes)attrs.clone();
      }
      attrs.put(objectClass);
      return (encodeReference(separator,((Referenceable)obj).getReference(),attrs,obj));
    }
 else     if (obj instanceof Reference) {
      objectClass.add(JAVA_OBJECT_CLASSES[BASE_OBJECT]);
      objectClass.add(JAVA_OBJECT_CLASSES[REF_OBJECT]);
      if (!cloned) {
        attrs=(Attributes)attrs.clone();
      }
      attrs.put(objectClass);
      return (encodeReference(separator,(Reference)obj,attrs,null));
    }
 else     if (obj instanceof java.io.Serializable) {
      objectClass.add(JAVA_OBJECT_CLASSES[BASE_OBJECT]);
      if (!(objectClass.contains(JAVA_OBJECT_CLASSES[MAR_OBJECT]) || objectClass.contains(JAVA_OBJECT_CLASSES_LOWER[MAR_OBJECT]))) {
        objectClass.add(JAVA_OBJECT_CLASSES[SER_OBJECT]);
      }
      if (!cloned) {
        attrs=(Attributes)attrs.clone();
      }
      attrs.put(objectClass);
      attrs.put(new BasicAttribute(JAVA_ATTRIBUTES[SERIALIZED_DATA],serializeObject(obj)));
      if (attrs.get(JAVA_ATTRIBUTES[CLASSNAME]) == null) {
        attrs.put(JAVA_ATTRIBUTES[CLASSNAME],obj.getClass().getName());
      }
      if (attrs.get(JAVA_ATTRIBUTES[TYPENAME]) == null) {
        Attribute tAttr=LdapCtxFactory.createTypeNameAttr(obj.getClass());
        if (tAttr != null) {
          attrs.put(tAttr);
        }
      }
    }
 else     if (obj instanceof DirContext) {
    }
 else {
      throw new IllegalArgumentException("can only bind Referenceable, Serializable, DirContext");
    }
    return attrs;
  }
  /** 
 * Each value in javaCodebase contains a list of space-separated
 * URLs. Each value is independent; we can pick any of the values
 * so we just use the first one.
 * @return an array of URL strings for the codebase
 */
  private static String[] getCodebases(  Attribute codebaseAttr) throws NamingException {
    if (codebaseAttr == null) {
      return null;
    }
 else {
      StringTokenizer parser=new StringTokenizer((String)codebaseAttr.get());
      Vector vec=new Vector(10);
      while (parser.hasMoreTokens()) {
        vec.addElement(parser.nextToken());
      }
      String[] answer=new String[vec.size()];
      for (int i=0; i < answer.length; i++) {
        answer[i]=(String)vec.elementAt(i);
      }
      return answer;
    }
  }
  static Object decodeObject(  Attributes attrs) throws NamingException {
    Attribute attr;
    String[] codebases=getCodebases(attrs.get(JAVA_ATTRIBUTES[CODEBASE]));
    try {
      if ((attr=attrs.get(JAVA_ATTRIBUTES[SERIALIZED_DATA])) != null) {
        ClassLoader cl=helper.getURLClassLoader(codebases);
        return deserializeObject((byte[])attr.get(),cl);
      }
 else       if ((attr=attrs.get(JAVA_ATTRIBUTES[REMOTE_LOC])) != null) {
        return decodeRmiObject((String)attrs.get(JAVA_ATTRIBUTES[CLASSNAME]).get(),(String)attr.get(),codebases);
      }
      attr=attrs.get(JAVA_ATTRIBUTES[OBJECT_CLASS]);
      if (attr != null && (attr.contains(JAVA_OBJECT_CLASSES[REF_OBJECT]) || attr.contains(JAVA_OBJECT_CLASSES_LOWER[REF_OBJECT]))) {
        return decodeReference(attrs,codebases);
      }
      return null;
    }
 catch (    IOException e) {
      NamingException ne=new NamingException();
      ne.setRootCause(e);
      throw ne;
    }
  }
  /** 
 * Convert a Reference object into several LDAP attributes.
 * A Reference is stored as into the following attributes:
 * javaClassName
 * value: Reference.getClassName();
 * javaFactory
 * value: Reference.getFactoryClassName();
 * javaCodeBase
 * value: Reference.getFactoryClassLocation();
 * javaReferenceAddress
 * value: #0#typeA#valA
 * value: #1#typeB#valB
 * value: #2#typeC##[serialized RefAddr C]
 * value: #3#typeD#valD
 * where
 * -  the first character denotes the separator
 * -  the number following the first separator denotes the position
 * of the RefAddr within the Reference
 * -  "typeA" is RefAddr.getType()
 * -  ## denotes that the Base64-encoded form of the non-StringRefAddr
 * is to follow; otherwise the value that follows is
 * StringRefAddr.getContents()
 * The default separator is the hash character (#).
 * May provide property for this in future.
 */
  private static Attributes encodeReference(  char separator,  Reference ref,  Attributes attrs,  Object orig) throws NamingException {
    if (ref == null)     return attrs;
    String s;
    if ((s=ref.getClassName()) != null) {
      attrs.put(new BasicAttribute(JAVA_ATTRIBUTES[CLASSNAME],s));
    }
    if ((s=ref.getFactoryClassName()) != null) {
      attrs.put(new BasicAttribute(JAVA_ATTRIBUTES[FACTORY],s));
    }
    if ((s=ref.getFactoryClassLocation()) != null) {
      attrs.put(new BasicAttribute(JAVA_ATTRIBUTES[CODEBASE],s));
    }
    if (orig != null && attrs.get(JAVA_ATTRIBUTES[TYPENAME]) != null) {
      Attribute tAttr=LdapCtxFactory.createTypeNameAttr(orig.getClass());
      if (tAttr != null) {
        attrs.put(tAttr);
      }
    }
    int count=ref.size();
    if (count > 0) {
      Attribute refAttr=new BasicAttribute(JAVA_ATTRIBUTES[REF_ADDR]);
      RefAddr refAddr;
      BASE64Encoder encoder=null;
      for (int i=0; i < count; i++) {
        refAddr=ref.get(i);
        if (refAddr instanceof StringRefAddr) {
          refAttr.add("" + separator + i+ separator+ refAddr.getType()+ separator+ refAddr.getContent());
        }
 else {
          if (encoder == null)           encoder=new BASE64Encoder();
          refAttr.add("" + separator + i+ separator+ refAddr.getType()+ separator+ separator+ encoder.encodeBuffer(serializeObject(refAddr)));
        }
      }
      attrs.put(refAttr);
    }
    return attrs;
  }
  private static Object decodeRmiObject(  String className,  String rmiName,  String[] codebases) throws NamingException {
    return new Reference(className,new StringRefAddr("URL",rmiName));
  }
  private static Reference decodeReference(  Attributes attrs,  String[] codebases) throws NamingException, IOException {
    Attribute attr;
    String className;
    String factory=null;
    if ((attr=attrs.get(JAVA_ATTRIBUTES[CLASSNAME])) != null) {
      className=(String)attr.get();
    }
 else {
      throw new InvalidAttributesException(JAVA_ATTRIBUTES[CLASSNAME] + " attribute is required");
    }
    if ((attr=attrs.get(JAVA_ATTRIBUTES[FACTORY])) != null) {
      factory=(String)attr.get();
    }
    Reference ref=new Reference(className,factory,(codebases != null ? codebases[0] : null));
    if ((attr=attrs.get(JAVA_ATTRIBUTES[REF_ADDR])) != null) {
      String val, posnStr, type;
      char separator;
      int start, sep, posn;
      BASE64Decoder decoder=null;
      ClassLoader cl=helper.getURLClassLoader(codebases);
      Vector refAddrList=new Vector();
      refAddrList.setSize(attr.size());
      for (NamingEnumeration vals=attr.getAll(); vals.hasMore(); ) {
        val=(String)vals.next();
        if (val.length() == 0) {
          throw new InvalidAttributeValueException("malformed " + JAVA_ATTRIBUTES[REF_ADDR] + " attribute - "+ "empty attribute value");
        }
        separator=val.charAt(0);
        start=1;
        if ((sep=val.indexOf(separator,start)) < 0) {
          throw new InvalidAttributeValueException("malformed " + JAVA_ATTRIBUTES[REF_ADDR] + " attribute - "+ "separator '"+ separator+ "'"+ "not found");
        }
        if ((posnStr=val.substring(start,sep)) == null) {
          throw new InvalidAttributeValueException("malformed " + JAVA_ATTRIBUTES[REF_ADDR] + " attribute - "+ "empty RefAddr position");
        }
        try {
          posn=Integer.parseInt(posnStr);
        }
 catch (        NumberFormatException nfe) {
          throw new InvalidAttributeValueException("malformed " + JAVA_ATTRIBUTES[REF_ADDR] + " attribute - "+ "RefAddr position not an integer");
        }
        start=sep + 1;
        if ((sep=val.indexOf(separator,start)) < 0) {
          throw new InvalidAttributeValueException("malformed " + JAVA_ATTRIBUTES[REF_ADDR] + " attribute - "+ "RefAddr type not found");
        }
        if ((type=val.substring(start,sep)) == null) {
          throw new InvalidAttributeValueException("malformed " + JAVA_ATTRIBUTES[REF_ADDR] + " attribute - "+ "empty RefAddr type");
        }
        start=sep + 1;
        if (start == val.length()) {
          refAddrList.setElementAt(new StringRefAddr(type,null),posn);
        }
 else         if (val.charAt(start) == separator) {
          ++start;
          if (decoder == null)           decoder=new BASE64Decoder();
          RefAddr ra=(RefAddr)deserializeObject(decoder.decodeBuffer(val.substring(start)),cl);
          refAddrList.setElementAt(ra,posn);
        }
 else {
          refAddrList.setElementAt(new StringRefAddr(type,val.substring(start)),posn);
        }
      }
      for (int i=0; i < refAddrList.size(); i++) {
        ref.add((RefAddr)refAddrList.elementAt(i));
      }
    }
    return (ref);
  }
  private static byte[] serializeObject(  Object obj) throws NamingException {
    try {
      ByteArrayOutputStream bytes=new ByteArrayOutputStream();
      ObjectOutputStream serial=new ObjectOutputStream(bytes);
      serial.writeObject(obj);
      serial.close();
      return (bytes.toByteArray());
    }
 catch (    IOException e) {
      NamingException ne=new NamingException();
      ne.setRootCause(e);
      throw ne;
    }
  }
  private static Object deserializeObject(  byte[] obj,  ClassLoader cl) throws NamingException {
    try {
      ByteArrayInputStream bytes=new ByteArrayInputStream(obj);
      ObjectInputStream deserial=(cl == null ? new ObjectInputStream(bytes) : new LoaderInputStream(bytes,cl));
      try {
        return deserial.readObject();
      }
 catch (      ClassNotFoundException e) {
        NamingException ne=new NamingException();
        ne.setRootCause(e);
        throw ne;
      }
 finally {
        deserial.close();
      }
    }
 catch (    IOException e) {
      NamingException ne=new NamingException();
      ne.setRootCause(e);
      throw ne;
    }
  }
  /** 
 * Returns the attributes to bind given an object and its attributes.
 */
  static Attributes determineBindAttrs(  char separator,  Object obj,  Attributes attrs,  boolean cloned,  Name name,  Context ctx,  Hashtable env) throws NamingException {
    DirStateFactory.Result res=DirectoryManager.getStateToBind(obj,name,ctx,env,attrs);
    obj=res.getObject();
    attrs=res.getAttributes();
    if (obj == null) {
      return attrs;
    }
    if ((attrs == null) && (obj instanceof DirContext)) {
      cloned=true;
      attrs=((DirContext)obj).getAttributes("");
    }
    boolean ocNeedsCloning=false;
    Attribute objectClass;
    if (attrs == null || attrs.size() == 0) {
      attrs=new BasicAttributes(LdapClient.caseIgnore);
      cloned=true;
      objectClass=new BasicAttribute("objectClass","top");
    }
 else {
      objectClass=(Attribute)attrs.get("objectClass");
      if (objectClass == null && !attrs.isCaseIgnored()) {
        objectClass=(Attribute)attrs.get("objectclass");
      }
      if (objectClass == null) {
        objectClass=new BasicAttribute("objectClass","top");
      }
 else       if (ocNeedsCloning || !cloned) {
        objectClass=(Attribute)objectClass.clone();
      }
    }
    attrs=encodeObject(separator,obj,attrs,objectClass,cloned);
    return attrs;
  }
  /** 
 * An ObjectInputStream that uses a class loader to find classes.
 */
private static final class LoaderInputStream extends ObjectInputStream {
    private ClassLoader classLoader;
    LoaderInputStream(    InputStream in,    ClassLoader cl) throws IOException {
      super(in);
      classLoader=cl;
    }
    protected Class resolveClass(    ObjectStreamClass desc) throws IOException, ClassNotFoundException {
      try {
        return classLoader.loadClass(desc.getName());
      }
 catch (      ClassNotFoundException e) {
        return super.resolveClass(desc);
      }
    }
    protected Class resolveProxyClass(    String[] interfaces) throws IOException, ClassNotFoundException {
      ClassLoader nonPublicLoader=null;
      boolean hasNonPublicInterface=false;
      Class[] classObjs=new Class[interfaces.length];
      for (int i=0; i < interfaces.length; i++) {
        Class cl=Class.forName(interfaces[i],false,classLoader);
        if ((cl.getModifiers() & Modifier.PUBLIC) == 0) {
          if (hasNonPublicInterface) {
            if (nonPublicLoader != cl.getClassLoader()) {
              throw new IllegalAccessError("conflicting non-public interface class loaders");
            }
          }
 else {
            nonPublicLoader=cl.getClassLoader();
            hasNonPublicInterface=true;
          }
        }
        classObjs[i]=cl;
      }
      try {
        return Proxy.getProxyClass(hasNonPublicInterface ? nonPublicLoader : classLoader,classObjs);
      }
 catch (      IllegalArgumentException e) {
        throw new ClassNotFoundException(null,e);
      }
    }
  }
}
