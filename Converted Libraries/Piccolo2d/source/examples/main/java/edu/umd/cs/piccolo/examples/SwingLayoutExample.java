package edu.umd.cs.piccolo.examples;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PHtmlView;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.pswing.PSwing;
import edu.umd.cs.piccolox.pswing.PSwingCanvas;
import edu.umd.cs.piccolox.swing.SwingLayoutNode;
import edu.umd.cs.piccolox.swing.SwingLayoutNode.Anchor;
public class SwingLayoutExample {
public static class MyPPath extends PPath {
    public MyPPath(    final Shape shape,    final Color color,    final Stroke stroke,    final Color strokeColor){
      super(shape,stroke);
      setPaint(color);
      setStrokePaint(strokeColor);
    }
  }
  public static void main(  final String[] args){
    final Dimension canvasSize=new Dimension(800,600);
    final PCanvas canvas=new PSwingCanvas();
    canvas.setPreferredSize(canvasSize);
    final PNode rootNode=new PNode();
    canvas.getLayer().addChild(rootNode);
    rootNode.addInputEventListener(new PBasicInputEventHandler(){
      public void mouseDragged(      final PInputEvent event){
        super.mouseDragged(event);
        if (event.isShiftDown()) {
          event.getPickedNode().scale(event.getCanvasDelta().height > 0 ? 0.98 : 1.02);
        }
      }
    }
);
    final BorderLayout borderLayout=new BorderLayout();
    borderLayout.setHgap(10);
    borderLayout.setVgap(5);
    final SwingLayoutNode borderLayoutNode=new SwingLayoutNode(borderLayout);
    borderLayoutNode.addChild(new PText("North"),BorderLayout.NORTH);
    borderLayoutNode.setAnchor(Anchor.CENTER);
    borderLayoutNode.addChild(new PText("South"),BorderLayout.SOUTH);
    borderLayoutNode.setAnchor(Anchor.WEST);
    borderLayoutNode.addChild(new PText("East"),BorderLayout.EAST);
    borderLayoutNode.addChild(new PText("West"),BorderLayout.WEST);
    borderLayoutNode.addChild(new PText("CENTER"),BorderLayout.CENTER);
    borderLayoutNode.setOffset(100,100);
    rootNode.addChild(borderLayoutNode);
    final SwingLayoutNode flowLayoutNode=new SwingLayoutNode(new FlowLayout());
    flowLayoutNode.addChild(new PText("1+1"));
    flowLayoutNode.addChild(new PText("2+2"));
    flowLayoutNode.setOffset(200,200);
    rootNode.addChild(flowLayoutNode);
    final SwingLayoutNode gridBagLayoutNode=new SwingLayoutNode(new GridBagLayout());
    final GridBagConstraints gridBagConstraints=new GridBagConstraints();
    gridBagConstraints.gridx=GridBagConstraints.RELATIVE;
    gridBagLayoutNode.addChild(new PText("FirstNode"),gridBagConstraints);
    gridBagLayoutNode.addChild(new PText("SecondNode"),gridBagConstraints);
    gridBagConstraints.insets=new Insets(50,50,50,50);
    gridBagLayoutNode.addChild(new PText("ThirdNode"),gridBagConstraints);
    gridBagLayoutNode.setOffset(400,250);
    rootNode.addChild(gridBagLayoutNode);
    JPanel container=new JPanel();
    container.setLayout(new BoxLayout(container,BoxLayout.Y_AXIS));
    final SwingLayoutNode boxLayoutNode=new SwingLayoutNode(container);
    boxLayoutNode.addChild(new MyPPath(new Rectangle2D.Double(0,0,50,50),Color.yellow,new BasicStroke(2),Color.red));
    boxLayoutNode.addChild(new MyPPath(new Rectangle2D.Double(0,0,100,50),Color.orange,new BasicStroke(2),Color.blue));
    final SwingLayoutNode innerNode=new SwingLayoutNode();
    innerNode.addChild(new PSwing(new JLabel("foo")));
    innerNode.addChild(new PSwing(new JLabel("bar")));
    boxLayoutNode.addChild(innerNode,Anchor.CENTER);
    boxLayoutNode.setOffset(300,300);
    rootNode.addChild(boxLayoutNode);
    final SwingLayoutNode horizontalLayoutNode=new SwingLayoutNode(new GridBagLayout());
    horizontalLayoutNode.addChild(new PSwing(new JButton("Zero")));
    horizontalLayoutNode.addChild(new PSwing(new JButton("One")));
    horizontalLayoutNode.addChild(new PSwing(new JButton("Two")));
    horizontalLayoutNode.addChild(new PSwing(new JLabel("Three")));
    horizontalLayoutNode.addChild(new PSwing(new JSlider()));
    horizontalLayoutNode.addChild(new PSwing(new JTextField("Four")));
    final PHtmlView htmlNode=new PHtmlView("<html>Five</html>",new JLabel().getFont().deriveFont(15f),Color.blue);
    htmlNode.scale(3);
    horizontalLayoutNode.addChild(htmlNode);
    horizontalLayoutNode.setOffset(100,450);
    rootNode.addChild(horizontalLayoutNode);
    final SwingLayoutNode gridNode=new SwingLayoutNode(new GridBagLayout());
    final GridBagConstraints constraints=new GridBagConstraints();
    constraints.insets=new Insets(10,10,10,10);
    constraints.gridy=0;
    constraints.gridx=0;
    constraints.anchor=GridBagConstraints.EAST;
    final PText dynamicNode=new PText("0");
    gridNode.addChild(dynamicNode,constraints);
    constraints.gridy++;
    gridNode.addChild(new PText("0"),constraints);
    constraints.gridy=0;
    constraints.gridx++;
    constraints.anchor=GridBagConstraints.CENTER;
    final PPath redCircle=new PPath(new Ellipse2D.Double(0,0,25,25));
    redCircle.setPaint(Color.RED);
    gridNode.addChild(redCircle,constraints);
    constraints.gridy++;
    final PPath greenCircle=new PPath(new Ellipse2D.Double(0,0,25,25));
    greenCircle.setPaint(Color.GREEN);
    gridNode.addChild(greenCircle,constraints);
    constraints.gridy=0;
    constraints.gridx++;
    constraints.anchor=GridBagConstraints.WEST;
    gridNode.addChild(new PHtmlView("<html>H<sub>2</sub>O</html>"),constraints);
    constraints.gridy++;
    gridNode.addChild(new PHtmlView("<html>H<sub>3</sub>O<sup>+</sup></html>"),constraints);
    gridNode.scale(2.0);
    gridNode.setOffset(400,50);
    rootNode.addChild(gridNode);
    final JPanel controlPanel=new JPanel();
    controlPanel.setLayout(new BoxLayout(controlPanel,BoxLayout.Y_AXIS));
    final JSlider dynamicSlider=new JSlider(0,1000,0);
    dynamicSlider.setMajorTickSpacing(dynamicSlider.getMaximum());
    dynamicSlider.setPaintTicks(true);
    dynamicSlider.setPaintLabels(true);
    dynamicSlider.addChangeListener(new ChangeListener(){
      public void stateChanged(      final ChangeEvent e){
        dynamicNode.setText(String.valueOf(dynamicSlider.getValue()));
      }
    }
);
    controlPanel.add(dynamicSlider);
    final JPanel appPanel=new JPanel(new BorderLayout());
    appPanel.add(canvas,BorderLayout.CENTER);
    appPanel.add(controlPanel,BorderLayout.EAST);
    final JFrame frame=new JFrame();
    frame.setContentPane(appPanel);
    frame.pack();
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setVisible(true);
  }
}
