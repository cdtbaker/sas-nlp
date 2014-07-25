package edu.umd.cs.piccolo.examples;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PActivity;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolox.PFrame;
/** 
 * This example demonstrates the difference between
 * <code>offset(double, double)</code> and
 * <code>translate(double, double)</code>.
 * @see PNode#offset(double,double)
 * @see PNode#translate(double,double)
 */
public class OffsetVsTranslateExample extends PFrame {
  /** 
 * Default serial version UID. 
 */
  private static final long serialVersionUID=1L;
  /** 
 * Create a new offset vs. translate example.
 */
  public OffsetVsTranslateExample(){
    this(null);
  }
  /** 
 * Create a new offset vs. translate example for the specified canvas.
 * @param canvas canvas for this offset vs. translate example
 */
  public OffsetVsTranslateExample(  final PCanvas canvas){
    super("OffsetVsTranslateExample",false,canvas);
  }
  /** 
 * {@inheritDoc} 
 */
  public void initialize(){
    final PText offset=new PText("Offset node");
    final PText offsetRotated=new PText("Offset rotated node");
    final PText translate=new PText("Translated node");
    final PText translateRotated=new PText("Translated rotated node");
    offset.setScale(2.0d);
    offsetRotated.setScale(2.0d);
    translate.setScale(2.0d);
    translateRotated.setScale(2.0d);
    offsetRotated.setRotation(Math.PI / 8.0d);
    translateRotated.setRotation(Math.PI / 8.0d);
    offset.setOffset(15.0d,100.0d);
    offsetRotated.setOffset(15.0d,150.0d);
    translate.setOffset(15.0d,200.0d);
    translateRotated.setOffset(15.0d,250.0d);
    getCanvas().getLayer().addChild(offset);
    getCanvas().getLayer().addChild(offsetRotated);
    getCanvas().getLayer().addChild(translate);
    getCanvas().getLayer().addChild(translateRotated);
    offset.addActivity(new PActivity(-1L){
      /** 
 * {@inheritDoc} 
 */
      protected void activityStep(      final long elapsedTime){
        offset.offset(1.0d,0.0d);
      }
    }
);
    offsetRotated.addActivity(new PActivity(-1L){
      /** 
 * {@inheritDoc} 
 */
      protected void activityStep(      final long elapsedTime){
        offsetRotated.offset(1.0d,0.0d);
      }
    }
);
    translate.addActivity(new PActivity(-1L){
      /** 
 * {@inheritDoc} 
 */
      protected void activityStep(      final long elapsedTime){
        translate.translate(1.0d,0.0d);
      }
    }
);
    translateRotated.addActivity(new PActivity(-1L){
      /** 
 * {@inheritDoc} 
 */
      protected void activityStep(      final long elapsedTime){
        translateRotated.translate(1.0d,0.0d);
      }
    }
);
  }
  /** 
 * Main.
 * @param args command line arguments, ignored
 */
  public static void main(  final String[] args){
    new OffsetVsTranslateExample();
  }
}
