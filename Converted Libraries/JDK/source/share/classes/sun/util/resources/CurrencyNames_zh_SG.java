package sun.util.resources;
import java.util.Locale;
import java.util.ResourceBundle;
public final class CurrencyNames_zh_SG extends OpenListResourceBundle {
  public CurrencyNames_zh_SG(){
    ResourceBundle bundle=LocaleData.getCurrencyNames(Locale.CHINA);
    setParent(bundle);
  }
  protected final Object[][] getContents(){
    return new Object[][]{{"CNY","CNY"},{"SGD","S$"}};
  }
}
