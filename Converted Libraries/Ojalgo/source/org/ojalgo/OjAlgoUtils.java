package org.ojalgo;
import java.util.Date;
import org.ojalgo.machine.Hardware;
import org.ojalgo.machine.VirtualMachine;
import org.ojalgo.netio.BasicLogger;
import org.ojalgo.type.StandardType;
public abstract class OjAlgoUtils {
  /** 
 * This is set for you, but you may want to set it to something different/better.
 * Create a {@linkplain Hardware} instance and then call {@linkplain Hardware#virtualise()}.
 */
  public static VirtualMachine ENVIRONMENT=null;
static {
    final String tmpArchitecture=VirtualMachine.getArchitecture();
    final long tmpMemory=VirtualMachine.getMemory();
    final int tmpThreads=VirtualMachine.getThreads();
    for (    final Hardware tmpHardware : Hardware.PREDEFINED) {
      if (tmpHardware.architecture.equals(tmpArchitecture) && (tmpHardware.threads == tmpThreads) && (tmpHardware.memory >= tmpMemory)) {
        ENVIRONMENT=tmpHardware.virtualise();
      }
    }
    if (ENVIRONMENT == null) {
      BasicLogger.logDebug("ojAlgo includes a small set of predefined hardware profiles,");
      BasicLogger.logDebug("none of which were deemed suitable for the hardware you're currently using.");
      BasicLogger.logDebug("You should set org.ojalgo.OjAlgoUtils.ENVIRONMENT to something that matches the hardware/OS/JVM you're running on.");
      BasicLogger.logDebug("Additionally it would be appreciated if you contribute your hardware profile to ojAlgo.");
      BasicLogger.logDebug("https://lists.sourceforge.net/lists/listinfo/ojalgo-user");
      ENVIRONMENT=Hardware.makeSimple(tmpArchitecture,tmpMemory,tmpThreads).virtualise();
    }
  }
  /** 
 * @see Package#getSpecificationVersion()
 */
  public static String getDate(){
    final String tmpManifestValue=OjAlgoUtils.class.getPackage().getSpecificationVersion();
    return tmpManifestValue != null ? tmpManifestValue : StandardType.SQL_DATE.format(new Date());
  }
  /** 
 * @see Package#getImplementationTitle()
 */
  public static String getTitle(){
    final String tmpManifestValue=OjAlgoUtils.class.getPackage().getImplementationTitle();
    return tmpManifestValue != null ? tmpManifestValue : "ojAlgo";
  }
  /** 
 * @see Package#getImplementationVendor()
 */
  public static String getVendor(){
    final String tmpManifestValue=OjAlgoUtils.class.getPackage().getImplementationVendor();
    return tmpManifestValue != null ? tmpManifestValue : "Optimatika";
  }
  /** 
 * @see Package#getImplementationVersion()
 */
  public static String getVersion(){
    final String tmpManifestValue=OjAlgoUtils.class.getPackage().getImplementationVersion();
    return tmpManifestValue != null ? tmpManifestValue : "X.X";
  }
  private OjAlgoUtils(){
    super();
  }
}
