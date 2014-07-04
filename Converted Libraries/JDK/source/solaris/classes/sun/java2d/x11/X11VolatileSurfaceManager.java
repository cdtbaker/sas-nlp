package sun.java2d.x11;
import java.awt.GraphicsConfiguration;
import java.awt.ImageCapabilities;
import java.awt.Transparency;
import java.awt.image.ColorModel;
import sun.awt.X11GraphicsConfig;
import sun.awt.image.SunVolatileImage;
import sun.awt.image.VolatileSurfaceManager;
import sun.java2d.SurfaceData;
/** 
 * X11 platform implementation of the VolatileSurfaceManager class.
 * The class attempts to create and use a pixmap-based SurfaceData
 * object (X11PixmapSurfaceData).
 * If this object cannot be created or re-created as necessary, the
 * class falls back to a system memory based SurfaceData object
 * (BufImgSurfaceData) that will be used until the accelerated
 * SurfaceData can be restored.
 */
public class X11VolatileSurfaceManager extends VolatileSurfaceManager {
  private boolean accelerationEnabled;
  public X11VolatileSurfaceManager(  SunVolatileImage vImg,  Object context){
    super(vImg,context);
    accelerationEnabled=X11SurfaceData.isAccelerationEnabled() && (vImg.getTransparency() == Transparency.OPAQUE);
    if ((context != null) && !accelerationEnabled) {
      accelerationEnabled=true;
      sdAccel=initAcceleratedSurface();
      sdCurrent=sdAccel;
      if (sdBackup != null) {
        sdBackup=null;
      }
    }
  }
  protected boolean isAccelerationEnabled(){
    return accelerationEnabled;
  }
  /** 
 * Create a pixmap-based SurfaceData object
 */
  protected SurfaceData initAcceleratedSurface(){
    SurfaceData sData;
    try {
      X11GraphicsConfig gc=(X11GraphicsConfig)vImg.getGraphicsConfig();
      ColorModel cm=gc.getColorModel();
      long drawable=0;
      if (context instanceof Long) {
        drawable=((Long)context).longValue();
      }
      sData=X11SurfaceData.createData(gc,vImg.getWidth(),vImg.getHeight(),cm,vImg,drawable,Transparency.OPAQUE);
    }
 catch (    NullPointerException ex) {
      sData=null;
    }
catch (    OutOfMemoryError er) {
      sData=null;
    }
    return sData;
  }
  protected boolean isConfigValid(  GraphicsConfiguration gc){
    return ((gc == null) || (gc == vImg.getGraphicsConfig()));
  }
  /** 
 * Need to override the default behavior because Pixmaps-based
 * images are accelerated but not volatile.
 */
  @Override public ImageCapabilities getCapabilities(  GraphicsConfiguration gc){
    if (isConfigValid(gc) && isAccelerationEnabled()) {
      return new ImageCapabilities(true);
    }
    return new ImageCapabilities(false);
  }
}
