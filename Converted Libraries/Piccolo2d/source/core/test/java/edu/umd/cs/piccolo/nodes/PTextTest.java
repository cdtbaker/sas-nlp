package edu.umd.cs.piccolo.nodes;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import junit.framework.TestCase;
import edu.umd.cs.piccolo.MockPropertyChangeListener;
/** 
 * Unit test for PText.
 */
public class PTextTest extends TestCase {
  private PText textNode;
  private MockPropertyChangeListener mockListener;
  public PTextTest(  final String name){
    super(name);
  }
  public void setUp(){
    textNode=new PText();
    mockListener=new MockPropertyChangeListener();
  }
  public void testClone(){
    textNode.setTextPaint(Color.BLUE);
    textNode.setText("Boo");
    final PText clonedNode=(PText)textNode.clone();
    assertEquals("Boo",clonedNode.getText());
    assertEquals(textNode.getFont(),clonedNode.getFont());
    assertEquals(Color.BLUE,clonedNode.getTextPaint());
  }
  public void testTextIsEmptyByDefault(){
    final PText textNode=new PText();
    assertEquals("",textNode.getText());
  }
  public void testTextMayBeAssignedEmptyString(){
    textNode.setText("");
    assertEquals("",textNode.getText());
  }
  public void testTextNullGetsInterpretedAsEmptyString(){
    textNode.setText(null);
    assertEquals("",textNode.getText());
  }
  public void testBoundsGrowWithTextByDefault(){
    final PText text123=new PText("123");
    final double width123=text123.getBounds().getWidth();
    final PText text1234=new PText("1234");
    final double width1234=text1234.getBounds().getWidth();
    assertTrue(width123 < width1234);
  }
  public void testBoundsOfEmptyString(){
    textNode.setText("");
    assertEquals(0,textNode.getBoundsReference().getWidth(),0.000001);
    textNode.setText(null);
    assertEquals(0,textNode.getBoundsReference().getWidth(),0.000001);
  }
  public void testToString(){
    textNode.setText("hello world");
    assertNotNull(textNode.toString());
  }
  public void testJustificationIsLeftByDefault(){
    assertEquals(Component.LEFT_ALIGNMENT,textNode.getJustification(),0.000001);
  }
  public void testSetJustificationPersists(){
    textNode.setJustification(Component.RIGHT_ALIGNMENT);
    assertEquals(Component.RIGHT_ALIGNMENT,textNode.getJustification(),0.000001);
  }
  public void testHorizontalAlignmentIsLeftByDefault(){
    assertEquals(Component.LEFT_ALIGNMENT,textNode.getHorizontalAlignment(),0.000001);
  }
  public void testSetHorizontalAlignmentPersists(){
    textNode.setHorizontalAlignment(Component.RIGHT_ALIGNMENT);
    assertEquals(Component.RIGHT_ALIGNMENT,textNode.getHorizontalAlignment(),0.000001);
  }
  public void testSetHorizontalAlignmentInvalidValues(){
    try {
      textNode.setHorizontalAlignment(-2.0f);
    }
 catch (    final IllegalArgumentException e) {
    }
    try {
      textNode.setHorizontalAlignment(2.0f);
    }
 catch (    final IllegalArgumentException e) {
    }
    try {
      textNode.setHorizontalAlignment(-Float.MAX_VALUE);
    }
 catch (    final IllegalArgumentException e) {
    }
    try {
      textNode.setHorizontalAlignment(Float.MAX_VALUE);
    }
 catch (    final IllegalArgumentException e) {
    }
    try {
      textNode.setHorizontalAlignment(-1.00f);
    }
 catch (    final IllegalArgumentException e) {
    }
    try {
      textNode.setHorizontalAlignment(1.00f);
    }
 catch (    final IllegalArgumentException e) {
    }
  }
  public void testTextPaintIsBlackByDefault(){
    assertEquals(Color.BLACK,textNode.getTextPaint());
  }
  public void testSetTextPaintPersists(){
    textNode.setTextPaint(Color.RED);
    assertEquals(Color.RED,textNode.getTextPaint());
  }
  public void testConstrainWidthToTextTrueByDefault(){
    assertTrue(textNode.isConstrainWidthToTextWidth());
  }
  public void testConstrainHeightToTextTrueByDefault(){
    assertTrue(textNode.isConstrainHeightToTextHeight());
  }
  public void testConstrainWidthPersists(){
    textNode.setConstrainWidthToTextWidth(true);
    assertTrue(textNode.isConstrainWidthToTextWidth());
  }
  public void testConstrainHeightPersists(){
    textNode.setConstrainHeightToTextHeight(true);
    assertTrue(textNode.isConstrainHeightToTextHeight());
  }
  public void testDefaultGreekThreshold(){
    assertEquals(PText.DEFAULT_GREEK_THRESHOLD,textNode.getGreekThreshold(),0.000001);
  }
  public void testSetGreekThreshold(){
    textNode.setGreekThreshold(2);
    assertEquals(2,textNode.getGreekThreshold(),0.000001);
  }
  public void testDefaultFont(){
    assertEquals(PText.DEFAULT_FONT,textNode.getFont());
  }
  public void testSetFontPersists(){
    final Font newFont=new Font("Arial",Font.BOLD,10);
    textNode.setFont(newFont);
    assertEquals(newFont,textNode.getFont());
  }
  public void testSetFontFiresPropertyChangedEvent(){
    textNode.addPropertyChangeListener(PText.PROPERTY_FONT,mockListener);
    final Font newFont=new Font("Arial",Font.BOLD,10);
    textNode.setFont(newFont);
    assertEquals(1,mockListener.getPropertyChangeCount());
    assertEquals(PText.PROPERTY_FONT,mockListener.getPropertyChange(0).getPropertyName());
  }
}
