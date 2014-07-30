package sun.awt.image;
import java.util.Hashtable;
import java.awt.image.ImageConsumer;
import java.awt.image.ImageProducer;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
public class OffScreenImageSource implements ImageProducer {
  BufferedImage image;
  int width;
  int height;
  Hashtable properties;
  public OffScreenImageSource(  BufferedImage image,  Hashtable properties){
    this.image=image;
    if (properties != null) {
      this.properties=properties;
    }
 else {
      this.properties=new Hashtable();
    }
    width=image.getWidth();
    height=image.getHeight();
  }
  public OffScreenImageSource(  BufferedImage image){
    this(image,null);
  }
  private ImageConsumer theConsumer;
  public synchronized void addConsumer(  ImageConsumer ic){
    theConsumer=ic;
    produce();
  }
  public synchronized boolean isConsumer(  ImageConsumer ic){
    return (ic == theConsumer);
  }
  public synchronized void removeConsumer(  ImageConsumer ic){
    if (theConsumer == ic) {
      theConsumer=null;
    }
  }
  public void startProduction(  ImageConsumer ic){
    addConsumer(ic);
  }
  public void requestTopDownLeftRightResend(  ImageConsumer ic){
  }
  private void sendPixels(){
    ColorModel cm=image.getColorModel();
    WritableRaster raster=image.getRaster();
    int numDataElements=raster.getNumDataElements();
    int dataType=raster.getDataBuffer().getDataType();
    int[] scanline=new int[width * numDataElements];
    boolean needToCvt=true;
    if (cm instanceof IndexColorModel) {
      byte[] pixels=new byte[width];
      theConsumer.setColorModel(cm);
      if (raster instanceof ByteComponentRaster) {
        needToCvt=false;
        for (int y=0; y < height; y++) {
          raster.getDataElements(0,y,width,1,pixels);
          theConsumer.setPixels(0,y,width,1,cm,pixels,0,width);
        }
      }
 else       if (raster instanceof BytePackedRaster) {
        needToCvt=false;
        for (int y=0; y < height; y++) {
          raster.getPixels(0,y,width,1,scanline);
          for (int x=0; x < width; x++) {
            pixels[x]=(byte)scanline[x];
          }
          theConsumer.setPixels(0,y,width,1,cm,pixels,0,width);
        }
      }
 else       if (dataType == DataBuffer.TYPE_SHORT || dataType == DataBuffer.TYPE_INT) {
        needToCvt=false;
        for (int y=0; y < height; y++) {
          raster.getPixels(0,y,width,1,scanline);
          theConsumer.setPixels(0,y,width,1,cm,scanline,0,width);
        }
      }
    }
 else     if (cm instanceof DirectColorModel) {
      theConsumer.setColorModel(cm);
      needToCvt=false;
switch (dataType) {
case DataBuffer.TYPE_INT:
        for (int y=0; y < height; y++) {
          raster.getDataElements(0,y,width,1,scanline);
          theConsumer.setPixels(0,y,width,1,cm,scanline,0,width);
        }
      break;
case DataBuffer.TYPE_BYTE:
    byte[] bscanline=new byte[width];
  for (int y=0; y < height; y++) {
    raster.getDataElements(0,y,width,1,bscanline);
    for (int x=0; x < width; x++) {
      scanline[x]=bscanline[x] & 0xff;
    }
    theConsumer.setPixels(0,y,width,1,cm,scanline,0,width);
  }
break;
case DataBuffer.TYPE_USHORT:
short[] sscanline=new short[width];
for (int y=0; y < height; y++) {
raster.getDataElements(0,y,width,1,sscanline);
for (int x=0; x < width; x++) {
scanline[x]=sscanline[x] & 0xffff;
}
theConsumer.setPixels(0,y,width,1,cm,scanline,0,width);
}
break;
default :
needToCvt=true;
}
}
if (needToCvt) {
ColorModel newcm=ColorModel.getRGBdefault();
theConsumer.setColorModel(newcm);
for (int y=0; y < height; y++) {
for (int x=0; x < width; x++) {
scanline[x]=image.getRGB(x,y);
}
theConsumer.setPixels(0,y,width,1,newcm,scanline,0,width);
}
}
}
private void produce(){
try {
theConsumer.setDimensions(image.getWidth(),image.getHeight());
theConsumer.setProperties(properties);
sendPixels();
theConsumer.imageComplete(ImageConsumer.SINGLEFRAMEDONE);
}
 catch (NullPointerException e) {
if (theConsumer != null) {
theConsumer.imageComplete(ImageConsumer.IMAGEERROR);
}
}
}
}