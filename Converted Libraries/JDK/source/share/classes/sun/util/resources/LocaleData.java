package sun.util.resources;
import java.io.File;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import sun.util.LocaleDataMetaInfo;
/** 
 * Provides information about and access to resource bundles in the
 * sun.text.resources and sun.util.resources package.
 * @author Asmus Freytag
 * @author Mark Davis
 */
public class LocaleData {
  private static final String localeDataJarName="localedata.jar";
  /** 
 * Lazy load available locales.
 */
private static class AvailableLocales {
    static final Locale[] localeList=createLocaleList();
  }
  /** 
 * Returns a list of the installed locales. Currently, this simply returns
 * the list of locales for which a sun.text.resources.FormatData bundle
 * exists. This bundle family happens to be the one with the broadest
 * locale coverage in the JRE.
 */
  public static Locale[] getAvailableLocales(){
    return AvailableLocales.localeList.clone();
  }
  /** 
 * Gets a calendar data resource bundle, using privileges
 * to allow accessing a sun.* package.
 */
  public static ResourceBundle getCalendarData(  Locale locale){
    return getBundle("sun.util.resources.CalendarData",locale);
  }
  /** 
 * Gets a currency names resource bundle, using privileges
 * to allow accessing a sun.* package.
 */
  public static OpenListResourceBundle getCurrencyNames(  Locale locale){
    return (OpenListResourceBundle)getBundle("sun.util.resources.CurrencyNames",locale);
  }
  /** 
 * Gets a locale names resource bundle, using privileges
 * to allow accessing a sun.* package.
 */
  public static OpenListResourceBundle getLocaleNames(  Locale locale){
    return (OpenListResourceBundle)getBundle("sun.util.resources.LocaleNames",locale);
  }
  /** 
 * Gets a time zone names resource bundle, using privileges
 * to allow accessing a sun.* package.
 */
  public static OpenListResourceBundle getTimeZoneNames(  Locale locale){
    return (OpenListResourceBundle)getBundle("sun.util.resources.TimeZoneNames",locale);
  }
  /** 
 * Gets a collation data resource bundle, using privileges
 * to allow accessing a sun.* package.
 */
  public static ResourceBundle getCollationData(  Locale locale){
    return getBundle("sun.text.resources.CollationData",locale);
  }
  /** 
 * Gets a date format data resource bundle, using privileges
 * to allow accessing a sun.* package.
 */
  public static ResourceBundle getDateFormatData(  Locale locale){
    return getBundle("sun.text.resources.FormatData",locale);
  }
  /** 
 * Gets a number format data resource bundle, using privileges
 * to allow accessing a sun.* package.
 */
  public static ResourceBundle getNumberFormatData(  Locale locale){
    return getBundle("sun.text.resources.FormatData",locale);
  }
  private static ResourceBundle getBundle(  final String baseName,  final Locale locale){
    return (ResourceBundle)AccessController.doPrivileged(new PrivilegedAction(){
      public Object run(){
        return ResourceBundle.getBundle(baseName,locale,LocaleDataResourceBundleControl.getRBControlInstance());
      }
    }
);
  }
static class LocaleDataResourceBundleControl extends ResourceBundle.Control {
    private static LocaleDataResourceBundleControl rbControlInstance=new LocaleDataResourceBundleControl();
    public static LocaleDataResourceBundleControl getRBControlInstance(){
      return rbControlInstance;
    }
    @Override public List<Locale> getCandidateLocales(    String baseName,    Locale locale){
      List<Locale> candidates=super.getCandidateLocales(baseName,locale);
      String localeString=LocaleDataMetaInfo.getSupportedLocaleString(baseName);
      if (localeString.length() == 0) {
        return candidates;
      }
      for (Iterator<Locale> l=candidates.iterator(); l.hasNext(); ) {
        Locale loc=l.next();
        String lstr=null;
        if (loc.getScript().length() > 0) {
          lstr=loc.toLanguageTag().replace('-','_');
        }
 else {
          lstr=loc.toString();
          int idx=lstr.indexOf("_#");
          if (idx >= 0) {
            lstr=lstr.substring(0,idx);
          }
        }
        if (lstr.length() != 0 && localeString.indexOf(" " + lstr + " ") == -1) {
          l.remove();
        }
      }
      return candidates;
    }
    @Override public Locale getFallbackLocale(    String baseName,    Locale locale){
      if (baseName == null || locale == null) {
        throw new NullPointerException();
      }
      return null;
    }
  }
  private static boolean isNonEuroLangSupported(){
    final String sep=File.separator;
    String localeDataJar=java.security.AccessController.doPrivileged(new sun.security.action.GetPropertyAction("java.home")) + sep + "lib"+ sep+ "ext"+ sep+ localeDataJarName;
    final File f=new File(localeDataJar);
    boolean isNonEuroResJarExist=AccessController.doPrivileged(new PrivilegedAction<Boolean>(){
      public Boolean run(){
        return Boolean.valueOf(f.exists());
      }
    }
).booleanValue();
    return isNonEuroResJarExist;
  }
  private static Locale[] createLocaleList(){
    String supportedLocaleString=LocaleDataMetaInfo.getSupportedLocaleString("sun.text.resources.FormatData");
    if (supportedLocaleString.length() == 0) {
      return null;
    }
    int barIndex=supportedLocaleString.indexOf("|");
    StringTokenizer localeStringTokenizer=null;
    if (isNonEuroLangSupported()) {
      localeStringTokenizer=new StringTokenizer(supportedLocaleString.substring(0,barIndex) + supportedLocaleString.substring(barIndex + 1));
    }
 else {
      localeStringTokenizer=new StringTokenizer(supportedLocaleString.substring(0,barIndex));
    }
    Locale[] locales=new Locale[localeStringTokenizer.countTokens()];
    for (int i=0; i < locales.length; i++) {
      String currentToken=localeStringTokenizer.nextToken().replace('_','-');
      if (currentToken.equals("ja-JP-JP")) {
        currentToken="ja-JP-u-ca-japanese-x-lvariant-JP";
      }
 else       if (currentToken.equals("th-TH-TH")) {
        currentToken="th-TH-u-nu-thai-x-lvariant-TH";
      }
 else       if (currentToken.equals("no-NO-NY")) {
        currentToken="no-NO-x-lvariant-NY";
      }
      locales[i]=Locale.forLanguageTag(currentToken);
    }
    return locales;
  }
}
