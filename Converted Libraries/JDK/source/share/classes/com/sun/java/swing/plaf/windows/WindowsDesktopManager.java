package com.sun.java.swing.plaf.windows;
import javax.swing.DefaultDesktopManager;
import javax.swing.JInternalFrame;
import javax.swing.JLayeredPane;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.beans.PropertyVetoException;
import java.util.Vector;
import java.lang.ref.WeakReference;
/** 
 * This class implements a DesktopManager which more closely follows
 * the MDI model than the DefaultDesktopManager.  Unlike the
 * DefaultDesktopManager policy, MDI requires that the selected
 * and activated child frames are the same, and that that frame
 * always be the top-most window.
 * <p>
 * The maximized state is managed by the DesktopManager with MDI,
 * instead of just being a property of the individual child frame.
 * This means that if the currently selected window is maximized
 * and another window is selected, that new window will be maximized.
 * @see javax.swing.DefaultDesktopManager
 * @author Thomas Ball
 */
public class WindowsDesktopManager extends DefaultDesktopManager implements java.io.Serializable, javax.swing.plaf.UIResource {
  private WeakReference<JInternalFrame> currentFrameRef;
  public void activateFrame(  JInternalFrame f){
    JInternalFrame currentFrame=currentFrameRef != null ? currentFrameRef.get() : null;
    try {
      super.activateFrame(f);
      if (currentFrame != null && f != currentFrame) {
        if (currentFrame.isMaximum() && (f.getClientProperty("JInternalFrame.frameType") != "optionDialog")) {
          if (!currentFrame.isIcon()) {
            currentFrame.setMaximum(false);
            if (f.isMaximizable()) {
              if (!f.isMaximum()) {
                f.setMaximum(true);
              }
 else               if (f.isMaximum() && f.isIcon()) {
                f.setIcon(false);
              }
 else {
                f.setMaximum(false);
              }
            }
          }
        }
        if (currentFrame.isSelected()) {
          currentFrame.setSelected(false);
        }
      }
      if (!f.isSelected()) {
        f.setSelected(true);
      }
    }
 catch (    PropertyVetoException e) {
    }
    if (f != currentFrame) {
      currentFrameRef=new WeakReference<JInternalFrame>(f);
    }
  }
}
