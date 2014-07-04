package sun.reflect;
import java.lang.reflect.Field;
class UnsafeCharacterFieldAccessorImpl extends UnsafeFieldAccessorImpl {
  UnsafeCharacterFieldAccessorImpl(  Field field){
    super(field);
  }
  public Object get(  Object obj) throws IllegalArgumentException {
    return new Character(getChar(obj));
  }
  public boolean getBoolean(  Object obj) throws IllegalArgumentException {
    throw newGetBooleanIllegalArgumentException();
  }
  public byte getByte(  Object obj) throws IllegalArgumentException {
    throw newGetByteIllegalArgumentException();
  }
  public char getChar(  Object obj) throws IllegalArgumentException {
    ensureObj(obj);
    return unsafe.getChar(obj,fieldOffset);
  }
  public short getShort(  Object obj) throws IllegalArgumentException {
    throw newGetShortIllegalArgumentException();
  }
  public int getInt(  Object obj) throws IllegalArgumentException {
    return getChar(obj);
  }
  public long getLong(  Object obj) throws IllegalArgumentException {
    return getChar(obj);
  }
  public float getFloat(  Object obj) throws IllegalArgumentException {
    return getChar(obj);
  }
  public double getDouble(  Object obj) throws IllegalArgumentException {
    return getChar(obj);
  }
  public void set(  Object obj,  Object value) throws IllegalArgumentException, IllegalAccessException {
    ensureObj(obj);
    if (isFinal) {
      throwFinalFieldIllegalAccessException(value);
    }
    if (value == null) {
      throwSetIllegalArgumentException(value);
    }
    if (value instanceof Character) {
      unsafe.putChar(obj,fieldOffset,((Character)value).charValue());
      return;
    }
    throwSetIllegalArgumentException(value);
  }
  public void setBoolean(  Object obj,  boolean z) throws IllegalArgumentException, IllegalAccessException {
    throwSetIllegalArgumentException(z);
  }
  public void setByte(  Object obj,  byte b) throws IllegalArgumentException, IllegalAccessException {
    throwSetIllegalArgumentException(b);
  }
  public void setChar(  Object obj,  char c) throws IllegalArgumentException, IllegalAccessException {
    ensureObj(obj);
    if (isFinal) {
      throwFinalFieldIllegalAccessException(c);
    }
    unsafe.putChar(obj,fieldOffset,c);
  }
  public void setShort(  Object obj,  short s) throws IllegalArgumentException, IllegalAccessException {
    throwSetIllegalArgumentException(s);
  }
  public void setInt(  Object obj,  int i) throws IllegalArgumentException, IllegalAccessException {
    throwSetIllegalArgumentException(i);
  }
  public void setLong(  Object obj,  long l) throws IllegalArgumentException, IllegalAccessException {
    throwSetIllegalArgumentException(l);
  }
  public void setFloat(  Object obj,  float f) throws IllegalArgumentException, IllegalAccessException {
    throwSetIllegalArgumentException(f);
  }
  public void setDouble(  Object obj,  double d) throws IllegalArgumentException, IllegalAccessException {
    throwSetIllegalArgumentException(d);
  }
}
