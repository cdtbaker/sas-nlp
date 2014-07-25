package sun.tools.java;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.FilterReader;
import java.io.UnsupportedEncodingException;
/** 
 * An input stream for java programs. The stream treats either "\n", "\r"
 * or "\r\n" as the end of a line, it always returns \n. It also parses
 * UNICODE characters expressed as \uffff. However, if it sees "\\", the
 * second slash cannot begin a unicode sequence. It keeps track of the current
 * position in the input stream.
 * WARNING: The contents of this source file are not part of any
 * supported API.  Code that depends on them does so at its own risk:
 * they are subject to change or removal without notice.
 * @author      Arthur van Hoff
 */
public class ScannerInputReader extends FilterReader implements Constants {
  Environment env;
  long pos;
  private long chpos;
  private int pushBack=-1;
  public ScannerInputReader(  Environment env,  InputStream in) throws UnsupportedEncodingException {
    super(env.getCharacterEncoding() != null ? new InputStreamReader(in,env.getCharacterEncoding()) : new InputStreamReader(in));
    currentIndex=0;
    numChars=0;
    this.env=env;
    chpos=Scanner.LINEINC;
  }
  private static final int BUFFERLEN=10 * 1024;
  private final char[] buffer=new char[BUFFERLEN];
  private int currentIndex;
  private int numChars;
  /** 
 * Get the next character from our buffer.
 * Note: this method has been inlined by hand in the `read' method
 * below.  Any changes made to this method should be equally applied
 * to that code.
 */
  private int getNextChar() throws IOException {
    if (currentIndex >= numChars) {
      numChars=in.read(buffer);
      if (numChars == -1) {
        return -1;
      }
      currentIndex=0;
    }
    return buffer[currentIndex++];
  }
  public int read(  char[] buffer,  int off,  int len){
    throw new CompilerError("ScannerInputReader is not a fully implemented reader.");
  }
  public int read() throws IOException {
    pos=chpos;
    chpos+=Scanner.OFFSETINC;
    int c=pushBack;
    if (c == -1) {
      getchar:       try {
        if (currentIndex >= numChars) {
          numChars=in.read(buffer);
          if (numChars == -1) {
            c=-1;
            break getchar;
          }
          currentIndex=0;
        }
        c=buffer[currentIndex++];
      }
 catch (      java.io.CharConversionException e) {
        env.error(pos,"invalid.encoding.char");
        return -1;
      }
    }
 else {
      pushBack=-1;
    }
switch (c) {
case -2:
      return '\\';
case '\\':
    if ((c=getNextChar()) != 'u') {
      pushBack=(c == '\\' ? -2 : c);
      return '\\';
    }
  chpos+=Scanner.OFFSETINC;
while ((c=getNextChar()) == 'u') {
  chpos+=Scanner.OFFSETINC;
}
int d=0;
for (int i=0; i < 4; i++, chpos+=Scanner.OFFSETINC, c=getNextChar()) {
switch (c) {
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
d=(d << 4) + c - '0';
break;
case 'a':
case 'b':
case 'c':
case 'd':
case 'e':
case 'f':
d=(d << 4) + 10 + c - 'a';
break;
case 'A':
case 'B':
case 'C':
case 'D':
case 'E':
case 'F':
d=(d << 4) + 10 + c - 'A';
break;
default :
env.error(pos,"invalid.escape.char");
pushBack=c;
return d;
}
}
pushBack=c;
switch (d) {
case '\n':
chpos+=Scanner.LINEINC;
return '\n';
case '\r':
if ((c=getNextChar()) != '\n') {
pushBack=c;
}
 else {
chpos+=Scanner.OFFSETINC;
}
chpos+=Scanner.LINEINC;
return '\n';
default :
return d;
}
case '\n':
chpos+=Scanner.LINEINC;
return '\n';
case '\r':
if ((c=getNextChar()) != '\n') {
pushBack=c;
}
 else {
chpos+=Scanner.OFFSETINC;
}
chpos+=Scanner.LINEINC;
return '\n';
default :
return c;
}
}
}
