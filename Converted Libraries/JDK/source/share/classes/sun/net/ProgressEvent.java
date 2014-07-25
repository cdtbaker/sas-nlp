package sun.net;
import java.util.EventObject;
import java.net.URL;
/** 
 * ProgressEvent represents an progress event in monitering network input stream.
 * @author Stanley Man-Kit Ho
 */
@SuppressWarnings("serial") public class ProgressEvent extends EventObject {
  private URL url;
  private String contentType;
  private String method;
  private long progress;
  private long expected;
  private ProgressSource.State state;
  /** 
 * Construct a ProgressEvent object.
 */
  public ProgressEvent(  ProgressSource source,  URL url,  String method,  String contentType,  ProgressSource.State state,  long progress,  long expected){
    super(source);
    this.url=url;
    this.method=method;
    this.contentType=contentType;
    this.progress=progress;
    this.expected=expected;
    this.state=state;
  }
  /** 
 * Return URL related to the progress.
 */
  public URL getURL(){
    return url;
  }
  /** 
 * Return method associated with URL.
 */
  public String getMethod(){
    return method;
  }
  /** 
 * Return content type of the URL.
 */
  public String getContentType(){
    return contentType;
  }
  /** 
 * Return current progress value.
 */
  public long getProgress(){
    return progress;
  }
  /** 
 * Return expected maximum progress value; -1 if expected is unknown.
 */
  public long getExpected(){
    return expected;
  }
  /** 
 * Return state.
 */
  public ProgressSource.State getState(){
    return state;
  }
  public String toString(){
    return getClass().getName() + "[url=" + url+ ", method="+ method+ ", state="+ state+ ", content-type="+ contentType+ ", progress="+ progress+ ", expected="+ expected+ "]";
  }
}
