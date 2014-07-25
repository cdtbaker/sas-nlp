package sun.awt.X11;
public interface XMSelectionListener {
  public void ownerChanged(  int screen,  XMSelection sel,  long newOwner,  long data,  long timestamp);
  public void ownerDeath(  int screen,  XMSelection sel,  long deadOwner);
  public void selectionChanged(  int screen,  XMSelection sel,  long owner,  XPropertyEvent event);
}
