package edu.umd.cs.piccolo.examples;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PActivity;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.PFrame;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.ref.WeakReference;
/** 
 * Example that demonstrates the memory leak in Issue 185.
 * <p>
 * Memory leak - PActivityScheduler keeps processed activities in reference<br/>
 * <a href="http://code.google.com/p/piccolo2d/issues/detail?id=185">http://code.google.com/p/piccolo2d/issues/detail?id=185</a>
 * </p>
 */
public class ActivityMemoryLeakBugExample extends PFrame {
  public static void main(  String[] args){
    new ActivityMemoryLeakBugExample();
  }
  /** 
 * {@inheritDoc} 
 */
  public void initialize(){
    final PLayer layer=getCanvas().getLayer();
    PNode node=PPath.createEllipse(20,20,20,20);
    layer.addChild(node);
    final WeakReference ref=new WeakReference(layer.getChild(0));
    ((PNode)ref.get()).animateToPositionScaleRotation(0,0,5.0,0,1000);
    new Timer(2000,new ActionListener(){
      public void actionPerformed(      final ActionEvent e){
        layer.removeAllChildren();
        System.gc();
        System.out.println(ref.get());
        System.out.println(layer.getRoot().getActivityScheduler().getActivitiesReference().size());
      }
    }
).start();
    forceCleanupOfPriorActivities(layer);
  }
  private void forceCleanupOfPriorActivities(  final PLayer layer){
    new Thread(){
      /** 
 * {@inheritDoc} 
 */
      public void run(){
        try {
          Thread.sleep(6000);
        }
 catch (        InterruptedException e) {
        }
        SwingUtilities.invokeLater(new Runnable(){
          public void run(){
            PActivity a=new PActivity(-1){
              protected void activityStep(              final long elapsedTime){
                System.out.println("cleanup activity");
                terminate();
              }
            }
;
            layer.getRoot().addActivity(a);
          }
        }
);
      }
    }
.start();
  }
}
