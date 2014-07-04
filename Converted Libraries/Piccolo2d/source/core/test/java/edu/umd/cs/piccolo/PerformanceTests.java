package edu.umd.cs.piccolo;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;
import junit.framework.TestCase;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PAffineTransform;
import edu.umd.cs.piccolo.util.PBounds;
/** 
 * Performance tests.
 */
public class PerformanceTests extends TestCase {
  private static PerformanceLog log=new PerformanceLog();
  private static int NUMBER_NODES=20000;
  public PerformanceTests(  final String name){
    super(name);
  }
  public void testRunPerformanceTests(){
    if (1 == 1) {
      return;
    }
    for (int i=0; i < 3; i++) {
      addNodes();
      copyNodes();
      createNodes();
      createPaths();
      fullIntersectsNodes();
      memorySizeOfNodes();
      translateNodes();
      costOfNoBoundsCache();
      if (i != 2) {
        log.clear();
      }
    }
    log.writeLog();
  }
  public void createNodes(){
    final PNode[] nodes=new PNode[NUMBER_NODES];
    log.startTest();
    for (int i=0; i < NUMBER_NODES; i++) {
      nodes[i]=new PNode();
    }
    log.endTest("Create " + NUMBER_NODES + " new nodes");
  }
  public void createPaths(){
    final PNode[] nodes=new PNode[NUMBER_NODES];
    log.startTest();
    for (int i=0; i < NUMBER_NODES; i++) {
      nodes[i]=PPath.createRectangle(0,0,100,80);
    }
    log.endTest("Create " + NUMBER_NODES + " new rect paths");
    final Random r=new Random();
    for (int i=0; i < NUMBER_NODES; i++) {
      nodes[i].translate(r.nextFloat() * 300,r.nextFloat() * 300);
    }
  }
  public void addNodes(){
    final PNode parent=new PNode();
    final PNode[] nodes=new PNode[NUMBER_NODES];
    for (int i=0; i < NUMBER_NODES; i++) {
      nodes[i]=new PNode();
    }
    log.startTest();
    for (int i=0; i < NUMBER_NODES; i++) {
      parent.addChild(nodes[i]);
    }
    log.endTest("Add " + NUMBER_NODES + " nodes to a new parent");
  }
  public void removeNodes(){
    final PNode parent=new PNode();
    final PNode[] nodes=new PNode[NUMBER_NODES];
    final ArrayList list=new ArrayList();
    for (int i=0; i < NUMBER_NODES; i++) {
      nodes[i]=new PNode();
    }
    for (int i=0; i < NUMBER_NODES; i++) {
      parent.addChild(nodes[i]);
      list.add(nodes[i]);
    }
    log.startTest();
    for (int i=0; i < NUMBER_NODES; i++) {
      parent.removeChild(nodes[i]);
    }
    log.endTest("Remove " + NUMBER_NODES + " nodes using removeChild() front to back");
    parent.addChildren(list);
    log.startTest();
    for (int i=NUMBER_NODES - 1; i >= 0; i--) {
      parent.removeChild(i);
    }
    log.endTest("Remove " + NUMBER_NODES + " nodes using removeChild() back to front by index");
    log.startTest();
    log.endTest("Remove " + NUMBER_NODES + " nodes using removeChild() back to front by object, TO_SLOW");
    parent.addChildren(list);
    log.startTest();
    parent.removeChildren(list);
    log.endTest("Remove " + NUMBER_NODES + " nodes using removeChildren()");
    parent.addChildren(list);
    log.startTest();
    parent.removeAllChildren();
    log.endTest("Remove " + NUMBER_NODES + " nodes using removeAllChildren()");
  }
  public void translateNodes(){
    final PNode parent=new PNode();
    final PNode[] nodes=new PNode[NUMBER_NODES];
    final PBounds b=new PBounds();
    final Random r=new Random();
    for (int i=0; i < NUMBER_NODES; i++) {
      nodes[i]=new PNode();
      nodes[i].setBounds(1000 * r.nextFloat(),1000 * r.nextFloat(),100,80);
      parent.addChild(nodes[i]);
      nodes[i].getFullBoundsReference();
    }
    log.startTest();
    for (int i=0; i < NUMBER_NODES; i++) {
      nodes[i].translate(1000 * r.nextFloat(),1000 * r.nextFloat());
      nodes[i].scale(1000 * r.nextFloat());
    }
    log.endTest("Translate " + NUMBER_NODES + " nodes, not counting repaint or validate layout");
    log.startTest();
    parent.getFullBoundsReference();
    log.endTest("Validate Layout after translate " + NUMBER_NODES + " nodes");
    log.startTest();
    parent.validateFullPaint();
    log.endTest("Validate Paint after translate " + NUMBER_NODES + " nodes");
    log.startTest();
    parent.computeFullBounds(b);
    log.endTest("Parent compute bounds of " + NUMBER_NODES + " children nodes");
  }
  public void fullIntersectsNodes(){
    final PNode parent=new PNode();
    final PNode[] nodes=new PNode[NUMBER_NODES];
    final PBounds b=new PBounds(0,50,100,20);
    for (int i=0; i < NUMBER_NODES; i++) {
      nodes[i]=new PNode();
      parent.addChild(nodes[i]);
    }
    parent.getFullBoundsReference();
    log.startTest();
    for (int i=0; i < NUMBER_NODES; i++) {
      nodes[i].fullIntersects(b);
    }
    log.endTest("Do fullIntersects test for " + NUMBER_NODES + " nodes");
  }
  public void memorySizeOfNodes(){
    final PNode[] nodes=new PNode[NUMBER_NODES];
    Runtime.getRuntime().gc();
    final long startTotalMemory=Runtime.getRuntime().totalMemory();
    final long startFree=Runtime.getRuntime().freeMemory();
    long endFree;
    long endTotal;
    for (int i=0; i < NUMBER_NODES; i++) {
      nodes[i]=new PNode();
    }
    Runtime.getRuntime().gc();
    endFree=Runtime.getRuntime().freeMemory();
    endTotal=Runtime.getRuntime().totalMemory();
    log.addEntry("Approximate k used by " + NUMBER_NODES + " nodes",(endTotal - startTotalMemory + startFree - endFree) / 1024);
    nodes[0].getPaint();
  }
  public void copyNodes(){
    final PNode parent=new PNode();
    final PNode[] nodes=new PNode[NUMBER_NODES];
    for (int i=0; i < NUMBER_NODES; i++) {
      nodes[i]=new PNode();
      parent.addChild(nodes[i]);
    }
    log.startTest();
    parent.clone();
    log.endTest("Copy/Serialize " + NUMBER_NODES + " nodes");
  }
  public void costOfNoBoundsCache(){
    final PNode[] nodes=new PNode[NUMBER_NODES];
    final PBounds[] bounds=new PBounds[NUMBER_NODES];
    final PBounds pickRect=new PBounds(0,0,1,1);
    final Random r=new Random();
    for (int i=0; i < NUMBER_NODES; i++) {
      nodes[i]=new PNode();
      nodes[i].translate(1000 * r.nextFloat(),1000 * r.nextFloat());
      nodes[i].scale(1000 * r.nextFloat());
      bounds[i]=new PBounds(1000 * r.nextFloat(),1000 * r.nextFloat(),100,80);
    }
    log.startTest();
    for (int i=0; i < NUMBER_NODES; i++) {
      bounds[i].intersects(pickRect);
    }
    log.endTest("Do intersects test for " + NUMBER_NODES + " bounds");
    log.startTest();
    for (int i=0; i < NUMBER_NODES; i++) {
      nodes[i].localToParent(bounds[i]);
    }
    log.endTest("Transform " + NUMBER_NODES + " bounds from local to parent");
    log.startTest();
    for (int i=0; i < NUMBER_NODES; i++) {
      pickRect.add(bounds[i]);
    }
    log.endTest("Sum " + NUMBER_NODES + " bounds");
    final PBounds b=new PBounds(r.nextDouble(),r.nextDouble(),r.nextDouble(),r.nextDouble());
    log.startTest();
    for (int i=0; i < NUMBER_NODES * 10; i++) {
      b.clone();
    }
    log.endTest("Clone " + NUMBER_NODES * 10 + " PBounds");
  }
  public void renderSpeed() throws NoninvertibleTransformException {
    final Random r=new Random();
    final PAffineTransform at=new PAffineTransform();
    at.setScale(r.nextFloat());
    at.translate(r.nextFloat(),r.nextFloat());
    log.startTest();
    for (int i=0; i < NUMBER_NODES; i++) {
      at.createInverse();
    }
    log.endTest("Create inverse transform " + NUMBER_NODES + " times");
    final int height=400;
    final int width=400;
    final double scale1=0.5;
    final double scale2=2;
    boolean scaleFlip=true;
    final PAffineTransform transorm1=new PAffineTransform();
    transorm1.translate(0.5,10.1);
    PAffineTransform transorm2=null;
    transorm2=new PAffineTransform(transorm1.createInverse());
    final GraphicsConfiguration graphicsConfiguration=GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
    final BufferedImage result=graphicsConfiguration.createCompatibleImage(width,height,Transparency.TRANSLUCENT);
    final Graphics2D g2=result.createGraphics();
    log.startTest();
    for (int i=0; i < NUMBER_NODES; i++) {
      if (scaleFlip) {
        g2.scale(scale2,scale2);
        scaleFlip=!scaleFlip;
      }
 else {
        g2.scale(scale1,scale1);
        scaleFlip=!scaleFlip;
      }
    }
    log.endTest("Scale graphics context " + NUMBER_NODES + " times");
    g2.setTransform(new AffineTransform());
    log.startTest();
    for (int i=0; i < NUMBER_NODES; i++) {
      g2.translate(0.5,0.5);
    }
    log.endTest("Translate graphics context " + NUMBER_NODES + " times");
    g2.setTransform(new AffineTransform());
    log.startTest();
    for (int i=0; i < NUMBER_NODES; i++) {
      if (scaleFlip) {
        g2.transform(transorm1);
        scaleFlip=!scaleFlip;
      }
 else {
        g2.transform(transorm2);
        scaleFlip=!scaleFlip;
      }
    }
    log.endTest("Transform graphics context " + NUMBER_NODES + " times");
    final Rectangle2D rect=new Rectangle2D.Double(0,0,100,80);
    final GeneralPath path=new GeneralPath(rect);
    log.startTest();
    for (int i=0; i < NUMBER_NODES; i++) {
      g2.fill(rect);
    }
    log.endTest("Fill " + NUMBER_NODES + " rects");
    log.startTest();
    for (int i=0; i < NUMBER_NODES; i++) {
      g2.getTransform().getScaleX();
    }
    log.endTest("Call g2.getTransform() " + NUMBER_NODES + " times");
    log.startTest();
    for (int i=0; i < NUMBER_NODES; i++) {
      g2.fill(path);
    }
    log.endTest("Fill " + NUMBER_NODES + " paths");
  }
}
