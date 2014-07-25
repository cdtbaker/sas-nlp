package edu.umd.cs.piccolo;
import java.awt.Cursor;
import edu.umd.cs.piccolo.util.PBounds;
/** 
 * Interface that a component needs to implement if it wants to act as a Piccolo
 * canvas.
 * @version 1.0
 * @author Lance Good
 */
public interface PComponent {
  /** 
 * Called to notify PComponent that given bounds need repainting.
 * @param bounds bounds needing repaint
 */
  void repaint(  PBounds bounds);
  /** 
 * Sends a repaint notification the repaint manager if PComponent is not
 * already painting immediately.
 */
  void paintImmediately();
  /** 
 * Pushes the given cursor onto the cursor stack and sets the current cursor
 * to the one provided.
 * @param cursor The cursor to set as the current one and push
 */
  void pushCursor(  Cursor cursor);
  /** 
 * Pops the topmost cursor from the stack and sets it as the current one.
 */
  void popCursor();
  /** 
 * Sets whether the component is currently being interacted with.
 * @param interacting whether the component is currently being interacted
 * with
 */
  void setInteracting(  boolean interacting);
}
