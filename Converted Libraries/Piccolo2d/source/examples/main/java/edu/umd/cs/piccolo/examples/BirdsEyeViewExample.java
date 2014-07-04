package edu.umd.cs.piccolo.examples;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JDialog;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PDragSequenceEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolo.util.PPaintContext;
import edu.umd.cs.piccolox.PFrame;
import edu.umd.cs.piccolox.nodes.P3DRect;
/** 
 * This example, contributed by Rowan Christmas, shows how to create a birds-eye
 * view window.
 */
public class BirdsEyeViewExample extends PFrame {
  /** 
 */
  private static final long serialVersionUID=1L;
  boolean fIsPressed=false;
  public BirdsEyeViewExample(){
    this(null);
  }
  public BirdsEyeViewExample(  final PCanvas aCanvas){
    super("BirdsEyeViewExample",false,aCanvas);
  }
  public void initialize(){
    nodeDemo();
    createNodeUsingExistingClasses();
    subclassExistingClasses();
    composeOtherNodes();
    createCustomNode();
    getCanvas().removeInputEventListener(getCanvas().getPanEventHandler());
    getCanvas().addInputEventListener(new PDragEventHandler());
    final BirdsEyeView bev=new BirdsEyeView();
    bev.connect(getCanvas(),new PLayer[]{getCanvas().getLayer()});
    final JDialog bird=new JDialog();
    bird.getContentPane().add(bev);
    bird.pack();
    bird.setSize(150,150);
    bird.setVisible(true);
  }
  public void nodeDemo(){
    final PLayer layer=getCanvas().getLayer();
    final PNode aNode=PPath.createRectangle(0,0,100,80);
    layer.addChild(aNode);
    aNode.setPaint(Color.red);
    aNode.addChild(PPath.createRectangle(0,0,100,80));
    aNode.setBounds(-10,-10,200,110);
    aNode.translate(100,100);
    aNode.scale(1.5);
    aNode.rotate(45);
    aNode.setTransparency(0.75f);
    final PNode aCopy=(PNode)aNode.clone();
    aNode.setChildrenPickable(false);
    aNode.setPaint(Color.GREEN);
    aNode.setTransparency(1.0f);
    layer.addChild(aCopy);
    aCopy.setOffset(0,0);
    aCopy.rotate(-45);
  }
  public void createNodeUsingExistingClasses(){
    final PLayer layer=getCanvas().getLayer();
    layer.addChild(PPath.createEllipse(0,0,100,100));
    layer.addChild(PPath.createRectangle(0,100,100,100));
    layer.addChild(new PText("Hello World"));
    layer.addChild(new PImage(layer.toImage(300,300,Color.YELLOW)));
  }
  public void subclassExistingClasses(){
    final PNode n=new PPath(new Ellipse2D.Float(0,0,100,80)){
      /** 
 */
      private static final long serialVersionUID=1L;
      public void paint(      final PPaintContext aPaintContext){
        if (fIsPressed) {
          final Graphics2D g2=aPaintContext.getGraphics();
          g2.setPaint(getPaint());
          g2.fill(getBoundsReference());
        }
 else {
          super.paint(aPaintContext);
        }
      }
    }
;
    n.addInputEventListener(new PBasicInputEventHandler(){
      public void mousePressed(      final PInputEvent aEvent){
        super.mousePressed(aEvent);
        fIsPressed=true;
        n.invalidatePaint();
      }
      public void mouseReleased(      final PInputEvent aEvent){
        super.mousePressed(aEvent);
        fIsPressed=false;
        n.invalidatePaint();
      }
    }
);
    n.setPaint(Color.ORANGE);
    getCanvas().getLayer().addChild(n);
  }
  public void composeOtherNodes(){
    final PNode myCompositeFace=PPath.createRectangle(0,0,100,80);
    final PNode eye1=PPath.createEllipse(0,0,20,20);
    eye1.setPaint(Color.YELLOW);
    final PNode eye2=(PNode)eye1.clone();
    final PNode mouth=PPath.createRectangle(0,0,40,20);
    mouth.setPaint(Color.BLACK);
    myCompositeFace.addChild(eye1);
    myCompositeFace.addChild(eye2);
    myCompositeFace.addChild(mouth);
    myCompositeFace.setChildrenPickable(false);
    eye2.translate(25,0);
    mouth.translate(0,30);
    final PBounds b=myCompositeFace.getUnionOfChildrenBounds(null);
    myCompositeFace.setBounds(b.inset(-5,-5));
    myCompositeFace.scale(1.5);
    getCanvas().getLayer().addChild(myCompositeFace);
  }
  public void createCustomNode(){
    final PNode n=new PNode(){
      /** 
 */
      private static final long serialVersionUID=1L;
      public void paint(      final PPaintContext aPaintContext){
        final double bx=getX();
        final double by=getY();
        final double rightBorder=bx + getWidth();
        final double bottomBorder=by + getHeight();
        final Line2D line=new Line2D.Double();
        final Graphics2D g2=aPaintContext.getGraphics();
        g2.setStroke(new BasicStroke(0));
        g2.setPaint(getPaint());
        for (double x=bx; x < rightBorder; x+=5) {
          line.setLine(x,by,x,bottomBorder);
          g2.draw(line);
        }
        for (double y=by; y < bottomBorder; y+=5) {
          line.setLine(bx,y,rightBorder,y);
          g2.draw(line);
        }
      }
    }
;
    n.setBounds(0,0,100,80);
    n.setPaint(Color.black);
    getCanvas().getLayer().addChild(n);
  }
  public static void main(  final String[] args){
    new BirdsEyeViewExample();
  }
  /** 
 * The Birds Eye View Class
 */
public class BirdsEyeView extends PCanvas implements PropertyChangeListener {
    /** 
 */
    private static final long serialVersionUID=1L;
    /** 
 * This is the node that shows the viewed area.
 */
    PNode areaVisiblePNode;
    /** 
 * This is the canvas that is being viewed
 */
    PCanvas viewedCanvas;
    /** 
 * The change listener to know when to update the birds eye view.
 */
    PropertyChangeListener changeListener;
    int layerCount;
    /** 
 * Creates a new instance of a BirdsEyeView
 */
    public BirdsEyeView(){
      changeListener=new PropertyChangeListener(){
        public void propertyChange(        final PropertyChangeEvent evt){
          updateFromViewed();
        }
      }
;
      areaVisiblePNode=new P3DRect();
      areaVisiblePNode.setPaint(new Color(128,128,255));
      areaVisiblePNode.setTransparency(.8f);
      areaVisiblePNode.setBounds(0,0,100,100);
      getCamera().addChild(areaVisiblePNode);
      getCamera().addInputEventListener(new PDragSequenceEventHandler(){
        protected void startDrag(        final PInputEvent e){
          if (e.getPickedNode() == areaVisiblePNode) {
            super.startDrag(e);
          }
        }
        protected void drag(        final PInputEvent e){
          final PDimension dim=e.getDelta();
          viewedCanvas.getCamera().translateView(0 - dim.getWidth(),0 - dim.getHeight());
        }
      }
);
      removeInputEventListener(getPanEventHandler());
      removeInputEventListener(getZoomEventHandler());
      setDefaultRenderQuality(PPaintContext.LOW_QUALITY_RENDERING);
    }
    public void connect(    final PCanvas canvas,    final PLayer[] viewed_layers){
      viewedCanvas=canvas;
      layerCount=0;
      viewedCanvas.getCamera().addPropertyChangeListener(changeListener);
      for (layerCount=0; layerCount < viewed_layers.length; ++layerCount) {
        getCamera().addLayer(layerCount,viewed_layers[layerCount]);
      }
    }
    /** 
 * Add a layer to list of viewed layers
 */
    public void addLayer(    final PLayer new_layer){
      getCamera().addLayer(new_layer);
      layerCount++;
    }
    /** 
 * Remove the layer from the viewed layers
 */
    public void removeLayer(    final PLayer old_layer){
      getCamera().removeLayer(old_layer);
      layerCount--;
    }
    /** 
 * Stop the birds eye view from receiving events from the viewed canvas
 * and remove all layers
 */
    public void disconnect(){
      viewedCanvas.getCamera().removePropertyChangeListener(changeListener);
      for (int i=0; i < getCamera().getLayerCount(); ++i) {
        getCamera().removeLayer(i);
      }
    }
    /** 
 * This method will get called when the viewed canvas changes
 */
    public void propertyChange(    final PropertyChangeEvent event){
      updateFromViewed();
    }
    /** 
 * This method gets the state of the viewed canvas and updates the
 * BirdsEyeViewer This can be called from outside code
 */
    public void updateFromViewed(){
      double viewedX;
      double viewedY;
      double viewedHeight;
      double viewedWidth;
      final double ul_camera_x=viewedCanvas.getCamera().getViewBounds().getX();
      final double ul_camera_y=viewedCanvas.getCamera().getViewBounds().getY();
      final double lr_camera_x=ul_camera_x + viewedCanvas.getCamera().getViewBounds().getWidth();
      final double lr_camera_y=ul_camera_y + viewedCanvas.getCamera().getViewBounds().getHeight();
      final Rectangle2D drag_bounds=getCamera().getUnionOfLayerFullBounds();
      final double ul_layer_x=drag_bounds.getX();
      final double ul_layer_y=drag_bounds.getY();
      final double lr_layer_x=drag_bounds.getX() + drag_bounds.getWidth();
      final double lr_layer_y=drag_bounds.getY() + drag_bounds.getHeight();
      if (ul_camera_x < ul_layer_x) {
        viewedX=ul_layer_x;
      }
 else {
        viewedX=ul_camera_x;
      }
      if (ul_camera_y < ul_layer_y) {
        viewedY=ul_layer_y;
      }
 else {
        viewedY=ul_camera_y;
      }
      if (lr_camera_x < lr_layer_x) {
        viewedWidth=lr_camera_x - viewedX;
      }
 else {
        viewedWidth=lr_layer_x - viewedX;
      }
      if (lr_camera_y < lr_layer_y) {
        viewedHeight=lr_camera_y - viewedY;
      }
 else {
        viewedHeight=lr_layer_y - viewedY;
      }
      Rectangle2D bounds=new Rectangle2D.Double(viewedX,viewedY,viewedWidth,viewedHeight);
      bounds=getCamera().viewToLocal(bounds);
      areaVisiblePNode.setBounds(bounds);
      getCamera().animateViewToCenterBounds(drag_bounds,true,0);
    }
  }
}
