package sun.nio.ch;
import java.io.IOException;
import java.nio.channels.*;
import java.nio.channels.spi.*;
import java.net.SocketException;
import java.util.*;
import sun.misc.*;
/** 
 * Base Selector implementation class.
 */
abstract class SelectorImpl extends AbstractSelector {
  protected Set<SelectionKey> selectedKeys;
  protected HashSet<SelectionKey> keys;
  private Set<SelectionKey> publicKeys;
  private Set<SelectionKey> publicSelectedKeys;
  protected SelectorImpl(  SelectorProvider sp){
    super(sp);
    keys=new HashSet<SelectionKey>();
    selectedKeys=new HashSet<SelectionKey>();
    if (Util.atBugLevel("1.4")) {
      publicKeys=keys;
      publicSelectedKeys=selectedKeys;
    }
 else {
      publicKeys=Collections.unmodifiableSet(keys);
      publicSelectedKeys=Util.ungrowableSet(selectedKeys);
    }
  }
  public Set<SelectionKey> keys(){
    if (!isOpen() && !Util.atBugLevel("1.4"))     throw new ClosedSelectorException();
    return publicKeys;
  }
  public Set<SelectionKey> selectedKeys(){
    if (!isOpen() && !Util.atBugLevel("1.4"))     throw new ClosedSelectorException();
    return publicSelectedKeys;
  }
  protected abstract int doSelect(  long timeout) throws IOException ;
  private int lockAndDoSelect(  long timeout) throws IOException {
synchronized (this) {
      if (!isOpen())       throw new ClosedSelectorException();
synchronized (publicKeys) {
synchronized (publicSelectedKeys) {
          return doSelect(timeout);
        }
      }
    }
  }
  public int select(  long timeout) throws IOException {
    if (timeout < 0)     throw new IllegalArgumentException("Negative timeout");
    return lockAndDoSelect((timeout == 0) ? -1 : timeout);
  }
  public int select() throws IOException {
    return select(0);
  }
  public int selectNow() throws IOException {
    return lockAndDoSelect(0);
  }
  public void implCloseSelector() throws IOException {
    wakeup();
synchronized (this) {
synchronized (publicKeys) {
synchronized (publicSelectedKeys) {
          implClose();
        }
      }
    }
  }
  protected abstract void implClose() throws IOException ;
  void putEventOps(  SelectionKeyImpl sk,  int ops){
  }
  protected final SelectionKey register(  AbstractSelectableChannel ch,  int ops,  Object attachment){
    if (!(ch instanceof SelChImpl))     throw new IllegalSelectorException();
    SelectionKeyImpl k=new SelectionKeyImpl((SelChImpl)ch,this);
    k.attach(attachment);
synchronized (publicKeys) {
      implRegister(k);
    }
    k.interestOps(ops);
    return k;
  }
  protected abstract void implRegister(  SelectionKeyImpl ski);
  void processDeregisterQueue() throws IOException {
    Set cks=cancelledKeys();
synchronized (cks) {
      if (!cks.isEmpty()) {
        Iterator i=cks.iterator();
        while (i.hasNext()) {
          SelectionKeyImpl ski=(SelectionKeyImpl)i.next();
          try {
            implDereg(ski);
          }
 catch (          SocketException se) {
            IOException ioe=new IOException("Error deregistering key");
            ioe.initCause(se);
            throw ioe;
          }
 finally {
            i.remove();
          }
        }
      }
    }
  }
  protected abstract void implDereg(  SelectionKeyImpl ski) throws IOException ;
  abstract public Selector wakeup();
}
