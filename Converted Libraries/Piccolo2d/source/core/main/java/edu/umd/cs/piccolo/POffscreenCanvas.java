package edu.umd.cs.piccolo;
import java.awt.Cursor;
import java.awt.Graphics2D;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PPaintContext;
import edu.umd.cs.piccolo.util.PUtil;
/** 
 * Offscreen canvas.
 * @since 1.3
 */
public final class POffscreenCanvas implements PComponent {
  /** 
 * Bounds of this offscreen canvas. 
 */
  private final PBounds bounds;
  /** 
 * Camera for this offscreen canvas. 
 */
  private PCamera camera;
  /** 
 * Render quality. 
 */
  private int renderQuality=DEFAULT_RENDER_QUALITY;
  /** 
 * Default render quality, <code>PPaintContext.HIGH_QUALITY_RENDERING</code>. 
 */
  static final int DEFAULT_RENDER_QUALITY=PPaintContext.HIGH_QUALITY_RENDERING;
  /** 
 * Create a new offscreen canvas the specified width and height.
 * @param width width of this offscreen canvas, must be at least zero
 * @param height height of this offscreen canvas, must be at least zero
 */
  public POffscreenCanvas(  final int width,  final int height){
    if (width < 0) {
      throw new IllegalArgumentException("width must be at least zero, was " + width);
    }
    if (height < 0) {
      throw new IllegalArgumentException("height must be at least zero, was " + height);
    }
    bounds=new PBounds(0.0d,0.0d,width,height);
    setCamera(PUtil.createBasicScenegraph());
  }
  /** 
 * Render this offscreen canvas to the specified graphics.
 * @param graphics graphics to render this offscreen canvas to, must not be
 * null
 */
  public void render(  final Graphics2D graphics){
    if (graphics == null) {
      throw new IllegalArgumentException("graphics must not be null");
    }
    final PPaintContext paintContext=new PPaintContext(graphics);
    paintContext.setRenderQuality(renderQuality);
    camera.fullPaint(paintContext);
  }
  /** 
 * Set the camera for this offscreen canvas to <code>camera</code>.
 * @param camera camera for this offscreen canvas
 */
  public void setCamera(  final PCamera camera){
    if (this.camera != null) {
      this.camera.setComponent(null);
    }
    this.camera=camera;
    if (camera != null) {
      camera.setComponent(this);
      camera.setBounds((PBounds)bounds.clone());
    }
  }
  /** 
 * Return the camera for this offscreen canvas.
 * @return the camera for this offscreen canvas
 */
  public PCamera getCamera(){
    return camera;
  }
  /** 
 * Set the render quality hint for this offscreen canvas to
 * <code>renderQuality</code>.
 * @param renderQuality render quality hint, must be one of
 * <code>PPaintContext.HIGH_QUALITY_RENDERING</code> or
 * <code>PPaintContext.LOW_QUALITY_RENDERING</code>
 */
  public void setRenderQuality(  final int renderQuality){
    if (renderQuality == PPaintContext.HIGH_QUALITY_RENDERING || renderQuality == PPaintContext.LOW_QUALITY_RENDERING) {
      this.renderQuality=renderQuality;
    }
 else {
      throw new IllegalArgumentException("renderQuality must be one of PPaintContext.HIGH_QUALITY_RENDERING" + " or PPaintContext.LOW_QUALITY_RENDERING, was " + renderQuality);
    }
  }
  /** 
 * Return the render quality hint for this offscreen canvas.
 * @return the render quality hint for this offscreen canvas
 */
  public int getRenderQuality(){
    return renderQuality;
  }
  /** 
 * {@inheritDoc} 
 */
  public void paintImmediately(){
  }
  /** 
 * {@inheritDoc} 
 */
  public void popCursor(){
  }
  /** 
 * {@inheritDoc} 
 */
  public void pushCursor(  final Cursor cursor){
  }
  /** 
 * {@inheritDoc} 
 */
  public void repaint(  final PBounds repaintBounds){
  }
  /** 
 * {@inheritDoc} 
 */
  public void setInteracting(  final boolean interacting){
  }
}
