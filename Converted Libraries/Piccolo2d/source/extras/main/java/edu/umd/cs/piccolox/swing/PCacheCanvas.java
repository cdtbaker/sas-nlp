package edu.umd.cs.piccolox.swing;
import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.PRoot;
import edu.umd.cs.piccolox.nodes.PCacheCamera;
/** 
 * An extension of PCanvas that automatically installs a PCacheCamera.
 * @author Lance Good
 */
public class PCacheCanvas extends PCanvas {
  private static final long serialVersionUID=1L;
  /** 
 * Creates a default scene with 1 root, 1 layer, and 1 PCacheCamera.
 * @return constructed scene with PCacheCamera
 */
  protected PCamera createDefaultCamera(){
    final PRoot r=new PRoot();
    final PLayer l=new PLayer();
    final PCamera c=new PCacheCamera();
    r.addChild(c);
    r.addChild(l);
    c.addLayer(l);
    return c;
  }
}
