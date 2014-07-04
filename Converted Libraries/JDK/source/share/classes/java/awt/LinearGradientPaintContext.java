package java.awt;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.MultipleGradientPaint.ColorSpaceType;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;
/** 
 * Provides the actual implementation for the LinearGradientPaint.
 * This is where the pixel processing is done.
 * @see java.awt.LinearGradientPaint
 * @see java.awt.PaintContext
 * @see java.awt.Paint
 * @author Nicholas Talian, Vincent Hardy, Jim Graham, Jerry Evans
 */
final class LinearGradientPaintContext extends MultipleGradientPaintContext {
  /** 
 * The following invariants are used to process the gradient value from
 * a device space coordinate, (X, Y):
 * g(X, Y) = dgdX*X + dgdY*Y + gc
 */
  private float dgdX, dgdY, gc;
  /** 
 * Constructor for LinearGradientPaintContext.
 * @param paint the {@code LinearGradientPaint} from which this context
 * is created
 * @param cm {@code ColorModel} that receives
 * the <code>Paint</code> data. This is used only as a hint.
 * @param deviceBounds the device space bounding box of the
 * graphics primitive being rendered
 * @param userBounds the user space bounding box of the
 * graphics primitive being rendered
 * @param t the {@code AffineTransform} from user
 * space into device space (gradientTransform should be
 * concatenated with this)
 * @param hints the hints that the context object uses to choose
 * between rendering alternatives
 * @param dStart gradient start point, in user space
 * @param dEnd gradient end point, in user space
 * @param fractions the fractions specifying the gradient distribution
 * @param colors the gradient colors
 * @param cycleMethod either NO_CYCLE, REFLECT, or REPEAT
 * @param colorSpace which colorspace to use for interpolation,
 * either SRGB or LINEAR_RGB
 */
  LinearGradientPaintContext(  LinearGradientPaint paint,  ColorModel cm,  Rectangle deviceBounds,  Rectangle2D userBounds,  AffineTransform t,  RenderingHints hints,  Point2D start,  Point2D end,  float[] fractions,  Color[] colors,  CycleMethod cycleMethod,  ColorSpaceType colorSpace){
    super(paint,cm,deviceBounds,userBounds,t,hints,fractions,colors,cycleMethod,colorSpace);
    float startx=(float)start.getX();
    float starty=(float)start.getY();
    float endx=(float)end.getX();
    float endy=(float)end.getY();
    float dx=endx - startx;
    float dy=endy - starty;
    float dSq=dx * dx + dy * dy;
    float constX=dx / dSq;
    float constY=dy / dSq;
    dgdX=a00 * constX + a10 * constY;
    dgdY=a01 * constX + a11 * constY;
    gc=(a02 - startx) * constX + (a12 - starty) * constY;
  }
  /** 
 * Return a Raster containing the colors generated for the graphics
 * operation.  This is where the area is filled with colors distributed
 * linearly.
 * @param x,y,w,h the area in device space for which colors are
 * generated.
 */
  protected void fillRaster(  int[] pixels,  int off,  int adjust,  int x,  int y,  int w,  int h){
    float g=0;
    int rowLimit=off + w;
    float initConst=(dgdX * x) + gc;
    for (int i=0; i < h; i++) {
      g=initConst + dgdY * (y + i);
      while (off < rowLimit) {
        pixels[off++]=indexIntoGradientsArrays(g);
        g+=dgdX;
      }
      off+=adjust;
      rowLimit=off + w;
    }
  }
}
