package sun.text.resources;
import java.util.Locale;
import java.util.ResourceBundle;
import sun.util.EmptyListResourceBundle;
import sun.util.resources.LocaleData;
public class CollationData_zh_HK extends EmptyListResourceBundle {
  public CollationData_zh_HK(){
    ResourceBundle bundle=LocaleData.getCollationData(Locale.TAIWAN);
    setParent(bundle);
  }
}
