package org.apache.commons.math3.complex;
import java.util.Locale;
public class FrenchComplexFormatTest extends ComplexFormatAbstractTest {
  @Override protected char getDecimalCharacter(){
    return ',';
  }
  @Override protected Locale getLocale(){
    return Locale.FRENCH;
  }
}
