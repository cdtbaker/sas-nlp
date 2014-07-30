package sun.security.jgss;
import sun.net.www.protocol.http.HttpCallerInfo;
/** 
 * A special kind of GSSCaller, which origins from HTTP/Negotiate and contains
 * info about what triggers the JGSS calls.
 */
public class HttpCaller extends GSSCaller {
  final private HttpCallerInfo hci;
  public HttpCaller(  HttpCallerInfo hci){
    this.hci=hci;
  }
  public HttpCallerInfo info(){
    return hci;
  }
}