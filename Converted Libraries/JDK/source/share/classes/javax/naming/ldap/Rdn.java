package javax.naming.ldap;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.ArrayList;
import java.util.Collections;
import javax.naming.InvalidNameException;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.Attributes;
import javax.naming.directory.Attribute;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import java.io.Serializable;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;
/** 
 * This class represents a relative distinguished name, or RDN, which is a
 * component of a distinguished name as specified by
 * <a href="http://www.ietf.org/rfc/rfc2253.txt">RFC 2253</a>.
 * An example of an RDN is "OU=Sales+CN=J.Smith". In this example,
 * the RDN consist of multiple attribute type/value pairs. The
 * RDN is parsed as described in the class description for{@link javax.naming.ldap.LdapName <tt>LdapName</tt>}.
 * <p>
 * The Rdn class represents an RDN as attribute type/value mappings,
 * which can be viewed using{@link javax.naming.directory.Attributes Attributes}.
 * In addition, it contains convenience methods that allow easy retrieval
 * of type and value when the Rdn consist of a single type/value pair,
 * which is how it appears in a typical usage.
 * It also contains helper methods that allow escaping of the unformatted
 * attribute value and unescaping of the value formatted according to the
 * escaping syntax defined in RFC2253. For methods that take or return
 * attribute value as an Object, the value is either a String
 * (in unescaped form) or a byte array.
 * <p>
 * <code>Rdn</code> will properly parse all valid RDNs, but
 * does not attempt to detect all possible violations when parsing
 * invalid RDNs. It is "generous" in accepting invalid RDNs.
 * The "validity" of a name is determined ultimately when it
 * is supplied to an LDAP server, which may accept or
 * reject the name based on factors such as its schema information
 * and interoperability considerations.
 * <p>
 * The following code example shows how to construct an Rdn using the
 * constructor that takes type and value as arguments:
 * <pre>
 * Rdn rdn = new Rdn("cn", "Juicy, Fruit");
 * System.out.println(rdn.toString());
 * </pre>
 * The last line will print <tt>cn=Juicy\, Fruit</tt>. The{@link #unescapeValue(String) <tt>unescapeValue()</tt>} method can be
 * used to unescape the escaped comma resulting in the original
 * value <tt>"Juicy, Fruit"</tt>. The {@link #escapeValue(Object)<tt>escapeValue()</tt>} method adds the escape back preceding the comma.
 * <p>
 * This class can be instantiated by a string representation
 * of the RDN defined in RFC 2253 as shown in the following code example:
 * <pre>
 * Rdn rdn = new Rdn("cn=Juicy\\, Fruit");
 * System.out.println(rdn.toString());
 * </pre>
 * The last line will print <tt>cn=Juicy\, Fruit</tt>.
 * <p>
 * Concurrent multithreaded read-only access of an instance of
 * <tt>Rdn</tt> need not be synchronized.
 * <p>
 * Unless otherwise noted, the behavior of passing a null argument
 * to a constructor or method in this class will cause NullPointerException
 * to be thrown.
 * @since 1.5
 */
