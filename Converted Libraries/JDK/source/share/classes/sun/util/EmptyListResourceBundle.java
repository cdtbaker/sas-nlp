package sun.util;
import java.util.ListResourceBundle;
public class EmptyListResourceBundle extends ListResourceBundle {
  @Override protected final Object[][] getContents(){
    return new Object[][]{};
  }
}
