package java.net;
import java.io.ByteArrayOutputStream;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.CharArrayWriter;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.BitSet;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.security.action.GetBooleanAction;
import sun.security.action.GetPropertyAction;
/** 
 * Utility class for HTML form encoding. This class contains static methods
 * for converting a String to the <CODE>application/x-www-form-urlencoded</CODE> MIME
 * format. For more information about HTML form encoding, consult the HTML
 * <A HREF="http://www.w3.org/TR/html4/">specification</A>.
 * <p>
 * When encoding a String, the following rules apply:
 * <p>
 * <ul>
 * <li>The alphanumeric characters &quot;<code>a</code>&quot; through
 * &quot;<code>z</code>&quot;, &quot;<code>A</code>&quot; through
 * &quot;<code>Z</code>&quot; and &quot;<code>0</code>&quot;
 * through &quot;<code>9</code>&quot; remain the same.
 * <li>The special characters &quot;<code>.</code>&quot;,
 * &quot;<code>-</code>&quot;, &quot;<code>*</code>&quot;, and
 * &quot;<code>_</code>&quot; remain the same.
 * <li>The space character &quot;<code>&nbsp;</code>&quot; is
 * converted into a plus sign &quot;<code>+</code>&quot;.
 * <li>All other characters are unsafe and are first converted into
 * one or more bytes using some encoding scheme. Then each byte is
 * represented by the 3-character string
 * &quot;<code>%<i>xy</i></code>&quot;, where <i>xy</i> is the
 * two-digit hexadecimal representation of the byte.
 * The recommended encoding scheme to use is UTF-8. However,
 * for compatibility reasons, if an encoding is not specified,
 * then the default encoding of the platform is used.
 * </ul>
 * <p>
 * For example using UTF-8 as the encoding scheme the string &quot;The
 * string &#252;@foo-bar&quot; would get converted to
 * &quot;The+string+%C3%BC%40foo-bar&quot; because in UTF-8 the character
 * &#252; is encoded as two bytes C3 (hex) and BC (hex), and the
 * character @ is encoded as one byte 40 (hex).
 * @author  Herb Jellinek
 * @since   JDK1.0
 */
public class URLEncoder {
  static BitSet dontNeedEncoding;
  static final int caseDiff=('a' - 'A');
  static String dfltEncName=null;
static {
    dontNeedEncoding=new BitSet(256);
    int i;
    for (i='a'; i <= 'z'; i++) {
      dontNeedEncoding.set(i);
    }
    for (i='A'; i <= 'Z'; i++) {
      dontNeedEncoding.set(i);
    }
    for (i='0'; i <= '9'; i++) {
      dontNeedEncoding.set(i);
    }
    dontNeedEncoding.set(' ');
    dontNeedEncoding.set('-');
    dontNeedEncoding.set('_');
    dontNeedEncoding.set('.');
    dontNeedEncoding.set('*');
    dfltEncName=AccessController.doPrivileged(new GetPropertyAction("file.encoding"));
  }
  /** 
 * You can't call the constructor.
 */
  private URLEncoder(){
  }
  /** 
 * Translates a string into <code>x-www-form-urlencoded</code>
 * format. This method uses the platform's default encoding
 * as the encoding scheme to obtain the bytes for unsafe characters.
 * @param s   <code>String</code> to be translated.
 * @deprecated The resulting string may vary depending on the platform's
 * default encoding. Instead, use the encode(String,String)
 * method to specify the encoding.
 * @return  the translated <code>String</code>.
 */
  @Deprecated public static String encode(  String s){
    String str=null;
    try {
      str=encode(s,dfltEncName);
    }
 catch (    UnsupportedEncodingException e) {
    }
    return str;
  }
  /** 
 * Translates a string into <code>application/x-www-form-urlencoded</code>
 * format using a specific encoding scheme. This method uses the
 * supplied encoding scheme to obtain the bytes for unsafe
 * characters.
 * <p>
 * <em><strong>Note:</strong> The <a href=
 * "http://www.w3.org/TR/html40/appendix/notes.html#non-ascii-chars">
 * World Wide Web Consortium Recommendation</a> states that
 * UTF-8 should be used. Not doing so may introduce
 * incompatibilites.</em>
 * @param s   <code>String</code> to be translated.
 * @param enc   The name of a supported
 * <a href="../lang/package-summary.html#charenc">character
 * encoding</a>.
 * @return  the translated <code>String</code>.
 * @exception UnsupportedEncodingExceptionIf the named encoding is not supported
 * @see URLDecoder#decode(java.lang.String,java.lang.String)
 * @since 1.4
 */
  public static String encode(  String s,  String enc) throws UnsupportedEncodingException {
    boolean needToChange=false;
    StringBuffer out=new StringBuffer(s.length());
    Charset charset;
    CharArrayWriter charArrayWriter=new CharArrayWriter();
    if (enc == null)     throw new NullPointerException("charsetName");
    try {
      charset=Charset.forName(enc);
    }
 catch (    IllegalCharsetNameException e) {
      throw new UnsupportedEncodingException(enc);
    }
catch (    UnsupportedCharsetException e) {
      throw new UnsupportedEncodingException(enc);
    }
    for (int i=0; i < s.length(); ) {
      int c=(int)s.charAt(i);
      if (dontNeedEncoding.get(c)) {
        if (c == ' ') {
          c='+';
          needToChange=true;
        }
        out.append((char)c);
        i++;
      }
 else {
        do {
          charArrayWriter.write(c);
          if (c >= 0xD800 && c <= 0xDBFF) {
            if ((i + 1) < s.length()) {
              int d=(int)s.charAt(i + 1);
              if (d >= 0xDC00 && d <= 0xDFFF) {
                charArrayWriter.write(d);
                i++;
              }
            }
          }
          i++;
        }
 while (i < s.length() && !dontNeedEncoding.get((c=(int)s.charAt(i))));
        charArrayWriter.flush();
        String str=new String(charArrayWriter.toCharArray());
        byte[] ba=str.getBytes(charset);
        for (int j=0; j < ba.length; j++) {
          out.append('%');
          char ch=Character.forDigit((ba[j] >> 4) & 0xF,16);
          if (Character.isLetter(ch)) {
            ch-=caseDiff;
          }
          out.append(ch);
          ch=Character.forDigit(ba[j] & 0xF,16);
          if (Character.isLetter(ch)) {
            ch-=caseDiff;
          }
          out.append(ch);
        }
        charArrayWriter.reset();
        needToChange=true;
      }
    }
    return (needToChange ? out.toString() : s);
  }
}