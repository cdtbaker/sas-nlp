package sun.java2d.pipe;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Paint;
import java.awt.geom.AffineTransform;
import sun.java2d.pipe.hw.AccelSurface;
import sun.java2d.InvalidPipeException;
import sun.java2d.SunGraphics2D;
import sun.java2d.loops.XORComposite;
import static sun.java2d.pipe.BufferedOpCodes.*;
import static sun.java2d.pipe.BufferedRenderPipe.BYTES_PER_SPAN;
/** 
 * Base context class for managing state in a single-threaded rendering
 * environment.  Each state-setting operation (e.g. SET_COLOR) is added to
 * the provided RenderQueue, which will be processed at a later time by a
 * single thread.  Note that the RenderQueue lock must be acquired before
 * calling the validate() method (or any other method in this class).  See
 * the RenderQueue class comments for a sample usage scenario.
 * @see RenderQueue
 */
public abstract class BufferedContext {
  /** 
 * Indicates that no flags are needed; take all default code paths.
 */
  public static final int NO_CONTEXT_FLAGS=(0 << 0);
  /** 
 * Indicates that the source surface (or color value, if it is a simple
 * rendering operation) is opaque (has an alpha value of 1.0).  If this
 * flag is present, it allows us to disable blending in certain
 * situations in order to improve performance.
 */
  public static final int SRC_IS_OPAQUE=(1 << 0);
  /** 
 * Indicates that the operation uses an alpha mask, which may determine
 * the code path that is used when setting up the current paint state.
 */
  public static final int USE_MASK=(1 << 1);
  protected RenderQueue rq;
  protected RenderBuffer buf;
  /** 
 * This is a reference to the most recently validated BufferedContext.  If
 * this value is null, it means that there is no current context.  It is
 * provided here so that validate() only needs to do a quick reference
 * check to see if the BufferedContext passed to that method is the same
 * as the one we've cached here.
 */
  protected static BufferedContext currentContext;
  private AccelSurface validatedSrcData;
  private AccelSurface validatedDstData;
  private Region validatedClip;
  private Composite validatedComp;
  private Paint validatedPaint;
  private boolean isValidatedPaintJustAColor;
  private int validatedRGB;
  private int validatedFlags;
  private boolean xformInUse;
  private int transX;
  private int transY;
  protected BufferedContext(  RenderQueue rq){
    this.rq=rq;
    this.buf=rq.getBuffer();
  }
  /** 
 * Fetches the BufferedContextContext associated with the dst. surface
 * and validates the context using the given parameters.  Most rendering
 * operations will call this method first in order to set the necessary
 * state before issuing rendering commands.
 * Note: must be called while the RenderQueue lock is held.
 * @throws InvalidPipeException if either src or dest surface is not valid
 * or lost
 * @see RenderQueue#lock
 * @see RenderQueue#unlock
 */
  public static void validateContext(  AccelSurface srcData,  AccelSurface dstData,  Region clip,  Composite comp,  AffineTransform xform,  Paint paint,  SunGraphics2D sg2d,  int flags){
    BufferedContext d3dc=dstData.getContext();
    d3dc.validate(srcData,dstData,clip,comp,xform,paint,sg2d,flags);
  }
  /** 
 * Fetches the BufferedContextassociated with the surface
 * and disables all context state settings.
 * Note: must be called while the RenderQueue lock is held.
 * @throws InvalidPipeException if the surface is not valid
 * or lost
 * @see RenderQueue#lock
 * @see RenderQueue#unlock
 */
  public static void validateContext(  AccelSurface surface){
    validateContext(surface,surface,null,null,null,null,null,NO_CONTEXT_FLAGS);
  }
  /** 
 * Validates the given parameters against the current state for this
 * context.  If this context is not current, it will be made current
 * for the given source and destination surfaces, and the viewport will
 * be updated.  Then each part of the context state (clip, composite,
 * etc.) is checked against the previous value.  If the value has changed
 * since the last call to validate(), it will be updated accordingly.
 * Note that the SunGraphics2D parameter is only used for the purposes
 * of validating a (non-null) Paint parameter.  In all other cases it
 * is safe to pass a null SunGraphics2D and it will be ignored.
 * Note: must be called while the RenderQueue lock is held.
 * @throws InvalidPipeException if either src or dest surface is not valid
 * or lost
 */
  public void validate(  AccelSurface srcData,  AccelSurface dstData,  Region clip,  Composite comp,  AffineTransform xform,  Paint paint,  SunGraphics2D sg2d,  int flags){
    boolean updateClip=false;
    boolean updatePaint=false;
    if (!dstData.isValid() || dstData.isSurfaceLost() || srcData.isSurfaceLost()) {
      invalidateContext();
      throw new InvalidPipeException("bounds changed or surface lost");
    }
    if (paint instanceof Color) {
      int newRGB=((Color)paint).getRGB();
      if (isValidatedPaintJustAColor) {
        if (newRGB != validatedRGB) {
          validatedRGB=newRGB;
          updatePaint=true;
        }
      }
 else {
        validatedRGB=newRGB;
        updatePaint=true;
        isValidatedPaintJustAColor=true;
      }
    }
 else     if (validatedPaint != paint) {
      updatePaint=true;
      isValidatedPaintJustAColor=false;
    }
    if ((currentContext != this) || (srcData != validatedSrcData) || (dstData != validatedDstData)) {
      if (dstData != validatedDstData) {
        updateClip=true;
      }
      if (paint == null) {
        updatePaint=true;
      }
      setSurfaces(srcData,dstData);
      currentContext=this;
      validatedSrcData=srcData;
      validatedDstData=dstData;
    }
    if ((clip != validatedClip) || updateClip) {
      if (clip != null) {
        if (updateClip || validatedClip == null || !(validatedClip.isRectangular() && clip.isRectangular()) || ((clip.getLoX() != validatedClip.getLoX() || clip.getLoY() != validatedClip.getLoY() || clip.getHiX() != validatedClip.getHiX() || clip.getHiY() != validatedClip.getHiY()))) {
          setClip(clip);
        }
      }
 else {
        resetClip();
      }
      validatedClip=clip;
    }
    if ((comp != validatedComp) || (flags != validatedFlags)) {
      if (comp != null) {
        setComposite(comp,flags);
      }
 else {
        resetComposite();
      }
      updatePaint=true;
      validatedComp=comp;
      validatedFlags=flags;
    }
    boolean txChanged=false;
    if (xform == null) {
      if (xformInUse) {
        resetTransform();
        xformInUse=false;
        txChanged=true;
      }
 else       if (sg2d != null) {
        if (transX != sg2d.transX || transY != sg2d.transY) {
          txChanged=true;
        }
      }
      if (sg2d != null) {
        transX=sg2d.transX;
        transY=sg2d.transY;
      }
    }
 else {
      setTransform(xform);
      xformInUse=true;
      txChanged=true;
    }
    if (!isValidatedPaintJustAColor && txChanged) {
      updatePaint=true;
    }
    if (updatePaint) {
      if (paint != null) {
        BufferedPaints.setPaint(rq,sg2d,paint,flags);
      }
 else {
        BufferedPaints.resetPaint(rq);
      }
      validatedPaint=paint;
    }
    dstData.markDirty();
  }
  /** 
 * Invalidates the surfaces associated with this context.  This is
 * useful when the context is no longer needed, and we want to break
 * the chain caused by these surface references.
 * Note: must be called while the RenderQueue lock is held.
 * @see RenderQueue#lock
 * @see RenderQueue#unlock
 */
  public void invalidateSurfaces(){
    validatedSrcData=null;
    validatedDstData=null;
  }
  private void setSurfaces(  AccelSurface srcData,  AccelSurface dstData){
    rq.ensureCapacityAndAlignment(20,4);
    buf.putInt(SET_SURFACES);
    buf.putLong(srcData.getNativeOps());
    buf.putLong(dstData.getNativeOps());
  }
  private void resetClip(){
    rq.ensureCapacity(4);
    buf.putInt(RESET_CLIP);
  }
  private void setClip(  Region clip){
    if (clip.isRectangular()) {
      rq.ensureCapacity(20);
      buf.putInt(SET_RECT_CLIP);
      buf.putInt(clip.getLoX()).putInt(clip.getLoY());
      buf.putInt(clip.getHiX()).putInt(clip.getHiY());
    }
 else {
      rq.ensureCapacity(28);
      buf.putInt(BEGIN_SHAPE_CLIP);
      buf.putInt(SET_SHAPE_CLIP_SPANS);
      int countIndex=buf.position();
      buf.putInt(0);
      int spanCount=0;
      int remainingSpans=buf.remaining() / BYTES_PER_SPAN;
      int span[]=new int[4];
      SpanIterator si=clip.getSpanIterator();
      while (si.nextSpan(span)) {
        if (remainingSpans == 0) {
          buf.putInt(countIndex,spanCount);
          rq.flushNow();
          buf.putInt(SET_SHAPE_CLIP_SPANS);
          countIndex=buf.position();
          buf.putInt(0);
          spanCount=0;
          remainingSpans=buf.remaining() / BYTES_PER_SPAN;
        }
        buf.putInt(span[0]);
        buf.putInt(span[1]);
        buf.putInt(span[2]);
        buf.putInt(span[3]);
        spanCount++;
        remainingSpans--;
      }
      buf.putInt(countIndex,spanCount);
      rq.ensureCapacity(4);
      buf.putInt(END_SHAPE_CLIP);
    }
  }
  private void resetComposite(){
    rq.ensureCapacity(4);
    buf.putInt(RESET_COMPOSITE);
  }
  private void setComposite(  Composite comp,  int flags){
    if (comp instanceof AlphaComposite) {
      AlphaComposite ac=(AlphaComposite)comp;
      rq.ensureCapacity(16);
      buf.putInt(SET_ALPHA_COMPOSITE);
      buf.putInt(ac.getRule());
      buf.putFloat(ac.getAlpha());
      buf.putInt(flags);
    }
 else     if (comp instanceof XORComposite) {
      int xorPixel=((XORComposite)comp).getXorPixel();
      rq.ensureCapacity(8);
      buf.putInt(SET_XOR_COMPOSITE);
      buf.putInt(xorPixel);
    }
 else {
      throw new InternalError("not yet implemented");
    }
  }
  private void resetTransform(){
    rq.ensureCapacity(4);
    buf.putInt(RESET_TRANSFORM);
  }
  private void setTransform(  AffineTransform xform){
    rq.ensureCapacityAndAlignment(52,4);
    buf.putInt(SET_TRANSFORM);
    buf.putDouble(xform.getScaleX());
    buf.putDouble(xform.getShearY());
    buf.putDouble(xform.getShearX());
    buf.putDouble(xform.getScaleY());
    buf.putDouble(xform.getTranslateX());
    buf.putDouble(xform.getTranslateY());
  }
  /** 
 * Resets this context's surfaces and all attributes.
 * Note: must be called while the RenderQueue lock is held.
 * @see RenderQueue#lock
 * @see RenderQueue#unlock
 */
  public void invalidateContext(){
    resetTransform();
    resetComposite();
    resetClip();
    BufferedPaints.resetPaint(rq);
    invalidateSurfaces();
    validatedComp=null;
    validatedClip=null;
    validatedPaint=null;
    isValidatedPaintJustAColor=false;
    xformInUse=false;
  }
  /** 
 * Returns a singleton {@code RenderQueue} object used by the rendering
 * pipeline.
 * @return a render queue
 * @see RenderQueue
 */
  public abstract RenderQueue getRenderQueue();
  /** 
 * Saves the the state of this context.
 * It may reset the current context.
 * Note: must be called while the RenderQueue lock is held.
 * @see RenderQueue#lock
 * @see RenderQueue#unlock
 */
  public abstract void saveState();
  /** 
 * Restores the native state of this context.
 * It may reset the current context.
 * Note: must be called while the RenderQueue lock is held.
 * @see RenderQueue#lock
 * @see RenderQueue#unlock
 */
  public abstract void restoreState();
}
