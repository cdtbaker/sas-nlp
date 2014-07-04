package edu.umd.cs.piccolo.examples;
import java.awt.Color;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.nodes.PHtmlView;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.PFrame;
import edu.umd.cs.piccolox.nodes.PStyledText;
/** 
 * Example of text rendering with offset bounds.
 */
public class TextOffsetBoundsExample extends PFrame {
  /** 
 * Default serial version UID. 
 */
  private static final long serialVersionUID=1L;
  public TextOffsetBoundsExample(){
    this(null);
  }
  public TextOffsetBoundsExample(  final PCanvas aCanvas){
    super("TextOffsetBoundsExample",false,aCanvas);
  }
  public void initialize(){
    String text="Lorem ipsum dolor sit amet, consectetur adipiscing elit posuere.";
    PText ptext=new PText(text);
    ptext.setPaint(Color.GRAY);
    ptext.setBounds(0.0d,10.0d,600.0d,40.0d);
    ptext.offset(0.0d,50.0d);
    PHtmlView phtmlView=new PHtmlView(text);
    phtmlView.setPaint(Color.GRAY);
    phtmlView.setBounds(0.0d,10.0d,600.0d,40.0d);
    phtmlView.offset(0.0d,150.0d);
    PStyledText pstyledText=new PStyledText();
    Document document=new DefaultStyledDocument();
    try {
      document.insertString(0,text,null);
    }
 catch (    BadLocationException e) {
    }
    pstyledText.setDocument(document);
    pstyledText.setPaint(Color.GRAY);
    pstyledText.setBounds(0.0d,10.0d,600.0d,40.0d);
    pstyledText.offset(0.0d,250.0d);
    getCanvas().getLayer().addChild(ptext);
    getCanvas().getLayer().addChild(phtmlView);
    getCanvas().getLayer().addChild(pstyledText);
  }
  public static void main(  final String[] args){
    new TextOffsetBoundsExample();
  }
}
