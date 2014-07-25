package java.net;
import java.util.List;
import java.util.StringTokenizer;
import java.util.NoSuchElementException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.Date;
import java.lang.NullPointerException;
import java.util.Locale;
import java.util.Objects;
/** 
 * An HttpCookie object represents an http cookie, which carries state
 * information between server and user agent. Cookie is widely adopted
 * to create stateful sessions.
 * <p>There are 3 http cookie specifications:
 * <blockquote>
 * Netscape draft<br>
 * RFC 2109 - <a href="http://www.ietf.org/rfc/rfc2109.txt">
 * <i>http://www.ietf.org/rfc/rfc2109.txt</i></a><br>
 * RFC 2965 - <a href="http://www.ietf.org/rfc/rfc2965.txt">
 * <i>http://www.ietf.org/rfc/rfc2965.txt</i></a>
 * </blockquote>
 * <p>HttpCookie class can accept all these 3 forms of syntax.
 * @author Edward Wang
 * @since 1.6
 */
public final class HttpCookie implements Cloneable {
  private String name;
  private String value;
  private String comment;
  private String commentURL;
  private boolean toDiscard;
  private String domain;
  private long maxAge=MAX_AGE_UNSPECIFIED;
  private String path;
  private String portlist;
  private boolean secure;
  private boolean httpOnly;
  private int version=1;
  private long whenCreated=0;
  private final static long MAX_AGE_UNSPECIFIED=-1;
  private final static String[] COOKIE_DATE_FORMATS={"EEE',' dd-MMM-yyyy HH:mm:ss 'GMT'","EEE',' dd MMM yyyy HH:mm:ss 'GMT'","EEE MMM dd yyyy HH:mm:ss 'GMT'Z"};
  private final static String SET_COOKIE="set-cookie:";
  private final static String SET_COOKIE2="set-cookie2:";
  /** 
 * Constructs a cookie with a specified name and value.
 * <p>The name must conform to RFC 2965. That means it can contain
 * only ASCII alphanumeric characters and cannot contain commas,
 * semicolons, or white space or begin with a $ character. The cookie's
 * name cannot be changed after creation.
 * <p>The value can be anything the server chooses to send. Its
 * value is probably of interest only to the server. The cookie's
 * value can be changed after creation with the
 * <code>setValue</code> method.
 * <p>By default, cookies are created according to the RFC 2965
 * cookie specification. The version can be changed with the
 * <code>setVersion</code> method.
 * @param name                      a <code>String</code> specifying the name of the cookie
 * @param value                     a <code>String</code> specifying the value of the cookie
 * @throws IllegalArgumentException if the cookie name contains illegal characters
 * or it is one of the tokens reserved for use
 * by the cookie protocol
 * @throws NullPointerException     if <tt>name</tt> is <tt>null</tt>
 * @see #setValue
 * @see #setVersion
 */
  public HttpCookie(  String name,  String value){
    name=name.trim();
    if (name.length() == 0 || !isToken(name) || isReserved(name)) {
      throw new IllegalArgumentException("Illegal cookie name");
    }
    this.name=name;
    this.value=value;
    toDiscard=false;
    secure=false;
    whenCreated=System.currentTimeMillis();
    portlist=null;
  }
  /** 
 * Constructs cookies from set-cookie or set-cookie2 header string.
 * RFC 2965 section 3.2.2 set-cookie2 syntax indicates that one header line
 * may contain more than one cookie definitions, so this is a static
 * utility method instead of another constructor.
 * @param header    a <tt>String</tt> specifying the set-cookie header.
 * The header should start with "set-cookie", or "set-cookie2"
 * token; or it should have no leading token at all.
 * @return          a List of cookie parsed from header line string
 * @throws IllegalArgumentException if header string violates the cookie
 * specification's syntax, or the cookie
 * name contains llegal characters, or
 * the cookie name is one of the tokens
 * reserved for use by the cookie protocol
 * @throws NullPointerException     if the header string is <tt>null</tt>
 */
  public static List<HttpCookie> parse(  String header){
    int version=guessCookieVersion(header);
    if (startsWithIgnoreCase(header,SET_COOKIE2)) {
      header=header.substring(SET_COOKIE2.length());
    }
 else     if (startsWithIgnoreCase(header,SET_COOKIE)) {
      header=header.substring(SET_COOKIE.length());
    }
    List<HttpCookie> cookies=new java.util.ArrayList<HttpCookie>();
    if (version == 0) {
      HttpCookie cookie=parseInternal(header);
      cookie.setVersion(0);
      cookies.add(cookie);
    }
 else {
      List<String> cookieStrings=splitMultiCookies(header);
      for (      String cookieStr : cookieStrings) {
        HttpCookie cookie=parseInternal(cookieStr);
        cookie.setVersion(1);
        cookies.add(cookie);
      }
    }
    return cookies;
  }
  /** 
 * Reports whether this http cookie has expired or not.
 * @return  <tt>true</tt> to indicate this http cookie has expired;
 * otherwise, <tt>false</tt>
 */
  public boolean hasExpired(){
    if (maxAge == 0)     return true;
    if (maxAge == MAX_AGE_UNSPECIFIED)     return false;
    long deltaSecond=(System.currentTimeMillis() - whenCreated) / 1000;
    if (deltaSecond > maxAge)     return true;
 else     return false;
  }
  /** 
 * Specifies a comment that describes a cookie's purpose.
 * The comment is useful if the browser presents the cookie
 * to the user. Comments
 * are not supported by Netscape Version 0 cookies.
 * @param purpose           a <code>String</code> specifying the comment
 * to display to the user
 * @see #getComment
 */
  public void setComment(  String purpose){
    comment=purpose;
  }
  /** 
 * Returns the comment describing the purpose of this cookie, or
 * <code>null</code> if the cookie has no comment.
 * @return                  a <code>String</code> containing the comment,
 * or <code>null</code> if none
 * @see #setComment
 */
  public String getComment(){
    return comment;
  }
  /** 
 * Specifies a comment url that describes a cookie's purpose.
 * The comment url is useful if the browser presents the cookie
 * to the user. Comment url is RFC 2965 only.
 * @param purpose           a <code>String</code> specifying the comment url
 * to display to the user
 * @see #getCommentURL
 */
  public void setCommentURL(  String purpose){
    commentURL=purpose;
  }
  /** 
 * Returns the comment url describing the purpose of this cookie, or
 * <code>null</code> if the cookie has no comment url.
 * @return                  a <code>String</code> containing the comment url,
 * or <code>null</code> if none
 * @see #setCommentURL
 */
  public String getCommentURL(){
    return commentURL;
  }
  /** 
 * Specify whether user agent should discard the cookie unconditionally.
 * This is RFC 2965 only attribute.
 * @param discard   <tt>true</tt> indicates to discard cookie unconditionally
 * @see #getDiscard
 */
  public void setDiscard(  boolean discard){
    toDiscard=discard;
  }
  /** 
 * Return the discard attribute of the cookie
 * @return  a <tt>boolean</tt> to represent this cookie's discard attribute
 * @see #setDiscard
 */
  public boolean getDiscard(){
    return toDiscard;
  }
  /** 
 * Specify the portlist of the cookie, which restricts the port(s)
 * to which a cookie may be sent back in a Cookie header.
 * @param ports     a <tt>String</tt> specify the port list, which is
 * comma seperated series of digits
 * @see #getPortlist
 */
  public void setPortlist(  String ports){
    portlist=ports;
  }
  /** 
 * Return the port list attribute of the cookie
 * @return  a <tt>String</tt> contains the port list
 * or <tt>null</tt> if none
 * @see #setPortlist
 */
  public String getPortlist(){
    return portlist;
  }
  /** 
 * Specifies the domain within which this cookie should be presented.
 * <p>The form of the domain name is specified by RFC 2965. A domain
 * name begins with a dot (<code>.foo.com</code>) and means that
 * the cookie is visible to servers in a specified Domain Name System
 * (DNS) zone (for example, <code>www.foo.com</code>, but not
 * <code>a.b.foo.com</code>). By default, cookies are only returned
 * to the server that sent them.
 * @param pattern           a <code>String</code> containing the domain name
 * within which this cookie is visible;
 * form is according to RFC 2965
 * @see #getDomain
 */
  public void setDomain(  String pattern){
    if (pattern != null)     domain=pattern.toLowerCase();
 else     domain=pattern;
  }
  /** 
 * Returns the domain name set for this cookie. The form of
 * the domain name is set by RFC 2965.
 * @return                  a <code>String</code> containing the domain name
 * @see #setDomain
 */
  public String getDomain(){
    return domain;
  }
  /** 
 * Sets the maximum age of the cookie in seconds.
 * <p>A positive value indicates that the cookie will expire
 * after that many seconds have passed. Note that the value is
 * the <i>maximum</i> age when the cookie will expire, not the cookie's
 * current age.
 * <p>A negative value means
 * that the cookie is not stored persistently and will be deleted
 * when the Web browser exits. A zero value causes the cookie
 * to be deleted.
 * @param expiry            an integer specifying the maximum age of the
 * cookie in seconds; if zero, the cookie
 * should be discarded immediately;
 * otherwise, the cookie's max age is unspecified.
 * @see #getMaxAge
 */
  public void setMaxAge(  long expiry){
    maxAge=expiry;
  }
  /** 
 * Returns the maximum age of the cookie, specified in seconds.
 * By default, <code>-1</code> indicating the cookie will persist
 * until browser shutdown.
 * @return                  an integer specifying the maximum age of the
 * cookie in seconds
 * @see #setMaxAge
 */
  public long getMaxAge(){
    return maxAge;
  }
  /** 
 * Specifies a path for the cookie
 * to which the client should return the cookie.
 * <p>The cookie is visible to all the pages in the directory
 * you specify, and all the pages in that directory's subdirectories.
 * A cookie's path must include the servlet that set the cookie,
 * for example, <i>/catalog</i>, which makes the cookie
 * visible to all directories on the server under <i>/catalog</i>.
 * <p>Consult RFC 2965 (available on the Internet) for more
 * information on setting path names for cookies.
 * @param uri               a <code>String</code> specifying a path
 * @see #getPath
 */
  public void setPath(  String uri){
    path=uri;
  }
  /** 
 * Returns the path on the server
 * to which the browser returns this cookie. The
 * cookie is visible to all subpaths on the server.
 * @return          a <code>String</code> specifying a path that contains
 * a servlet name, for example, <i>/catalog</i>
 * @see #setPath
 */
  public String getPath(){
    return path;
  }
  /** 
 * Indicates whether the cookie should only be sent using a secure protocol,
 * such as HTTPS or SSL.
 * <p>The default value is <code>false</code>.
 * @param flag      If <code>true</code>, the cookie can only be sent over
 * a secure protocol like https.
 * If <code>false</code>, it can be sent over any protocol.
 * @see #getSecure
 */
  public void setSecure(  boolean flag){
    secure=flag;
  }
  /** 
 * Returns <code>true</code> if sending this cookie should be
 * restricted to a secure protocol, or <code>false</code> if the
 * it can be sent using any protocol.
 * @return          <code>false</code> if the cookie can be sent over
 * any standard protocol; otherwise, <code>true</code>
 * @see #setSecure
 */
  public boolean getSecure(){
    return secure;
  }
  /** 
 * Returns the name of the cookie. The name cannot be changed after
 * creation.
 * @return          a <code>String</code> specifying the cookie's name
 */
  public String getName(){
    return name;
  }
  /** 
 * Assigns a new value to a cookie after the cookie is created.
 * If you use a binary value, you may want to use BASE64 encoding.
 * <p>With Version 0 cookies, values should not contain white
 * space, brackets, parentheses, equals signs, commas,
 * double quotes, slashes, question marks, at signs, colons,
 * and semicolons. Empty values may not behave the same way
 * on all browsers.
 * @param newValue          a <code>String</code> specifying the new value
 * @see #getValue
 */
  public void setValue(  String newValue){
    value=newValue;
  }
  /** 
 * Returns the value of the cookie.
 * @return                  a <code>String</code> containing the cookie's
 * present value
 * @see #setValue
 */
  public String getValue(){
    return value;
  }
  /** 
 * Returns the version of the protocol this cookie complies
 * with. Version 1 complies with RFC 2965/2109,
 * and version 0 complies with the original
 * cookie specification drafted by Netscape. Cookies provided
 * by a browser use and identify the browser's cookie version.
 * @return                  0 if the cookie complies with the
 * original Netscape specification; 1
 * if the cookie complies with RFC 2965/2109
 * @see #setVersion
 */
  public int getVersion(){
    return version;
  }
  /** 
 * Sets the version of the cookie protocol this cookie complies
 * with. Version 0 complies with the original Netscape cookie
 * specification. Version 1 complies with RFC 2965/2109.
 * @param v                 0 if the cookie should comply with
 * the original Netscape specification;
 * 1 if the cookie should comply with RFC 2965/2109
 * @throws IllegalArgumentException if <tt>v</tt> is neither 0 nor 1
 * @see #getVersion
 */
  public void setVersion(  int v){
    if (v != 0 && v != 1) {
      throw new IllegalArgumentException("cookie version should be 0 or 1");
    }
    version=v;
  }
  /** 
 * Returns {@code true} if this cookie contains the <i>HttpOnly</i>
 * attribute. This means that the cookie should not be accessible to
 * scripting engines, like javascript.
 * @return {@code true} if this cookie should be considered http only.
 * @see #setHttpOnly(boolean)
 */
  public boolean isHttpOnly(){
    return httpOnly;
  }
  /** 
 * Indicates whether the cookie should be considered HTTP Only. If set to{@code true} it means the cookie should not be accessible to scripting
 * engines like javascript.
 * @param httpOnly if {@code true} make the cookie HTTP only, i.e.
 * only visible as part of an HTTP request.
 * @see #isHttpOnly()
 */
  public void setHttpOnly(  boolean httpOnly){
    this.httpOnly=httpOnly;
  }
  /** 
 * The utility method to check whether a host name is in a domain
 * or not.
 * <p>This concept is described in the cookie specification.
 * To understand the concept, some terminologies need to be defined first:
 * <blockquote>
 * effective host name = hostname if host name contains dot<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;or = hostname.local if not
 * </blockquote>
 * <p>Host A's name domain-matches host B's if:
 * <blockquote><ul>
 * <li>their host name strings string-compare equal; or</li>
 * <li>A is a HDN string and has the form NB, where N is a non-empty
 * name string, B has the form .B', and B' is a HDN string.  (So,
 * x.y.com domain-matches .Y.com but not Y.com.)</li>
 * </ul></blockquote>
 * <p>A host isn't in a domain (RFC 2965 sec. 3.3.2) if:
 * <blockquote><ul>
 * <li>The value for the Domain attribute contains no embedded dots,
 * and the value is not .local.</li>
 * <li>The effective host name that derives from the request-host does
 * not domain-match the Domain attribute.</li>
 * <li>The request-host is a HDN (not IP address) and has the form HD,
 * where D is the value of the Domain attribute, and H is a string
 * that contains one or more dots.</li>
 * </ul></blockquote>
 * <p>Examples:
 * <blockquote><ul>
 * <li>A Set-Cookie2 from request-host y.x.foo.com for Domain=.foo.com
 * would be rejected, because H is y.x and contains a dot.</li>
 * <li>A Set-Cookie2 from request-host x.foo.com for Domain=.foo.com
 * would be accepted.</li>
 * <li>A Set-Cookie2 with Domain=.com or Domain=.com., will always be
 * rejected, because there is no embedded dot.</li>
 * <li>A Set-Cookie2 with Domain=ajax.com will be accepted, and the
 * value for Domain will be taken to be .ajax.com, because a dot
 * gets prepended to the value.</li>
 * <li>A Set-Cookie2 from request-host example for Domain=.local will
 * be accepted, because the effective host name for the request-
 * host is example.local, and example.local domain-matches .local.</li>
 * </ul></blockquote>
 * @param domain    the domain name to check host name with
 * @param host      the host name in question
 * @return          <tt>true</tt> if they domain-matches; <tt>false</tt> if not
 */
  public static boolean domainMatches(  String domain,  String host){
    if (domain == null || host == null)     return false;
    boolean isLocalDomain=".local".equalsIgnoreCase(domain);
    int embeddedDotInDomain=domain.indexOf('.');
    if (embeddedDotInDomain == 0)     embeddedDotInDomain=domain.indexOf('.',1);
    if (!isLocalDomain && (embeddedDotInDomain == -1 || embeddedDotInDomain == domain.length() - 1))     return false;
    int firstDotInHost=host.indexOf('.');
    if (firstDotInHost == -1 && isLocalDomain)     return true;
    int domainLength=domain.length();
    int lengthDiff=host.length() - domainLength;
    if (lengthDiff == 0) {
      return host.equalsIgnoreCase(domain);
    }
 else     if (lengthDiff > 0) {
      String H=host.substring(0,lengthDiff);
      String D=host.substring(lengthDiff);
      return (H.indexOf('.') == -1 && D.equalsIgnoreCase(domain));
    }
 else     if (lengthDiff == -1) {
      return (domain.charAt(0) == '.' && host.equalsIgnoreCase(domain.substring(1)));
    }
    return false;
  }
  /** 
 * Constructs a cookie header string representation of this cookie,
 * which is in the format defined by corresponding cookie specification,
 * but without the leading "Cookie:" token.
 * @return  a string form of the cookie. The string has the defined format
 */
  @Override public String toString(){
    if (getVersion() > 0) {
      return toRFC2965HeaderString();
    }
 else {
      return toNetscapeHeaderString();
    }
  }
  /** 
 * Test the equality of two http cookies.
 * <p> The result is <tt>true</tt> only if two cookies
 * come from same domain (case-insensitive),
 * have same name (case-insensitive),
 * and have same path (case-sensitive).
 * @return          <tt>true</tt> if 2 http cookies equal to each other;
 * otherwise, <tt>false</tt>
 */
  @Override public boolean equals(  Object obj){
    if (obj == this)     return true;
    if (!(obj instanceof HttpCookie))     return false;
    HttpCookie other=(HttpCookie)obj;
    return equalsIgnoreCase(getName(),other.getName()) && equalsIgnoreCase(getDomain(),other.getDomain()) && Objects.equals(getPath(),other.getPath());
  }
  /** 
 * Return hash code of this http cookie. The result is the sum of
 * hash code value of three significant components of this cookie:
 * name, domain, and path.
 * That is, the hash code is the value of the expression:
 * <blockquote>
 * getName().toLowerCase().hashCode()<br>
 * + getDomain().toLowerCase().hashCode()<br>
 * + getPath().hashCode()
 * </blockquote>
 * @return          this http cookie's hash code
 */
  @Override public int hashCode(){
    int h1=name.toLowerCase().hashCode();
    int h2=(domain != null) ? domain.toLowerCase().hashCode() : 0;
    int h3=(path != null) ? path.hashCode() : 0;
    return h1 + h2 + h3;
  }
  /** 
 * Create and return a copy of this object.
 * @return          a clone of this http cookie
 */
  @Override public Object clone(){
    try {
      return super.clone();
    }
 catch (    CloneNotSupportedException e) {
      throw new RuntimeException(e.getMessage());
    }
  }
  private static final String tspecials=",;";
  private static boolean isToken(  String value){
    int len=value.length();
    for (int i=0; i < len; i++) {
      char c=value.charAt(i);
      if (c < 0x20 || c >= 0x7f || tspecials.indexOf(c) != -1)       return false;
    }
    return true;
  }
  private static boolean isReserved(  String name){
    if (name.equalsIgnoreCase("Comment") || name.equalsIgnoreCase("CommentURL") || name.equalsIgnoreCase("Discard")|| name.equalsIgnoreCase("Domain")|| name.equalsIgnoreCase("Expires")|| name.equalsIgnoreCase("Max-Age")|| name.equalsIgnoreCase("Path")|| name.equalsIgnoreCase("Port")|| name.equalsIgnoreCase("Secure")|| name.equalsIgnoreCase("Version")|| name.equalsIgnoreCase("HttpOnly")|| name.charAt(0) == '$') {
      return true;
    }
    return false;
  }
  private static HttpCookie parseInternal(  String header){
    HttpCookie cookie=null;
    String namevaluePair=null;
    StringTokenizer tokenizer=new StringTokenizer(header,";");
    try {
      namevaluePair=tokenizer.nextToken();
      int index=namevaluePair.indexOf('=');
      if (index != -1) {
        String name=namevaluePair.substring(0,index).trim();
        String value=namevaluePair.substring(index + 1).trim();
        cookie=new HttpCookie(name,stripOffSurroundingQuote(value));
      }
 else {
        throw new IllegalArgumentException("Invalid cookie name-value pair");
      }
    }
 catch (    NoSuchElementException ignored) {
      throw new IllegalArgumentException("Empty cookie header string");
    }
    while (tokenizer.hasMoreTokens()) {
      namevaluePair=tokenizer.nextToken();
      int index=namevaluePair.indexOf('=');
      String name, value;
      if (index != -1) {
        name=namevaluePair.substring(0,index).trim();
        value=namevaluePair.substring(index + 1).trim();
      }
 else {
        name=namevaluePair.trim();
        value=null;
      }
      assignAttribute(cookie,name,value);
    }
    return cookie;
  }
static interface CookieAttributeAssignor {
    public void assign(    HttpCookie cookie,    String attrName,    String attrValue);
  }
  static java.util.Map<String,CookieAttributeAssignor> assignors=null;
static {
    assignors=new java.util.HashMap<String,CookieAttributeAssignor>();
    assignors.put("comment",new CookieAttributeAssignor(){
      public void assign(      HttpCookie cookie,      String attrName,      String attrValue){
        if (cookie.getComment() == null)         cookie.setComment(attrValue);
      }
    }
);
    assignors.put("commenturl",new CookieAttributeAssignor(){
      public void assign(      HttpCookie cookie,      String attrName,      String attrValue){
        if (cookie.getCommentURL() == null)         cookie.setCommentURL(attrValue);
      }
    }
);
    assignors.put("discard",new CookieAttributeAssignor(){
      public void assign(      HttpCookie cookie,      String attrName,      String attrValue){
        cookie.setDiscard(true);
      }
    }
);
    assignors.put("domain",new CookieAttributeAssignor(){
      public void assign(      HttpCookie cookie,      String attrName,      String attrValue){
        if (cookie.getDomain() == null)         cookie.setDomain(attrValue);
      }
    }
);
    assignors.put("max-age",new CookieAttributeAssignor(){
      public void assign(      HttpCookie cookie,      String attrName,      String attrValue){
        try {
          long maxage=Long.parseLong(attrValue);
          if (cookie.getMaxAge() == MAX_AGE_UNSPECIFIED)           cookie.setMaxAge(maxage);
        }
 catch (        NumberFormatException ignored) {
          throw new IllegalArgumentException("Illegal cookie max-age attribute");
        }
      }
    }
);
    assignors.put("path",new CookieAttributeAssignor(){
      public void assign(      HttpCookie cookie,      String attrName,      String attrValue){
        if (cookie.getPath() == null)         cookie.setPath(attrValue);
      }
    }
);
    assignors.put("port",new CookieAttributeAssignor(){
      public void assign(      HttpCookie cookie,      String attrName,      String attrValue){
        if (cookie.getPortlist() == null)         cookie.setPortlist(attrValue == null ? "" : attrValue);
      }
    }
);
    assignors.put("secure",new CookieAttributeAssignor(){
      public void assign(      HttpCookie cookie,      String attrName,      String attrValue){
        cookie.setSecure(true);
      }
    }
);
    assignors.put("httponly",new CookieAttributeAssignor(){
      public void assign(      HttpCookie cookie,      String attrName,      String attrValue){
        cookie.setHttpOnly(true);
      }
    }
);
    assignors.put("version",new CookieAttributeAssignor(){
      public void assign(      HttpCookie cookie,      String attrName,      String attrValue){
        try {
          int version=Integer.parseInt(attrValue);
          cookie.setVersion(version);
        }
 catch (        NumberFormatException ignored) {
        }
      }
    }
);
    assignors.put("expires",new CookieAttributeAssignor(){
      public void assign(      HttpCookie cookie,      String attrName,      String attrValue){
        if (cookie.getMaxAge() == MAX_AGE_UNSPECIFIED) {
          cookie.setMaxAge(cookie.expiryDate2DeltaSeconds(attrValue));
        }
      }
    }
);
  }
  private static void assignAttribute(  HttpCookie cookie,  String attrName,  String attrValue){
    attrValue=stripOffSurroundingQuote(attrValue);
    CookieAttributeAssignor assignor=assignors.get(attrName.toLowerCase());
    if (assignor != null) {
      assignor.assign(cookie,attrName,attrValue);
    }
 else {
    }
  }
  private String toNetscapeHeaderString(){
    StringBuilder sb=new StringBuilder();
    sb.append(getName() + "=" + getValue());
    return sb.toString();
  }
  private String toRFC2965HeaderString(){
    StringBuilder sb=new StringBuilder();
    sb.append(getName()).append("=\"").append(getValue()).append('"');
    if (getPath() != null)     sb.append(";$Path=\"").append(getPath()).append('"');
    if (getDomain() != null)     sb.append(";$Domain=\"").append(getDomain()).append('"');
    if (getPortlist() != null)     sb.append(";$Port=\"").append(getPortlist()).append('"');
    return sb.toString();
  }
  static final TimeZone GMT=TimeZone.getTimeZone("GMT");
  private long expiryDate2DeltaSeconds(  String dateString){
    for (int i=0; i < COOKIE_DATE_FORMATS.length; i++) {
      SimpleDateFormat df=new SimpleDateFormat(COOKIE_DATE_FORMATS[i],Locale.US);
      df.setTimeZone(GMT);
      try {
        Date date=df.parse(dateString);
        return (date.getTime() - whenCreated) / 1000;
      }
 catch (      Exception e) {
      }
    }
    return 0;
  }
  private static int guessCookieVersion(  String header){
    int version=0;
    header=header.toLowerCase();
    if (header.indexOf("expires=") != -1) {
      version=0;
    }
 else     if (header.indexOf("version=") != -1) {
      version=1;
    }
 else     if (header.indexOf("max-age") != -1) {
      version=1;
    }
 else     if (startsWithIgnoreCase(header,SET_COOKIE2)) {
      version=1;
    }
    return version;
  }
  private static String stripOffSurroundingQuote(  String str){
    if (str != null && str.length() > 2 && str.charAt(0) == '"' && str.charAt(str.length() - 1) == '"') {
      return str.substring(1,str.length() - 1);
    }
    if (str != null && str.length() > 2 && str.charAt(0) == '\'' && str.charAt(str.length() - 1) == '\'') {
      return str.substring(1,str.length() - 1);
    }
    return str;
  }
  private static boolean equalsIgnoreCase(  String s,  String t){
    if (s == t)     return true;
    if ((s != null) && (t != null)) {
      return s.equalsIgnoreCase(t);
    }
    return false;
  }
  private static boolean startsWithIgnoreCase(  String s,  String start){
    if (s == null || start == null)     return false;
    if (s.length() >= start.length() && start.equalsIgnoreCase(s.substring(0,start.length()))) {
      return true;
    }
    return false;
  }
  private static List<String> splitMultiCookies(  String header){
    List<String> cookies=new java.util.ArrayList<String>();
    int quoteCount=0;
    int p, q;
    for (p=0, q=0; p < header.length(); p++) {
      char c=header.charAt(p);
      if (c == '"')       quoteCount++;
      if (c == ',' && (quoteCount % 2 == 0)) {
        cookies.add(header.substring(q,p));
        q=p + 1;
      }
    }
    cookies.add(header.substring(q));
    return cookies;
  }
}
