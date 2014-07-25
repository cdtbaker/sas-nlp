package javax.management.relation;
import static com.sun.jmx.mbeanserver.Util.cast;
import static com.sun.jmx.defaults.JmxProperties.RELATION_LOGGER;
import com.sun.jmx.mbeanserver.GetPropertyAction;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.security.AccessController;
import java.util.List;
import java.util.Vector;
import javax.management.MBeanServerNotification;
import javax.management.Notification;
import javax.management.NotificationFilterSupport;
import javax.management.ObjectName;
import java.util.List;
import java.util.logging.Level;
import java.util.Vector;
/** 
 * Filter for {@link MBeanServerNotification}.
 * This filter filters MBeanServerNotification notifications by
 * selecting the ObjectNames of interest and the operations (registration,
 * unregistration, both) of interest (corresponding to notification
 * types).
 * <p>The <b>serialVersionUID</b> of this class is <code>2605900539589789736L</code>.
 * @since 1.5
 */
@SuppressWarnings("serial") public class MBeanServerNotificationFilter extends NotificationFilterSupport {
  private static final long oldSerialVersionUID=6001782699077323605L;
  private static final long newSerialVersionUID=2605900539589789736L;
  private static final ObjectStreamField[] oldSerialPersistentFields={new ObjectStreamField("mySelectObjNameList",Vector.class),new ObjectStreamField("myDeselectObjNameList",Vector.class)};
  private static final ObjectStreamField[] newSerialPersistentFields={new ObjectStreamField("selectedNames",List.class),new ObjectStreamField("deselectedNames",List.class)};
  private static final long serialVersionUID;
  /** 
 * @serialField selectedNames List List of {@link ObjectName}s of interest
 * <ul>
 * <li><code>null</code> means that all {@link ObjectName}s are implicitly selected
 * (check for explicit deselections)</li>
 * <li>Empty vector means that no {@link ObjectName} is explicitly selected</li>
 * </ul>
 * @serialField deselectedNames List List of {@link ObjectName}s with no interest
 * <ul>
 * <li><code>null</code> means that all {@link ObjectName}s are implicitly deselected
 * (check for explicit selections))</li>
 * <li>Empty vector means that no {@link ObjectName} is explicitly deselected</li>
 * </ul>
 */
  private static final ObjectStreamField[] serialPersistentFields;
  private static boolean compat=false;
static {
    try {
      GetPropertyAction act=new GetPropertyAction("jmx.serial.form");
      String form=AccessController.doPrivileged(act);
      compat=(form != null && form.equals("1.0"));
    }
 catch (    Exception e) {
    }
    if (compat) {
      serialPersistentFields=oldSerialPersistentFields;
      serialVersionUID=oldSerialVersionUID;
    }
 else {
      serialPersistentFields=newSerialPersistentFields;
      serialVersionUID=newSerialVersionUID;
    }
  }
  /** 
 * @serial List of {@link ObjectName}s of interest
 * <ul>
 * <li><code>null</code> means that all {@link ObjectName}s are implicitly selected
 * (check for explicit deselections)</li>
 * <li>Empty vector means that no {@link ObjectName} is explicitly selected</li>
 * </ul>
 */
  private List<ObjectName> selectedNames=new Vector<ObjectName>();
  /** 
 * @serial List of {@link ObjectName}s with no interest
 * <ul>
 * <li><code>null</code> means that all {@link ObjectName}s are implicitly deselected
 * (check for explicit selections))</li>
 * <li>Empty vector means that no {@link ObjectName} is explicitly deselected</li>
 * </ul>
 */
  private List<ObjectName> deselectedNames=null;
  /** 
 * Creates a filter selecting all MBeanServerNotification notifications for
 * all ObjectNames.
 */
  public MBeanServerNotificationFilter(){
    super();
    RELATION_LOGGER.entering(MBeanServerNotificationFilter.class.getName(),"MBeanServerNotificationFilter");
    enableType(MBeanServerNotification.REGISTRATION_NOTIFICATION);
    enableType(MBeanServerNotification.UNREGISTRATION_NOTIFICATION);
    RELATION_LOGGER.exiting(MBeanServerNotificationFilter.class.getName(),"MBeanServerNotificationFilter");
    return;
  }
  /** 
 * Disables any MBeanServerNotification (all ObjectNames are
 * deselected).
 */
  public synchronized void disableAllObjectNames(){
    RELATION_LOGGER.entering(MBeanServerNotificationFilter.class.getName(),"disableAllObjectNames");
    selectedNames=new Vector<ObjectName>();
    deselectedNames=null;
    RELATION_LOGGER.exiting(MBeanServerNotificationFilter.class.getName(),"disableAllObjectNames");
    return;
  }
  /** 
 * Disables MBeanServerNotifications concerning given ObjectName.
 * @param objectName  ObjectName no longer of interest
 * @exception IllegalArgumentException  if the given ObjectName is null
 */
  public synchronized void disableObjectName(  ObjectName objectName) throws IllegalArgumentException {
    if (objectName == null) {
      String excMsg="Invalid parameter.";
      throw new IllegalArgumentException(excMsg);
    }
    RELATION_LOGGER.entering(MBeanServerNotificationFilter.class.getName(),"disableObjectName",objectName);
    if (selectedNames != null) {
      if (selectedNames.size() != 0) {
        selectedNames.remove(objectName);
      }
    }
    if (deselectedNames != null) {
      if (!(deselectedNames.contains(objectName))) {
        deselectedNames.add(objectName);
      }
    }
    RELATION_LOGGER.exiting(MBeanServerNotificationFilter.class.getName(),"disableObjectName");
    return;
  }
  /** 
 * Enables all MBeanServerNotifications (all ObjectNames are selected).
 */
  public synchronized void enableAllObjectNames(){
    RELATION_LOGGER.entering(MBeanServerNotificationFilter.class.getName(),"enableAllObjectNames");
    selectedNames=null;
    deselectedNames=new Vector<ObjectName>();
    RELATION_LOGGER.exiting(MBeanServerNotificationFilter.class.getName(),"enableAllObjectNames");
    return;
  }
  /** 
 * Enables MBeanServerNotifications concerning given ObjectName.
 * @param objectName  ObjectName of interest
 * @exception IllegalArgumentException  if the given ObjectName is null
 */
  public synchronized void enableObjectName(  ObjectName objectName) throws IllegalArgumentException {
    if (objectName == null) {
      String excMsg="Invalid parameter.";
      throw new IllegalArgumentException(excMsg);
    }
    RELATION_LOGGER.entering(MBeanServerNotificationFilter.class.getName(),"enableObjectName",objectName);
    if (deselectedNames != null) {
      if (deselectedNames.size() != 0) {
        deselectedNames.remove(objectName);
      }
    }
    if (selectedNames != null) {
      if (!(selectedNames.contains(objectName))) {
        selectedNames.add(objectName);
      }
    }
    RELATION_LOGGER.exiting(MBeanServerNotificationFilter.class.getName(),"enableObjectName");
    return;
  }
  /** 
 * Gets all the ObjectNames enabled.
 * @return Vector of ObjectNames:
 * <P>- null means all ObjectNames are implicitly selected, except the
 * ObjectNames explicitly deselected
 * <P>- empty means all ObjectNames are deselected, i.e. no ObjectName
 * selected.
 */
  public synchronized Vector<ObjectName> getEnabledObjectNames(){
    if (selectedNames != null) {
      return new Vector<ObjectName>(selectedNames);
    }
 else {
      return null;
    }
  }
  /** 
 * Gets all the ObjectNames disabled.
 * @return Vector of ObjectNames:
 * <P>- null means all ObjectNames are implicitly deselected, except the
 * ObjectNames explicitly selected
 * <P>- empty means all ObjectNames are selected, i.e. no ObjectName
 * deselected.
 */
  public synchronized Vector<ObjectName> getDisabledObjectNames(){
    if (deselectedNames != null) {
      return new Vector<ObjectName>(deselectedNames);
    }
 else {
      return null;
    }
  }
  /** 
 * Invoked before sending the specified notification to the listener.
 * <P>If:
 * <P>- the ObjectName of the concerned MBean is selected (explicitly OR
 * (implicitly and not explicitly deselected))
 * <P>AND
 * <P>- the type of the operation (registration or unregistration) is
 * selected
 * <P>then the notification is sent to the listener.
 * @param notif  The notification to be sent.
 * @return true if the notification has to be sent to the listener, false
 * otherwise.
 * @exception IllegalArgumentException  if null parameter
 */
  public synchronized boolean isNotificationEnabled(  Notification notif) throws IllegalArgumentException {
    if (notif == null) {
      String excMsg="Invalid parameter.";
      throw new IllegalArgumentException(excMsg);
    }
    RELATION_LOGGER.entering(MBeanServerNotificationFilter.class.getName(),"isNotificationEnabled",notif);
    String ntfType=notif.getType();
    Vector<String> enabledTypes=getEnabledTypes();
    if (!(enabledTypes.contains(ntfType))) {
      RELATION_LOGGER.logp(Level.FINER,MBeanServerNotificationFilter.class.getName(),"isNotificationEnabled","Type not selected, exiting");
      return false;
    }
    MBeanServerNotification mbsNtf=(MBeanServerNotification)notif;
    ObjectName objName=mbsNtf.getMBeanName();
    boolean isSelectedFlg=false;
    if (selectedNames != null) {
      if (selectedNames.size() == 0) {
        RELATION_LOGGER.logp(Level.FINER,MBeanServerNotificationFilter.class.getName(),"isNotificationEnabled","No ObjectNames selected, exiting");
        return false;
      }
      isSelectedFlg=selectedNames.contains(objName);
      if (!isSelectedFlg) {
        RELATION_LOGGER.logp(Level.FINER,MBeanServerNotificationFilter.class.getName(),"isNotificationEnabled","ObjectName not in selected list, exiting");
        return false;
      }
    }
    if (!isSelectedFlg) {
      if (deselectedNames == null) {
        RELATION_LOGGER.logp(Level.FINER,MBeanServerNotificationFilter.class.getName(),"isNotificationEnabled","ObjectName not selected, and all " + "names deselected, exiting");
        return false;
      }
 else       if (deselectedNames.contains(objName)) {
        RELATION_LOGGER.logp(Level.FINER,MBeanServerNotificationFilter.class.getName(),"isNotificationEnabled","ObjectName explicitly not selected, exiting");
        return false;
      }
    }
    RELATION_LOGGER.logp(Level.FINER,MBeanServerNotificationFilter.class.getName(),"isNotificationEnabled","ObjectName selected, exiting");
    return true;
  }
  /** 
 * Deserializes an {@link MBeanServerNotificationFilter} from an {@link ObjectInputStream}.
 */
  private void readObject(  ObjectInputStream in) throws IOException, ClassNotFoundException {
    if (compat) {
      ObjectInputStream.GetField fields=in.readFields();
      selectedNames=cast(fields.get("mySelectObjNameList",null));
      if (fields.defaulted("mySelectObjNameList")) {
        throw new NullPointerException("mySelectObjNameList");
      }
      deselectedNames=cast(fields.get("myDeselectObjNameList",null));
      if (fields.defaulted("myDeselectObjNameList")) {
        throw new NullPointerException("myDeselectObjNameList");
      }
    }
 else {
      in.defaultReadObject();
    }
  }
  /** 
 * Serializes an {@link MBeanServerNotificationFilter} to an {@link ObjectOutputStream}.
 */
  private void writeObject(  ObjectOutputStream out) throws IOException {
    if (compat) {
      ObjectOutputStream.PutField fields=out.putFields();
      fields.put("mySelectObjNameList",selectedNames);
      fields.put("myDeselectObjNameList",deselectedNames);
      out.writeFields();
    }
 else {
      out.defaultWriteObject();
    }
  }
}
