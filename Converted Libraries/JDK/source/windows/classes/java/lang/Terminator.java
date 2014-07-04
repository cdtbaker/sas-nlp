package java.lang;
import sun.misc.Signal;
import sun.misc.SignalHandler;
/** 
 * Package-private utility class for setting up and tearing down
 * platform-specific support for termination-triggered shutdowns.
 * @author   Mark Reinhold
 * @since    1.3
 */
class Terminator {
  private static SignalHandler handler=null;
  static void setup(){
    if (handler != null)     return;
    SignalHandler sh=new SignalHandler(){
      public void handle(      Signal sig){
        Shutdown.exit(sig.getNumber() + 0200);
      }
    }
;
    handler=sh;
    try {
      Signal.handle(new Signal("INT"),sh);
      Signal.handle(new Signal("TERM"),sh);
    }
 catch (    IllegalArgumentException e) {
    }
  }
  static void teardown(){
  }
}
