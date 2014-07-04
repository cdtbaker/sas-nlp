package sun.util.resources;
import java.util.Locale;
import java.util.ResourceBundle;
public final class LocaleNames_zh_HK extends OpenListResourceBundle {
  public LocaleNames_zh_HK(){
    ResourceBundle bundle=LocaleData.getLocaleNames(Locale.TAIWAN);
    setParent(bundle);
  }
  protected final Object[][] getContents(){
    return new Object[][]{};
  }
}
