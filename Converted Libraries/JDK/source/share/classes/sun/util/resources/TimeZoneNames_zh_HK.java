package sun.util.resources;
import java.util.Locale;
import java.util.ResourceBundle;
public final class TimeZoneNames_zh_HK extends TimeZoneNamesBundle {
  public TimeZoneNames_zh_HK(){
    ResourceBundle bundle=LocaleData.getTimeZoneNames(Locale.TAIWAN);
    setParent(bundle);
  }
  protected final Object[][] getContents(){
    return new Object[][]{};
  }
}
