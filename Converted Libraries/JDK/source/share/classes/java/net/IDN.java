package java.net;
import java.io.InputStream;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.net.idn.StringPrep;
import sun.net.idn.Punycode;
import sun.text.normalizer.UCharacterIterator;
/** 
 * Provides methods to convert internationalized domain names (IDNs) between
 * a normal Unicode representation and an ASCII Compatible Encoding (ACE) representation.
 * Internationalized domain names can use characters from the entire range of
 * Unicode, while traditional domain names are restricted to ASCII characters.
 * ACE is an encoding of Unicode strings that uses only ASCII characters and
 * can be used with software (such as the Domain Name System) that only
 * understands traditional domain names.
 * <p>Internationalized domain names are defined in <a href="http://www.ietf.org/rfc/rfc3490.txt">RFC 3490</a>.
 * RFC 3490 defines two operations: ToASCII and ToUnicode. These 2 operations employ
 * <a href="http://www.ietf.org/rfc/rfc3491.txt">Nameprep</a> algorithm, which is a
 * profile of <a href="http://www.ietf.org/rfc/rfc3454.txt">Stringprep</a>, and
 * <a href="http://www.ietf.org/rfc/rfc3492.txt">Punycode</a> algorithm to convert
 * domain name string back and forth.
 * <p>The behavior of aforementioned conversion process can be adjusted by various flags:
 * <ul>
 * <li>If the ALLOW_UNASSIGNED flag is used, the domain name string to be converted
 * can contain code points that are unassigned in Unicode 3.2, which is the
 * Unicode version on which IDN conversion is based. If the flag is not used,
 * the presence of such unassigned code points is treated as an error.
 * <li>If the USE_STD3_ASCII_RULES flag is used, ASCII strings are checked against <a href="http://www.ietf.org/rfc/rfc1122.txt">RFC 1122</a> and <a href="http://www.ietf.org/rfc/rfc1123.txt">RFC 1123</a>.
 * It is an error if they don't meet the requirements.
 * </ul>
 * These flags can be logically OR'ed together.
 * <p>The security consideration is important with respect to internationalization
 * domain name support. For example, English domain names may be <i>homographed</i>
 * - maliciously misspelled by substitution of non-Latin letters.
 * <a href="http://www.unicode.org/reports/tr36/">Unicode Technical Report #36</a>
 * discusses security issues of IDN support as well as possible solutions.
 * Applications are responsible for taking adequate security measures when using
 * international domain names.
 * @author Edward Wang
 * @since 1.6
 */
