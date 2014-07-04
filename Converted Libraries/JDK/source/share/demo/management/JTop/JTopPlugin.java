import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import com.sun.tools.jconsole.JConsoleContext;
import com.sun.tools.jconsole.JConsoleContext.ConnectionState;
import com.sun.tools.jconsole.JConsolePlugin;
/** 
 * JTopPlugin is a subclass to com.sun.tools.jconsole.JConsolePlugin
 * JTopPlugin is loaded and instantiated by JConsole.  One instance
 * is created for each window that JConsole creates. It listens to
 * the connected property change so that it will update JTop with
 * the valid MBeanServerConnection object.  JTop is a JPanel object
 * displaying the thread and its CPU usage information.
 */
public class JTopPlugin extends JConsolePlugin implements PropertyChangeListener {
  private JTop jtop=null;
  private Map<String,JPanel> tabs=null;
  public JTopPlugin(){
    addContextPropertyChangeListener(this);
  }
  @Override public synchronized Map<String,JPanel> getTabs(){
    if (tabs == null) {
      jtop=new JTop();
      jtop.setMBeanServerConnection(getContext().getMBeanServerConnection());
      tabs=new LinkedHashMap<String,JPanel>();
      tabs.put("JTop",jtop);
    }
    return tabs;
  }
  @Override public SwingWorker<?,?> newSwingWorker(){
    return jtop.newSwingWorker();
  }
  @Override public void propertyChange(  PropertyChangeEvent ev){
    String prop=ev.getPropertyName();
    if (prop == JConsoleContext.CONNECTION_STATE_PROPERTY) {
      ConnectionState newState=(ConnectionState)ev.getNewValue();
      if (newState == ConnectionState.CONNECTED && jtop != null) {
        jtop.setMBeanServerConnection(getContext().getMBeanServerConnection());
      }
    }
  }
}
