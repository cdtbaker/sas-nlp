package javax.swing.plaf.basic;
import javax.swing.*;
import javax.swing.plaf.UIResource;
import java.awt.Container;
import java.awt.Dimension;
/** 
 * The default layout manager for Popup menus and menubars.  This
 * class is an extension of BoxLayout which adds the UIResource tag
 * so that pluggable L&Fs can distinguish it from user-installed
 * layout managers on menus.
 * @author Georges Saab
 */
public class DefaultMenuLayout extends BoxLayout implements UIResource {
  public DefaultMenuLayout(  Container target,  int axis){
    super(target,axis);
  }
  public Dimension preferredLayoutSize(  Container target){
    if (target instanceof JPopupMenu) {
      JPopupMenu popupMenu=(JPopupMenu)target;
      sun.swing.MenuItemLayoutHelper.clearUsedClientProperties(popupMenu);
      if (popupMenu.getComponentCount() == 0) {
        return new Dimension(0,0);
      }
    }
    super.invalidateLayout(target);
    return super.preferredLayoutSize(target);
  }
}
