package javax.swing.plaf;
import javax.swing.ComponentInputMap;
import javax.swing.JComponent;
/** 
 * A subclass of javax.swing.ComponentInputMap that implements UIResource.
 * UI classes which provide a ComponentInputMap should use this class.
 * @author Scott Violet
 * @since 1.3
 */
public class ComponentInputMapUIResource extends ComponentInputMap implements UIResource {
  public ComponentInputMapUIResource(  JComponent component){
    super(component);
  }
}
