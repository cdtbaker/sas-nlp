package edu.umd.cs.piccolox.nodes;
import java.awt.Image;
import java.awt.Paint;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolox.util.ShadowUtils;
/** 
 * Shadow node.
 * @since 1.3
 */
public final class PShadow extends PImage {
  /** 
 * Default serial version UID. 
 */
  private static final long serialVersionUID=1L;
  /** 
 * Create a new shadow node containing a shadow of the specified source image using the
 * specified shadow paint and gaussian blur radius.  The dimensions of this node will be
 * <code>src.getWidth() + 4 * blurRadius</code> x <code>src.getHeight() + 4 * blurRadius</code>
 * to account for blurring beyond the bounds of the source image.  Thus the source image
 * will appear to be be offset by (<code>2 * blurRadius</code>, <code>2 * blurRadius</code>)
 * in this node.
 * @param src source image, must not be null
 * @param shadowPaint shadow paint
 * @param blurRadius gaussian blur radius, must be <code>&gt; 0</code>
 */
  public PShadow(  final Image src,  final Paint shadowPaint,  final int blurRadius){
    super(ShadowUtils.createShadow(src,shadowPaint,blurRadius));
  }
}
