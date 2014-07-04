package javax.naming.ldap;
import java.util.List;
import java.util.ArrayList;
import javax.naming.InvalidNameException;
final class Rfc2253Parser {
  private final String name;
  private final char[] chars;
  private final int len;
  private int cur=0;
  Rfc2253Parser(  String name){
    this.name=name;
    len=name.length();
    chars=name.toCharArray();
  }
  List parseDn() throws InvalidNameException {
    cur=0;
    ArrayList rdns=new ArrayList(len / 3 + 10);
    if (len == 0) {
      return rdns;
    }
    rdns.add(doParse(new Rdn()));
    while (cur < len) {
      if (chars[cur] == ',' || chars[cur] == ';') {
        ++cur;
        rdns.add(0,doParse(new Rdn()));
      }
 else {
        throw new InvalidNameException("Invalid name: " + name);
      }
    }
    return rdns;
  }
  Rdn parseRdn() throws InvalidNameException {
    return parseRdn(new Rdn());
  }
  Rdn parseRdn(  Rdn rdn) throws InvalidNameException {
    rdn=doParse(rdn);
    if (cur < len) {
      throw new InvalidNameException("Invalid RDN: " + name);
    }
    return rdn;
  }
  private Rdn doParse(  Rdn rdn) throws InvalidNameException {
    while (cur < len) {
      consumeWhitespace();
      String attrType=parseAttrType();
      consumeWhitespace();
      if (cur >= len || chars[cur] != '=') {
        throw new InvalidNameException("Invalid name: " + name);
      }
      ++cur;
      consumeWhitespace();
      String value=parseAttrValue();
      consumeWhitespace();
      rdn.put(attrType,Rdn.unescapeValue(value));
      if (cur >= len || chars[cur] != '+') {
        break;
      }
      ++cur;
    }
    rdn.sort();
    return rdn;
  }
  private String parseAttrType() throws InvalidNameException {
    final int beg=cur;
    while (cur < len) {
      char c=chars[cur];
      if (Character.isLetterOrDigit(c) || c == '.' || c == '-' || c == ' ') {
        ++cur;
      }
 else {
        break;
      }
    }
    while ((cur > beg) && (chars[cur - 1] == ' ')) {
      --cur;
    }
    if (beg == cur) {
      throw new InvalidNameException("Invalid name: " + name);
    }
    return new String(chars,beg,cur - beg);
  }
  private String parseAttrValue() throws InvalidNameException {
    if (cur < len && chars[cur] == '#') {
      return parseBinaryAttrValue();
    }
 else     if (cur < len && chars[cur] == '"') {
      return parseQuotedAttrValue();
    }
 else {
      return parseStringAttrValue();
    }
  }
  private String parseBinaryAttrValue() throws InvalidNameException {
    final int beg=cur;
    ++cur;
    while ((cur < len) && Character.isLetterOrDigit(chars[cur])) {
      ++cur;
    }
    return new String(chars,beg,cur - beg);
  }
  private String parseQuotedAttrValue() throws InvalidNameException {
    final int beg=cur;
    ++cur;
    while ((cur < len) && chars[cur] != '"') {
      if (chars[cur] == '\\') {
        ++cur;
      }
      ++cur;
    }
    if (cur >= len) {
      throw new InvalidNameException("Invalid name: " + name);
    }
    ++cur;
    return new String(chars,beg,cur - beg);
  }
  private String parseStringAttrValue() throws InvalidNameException {
    final int beg=cur;
    int esc=-1;
    while ((cur < len) && !atTerminator()) {
      if (chars[cur] == '\\') {
        ++cur;
        esc=cur;
      }
      ++cur;
    }
    if (cur > len) {
      throw new InvalidNameException("Invalid name: " + name);
    }
    int end;
    for (end=cur; end > beg; end--) {
      if (!isWhitespace(chars[end - 1]) || (esc == end - 1)) {
        break;
      }
    }
    return new String(chars,beg,end - beg);
  }
  private void consumeWhitespace(){
    while ((cur < len) && isWhitespace(chars[cur])) {
      ++cur;
    }
  }
  private boolean atTerminator(){
    return (cur < len && (chars[cur] == ',' || chars[cur] == ';' || chars[cur] == '+'));
  }
  private static boolean isWhitespace(  char c){
    return (c == ' ' || c == '\r');
  }
}
