package sun.awt.X11;
final public class XUtilConstants {
  private XUtilConstants(){
  }
  public static final int NoValue=0x0000;
  public static final int XValue=0x0001;
  public static final int YValue=0x0002;
  public static final int WidthValue=0x0004;
  public static final int HeightValue=0x0008;
  public static final int AllValues=0x000F;
  public static final int XNegative=0x0010;
  public static final int YNegative=0x0020;
  public static final long USPosition=1L << 0;
  public static final long USSize=1L << 1;
  public static final long PPosition=1L << 2;
  public static final long PSize=1L << 3;
  public static final long PMinSize=1L << 4;
  public static final long PMaxSize=1L << 5;
  public static final long PResizeInc=1L << 6;
  public static final long PAspect=1L << 7;
  public static final long PBaseSize=1L << 8;
  public static final long PWinGravity=1L << 9;
  public static final long PAllHints=(PPosition | PSize | PMinSize| PMaxSize| PResizeInc| PAspect);
  public static final long InputHint=1L << 0;
  public static final long StateHint=1L << 1;
  public static final long IconPixmapHint=1L << 2;
  public static final long IconWindowHint=1L << 3;
  public static final long IconPositionHint=1L << 4;
  public static final long IconMaskHint=1L << 5;
  public static final long WindowGroupHint=1L << 6;
  public static final long AllHints=(InputHint | StateHint | IconPixmapHint| IconWindowHint| IconPositionHint| IconMaskHint| WindowGroupHint);
  public static final long XUrgencyHint=1L << 8;
  public static final int WithdrawnState=0;
  public static final int NormalState=1;
  public static final int IconicState=3;
  public static final int DontCareState=0;
  public static final int ZoomState=2;
  public static final int InactiveState=4;
  public static final int XNoMemory=-1;
  public static final int XLocaleNotSupported=-2;
  public static final int XConverterNotFound=-3;
  public static final int RectangleOut=0;
  public static final int RectangleIn=1;
  public static final int RectanglePart=2;
  public static final int VisualNoMask=0x0;
  public static final int VisualIDMask=0x1;
  public static final int VisualScreenMask=0x2;
  public static final int VisualDepthMask=0x4;
  public static final int VisualClassMask=0x8;
  public static final int VisualRedMaskMask=0x10;
  public static final int VisualGreenMaskMask=0x20;
  public static final int VisualBlueMaskMask=0x40;
  public static final int VisualColormapSizeMask=0x80;
  public static final int VisualBitsPerRGBMask=0x100;
  public static final int VisualAllMask=0x1FF;
  public static final int BitmapSuccess=0;
  public static final int BitmapOpenFailed=1;
  public static final int BitmapFileInvalid=2;
  public static final int BitmapNoMemory=3;
  /** 
 * Context Management
 */
  public static final int XCSUCCESS=0;
  public static final int XCNOMEM=1;
  public static final int XCNOENT=2;
}
