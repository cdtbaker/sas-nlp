package java.awt;
/** 
 * The super class of all menu related containers.
 * @author      Arthur van Hoff
 */
public interface MenuContainer {
  Font getFont();
  void remove(  MenuComponent comp);
  /** 
 * @deprecated As of JDK version 1.1
 * replaced by dispatchEvent(AWTEvent).
 */
  @Deprecated boolean postEvent(  Event evt);
}
