package javax.swing;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.lang.ref.WeakReference;
import java.lang.ref.ReferenceQueue;
/** 
 * A package-private PropertyChangeListener which listens for
 * property changes on an Action and updates the properties
 * of an ActionEvent source.
 * <p>
 * Subclasses must override the actionPropertyChanged method,
 * which is invoked from the propertyChange method as long as
 * the target is still valid.
 * </p>
 * <p>
 * WARNING WARNING WARNING WARNING WARNING WARNING:<br>
 * Do NOT create an annonymous inner class that extends this!  If you do
 * a strong reference will be held to the containing class, which in most
 * cases defeats the purpose of this class.
 * @param T the type of JComponent the underlying Action is attached to
 * @author Georges Saab
 * @see AbstractButton
 */
abstract class ActionPropertyChangeListener<T extends JComponent> implements PropertyChangeListener, Serializable {
  private static ReferenceQueue<JComponent> queue;
  private transient OwnedWeakReference<T> target;
  private Action action;
  private static ReferenceQueue<JComponent> getQueue(){
synchronized (ActionPropertyChangeListener.class) {
      if (queue == null) {
        queue=new ReferenceQueue<JComponent>();
      }
    }
    return queue;
  }
  public ActionPropertyChangeListener(  T c,  Action a){
    super();
    setTarget(c);
    this.action=a;
  }
  /** 
 * PropertyChangeListener method.  If the target has been gc'ed this
 * will remove the <code>PropertyChangeListener</code> from the Action,
 * otherwise this will invoke actionPropertyChanged.
 */
  public final void propertyChange(  PropertyChangeEvent e){
    T target=getTarget();
    if (target == null) {
      getAction().removePropertyChangeListener(this);
    }
 else {
      actionPropertyChanged(target,getAction(),e);
    }
  }
  /** 
 * Invoked when a property changes on the Action and the target
 * still exists.
 */
  protected abstract void actionPropertyChanged(  T target,  Action action,  PropertyChangeEvent e);
  private void setTarget(  T c){
    ReferenceQueue<JComponent> queue=getQueue();
    OwnedWeakReference r;
    while ((r=(OwnedWeakReference)queue.poll()) != null) {
      ActionPropertyChangeListener oldPCL=r.getOwner();
      Action oldAction=oldPCL.getAction();
      if (oldAction != null) {
        oldAction.removePropertyChangeListener(oldPCL);
      }
    }
    this.target=new OwnedWeakReference<T>(c,queue,this);
  }
  public T getTarget(){
    if (target == null) {
      return null;
    }
    return this.target.get();
  }
  public Action getAction(){
    return action;
  }
  private void writeObject(  ObjectOutputStream s) throws IOException {
    s.defaultWriteObject();
    s.writeObject(getTarget());
  }
  @SuppressWarnings("unchecked") private void readObject(  ObjectInputStream s) throws IOException, ClassNotFoundException {
    s.defaultReadObject();
    T target=(T)s.readObject();
    if (target != null) {
      setTarget(target);
    }
  }
private static class OwnedWeakReference<U extends JComponent> extends WeakReference<U> {
    private ActionPropertyChangeListener owner;
    OwnedWeakReference(    U target,    ReferenceQueue<? super U> queue,    ActionPropertyChangeListener owner){
      super(target,queue);
      this.owner=owner;
    }
    public ActionPropertyChangeListener getOwner(){
      return owner;
    }
  }
}
