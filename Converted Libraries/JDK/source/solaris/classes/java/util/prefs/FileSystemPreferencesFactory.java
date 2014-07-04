package java.util.prefs;
/** 
 * Factory for FileSystemPreferences.  This class allows FileSystemPreferences
 * to be installed as the Preferences implementations via the
 * java.util.prefs.PreferencesFactory system property.
 * @author  Josh Bloch
 * @see FileSystemPreferences
 * @see Preferences
 * @since   1.4
 */
class FileSystemPreferencesFactory implements PreferencesFactory {
  public Preferences userRoot(){
    return FileSystemPreferences.getUserRoot();
  }
  public Preferences systemRoot(){
    return FileSystemPreferences.getSystemRoot();
  }
}
