package sun.awt.windows;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.BasicStroke;
import java.awt.Button;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.FileDialog;
import java.awt.Dialog;
import java.awt.Label;
import java.awt.Panel;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.awt.print.Pageable;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterJob;
import java.awt.print.PrinterException;
import javax.print.PrintService;
import java.io.IOException;
import java.io.File;
import java.util.Hashtable;
import java.util.Properties;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import sun.awt.Win32GraphicsEnvironment;
import sun.print.PeekGraphics;
import sun.print.PeekMetrics;
import java.net.URL;
import java.net.URI;
import java.net.URISyntaxException;
import javax.print.PrintServiceLookup;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.HashPrintServiceAttributeSet;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.Attribute;
import javax.print.attribute.standard.Sides;
import javax.print.attribute.standard.Chromaticity;
import javax.print.attribute.standard.PrintQuality;
import javax.print.attribute.standard.PrinterResolution;
import javax.print.attribute.standard.SheetCollate;
import javax.print.attribute.IntegerSyntax;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.Destination;
import javax.print.attribute.standard.OrientationRequested;
import javax.print.attribute.standard.Media;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.MediaSize;
import javax.print.attribute.standard.MediaTray;
import javax.print.attribute.standard.PrinterName;
import javax.print.attribute.standard.JobMediaSheetsSupported;
import javax.print.attribute.standard.PageRanges;
import javax.print.attribute.Size2DSyntax;
import javax.print.StreamPrintService;
import sun.awt.Win32FontManager;
import sun.print.RasterPrinterJob;
import sun.print.SunAlternateMedia;
import sun.print.SunPageSelection;
import sun.print.SunMinMaxPage;
import sun.print.Win32MediaTray;
import sun.print.Win32PrintService;
import sun.print.Win32PrintServiceLookup;
import sun.print.ServiceDialog;
import sun.print.DialogOwner;
import java.awt.Frame;
import java.io.FilePermission;
import sun.java2d.Disposer;
import sun.java2d.DisposerRecord;
import sun.java2d.DisposerTarget;
/** 
 * A class which initiates and executes a Win32 printer job.
 * @author Richard Blanchard
 */
