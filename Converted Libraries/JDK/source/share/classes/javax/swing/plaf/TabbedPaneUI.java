package javax.swing.plaf;
import java.awt.Rectangle;
import javax.swing.JTabbedPane;
/** 
 * Pluggable look and feel interface for JTabbedPane.
 * @author Dave Moore
 * @author Amy Fowler
 */
public abstract class TabbedPaneUI extends ComponentUI {
  public abstract int tabForCoordinate(  JTabbedPane pane,  int x,  int y);
  public abstract Rectangle getTabBounds(  JTabbedPane pane,  int index);
  public abstract int getTabRunCount(  JTabbedPane pane);
}
