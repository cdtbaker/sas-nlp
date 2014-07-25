package edu.umd.cs.piccolox.swing;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import edu.umd.cs.piccolo.PCanvas;
/** 
 * The interface an application can implement to control scrolling in a
 * PScrollPane->PViewport->ZCanvas component hierarchy.
 * @see PDefaultScrollDirector
 * @author Lance Good
 */
public interface PScrollDirector {
  /** 
 * Installs the scroll director.
 * @param viewport The viewport on which this director directs
 * @param view The ZCanvas that the viewport looks at
 */
  void install(  PViewport viewport,  PCanvas view);
  /** 
 * Uninstall the scroll director.
 */
  void unInstall();
  /** 
 * Get the View position given the specified camera bounds.
 * @param viewBounds The bounds for which the view position will be computed
 * @return The view position
 */
  Point getViewPosition(  Rectangle2D viewBounds);
  /** 
 * Set the view position.
 * @param x The new x position
 * @param y The new y position
 */
  void setViewPosition(  double x,  double y);
  /** 
 * Get the size of the view based on the specified camera bounds.
 * @param viewBounds The view bounds for which the view size will be
 * computed
 * @return The view size
 */
  Dimension getViewSize(  Rectangle2D viewBounds);
}
