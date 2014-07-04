package javax.swing.colorchooser;
import javax.swing.JComponent;
/** 
 * A class designed to produce preconfigured "accessory" objects to
 * insert into color choosers.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases. The current serialization support is
 * appropriate for short term storage or RMI between applications running
 * the same version of Swing.  As of 1.4, support for long term storage
 * of all JavaBeans<sup><font size="-2">TM</font></sup>
 * has been added to the <code>java.beans</code> package.
 * Please see {@link java.beans.XMLEncoder}.
 * @author Steve Wilson
 */
public class ColorChooserComponentFactory {
  private ColorChooserComponentFactory(){
  }
  public static AbstractColorChooserPanel[] getDefaultChooserPanels(){
    return new AbstractColorChooserPanel[]{new DefaultSwatchChooserPanel(),new ColorChooserPanel(new ColorModelHSV()),new ColorChooserPanel(new ColorModelHSL()),new ColorChooserPanel(new ColorModel()),new ColorChooserPanel(new ColorModelCMYK())};
  }
  public static JComponent getPreviewPanel(){
    return new DefaultPreviewPanel();
  }
}
