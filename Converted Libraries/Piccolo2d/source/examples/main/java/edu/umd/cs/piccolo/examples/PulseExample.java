package edu.umd.cs.piccolo.examples;
import java.awt.Color;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.PRoot;
import edu.umd.cs.piccolo.activities.PActivityScheduler;
import edu.umd.cs.piccolo.activities.PColorActivity;
import edu.umd.cs.piccolo.activities.PInterpolatingActivity;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.PFrame;
/** 
 * This example shows how to set up interpolating activities that repeat. For
 * example it shows how to create a rectangle whos color pulses.
 * @author jesse
 */
public class PulseExample extends PFrame {
  /** 
 */
  private static final long serialVersionUID=1L;
  public PulseExample(){
    this(null);
  }
  public PulseExample(  final PCanvas aCanvas){
    super("PulseExample",false,aCanvas);
  }
  public void initialize(){
    final PRoot root=getCanvas().getRoot();
    final PLayer layer=getCanvas().getLayer();
    final PActivityScheduler scheduler=root.getActivityScheduler();
    final PNode singlePulse=PPath.createRectangle(0,0,100,80);
    final PPath repeatePulse=PPath.createRectangle(100,80,100,80);
    final PNode repeateReversePulse=PPath.createRectangle(200,160,100,80);
    layer.addChild(singlePulse);
    layer.addChild(repeatePulse);
    layer.addChild(repeateReversePulse);
    final PColorActivity singlePulseActivity=new PColorActivity(1000,0,1,PInterpolatingActivity.SOURCE_TO_DESTINATION,new PColorActivity.Target(){
      public Color getColor(){
        return (Color)singlePulse.getPaint();
      }
      public void setColor(      final Color color){
        singlePulse.setPaint(color);
      }
    }
,Color.ORANGE);
    final PColorActivity repeatPulseActivity=new PColorActivity(1000,0,5,PInterpolatingActivity.SOURCE_TO_DESTINATION,new PColorActivity.Target(){
      public Color getColor(){
        return (Color)repeatePulse.getPaint();
      }
      public void setColor(      final Color color){
        repeatePulse.setPaint(color);
      }
    }
,Color.BLUE);
    final PColorActivity repeatReversePulseActivity=new PColorActivity(500,0,10,PInterpolatingActivity.SOURCE_TO_DESTINATION_TO_SOURCE,new PColorActivity.Target(){
      public Color getColor(){
        return (Color)repeateReversePulse.getPaint();
      }
      public void setColor(      final Color color){
        repeateReversePulse.setPaint(color);
      }
    }
,Color.GREEN);
    scheduler.addActivity(singlePulseActivity);
    scheduler.addActivity(repeatPulseActivity);
    scheduler.addActivity(repeatReversePulseActivity);
  }
  public static void main(  final String[] args){
    new PulseExample();
  }
}
