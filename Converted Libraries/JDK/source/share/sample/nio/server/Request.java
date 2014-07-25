import java.net.*;
import java.nio.*;
import java.nio.charset.*;
import java.util.regex.*;
/** 
 * An encapsulation of the request received.
 * <P>
 * The static method parse() is responsible for creating this
 * object.
 * @author Mark Reinhold
 * @author Brad R. Wetmore
 */
class Request {
  /** 
 * A helper class for parsing HTTP command actions.
 */
static class Action {
    private String name;
    private Action(    String name){
      this.name=name;
    }
    public String toString(){
      return name;
    }
    static Action GET=new Action("GET");
    static Action PUT=new Action("PUT");
    static Action POST=new Action("POST");
    static Action HEAD=new Action("HEAD");
    static Action parse(    String s){
      if (s.equals("GET"))       return GET;
      if (s.equals("PUT"))       return PUT;
      if (s.equals("POST"))       return POST;
      if (s.equals("HEAD"))       return HEAD;
      throw new IllegalArgumentException(s);
    }
  }
  private Action action;
  private String version;
  private URI uri;
  Action action(){
    return action;
  }
  String version(){
    return version;
  }
  URI uri(){
    return uri;
  }
  private Request(  Action a,  String v,  URI u){
    action=a;
    version=v;
    uri=u;
  }
  public String toString(){
    return (action + " " + version+ " "+ uri);
  }
  static boolean isComplete(  ByteBuffer bb){
    int p=bb.position() - 4;
    if (p < 0)     return false;
    return (((bb.get(p + 0) == '\r') && (bb.get(p + 1) == '\n') && (bb.get(p + 2) == '\r')&& (bb.get(p + 3) == '\n')));
  }
  private static Charset ascii=Charset.forName("US-ASCII");
  private static Pattern requestPattern=Pattern.compile("\\A([A-Z]+) +([^ ]+) +HTTP/([0-9\\.]+)$" + ".*^Host: ([^ ]+)$.*\r\n\r\n\\z",Pattern.MULTILINE | Pattern.DOTALL);
  static Request parse(  ByteBuffer bb) throws MalformedRequestException {
    CharBuffer cb=ascii.decode(bb);
    Matcher m=requestPattern.matcher(cb);
    if (!m.matches())     throw new MalformedRequestException();
    Action a;
    try {
      a=Action.parse(m.group(1));
    }
 catch (    IllegalArgumentException x) {
      throw new MalformedRequestException();
    }
    URI u;
    try {
      u=new URI("http://" + m.group(4) + m.group(2));
    }
 catch (    URISyntaxException x) {
      throw new MalformedRequestException();
    }
    return new Request(a,m.group(3),u);
  }
}
