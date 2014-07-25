package sun.java2d.opengl;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.LookupOp;
import java.awt.image.RescaleOp;
import sun.java2d.SunGraphics2D;
import sun.java2d.SurfaceData;
import sun.java2d.loops.CompositeType;
import sun.java2d.pipe.BufferedBufImgOps;
import static sun.java2d.opengl.OGLContext.OGLContextCaps.*;
class OGLBufImgOps extends BufferedBufImgOps {
  /** 
 * This method is called from OGLDrawImage.transformImage() only.  It
 * validates the provided BufferedImageOp to determine whether the op
 * is one that can be accelerated by the OGL pipeline.  If the operation
 * cannot be completed for any reason, this method returns false;
 * otherwise, the given BufferedImage is rendered to the destination
 * using the provided BufferedImageOp and this method returns true.
 */
  static boolean renderImageWithOp(  SunGraphics2D sg,  BufferedImage img,  BufferedImageOp biop,  int x,  int y){
    if (biop instanceof ConvolveOp) {
      if (!isConvolveOpValid((ConvolveOp)biop)) {
        return false;
      }
    }
 else     if (biop instanceof RescaleOp) {
      if (!isRescaleOpValid((RescaleOp)biop,img)) {
        return false;
      }
    }
 else     if (biop instanceof LookupOp) {
      if (!isLookupOpValid((LookupOp)biop,img)) {
        return false;
      }
    }
 else {
      return false;
    }
    SurfaceData dstData=sg.surfaceData;
    if (!(dstData instanceof OGLSurfaceData) || (sg.interpolationType == AffineTransformOp.TYPE_BICUBIC) || (sg.compositeState > SunGraphics2D.COMP_ALPHA)) {
      return false;
    }
    SurfaceData srcData=dstData.getSourceSurfaceData(img,sg.TRANSFORM_ISIDENT,CompositeType.SrcOver,null);
    if (!(srcData instanceof OGLSurfaceData)) {
      srcData=dstData.getSourceSurfaceData(img,sg.TRANSFORM_ISIDENT,CompositeType.SrcOver,null);
      if (!(srcData instanceof OGLSurfaceData)) {
        return false;
      }
    }
    OGLSurfaceData oglSrc=(OGLSurfaceData)srcData;
    OGLGraphicsConfig gc=oglSrc.getOGLGraphicsConfig();
    if (oglSrc.getType() != OGLSurfaceData.TEXTURE || !gc.isCapPresent(CAPS_EXT_BIOP_SHADER)) {
      return false;
    }
    int sw=img.getWidth();
    int sh=img.getHeight();
    OGLBlitLoops.IsoBlit(srcData,dstData,img,biop,sg.composite,sg.getCompClip(),sg.transform,sg.interpolationType,0,0,sw,sh,x,y,x + sw,y + sh,true);
    return true;
  }
}
