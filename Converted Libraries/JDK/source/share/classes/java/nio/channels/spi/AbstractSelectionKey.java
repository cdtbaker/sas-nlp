package java.nio.channels.spi;
import java.nio.channels.*;
/** 
 * Base implementation class for selection keys.
 * <p> This class tracks the validity of the key and implements cancellation.
 * @author Mark Reinhold
 * @author JSR-51 Expert Group
 * @since 1.4
 */
public abstract class AbstractSelectionKey extends SelectionKey {
  /** 
 * Initializes a new instance of this class.  </p>
 */
  protected AbstractSelectionKey(){
  }
  private volatile boolean valid=true;
  public final boolean isValid(){
    return valid;
  }
  void invalidate(){
    valid=false;
  }
  /** 
 * Cancels this key.
 * <p> If this key has not yet been cancelled then it is added to its
 * selector's cancelled-key set while synchronized on that set.  </p>
 */
  public final void cancel(){
synchronized (this) {
      if (valid) {
        valid=false;
        ((AbstractSelector)selector()).cancel(this);
      }
    }
  }
}
