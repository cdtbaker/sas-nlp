package sun.security.x509;
import java.lang.reflect.*;
import java.io.IOException;
import java.io.StringReader;
import java.security.PrivilegedExceptionAction;
import java.security.AccessController;
import java.security.Principal;
import java.util.*;
import sun.security.util.*;
import sun.security.pkcs.PKCS9Attribute;
import javax.security.auth.x500.X500Principal;
/** 
 * RDNs are a set of {attribute = value} assertions.  Some of those
 * attributes are "distinguished" (unique w/in context).  Order is
 * never relevant.
 * Some X.500 names include only a single distinguished attribute
 * per RDN.  This style is currently common.
 * Note that DER-encoded RDNs sort AVAs by assertion OID ... so that
 * when we parse this data we don't have to worry about canonicalizing
 * it, but we'll need to sort them when we expose the RDN class more.
 * <p>
 * The ASN.1 for RDNs is:
 * <pre>
 * RelativeDistinguishedName ::=
 * SET OF AttributeTypeAndValue
 * AttributeTypeAndValue ::= SEQUENCE {
 * type     AttributeType,
 * value    AttributeValue }
 * AttributeType ::= OBJECT IDENTIFIER
 * AttributeValue ::= ANY DEFINED BY AttributeType
 * </pre>
 * Note that instances of this class are immutable.
 */
