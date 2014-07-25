package sun.jvmstat.perfdata.monitor;
import java.net.*;
import java.io.*;
import java.util.*;
import java.util.regex.*;
/** 
 * Class for encapsulating syntax exceptions generated by AliasFileParser.
 * @author Brian Doherty
 * @since 1.5
 */
public class SyntaxException extends Exception {
  int lineno;
  public SyntaxException(  int lineno){
    this.lineno=lineno;
  }
  public String getMessage(){
    return "syntax error at line " + lineno;
  }
}