public class Rdn implements Serializable, Comparable<Object> {
  private transient ArrayList entries;
  private static final int DEFAULT_SIZE=1;
  private static final long serialVersionUID=-5994465067210009656L;
  /** 
 * Constructs an Rdn from the given attribute set. See{@link javax.naming.directory.Attributes Attributes}.
 * <p>
 * The string attribute values are not interpretted as
 * <a href="http://www.ietf.org/rfc/rfc2253.txt">RFC 2253</a>
 * formatted RDN strings. That is, the values are used
 * literally (not parsed) and assumed to be unescaped.
 * @param attrSet The non-null and non-empty attributes containing
 * type/value mappings.
 * @throws InvalidNameException If contents of <tt>attrSet</tt> cannot
 * be used to construct a valid RDN.
 */
  public Rdn(  Attributes attrSet) throws InvalidNameException {
    if (attrSet.size() == 0) {
      throw new InvalidNameException("Attributes cannot be empty");
    }
    entries=new ArrayList(attrSet.size());
    NamingEnumeration attrs=attrSet.getAll();
    try {
      for (int nEntries=0; attrs.hasMore(); nEntries++) {
        RdnEntry entry=new RdnEntry();
        Attribute attr=(Attribute)attrs.next();
        entry.type=attr.getID();
        entry.value=attr.get();
        entries.add(nEntries,entry);
      }
    }
 catch (    NamingException e) {
      InvalidNameException e2=new InvalidNameException(e.getMessage());
      e2.initCause(e);
      throw e2;
    }
    sort();
  }
  /** 
 * Constructs an Rdn from the given string.
 * This constructor takes a string formatted according to the rules
 * defined in <a href="http://www.ietf.org/rfc/rfc2253.txt">RFC 2253</a>
 * and described in the class description for{@link javax.naming.ldap.LdapName}.
 * @param rdnString The non-null and non-empty RFC2253 formatted string.
 * @throws InvalidNameException If a syntax error occurs during
 * parsing of the rdnString.
 */
  public Rdn(  String rdnString) throws InvalidNameException {
    entries=new ArrayList(DEFAULT_SIZE);
    (new Rfc2253Parser(rdnString)).parseRdn(this);
  }
  /** 
 * Constructs an Rdn from the given <tt>rdn</tt>.
 * The contents of the <tt>rdn</tt> are simply copied into the newly
 * created Rdn.
 * @param rdn The non-null Rdn to be copied.
 */
  public Rdn(  Rdn rdn){
    entries=new ArrayList(rdn.entries.size());
    entries.addAll(rdn.entries);
  }
  /** 
 * Constructs an Rdn from the given attribute type and
 * value.
 * The string attribute values are not interpretted as
 * <a href="http://www.ietf.org/rfc/rfc2253.txt">RFC 2253</a>
 * formatted RDN strings. That is, the values are used
 * literally (not parsed) and assumed to be unescaped.
 * @param type The non-null and non-empty string attribute type.
 * @param value The non-null and non-empty attribute value.
 * @throws InvalidNameException If type/value cannot be used to
 * construct a valid RDN.
 * @see #toString()
 */
  public Rdn(  String type,  Object value) throws InvalidNameException {
    if (value == null) {
      throw new NullPointerException("Cannot set value to null");
    }
    if (type.equals("") || isEmptyValue(value)) {
      throw new InvalidNameException("type or value cannot be empty, type:" + type + " value:"+ value);
    }
    entries=new ArrayList(DEFAULT_SIZE);
    put(type,value);
  }
  private boolean isEmptyValue(  Object val){
    return ((val instanceof String) && val.equals("")) || ((val instanceof byte[]) && (((byte[])val).length == 0));
  }
  Rdn(){
    entries=new ArrayList(DEFAULT_SIZE);
  }
  Rdn put(  String type,  Object value){
    RdnEntry newEntry=new RdnEntry();
    newEntry.type=type;
    if (value instanceof byte[]) {
      newEntry.value=((byte[])value).clone();
    }
 else {
      newEntry.value=value;
    }
    entries.add(newEntry);
    return this;
  }
  void sort(){
    if (entries.size() > 1) {
      Collections.sort(entries);
    }
  }
  /** 
 * Retrieves one of this Rdn's value.
 * This is a convenience method for obtaining the value,
 * when the RDN contains a single type and value mapping,
 * which is the common RDN usage.
 * <p>
 * For a multi-valued RDN, this method returns value corresponding
 * to the type returned by {@link #getType() getType()} method.
 * @return The non-null attribute value.
 */
  public Object getValue(){
    return ((RdnEntry)entries.get(0)).getValue();
  }
  /** 
 * Retrieves one of this Rdn's type.
 * This is a convenience method for obtaining the type,
 * when the RDN contains a single type and value mapping,
 * which is the common RDN usage.
 * <p>
 * For a multi-valued RDN, the type/value pairs have
 * no specific order defined on them. In that case, this method
 * returns type of one of the type/value pairs.
 * The {@link #getValue() getValue()} method returns the
 * value corresponding to the type returned by this method.
 * @return The non-null attribute type.
 */
  public String getType(){
    return ((RdnEntry)entries.get(0)).getType();
  }
  /** 
 * Returns this Rdn as a string represented in a format defined by
 * <a href="http://www.ietf.org/rfc/rfc2253.txt">RFC 2253</a> and described
 * in the class description for {@link javax.naming.ldap.LdapName LdapName}.
 * @return The string representation of the Rdn.
 */
  public String toString(){
    StringBuilder builder=new StringBuilder();
    int size=entries.size();
    if (size > 0) {
      builder.append(entries.get(0));
    }
    for (int next=1; next < size; next++) {
      builder.append('+');
      builder.append(entries.get(next));
    }
    return builder.toString();
  }
  /** 
 * Compares this Rdn with the specified Object for order.
 * Returns a negative integer, zero, or a positive integer as this
 * Rdn is less than, equal to, or greater than the given Object.
 * <p>
 * If obj is null or not an instance of Rdn, ClassCastException
 * is thrown.
 * <p>
 * The attribute type and value pairs of the RDNs are lined up
 * against each other and compared lexicographically. The order of
 * components in multi-valued Rdns (such as "ou=Sales+cn=Bob") is not
 * significant.
 * @param obj The non-null object to compare against.
 * @return  A negative integer, zero, or a positive integer as this Rdn
 * is less than, equal to, or greater than the given Object.
 * @exception ClassCastException if obj is null or not a Rdn.
 * <p>
 */
  public int compareTo(  Object obj){
    if (!(obj instanceof Rdn)) {
      throw new ClassCastException("The obj is not a Rdn");
    }
    if (obj == this) {
      return 0;
    }
    Rdn that=(Rdn)obj;
    int minSize=Math.min(entries.size(),that.entries.size());
    for (int i=0; i < minSize; i++) {
      int diff=((RdnEntry)entries.get(i)).compareTo(that.entries.get(i));
      if (diff != 0) {
        return diff;
      }
    }
    return (entries.size() - that.entries.size());
  }
  /** 
 * Compares the specified Object with this Rdn for equality.
 * Returns true if the given object is also a Rdn and the two Rdns
 * represent the same attribute type and value mappings. The order of
 * components in multi-valued Rdns (such as "ou=Sales+cn=Bob") is not
 * significant.
 * <p>
 * Type and value equalilty matching is done as below:
 * <ul>
 * <li> The types are compared for equality with their case ignored.
 * <li> String values with different but equivalent usage of quoting,
 * escaping, or UTF8-hex-encoding are considered equal.
 * The case of the values is ignored during the comparison.
 * </ul>
 * <p>
 * If obj is null or not an instance of Rdn, false is returned.
 * <p>
 * @param obj object to be compared for equality with this Rdn.
 * @return true if the specified object is equal to this Rdn.
 * @see #hashCode()
 */
  public boolean equals(  Object obj){
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof Rdn)) {
      return false;
    }
    Rdn that=(Rdn)obj;
    if (entries.size() != that.size()) {
      return false;
    }
    for (int i=0; i < entries.size(); i++) {
      if (!entries.get(i).equals(that.entries.get(i))) {
        return false;
      }
    }
    return true;
  }
  /** 
 * Returns the hash code of this RDN. Two RDNs that are
 * equal (according to the equals method) will have the same
 * hash code.
 * @return An int representing the hash code of this Rdn.
 * @see #equals
 */
  public int hashCode(){
    int hash=0;
    for (int i=0; i < entries.size(); i++) {
      hash+=entries.get(i).hashCode();
    }
    return hash;
  }
  /** 
 * Retrieves the {@link javax.naming.directory.Attributes Attributes}view of the type/value mappings contained in this Rdn.
 * @return  The non-null attributes containing the type/value
 * mappings of this Rdn.
 */
  public Attributes toAttributes(){
    Attributes attrs=new BasicAttributes(true);
    for (int i=0; i < entries.size(); i++) {
      RdnEntry entry=(RdnEntry)entries.get(i);
      Attribute attr=attrs.put(entry.getType(),entry.getValue());
      if (attr != null) {
        attr.add(entry.getValue());
        attrs.put(attr);
      }
    }
    return attrs;
  }
