import java.io.*;
import java.nio.channels.*;
import java.util.*;
/** 
 * A single-threaded dispatcher.
 * <P>
 * When a SelectionKey is ready, it dispatches the job in this
 * thread.
 * @author Mark Reinhold
 * @author Brad R. Wetmore
 */
class Dispatcher1 implements Dispatcher {
  private Selector sel;
  Dispatcher1() throws IOException {
    sel=Selector.open();
  }
  public void run(){
    for (; ; ) {
      try {
        dispatch();
      }
 catch (      IOException x) {
        x.printStackTrace();
      }
    }
  }
  private void dispatch() throws IOException {
    sel.select();
    for (Iterator i=sel.selectedKeys().iterator(); i.hasNext(); ) {
      SelectionKey sk=(SelectionKey)i.next();
      i.remove();
      Handler h=(Handler)sk.attachment();
      h.handle(sk);
    }
  }
  public void register(  SelectableChannel ch,  int ops,  Handler h) throws IOException {
    ch.register(sel,ops,h);
  }
}
