package sun.swing.icon;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.io.Serializable;
import javax.swing.Icon;
import javax.swing.UIManager;
import javax.swing.plaf.UIResource;
/** 
 * Sorting icon.
 */
public class SortArrowIcon implements Icon, UIResource, Serializable {
  private static final int ARROW_HEIGHT=5;
  private static final int X_PADDING=7;
  private boolean ascending;
  private Color color;
  private String colorKey;
  /** 
 * Creates a <code>SortArrowIcon</code>.
 * @param ascending if true, icon respresenting ascending sort, otherwise
 * descending
 * @param color the color to render the icon
 */
  public SortArrowIcon(  boolean ascending,  Color color){
    this.ascending=ascending;
    this.color=color;
    if (color == null) {
      throw new IllegalArgumentException();
    }
  }
  /** 
 * Creates a <code>SortArrowIcon</code>.
 * @param ascending if true, icon respresenting ascending sort, otherwise
 * descending
 * @param colorKey the key used to find color in UIManager
 */
  public SortArrowIcon(  boolean ascending,  String colorKey){
    this.ascending=ascending;
    this.colorKey=colorKey;
    if (colorKey == null) {
      throw new IllegalArgumentException();
    }
  }
  public void paintIcon(  Component c,  Graphics g,  int x,  int y){
    g.setColor(getColor());
    int startX=X_PADDING + x + ARROW_HEIGHT / 2;
    if (ascending) {
      int startY=y;
      g.fillRect(startX,startY,1,1);
      for (int line=1; line < ARROW_HEIGHT; line++) {
        g.fillRect(startX - line,startY + line,line + line + 1,1);
      }
    }
 else {
      int startY=y + ARROW_HEIGHT - 1;
      g.fillRect(startX,startY,1,1);
      for (int line=1; line < ARROW_HEIGHT; line++) {
        g.fillRect(startX - line,startY - line,line + line + 1,1);
      }
    }
  }
  public int getIconWidth(){
    return X_PADDING + ARROW_HEIGHT * 2;
  }
  public int getIconHeight(){
    return ARROW_HEIGHT + 2;
  }
  private Color getColor(){
    if (color != null) {
      return color;
    }
    return UIManager.getColor(colorKey);
  }
}
