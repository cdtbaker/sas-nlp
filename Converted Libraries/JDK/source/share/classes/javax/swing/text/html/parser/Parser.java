package javax.swing.text.html.parser;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.ChangedCharSetException;
import java.io.*;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;
import java.util.Enumeration;
import java.net.URL;
import sun.misc.MessageUtils;
/** 
 * A simple DTD-driven HTML parser. The parser reads an
 * HTML file from an InputStream and calls various methods
 * (which should be overridden in a subclass) when tags and
 * data are encountered.
 * <p>
 * Unfortunately there are many badly implemented HTML parsers
 * out there, and as a result there are many badly formatted
 * HTML files. This parser attempts to parse most HTML files.
 * This means that the implementation sometimes deviates from
 * the SGML specification in favor of HTML.
 * <p>
 * The parser treats \r and \r\n as \n. Newlines after starttags
 * and before end tags are ignored just as specified in the SGML/HTML
 * specification.
 * <p>
 * The html spec does not specify how spaces are to be coalesced very well.
 * Specifically, the following scenarios are not discussed (note that a
 * space should be used here, but I am using &amp;nbsp to force the space to
 * be displayed):
 * <p>
 * '&lt;b>blah&nbsp;&lt;i>&nbsp;&lt;strike>&nbsp;foo' which can be treated as:
 * '&lt;b>blah&nbsp;&lt;i>&lt;strike>foo'
 * <p>as well as:
 * '&lt;p>&lt;a href="xx">&nbsp;&lt;em>Using&lt;/em>&lt;/a>&lt;/p>'
 * which appears to be treated as:
 * '&lt;p>&lt;a href="xx">&lt;em>Using&lt;/em>&lt;/a>&lt;/p>'
 * <p>
 * If <code>strict</code> is false, when a tag that breaks flow,
 * (<code>TagElement.breaksFlows</code>) or trailing whitespace is
 * encountered, all whitespace will be ignored until a non whitespace
 * character is encountered. This appears to give behavior closer to
 * the popular browsers.
 * @see DTD
 * @see TagElement
 * @see SimpleAttributeSet
 * @author Arthur van Hoff
 * @author Sunita Mani
 */
