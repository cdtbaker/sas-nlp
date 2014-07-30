package org.apache.commons.math3.linear;
import java.util.Locale;
public class FrenchRealVectorFormatTest extends RealVectorFormatAbstractTest {
  @Override protected char getDecimalCharacter(){
    return ',';
  }
  @Override protected Locale getLocale(){
    return Locale.FRENCH;
  }
}