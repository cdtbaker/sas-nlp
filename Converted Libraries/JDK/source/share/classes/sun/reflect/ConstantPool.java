package sun.reflect;
import java.lang.reflect.*;
/** 
 * Provides reflective access to the constant pools of classes.
 * Currently this is needed to provide reflective access to annotations
 * but may be used by other internal subsystems in the future. 
 */
public class ConstantPool {
  public int getSize(){
    return getSize0(constantPoolOop);
  }
  public Class getClassAt(  int index){
    return getClassAt0(constantPoolOop,index);
  }
  public Class getClassAtIfLoaded(  int index){
    return getClassAtIfLoaded0(constantPoolOop,index);
  }
  public Member getMethodAt(  int index){
    return getMethodAt0(constantPoolOop,index);
  }
  public Member getMethodAtIfLoaded(  int index){
    return getMethodAtIfLoaded0(constantPoolOop,index);
  }
  public Field getFieldAt(  int index){
    return getFieldAt0(constantPoolOop,index);
  }
  public Field getFieldAtIfLoaded(  int index){
    return getFieldAtIfLoaded0(constantPoolOop,index);
  }
  public String[] getMemberRefInfoAt(  int index){
    return getMemberRefInfoAt0(constantPoolOop,index);
  }
  public int getIntAt(  int index){
    return getIntAt0(constantPoolOop,index);
  }
  public long getLongAt(  int index){
    return getLongAt0(constantPoolOop,index);
  }
  public float getFloatAt(  int index){
    return getFloatAt0(constantPoolOop,index);
  }
  public double getDoubleAt(  int index){
    return getDoubleAt0(constantPoolOop,index);
  }
  public String getStringAt(  int index){
    return getStringAt0(constantPoolOop,index);
  }
  public String getUTF8At(  int index){
    return getUTF8At0(constantPoolOop,index);
  }
static {
    Reflection.registerFieldsToFilter(ConstantPool.class,new String[]{"constantPoolOop"});
  }
  private Object constantPoolOop;
  private native int getSize0(  Object constantPoolOop);
  private native Class getClassAt0(  Object constantPoolOop,  int index);
  private native Class getClassAtIfLoaded0(  Object constantPoolOop,  int index);
  private native Member getMethodAt0(  Object constantPoolOop,  int index);
  private native Member getMethodAtIfLoaded0(  Object constantPoolOop,  int index);
  private native Field getFieldAt0(  Object constantPoolOop,  int index);
  private native Field getFieldAtIfLoaded0(  Object constantPoolOop,  int index);
  private native String[] getMemberRefInfoAt0(  Object constantPoolOop,  int index);
  private native int getIntAt0(  Object constantPoolOop,  int index);
  private native long getLongAt0(  Object constantPoolOop,  int index);
  private native float getFloatAt0(  Object constantPoolOop,  int index);
  private native double getDoubleAt0(  Object constantPoolOop,  int index);
  private native String getStringAt0(  Object constantPoolOop,  int index);
  private native String getUTF8At0(  Object constantPoolOop,  int index);
}