public class Parser implements DTDConstants {
  private char text[]=new char[1024];
  private int textpos=0;
  private TagElement last;
  private boolean space;
  private char str[]=new char[128];
  private int strpos=0;
  protected DTD dtd=null;
  private int ch;
  private int ln;
  private Reader in;
  private Element recent;
  private TagStack stack;
  private boolean skipTag=false;
  private TagElement lastFormSent=null;
  private SimpleAttributeSet attributes=new SimpleAttributeSet();
  private boolean seenHtml=false;
  private boolean seenHead=false;
  private boolean seenBody=false;
  /** 
 * The html spec does not specify how spaces are coalesced very well.
 * If strict == false, ignoreSpace is used to try and mimic the behavior
 * of the popular browsers.
 * <p>
 * The problematic scenarios are:
 * '&lt;b>blah &lt;i> &lt;strike> foo' which can be treated as:
 * '&lt;b>blah &lt;i>&lt;strike>foo'
 * as well as:
 * '&lt;p>&lt;a href="xx"> &lt;em>Using&lt;/em>&lt;/a>&lt;/p>'
 * which appears to be treated as:
 * '&lt;p>&lt;a href="xx">&lt;em>Using&lt;/em>&lt;/a>&lt;/p>'
 * <p>
 * When a tag that breaks flow, or trailing whitespace is encountered
 * ignoreSpace is set to true. From then on, all whitespace will be
 * ignored.
 * ignoreSpace will be set back to false the first time a
 * non whitespace character is encountered. This appears to give
 * behavior closer to the popular browsers.
 */
  private boolean ignoreSpace;
  /** 
 * This flag determines whether or not the Parser will be strict
 * in enforcing SGML compatibility.  If false, it will be lenient
 * with certain common classes of erroneous HTML constructs.
 * Strict or not, in either case an error will be recorded.
 */
  protected boolean strict=false;
  /** 
 * Number of \r\n's encountered. 
 */
  private int crlfCount;
  /** 
 * Number of \r's encountered. A \r\n will not increment this. 
 */
  private int crCount;
  /** 
 * Number of \n's encountered. A \r\n will not increment this. 
 */
  private int lfCount;
  /** 
 * The start position of the current block. Block is overloaded here,
 * it really means the current start position for the current comment,
 * tag, text. Use getBlockStartPosition to access this. 
 */
  private int currentBlockStartPos;
  /** 
 * Start position of the last block. 
 */
  private int lastBlockStartPos;
  /** 
 * array for mapping numeric references in range
 * 130-159 to displayable Unicode characters.
 */
  private static final char[] cp1252Map={8218,402,8222,8230,8224,8225,710,8240,352,8249,338,141,142,143,144,8216,8217,8220,8221,8226,8211,8212,732,8482,353,8250,339,157,158,376};
  public Parser(  DTD dtd){
    this.dtd=dtd;
  }
  /** 
 * @return the line number of the line currently being parsed
 */
  protected int getCurrentLine(){
    return ln;
  }
  /** 
 * Returns the start position of the current block. Block is
 * overloaded here, it really means the current start position for
 * the current comment tag, text, block.... This is provided for
 * subclassers that wish to know the start of the current block when
 * called with one of the handleXXX methods.
 */
  int getBlockStartPosition(){
    return Math.max(0,lastBlockStartPos - 1);
  }
  /** 
 * Makes a TagElement.
 */
  protected TagElement makeTag(  Element elem,  boolean fictional){
    return new TagElement(elem,fictional);
  }
  protected TagElement makeTag(  Element elem){
    return makeTag(elem,false);
  }
  protected SimpleAttributeSet getAttributes(){
    return attributes;
  }
  protected void flushAttributes(){
    attributes.removeAttributes(attributes);
  }
  /** 
 * Called when PCDATA is encountered.
 */
  protected void handleText(  char text[]){
  }
  /** 
 * Called when an HTML title tag is encountered.
 */
  protected void handleTitle(  char text[]){
    handleText(text);
  }
  /** 
 * Called when an HTML comment is encountered.
 */
  protected void handleComment(  char text[]){
  }
  protected void handleEOFInComment(){
    int commentEndPos=strIndexOf('\n');
    if (commentEndPos >= 0) {
      handleComment(getChars(0,commentEndPos));
      try {
        in.close();
        in=new CharArrayReader(getChars(commentEndPos + 1));
        ch='>';
      }
 catch (      IOException e) {
        error("ioexception");
      }
      resetStrBuffer();
    }
 else {
      error("eof.comment");
    }
  }
  /** 
 * Called when an empty tag is encountered.
 */
  protected void handleEmptyTag(  TagElement tag) throws ChangedCharSetException {
  }
  /** 
 * Called when a start tag is encountered.
 */
  protected void handleStartTag(  TagElement tag){
  }
  /** 
 * Called when an end tag is encountered.
 */
  protected void handleEndTag(  TagElement tag){
  }
  /** 
 * An error has occurred.
 */
  protected void handleError(  int ln,  String msg){
  }
  /** 
 * Output text.
 */
  void handleText(  TagElement tag){
    if (tag.breaksFlow()) {
      space=false;
      if (!strict) {
        ignoreSpace=true;
      }
    }
    if (textpos == 0) {
      if ((!space) || (stack == null) || last.breaksFlow()|| !stack.advance(dtd.pcdata)) {
        last=tag;
        space=false;
        lastBlockStartPos=currentBlockStartPos;
        return;
      }
    }
    if (space) {
      if (!ignoreSpace) {
        if (textpos + 1 > text.length) {
          char newtext[]=new char[text.length + 200];
          System.arraycopy(text,0,newtext,0,text.length);
          text=newtext;
        }
        text[textpos++]=' ';
        if (!strict && !tag.getElement().isEmpty()) {
          ignoreSpace=true;
        }
      }
      space=false;
    }
    char newtext[]=new char[textpos];
    System.arraycopy(text,0,newtext,0,textpos);
    if (tag.getElement().getName().equals("title")) {
      handleTitle(newtext);
    }
 else {
      handleText(newtext);
    }
    lastBlockStartPos=currentBlockStartPos;
    textpos=0;
    last=tag;
    space=false;
  }
  /** 
 * Invoke the error handler.
 */
  protected void error(  String err,  String arg1,  String arg2,  String arg3){
    handleError(ln,err + " " + arg1+ " "+ arg2+ " "+ arg3);
  }
  protected void error(  String err,  String arg1,  String arg2){
    error(err,arg1,arg2,"?");
  }
  protected void error(  String err,  String arg1){
    error(err,arg1,"?","?");
  }
  protected void error(  String err){
    error(err,"?","?","?");
  }
  /** 
 * Handle a start tag. The new tag is pushed
 * onto the tag stack. The attribute list is
 * checked for required attributes.
 */
  protected void startTag(  TagElement tag) throws ChangedCharSetException {
    Element elem=tag.getElement();
    if (!elem.isEmpty() || ((last != null) && !last.breaksFlow()) || (textpos != 0)) {
      handleText(tag);
    }
 else {
      last=tag;
      space=false;
    }
    lastBlockStartPos=currentBlockStartPos;
    for (AttributeList a=elem.atts; a != null; a=a.next) {
      if ((a.modifier == REQUIRED) && ((attributes.isEmpty()) || ((!attributes.isDefined(a.name)) && (!attributes.isDefined(HTML.getAttributeKey(a.name)))))) {
        error("req.att ",a.getName(),elem.getName());
      }
    }
    if (elem.isEmpty()) {
      handleEmptyTag(tag);
    }
 else {
      recent=elem;
      stack=new TagStack(tag,stack);
      handleStartTag(tag);
    }
  }
  /** 
 * Handle an end tag. The end tag is popped
 * from the tag stack.
 */
  protected void endTag(  boolean omitted){
    handleText(stack.tag);
    if (omitted && !stack.elem.omitEnd()) {
      error("end.missing",stack.elem.getName());
    }
 else     if (!stack.terminate()) {
      error("end.unexpected",stack.elem.getName());
    }
    handleEndTag(stack.tag);
    stack=stack.next;
    recent=(stack != null) ? stack.elem : null;
  }
  boolean ignoreElement(  Element elem){
    String stackElement=stack.elem.getName();
    String elemName=elem.getName();
    if ((elemName.equals("html") && seenHtml) || (elemName.equals("head") && seenHead) || (elemName.equals("body") && seenBody)) {
      return true;
    }
    if (elemName.equals("dt") || elemName.equals("dd")) {
      TagStack s=stack;
      while (s != null && !s.elem.getName().equals("dl")) {
        s=s.next;
      }
      if (s == null) {
        return true;
      }
    }
    if (((stackElement.equals("table")) && (!elemName.equals("#pcdata")) && (!elemName.equals("input"))) || ((elemName.equals("font")) && (stackElement.equals("ul") || stackElement.equals("ol"))) || (elemName.equals("meta") && stack != null)|| (elemName.equals("style") && seenBody)|| (stackElement.equals("table") && elemName.equals("a"))) {
      return true;
    }
    return false;
  }
  /** 
 * Marks the first time a tag has been seen in a document
 */
  protected void markFirstTime(  Element elem){
    String elemName=elem.getName();
    if (elemName.equals("html")) {
      seenHtml=true;
    }
 else     if (elemName.equals("head")) {
      seenHead=true;
    }
 else     if (elemName.equals("body")) {
      if (buf.length == 1) {
        char[] newBuf=new char[256];
        newBuf[0]=buf[0];
        buf=newBuf;
      }
      seenBody=true;
    }
  }
  /** 
 * Create a legal content for an element.
 */
  boolean legalElementContext(  Element elem) throws ChangedCharSetException {
    if (stack == null) {
      if (elem != dtd.html) {
        startTag(makeTag(dtd.html,true));
        return legalElementContext(elem);
      }
      return true;
    }
    if (stack.advance(elem)) {
      markFirstTime(elem);
      return true;
    }
    boolean insertTag=false;
    String stackElemName=stack.elem.getName();
    String elemName=elem.getName();
    if (!strict && ((stackElemName.equals("table") && elemName.equals("td")) || (stackElemName.equals("table") && elemName.equals("th")) || (stackElemName.equals("tr") && !elemName.equals("tr")))) {
      insertTag=true;
    }
    if (!strict && !insertTag && (stack.elem.getName() != elem.getName() || elem.getName().equals("body"))) {
      if (skipTag=ignoreElement(elem)) {
        error("tag.ignore",elem.getName());
        return skipTag;
      }
    }
    if (!strict && stackElemName.equals("table") && !elemName.equals("tr")&& !elemName.equals("td")&& !elemName.equals("th")&& !elemName.equals("caption")) {
      Element e=dtd.getElement("tr");
      TagElement t=makeTag(e,true);
      legalTagContext(t);
      startTag(t);
      error("start.missing",elem.getName());
      return legalElementContext(elem);
    }
    if (!insertTag && stack.terminate() && (!strict || stack.elem.omitEnd())) {
      for (TagStack s=stack.next; s != null; s=s.next) {
        if (s.advance(elem)) {
          while (stack != s) {
            endTag(true);
          }
          return true;
        }
        if (!s.terminate() || (strict && !s.elem.omitEnd())) {
          break;
        }
      }
    }
    Element next=stack.first();
    if (next != null && (!strict || next.omitStart()) && !(next == dtd.head && elem == dtd.pcdata)) {
      TagElement t=makeTag(next,true);
      legalTagContext(t);
      startTag(t);
      if (!next.omitStart()) {
        error("start.missing",elem.getName());
      }
      return legalElementContext(elem);
    }
    if (!strict) {
      ContentModel content=stack.contentModel();
      Vector<Element> elemVec=new Vector<Element>();
      if (content != null) {
        content.getElements(elemVec);
        for (        Element e : elemVec) {
          if (stack.excluded(e.getIndex())) {
            continue;
          }
          boolean reqAtts=false;
          for (AttributeList a=e.getAttributes(); a != null; a=a.next) {
            if (a.modifier == REQUIRED) {
              reqAtts=true;
              break;
            }
          }
          if (reqAtts) {
            continue;
          }
          ContentModel m=e.getContent();
          if (m != null && m.first(elem)) {
            TagElement t=makeTag(e,true);
            legalTagContext(t);
            startTag(t);
            error("start.missing",e.getName());
            return legalElementContext(elem);
          }
        }
      }
    }
    if (stack.terminate() && (stack.elem != dtd.body) && (!strict || stack.elem.omitEnd())) {
      if (!stack.elem.omitEnd()) {
        error("end.missing",elem.getName());
      }
      endTag(true);
      return legalElementContext(elem);
    }
    return false;
  }
  /** 
 * Create a legal context for a tag.
 */
  void legalTagContext(  TagElement tag) throws ChangedCharSetException {
    if (legalElementContext(tag.getElement())) {
      markFirstTime(tag.getElement());
      return;
    }
    if (tag.breaksFlow() && (stack != null) && !stack.tag.breaksFlow()) {
      endTag(true);
      legalTagContext(tag);
      return;
    }
    for (TagStack s=stack; s != null; s=s.next) {
      if (s.tag.getElement() == dtd.head) {
        while (stack != s) {
          endTag(true);
        }
        endTag(true);
        legalTagContext(tag);
        return;
      }
    }
    error("tag.unexpected",tag.getElement().getName());
  }
  /** 
 * Error context. Something went wrong, make sure we are in
 * the document's body context
 */
  void errorContext() throws ChangedCharSetException {
    for (; (stack != null) && (stack.tag.getElement() != dtd.body); stack=stack.next) {
      handleEndTag(stack.tag);
    }
    if (stack == null) {
      legalElementContext(dtd.body);
      startTag(makeTag(dtd.body,true));
    }
  }
  /** 
 * Add a char to the string buffer.
 */
  void addString(  int c){
    if (strpos == str.length) {
      char newstr[]=new char[str.length + 128];
      System.arraycopy(str,0,newstr,0,str.length);
      str=newstr;
    }
    str[strpos++]=(char)c;
  }
  /** 
 * Get the string that's been accumulated.
 */
  String getString(  int pos){
    char newStr[]=new char[strpos - pos];
    System.arraycopy(str,pos,newStr,0,strpos - pos);
    strpos=pos;
    return new String(newStr);
  }
  char[] getChars(  int pos){
    char newStr[]=new char[strpos - pos];
    System.arraycopy(str,pos,newStr,0,strpos - pos);
    strpos=pos;
    return newStr;
  }
  char[] getChars(  int pos,  int endPos){
    char newStr[]=new char[endPos - pos];
    System.arraycopy(str,pos,newStr,0,endPos - pos);
    return newStr;
  }
  void resetStrBuffer(){
    strpos=0;
  }
  int strIndexOf(  char target){
    for (int i=0; i < strpos; i++) {
      if (str[i] == target) {
        return i;
      }
    }
    return -1;
  }
  /** 
 * Skip space.
 * [5] 297:5
 */
  void skipSpace() throws IOException {
    while (true) {
switch (ch) {
case '\n':
        ln++;
      ch=readCh();
    lfCount++;
  break;
case '\r':
ln++;
if ((ch=readCh()) == '\n') {
ch=readCh();
crlfCount++;
}
 else {
crCount++;
}
break;
case ' ':
case '\t':
ch=readCh();
break;
default :
return;
}
}
}
/** 
 * Parse identifier. Uppercase characters are folded
 * to lowercase when lower is true. Returns falsed if
 * no identifier is found. [55] 346:17
 */
boolean parseIdentifier(boolean lower) throws IOException {
switch (ch) {
case 'A':
case 'B':
case 'C':
case 'D':
case 'E':
case 'F':
case 'G':
case 'H':
case 'I':
case 'J':
case 'K':
case 'L':
case 'M':
case 'N':
case 'O':
case 'P':
case 'Q':
case 'R':
case 'S':
case 'T':
case 'U':
case 'V':
case 'W':
case 'X':
case 'Y':
case 'Z':
if (lower) {
ch='a' + (ch - 'A');
}
case 'a':
case 'b':
case 'c':
case 'd':
case 'e':
case 'f':
case 'g':
case 'h':
case 'i':
case 'j':
case 'k':
case 'l':
case 'm':
case 'n':
case 'o':
case 'p':
case 'q':
case 'r':
case 's':
case 't':
case 'u':
case 'v':
case 'w':
case 'x':
case 'y':
case 'z':
break;
default :
return false;
}
while (true) {
addString(ch);
switch (ch=readCh()) {
case 'A':
case 'B':
case 'C':
case 'D':
case 'E':
case 'F':
case 'G':
case 'H':
case 'I':
case 'J':
case 'K':
case 'L':
case 'M':
case 'N':
case 'O':
case 'P':
case 'Q':
case 'R':
case 'S':
case 'T':
case 'U':
case 'V':
case 'W':
case 'X':
case 'Y':
case 'Z':
if (lower) {
ch='a' + (ch - 'A');
}
case 'a':
case 'b':
case 'c':
case 'd':
case 'e':
case 'f':
case 'g':
case 'h':
case 'i':
case 'j':
case 'k':
case 'l':
case 'm':
case 'n':
case 'o':
case 'p':
case 'q':
case 'r':
case 's':
case 't':
case 'u':
case 'v':
case 'w':
case 'x':
case 'y':
case 'z':
case '0':
case '1':
case '2':
case '3':
case '4':
case '5':
case '6':
case '7':
case '8':
case '9':
case '.':
case '-':
case '_':
break;
default :
return true;
}
}
}
/** 
 * Parse an entity reference. [59] 350:17
 */
private char[] parseEntityReference() throws IOException {
int pos=strpos;
if ((ch=readCh()) == '#') {
int n=0;
ch=readCh();
if ((ch >= '0') && (ch <= '9') || ch == 'x' || ch == 'X') {
if ((ch >= '0') && (ch <= '9')) {
while ((ch >= '0') && (ch <= '9')) {
n=(n * 10) + ch - '0';
ch=readCh();
}
}
 else {
ch=readCh();
char lch=(char)Character.toLowerCase(ch);
while ((lch >= '0') && (lch <= '9') || (lch >= 'a') && (lch <= 'f')) {
if (lch >= '0' && lch <= '9') {
n=(n * 16) + lch - '0';
}
 else {
n=(n * 16) + lch - 'a' + 10;
}
ch=readCh();
lch=(char)Character.toLowerCase(ch);
}
}
switch (ch) {
case '\n':
ln++;
ch=readCh();
lfCount++;
break;
case '\r':
ln++;
if ((ch=readCh()) == '\n') {
ch=readCh();
crlfCount++;
}
 else {
crCount++;
}
break;
case ';':
ch=readCh();
break;
}
char data[]={mapNumericReference((char)n)};
return data;
}
addString('#');
if (!parseIdentifier(false)) {
error("ident.expected");
strpos=pos;
char data[]={'&','#'};
return data;
}
}
 else if (!parseIdentifier(false)) {
char data[]={'&'};
return data;
}
boolean semicolon=false;
switch (ch) {
case '\n':
ln++;
ch=readCh();
lfCount++;
break;
case '\r':
ln++;
if ((ch=readCh()) == '\n') {
ch=readCh();
crlfCount++;
}
 else {
crCount++;
}
break;
case ';':
semicolon=true;
ch=readCh();
break;
}
String nm=getString(pos);
Entity ent=dtd.getEntity(nm);
if (!strict && (ent == null)) {
ent=dtd.getEntity(nm.toLowerCase());
}
if ((ent == null) || !ent.isGeneral()) {
if (nm.length() == 0) {
error("invalid.entref",nm);
return new char[0];
}
String str="&" + nm + (semicolon ? ";" : "");
char b[]=new char[str.length()];
str.getChars(0,b.length,b,0);
return b;
}
return ent.getData();
}
/** 
 * Converts numeric character reference to Unicode character.
 * Normally the code in a reference should be always converted
 * to the Unicode character with the same code, but due to
 * wide usage of Cp1252 charset most browsers map numeric references
 * in the range 130-159 (which are control chars in Unicode set)
 * to displayable characters with other codes.
 * @param c the code of numeric character reference.
 * @return the character corresponding to the reference code.
 */
private char mapNumericReference(char c){
if (c < 130 || c > 159) {
return c;
}
return cp1252Map[c - 130];
}
/** 
 * Parse a comment. [92] 391:7
 */
void parseComment() throws IOException {
while (true) {
int c=ch;
switch (c) {
case '-':
if (!strict && (strpos != 0) && (str[strpos - 1] == '-')) {
if ((ch=readCh()) == '>') {
return;
}
if (ch == '!') {
if ((ch=readCh()) == '>') {
return;
}
 else {
addString('-');
addString('!');
continue;
}
}
break;
}
if ((ch=readCh()) == '-') {
ch=readCh();
if (strict || ch == '>') {
return;
}
if (ch == '!') {
if ((ch=readCh()) == '>') {
return;
}
 else {
addString('-');
addString('!');
continue;
}
}
addString('-');
}
break;
case -1:
handleEOFInComment();
return;
case '\n':
ln++;
ch=readCh();
lfCount++;
break;
case '>':
ch=readCh();
break;
case '\r':
ln++;
if ((ch=readCh()) == '\n') {
ch=readCh();
crlfCount++;
}
 else {
crCount++;
}
c='\n';
break;
default :
ch=readCh();
break;
}
addString(c);
}
}
/** 
 * Parse literal content. [46] 343:1 and [47] 344:1
 */
void parseLiteral(boolean replace) throws IOException {
while (true) {
int c=ch;
switch (c) {
case -1:
error("eof.literal",stack.elem.getName());
endTag(true);
return;
case '>':
ch=readCh();
int i=textpos - (stack.elem.name.length() + 2), j=0;
if ((i >= 0) && (text[i++] == '<') && (text[i] == '/')) {
while ((++i < textpos) && (Character.toLowerCase(text[i]) == stack.elem.name.charAt(j++))) ;
if (i == textpos) {
textpos-=(stack.elem.name.length() + 2);
if ((textpos > 0) && (text[textpos - 1] == '\n')) {
textpos--;
}
endTag(false);
return;
}
}
break;
case '&':
char data[]=parseEntityReference();
if (textpos + data.length > text.length) {
char newtext[]=new char[Math.max(textpos + data.length + 128,text.length * 2)];
System.arraycopy(text,0,newtext,0,text.length);
text=newtext;
}
System.arraycopy(data,0,text,textpos,data.length);
textpos+=data.length;
continue;
case '\n':
ln++;
ch=readCh();
lfCount++;
break;
case '\r':
ln++;
if ((ch=readCh()) == '\n') {
ch=readCh();
crlfCount++;
}
 else {
crCount++;
}
c='\n';
break;
default :
ch=readCh();
break;
}
if (textpos == text.length) {
char newtext[]=new char[text.length + 128];
System.arraycopy(text,0,newtext,0,text.length);
text=newtext;
}
text[textpos++]=(char)c;
}
}
/** 
 * Parse attribute value. [33] 331:1
 */
String parseAttributeValue(boolean lower) throws IOException {
int delim=-1;
switch (ch) {
case '\'':
case '"':
delim=ch;
ch=readCh();
break;
}
while (true) {
int c=ch;
switch (c) {
case '\n':
ln++;
ch=readCh();
lfCount++;
if (delim < 0) {
return getString(0);
}
break;
case '\r':
ln++;
if ((ch=readCh()) == '\n') {
ch=readCh();
crlfCount++;
}
 else {
crCount++;
}
if (delim < 0) {
return getString(0);
}
break;
case '\t':
if (delim < 0) c=' ';
case ' ':
ch=readCh();
if (delim < 0) {
return getString(0);
}
break;
case '>':
case '<':
if (delim < 0) {
return getString(0);
}
ch=readCh();
break;
case '\'':
case '"':
ch=readCh();
if (c == delim) {
return getString(0);
}
 else if (delim == -1) {
error("attvalerr");
if (strict || ch == ' ') {
return getString(0);
}
 else {
continue;
}
}
break;
case '=':
if (delim < 0) {
error("attvalerr");
if (strict) {
return getString(0);
}
}
ch=readCh();
break;
case '&':
if (strict && delim < 0) {
ch=readCh();
break;
}
char data[]=parseEntityReference();
for (int i=0; i < data.length; i++) {
c=data[i];
addString((lower && (c >= 'A') && (c <= 'Z')) ? 'a' + c - 'A' : c);
}
continue;
case -1:
return getString(0);
default :
if (lower && (c >= 'A') && (c <= 'Z')) {
c='a' + c - 'A';
}
ch=readCh();
break;
}
addString(c);
}
}
/** 
 * Parse attribute specification List. [31] 327:17
 */
void parseAttributeSpecificationList(Element elem) throws IOException {
while (true) {
skipSpace();
switch (ch) {
case '/':
case '>':
case '<':
case -1:
return;
case '-':
if ((ch=readCh()) == '-') {
ch=readCh();
parseComment();
strpos=0;
}
 else {
error("invalid.tagchar","-",elem.getName());
ch=readCh();
}
continue;
}
AttributeList att;
String attname;
String attvalue;
if (parseIdentifier(true)) {
attname=getString(0);
skipSpace();
if (ch == '=') {
ch=readCh();
skipSpace();
att=elem.getAttribute(attname);
attvalue=parseAttributeValue((att != null) && (att.type != CDATA) && (att.type != NOTATION)&& (att.type != NAME));
}
 else {
attvalue=attname;
att=elem.getAttributeByValue(attvalue);
if (att == null) {
att=elem.getAttribute(attname);
if (att != null) {
attvalue=att.getValue();
}
 else {
attvalue=null;
}
}
}
}
 else if (!strict && ch == ',') {
ch=readCh();
continue;
}
 else if (!strict && ch == '"') {
ch=readCh();
skipSpace();
if (parseIdentifier(true)) {
attname=getString(0);
if (ch == '"') {
ch=readCh();
}
skipSpace();
if (ch == '=') {
ch=readCh();
skipSpace();
att=elem.getAttribute(attname);
attvalue=parseAttributeValue((att != null) && (att.type != CDATA) && (att.type != NOTATION));
}
 else {
attvalue=attname;
att=elem.getAttributeByValue(attvalue);
if (att == null) {
att=elem.getAttribute(attname);
if (att != null) {
attvalue=att.getValue();
}
}
}
}
 else {
char str[]={(char)ch};
error("invalid.tagchar",new String(str),elem.getName());
ch=readCh();
continue;
}
}
 else if (!strict && (attributes.isEmpty()) && (ch == '=')) {
ch=readCh();
skipSpace();
attname=elem.getName();
att=elem.getAttribute(attname);
attvalue=parseAttributeValue((att != null) && (att.type != CDATA) && (att.type != NOTATION));
}
 else if (!strict && (ch == '=')) {
ch=readCh();
skipSpace();
attvalue=parseAttributeValue(true);
error("attvalerr");
return;
}
 else {
char str[]={(char)ch};
error("invalid.tagchar",new String(str),elem.getName());
if (!strict) {
ch=readCh();
continue;
}
 else {
return;
}
}
if (att != null) {
attname=att.getName();
}
 else {
error("invalid.tagatt",attname,elem.getName());
}
if (attributes.isDefined(attname)) {
error("multi.tagatt",attname,elem.getName());
}
if (attvalue == null) {
attvalue=((att != null) && (att.value != null)) ? att.value : HTML.NULL_ATTRIBUTE_VALUE;
}
 else if ((att != null) && (att.values != null) && !att.values.contains(attvalue)) {
error("invalid.tagattval",attname,elem.getName());
}
HTML.Attribute attkey=HTML.getAttributeKey(attname);
if (attkey == null) {
attributes.addAttribute(attname,attvalue);
}
 else {
attributes.addAttribute(attkey,attvalue);
}
}
}
/** 
 * Parses th Document Declaration Type markup declaration.
 * Currently ignores it.
 */
public String parseDTDMarkup() throws IOException {
StringBuilder strBuff=new StringBuilder();
ch=readCh();
while (true) {
switch (ch) {
case '>':
ch=readCh();
return strBuff.toString();
case -1:
error("invalid.markup");
return strBuff.toString();
case '\n':
ln++;
ch=readCh();
lfCount++;
break;
case '"':
ch=readCh();
break;
case '\r':
ln++;
if ((ch=readCh()) == '\n') {
ch=readCh();
crlfCount++;
}
 else {
crCount++;
}
break;
default :
strBuff.append((char)(ch & 0xFF));
ch=readCh();
break;
}
}
}
/** 
 * Parse markup declarations.
 * Currently only handles the Document Type Declaration markup.
 * Returns true if it is a markup declaration false otherwise.
 */
protected boolean parseMarkupDeclarations(StringBuffer strBuff) throws IOException {
if ((strBuff.length() == "DOCTYPE".length()) && (strBuff.toString().toUpperCase().equals("DOCTYPE"))) {
parseDTDMarkup();
return true;
}
return false;
}
/** 
 * Parse an invalid tag.
 */
void parseInvalidTag() throws IOException {
while (true) {
skipSpace();
switch (ch) {
case '>':
case -1:
ch=readCh();
return;
case '<':
return;
default :
ch=readCh();
}
}
}
/** 
 * Parse a start or end tag.
 */
void parseTag() throws IOException {
Element elem;
boolean net=false;
boolean warned=false;
boolean unknown=false;
switch (ch=readCh()) {
case '!':
switch (ch=readCh()) {
case '-':
while (true) {
if (ch == '-') {
if (!strict || ((ch=readCh()) == '-')) {
ch=readCh();
if (!strict && ch == '-') {
ch=readCh();
}
if (textpos != 0) {
char newtext[]=new char[textpos];
System.arraycopy(text,0,newtext,0,textpos);
handleText(newtext);
lastBlockStartPos=currentBlockStartPos;
textpos=0;
}
parseComment();
last=makeTag(dtd.getElement("comment"),true);
handleComment(getChars(0));
continue;
}
 else if (!warned) {
warned=true;
error("invalid.commentchar","-");
}
}
skipSpace();
switch (ch) {
case '-':
continue;
case '>':
ch=readCh();
case -1:
return;
default :
ch=readCh();
if (!warned) {
warned=true;
error("invalid.commentchar",String.valueOf((char)ch));
}
break;
}
}
default :
StringBuffer strBuff=new StringBuffer();
while (true) {
strBuff.append((char)ch);
if (parseMarkupDeclarations(strBuff)) {
return;
}
switch (ch) {
case '>':
ch=readCh();
case -1:
error("invalid.markup");
return;
case '\n':
ln++;
ch=readCh();
lfCount++;
break;
case '\r':
ln++;
if ((ch=readCh()) == '\n') {
ch=readCh();
crlfCount++;
}
 else {
crCount++;
}
break;
default :
ch=readCh();
break;
}
}
}
case '/':
switch (ch=readCh()) {
case '>':
ch=readCh();
case '<':
if (recent == null) {
error("invalid.shortend");
return;
}
elem=recent;
break;
default :
if (!parseIdentifier(true)) {
error("expected.endtagname");
return;
}
skipSpace();
switch (ch) {
case '>':
ch=readCh();
case '<':
break;
default :
error("expected","'>'");
while ((ch != -1) && (ch != '\n') && (ch != '>')) {
ch=readCh();
}
if (ch == '>') {
ch=readCh();
}
break;
}
String elemStr=getString(0);
if (!dtd.elementExists(elemStr)) {
error("end.unrecognized",elemStr);
if ((textpos > 0) && (text[textpos - 1] == '\n')) {
textpos--;
}
elem=dtd.getElement("unknown");
elem.name=elemStr;
unknown=true;
}
 else {
elem=dtd.getElement(elemStr);
}
break;
}
if (stack == null) {
error("end.extra.tag",elem.getName());
return;
}
if ((textpos > 0) && (text[textpos - 1] == '\n')) {
if (stack.pre) {
if ((textpos > 1) && (text[textpos - 2] != '\n')) {
textpos--;
}
}
 else {
textpos--;
}
}
if (unknown) {
TagElement t=makeTag(elem);
handleText(t);
attributes.addAttribute(HTML.Attribute.ENDTAG,"true");
handleEmptyTag(makeTag(elem));
unknown=false;
return;
}
if (!strict) {
String stackElem=stack.elem.getName();
if (stackElem.equals("table")) {
if (!elem.getName().equals(stackElem)) {
error("tag.ignore",elem.getName());
return;
}
}
if (stackElem.equals("tr") || stackElem.equals("td")) {
if ((!elem.getName().equals("table")) && (!elem.getName().equals(stackElem))) {
error("tag.ignore",elem.getName());
return;
}
}
}
TagStack sp=stack;
while ((sp != null) && (elem != sp.elem)) {
sp=sp.next;
}
if (sp == null) {
error("unmatched.endtag",elem.getName());
return;
}
String elemName=elem.getName();
if (stack != sp && (elemName.equals("font") || elemName.equals("center"))) {
if (elemName.equals("center")) {
while (stack.elem.omitEnd() && stack != sp) {
endTag(true);
}
if (stack.elem == elem) {
endTag(false);
}
}
return;
}
while (stack != sp) {
endTag(true);
}
endTag(false);
return;
case -1:
error("eof");
return;
}
if (!parseIdentifier(true)) {
elem=recent;
if ((ch != '>') || (elem == null)) {
error("expected.tagname");
return;
}
}
 else {
String elemStr=getString(0);
if (elemStr.equals("image")) {
elemStr="img";
}
if (!dtd.elementExists(elemStr)) {
error("tag.unrecognized ",elemStr);
elem=dtd.getElement("unknown");
elem.name=elemStr;
unknown=true;
}
 else {
elem=dtd.getElement(elemStr);
}
}
parseAttributeSpecificationList(elem);
switch (ch) {
case '/':
net=true;
case '>':
ch=readCh();
if (ch == '>' && net) {
ch=readCh();
}
case '<':
break;
default :
error("expected","'>'");
break;
}
if (!strict) {
if (elem.getName().equals("script")) {
error("javascript.unsupported");
}
}
if (!elem.isEmpty()) {
if (ch == '\n') {
ln++;
lfCount++;
ch=readCh();
}
 else if (ch == '\r') {
ln++;
if ((ch=readCh()) == '\n') {
ch=readCh();
crlfCount++;
}
 else {
crCount++;
}
}
}
TagElement tag=makeTag(elem,false);
if (!unknown) {
legalTagContext(tag);
if (!strict && skipTag) {
skipTag=false;
return;
}
}
startTag(tag);
if (!elem.isEmpty()) {
switch (elem.getType()) {
case CDATA:
parseLiteral(false);
break;
case RCDATA:
parseLiteral(true);
break;
default :
if (stack != null) {
stack.net=net;
}
break;
}
}
}
private static final String START_COMMENT="<!--";
private static final String END_COMMENT="-->";
private static final char[] SCRIPT_END_TAG="</script>".toCharArray();
private static final char[] SCRIPT_END_TAG_UPPER_CASE="</SCRIPT>".toCharArray();
void parseScript() throws IOException {
char[] charsToAdd=new char[SCRIPT_END_TAG.length];
while (true) {
int i=0;
while (i < SCRIPT_END_TAG.length && (SCRIPT_END_TAG[i] == ch || SCRIPT_END_TAG_UPPER_CASE[i] == ch)) {
charsToAdd[i]=(char)ch;
ch=readCh();
i++;
}
if (i == SCRIPT_END_TAG.length) {
ch=readCh();
return;
}
 else {
for (int j=0; j < i; j++) {
addString(charsToAdd[j]);
}
switch (ch) {
case -1:
error("eof.script");
return;
case '\n':
ln++;
ch=readCh();
lfCount++;
addString('\n');
break;
case '\r':
ln++;
if ((ch=readCh()) == '\n') {
ch=readCh();
crlfCount++;
}
 else {
crCount++;
}
addString('\n');
break;
default :
addString(ch);
ch=readCh();
break;
}
}
}
}
/** 
 * Parse Content. [24] 320:1
 */
void parseContent() throws IOException {
Thread curThread=Thread.currentThread();
for (; ; ) {
if (curThread.isInterrupted()) {
curThread.interrupt();
break;
}
int c=ch;
currentBlockStartPos=currentPosition;
if (recent == dtd.script) {
parseScript();
last=makeTag(dtd.getElement("comment"),true);
String str=new String(getChars(0)).trim();
int minLength=START_COMMENT.length() + END_COMMENT.length();
if (str.startsWith(START_COMMENT) && str.endsWith(END_COMMENT) && str.length() >= (minLength)) {
str=str.substring(START_COMMENT.length(),str.length() - END_COMMENT.length());
}
handleComment(str.toCharArray());
endTag(false);
lastBlockStartPos=currentPosition;
}
 else {
switch (c) {
case '<':
parseTag();
lastBlockStartPos=currentPosition;
continue;
case '/':
ch=readCh();
if ((stack != null) && stack.net) {
endTag(false);
continue;
}
break;
case -1:
return;
case '&':
if (textpos == 0) {
if (!legalElementContext(dtd.pcdata)) {
error("unexpected.pcdata");
}
if (last.breaksFlow()) {
space=false;
}
}
char data[]=parseEntityReference();
if (textpos + data.length + 1 > text.length) {
char newtext[]=new char[Math.max(textpos + data.length + 128,text.length * 2)];
System.arraycopy(text,0,newtext,0,text.length);
text=newtext;
}
if (space) {
space=false;
text[textpos++]=' ';
}
System.arraycopy(data,0,text,textpos,data.length);
textpos+=data.length;
ignoreSpace=false;
continue;
case '\n':
ln++;
lfCount++;
ch=readCh();
if ((stack != null) && stack.pre) {
break;
}
if (textpos == 0) {
lastBlockStartPos=currentPosition;
}
if (!ignoreSpace) {
space=true;
}
continue;
case '\r':
ln++;
c='\n';
if ((ch=readCh()) == '\n') {
ch=readCh();
crlfCount++;
}
 else {
crCount++;
}
if ((stack != null) && stack.pre) {
break;
}
if (textpos == 0) {
lastBlockStartPos=currentPosition;
}
if (!ignoreSpace) {
space=true;
}
continue;
case '\t':
case ' ':
ch=readCh();
if ((stack != null) && stack.pre) {
break;
}
if (textpos == 0) {
lastBlockStartPos=currentPosition;
}
if (!ignoreSpace) {
space=true;
}
continue;
default :
if (textpos == 0) {
if (!legalElementContext(dtd.pcdata)) {
error("unexpected.pcdata");
}
if (last.breaksFlow()) {
space=false;
}
}
ch=readCh();
break;
}
}
if (textpos + 2 > text.length) {
char newtext[]=new char[text.length + 128];
System.arraycopy(text,0,newtext,0,text.length);
text=newtext;
}
if (space) {
if (textpos == 0) {
lastBlockStartPos--;
}
text[textpos++]=' ';
space=false;
}
text[textpos++]=(char)c;
ignoreSpace=false;
}
}
/** 
 * Returns the end of line string. This will return the end of line
 * string that has been encountered the most, one of \r, \n or \r\n.
 */
String getEndOfLineString(){
if (crlfCount >= crCount) {
if (lfCount >= crlfCount) {
return "\n";
}
 else {
return "\r\n";
}
}
 else {
if (crCount > lfCount) {
return "\r";
}
 else {
return "\n";
}
}
}
/** 
 * Parse an HTML stream, given a DTD.
 */
public synchronized void parse(Reader in) throws IOException {
this.in=in;
this.ln=1;
seenHtml=false;
seenHead=false;
seenBody=false;
crCount=lfCount=crlfCount=0;
try {
ch=readCh();
text=new char[1024];
str=new char[128];
parseContent();
while (stack != null) {
endTag(true);
}
in.close();
}
 catch (IOException e) {
errorContext();
error("ioexception");
throw e;
}
catch (Exception e) {
errorContext();
error("exception",e.getClass().getName(),e.getMessage());
e.printStackTrace();
}
catch (ThreadDeath e) {
errorContext();
error("terminated");
e.printStackTrace();
throw e;
}
 finally {
for (; stack != null; stack=stack.next) {
handleEndTag(stack.tag);
}
text=null;
str=null;
}
}
private char buf[]=new char[1];
private int pos;
private int len;
private int currentPosition;
private final int readCh() throws IOException {
if (pos >= len) {
for (; ; ) {
try {
len=in.read(buf);
break;
}
 catch (InterruptedIOException ex) {
throw ex;
}
}
if (len <= 0) {
return -1;
}
pos=0;
}
++currentPosition;
return buf[pos++];
}
protected int getCurrentPos(){
return currentPosition;
}
}
