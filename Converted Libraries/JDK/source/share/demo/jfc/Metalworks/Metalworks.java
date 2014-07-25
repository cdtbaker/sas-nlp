import java.awt.Toolkit;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.metal.MetalLookAndFeel;
/** 
 * This application is a demo of the Metal Look & Feel
 * @author Steve Wilson
 * @author Alexander Kouznetsov
 */
public class Metalworks {
  public static void main(  String[] args){
    UIManager.put("swing.boldMetal",Boolean.FALSE);
    JDialog.setDefaultLookAndFeelDecorated(true);
    JFrame.setDefaultLookAndFeelDecorated(true);
    Toolkit.getDefaultToolkit().setDynamicLayout(true);
    System.setProperty("sun.awt.noerasebackground","true");
    try {
      UIManager.setLookAndFeel(new MetalLookAndFeel());
    }
 catch (    UnsupportedLookAndFeelException e) {
      System.out.println("Metal Look & Feel not supported on this platform. \n" + "Program Terminated");
      System.exit(0);
    }
    JFrame frame=new MetalworksFrame();
    frame.setVisible(true);
  }
}
