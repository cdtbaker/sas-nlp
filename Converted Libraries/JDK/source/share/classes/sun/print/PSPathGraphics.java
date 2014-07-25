package sun.print;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Shape;
import java.awt.Transparency;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import sun.awt.image.ByteComponentRaster;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
/** 
 * This class converts paths into PostScript
 * by breaking all graphics into fills and
 * clips of paths.
 */
class PSPathGraphics extends PathGraphics {
  /** 
 * For a drawing application the initial user space
 * resolution is 72dpi.
 */
  private static final int DEFAULT_USER_RES=72;
  PSPathGraphics(  Graphics2D graphics,  PrinterJob printerJob,  Printable painter,  PageFormat pageFormat,  int pageIndex,  boolean canRedraw){
    super(graphics,printerJob,painter,pageFormat,pageIndex,canRedraw);
  }
  /** 
 * Creates a new <code>Graphics</code> object that is
 * a copy of this <code>Graphics</code> object.
 * @return     a new graphics context that is a copy of
 * this graphics context.
 * @since      JDK1.0
 */
  public Graphics create(){
    return new PSPathGraphics((Graphics2D)getDelegate().create(),getPrinterJob(),getPrintable(),getPageFormat(),getPageIndex(),canDoRedraws());
  }
  /** 
 * Override the inherited implementation of fill
 * so that we can generate PostScript in user space
 * rather than device space.
 */
  public void fill(  Shape s,  Color color){
    deviceFill(s.getPathIterator(new AffineTransform()),color);
  }
  /** 
 * Draws the text given by the specified string, using this
 * graphics context's current font and color. The baseline of the
 * first character is at position (<i>x</i>,&nbsp;<i>y</i>) in this
 * graphics context's coordinate system.
 * @param str      the string to be drawn.
 * @param x        the <i>x</i> coordinate.
 * @param y        the <i>y</i> coordinate.
 * @see java.awt.Graphics#drawBytes
 * @see java.awt.Graphics#drawChars
 * @since       JDK1.0
 */
  public void drawString(  String str,  int x,  int y){
    drawString(str,(float)x,(float)y);
  }
  /** 
 * Renders the text specified by the specified <code>String</code>,
 * using the current <code>Font</code> and <code>Paint</code> attributes
 * in the <code>Graphics2D</code> context.
 * The baseline of the first character is at position
 * (<i>x</i>,&nbsp;<i>y</i>) in the User Space.
 * The rendering attributes applied include the <code>Clip</code>,
 * <code>Transform</code>, <code>Paint</code>, <code>Font</code> and
 * <code>Composite</code> attributes. For characters in script systems
 * such as Hebrew and Arabic, the glyphs can be rendered from right to
 * left, in which case the coordinate supplied is the location of the
 * leftmost character on the baseline.
 * @param s the <code>String</code> to be rendered
 * @param x,&nbsp;y the coordinates where the <code>String</code>
 * should be rendered
 * @see #setPaint
 * @see java.awt.Graphics#setColor
 * @see java.awt.Graphics#setFont
 * @see #setTransform
 * @see #setComposite
 * @see #setClip
 */
  public void drawString(  String str,  float x,  float y){
    drawString(str,x,y,getFont(),getFontRenderContext(),0f);
  }
  protected boolean canDrawStringToWidth(){
    return true;
  }
  protected int platformFontCount(  Font font,  String str){
    PSPrinterJob psPrinterJob=(PSPrinterJob)getPrinterJob();
    return psPrinterJob.platformFontCount(font,str);
  }
  protected void drawString(  String str,  float x,  float y,  Font font,  FontRenderContext frc,  float w){
    if (str.length() == 0) {
      return;
    }
    if (font.hasLayoutAttributes() && !printingGlyphVector) {
      TextLayout layout=new TextLayout(str,font,frc);
      layout.draw(this,x,y);
      return;
    }
    Font oldFont=getFont();
    if (!oldFont.equals(font)) {
      setFont(font);
    }
 else {
      oldFont=null;
    }
    boolean drawnWithPS=false;
    float translateX=0f, translateY=0f;
    boolean fontisTransformed=getFont().isTransformed();
    if (fontisTransformed) {
      AffineTransform fontTx=getFont().getTransform();
      int transformType=fontTx.getType();
      if (transformType == AffineTransform.TYPE_TRANSLATION) {
        translateX=(float)(fontTx.getTranslateX());
        translateY=(float)(fontTx.getTranslateY());
        if (Math.abs(translateX) < 0.00001)         translateX=0f;
        if (Math.abs(translateY) < 0.00001)         translateY=0f;
        fontisTransformed=false;
      }
    }
    boolean directToPS=!fontisTransformed;
    if (!PSPrinterJob.shapeTextProp && directToPS) {
      PSPrinterJob psPrinterJob=(PSPrinterJob)getPrinterJob();
      if (psPrinterJob.setFont(getFont())) {
        try {
          psPrinterJob.setColor((Color)getPaint());
        }
 catch (        ClassCastException e) {
          if (oldFont != null) {
            setFont(oldFont);
          }
          throw new IllegalArgumentException("Expected a Color instance");
        }
        psPrinterJob.setTransform(getTransform());
        psPrinterJob.setClip(getClip());
        drawnWithPS=psPrinterJob.textOut(this,str,x + translateX,y + translateY,font,frc,w);
      }
    }
    if (drawnWithPS == false) {
      if (oldFont != null) {
        setFont(oldFont);
        oldFont=null;
      }
      super.drawString(str,x,y,font,frc,w);
    }
    if (oldFont != null) {
      setFont(oldFont);
    }
  }
  /** 
 * The various <code>drawImage()</code> methods for
 * <code>WPathGraphics</code> are all decomposed
 * into an invocation of <code>drawImageToPlatform</code>.
 * The portion of the passed in image defined by
 * <code>srcX, srcY, srcWidth, and srcHeight</code>
 * is transformed by the supplied AffineTransform and
 * drawn using PS to the printer context.
 * @param img     The image to be drawn.
 * This method does nothing if <code>img</code> is null.
 * @param xform   Used to tranform the image before drawing.
 * This can be null.
 * @param bgcolor This color is drawn where the image has transparent
 * pixels. If this parameter is null then the
 * pixels already in the destination should show
 * through.
 * @param srcX    With srcY this defines the upper-left corner
 * of the portion of the image to be drawn.
 * @param srcY    With srcX this defines the upper-left corner
 * of the portion of the image to be drawn.
 * @param srcWidth    The width of the portion of the image to
 * be drawn.
 * @param srcHeight   The height of the portion of the image to
 * be drawn.
 * @param handlingTransparency if being recursively called to
 * print opaque region of transparent image
 */
  protected boolean drawImageToPlatform(  Image image,  AffineTransform xform,  Color bgcolor,  int srcX,  int srcY,  int srcWidth,  int srcHeight,  boolean handlingTransparency){
    BufferedImage img=getBufferedImage(image);
    if (img == null) {
      return true;
    }
    PSPrinterJob psPrinterJob=(PSPrinterJob)getPrinterJob();
    AffineTransform fullTransform=getTransform();
    if (xform == null) {
      xform=new AffineTransform();
    }
    fullTransform.concatenate(xform);
    double[] fullMatrix=new double[6];
    fullTransform.getMatrix(fullMatrix);
    Point2D.Float unitVectorX=new Point2D.Float(1,0);
    Point2D.Float unitVectorY=new Point2D.Float(0,1);
    fullTransform.deltaTransform(unitVectorX,unitVectorX);
    fullTransform.deltaTransform(unitVectorY,unitVectorY);
    Point2D.Float origin=new Point2D.Float(0,0);
    double scaleX=unitVectorX.distance(origin);
    double scaleY=unitVectorY.distance(origin);
    double devResX=psPrinterJob.getXRes();
    double devResY=psPrinterJob.getYRes();
    double devScaleX=devResX / DEFAULT_USER_RES;
    double devScaleY=devResY / DEFAULT_USER_RES;
    int transformType=fullTransform.getType();
    boolean clampScale=((transformType & (AffineTransform.TYPE_GENERAL_ROTATION | AffineTransform.TYPE_GENERAL_TRANSFORM)) != 0);
    if (clampScale) {
      if (scaleX > devScaleX)       scaleX=devScaleX;
      if (scaleY > devScaleY)       scaleY=devScaleY;
    }
    if (scaleX != 0 && scaleY != 0) {
      AffineTransform rotTransform=new AffineTransform(fullMatrix[0] / scaleX,fullMatrix[1] / scaleY,fullMatrix[2] / scaleX,fullMatrix[3] / scaleY,fullMatrix[4] / scaleX,fullMatrix[5] / scaleY);
      Rectangle2D.Float srcRect=new Rectangle2D.Float(srcX,srcY,srcWidth,srcHeight);
      Shape rotShape=rotTransform.createTransformedShape(srcRect);
      Rectangle2D rotBounds=rotShape.getBounds2D();
      rotBounds.setRect(rotBounds.getX(),rotBounds.getY(),rotBounds.getWidth() + 0.001,rotBounds.getHeight() + 0.001);
      int boundsWidth=(int)rotBounds.getWidth();
      int boundsHeight=(int)rotBounds.getHeight();
      if (boundsWidth > 0 && boundsHeight > 0) {
        boolean drawOpaque=true;
        if (!handlingTransparency && hasTransparentPixels(img)) {
          drawOpaque=false;
          if (isBitmaskTransparency(img)) {
            if (bgcolor == null) {
              if (drawBitmaskImage(img,xform,bgcolor,srcX,srcY,srcWidth,srcHeight)) {
                return true;
              }
            }
 else             if (bgcolor.getTransparency() == Transparency.OPAQUE) {
              drawOpaque=true;
            }
          }
          if (!canDoRedraws()) {
            drawOpaque=true;
          }
        }
 else {
          bgcolor=null;
        }
        if ((srcX + srcWidth > img.getWidth(null) || srcY + srcHeight > img.getHeight(null)) && canDoRedraws()) {
          drawOpaque=false;
        }
        if (drawOpaque == false) {
          fullTransform.getMatrix(fullMatrix);
          AffineTransform tx=new AffineTransform(fullMatrix[0] / devScaleX,fullMatrix[1] / devScaleY,fullMatrix[2] / devScaleX,fullMatrix[3] / devScaleY,fullMatrix[4] / devScaleX,fullMatrix[5] / devScaleY);
          Rectangle2D.Float rect=new Rectangle2D.Float(srcX,srcY,srcWidth,srcHeight);
          Shape shape=fullTransform.createTransformedShape(rect);
          Rectangle2D region=shape.getBounds2D();
          region.setRect(region.getX(),region.getY(),region.getWidth() + 0.001,region.getHeight() + 0.001);
          int w=(int)region.getWidth();
          int h=(int)region.getHeight();
          int nbytes=w * h * 3;
          int maxBytes=8 * 1024 * 1024;
          double origDpi=(devResX < devResY) ? devResX : devResY;
          int dpi=(int)origDpi;
          double scaleFactor=1;
          double maxSFX=w / (double)boundsWidth;
          double maxSFY=h / (double)boundsHeight;
          double maxSF=(maxSFX > maxSFY) ? maxSFY : maxSFX;
          int minDpi=(int)(dpi / maxSF);
          if (minDpi < DEFAULT_USER_RES)           minDpi=DEFAULT_USER_RES;
          while (nbytes > maxBytes && dpi > minDpi) {
            scaleFactor*=2;
            dpi/=2;
            nbytes/=4;
          }
          if (dpi < minDpi) {
            scaleFactor=(origDpi / minDpi);
          }
          region.setRect(region.getX() / scaleFactor,region.getY() / scaleFactor,region.getWidth() / scaleFactor,region.getHeight() / scaleFactor);
          psPrinterJob.saveState(getTransform(),getClip(),region,scaleFactor,scaleFactor);
          return true;
        }
 else {
          BufferedImage deepImage=new BufferedImage((int)rotBounds.getWidth(),(int)rotBounds.getHeight(),BufferedImage.TYPE_3BYTE_BGR);
          Graphics2D imageGraphics=deepImage.createGraphics();
          imageGraphics.clipRect(0,0,deepImage.getWidth(),deepImage.getHeight());
          imageGraphics.translate(-rotBounds.getX(),-rotBounds.getY());
          imageGraphics.transform(rotTransform);
          if (bgcolor == null) {
            bgcolor=Color.white;
          }
          imageGraphics.drawImage(img,srcX,srcY,srcX + srcWidth,srcY + srcHeight,srcX,srcY,srcX + srcWidth,srcY + srcHeight,bgcolor,null);
          Shape holdClip=getClip();
          Shape oldClip=getTransform().createTransformedShape(holdClip);
          AffineTransform sat=AffineTransform.getScaleInstance(scaleX,scaleY);
          Shape imgClip=sat.createTransformedShape(rotShape);
          Area imgArea=new Area(imgClip);
          Area oldArea=new Area(oldClip);
          imgArea.intersect(oldArea);
          psPrinterJob.setClip(imgArea);
          Rectangle2D.Float scaledBounds=new Rectangle2D.Float((float)(rotBounds.getX() * scaleX),(float)(rotBounds.getY() * scaleY),(float)(rotBounds.getWidth() * scaleX),(float)(rotBounds.getHeight() * scaleY));
          ByteComponentRaster tile=(ByteComponentRaster)deepImage.getRaster();
          psPrinterJob.drawImageBGR(tile.getDataStorage(),scaledBounds.x,scaledBounds.y,(float)Math.rint(scaledBounds.width + 0.5),(float)Math.rint(scaledBounds.height + 0.5),0f,0f,deepImage.getWidth(),deepImage.getHeight(),deepImage.getWidth(),deepImage.getHeight());
          psPrinterJob.setClip(getTransform().createTransformedShape(holdClip));
          imageGraphics.dispose();
        }
      }
    }
    return true;
  }
  /** 
 * Redraw a rectanglular area using a proxy graphics
 * To do this we need to know the rectangular area to redraw and
 * the transform & clip in effect at the time of the original drawImage
 */
  public void redrawRegion(  Rectangle2D region,  double scaleX,  double scaleY,  Shape savedClip,  AffineTransform savedTransform) throws PrinterException {
    PSPrinterJob psPrinterJob=(PSPrinterJob)getPrinterJob();
    Printable painter=getPrintable();
    PageFormat pageFormat=getPageFormat();
    int pageIndex=getPageIndex();
    BufferedImage deepImage=new BufferedImage((int)region.getWidth(),(int)region.getHeight(),BufferedImage.TYPE_3BYTE_BGR);
    Graphics2D g=deepImage.createGraphics();
    ProxyGraphics2D proxy=new ProxyGraphics2D(g,psPrinterJob);
    proxy.setColor(Color.white);
    proxy.fillRect(0,0,deepImage.getWidth(),deepImage.getHeight());
    proxy.clipRect(0,0,deepImage.getWidth(),deepImage.getHeight());
    proxy.translate(-region.getX(),-region.getY());
    float sourceResX=(float)(psPrinterJob.getXRes() / scaleX);
    float sourceResY=(float)(psPrinterJob.getYRes() / scaleY);
    proxy.scale(sourceResX / DEFAULT_USER_RES,sourceResY / DEFAULT_USER_RES);
    proxy.translate(-psPrinterJob.getPhysicalPrintableX(pageFormat.getPaper()) / psPrinterJob.getXRes() * DEFAULT_USER_RES,-psPrinterJob.getPhysicalPrintableY(pageFormat.getPaper()) / psPrinterJob.getYRes() * DEFAULT_USER_RES);
    proxy.transform(new AffineTransform(getPageFormat().getMatrix()));
    proxy.setPaint(Color.black);
    painter.print(proxy,pageFormat,pageIndex);
    g.dispose();
    psPrinterJob.setClip(savedTransform.createTransformedShape(savedClip));
    Rectangle2D.Float scaledBounds=new Rectangle2D.Float((float)(region.getX() * scaleX),(float)(region.getY() * scaleY),(float)(region.getWidth() * scaleX),(float)(region.getHeight() * scaleY));
    ByteComponentRaster tile=(ByteComponentRaster)deepImage.getRaster();
    psPrinterJob.drawImageBGR(tile.getDataStorage(),scaledBounds.x,scaledBounds.y,scaledBounds.width,scaledBounds.height,0f,0f,deepImage.getWidth(),deepImage.getHeight(),deepImage.getWidth(),deepImage.getHeight());
  }
  protected void deviceFill(  PathIterator pathIter,  Color color){
    PSPrinterJob psPrinterJob=(PSPrinterJob)getPrinterJob();
    psPrinterJob.deviceFill(pathIter,color,getTransform(),getClip());
  }
  protected void deviceFrameRect(  int x,  int y,  int width,  int height,  Color color){
    draw(new Rectangle2D.Float(x,y,width,height));
  }
  protected void deviceDrawLine(  int xBegin,  int yBegin,  int xEnd,  int yEnd,  Color color){
    draw(new Line2D.Float(xBegin,yBegin,xEnd,yEnd));
  }
  protected void deviceFillRect(  int x,  int y,  int width,  int height,  Color color){
    fill(new Rectangle2D.Float(x,y,width,height));
  }
  protected void deviceClip(  PathIterator pathIter){
  }
}
