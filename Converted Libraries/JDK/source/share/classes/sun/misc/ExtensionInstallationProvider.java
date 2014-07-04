package sun.misc;
/** 
 * This interface defines the contract a extension installation capable
 * provided to the extension installation dependency mechanism to
 * install new extensions on the user's disk
 * @author  Jerome Dochez
 */
public interface ExtensionInstallationProvider {
  boolean installExtension(  ExtensionInfo requestExtInfo,  ExtensionInfo installedExtInfo) throws ExtensionInstallationException ;
}
