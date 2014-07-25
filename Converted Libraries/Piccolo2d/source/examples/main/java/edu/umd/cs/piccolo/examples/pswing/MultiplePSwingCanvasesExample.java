package edu.umd.cs.piccolo.examples.pswing;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;
public class MultiplePSwingCanvasesExample extends JFrame {
  public static void main(  final String[] args){
    JFrame frame=new MultiplePSwingCanvasesExample();
    Container container=frame.getContentPane();
    container.setLayout(new BorderLayout());
    PSwingCanvas canvas1=buildPSwingCanvas("Canvas 1",Color.RED);
    canvas1.setPreferredSize(new Dimension(350,350));
    container.add(canvas1,BorderLayout.WEST);
    PSwingCanvas canvas2=buildPSwingCanvas("Canvas 2",Color.BLUE);
    container.add(canvas2,BorderLayout.EAST);
    canvas2.setPreferredSize(new Dimension(350,350));
    frame.pack();
    frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
    frame.setVisible(true);
  }
  private static PSwingCanvas buildPSwingCanvas(  String canvasName,  Color rectangleColor){
    PSwingCanvas canvas=new PSwingCanvas();
    canvas.setPreferredSize(new Dimension(350,350));
    canvas.getLayer().addChild(new PSwing(new JLabel(canvasName)));
    PSwing rectNode=buildRectangleNode(rectangleColor);
    rectNode.setOffset(100,100);
    canvas.getLayer().addChild(rectNode);
    PSwing buttonNode=buildTestButton();
    buttonNode.setOffset(50,50);
    canvas.getLayer().addChild(buttonNode);
    return canvas;
  }
  private static PSwing buildRectangleNode(  Color rectangleColor){
    JPanel rectPanel=new JPanel();
    rectPanel.setPreferredSize(new Dimension((int)(Math.random() * 50 + 50),(int)(Math.random() * 50 + 50)));
    rectPanel.setBackground(rectangleColor);
    PSwing rect=new PSwing(rectPanel);
    return rect;
  }
  private static PSwing buildTestButton(){
    final JButton button=new JButton("Click Me");
    button.addActionListener(new AbstractAction(){
      public void actionPerformed(      ActionEvent arg0){
        button.setText("Thanks");
      }
    }
);
    return new PSwing(button);
  }
}
