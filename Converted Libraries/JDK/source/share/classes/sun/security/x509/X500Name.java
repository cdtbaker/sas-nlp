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
 * Note:  As of 1.4, the public class,
 * javax.security.auth.x500.X500Principal,
 * should be used when parsing, generating, and comparing X.500 DNs.
 * This class contains other useful methods for checking name constraints
 * and retrieving DNs by keyword.
 * <p> X.500 names are used to identify entities, such as those which are
 * identified by X.509 certificates.  They are world-wide, hierarchical,
 * and descriptive.  Entities can be identified by attributes, and in
 * some systems can be searched for according to those attributes.
 * <p>
 * The ASN.1 for this is:
 * <pre>
 * GeneralName ::= CHOICE {
 * ....
 * directoryName                   [4]     Name,
 * ....
 * Name ::= CHOICE {
 * RDNSequence }
 * RDNSequence ::= SEQUENCE OF RelativeDistinguishedName
 * RelativeDistinguishedName ::=
 * SET OF AttributeTypeAndValue
 * AttributeTypeAndValue ::= SEQUENCE {
 * type     AttributeType,
 * value    AttributeValue }
 * AttributeType ::= OBJECT IDENTIFIER
 * AttributeValue ::= ANY DEFINED BY AttributeType
 * ....
 * DirectoryString ::= CHOICE {
 * teletexString           TeletexString (SIZE (1..MAX)),
 * printableString         PrintableString (SIZE (1..MAX)),
 * universalString         UniversalString (SIZE (1..MAX)),
 * utf8String              UTF8String (SIZE (1.. MAX)),
 * bmpString               BMPString (SIZE (1..MAX)) }
 * </pre>
 * <p>
 * This specification requires only a subset of the name comparison
 * functionality specified in the X.500 series of specifications.  The
 * requirements for conforming implementations are as follows:
 * <ol TYPE=a>
 * <li>attribute values encoded in different types (e.g.,
 * PrintableString and BMPString) may be assumed to represent
 * different strings;
 * <p>
 * <li>attribute values in types other than PrintableString are case
 * sensitive (this permits matching of attribute values as binary
 * objects);
 * <p>
 * <li>attribute values in PrintableString are not case sensitive
 * (e.g., "Marianne Swanson" is the same as "MARIANNE SWANSON"); and
 * <p>
 * <li>attribute values in PrintableString are compared after
 * removing leading and trailing white space and converting internal
 * substrings of one or more consecutive white space characters to a
 * single space.
 * </ol>
 * <p>
 * These name comparison rules permit a certificate user to validate
 * certificates issued using languages or encodings unfamiliar to the
 * certificate user.
 * <p>
 * In addition, implementations of this specification MAY use these
 * comparison rules to process unfamiliar attribute types for name
 * chaining. This allows implementations to process certificates with
 * unfamiliar attributes in the issuer name.
 * <p>
 * Note that the comparison rules defined in the X.500 series of
 * specifications indicate that the character sets used to encode data
 * in distinguished names are irrelevant.  The characters themselves are
 * compared without regard to encoding. Implementations of the profile
 * are permitted to use the comparison algorithm defined in the X.500
 * series.  Such an implementation will recognize a superset of name
 * matches recognized by the algorithm specified above.
 * <p>
 * Note that instances of this class are immutable.
 * @author David Brownell
 * @author Amit Kapoor
 * @author Hemma Prafullchandra
 * @see GeneralName
 * @see GeneralNames
 * @see GeneralNameInterface
 */
public class X500Name implements GeneralNameInterface, Principal {
  private String dn;
  private String rfc1779Dn;
  private String rfc2253Dn;
  private String canonicalDn;
  private RDN[] names;
  private X500Principal x500Principal;
  private byte[] encoded;
  private volatile List<RDN> rdnList;
  private volatile List<AVA> allAvaList;
  /** 
 * Constructs a name from a conventionally formatted string, such
 * as "CN=Dave, OU=JavaSoft, O=Sun Microsystems, C=US".
 * (RFC 1779 or RFC 2253 style).
 * @param DN X.500 Distinguished Name
 */
  public X500Name(  String dname) throws IOException {
    this(dname,Collections.<String,String>emptyMap());
  }
  /** 
 * Constructs a name from a conventionally formatted string, such
 * as "CN=Dave, OU=JavaSoft, O=Sun Microsystems, C=US".
 * (RFC 1779 or RFC 2253 style).
 * @param DN X.500 Distinguished Name
 * @param keywordMap an additional keyword/OID map
 */
  public X500Name(  String dname,  Map<String,String> keywordMap) throws IOException {
    parseDN(dname,keywordMap);
  }
  /** 
 * Constructs a name from a string formatted according to format.
 * Currently, the formats DEFAULT and RFC2253 are supported.
 * DEFAULT is the default format used by the X500Name(String)
 * constructor. RFC2253 is format strictly according to RFC2253
 * without extensions.
 * @param DN X.500 Distinguished Name
 */
  public X500Name(  String dname,  String format) throws IOException {
    if (dname == null) {
      throw new NullPointerException("Name must not be null");
    }
    if (format.equalsIgnoreCase("RFC2253")) {
      parseRFC2253DN(dname);
    }
 else     if (format.equalsIgnoreCase("DEFAULT")) {
      parseDN(dname,Collections.<String,String>emptyMap());
    }
 else {
      throw new IOException("Unsupported format " + format);
    }
  }
  /** 
 * Constructs a name from fields common in enterprise application
 * environments.
 * <P><EM><STRONG>NOTE:</STRONG>  The behaviour when any of
 * these strings contain characters outside the ASCII range
 * is unspecified in currently relevant standards.</EM>
 * @param commonName common name of a person, e.g. "Vivette Davis"
 * @param organizationUnit small organization name, e.g. "Purchasing"
 * @param organizationName large organization name, e.g. "Onizuka, Inc."
 * @param country two letter country code, e.g. "CH"
 */
  public X500Name(  String commonName,  String organizationUnit,  String organizationName,  String country) throws IOException {
    names=new RDN[4];
    names[3]=new RDN(1);
    names[3].assertion[0]=new AVA(commonName_oid,new DerValue(commonName));
    names[2]=new RDN(1);
    names[2].assertion[0]=new AVA(orgUnitName_oid,new DerValue(organizationUnit));
    names[1]=new RDN(1);
    names[1].assertion[0]=new AVA(orgName_oid,new DerValue(organizationName));
    names[0]=new RDN(1);
    names[0].assertion[0]=new AVA(countryName_oid,new DerValue(country));
  }
  /** 
 * Constructs a name from fields common in Internet application
 * environments.
 * <P><EM><STRONG>NOTE:</STRONG>  The behaviour when any of
 * these strings contain characters outside the ASCII range
 * is unspecified in currently relevant standards.</EM>
 * @param commonName common name of a person, e.g. "Vivette Davis"
 * @param organizationUnit small organization name, e.g. "Purchasing"
 * @param organizationName large organization name, e.g. "Onizuka, Inc."
 * @param localityName locality (city) name, e.g. "Palo Alto"
 * @param stateName state name, e.g. "California"
 * @param country two letter country code, e.g. "CH"
 */
  public X500Name(  String commonName,  String organizationUnit,  String organizationName,  String localityName,  String stateName,  String country) throws IOException {
    names=new RDN[6];
    names[5]=new RDN(1);
    names[5].assertion[0]=new AVA(commonName_oid,new DerValue(commonName));
    names[4]=new RDN(1);
    names[4].assertion[0]=new AVA(orgUnitName_oid,new DerValue(organizationUnit));
    names[3]=new RDN(1);
    names[3].assertion[0]=new AVA(orgName_oid,new DerValue(organizationName));
    names[2]=new RDN(1);
    names[2].assertion[0]=new AVA(localityName_oid,new DerValue(localityName));
    names[1]=new RDN(1);
    names[1].assertion[0]=new AVA(stateName_oid,new DerValue(stateName));
    names[0]=new RDN(1);
    names[0].assertion[0]=new AVA(countryName_oid,new DerValue(country));
  }
  /** 
 * Constructs a name from an array of relative distinguished names
 * @param rdnArray array of relative distinguished names
 * @throws IOException on error
 */
  public X500Name(  RDN[] rdnArray) throws IOException {
    if (rdnArray == null) {
      names=new RDN[0];
    }
 else {
      names=rdnArray.clone();
      for (int i=0; i < names.length; i++) {
        if (names[i] == null) {
          throw new IOException("Cannot create an X500Name");
        }
      }
    }
  }
  /** 
 * Constructs a name from an ASN.1 encoded value.  The encoding
 * of the name in the stream uses DER (a BER/1 subset).
 * @param value a DER-encoded value holding an X.500 name.
 */
  public X500Name(  DerValue value) throws IOException {
    this(value.toDerInputStream());
  }
  /** 
 * Constructs a name from an ASN.1 encoded input stream.  The encoding
 * of the name in the stream uses DER (a BER/1 subset).
 * @param in DER-encoded data holding an X.500 name.
 */
  public X500Name(  DerInputStream in) throws IOException {
    parseDER(in);
  }
  /** 
 * Constructs a name from an ASN.1 encoded byte array.
 * @param name DER-encoded byte array holding an X.500 name.
 */
  public X500Name(  byte[] name) throws IOException {
    DerInputStream in=new DerInputStream(name);
    parseDER(in);
  }
  /** 
 * Return an immutable List of all RDNs in this X500Name.
 */
  public List<RDN> rdns(){
    List<RDN> list=rdnList;
    if (list == null) {
      list=Collections.unmodifiableList(Arrays.asList(names));
      rdnList=list;
    }
    return list;
  }
  /** 
 * Return the number of RDNs in this X500Name.
 */
  public int size(){
    return names.length;
  }
  /** 
 * Return an immutable List of the the AVAs contained in all the
 * RDNs of this X500Name.
 */
  public List<AVA> allAvas(){
    List<AVA> list=allAvaList;
    if (list == null) {
      list=new ArrayList<AVA>();
      for (int i=0; i < names.length; i++) {
        list.addAll(names[i].avas());
      }
    }
    return list;
  }
  /** 
 * Return the total number of AVAs contained in all the RDNs of
 * this X500Name.
 */
  public int avaSize(){
    return allAvas().size();
  }
  /** 
 * Return whether this X500Name is empty. An X500Name is not empty
 * if it has at least one RDN containing at least one AVA.
 */
  public boolean isEmpty(){
    int n=names.length;
    if (n == 0) {
      return true;
    }
    for (int i=0; i < n; i++) {
      if (names[i].assertion.length != 0) {
        return false;
      }
    }
    return true;
  }
  /** 
 * Calculates a hash code value for the object.  Objects
 * which are equal will also have the same hashcode.
 */
  public int hashCode(){
    return getRFC2253CanonicalName().hashCode();
  }
  /** 
 * Compares this name with another, for equality.
 * @return true iff the names are identical.
 */
  public boolean equals(  Object obj){
    if (this == obj) {
      return true;
    }
    if (obj instanceof X500Name == false) {
      return false;
    }
    X500Name other=(X500Name)obj;
    if ((this.canonicalDn != null) && (other.canonicalDn != null)) {
      return this.canonicalDn.equals(other.canonicalDn);
    }
    int n=this.names.length;
    if (n != other.names.length) {
      return false;
    }
    for (int i=0; i < n; i++) {
      RDN r1=this.names[i];
      RDN r2=other.names[i];
      if (r1.assertion.length != r2.assertion.length) {
        return false;
      }
    }
    String thisCanonical=this.getRFC2253CanonicalName();
    String otherCanonical=other.getRFC2253CanonicalName();
    return thisCanonical.equals(otherCanonical);
  }
  private String getString(  DerValue attribute) throws IOException {
    if (attribute == null)     return null;
    String value=attribute.getAsString();
    if (value == null)     throw new IOException("not a DER string encoding, " + attribute.tag);
 else     return value;
  }
  /** 
 * Return type of GeneralName.
 */
  public int getType(){
    return (GeneralNameInterface.NAME_DIRECTORY);
  }
  /** 
 * Returns a "Country" name component.  If more than one
 * such attribute exists, the topmost one is returned.
 * @return "C=" component of the name, if any.
 */
  public String getCountry() throws IOException {
    DerValue attr=findAttribute(countryName_oid);
    return getString(attr);
  }
  /** 
 * Returns an "Organization" name component.  If more than
 * one such attribute exists, the topmost one is returned.
 * @return "O=" component of the name, if any.
 */
  public String getOrganization() throws IOException {
    DerValue attr=findAttribute(orgName_oid);
    return getString(attr);
  }
  /** 
 * Returns an "Organizational Unit" name component.  If more
 * than one such attribute exists, the topmost one is returned.
 * @return "OU=" component of the name, if any.
 */
  public String getOrganizationalUnit() throws IOException {
    DerValue attr=findAttribute(orgUnitName_oid);
    return getString(attr);
  }
  /** 
 * Returns a "Common Name" component.  If more than one such
 * attribute exists, the topmost one is returned.
 * @return "CN=" component of the name, if any.
 */
  public String getCommonName() throws IOException {
    DerValue attr=findAttribute(commonName_oid);
    return getString(attr);
  }
  /** 
 * Returns a "Locality" name component.  If more than one
 * such component exists, the topmost one is returned.
 * @return "L=" component of the name, if any.
 */
  public String getLocality() throws IOException {
    DerValue attr=findAttribute(localityName_oid);
    return getString(attr);
  }
  /** 
 * Returns a "State" name component.  If more than one
 * such component exists, the topmost one is returned.
 * @return "S=" component of the name, if any.
 */
  public String getState() throws IOException {
    DerValue attr=findAttribute(stateName_oid);
    return getString(attr);
  }
  /** 
 * Returns a "Domain" name component.  If more than one
 * such component exists, the topmost one is returned.
 * @return "DC=" component of the name, if any.
 */
  public String getDomain() throws IOException {
    DerValue attr=findAttribute(DOMAIN_COMPONENT_OID);
    return getString(attr);
  }
  /** 
 * Returns a "DN Qualifier" name component.  If more than one
 * such component exists, the topmost one is returned.
 * @return "DNQ=" component of the name, if any.
 */
  public String getDNQualifier() throws IOException {
    DerValue attr=findAttribute(DNQUALIFIER_OID);
    return getString(attr);
  }
  /** 
 * Returns a "Surname" name component.  If more than one
 * such component exists, the topmost one is returned.
 * @return "SURNAME=" component of the name, if any.
 */
  public String getSurname() throws IOException {
    DerValue attr=findAttribute(SURNAME_OID);
    return getString(attr);
  }
  /** 
 * Returns a "Given Name" name component.  If more than one
 * such component exists, the topmost one is returned.
 * @return "GIVENNAME=" component of the name, if any.
 */
  public String getGivenName() throws IOException {
    DerValue attr=findAttribute(GIVENNAME_OID);
    return getString(attr);
  }
  /** 
 * Returns an "Initials" name component.  If more than one
 * such component exists, the topmost one is returned.
 * @return "INITIALS=" component of the name, if any.
 */
  public String getInitials() throws IOException {
    DerValue attr=findAttribute(INITIALS_OID);
    return getString(attr);
  }
  /** 
 * Returns a "Generation Qualifier" name component.  If more than one
 * such component exists, the topmost one is returned.
 * @return "GENERATION=" component of the name, if any.
 */
  public String getGeneration() throws IOException {
    DerValue attr=findAttribute(GENERATIONQUALIFIER_OID);
    return getString(attr);
  }
  /** 
 * Returns an "IP address" name component.  If more than one
 * such component exists, the topmost one is returned.
 * @return "IP=" component of the name, if any.
 */
  public String getIP() throws IOException {
    DerValue attr=findAttribute(ipAddress_oid);
    return getString(attr);
  }
  /** 
 * Returns a string form of the X.500 distinguished name.
 * The format of the string is from RFC 1779. The returned string
 * may contain non-standardised keywords for more readability
 * (keywords from RFCs 1779, 2253, and 3280).
 */
  public String toString(){
    if (dn == null) {
      generateDN();
    }
    return dn;
  }
  /** 
 * Returns a string form of the X.500 distinguished name
 * using the algorithm defined in RFC 1779. Only standard attribute type
 * keywords defined in RFC 1779 are emitted.
 */
  public String getRFC1779Name(){
    return getRFC1779Name(Collections.<String,String>emptyMap());
  }
  /** 
 * Returns a string form of the X.500 distinguished name
 * using the algorithm defined in RFC 1779. Attribute type
 * keywords defined in RFC 1779 are emitted, as well as additional
 * keywords contained in the OID/keyword map.
 */
  public String getRFC1779Name(  Map<String,String> oidMap) throws IllegalArgumentException {
    if (oidMap.isEmpty()) {
      if (rfc1779Dn != null) {
        return rfc1779Dn;
      }
 else {
        rfc1779Dn=generateRFC1779DN(oidMap);
        return rfc1779Dn;
      }
    }
    return generateRFC1779DN(oidMap);
  }
  /** 
 * Returns a string form of the X.500 distinguished name
 * using the algorithm defined in RFC 2253. Only standard attribute type
 * keywords defined in RFC 2253 are emitted.
 */
  public String getRFC2253Name(){
    return getRFC2253Name(Collections.<String,String>emptyMap());
  }
  /** 
 * Returns a string form of the X.500 distinguished name
 * using the algorithm defined in RFC 2253. Attribute type
 * keywords defined in RFC 2253 are emitted, as well as additional
 * keywords contained in the OID/keyword map.
 */
  public String getRFC2253Name(  Map<String,String> oidMap){
    if (oidMap.isEmpty()) {
      if (rfc2253Dn != null) {
        return rfc2253Dn;
      }
 else {
        rfc2253Dn=generateRFC2253DN(oidMap);
        return rfc2253Dn;
      }
    }
    return generateRFC2253DN(oidMap);
  }
  private String generateRFC2253DN(  Map<String,String> oidMap){
    if (names.length == 0) {
      return "";
    }
    StringBuilder fullname=new StringBuilder(48);
    for (int i=names.length - 1; i >= 0; i--) {
      if (i < names.length - 1) {
        fullname.append(',');
      }
      fullname.append(names[i].toRFC2253String(oidMap));
    }
    return fullname.toString();
  }
  public String getRFC2253CanonicalName(){
    if (canonicalDn != null) {
      return canonicalDn;
    }
    if (names.length == 0) {
      canonicalDn="";
      return canonicalDn;
    }
    StringBuilder fullname=new StringBuilder(48);
    for (int i=names.length - 1; i >= 0; i--) {
      if (i < names.length - 1) {
        fullname.append(',');
      }
      fullname.append(names[i].toRFC2253String(true));
    }
    canonicalDn=fullname.toString();
    return canonicalDn;
  }
  /** 
 * Returns the value of toString().  This call is needed to
 * implement the java.security.Principal interface.
 */
  public String getName(){
    return toString();
  }
  /** 
 * Find the first instance of this attribute in a "top down"
 * search of all the attributes in the name.
 */
  private DerValue findAttribute(  ObjectIdentifier attribute){
    if (names != null) {
      for (int i=0; i < names.length; i++) {
        DerValue value=names[i].findAttribute(attribute);
        if (value != null) {
          return value;
        }
      }
    }
    return null;
  }
  /** 
 * Find the most specific ("last") attribute of the given
 * type.
 */
  public DerValue findMostSpecificAttribute(  ObjectIdentifier attribute){
    if (names != null) {
      for (int i=names.length - 1; i >= 0; i--) {
        DerValue value=names[i].findAttribute(attribute);
        if (value != null) {
          return value;
        }
      }
    }
    return null;
  }
  /** 
 */
  private void parseDER(  DerInputStream in) throws IOException {
    DerValue[] nameseq=null;
    byte[] derBytes=in.toByteArray();
    try {
      nameseq=in.getSequence(5);
    }
 catch (    IOException ioe) {
      if (derBytes == null) {
        nameseq=null;
      }
 else {
        DerValue derVal=new DerValue(DerValue.tag_Sequence,derBytes);
        derBytes=derVal.toByteArray();
        nameseq=new DerInputStream(derBytes).getSequence(5);
      }
    }
    if (nameseq == null) {
      names=new RDN[0];
    }
 else {
      names=new RDN[nameseq.length];
      for (int i=0; i < nameseq.length; i++) {
        names[i]=new RDN(nameseq[i]);
      }
    }
  }
  /** 
 * Encodes the name in DER-encoded form.
 * @deprecated Use encode() instead
 * @param out where to put the DER-encoded X.500 name
 */
  @Deprecated public void emit(  DerOutputStream out) throws IOException {
    encode(out);
  }
  /** 
 * Encodes the name in DER-encoded form.
 * @param out where to put the DER-encoded X.500 name
 */
  public void encode(  DerOutputStream out) throws IOException {
    DerOutputStream tmp=new DerOutputStream();
    for (int i=0; i < names.length; i++) {
      names[i].encode(tmp);
    }
    out.write(DerValue.tag_Sequence,tmp);
  }
  /** 
 * Returned the encoding as an uncloned byte array. Callers must
 * guarantee that they neither modify it not expose it to untrusted
 * code.
 */
  public byte[] getEncodedInternal() throws IOException {
    if (encoded == null) {
      DerOutputStream out=new DerOutputStream();
      DerOutputStream tmp=new DerOutputStream();
      for (int i=0; i < names.length; i++) {
        names[i].encode(tmp);
      }
      out.write(DerValue.tag_Sequence,tmp);
      encoded=out.toByteArray();
    }
    return encoded;
  }
  /** 
 * Gets the name in DER-encoded form.
 * @return the DER encoded byte array of this name.
 */
  public byte[] getEncoded() throws IOException {
    return getEncodedInternal().clone();
  }
  private void parseDN(  String input,  Map<String,String> keywordMap) throws IOException {
    if (input == null || input.length() == 0) {
      names=new RDN[0];
      return;
    }
    List<RDN> dnVector=new ArrayList<RDN>();
    int dnOffset=0;
    int rdnEnd;
    String rdnString;
    int quoteCount=0;
    String dnString=input;
    int searchOffset=0;
    int nextComma=dnString.indexOf(',');
    int nextSemiColon=dnString.indexOf(';');
    while (nextComma >= 0 || nextSemiColon >= 0) {
      if (nextSemiColon < 0) {
        rdnEnd=nextComma;
      }
 else       if (nextComma < 0) {
        rdnEnd=nextSemiColon;
      }
 else {
        rdnEnd=Math.min(nextComma,nextSemiColon);
      }
      quoteCount+=countQuotes(dnString,searchOffset,rdnEnd);
      if (rdnEnd >= 0 && quoteCount != 1 && !escaped(rdnEnd,searchOffset,dnString)) {
        rdnString=dnString.substring(dnOffset,rdnEnd);
        RDN rdn=new RDN(rdnString,keywordMap);
        dnVector.add(rdn);
        dnOffset=rdnEnd + 1;
        quoteCount=0;
      }
      searchOffset=rdnEnd + 1;
      nextComma=dnString.indexOf(',',searchOffset);
      nextSemiColon=dnString.indexOf(';',searchOffset);
    }
    rdnString=dnString.substring(dnOffset);
    RDN rdn=new RDN(rdnString,keywordMap);
    dnVector.add(rdn);
    Collections.reverse(dnVector);
    names=dnVector.toArray(new RDN[dnVector.size()]);
  }
  private void parseRFC2253DN(  String dnString) throws IOException {
    if (dnString.length() == 0) {
      names=new RDN[0];
      return;
    }
    List<RDN> dnVector=new ArrayList<RDN>();
    int dnOffset=0;
    String rdnString;
    int searchOffset=0;
    int rdnEnd=dnString.indexOf(',');
    while (rdnEnd >= 0) {
      if (rdnEnd > 0 && !escaped(rdnEnd,searchOffset,dnString)) {
        rdnString=dnString.substring(dnOffset,rdnEnd);
        RDN rdn=new RDN(rdnString,"RFC2253");
        dnVector.add(rdn);
        dnOffset=rdnEnd + 1;
      }
      searchOffset=rdnEnd + 1;
      rdnEnd=dnString.indexOf(',',searchOffset);
    }
    rdnString=dnString.substring(dnOffset);
    RDN rdn=new RDN(rdnString,"RFC2253");
    dnVector.add(rdn);
    Collections.reverse(dnVector);
    names=dnVector.toArray(new RDN[dnVector.size()]);
  }
  static int countQuotes(  String string,  int from,  int to){
    int count=0;
    for (int i=from; i < to; i++) {
      if ((string.charAt(i) == '"' && i == from) || (string.charAt(i) == '"' && string.charAt(i - 1) != '\\')) {
        count++;
      }
    }
    return count;
  }
  private static boolean escaped(  int rdnEnd,  int searchOffset,  String dnString){
    if (rdnEnd == 1 && dnString.charAt(rdnEnd - 1) == '\\') {
      return true;
    }
 else     if (rdnEnd > 1 && dnString.charAt(rdnEnd - 1) == '\\' && dnString.charAt(rdnEnd - 2) != '\\') {
      return true;
    }
 else     if (rdnEnd > 1 && dnString.charAt(rdnEnd - 1) == '\\' && dnString.charAt(rdnEnd - 2) == '\\') {
      int count=0;
      rdnEnd--;
      while (rdnEnd >= searchOffset) {
        if (dnString.charAt(rdnEnd) == '\\') {
          count++;
        }
        rdnEnd--;
      }
      return (count % 2) != 0 ? true : false;
    }
 else {
      return false;
    }
  }
  private void generateDN(){
    if (names.length == 1) {
      dn=names[0].toString();
      return;
    }
    StringBuilder sb=new StringBuilder(48);
    if (names != null) {
      for (int i=names.length - 1; i >= 0; i--) {
        if (i != names.length - 1) {
          sb.append(", ");
        }
        sb.append(names[i].toString());
      }
    }
    dn=sb.toString();
  }
  private String generateRFC1779DN(  Map<String,String> oidMap){
    if (names.length == 1) {
      return names[0].toRFC1779String(oidMap);
    }
    StringBuilder sb=new StringBuilder(48);
    if (names != null) {
      for (int i=names.length - 1; i >= 0; i--) {
        if (i != names.length - 1) {
          sb.append(", ");
        }
        sb.append(names[i].toRFC1779String(oidMap));
      }
    }
    return sb.toString();
  }
  /** 
 */
  static ObjectIdentifier intern(  ObjectIdentifier oid){
    ObjectIdentifier interned=internedOIDs.get(oid);
    if (interned != null) {
      return interned;
    }
    internedOIDs.put(oid,oid);
    return oid;
  }
  private static final Map<ObjectIdentifier,ObjectIdentifier> internedOIDs=new HashMap<ObjectIdentifier,ObjectIdentifier>();
  private static final int commonName_data[]={2,5,4,3};
  private static final int SURNAME_DATA[]={2,5,4,4};
  private static final int SERIALNUMBER_DATA[]={2,5,4,5};
  private static final int countryName_data[]={2,5,4,6};
  private static final int localityName_data[]={2,5,4,7};
  private static final int stateName_data[]={2,5,4,8};
  private static final int streetAddress_data[]={2,5,4,9};
  private static final int orgName_data[]={2,5,4,10};
  private static final int orgUnitName_data[]={2,5,4,11};
  private static final int title_data[]={2,5,4,12};
  private static final int GIVENNAME_DATA[]={2,5,4,42};
  private static final int INITIALS_DATA[]={2,5,4,43};
  private static final int GENERATIONQUALIFIER_DATA[]={2,5,4,44};
  private static final int DNQUALIFIER_DATA[]={2,5,4,46};
  private static final int ipAddress_data[]={1,3,6,1,4,1,42,2,11,2,1};
  private static final int DOMAIN_COMPONENT_DATA[]={0,9,2342,19200300,100,1,25};
  private static final int userid_data[]={0,9,2342,19200300,100,1,1};
  public static final ObjectIdentifier commonName_oid;
  public static final ObjectIdentifier countryName_oid;
  public static final ObjectIdentifier localityName_oid;
  public static final ObjectIdentifier orgName_oid;
  public static final ObjectIdentifier orgUnitName_oid;
  public static final ObjectIdentifier stateName_oid;
  public static final ObjectIdentifier streetAddress_oid;
  public static final ObjectIdentifier title_oid;
  public static final ObjectIdentifier DNQUALIFIER_OID;
  public static final ObjectIdentifier SURNAME_OID;
  public static final ObjectIdentifier GIVENNAME_OID;
  public static final ObjectIdentifier INITIALS_OID;
  public static final ObjectIdentifier GENERATIONQUALIFIER_OID;
  public static final ObjectIdentifier ipAddress_oid;
  public static final ObjectIdentifier DOMAIN_COMPONENT_OID;
  public static final ObjectIdentifier userid_oid;
  public static final ObjectIdentifier SERIALNUMBER_OID;
static {
    commonName_oid=intern(ObjectIdentifier.newInternal(commonName_data));
    SERIALNUMBER_OID=intern(ObjectIdentifier.newInternal(SERIALNUMBER_DATA));
    countryName_oid=intern(ObjectIdentifier.newInternal(countryName_data));
    localityName_oid=intern(ObjectIdentifier.newInternal(localityName_data));
    orgName_oid=intern(ObjectIdentifier.newInternal(orgName_data));
    orgUnitName_oid=intern(ObjectIdentifier.newInternal(orgUnitName_data));
    stateName_oid=intern(ObjectIdentifier.newInternal(stateName_data));
    streetAddress_oid=intern(ObjectIdentifier.newInternal(streetAddress_data));
    title_oid=intern(ObjectIdentifier.newInternal(title_data));
    DNQUALIFIER_OID=intern(ObjectIdentifier.newInternal(DNQUALIFIER_DATA));
    SURNAME_OID=intern(ObjectIdentifier.newInternal(SURNAME_DATA));
    GIVENNAME_OID=intern(ObjectIdentifier.newInternal(GIVENNAME_DATA));
    INITIALS_OID=intern(ObjectIdentifier.newInternal(INITIALS_DATA));
    GENERATIONQUALIFIER_OID=intern(ObjectIdentifier.newInternal(GENERATIONQUALIFIER_DATA));
    ipAddress_oid=intern(ObjectIdentifier.newInternal(ipAddress_data));
    DOMAIN_COMPONENT_OID=intern(ObjectIdentifier.newInternal(DOMAIN_COMPONENT_DATA));
    userid_oid=intern(ObjectIdentifier.newInternal(userid_data));
  }
  /** 
 * Return constraint type:<ul>
 * <li>NAME_DIFF_TYPE = -1: input name is different type from this name
 * (i.e. does not constrain)
 * <li>NAME_MATCH = 0: input name matches this name
 * <li>NAME_NARROWS = 1: input name narrows this name
 * <li>NAME_WIDENS = 2: input name widens this name
 * <li>NAME_SAME_TYPE = 3: input name does not match or narrow this name,
 * &       but is same type
 * </ul>.  These results are used in checking NameConstraints during
 * certification path verification.
 * @param inputName to be checked for being constrained
 * @returns constraint type above
 * @throws UnsupportedOperationException if name is not exact match, but
 * narrowing and widening are not supported for this name type.
 */
  public int constrains(  GeneralNameInterface inputName) throws UnsupportedOperationException {
    int constraintType;
    if (inputName == null) {
      constraintType=NAME_DIFF_TYPE;
    }
 else     if (inputName.getType() != NAME_DIRECTORY) {
      constraintType=NAME_DIFF_TYPE;
    }
 else {
      X500Name inputX500=(X500Name)inputName;
      if (inputX500.equals(this)) {
        constraintType=NAME_MATCH;
      }
 else       if (inputX500.names.length == 0) {
        constraintType=NAME_WIDENS;
      }
 else       if (this.names.length == 0) {
        constraintType=NAME_NARROWS;
      }
 else       if (inputX500.isWithinSubtree(this)) {
        constraintType=NAME_NARROWS;
      }
 else       if (isWithinSubtree(inputX500)) {
        constraintType=NAME_WIDENS;
      }
 else {
        constraintType=NAME_SAME_TYPE;
      }
    }
    return constraintType;
  }
  /** 
 * Compares this name with another and determines if
 * it is within the subtree of the other. Useful for
 * checking against the name constraints extension.
 * @return true iff this name is within the subtree of other.
 */
  private boolean isWithinSubtree(  X500Name other){
    if (this == other) {
      return true;
    }
    if (other == null) {
      return false;
    }
    if (other.names.length == 0) {
      return true;
    }
    if (this.names.length == 0) {
      return false;
    }
    if (names.length < other.names.length) {
      return false;
    }
    for (int i=0; i < other.names.length; i++) {
      if (!names[i].equals(other.names[i])) {
        return false;
      }
    }
    return true;
  }
  /** 
 * Return subtree depth of this name for purposes of determining
 * NameConstraints minimum and maximum bounds and for calculating
 * path lengths in name subtrees.
 * @returns distance of name from root
 * @throws UnsupportedOperationException if not supported for this name type
 */
  public int subtreeDepth() throws UnsupportedOperationException {
    return names.length;
  }
  /** 
 * Return lowest common ancestor of this name and other name
 * @param other another X500Name
 * @return X500Name of lowest common ancestor; null if none
 */
  public X500Name commonAncestor(  X500Name other){
    if (other == null) {
      return null;
    }
    int otherLen=other.names.length;
    int thisLen=this.names.length;
    if (thisLen == 0 || otherLen == 0) {
      return null;
    }
    int minLen=(thisLen < otherLen) ? thisLen : otherLen;
    int i=0;
    for (; i < minLen; i++) {
      if (!names[i].equals(other.names[i])) {
        if (i == 0) {
          return null;
        }
 else {
          break;
        }
      }
    }
    RDN[] ancestor=new RDN[i];
    for (int j=0; j < i; j++) {
      ancestor[j]=names[j];
    }
    X500Name commonAncestor=null;
    try {
      commonAncestor=new X500Name(ancestor);
    }
 catch (    IOException ioe) {
      return null;
    }
    return commonAncestor;
  }
  /** 
 * Constructor object for use by asX500Principal().
 */
  private static final Constructor principalConstructor;
  /** 
 * Field object for use by asX500Name().
 */
  private static final Field principalField;
  /** 
 * Retrieve the Constructor and Field we need for reflective access
 * and make them accessible.
 */
static {
    PrivilegedExceptionAction<Object[]> pa=new PrivilegedExceptionAction<Object[]>(){
      public Object[] run() throws Exception {
        Class pClass=X500Principal.class;
        Class[] args=new Class[]{X500Name.class};
        Constructor cons=((Class<?>)pClass).getDeclaredConstructor(args);
        cons.setAccessible(true);
        Field field=pClass.getDeclaredField("thisX500Name");
        field.setAccessible(true);
        return new Object[]{cons,field};
      }
    }
;
    try {
      Object[] result=AccessController.doPrivileged(pa);
      principalConstructor=(Constructor)result[0];
      principalField=(Field)result[1];
    }
 catch (    Exception e) {
      throw (InternalError)new InternalError("Could not obtain " + "X500Principal access").initCause(e);
    }
  }
  /** 
 * Get an X500Principal backed by this X500Name.
 * Note that we are using privileged reflection to access the hidden
 * package private constructor in X500Principal.
 */
  public X500Principal asX500Principal(){
    if (x500Principal == null) {
      try {
        Object[] args=new Object[]{this};
        x500Principal=(X500Principal)principalConstructor.newInstance(args);
      }
 catch (      Exception e) {
        throw new RuntimeException("Unexpected exception",e);
      }
    }
    return x500Principal;
  }
  /** 
 * Get the X500Name contained in the given X500Principal.
 * Note that the X500Name is retrieved using reflection.
 */
  public static X500Name asX500Name(  X500Principal p){
    try {
      X500Name name=(X500Name)principalField.get(p);
      name.x500Principal=p;
      return name;
    }
 catch (    Exception e) {
      throw new RuntimeException("Unexpected exception",e);
    }
  }
}