public final class IDN {
  /** 
 * Flag to allow processing of unassigned code points
 */
  public static final int ALLOW_UNASSIGNED=0x01;
  /** 
 * Flag to turn on the check against STD-3 ASCII rules
 */
  public static final int USE_STD3_ASCII_RULES=0x02;
  /** 
 * Translates a string from Unicode to ASCII Compatible Encoding (ACE),
 * as defined by the ToASCII operation of <a href="http://www.ietf.org/rfc/rfc3490.txt">RFC 3490</a>.
 * <p>ToASCII operation can fail. ToASCII fails if any step of it fails.
 * If ToASCII operation fails, an IllegalArgumentException will be thrown.
 * In this case, the input string should not be used in an internationalized domain name.
 * <p> A label is an individual part of a domain name. The original ToASCII operation,
 * as defined in RFC 3490, only operates on a single label. This method can handle
 * both label and entire domain name, by assuming that labels in a domain name are
 * always separated by dots. The following characters are recognized as dots:
 * &#0092;u002E (full stop), &#0092;u3002 (ideographic full stop), &#0092;uFF0E (fullwidth full stop),
 * and &#0092;uFF61 (halfwidth ideographic full stop). if dots are
 * used as label separators, this method also changes all of them to &#0092;u002E (full stop)
 * in output translated string.
 * @param input     the string to be processed
 * @param flag      process flag; can be 0 or any logical OR of possible flags
 * @return          the translated <tt>String</tt>
 * @throws IllegalArgumentException   if the input string doesn't conform to RFC 3490 specification
 */
  public static String toASCII(  String input,  int flag){
    int p=0, q=0;
    StringBuffer out=new StringBuffer();
    while (p < input.length()) {
      q=searchDots(input,p);
      out.append(toASCIIInternal(input.substring(p,q),flag));
      p=q + 1;
      if (p < input.length())       out.append('.');
    }
    return out.toString();
  }
  /** 
 * Translates a string from Unicode to ASCII Compatible Encoding (ACE),
 * as defined by the ToASCII operation of <a href="http://www.ietf.org/rfc/rfc3490.txt">RFC 3490</a>.
 * <p> This convenience method works as if by invoking the
 * two-argument counterpart as follows:
 * <blockquote><tt>{@link #toASCII(String,int) toASCII}(input,&nbsp;0);
 * </tt></blockquote>
 * @param input     the string to be processed
 * @return          the translated <tt>String</tt>
 * @throws IllegalArgumentException   if the input string doesn't conform to RFC 3490 specification
 */
  public static String toASCII(  String input){
    return toASCII(input,0);
  }
  /** 
 * Translates a string from ASCII Compatible Encoding (ACE) to Unicode,
 * as defined by the ToUnicode operation of <a href="http://www.ietf.org/rfc/rfc3490.txt">RFC 3490</a>.
 * <p>ToUnicode never fails. In case of any error, the input string is returned unmodified.
 * <p> A label is an individual part of a domain name. The original ToUnicode operation,
 * as defined in RFC 3490, only operates on a single label. This method can handle
 * both label and entire domain name, by assuming that labels in a domain name are
 * always separated by dots. The following characters are recognized as dots:
 * &#0092;u002E (full stop), &#0092;u3002 (ideographic full stop), &#0092;uFF0E (fullwidth full stop),
 * and &#0092;uFF61 (halfwidth ideographic full stop).
 * @param input     the string to be processed
 * @param flag      process flag; can be 0 or any logical OR of possible flags
 * @return          the translated <tt>String</tt>
 */
  public static String toUnicode(  String input,  int flag){
    int p=0, q=0;
    StringBuffer out=new StringBuffer();
    while (p < input.length()) {
      q=searchDots(input,p);
      out.append(toUnicodeInternal(input.substring(p,q),flag));
      p=q + 1;
      if (p < input.length())       out.append('.');
    }
    return out.toString();
  }
  /** 
 * Translates a string from ASCII Compatible Encoding (ACE) to Unicode,
 * as defined by the ToUnicode operation of <a href="http://www.ietf.org/rfc/rfc3490.txt">RFC 3490</a>.
 * <p> This convenience method works as if by invoking the
 * two-argument counterpart as follows:
 * <blockquote><tt>{@link #toUnicode(String,int) toUnicode}(input,&nbsp;0);
 * </tt></blockquote>
 * @param input     the string to be processed
 * @return          the translated <tt>String</tt>
 */
  public static String toUnicode(  String input){
    return toUnicode(input,0);
  }
  private static final String ACE_PREFIX="xn--";
  private static final int ACE_PREFIX_LENGTH=ACE_PREFIX.length();
  private static final int MAX_LABEL_LENGTH=63;
  private static StringPrep namePrep=null;
static {
    InputStream stream=null;
    try {
      final String IDN_PROFILE="uidna.spp";
      if (System.getSecurityManager() != null) {
        stream=AccessController.doPrivileged(new PrivilegedAction<InputStream>(){
          public InputStream run(){
            return StringPrep.class.getResourceAsStream(IDN_PROFILE);
          }
        }
);
      }
 else {
        stream=StringPrep.class.getResourceAsStream(IDN_PROFILE);
      }
      namePrep=new StringPrep(stream);
      stream.close();
    }
 catch (    IOException e) {
      assert false;
    }
  }
  private IDN(){
  }
  private static String toASCIIInternal(  String label,  int flag){
    boolean isASCII=isAllASCII(label);
    StringBuffer dest;
    if (!isASCII) {
      UCharacterIterator iter=UCharacterIterator.getInstance(label);
      try {
        dest=namePrep.prepare(iter,flag);
      }
 catch (      java.text.ParseException e) {
        throw new IllegalArgumentException(e);
      }
    }
 else {
      dest=new StringBuffer(label);
    }
    boolean useSTD3ASCIIRules=((flag & USE_STD3_ASCII_RULES) != 0);
    if (useSTD3ASCIIRules) {
      for (int i=0; i < dest.length(); i++) {
        int c=dest.charAt(i);
        if (!isLDHChar(c)) {
          throw new IllegalArgumentException("Contains non-LDH characters");
        }
      }
      if (dest.charAt(0) == '-' || dest.charAt(dest.length() - 1) == '-') {
        throw new IllegalArgumentException("Has leading or trailing hyphen");
      }
    }
    if (!isASCII) {
      if (!isAllASCII(dest.toString())) {
        if (!startsWithACEPrefix(dest)) {
          try {
            dest=Punycode.encode(dest,null);
          }
 catch (          java.text.ParseException e) {
            throw new IllegalArgumentException(e);
          }
          dest=toASCIILower(dest);
          dest.insert(0,ACE_PREFIX);
        }
 else {
          throw new IllegalArgumentException("The input starts with the ACE Prefix");
        }
      }
    }
    if (dest.length() > MAX_LABEL_LENGTH) {
      throw new IllegalArgumentException("The label in the input is too long");
    }
    return dest.toString();
  }
  private static String toUnicodeInternal(  String label,  int flag){
    boolean[] caseFlags=null;
    StringBuffer dest;
    boolean isASCII=isAllASCII(label);
    if (!isASCII) {
      try {
        UCharacterIterator iter=UCharacterIterator.getInstance(label);
        dest=namePrep.prepare(iter,flag);
      }
 catch (      Exception e) {
        return label;
      }
    }
 else {
      dest=new StringBuffer(label);
    }
    if (startsWithACEPrefix(dest)) {
      String temp=dest.substring(ACE_PREFIX_LENGTH,dest.length());
      try {
        StringBuffer decodeOut=Punycode.decode(new StringBuffer(temp),null);
        String toASCIIOut=toASCII(decodeOut.toString(),flag);
        if (toASCIIOut.equalsIgnoreCase(dest.toString())) {
          return decodeOut.toString();
        }
      }
 catch (      Exception ignored) {
      }
    }
    return label;
  }
  private static boolean isLDHChar(  int ch){
    if (ch > 0x007A) {
      return false;
    }
    if ((ch == 0x002D) || (0x0030 <= ch && ch <= 0x0039) || (0x0041 <= ch && ch <= 0x005A)|| (0x0061 <= ch && ch <= 0x007A)) {
      return true;
    }
    return false;
  }
  private static int searchDots(  String s,  int start){
    int i;
    for (i=start; i < s.length(); i++) {
      char c=s.charAt(i);
      if (c == '.' || c == '\u3002' || c == '\uFF0E' || c == '\uFF61') {
        break;
      }
    }
    return i;
  }
  private static boolean isAllASCII(  String input){
    boolean isASCII=true;
    for (int i=0; i < input.length(); i++) {
      int c=input.charAt(i);
      if (c > 0x7F) {
        isASCII=false;
        break;
      }
    }
    return isASCII;
  }
  private static boolean startsWithACEPrefix(  StringBuffer input){
    boolean startsWithPrefix=true;
    if (input.length() < ACE_PREFIX_LENGTH) {
      return false;
    }
    for (int i=0; i < ACE_PREFIX_LENGTH; i++) {
      if (toASCIILower(input.charAt(i)) != ACE_PREFIX.charAt(i)) {
        startsWithPrefix=false;
      }
    }
    return startsWithPrefix;
  }
  private static char toASCIILower(  char ch){
    if ('A' <= ch && ch <= 'Z') {
      return (char)(ch + 'a' - 'A');
    }
    return ch;
  }
  private static StringBuffer toASCIILower(  StringBuffer input){
    StringBuffer dest=new StringBuffer();
    for (int i=0; i < input.length(); i++) {
      dest.append(toASCIILower(input.charAt(i)));
    }
    return dest;
  }
}
