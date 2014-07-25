import java.io.*;
import java.nio.*;
import java.nio.charset.*;
/** 
 * A Content type that provides for transferring Strings.
 * @author Mark Reinhold
 * @author Brad R. Wetmore
 */
class StringContent implements Content {
  private static Charset ascii=Charset.forName("US-ASCII");
  private String type;
  private String content;
  StringContent(  CharSequence c,  String t){
    content=c.toString();
    if (!content.endsWith("\n"))     content+="\n";
    type=t + "; charset=iso-8859-1";
  }
  StringContent(  CharSequence c){
    this(c,"text/plain");
  }
  StringContent(  Exception x){
    StringWriter sw=new StringWriter();
    x.printStackTrace(new PrintWriter(sw));
    type="text/plain; charset=iso-8859-1";
    content=sw.toString();
  }
  public String type(){
    return type;
  }
  private ByteBuffer bb=null;
  private void encode(){
    if (bb == null)     bb=ascii.encode(CharBuffer.wrap(content));
  }
  public long length(){
    encode();
    return bb.remaining();
  }
  public void prepare(){
    encode();
    bb.rewind();
  }
  public boolean send(  ChannelIO cio) throws IOException {
    if (bb == null)     throw new IllegalStateException();
    cio.write(bb);
    return bb.hasRemaining();
  }
  public void release() throws IOException {
  }
}
