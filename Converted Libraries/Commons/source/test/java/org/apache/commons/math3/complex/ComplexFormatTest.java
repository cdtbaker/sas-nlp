package org.apache.commons.math3.complex;
import java.util.Locale;
public class ComplexFormatTest extends ComplexFormatAbstractTest {
  @Override protected char getDecimalCharacter(){
    return '.';
  }
  @Override protected Locale getLocale(){
    return Locale.US;
  }
}