public class WPrinterJob extends RasterPrinterJob implements DisposerTarget {
  /** 
 * These are Windows' ExtCreatePen End Cap Styles
 * and must match the values in <WINGDI.h>
 */
  protected static final long PS_ENDCAP_ROUND=0x00000000;
  protected static final long PS_ENDCAP_SQUARE=0x00000100;
  protected static final long PS_ENDCAP_FLAT=0x00000200;
  /** 
 * These are Windows' ExtCreatePen Line Join Styles
 * and must match the values in <WINGDI.h>
 */
  protected static final long PS_JOIN_ROUND=0x00000000;
  protected static final long PS_JOIN_BEVEL=0x00001000;
  protected static final long PS_JOIN_MITER=0x00002000;
  /** 
 * This is the Window's Polygon fill rule which
 * Selects alternate mode (fills the area between odd-numbered
 * and even-numbered polygon sides on each scan line).
 * It must match the value in <WINGDI.h> It can be passed
 * to setPolyFillMode().
 */
  protected static final int POLYFILL_ALTERNATE=1;
  /** 
 * This is the Window's Polygon fill rule which
 * Selects winding mode which fills any region
 * with a nonzero winding value). It must match
 * the value in <WINGDI.h> It can be passed
 * to setPolyFillMode().
 */
  protected static final int POLYFILL_WINDING=2;
  /** 
 * The maximum value for a Window's color component
 * as passed to selectSolidBrush.
 */
  private static final int MAX_WCOLOR=255;
  /** 
 * Flags for setting values from devmode in native code.
 * Values must match those defined in awt_PrintControl.cpp
 */
  private static final int SET_DUP_VERTICAL=0x00000010;
  private static final int SET_DUP_HORIZONTAL=0x00000020;
  private static final int SET_RES_HIGH=0x00000040;
  private static final int SET_RES_LOW=0x00000080;
  private static final int SET_COLOR=0x00000200;
  private static final int SET_ORIENTATION=0x00004000;
  /** 
 * Values must match those defined in wingdi.h & commdlg.h
 */
  private static final int PD_ALLPAGES=0x00000000;
  private static final int PD_SELECTION=0x00000001;
  private static final int PD_PAGENUMS=0x00000002;
  private static final int PD_NOSELECTION=0x00000004;
  private static final int PD_COLLATE=0x00000010;
  private static final int PD_PRINTTOFILE=0x00000020;
  private static final int DM_ORIENTATION=0x00000001;
  private static final int DM_PRINTQUALITY=0x00000400;
  private static final int DM_COLOR=0x00000800;
  private static final int DM_DUPLEX=0x00001000;
  /** 
 * Pageable MAX pages
 */
  private static final int MAX_UNKNOWN_PAGES=9999;
  private boolean driverDoesMultipleCopies=false;
  private boolean driverDoesCollation=false;
  private boolean userRequestedCollation=false;
  private boolean noDefaultPrinter=false;
static class HandleRecord implements DisposerRecord {
    /** 
 * The Windows device context we will print into.
 * This variable is set after the Print dialog
 * is okayed by the user. If the user cancels
 * the print dialog, then this variable is 0.
 * Much of the configuration information for a printer is
 * obtained through printer device specific handles.
 * We need to associate these with, and free with, the mPrintDC.
 */
    private long mPrintDC;
    private long mPrintHDevMode;
    private long mPrintHDevNames;
    public void dispose(){
      WPrinterJob.deleteDC(mPrintDC,mPrintHDevMode,mPrintHDevNames);
    }
  }
  private HandleRecord handleRecord=new HandleRecord();
  private int mPrintPaperSize;
  private int mPrintXRes;
  private int mPrintYRes;
  private int mPrintPhysX;
  private int mPrintPhysY;
  private int mPrintWidth;
  private int mPrintHeight;
  private int mPageWidth;
  private int mPageHeight;
  private int mAttSides;
  private int mAttChromaticity;
  private int mAttXRes;
  private int mAttYRes;
  private int mAttQuality;
  private int mAttCollate;
  private int mAttCopies;
  private int mAttMediaSizeName;
  private int mAttMediaTray;
  private String mDestination=null;
  /** 
 * The last color set into the print device context or
 * <code>null</code> if no color has been set.
 */
  private Color mLastColor;
  /** 
 * The last text color set into the print device context or
 * <code>null</code> if no color has been set.
 */
  private Color mLastTextColor;
  /** 
 * Define the most recent java font set as a GDI font in the printer
 * device context. mLastFontFamily will be NULL if no
 * GDI font has been set.
 */
  private String mLastFontFamily;
  private float mLastFontSize;
  private int mLastFontStyle;
  private int mLastRotation;
  private float mLastAwScale;
  private PrinterJob pjob;
  private java.awt.peer.ComponentPeer dialogOwnerPeer=null;
static {
    Toolkit.getDefaultToolkit();
    initIDs();
    Win32FontManager.registerJREFontsForPrinting();
  }
  public WPrinterJob(){
    Disposer.addRecord(disposerReferent,handleRecord=new HandleRecord());
    initAttributeMembers();
  }
  private Object disposerReferent=new Object();
  public Object getDisposerReferent(){
    return disposerReferent;
  }
  /** 
 * Display a dialog to the user allowing the modification of a
 * PageFormat instance.
 * The <code>page</code> argument is used to initialize controls
 * in the page setup dialog.
 * If the user cancels the dialog, then the method returns the
 * original <code>page</code> object unmodified.
 * If the user okays the dialog then the method returns a new
 * PageFormat object with the indicated changes.
 * In either case the original <code>page</code> object will
 * not be modified.
 * @param page    the default PageFormat presented to the user
 * for modification
 * @return    the original <code>page</code> object if the dialog
 * is cancelled, or a new PageFormat object containing
 * the format indicated by the user if the dialog is
 * acknowledged
 * @exception HeadlessException if GraphicsEnvironment.isHeadless()
 * returns true.
 * @see java.awt.GraphicsEnvironment#isHeadless
 * @since     JDK1.2
 */
  public PageFormat pageDialog(  PageFormat page) throws HeadlessException {
    if (GraphicsEnvironment.isHeadless()) {
      throw new HeadlessException();
    }
    if (getPrintService() instanceof StreamPrintService) {
      return super.pageDialog(page);
    }
    PageFormat pageClone=(PageFormat)page.clone();
    boolean result=false;
    WPageDialog dialog=new WPageDialog((Frame)null,this,pageClone,null);
    dialog.setRetVal(false);
    dialog.setVisible(true);
    result=dialog.getRetVal();
    dialog.dispose();
    if (result && (myService != null)) {
      String printerName=getNativePrintService();
      if (!myService.getName().equals(printerName)) {
        try {
          setPrintService(Win32PrintServiceLookup.getWin32PrintLUS().getPrintServiceByName(printerName));
        }
 catch (        PrinterException e) {
        }
      }
      updatePageAttributes(myService,pageClone);
      return pageClone;
    }
 else {
      return page;
    }
  }
  private boolean displayNativeDialog(){
    if (attributes == null) {
      return false;
    }
    DialogOwner dlgOwner=(DialogOwner)attributes.get(DialogOwner.class);
    Frame ownerFrame=(dlgOwner != null) ? dlgOwner.getOwner() : null;
    WPrintDialog dialog=new WPrintDialog(ownerFrame,this);
    dialog.setRetVal(false);
    dialog.setVisible(true);
    boolean prv=dialog.getRetVal();
    dialog.dispose();
    Destination dest=(Destination)attributes.get(Destination.class);
    if ((dest == null) || !prv) {
      return prv;
    }
 else {
      String title=null;
      String strBundle="sun.print.resources.serviceui";
      ResourceBundle rb=ResourceBundle.getBundle(strBundle);
      try {
        title=rb.getString("dialog.printtofile");
      }
 catch (      MissingResourceException e) {
      }
      FileDialog fileDialog=new FileDialog(ownerFrame,title,FileDialog.SAVE);
      URI destURI=dest.getURI();
      String pathName=(destURI != null) ? destURI.getSchemeSpecificPart() : null;
      if (pathName != null) {
        File file=new File(pathName);
        fileDialog.setFile(file.getName());
        File parent=file.getParentFile();
        if (parent != null) {
          fileDialog.setDirectory(parent.getPath());
        }
      }
 else {
        fileDialog.setFile("out.prn");
      }
      fileDialog.setVisible(true);
      String fileName=fileDialog.getFile();
      if (fileName == null) {
        fileDialog.dispose();
        return false;
      }
      String fullName=fileDialog.getDirectory() + fileName;
      File f=new File(fullName);
      File pFile=f.getParentFile();
      while ((f.exists() && (!f.isFile() || !f.canWrite())) || ((pFile != null) && (!pFile.exists() || (pFile.exists() && !pFile.canWrite())))) {
        (new PrintToFileErrorDialog(ownerFrame,ServiceDialog.getMsg("dialog.owtitle"),ServiceDialog.getMsg("dialog.writeerror") + " " + fullName,ServiceDialog.getMsg("button.ok"))).setVisible(true);
        fileDialog.setVisible(true);
        fileName=fileDialog.getFile();
        if (fileName == null) {
          fileDialog.dispose();
          return false;
        }
        fullName=fileDialog.getDirectory() + fileName;
        f=new File(fullName);
        pFile=f.getParentFile();
      }
      fileDialog.dispose();
      attributes.add(new Destination(f.toURI()));
      return true;
    }
  }
  /** 
 * Presents the user a dialog for changing properties of the
 * print job interactively.
 * @returns false if the user cancels the dialog and
 * true otherwise.
 * @exception HeadlessException if GraphicsEnvironment.isHeadless()
 * returns true.
 * @see java.awt.GraphicsEnvironment#isHeadless
 */
  public boolean printDialog() throws HeadlessException {
    if (GraphicsEnvironment.isHeadless()) {
      throw new HeadlessException();
    }
    if (attributes == null) {
      attributes=new HashPrintRequestAttributeSet();
    }
    if (getPrintService() instanceof StreamPrintService) {
      return super.printDialog(attributes);
    }
    if (noDefaultPrinter == true) {
      return false;
    }
 else {
      return displayNativeDialog();
    }
  }
  /** 
 * Associate this PrinterJob with a new PrintService.
 * Throws <code>PrinterException</code> if the specified service
 * cannot support the <code>Pageable</code> and
 * </code>Printable</code> interfaces necessary to support 2D printing.
 * @param a print service which supports 2D printing.
 * @throws PrinterException if the specified service does not support
 * 2D printing.
 */
  public void setPrintService(  PrintService service) throws PrinterException {
    super.setPrintService(service);
    if (service instanceof StreamPrintService) {
      return;
    }
    driverDoesMultipleCopies=false;
    driverDoesCollation=false;
    setNativePrintService(service.getName());
  }
  private native void setNativePrintService(  String name) throws PrinterException ;
  public PrintService getPrintService(){
    if (myService == null) {
      String printerName=getNativePrintService();
      if (printerName != null) {
        myService=Win32PrintServiceLookup.getWin32PrintLUS().getPrintServiceByName(printerName);
        if (myService != null) {
          return myService;
        }
      }
      myService=PrintServiceLookup.lookupDefaultPrintService();
      if (myService != null) {
        try {
          setNativePrintService(myService.getName());
        }
 catch (        Exception e) {
          myService=null;
        }
      }
    }
    return myService;
  }
  private native String getNativePrintService();
  private void initAttributeMembers(){
    mAttSides=0;
    mAttChromaticity=0;
    mAttXRes=0;
    mAttYRes=0;
    mAttQuality=0;
    mAttCollate=-1;
    mAttCopies=0;
    mAttMediaTray=0;
    mAttMediaSizeName=0;
    mDestination=null;
  }
  /** 
 * copy the attributes to the native print job
 * Note that this method, and hence the re-initialisation
 * of the GDI values is done on each entry to the print dialog since
 * an app could redisplay the print dialog for the same job and
 * 1) the application may have changed attribute settings
 * 2) the application may have changed the printer.
 * In the event that the user changes the printer using the
 * dialog, then it is up to GDI to report back all changed values.
 */
  protected void setAttributes(  PrintRequestAttributeSet attributes) throws PrinterException {
    initAttributeMembers();
    super.setAttributes(attributes);
    mAttCopies=getCopiesInt();
    mDestination=destinationAttr;
    if (attributes == null) {
      return;
    }
    Attribute[] attrs=attributes.toArray();
    for (int i=0; i < attrs.length; i++) {
      Attribute attr=attrs[i];
      try {
        if (attr.getCategory() == Sides.class) {
          setSidesAttrib(attr);
        }
 else         if (attr.getCategory() == Chromaticity.class) {
          setColorAttrib(attr);
        }
 else         if (attr.getCategory() == PrinterResolution.class) {
          setResolutionAttrib(attr);
        }
 else         if (attr.getCategory() == PrintQuality.class) {
          setQualityAttrib(attr);
        }
 else         if (attr.getCategory() == SheetCollate.class) {
          setCollateAttrib(attr);
        }
 else         if (attr.getCategory() == Media.class || attr.getCategory() == SunAlternateMedia.class) {
          if (attr.getCategory() == SunAlternateMedia.class) {
            Media media=(Media)attributes.get(Media.class);
            if (media == null || !(media instanceof MediaTray)) {
              attr=((SunAlternateMedia)attr).getMedia();
            }
          }
          if (attr instanceof MediaSizeName) {
            setWin32MediaAttrib(attr);
          }
          if (attr instanceof MediaTray) {
            setMediaTrayAttrib(attr);
          }
        }
      }
 catch (      ClassCastException e) {
      }
    }
  }
  /** 
 * Alters the orientation and Paper to match defaults obtained
 * from a printer.
 */
  private native void getDefaultPage(  PageFormat page);
  /** 
 * The passed in PageFormat will be copied and altered to describe
 * the default page size and orientation of the PrinterJob's
 * current printer.
 * Note: PageFormat.getPaper() returns a clone and getDefaultPage()
 * gets that clone so it won't overwrite the original paper.
 */
  public PageFormat defaultPage(  PageFormat page){
    PageFormat newPage=(PageFormat)page.clone();
    getDefaultPage(newPage);
    return newPage;
  }
  /** 
 * validate the paper size against the current printer.
 */
  protected native void validatePaper(  Paper origPaper,  Paper newPaper);
  /** 
 * Examine the metrics captured by the
 * <code>PeekGraphics</code> instance and
 * if capable of directly converting this
 * print job to the printer's control language
 * or the native OS's graphics primitives, then
 * return a <code>PathGraphics</code> to perform
 * that conversion. If there is not an object
 * capable of the conversion then return
 * <code>null</code>. Returning <code>null</code>
 * causes the print job to be rasterized.
 */
  protected Graphics2D createPathGraphics(  PeekGraphics peekGraphics,  PrinterJob printerJob,  Printable painter,  PageFormat pageFormat,  int pageIndex){
    WPathGraphics pathGraphics;
    PeekMetrics metrics=peekGraphics.getMetrics();
    if (forcePDL == false && (forceRaster == true || metrics.hasNonSolidColors() || metrics.hasCompositing())) {
      pathGraphics=null;
    }
 else {
      BufferedImage bufferedImage=new BufferedImage(8,8,BufferedImage.TYPE_INT_RGB);
      Graphics2D bufferedGraphics=bufferedImage.createGraphics();
      boolean canRedraw=peekGraphics.getAWTDrawingOnly() == false;
      pathGraphics=new WPathGraphics(bufferedGraphics,printerJob,painter,pageFormat,pageIndex,canRedraw);
    }
    return pathGraphics;
  }
  protected double getXRes(){
    if (mAttXRes != 0) {
      return mAttXRes;
    }
 else {
      return mPrintXRes;
    }
  }
  protected double getYRes(){
    if (mAttYRes != 0) {
      return mAttYRes;
    }
 else {
      return mPrintYRes;
    }
  }
  protected double getPhysicalPrintableX(  Paper p){
    return mPrintPhysX;
  }
  protected double getPhysicalPrintableY(  Paper p){
    return mPrintPhysY;
  }
  protected double getPhysicalPrintableWidth(  Paper p){
    return mPrintWidth;
  }
  protected double getPhysicalPrintableHeight(  Paper p){
    return mPrintHeight;
  }
  protected double getPhysicalPageWidth(  Paper p){
    return mPageWidth;
  }
  protected double getPhysicalPageHeight(  Paper p){
    return mPageHeight;
  }
  /** 
 * We don't (yet) provide API to support collation, and
 * when we do the logic here will require adjustment, but
 * this method is currently necessary to honour user-originated
 * collation requests - which can only originate from the print dialog.
 * REMIND: check if this can be deleted already.
 */
  protected boolean isCollated(){
    return userRequestedCollation;
  }
  /** 
 * Returns how many times the entire book should
 * be printed by the PrintJob. If the printer
 * itself supports collation then this method
 * should return 1 indicating that the entire
 * book need only be printed once and the copies
 * will be collated and made in the printer.
 */
  protected int getCollatedCopies(){
    debug_println("driverDoesMultipleCopies=" + driverDoesMultipleCopies + " driverDoesCollation="+ driverDoesCollation);
    if (super.isCollated() && !driverDoesCollation) {
      mAttCollate=0;
      mAttCopies=1;
      return getCopies();
    }
    return 1;
  }
  /** 
 * Returns how many times each page in the book
 * should be consecutively printed by PrinterJob.
 * If the underlying Window's driver will
 * generate the copies, rather than having RasterPrinterJob
 * iterate over the number of copies, this method always returns
 * 1.
 */
  protected int getNoncollatedCopies(){
    if (driverDoesMultipleCopies || super.isCollated()) {
      return 1;
    }
 else {
      return getCopies();
    }
  }
  /** 
 * Return the Window's device context that we are printing
 * into.
 */
  private long getPrintDC(){
    return handleRecord.mPrintDC;
  }
  private void setPrintDC(  long mPrintDC){
    handleRecord.mPrintDC=mPrintDC;
  }
  private long getDevMode(){
    return handleRecord.mPrintHDevMode;
  }
  private void setDevMode(  long mPrintHDevMode){
    handleRecord.mPrintHDevMode=mPrintHDevMode;
  }
  private long getDevNames(){
    return handleRecord.mPrintHDevNames;
  }
  private void setDevNames(  long mPrintHDevNames){
    handleRecord.mPrintHDevNames=mPrintHDevNames;
  }
  protected void beginPath(){
    beginPath(getPrintDC());
  }
  protected void endPath(){
    endPath(getPrintDC());
  }
  protected void closeFigure(){
    closeFigure(getPrintDC());
  }
  protected void fillPath(){
    fillPath(getPrintDC());
  }
  protected void moveTo(  float x,  float y){
    moveTo(getPrintDC(),x,y);
  }
  protected void lineTo(  float x,  float y){
    lineTo(getPrintDC(),x,y);
  }
  protected void polyBezierTo(  float control1x,  float control1y,  float control2x,  float control2y,  float endX,  float endY){
    polyBezierTo(getPrintDC(),control1x,control1y,control2x,control2y,endX,endY);
  }
  /** 
 * Set the current polgon fill rule into the printer device context.
 * The <code>fillRule</code> should
 * be one of the following Windows constants:
 * <code>ALTERNATE</code> or <code>WINDING</code>.
 */
  protected void setPolyFillMode(  int fillRule){
    setPolyFillMode(getPrintDC(),fillRule);
  }
  protected void selectSolidBrush(  Color color){
    if (color.equals(mLastColor) == false) {
      mLastColor=color;
      float[] rgb=color.getRGBColorComponents(null);
      selectSolidBrush(getPrintDC(),(int)(rgb[0] * MAX_WCOLOR),(int)(rgb[1] * MAX_WCOLOR),(int)(rgb[2] * MAX_WCOLOR));
    }
  }
  /** 
 * Return the x coordinate of the current pen
 * position in the print device context.
 */
  protected int getPenX(){
    return getPenX(getPrintDC());
  }
  /** 
 * Return the y coordinate of the current pen
 * position in the print device context.
 */
  protected int getPenY(){
    return getPenY(getPrintDC());
  }
  /** 
 * Set the current path in the printer device's
 * context to be clipping path.
 */
  protected void selectClipPath(){
    selectClipPath(getPrintDC());
  }
  protected void frameRect(  float x,  float y,  float width,  float height){
    frameRect(getPrintDC(),x,y,width,height);
  }
  protected void fillRect(  float x,  float y,  float width,  float height,  Color color){
    float[] rgb=color.getRGBColorComponents(null);
    fillRect(getPrintDC(),x,y,width,height,(int)(rgb[0] * MAX_WCOLOR),(int)(rgb[1] * MAX_WCOLOR),(int)(rgb[2] * MAX_WCOLOR));
  }
  protected void selectPen(  float width,  Color color){
    float[] rgb=color.getRGBColorComponents(null);
    selectPen(getPrintDC(),width,(int)(rgb[0] * MAX_WCOLOR),(int)(rgb[1] * MAX_WCOLOR),(int)(rgb[2] * MAX_WCOLOR));
  }
  protected boolean selectStylePen(  int cap,  int join,  float width,  Color color){
    long endCap;
    long lineJoin;
    float[] rgb=color.getRGBColorComponents(null);
switch (cap) {
case BasicStroke.CAP_BUTT:
      endCap=PS_ENDCAP_FLAT;
    break;
case BasicStroke.CAP_ROUND:
  endCap=PS_ENDCAP_ROUND;
break;
default :
case BasicStroke.CAP_SQUARE:
endCap=PS_ENDCAP_SQUARE;
break;
}
switch (join) {
case BasicStroke.JOIN_BEVEL:
lineJoin=PS_JOIN_BEVEL;
break;
default :
case BasicStroke.JOIN_MITER:
lineJoin=PS_JOIN_MITER;
break;
case BasicStroke.JOIN_ROUND:
lineJoin=PS_JOIN_ROUND;
break;
}
return (selectStylePen(getPrintDC(),endCap,lineJoin,width,(int)(rgb[0] * MAX_WCOLOR),(int)(rgb[1] * MAX_WCOLOR),(int)(rgb[2] * MAX_WCOLOR)));
}
/** 
 * Set a GDI font capable of drawing the java Font
 * passed in.
 */
protected boolean setFont(String family,float size,int style,int rotation,float awScale){
boolean didSetFont=true;
if (!family.equals(mLastFontFamily) || size != mLastFontSize || style != mLastFontStyle || rotation != mLastRotation || awScale != mLastAwScale) {
didSetFont=setFont(getPrintDC(),family,size,(style & Font.BOLD) != 0,(style & Font.ITALIC) != 0,rotation,awScale);
if (didSetFont) {
mLastFontFamily=family;
mLastFontSize=size;
mLastFontStyle=style;
mLastRotation=rotation;
mLastAwScale=awScale;
}
}
return didSetFont;
}
/** 
 * Set the GDI color for text drawing.
 */
protected void setTextColor(Color color){
if (color.equals(mLastTextColor) == false) {
mLastTextColor=color;
float[] rgb=color.getRGBColorComponents(null);
setTextColor(getPrintDC(),(int)(rgb[0] * MAX_WCOLOR),(int)(rgb[1] * MAX_WCOLOR),(int)(rgb[2] * MAX_WCOLOR));
}
}
/** 
 * Remove control characters.
 */
protected String removeControlChars(String str){
return super.removeControlChars(str);
}
/** 
 * Draw the string <code>text</code> to the printer's
 * device context at the specified position.
 */
protected void textOut(String str,float x,float y,float[] positions){
String text=removeControlChars(str);
assert (positions == null) || (text.length() == str.length());
if (text.length() == 0) {
return;
}
textOut(getPrintDC(),text,text.length(),false,x,y,positions);
}
/** 
 * Draw the glyphs <code>glyphs</code> to the printer's
 * device context at the specified position.
 */
protected void glyphsOut(int[] glyphs,float x,float y,float[] positions){
char[] glyphCharArray=new char[glyphs.length];
for (int i=0; i < glyphs.length; i++) {
glyphCharArray[i]=(char)(glyphs[i] & 0xffff);
}
String glyphStr=new String(glyphCharArray);
textOut(getPrintDC(),glyphStr,glyphs.length,true,x,y,positions);
}
/** 
 * Get the advance of this text that GDI returns for the
 * font currently selected into the GDI device context for
 * this job. Note that the removed control characters are
 * interpreted as zero-width by JDK and we remove them for
 * rendering so also remove them for measurement so that
 * this measurement can be properly compared with JDK measurement.
 */
protected int getGDIAdvance(String text){
text=removeControlChars(text);
if (text.length() == 0) {
return 0;
}
return getGDIAdvance(getPrintDC(),text);
}
/** 
 * Draw the 24 bit BGR image buffer represented by
 * <code>image</code> to the GDI device context
 * <code>printDC</code>. The image is drawn at
 * <code>(destX, destY)</code> in device coordinates.
 * The image is scaled into a square of size
 * specified by <code>destWidth</code> and
 * <code>destHeight</code>. The portion of the
 * source image copied into that square is specified
 * by <code>srcX</code>, <code>srcY</code>,
 * <code>srcWidth</code>, and srcHeight.
 */
protected void drawImage3ByteBGR(byte[] image,float destX,float destY,float destWidth,float destHeight,float srcX,float srcY,float srcWidth,float srcHeight){
drawDIBImage(getPrintDC(),image,destX,destY,destWidth,destHeight,srcX,srcY,srcWidth,srcHeight,24,null);
}
protected void drawDIBImage(byte[] image,float destX,float destY,float destWidth,float destHeight,float srcX,float srcY,float srcWidth,float srcHeight,int sampleBitsPerPixel,IndexColorModel icm){
int bitCount=24;
byte[] bmiColors=null;
if (icm != null) {
bitCount=sampleBitsPerPixel;
bmiColors=new byte[(1 << icm.getPixelSize()) * 4];
for (int i=0; i < icm.getMapSize(); i++) {
bmiColors[i * 4 + 0]=(byte)(icm.getBlue(i) & 0xff);
bmiColors[i * 4 + 1]=(byte)(icm.getGreen(i) & 0xff);
bmiColors[i * 4 + 2]=(byte)(icm.getRed(i) & 0xff);
}
}
drawDIBImage(getPrintDC(),image,destX,destY,destWidth,destHeight,srcX,srcY,srcWidth,srcHeight,bitCount,bmiColors);
}
/** 
 * Begin a new page.
 */
protected void startPage(PageFormat format,Printable painter,int index,boolean paperChanged){
invalidateCachedState();
deviceStartPage(format,painter,index,paperChanged);
}
/** 
 * End a page.
 */
protected void endPage(PageFormat format,Printable painter,int index){
deviceEndPage(format,painter,index);
}
/** 
 * Forget any device state we may have cached.
 */
private void invalidateCachedState(){
mLastColor=null;
mLastTextColor=null;
mLastFontFamily=null;
}
/** 
 * Set the number of copies to be printed.
 */
public void setCopies(int copies){
super.setCopies(copies);
mAttCopies=copies;
setNativeCopies(copies);
}
/** 
 * Set copies in device.
 */
public native void setNativeCopies(int copies);
/** 
 * Displays the print dialog and records the user's settings
 * into this object. Return false if the user cancels the
 * dialog.
 * If the dialog is to use a set of attributes, useAttributes is true.
 */
private native boolean jobSetup(Pageable doc,boolean allowPrintToFile);
protected native void initPrinter();
/** 
 * Call Window's StartDoc routine to begin a
 * print job. The DC from the print dialog is
 * used. If the print dialog was not displayed
 * then a DC for the default printer is created.
 * The native StartDoc returns false if the end-user cancelled
 * printing. This is possible if the printer is connected to FILE:
 * in which case windows queries the user for a destination and the
 * user may cancel out of it. Note that the implementation of
 * cancel() throws PrinterAbortException to indicate the user cancelled.
 */
private native boolean _startDoc(String dest,String jobName) throws PrinterException ;
protected void startDoc() throws PrinterException {
if (!_startDoc(mDestination,getJobName())) {
cancel();
}
}
/** 
 * Call Window's EndDoc routine to end a
 * print job.
 */
protected native void endDoc();
/** 
 * Call Window's AbortDoc routine to abort a
 * print job.
 */
protected native void abortDoc();
/** 
 * Call Windows native resource freeing APIs
 */
private static native void deleteDC(long dc,long devmode,long devnames);
/** 
 * Begin a new page. This call's Window's
 * StartPage routine.
 */
protected native void deviceStartPage(PageFormat format,Printable painter,int index,boolean paperChanged);
/** 
 * End a page. This call's Window's EndPage
 * routine.
 */
protected native void deviceEndPage(PageFormat format,Printable painter,int index);
/** 
 * Prints the contents of the array of ints, 'data'
 * to the current page. The band is placed at the
 * location (x, y) in device coordinates on the
 * page. The width and height of the band is
 * specified by the caller.
 */
protected native void printBand(byte[] data,int x,int y,int width,int height);
/** 
 * Begin a Window's rendering path in the device
 * context <code>printDC</code>.
 */
protected native void beginPath(long printDC);
/** 
 * End a Window's rendering path in the device
 * context <code>printDC</code>.
 */
protected native void endPath(long printDC);
/** 
 * Close a subpath in a Window's rendering path in the device
 * context <code>printDC</code>.
 */
protected native void closeFigure(long printDC);
/** 
 * Fill a defined Window's rendering path in the device
 * context <code>printDC</code>.
 */
protected native void fillPath(long printDC);
/** 
 * Move the Window's pen position to <code>(x,y)</code>
 * in the device context <code>printDC</code>.
 */
protected native void moveTo(long printDC,float x,float y);
/** 
 * Draw a line from the current pen position to
 * <code>(x,y)</code> in the device context <code>printDC</code>.
 */
protected native void lineTo(long printDC,float x,float y);
protected native void polyBezierTo(long printDC,float control1x,float control1y,float control2x,float control2y,float endX,float endY);
/** 
 * Set the current polgon fill rule into the device context
 * <code>printDC</code>. The <code>fillRule</code> should
 * be one of the following Windows constants:
 * <code>ALTERNATE</code> or <code>WINDING</code>.
 */
protected native void setPolyFillMode(long printDC,int fillRule);
/** 
 * Create a Window's solid brush for the color specified
 * by <code>(red, green, blue)</code>. Once the brush
 * is created, select it in the device
 * context <code>printDC</code> and free the old brush.
 */
protected native void selectSolidBrush(long printDC,int red,int green,int blue);
/** 
 * Return the x coordinate of the current pen
 * position in the device context
 * <code>printDC</code>.
 */
protected native int getPenX(long printDC);
/** 
 * Return the y coordinate of the current pen
 * position in the device context
 * <code>printDC</code>.
 */
protected native int getPenY(long printDC);
/** 
 * Select the device context's current path
 * to be the clipping path.
 */
protected native void selectClipPath(long printDC);
/** 
 * Draw a rectangle using specified brush.
 */
protected native void frameRect(long printDC,float x,float y,float width,float height);
/** 
 * Fill a rectangle specified by the coordinates using
 * specified brush.
 */
protected native void fillRect(long printDC,float x,float y,float width,float height,int red,int green,int blue);
/** 
 * Create a solid brush using the RG & B colors and width.
 * Select this brush and delete the old one.
 */
protected native void selectPen(long printDC,float width,int red,int green,int blue);
/** 
 * Create a solid brush using the RG & B colors and specified
 * pen styles.  Select this created brush and delete the old one.
 */
protected native boolean selectStylePen(long printDC,long cap,long join,float width,int red,int green,int blue);
/** 
 * Set a GDI font capable of drawing the java Font
 * passed in.
 */
protected native boolean setFont(long printDC,String familyName,float fontSize,boolean bold,boolean italic,int rotation,float awScale);
/** 
 * Set the GDI color for text drawing.
 */
protected native void setTextColor(long printDC,int red,int green,int blue);
/** 
 * Draw the string <code>text</code> into the device
 * context <code>printDC</code> at the specified
 * position.
 */
protected native void textOut(long printDC,String text,int strlen,boolean glyphs,float x,float y,float[] positions);
private native int getGDIAdvance(long printDC,String text);
/** 
 * Draw the DIB compatible image buffer represented by
 * <code>image</code> to the GDI device context
 * <code>printDC</code>. The image is drawn at
 * <code>(destX, destY)</code> in device coordinates.
 * The image is scaled into a square of size
 * specified by <code>destWidth</code> and
 * <code>destHeight</code>. The portion of the
 * source image copied into that square is specified
 * by <code>srcX</code>, <code>srcY</code>,
 * <code>srcWidth</code>, and srcHeight.
 * Note that the image isn't completely compatible with DIB format.
 * At the very least it needs to be padded so each scanline is
 * DWORD aligned. Also we "flip" the image to make it a bottom-up DIB.
 */
private native void drawDIBImage(long printDC,byte[] image,float destX,float destY,float destWidth,float destHeight,float srcX,float srcY,float srcWidth,float srcHeight,int bitCount,byte[] bmiColors);
private final String getPrinterAttrib(){
PrintService service=this.getPrintService();
String name=(service != null) ? service.getName() : null;
return name;
}
private final boolean getCollateAttrib(){
return (mAttCollate == 1);
}
private void setCollateAttrib(Attribute attr){
if (attr == SheetCollate.COLLATED) {
mAttCollate=1;
}
 else {
mAttCollate=0;
}
}
private void setCollateAttrib(Attribute attr,PrintRequestAttributeSet set){
setCollateAttrib(attr);
set.add(attr);
}
private final int getOrientAttrib(){
int orient=PageFormat.PORTRAIT;
OrientationRequested orientReq=(attributes == null) ? null : (OrientationRequested)attributes.get(OrientationRequested.class);
if (orientReq != null) {
if (orientReq == OrientationRequested.REVERSE_LANDSCAPE) {
orient=PageFormat.REVERSE_LANDSCAPE;
}
 else if (orientReq == OrientationRequested.LANDSCAPE) {
orient=PageFormat.LANDSCAPE;
}
}
return orient;
}
private void setOrientAttrib(Attribute attr,PrintRequestAttributeSet set){
if (set != null) {
set.add(attr);
}
}
private final int getCopiesAttrib(){
return getCopiesInt();
}
private final void setRangeCopiesAttribute(int from,int to,boolean isRangeSet,int copies){
if (attributes != null) {
if (isRangeSet) {
attributes.add(new PageRanges(from,to));
setPageRange(from,to);
}
attributes.add(new Copies(copies));
super.setCopies(copies);
mAttCopies=copies;
}
}
private final int getFromPageAttrib(){
if (attributes != null) {
PageRanges pageRangesAttr=(PageRanges)attributes.get(PageRanges.class);
if (pageRangesAttr != null) {
int[][] range=pageRangesAttr.getMembers();
return range[0][0];
}
}
return getMinPageAttrib();
}
private final int getToPageAttrib(){
if (attributes != null) {
PageRanges pageRangesAttr=(PageRanges)attributes.get(PageRanges.class);
if (pageRangesAttr != null) {
int[][] range=pageRangesAttr.getMembers();
return range[range.length - 1][1];
}
}
return getMaxPageAttrib();
}
private final int getMinPageAttrib(){
if (attributes != null) {
SunMinMaxPage s=(SunMinMaxPage)attributes.get(SunMinMaxPage.class);
if (s != null) {
return s.getMin();
}
}
return 1;
}
private final int getMaxPageAttrib(){
if (attributes != null) {
SunMinMaxPage s=(SunMinMaxPage)attributes.get(SunMinMaxPage.class);
if (s != null) {
return s.getMax();
}
}
Pageable pageable=getPageable();
if (pageable != null) {
int numPages=pageable.getNumberOfPages();
if (numPages <= Pageable.UNKNOWN_NUMBER_OF_PAGES) {
numPages=MAX_UNKNOWN_PAGES;
}
return ((numPages == 0) ? 1 : numPages);
}
return Integer.MAX_VALUE;
}
private final boolean getDestAttrib(){
return (mDestination != null);
}
private final int getQualityAttrib(){
return mAttQuality;
}
private void setQualityAttrib(Attribute attr){
if (attr == PrintQuality.HIGH) {
mAttQuality=-4;
}
 else if (attr == PrintQuality.NORMAL) {
mAttQuality=-3;
}
 else {
mAttQuality=-2;
}
}
private void setQualityAttrib(Attribute attr,PrintRequestAttributeSet set){
setQualityAttrib(attr);
set.add(attr);
}
private final int getColorAttrib(){
return mAttChromaticity;
}
private void setColorAttrib(Attribute attr){
if (attr == Chromaticity.COLOR) {
mAttChromaticity=2;
}
 else {
mAttChromaticity=1;
}
}
private void setColorAttrib(Attribute attr,PrintRequestAttributeSet set){
setColorAttrib(attr);
set.add(attr);
}
private final int getSidesAttrib(){
return mAttSides;
}
private void setSidesAttrib(Attribute attr){
if (attr == Sides.TWO_SIDED_LONG_EDGE) {
mAttSides=2;
}
 else if (attr == Sides.TWO_SIDED_SHORT_EDGE) {
mAttSides=3;
}
 else {
mAttSides=1;
}
}
private void setSidesAttrib(Attribute attr,PrintRequestAttributeSet set){
setSidesAttrib(attr);
set.add(attr);
}
/** 
 * MediaSizeName / dmPaper 
 */
private final int[] getWin32MediaAttrib(){
int wid_ht[]={0,0};
if (attributes != null) {
Media media=(Media)attributes.get(Media.class);
if (media instanceof MediaSizeName) {
MediaSizeName msn=(MediaSizeName)media;
MediaSize ms=MediaSize.getMediaSizeForName(msn);
if (ms != null) {
wid_ht[0]=(int)(ms.getX(MediaSize.INCH) * 72.0);
wid_ht[1]=(int)(ms.getY(MediaSize.INCH) * 72.0);
}
}
}
return wid_ht;
}
private void setWin32MediaAttrib(Attribute attr){
if (!(attr instanceof MediaSizeName)) {
return;
}
MediaSizeName msn=(MediaSizeName)attr;
mAttMediaSizeName=((Win32PrintService)myService).findPaperID(msn);
}
private void setWin32MediaAttrib(int dmIndex,int width,int length){
MediaSizeName msn=((Win32PrintService)myService).findWin32Media(dmIndex);
if (msn == null) {
msn=((Win32PrintService)myService).findMatchingMediaSizeNameMM((float)width,(float)length);
}
if (msn != null) {
if (attributes != null) {
attributes.add(msn);
}
}
mAttMediaSizeName=dmIndex;
}
private void setMediaTrayAttrib(Attribute attr){
if (attr == MediaTray.BOTTOM) {
mAttMediaTray=2;
}
 else if (attr == MediaTray.ENVELOPE) {
mAttMediaTray=5;
}
 else if (attr == MediaTray.LARGE_CAPACITY) {
mAttMediaTray=11;
}
 else if (attr == MediaTray.MAIN) {
mAttMediaTray=1;
}
 else if (attr == MediaTray.MANUAL) {
mAttMediaTray=4;
}
 else if (attr == MediaTray.MIDDLE) {
mAttMediaTray=3;
}
 else if (attr == MediaTray.SIDE) {
mAttMediaTray=7;
}
 else if (attr == MediaTray.TOP) {
mAttMediaTray=1;
}
 else {
if (attr instanceof Win32MediaTray) {
mAttMediaTray=((Win32MediaTray)attr).winID;
}
 else {
mAttMediaTray=1;
}
}
}
private void setMediaTrayAttrib(int dmBinID){
mAttMediaTray=dmBinID;
MediaTray tray=((Win32PrintService)myService).findMediaTray(dmBinID);
}
private int getMediaTrayAttrib(){
return mAttMediaTray;
}
private final int getSelectAttrib(){
if (attributes != null) {
SunPageSelection pages=(SunPageSelection)attributes.get(SunPageSelection.class);
if (pages == SunPageSelection.RANGE) {
return PD_PAGENUMS;
}
 else if (pages == SunPageSelection.SELECTION) {
return PD_SELECTION;
}
 else if (pages == SunPageSelection.ALL) {
return PD_ALLPAGES;
}
}
return PD_NOSELECTION;
}
private final boolean getPrintToFileEnabled(){
SecurityManager security=System.getSecurityManager();
if (security != null) {
FilePermission printToFilePermission=new FilePermission("<<ALL FILES>>","read,write");
try {
security.checkPermission(printToFilePermission);
}
 catch (SecurityException e) {
return false;
}
}
return true;
}
private final void setNativeAttributes(int flags,int fields,int values){
if (attributes == null) {
return;
}
if ((flags & PD_PRINTTOFILE) != 0) {
Destination destPrn=(Destination)attributes.get(Destination.class);
if (destPrn == null) {
try {
attributes.add(new Destination(new File("./out.prn").toURI()));
}
 catch (SecurityException se) {
try {
attributes.add(new Destination(new URI("file:out.prn")));
}
 catch (URISyntaxException e) {
}
}
}
}
 else {
attributes.remove(Destination.class);
}
if ((flags & PD_COLLATE) != 0) {
setCollateAttrib(SheetCollate.COLLATED,attributes);
}
 else {
setCollateAttrib(SheetCollate.UNCOLLATED,attributes);
}
if ((flags & PD_PAGENUMS) != 0) {
attributes.add(SunPageSelection.RANGE);
}
 else if ((flags & PD_SELECTION) != 0) {
attributes.add(SunPageSelection.SELECTION);
}
 else {
attributes.add(SunPageSelection.ALL);
}
if ((fields & DM_ORIENTATION) != 0) {
if ((values & SET_ORIENTATION) != 0) {
setOrientAttrib(OrientationRequested.LANDSCAPE,attributes);
}
 else {
setOrientAttrib(OrientationRequested.PORTRAIT,attributes);
}
}
if ((fields & DM_COLOR) != 0) {
if ((values & SET_COLOR) != 0) {
setColorAttrib(Chromaticity.COLOR,attributes);
}
 else {
setColorAttrib(Chromaticity.MONOCHROME,attributes);
}
}
if ((fields & DM_PRINTQUALITY) != 0) {
PrintQuality quality;
if ((values & SET_RES_LOW) != 0) {
quality=PrintQuality.DRAFT;
}
 else if ((fields & SET_RES_HIGH) != 0) {
quality=PrintQuality.HIGH;
}
 else {
quality=PrintQuality.NORMAL;
}
setQualityAttrib(quality,attributes);
}
if ((fields & DM_DUPLEX) != 0) {
Sides sides;
if ((values & SET_DUP_VERTICAL) != 0) {
sides=Sides.TWO_SIDED_LONG_EDGE;
}
 else if ((values & SET_DUP_HORIZONTAL) != 0) {
sides=Sides.TWO_SIDED_SHORT_EDGE;
}
 else {
sides=Sides.ONE_SIDED;
}
setSidesAttrib(sides,attributes);
}
}
private final void setResolutionDPI(int xres,int yres){
if (attributes != null) {
PrinterResolution res=new PrinterResolution(xres,yres,PrinterResolution.DPI);
attributes.add(res);
}
mAttXRes=xres;
mAttYRes=yres;
}
private void setResolutionAttrib(Attribute attr){
PrinterResolution pr=(PrinterResolution)attr;
mAttXRes=pr.getCrossFeedResolution(PrinterResolution.DPI);
mAttYRes=pr.getFeedResolution(PrinterResolution.DPI);
}
private void setPrinterNameAttrib(String printerName){
PrintService service=this.getPrintService();
if (printerName == null) {
return;
}
if (service != null && printerName.equals(service.getName())) {
return;
}
 else {
PrintService[] services=PrinterJob.lookupPrintServices();
for (int i=0; i < services.length; i++) {
if (printerName.equals(services[i].getName())) {
try {
this.setPrintService(services[i]);
}
 catch (PrinterException e) {
}
return;
}
}
}
}
class PrintToFileErrorDialog extends Dialog implements ActionListener {
public PrintToFileErrorDialog(Frame parent,String title,String message,String buttonText){
super(parent,title,true);
init(parent,title,message,buttonText);
}
public PrintToFileErrorDialog(Dialog parent,String title,String message,String buttonText){
super(parent,title,true);
init(parent,title,message,buttonText);
}
private void init(Component parent,String title,String message,String buttonText){
Panel p=new Panel();
add("Center",new Label(message));
Button btn=new Button(buttonText);
btn.addActionListener(this);
p.add(btn);
add("South",p);
pack();
Dimension dDim=getSize();
if (parent != null) {
Rectangle fRect=parent.getBounds();
setLocation(fRect.x + ((fRect.width - dDim.width) / 2),fRect.y + ((fRect.height - dDim.height) / 2));
}
}
public void actionPerformed(ActionEvent event){
setVisible(false);
dispose();
return;
}
}
/** 
 * Initialize JNI field and method ids
 */
private static native void initIDs();
}
