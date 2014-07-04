package edu.umd.cs.piccolox.swing;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Rectangle;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneLayout;
import javax.swing.border.Border;
import edu.umd.cs.piccolo.util.PBounds;
/** 
 * A subclass of ScrollPaneLayout that looks at the Viewport for sizing
 * information rather than View. Also queries the Viewport for sizing
 * information after each decision about scrollbar visiblity.
 * @author Lance Good
 */
public class PScrollPaneLayout extends ScrollPaneLayout {
  private static final long serialVersionUID=1L;
  /** 
 * MODIFIED FROM javax.swing.ScrollPaneLayout.layoutContainer.
 * This is largely the same as ScrollPaneLayout.layoutContainer but obtains
 * the preferred view size from the viewport rather than directly from the
 * view so the viewport can get the preferred size from the PScrollDirector
 * @param parent the Container to lay out
 */
  public void layoutContainer(  final Container parent){
    if (!(parent instanceof JScrollPane)) {
      throw new IllegalArgumentException("layoutContainer may only be applied to JScrollPanes");
    }
    final JScrollPane scrollPane=(JScrollPane)parent;
    vsbPolicy=scrollPane.getVerticalScrollBarPolicy();
    hsbPolicy=scrollPane.getHorizontalScrollBarPolicy();
    final Rectangle availR=scrollPane.getBounds();
    availR.setLocation(0,0);
    final Insets insets=parent.getInsets();
    availR.x=insets.left;
    availR.y=insets.top;
    availR.width-=insets.left + insets.right;
    availR.height-=insets.top + insets.bottom;
    final boolean leftToRight=scrollPane.getComponentOrientation().isLeftToRight();
    final Rectangle colHeadR=new Rectangle(0,availR.y,0,0);
    if (colHead != null && colHead.isVisible()) {
      final int colHeadHeight=colHead.getPreferredSize().height;
      colHeadR.height=colHeadHeight;
      availR.y+=colHeadHeight;
      availR.height-=colHeadHeight;
    }
    final Rectangle rowHeadR=new Rectangle(0,0,0,0);
    if (rowHead != null && rowHead.isVisible()) {
      final int rowHeadWidth=rowHead.getPreferredSize().width;
      rowHeadR.width=rowHeadWidth;
      availR.width-=rowHeadWidth;
      if (leftToRight) {
        rowHeadR.x=availR.x;
        availR.x+=rowHeadWidth;
      }
 else {
        rowHeadR.x=availR.x + availR.width;
      }
    }
    final Border viewportBorder=scrollPane.getViewportBorder();
    Insets vpbInsets;
    if (viewportBorder != null) {
      vpbInsets=viewportBorder.getBorderInsets(parent);
      availR.x+=vpbInsets.left;
      availR.y+=vpbInsets.top;
      availR.width-=vpbInsets.left + vpbInsets.right;
      availR.height-=vpbInsets.top + vpbInsets.bottom;
    }
 else {
      vpbInsets=new Insets(0,0,0,0);
    }
    Dimension extentSize=getExtentSize(availR);
    final PBounds cameraBounds=new PBounds(0,0,extentSize.getWidth(),extentSize.getHeight());
    Dimension viewPrefSize=getViewSize(cameraBounds);
    final Rectangle vsbR=new Rectangle(0,availR.y - vpbInsets.top,0,0);
    boolean vsbNeeded;
    if (vsbPolicy == VERTICAL_SCROLLBAR_ALWAYS) {
      vsbNeeded=true;
    }
 else     if (vsbPolicy == VERTICAL_SCROLLBAR_NEVER) {
      vsbNeeded=false;
    }
 else {
      vsbNeeded=viewPrefSize.height > extentSize.height;
    }
    if (vsb != null && vsbNeeded) {
      adjustForVSB(true,availR,vsbR,vpbInsets,leftToRight);
      extentSize=viewport.toViewCoordinates(availR.getSize());
      cameraBounds.setRect(0,0,extentSize.getWidth(),extentSize.getHeight());
      viewPrefSize=((PViewport)viewport).getViewSize(cameraBounds);
    }
    final Rectangle hsbR=new Rectangle(availR.x - vpbInsets.left,0,0,0);
    boolean hsbNeeded;
    if (hsbPolicy == HORIZONTAL_SCROLLBAR_ALWAYS) {
      hsbNeeded=true;
    }
 else     if (hsbPolicy == HORIZONTAL_SCROLLBAR_NEVER) {
      hsbNeeded=false;
    }
 else {
      hsbNeeded=viewPrefSize.width > extentSize.width;
    }
    if (hsb != null && hsbNeeded) {
      adjustForHSB(true,availR,hsbR,vpbInsets);
      if (vsb != null && !vsbNeeded && vsbPolicy != VERTICAL_SCROLLBAR_NEVER) {
        extentSize=viewport.toViewCoordinates(availR.getSize());
        cameraBounds.setRect(0,0,extentSize.getWidth(),extentSize.getHeight());
        viewPrefSize=((PViewport)viewport).getViewSize(cameraBounds);
        vsbNeeded=viewPrefSize.height > extentSize.height;
        if (vsbNeeded) {
          adjustForVSB(true,availR,vsbR,vpbInsets,leftToRight);
        }
      }
    }
    if (viewport != null) {
      viewport.setBounds(availR);
    }
    vsbR.height=availR.height + vpbInsets.top + vpbInsets.bottom;
    hsbR.width=availR.width + vpbInsets.left + vpbInsets.right;
    rowHeadR.height=availR.height + vpbInsets.top + vpbInsets.bottom;
    rowHeadR.y=availR.y - vpbInsets.top;
    colHeadR.width=availR.width + vpbInsets.left + vpbInsets.right;
    colHeadR.x=availR.x - vpbInsets.left;
    if (rowHead != null) {
      rowHead.setBounds(rowHeadR);
    }
    if (colHead != null) {
      colHead.setBounds(colHeadR);
    }
    if (vsb != null) {
      if (vsbNeeded) {
        vsb.setVisible(true);
        vsb.setBounds(vsbR);
      }
 else {
        vsb.setVisible(false);
      }
    }
    if (hsb != null) {
      if (hsbNeeded) {
        hsb.setVisible(true);
        hsb.setBounds(hsbR);
      }
 else {
        hsb.setVisible(false);
      }
    }
    if (lowerLeft != null) {
      if (leftToRight) {
        lowerLeft.setBounds(rowHeadR.x,hsbR.y,rowHeadR.width,hsbR.height);
      }
 else {
        lowerLeft.setBounds(vsbR.x,hsbR.y,vsbR.width,hsbR.height);
      }
    }
    if (lowerRight != null) {
      if (leftToRight) {
        lowerRight.setBounds(vsbR.x,hsbR.y,vsbR.width,hsbR.height);
      }
 else {
        lowerRight.setBounds(rowHeadR.x,hsbR.y,rowHeadR.width,hsbR.height);
      }
    }
    if (upperLeft != null) {
      if (leftToRight) {
        upperLeft.setBounds(rowHeadR.x,colHeadR.y,rowHeadR.width,colHeadR.height);
      }
 else {
        upperLeft.setBounds(vsbR.x,colHeadR.y,vsbR.width,colHeadR.height);
      }
    }
    if (upperRight != null) {
      if (leftToRight) {
        upperRight.setBounds(vsbR.x,colHeadR.y,vsbR.width,colHeadR.height);
      }
 else {
        upperRight.setBounds(rowHeadR.x,colHeadR.y,rowHeadR.width,colHeadR.height);
      }
    }
  }
  /** 
 * @param cameraBounds
 * @return
 */
  private Dimension getViewSize(  final PBounds cameraBounds){
    Dimension viewPrefSize;
    if (viewport != null) {
      viewPrefSize=((PViewport)viewport).getViewSize(cameraBounds);
    }
 else {
      viewPrefSize=new Dimension(0,0);
    }
    return viewPrefSize;
  }
  /** 
 * @param availR
 * @return
 */
  private Dimension getExtentSize(  final Rectangle availR){
    Dimension extentSize;
    if (viewport != null) {
      extentSize=viewport.toViewCoordinates(availR.getSize());
    }
 else {
      extentSize=new Dimension(0,0);
    }
    return extentSize;
  }
  /** 
 * Copied FROM javax.swing.ScrollPaneLayout.adjustForVSB.
 * This method is called from ScrollPaneLayout.layoutContainer and is
 * private in ScrollPaneLayout so it was copied here
 * @param wantsVSB whether to account for vertical scrollbar
 * @param available region to adjust
 * @param vsbR vertical scroll bar region
 * @param vpbInsets margin of vertical scroll bars
 * @param leftToRight orientation of the text LTR or RTL
 */
  protected void adjustForVSB(  final boolean wantsVSB,  final Rectangle available,  final Rectangle vsbR,  final Insets vpbInsets,  final boolean leftToRight){
    final int vsbWidth=vsb.getPreferredSize().width;
    if (wantsVSB) {
      available.width-=vsbWidth;
      vsbR.width=vsbWidth;
      if (leftToRight) {
        vsbR.x=available.x + available.width + vpbInsets.right;
      }
 else {
        vsbR.x=available.x - vpbInsets.left;
        available.x+=vsbWidth;
      }
    }
 else {
      available.width+=vsbWidth;
    }
  }
  /** 
 * Copied FROM javax.swing.ScrollPaneLayout.adjustForHSB.
 * This method is called from ScrollPaneLayout.layoutContainer and is
 * private in ScrollPaneLayout so it was copied here
 * @param wantsHSB whether to account for horizontal scrollbar
 * @param available region to adjust
 * @param hsbR vertical scroll bar region
 * @param vpbInsets margin of the scroll bars
 */
  protected void adjustForHSB(  final boolean wantsHSB,  final Rectangle available,  final Rectangle hsbR,  final Insets vpbInsets){
    final int hsbHeight=hsb.getPreferredSize().height;
    if (wantsHSB) {
      available.height-=hsbHeight;
      hsbR.y=available.y + available.height + vpbInsets.bottom;
      hsbR.height=hsbHeight;
    }
 else {
      available.height+=hsbHeight;
    }
  }
  /** 
 * The UI resource version of PScrollPaneLayout. It isn't clear why Swing
 * does this in ScrollPaneLayout but we'll do it here too just to be safe.
 */
public static class UIResource extends PScrollPaneLayout implements javax.swing.plaf.UIResource {
    /** 
 */
    private static final long serialVersionUID=1L;
  }
}
