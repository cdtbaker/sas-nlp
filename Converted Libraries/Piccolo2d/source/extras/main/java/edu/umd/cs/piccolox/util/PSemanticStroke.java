package edu.umd.cs.piccolox.util;
import java.awt.Shape;
import java.awt.Stroke;
import edu.umd.cs.piccolo.util.PDebug;
import edu.umd.cs.piccolo.util.PPaintContext;
import edu.umd.cs.piccolo.util.PPickPath;
/** 
 * @see edu.umd.cs.piccolo.nodes.PPath
 * @see Stroke
 * @version 1.3
 * @author Marcus Rohrmoser
 */
abstract class PSemanticStroke implements Stroke {
  protected static final double THRESHOLD=1e-6;
  private transient float recentScale;
  private transient Stroke recentStroke;
  protected final Stroke stroke;
  protected PSemanticStroke(  final Stroke stroke){
    this.stroke=stroke;
    recentStroke=stroke;
    recentScale=1.0F;
  }
  /** 
 * Ask {@link #getActiveScale()}, call {@link #newStroke(float)} if
 * necessary and delegate to {@link Stroke#createStrokedShape(Shape)}.
 * @param s
 */
  public Shape createStrokedShape(  final Shape s){
    final float currentScale=getActiveScale();
    if (Math.abs(currentScale - recentScale) > THRESHOLD) {
      recentScale=currentScale;
      recentStroke=newStroke(recentScale);
    }
    return recentStroke.createStrokedShape(s);
  }
  /** 
 * Returns true if this stroke is equivalent to the object provided.
 * @param obj Object being tested
 * @return true if object is equivalent
 */
  public boolean equals(  final Object obj){
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final PSemanticStroke other=(PSemanticStroke)obj;
    if (stroke == null) {
      if (other.stroke != null) {
        return false;
      }
    }
 else     if (!stroke.equals(other.stroke)) {
      return false;
    }
    return true;
  }
  /** 
 * Detect the current scale. Made protected to enable custom
 * re-implementations.
 */
  protected float getActiveScale(){
    if (PDebug.getProcessingOutput()) {
      if (PPaintContext.CURRENT_PAINT_CONTEXT != null) {
        return (float)PPaintContext.CURRENT_PAINT_CONTEXT.getScale();
      }
    }
 else {
      if (PPickPath.CURRENT_PICK_PATH != null) {
        return (float)PPickPath.CURRENT_PICK_PATH.getScale();
      }
    }
    return 1.0f;
  }
  public int hashCode(){
    final int prime=31;
    int result=prime;
    if (stroke != null) {
      result+=stroke.hashCode();
    }
    return result;
  }
  /** 
 * Factory to create a new internal stroke delegate. Made protected to
 * enable custom re-implementations.
 */
  protected abstract Stroke newStroke(  final float activeScale);
  public String toString(){
    return stroke.toString();
  }
}
