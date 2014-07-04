package edu.umd.cs.piccolo.examples.pswing;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.InvocationTargetException;
/** 
 * Demonstrates a PSwing problem with dynamic JComponents.
 * <p>
 * This example shows 2 identical JPanels.
 * The panel on the left uses PSwing.
 * The panel on the right uses pure Swing.
 * <p>
 * The JPanel contain various JComponents whose text can be updated by 
 * typing into JTextFields and pressing the "Update" button.
 * The JPanel managed by PSwing is often rendered incorrectly.
 * <p>
 * Please see piccolo2d issue 163 for more information about this problem and solution:
 * http://code.google.com/p/piccolo2d/issues/detail?id=163
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @author Sam Reid
 */
public class PSwingDynamicComponentExample extends JFrame {
  private static final Dimension FRAME_SIZE=new Dimension(800,400);
  private static final int TEXT_FIELD_COLUMNS=30;
  private final ComponentPanel swingPanel, piccoloPanel;
  private final JTextField labelTextField, checkBoxTextField, radioButtonTextField;
  public PSwingDynamicComponentExample(){
    super(PSwingDynamicComponentExample.class.getName());
    setSize(FRAME_SIZE);
    PSwingCanvas canvas=new PSwingCanvas();
    canvas.setBackground(Color.RED);
    canvas.removeInputEventListener(canvas.getZoomEventHandler());
    canvas.removeInputEventListener(canvas.getPanEventHandler());
    piccoloPanel=new ComponentPanel();
    final PSwing pswing=new PSwing(piccoloPanel);
    canvas.getLayer().addChild(pswing);
    pswing.setOffset(10,10);
    swingPanel=new ComponentPanel();
    JPanel jpanel=new JPanel();
    jpanel.setBorder(new LineBorder(Color.BLACK));
    jpanel.add(swingPanel);
    labelTextField=new JTextField(swingPanel.label.getText(),TEXT_FIELD_COLUMNS);
    checkBoxTextField=new JTextField(swingPanel.checkBox.getText(),TEXT_FIELD_COLUMNS);
    radioButtonTextField=new JTextField(swingPanel.radioButton.getText(),TEXT_FIELD_COLUMNS);
    JButton updateButton=new JButton("Update");
    updateButton.addActionListener(new ActionListener(){
      public void actionPerformed(      ActionEvent e){
        updatePanels();
      }
    }
);
    JButton addComponentButton=new JButton("add component");
    addComponentButton.addActionListener(new ActionListener(){
      public void actionPerformed(      ActionEvent e){
        piccoloPanel.addComponent(new JLabel("new"));
        swingPanel.addComponent(new JLabel("new"));
      }
    }
);
    JPanel controlPanel=new JPanel();
    controlPanel.setBorder(new LineBorder(Color.BLACK));
    controlPanel.setLayout(new GridBagLayout());
    GridBagConstraints c=new GridBagConstraints();
    c.gridx=0;
    c.gridy=0;
    c.anchor=GridBagConstraints.EAST;
    controlPanel.add(new JLabel("JLabel text:"),c);
    c.gridx++;
    c.anchor=GridBagConstraints.WEST;
    controlPanel.add(labelTextField,c);
    c.gridx=0;
    c.gridy++;
    c.anchor=GridBagConstraints.EAST;
    controlPanel.add(new JLabel("JCheckBox text:"),c);
    c.gridx++;
    c.anchor=GridBagConstraints.WEST;
    controlPanel.add(checkBoxTextField,c);
    c.gridx=0;
    c.gridy++;
    c.anchor=GridBagConstraints.EAST;
    controlPanel.add(new JLabel("JRadioButton text:"),c);
    c.gridx++;
    c.anchor=GridBagConstraints.WEST;
    controlPanel.add(radioButtonTextField,c);
    c.gridx=1;
    c.gridy++;
    c.anchor=GridBagConstraints.WEST;
    controlPanel.add(updateButton,c);
    c.gridx=1;
    c.gridy++;
    c.anchor=GridBagConstraints.WEST;
    controlPanel.add(addComponentButton,c);
    JPanel mainPanel=new JPanel(new BorderLayout());
    mainPanel.add(canvas,BorderLayout.CENTER);
    mainPanel.add(jpanel,BorderLayout.EAST);
    mainPanel.add(controlPanel,BorderLayout.SOUTH);
    setContentPane(mainPanel);
  }
  private void updatePanels(){
    piccoloPanel.label.setText(labelTextField.getText());
    piccoloPanel.checkBox.setText(checkBoxTextField.getText());
    piccoloPanel.radioButton.setText(radioButtonTextField.getText());
    swingPanel.label.setText(labelTextField.getText());
    swingPanel.checkBox.setText(checkBoxTextField.getText());
    swingPanel.radioButton.setText(radioButtonTextField.getText());
  }
private static class ComponentPanel extends JPanel {
    public final JLabel label;
    public final JCheckBox checkBox;
    public final JRadioButton radioButton;
    public final GridBagConstraints constraints;
    public ComponentPanel(){
      setBorder(new CompoundBorder(new LineBorder(Color.BLACK,1),new EmptyBorder(5,14,5,14)));
      setBackground(new Color(180,205,255));
      label=new JLabel("JLabel");
      checkBox=new JCheckBox("JCheckBox");
      radioButton=new JRadioButton("JRadioButton");
      setLayout(new GridBagLayout());
      constraints=new GridBagConstraints();
      constraints.anchor=GridBagConstraints.WEST;
      constraints.gridx=0;
      constraints.gridy=GridBagConstraints.RELATIVE;
      addComponent(label);
      addComponent(checkBox);
      addComponent(radioButton);
    }
    public void addComponent(    JComponent c){
      add(c,constraints);
      revalidate();
    }
  }
public static class SleepThread extends Thread {
    public SleepThread(    long millis){
      super(new Runnable(){
        public void run(){
          while (true) {
            try {
              SwingUtilities.invokeAndWait(new Runnable(){
                public void run(){
                  try {
                    Thread.sleep(1000);
                  }
 catch (                  InterruptedException e) {
                    e.printStackTrace();
                  }
                }
              }
);
            }
 catch (            InterruptedException e) {
              e.printStackTrace();
            }
catch (            InvocationTargetException e) {
              e.printStackTrace();
            }
          }
        }
      }
);
    }
  }
  public static void main(  String[] args){
    SwingUtilities.invokeLater(new Runnable(){
      public void run(){
        JFrame frame=new PSwingDynamicComponentExample();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
      }
    }
);
  }
}
