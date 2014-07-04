package sun.java2d.xr;
/** 
 * UInt32 "emulation", mimics the behaviour of xcb's request counter.
 * In order to be compatible with xcb we have to wrap exactly when xcb would do.
 * @author Clemens Eisserer
 */
public class XcbRequestCounter {
  private final static long MAX_UINT=4294967295L;
  long value;
  public XcbRequestCounter(  long value){
    this.value=value;
  }
  public void setValue(  long value){
    this.value=value;
  }
  public long getValue(){
    return value;
  }
  public void add(  long v){
    value+=v;
    if (value > MAX_UINT) {
      value=0;
    }
  }
}