public class RDN {
  final AVA[] assertion;
  private volatile List<AVA> avaList;
  private volatile String canonicalString;
  /** 
 * Constructs an RDN from its printable representation.
 * An RDN may consist of one or multiple Attribute Value Assertions (AVAs),
 * using '+' as a separator.
 * If the '+' should be considered part of an AVA value, it must be
 * preceded by '\'.
 * @param name String form of RDN
 * @throws IOException on parsing error
 */
  public RDN(  String name) throws IOException {
    this(name,Collections.<String,String>emptyMap());
  }
  /** 
 * Constructs an RDN from its printable representation.
 * An RDN may consist of one or multiple Attribute Value Assertions (AVAs),
 * using '+' as a separator.
 * If the '+' should be considered part of an AVA value, it must be
 * preceded by '\'.
 * @param name String form of RDN
 * @param keyword an additional mapping of keywords to OIDs
 * @throws IOException on parsing error
 */
  public RDN(  String name,  Map<String,String> keywordMap) throws IOException {
    int quoteCount=0;
    int searchOffset=0;
    int avaOffset=0;
    List<AVA> avaVec=new ArrayList<AVA>(3);
    int nextPlus=name.indexOf('+');
    while (nextPlus >= 0) {
      quoteCount+=X500Name.countQuotes(name,searchOffset,nextPlus);
      if (nextPlus > 0 && name.charAt(nextPlus - 1) != '\\' && quoteCount != 1) {
        String avaString=name.substring(avaOffset,nextPlus);
        if (avaString.length() == 0) {
          throw new IOException("empty AVA in RDN \"" + name + "\"");
        }
        AVA ava=new AVA(new StringReader(avaString),keywordMap);
        avaVec.add(ava);
        avaOffset=nextPlus + 1;
        quoteCount=0;
      }
      searchOffset=nextPlus + 1;
      nextPlus=name.indexOf('+',searchOffset);
    }
    String avaString=name.substring(avaOffset);
    if (avaString.length() == 0) {
      throw new IOException("empty AVA in RDN \"" + name + "\"");
    }
    AVA ava=new AVA(new StringReader(avaString),keywordMap);
    avaVec.add(ava);
    assertion=avaVec.toArray(new AVA[avaVec.size()]);
  }
  RDN(  String name,  String format) throws IOException {
    this(name,format,Collections.<String,String>emptyMap());
  }
  RDN(  String name,  String format,  Map<String,String> keywordMap) throws IOException {
    if (format.equalsIgnoreCase("RFC2253") == false) {
      throw new IOException("Unsupported format " + format);
    }
    int searchOffset=0;
    int avaOffset=0;
    List<AVA> avaVec=new ArrayList<AVA>(3);
    int nextPlus=name.indexOf('+');
    while (nextPlus >= 0) {
      if (nextPlus > 0 && name.charAt(nextPlus - 1) != '\\') {
        String avaString=name.substring(avaOffset,nextPlus);
        if (avaString.length() == 0) {
          throw new IOException("empty AVA in RDN \"" + name + "\"");
        }
        AVA ava=new AVA(new StringReader(avaString),AVA.RFC2253,keywordMap);
        avaVec.add(ava);
        avaOffset=nextPlus + 1;
      }
      searchOffset=nextPlus + 1;
      nextPlus=name.indexOf('+',searchOffset);
    }
    String avaString=name.substring(avaOffset);
    if (avaString.length() == 0) {
      throw new IOException("empty AVA in RDN \"" + name + "\"");
    }
    AVA ava=new AVA(new StringReader(avaString),AVA.RFC2253,keywordMap);
    avaVec.add(ava);
    assertion=avaVec.toArray(new AVA[avaVec.size()]);
  }
  RDN(  DerValue rdn) throws IOException {
    if (rdn.tag != DerValue.tag_Set) {
      throw new IOException("X500 RDN");
    }
    DerInputStream dis=new DerInputStream(rdn.toByteArray());
    DerValue[] avaset=dis.getSet(5);
    assertion=new AVA[avaset.length];
    for (int i=0; i < avaset.length; i++) {
      assertion[i]=new AVA(avaset[i]);
    }
  }
  RDN(  int i){
    assertion=new AVA[i];
  }
  public RDN(  AVA ava){
    if (ava == null) {
      throw new NullPointerException();
    }
    assertion=new AVA[]{ava};
  }
  public RDN(  AVA[] avas){
    assertion=avas.clone();
    for (int i=0; i < assertion.length; i++) {
      if (assertion[i] == null) {
        throw new NullPointerException();
      }
    }
  }
  /** 
 * Return an immutable List of the AVAs in this RDN.
 */
  public List<AVA> avas(){
    List<AVA> list=avaList;
    if (list == null) {
      list=Collections.unmodifiableList(Arrays.asList(assertion));
      avaList=list;
    }
    return list;
  }
  /** 
 * Return the number of AVAs in this RDN.
 */
  public int size(){
    return assertion.length;
  }
  public boolean equals(  Object obj){
    if (this == obj) {
      return true;
    }
    if (obj instanceof RDN == false) {
      return false;
    }
    RDN other=(RDN)obj;
    if (this.assertion.length != other.assertion.length) {
      return false;
    }
    String thisCanon=this.toRFC2253String(true);
    String otherCanon=other.toRFC2253String(true);
    return thisCanon.equals(otherCanon);
  }
  public int hashCode(){
    return toRFC2253String(true).hashCode();
  }
  DerValue findAttribute(  ObjectIdentifier oid){
    for (int i=0; i < assertion.length; i++) {
      if (assertion[i].oid.equals(oid)) {
        return assertion[i].value;
      }
    }
    return null;
  }
  void encode(  DerOutputStream out) throws IOException {
    out.putOrderedSetOf(DerValue.tag_Set,assertion);
  }
  public String toString(){
    if (assertion.length == 1) {
      return assertion[0].toString();
    }
    StringBuilder sb=new StringBuilder();
    for (int i=0; i < assertion.length; i++) {
      if (i != 0) {
        sb.append(" + ");
      }
      sb.append(assertion[i].toString());
    }
    return sb.toString();
  }
  public String toRFC1779String(){
    return toRFC1779String(Collections.<String,String>emptyMap());
  }
  public String toRFC1779String(  Map<String,String> oidMap){
    if (assertion.length == 1) {
      return assertion[0].toRFC1779String(oidMap);
    }
    StringBuilder sb=new StringBuilder();
    for (int i=0; i < assertion.length; i++) {
      if (i != 0) {
        sb.append(" + ");
      }
      sb.append(assertion[i].toRFC1779String(oidMap));
    }
    return sb.toString();
  }
  public String toRFC2253String(){
    return toRFC2253StringInternal(false,Collections.<String,String>emptyMap());
  }
  public String toRFC2253String(  Map<String,String> oidMap){
    return toRFC2253StringInternal(false,oidMap);
  }
  public String toRFC2253String(  boolean canonical){
    if (canonical == false) {
      return toRFC2253StringInternal(false,Collections.<String,String>emptyMap());
    }
    String c=canonicalString;
    if (c == null) {
      c=toRFC2253StringInternal(true,Collections.<String,String>emptyMap());
      canonicalString=c;
    }
    return c;
  }
  private String toRFC2253StringInternal(  boolean canonical,  Map<String,String> oidMap){
    if (assertion.length == 1) {
      return canonical ? assertion[0].toRFC2253CanonicalString() : assertion[0].toRFC2253String(oidMap);
    }
    StringBuilder relname=new StringBuilder();
    if (!canonical) {
      for (int i=0; i < assertion.length; i++) {
        if (i > 0) {
          relname.append('+');
        }
        relname.append(assertion[i].toRFC2253String(oidMap));
      }
    }
 else {
      List<AVA> avaList=new ArrayList<AVA>(assertion.length);
      for (int i=0; i < assertion.length; i++) {
        avaList.add(assertion[i]);
      }
      java.util.Collections.sort(avaList,AVAComparator.getInstance());
      for (int i=0; i < avaList.size(); i++) {
        if (i > 0) {
          relname.append('+');
        }
        relname.append(avaList.get(i).toRFC2253CanonicalString());
      }
    }
    return relname.toString();
  }
}
class AVAComparator implements Comparator<AVA> {
  private static final Comparator<AVA> INSTANCE=new AVAComparator();
  private AVAComparator(){
  }
  static Comparator<AVA> getInstance(){
    return INSTANCE;
  }
  /** 
 * AVA's containing a standard keyword are ordered alphabetically,
 * followed by AVA's containing an OID keyword, ordered numerically
 */
  public int compare(  AVA a1,  AVA a2){
    boolean a1Has2253=a1.hasRFC2253Keyword();
    boolean a2Has2253=a2.hasRFC2253Keyword();
    if (a1Has2253 == a2Has2253) {
      return a1.toRFC2253CanonicalString().compareTo(a2.toRFC2253CanonicalString());
    }
 else {
      if (a1Has2253) {
        return -1;
      }
 else {
        return 1;
      }
    }
  }
}