private static class RdnEntry implements Comparable {
    private String type;
    private Object value;
    private String comparable=null;
    String getType(){
      return type;
    }
    Object getValue(){
      return value;
    }
    public int compareTo(    Object obj){
      RdnEntry that=(RdnEntry)obj;
      int diff=type.toUpperCase().compareTo(that.type.toUpperCase());
      if (diff != 0) {
        return diff;
      }
      if (value.equals(that.value)) {
        return 0;
      }
      return getValueComparable().compareTo(that.getValueComparable());
    }
    public boolean equals(    Object obj){
      if (obj == this) {
        return true;
      }
      if (!(obj instanceof RdnEntry)) {
        return false;
      }
      RdnEntry that=(RdnEntry)obj;
      return (type.equalsIgnoreCase(that.type)) && (getValueComparable().equals(that.getValueComparable()));
    }
    public int hashCode(){
      return (type.toUpperCase().hashCode() + getValueComparable().hashCode());
    }
    public String toString(){
      return type + "=" + escapeValue(value);
    }
    private String getValueComparable(){
      if (comparable != null) {
        return comparable;
      }
      if (value instanceof byte[]) {
        comparable=escapeBinaryValue((byte[])value);
      }
 else {
        comparable=((String)value).toUpperCase();
      }
      return comparable;
    }
  }
  /** 
 * Retrieves the number of attribute type/value pairs in this Rdn.
 * @return The non-negative number of type/value pairs in this Rdn.
 */
  public int size(){
    return entries.size();
  }
  /** 
 * Given the value of an attribute, returns a string escaped according
 * to the rules specified in
 * <a href="http://www.ietf.org/rfc/rfc2253.txt">RFC 2253</a>.
 * <p>
 * For example, if the val is "Sue, Grabbit and Runn", the escaped
 * value returned by this method is "Sue\, Grabbit and Runn".
 * <p>
 * A string value is represented as a String and binary value
 * as a byte array.
 * @param val The non-null object to be escaped.
 * @return Escaped string value.
 * @throws ClassCastException if val is is not a String or byte array.
 */
  public static String escapeValue(  Object val){
    return (val instanceof byte[]) ? escapeBinaryValue((byte[])val) : escapeStringValue((String)val);
  }
  private static final String escapees=",=+<>#;\"\\";
  private static String escapeStringValue(  String val){
    char[] chars=val.toCharArray();
    StringBuilder builder=new StringBuilder(2 * val.length());
    int lead;
    for (lead=0; lead < chars.length; lead++) {
      if (!isWhitespace(chars[lead])) {
        break;
      }
    }
    int trail;
    for (trail=chars.length - 1; trail >= 0; trail--) {
      if (!isWhitespace(chars[trail])) {
        break;
      }
    }
    for (int i=0; i < chars.length; i++) {
      char c=chars[i];
      if ((i < lead) || (i > trail) || (escapees.indexOf(c) >= 0)) {
        builder.append('\\');
      }
      builder.append(c);
    }
    return builder.toString();
  }
  private static String escapeBinaryValue(  byte[] val){
    StringBuilder builder=new StringBuilder(1 + 2 * val.length);
    builder.append("#");
    for (int i=0; i < val.length; i++) {
      byte b=val[i];
      builder.append(Character.forDigit(0xF & (b >>> 4),16));
      builder.append(Character.forDigit(0xF & b,16));
    }
    return builder.toString();
  }
  /** 
 * Given an attribute value string formated according to the rules
 * specified in
 * <a href="http://www.ietf.org/rfc/rfc2253.txt">RFC 2253</a>,
 * returns the unformated value.  Escapes and quotes are
 * stripped away, and hex-encoded UTF-8 is converted to equivalent
 * UTF-16 characters. Returns a string value as a String, and a
 * binary value as a byte array.
 * <p>
 * Legal and illegal values are defined in RFC 2253.
 * This method is generous in accepting the values and does not
 * catch all illegal values.
 * Therefore, passing in an illegal value might not necessarily
 * trigger an <tt>IllegalArgumentException</tt>.
 * @param val     The non-null string to be unescaped.
 * @return          Unescaped value.
 * @throws IllegalArgumentException When an Illegal value
 * is provided.
 */
  public static Object unescapeValue(  String val){
    char[] chars=val.toCharArray();
    int beg=0;
    int end=chars.length;
    while ((beg < end) && isWhitespace(chars[beg])) {
      ++beg;
    }
    while ((beg < end) && isWhitespace(chars[end - 1])) {
      --end;
    }
    if (end != chars.length && (beg < end) && chars[end - 1] == '\\') {
      end++;
    }
    if (beg >= end) {
      return "";
    }
    if (chars[beg] == '#') {
      return decodeHexPairs(chars,++beg,end);
    }
    if ((chars[beg] == '\"') && (chars[end - 1] == '\"')) {
      ++beg;
      --end;
    }
    StringBuilder builder=new StringBuilder(end - beg);
    int esc=-1;
    for (int i=beg; i < end; i++) {
      if ((chars[i] == '\\') && (i + 1 < end)) {
        if (!Character.isLetterOrDigit(chars[i + 1])) {
          ++i;
          builder.append(chars[i]);
          esc=i;
        }
 else {
          byte[] utf8=getUtf8Octets(chars,i,end);
          if (utf8.length > 0) {
            try {
              builder.append(new String(utf8,"UTF8"));
            }
 catch (            java.io.UnsupportedEncodingException e) {
            }
            i+=utf8.length * 3 - 1;
          }
 else {
            throw new IllegalArgumentException("Not a valid attribute string value:" + val + ",improper usage of backslash");
          }
        }
      }
 else {
        builder.append(chars[i]);
      }
    }
    int len=builder.length();
    if (isWhitespace(builder.charAt(len - 1)) && esc != (end - 1)) {
      builder.setLength(len - 1);
    }
    return builder.toString();
  }
  private static byte[] decodeHexPairs(  char[] chars,  int beg,  int end){
    byte[] bytes=new byte[(end - beg) / 2];
    for (int i=0; beg + 1 < end; i++) {
      int hi=Character.digit(chars[beg],16);
      int lo=Character.digit(chars[beg + 1],16);
      if (hi < 0 || lo < 0) {
        break;
      }
      bytes[i]=(byte)((hi << 4) + lo);
      beg+=2;
    }
    if (beg != end) {
      throw new IllegalArgumentException("Illegal attribute value: " + new String(chars));
    }
    return bytes;
  }
  private static byte[] getUtf8Octets(  char[] chars,  int beg,  int end){
    byte[] utf8=new byte[(end - beg) / 3];
    int len=0;
    while ((beg + 2 < end) && (chars[beg++] == '\\')) {
      int hi=Character.digit(chars[beg++],16);
      int lo=Character.digit(chars[beg++],16);
      if (hi < 0 || lo < 0) {
        break;
      }
      utf8[len++]=(byte)((hi << 4) + lo);
    }
    if (len == utf8.length) {
      return utf8;
    }
 else {
      byte[] res=new byte[len];
      System.arraycopy(utf8,0,res,0,len);
      return res;
    }
  }
  private static boolean isWhitespace(  char c){
    return (c == ' ' || c == '\r');
  }
  /** 
 * Serializes only the unparsed RDN, for compactness and to avoid
 * any implementation dependency.
 * @serialData      The RDN string
 */
  private void writeObject(  ObjectOutputStream s) throws java.io.IOException {
    s.defaultWriteObject();
    s.writeObject(toString());
  }
  private void readObject(  ObjectInputStream s) throws IOException, ClassNotFoundException {
    s.defaultReadObject();
    entries=new ArrayList(DEFAULT_SIZE);
    String unparsed=(String)s.readObject();
    try {
      (new Rfc2253Parser(unparsed)).parseRdn(this);
    }
 catch (    InvalidNameException e) {
      throw new java.io.StreamCorruptedException("Invalid name: " + unparsed);
    }
  }
}
