package javax.sound.sampled;
/** 
 * Instances of classes that implement the <code>LineListener</code> interface can register to
 * receive events when a line's status changes.
 * @author Kara Kytle
 * @see Line
 * @see Line#addLineListener
 * @see Line#removeLineListener
 * @see LineEvent
 * @since 1.3
 */
public interface LineListener extends java.util.EventListener {
  /** 
 * Informs the listener that a line's state has changed.  The listener can then invoke
 * <code>LineEvent</code> methods to obtain information about the event.
 * @param event a line event that describes the change
 */
  public void update(  LineEvent event);
}
