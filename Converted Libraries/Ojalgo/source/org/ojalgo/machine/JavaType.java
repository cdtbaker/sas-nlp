package org.ojalgo.machine;
public enum JavaType {BYTE(byte.class,8,1L), SHORT(short.class,16,2L), INT(int.class,32,4L), LONG(long.class,64,8L), FLOAT(float.class,32,4L), DOUBLE(double.class,64,8L), BOOLEAN(boolean.class,1,1L), CHAR(char.class,16,2L), /** 
 * 4 bytes with 32-bit JVM or 64-bit JVM with compressed pointers (All JVM:s assigned less than 32GB)
 */
REFERENCE(Object.class,64,4L); public static final JavaType match(final Class<?> aClass){
  for (  final JavaType tmpType : JavaType.values()) {
    if (tmpType.getJavaClass().isAssignableFrom(aClass)) {
      return tmpType;
    }
  }
  return null;
}
private final int myInformationBits;
private final long myMemoryBytes;
private final Class<?> myJavaClass;
JavaType(final Class<?> aClass,final int informationBits,final long memoryBytes){
  myJavaClass=aClass;
  myInformationBits=informationBits;
  myMemoryBytes=memoryBytes;
}
public final long estimateSizeOfWrapperClass(){
  return MemoryEstimator.makeForClassExtendingObject().add(this).estimate();
}
public final long memory(){
  return myMemoryBytes;
}
final int getInformationBits(){
  return myInformationBits;
}
Class<?> getJavaClass(){
  return myJavaClass;
}
}
