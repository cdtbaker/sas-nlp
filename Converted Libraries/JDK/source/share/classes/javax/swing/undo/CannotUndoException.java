package javax.swing.undo;
/** 
 * Thrown when an UndoableEdit is told to <code>undo()</code> and can't.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases. The current serialization support is
 * appropriate for short term storage or RMI between applications running
 * the same version of Swing.  As of 1.4, support for long term storage
 * of all JavaBeans<sup><font size="-2">TM</font></sup>
 * has been added to the <code>java.beans</code> package.
 * Please see {@link java.beans.XMLEncoder}.
 * @author Ray Ryan
 */
public class CannotUndoException extends RuntimeException {
}