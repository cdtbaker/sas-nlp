import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.UIManager;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.MetalTheme;
/** 
 * This class describes a theme using "green" colors.
 * @author Steve Wilson
 * @author Alexander Kouznetsov
 */
@SuppressWarnings("serial") public class MetalThemeMenu extends JMenu implements ActionListener {
  MetalTheme[] themes;
  @SuppressWarnings("LeakingThisInConstructor") public MetalThemeMenu(  String name,  MetalTheme[] themeArray){
    super(name);
    themes=themeArray;
    ButtonGroup group=new ButtonGroup();
    for (int i=0; i < themes.length; i++) {
      JRadioButtonMenuItem item=new JRadioButtonMenuItem(themes[i].getName());
      group.add(item);
      add(item);
      item.setActionCommand(i + "");
      item.addActionListener(this);
      if (i == 0) {
        item.setSelected(true);
      }
    }
  }
  public void actionPerformed(  ActionEvent e){
    String numStr=e.getActionCommand();
    MetalTheme selectedTheme=themes[Integer.parseInt(numStr)];
    MetalLookAndFeel.setCurrentTheme(selectedTheme);
    try {
      UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
    }
 catch (    Exception ex) {
      System.out.println("Failed loading Metal");
      System.out.println(ex);
    }
  }
}
