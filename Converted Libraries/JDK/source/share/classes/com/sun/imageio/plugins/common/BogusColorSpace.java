package com.sun.imageio.plugins.common;
import java.awt.color.ColorSpace;
/** 
 * A dummy <code>ColorSpace</code> to enable <code>ColorModel</code>
 * for image data which do not have an innate color representation.
 */
public class BogusColorSpace extends ColorSpace {
  /** 
 * Return the type given the number of components.
 * @param numComponents The number of components in the
 * <code>ColorSpace</code>.
 * @exception IllegalArgumentException if <code>numComponents</code>
 * is less than 1.
 */
  private static int getType(  int numComponents){
    if (numComponents < 1) {
      throw new IllegalArgumentException("numComponents < 1!");
    }
    int type;
switch (numComponents) {
case 1:
      type=ColorSpace.TYPE_GRAY;
    break;
default :
  type=numComponents + 10;
}
return type;
}
/** 
 * Constructs a bogus <code>ColorSpace</code>.
 * @param numComponents The number of components in the
 * <code>ColorSpace</code>.
 * @exception IllegalArgumentException if <code>numComponents</code>
 * is less than 1.
 */
public BogusColorSpace(int numComponents){
super(getType(numComponents),numComponents);
}
public float[] toRGB(float[] colorvalue){
if (colorvalue.length < getNumComponents()) {
throw new ArrayIndexOutOfBoundsException("colorvalue.length < getNumComponents()");
}
float[] rgbvalue=new float[3];
System.arraycopy(colorvalue,0,rgbvalue,0,Math.min(3,getNumComponents()));
return colorvalue;
}
public float[] fromRGB(float[] rgbvalue){
if (rgbvalue.length < 3) {
throw new ArrayIndexOutOfBoundsException("rgbvalue.length < 3");
}
float[] colorvalue=new float[getNumComponents()];
System.arraycopy(rgbvalue,0,colorvalue,0,Math.min(3,colorvalue.length));
return rgbvalue;
}
public float[] toCIEXYZ(float[] colorvalue){
if (colorvalue.length < getNumComponents()) {
throw new ArrayIndexOutOfBoundsException("colorvalue.length < getNumComponents()");
}
float[] xyzvalue=new float[3];
System.arraycopy(colorvalue,0,xyzvalue,0,Math.min(3,getNumComponents()));
return colorvalue;
}
public float[] fromCIEXYZ(float[] xyzvalue){
if (xyzvalue.length < 3) {
throw new ArrayIndexOutOfBoundsException("xyzvalue.length < 3");
}
float[] colorvalue=new float[getNumComponents()];
System.arraycopy(xyzvalue,0,colorvalue,0,Math.min(3,colorvalue.length));
return xyzvalue;
}
}
