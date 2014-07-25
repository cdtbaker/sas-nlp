package org.apache.commons.math3.geometry.euclidean.threed;
import java.util.Locale;
public class Vector3DFormatTest extends Vector3DFormatAbstractTest {
  @Override protected char getDecimalCharacter(){
    return '.';
  }
  @Override protected Locale getLocale(){
    return Locale.US;
  }
}
