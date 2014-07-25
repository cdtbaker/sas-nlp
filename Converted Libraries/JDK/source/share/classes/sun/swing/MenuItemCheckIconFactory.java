package sun.swing;
import javax.swing.Icon;
import javax.swing.JMenuItem;
/** 
 * @author Igor Kushnirskiy
 */
public interface MenuItemCheckIconFactory {
  Icon getIcon(  JMenuItem component);
  boolean isCompatible(  Object icon,  String prefix);
}
