package edu.umd.cs.piccolox.swt;
import java.awt.event.ActionListener;
import javax.swing.Timer;
import org.eclipse.swt.widgets.Composite;
import edu.umd.cs.piccolo.PRoot;
/** 
 * <b>PSWTRoot</b> is a subclass of PRoot that is designed to work in the SWT
 * environment. In particular it uses SWTTimers and the SWT event dispatch
 * thread. With the current setup only a single PSWTCanvas is expected to be
 * connected to a root.
 * @version 1.1
 * @author Jesse Grosjean
 */
public class PSWTRoot extends PRoot {
  private static final long serialVersionUID=1L;
  private final Composite composite;
  /** 
 * Constructs a PSWTRoot attached to the provided composite.
 * @param composite composite PSWTRoot is responsible for
 */
  public PSWTRoot(  final Composite composite){
    this.composite=composite;
  }
  /** 
 * Creates a timer that will fire the listener every delay milliseconds.
 * @param delay time in milliseconds between firings of listener
 * @param listener listener to be fired
 * @return the created timer
 */
  public Timer createTimer(  final int delay,  final ActionListener listener){
    return new SWTTimer(composite.getDisplay(),delay,listener);
  }
  /** 
 * Processes Inputs if any kind of IO needs to be done.
 */
  public void scheduleProcessInputsIfNeeded(){
    if (!Thread.currentThread().equals(composite.getDisplay().getThread())) {
      return;
    }
    if (!processInputsScheduled && !processingInputs && (getFullBoundsInvalid() || getChildBoundsInvalid() || getPaintInvalid()|| getChildPaintInvalid())) {
      processInputsScheduled=true;
      composite.getDisplay().asyncExec(new Runnable(){
        public void run(){
          processInputs();
          processInputsScheduled=false;
        }
      }
);
    }
  }
}
