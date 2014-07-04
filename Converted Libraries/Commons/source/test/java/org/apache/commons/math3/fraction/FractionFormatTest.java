package org.apache.commons.math3.fraction;
import java.text.NumberFormat;
import java.util.Locale;
import org.apache.commons.math3.exception.MathParseException;
import org.apache.commons.math3.util.FastMath;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
public class FractionFormatTest {
  FractionFormat properFormat=null;
  FractionFormat improperFormat=null;
  protected Locale getLocale(){
    return Locale.getDefault();
  }
  @Before public void setUp(){
    properFormat=FractionFormat.getProperInstance(getLocale());
    improperFormat=FractionFormat.getImproperInstance(getLocale());
  }
  @Test public void testFormat(){
    Fraction c=new Fraction(1,2);
    String expected="1 / 2";
    String actual=properFormat.format(c);
    Assert.assertEquals(expected,actual);
    actual=improperFormat.format(c);
    Assert.assertEquals(expected,actual);
  }
  @Test public void testFormatNegative(){
    Fraction c=new Fraction(-1,2);
    String expected="-1 / 2";
    String actual=properFormat.format(c);
    Assert.assertEquals(expected,actual);
    actual=improperFormat.format(c);
    Assert.assertEquals(expected,actual);
  }
  @Test public void testFormatZero(){
    Fraction c=new Fraction(0,1);
    String expected="0 / 1";
    String actual=properFormat.format(c);
    Assert.assertEquals(expected,actual);
    actual=improperFormat.format(c);
    Assert.assertEquals(expected,actual);
  }
  @Test public void testFormatImproper(){
    Fraction c=new Fraction(5,3);
    String actual=properFormat.format(c);
    Assert.assertEquals("1 2 / 3",actual);
    actual=improperFormat.format(c);
    Assert.assertEquals("5 / 3",actual);
  }
  @Test public void testFormatImproperNegative(){
    Fraction c=new Fraction(-5,3);
    String actual=properFormat.format(c);
    Assert.assertEquals("-1 2 / 3",actual);
    actual=improperFormat.format(c);
    Assert.assertEquals("-5 / 3",actual);
  }
  @Test public void testParse(){
    String source="1 / 2";
    try {
      Fraction c=properFormat.parse(source);
      Assert.assertNotNull(c);
      Assert.assertEquals(1,c.getNumerator());
      Assert.assertEquals(2,c.getDenominator());
      c=improperFormat.parse(source);
      Assert.assertNotNull(c);
      Assert.assertEquals(1,c.getNumerator());
      Assert.assertEquals(2,c.getDenominator());
    }
 catch (    MathParseException ex) {
      Assert.fail(ex.getMessage());
    }
  }
  @Test public void testParseInteger(){
    String source="10";
{
      Fraction c=properFormat.parse(source);
      Assert.assertNotNull(c);
      Assert.assertEquals(10,c.getNumerator());
      Assert.assertEquals(1,c.getDenominator());
    }
{
      Fraction c=improperFormat.parse(source);
      Assert.assertNotNull(c);
      Assert.assertEquals(10,c.getNumerator());
      Assert.assertEquals(1,c.getDenominator());
    }
  }
  @Test public void testParseOne1(){
    String source="1 / 1";
    Fraction c=properFormat.parse(source);
    Assert.assertNotNull(c);
    Assert.assertEquals(1,c.getNumerator());
    Assert.assertEquals(1,c.getDenominator());
  }
  @Test public void testParseOne2(){
    String source="10 / 10";
    Fraction c=properFormat.parse(source);
    Assert.assertNotNull(c);
    Assert.assertEquals(1,c.getNumerator());
    Assert.assertEquals(1,c.getDenominator());
  }
  @Test public void testParseZero1(){
    String source="0 / 1";
    Fraction c=properFormat.parse(source);
    Assert.assertNotNull(c);
    Assert.assertEquals(0,c.getNumerator());
    Assert.assertEquals(1,c.getDenominator());
  }
  @Test public void testParseZero2(){
    String source="-0 / 1";
    Fraction c=properFormat.parse(source);
    Assert.assertNotNull(c);
    Assert.assertEquals(0,c.getNumerator());
    Assert.assertEquals(1,c.getDenominator());
    Assert.assertEquals(Double.POSITIVE_INFINITY,1d / c.doubleValue(),0);
  }
  @Test public void testParseInvalid(){
    String source="a";
    String msg="should not be able to parse '10 / a'.";
    try {
      properFormat.parse(source);
      Assert.fail(msg);
    }
 catch (    MathParseException ex) {
    }
    try {
      improperFormat.parse(source);
      Assert.fail(msg);
    }
 catch (    MathParseException ex) {
    }
  }
  @Test public void testParseInvalidDenominator(){
    String source="10 / a";
    String msg="should not be able to parse '10 / a'.";
    try {
      properFormat.parse(source);
      Assert.fail(msg);
    }
 catch (    MathParseException ex) {
    }
    try {
      improperFormat.parse(source);
      Assert.fail(msg);
    }
 catch (    MathParseException ex) {
    }
  }
  @Test public void testParseNegative(){
{
      String source="-1 / 2";
      Fraction c=properFormat.parse(source);
      Assert.assertNotNull(c);
      Assert.assertEquals(-1,c.getNumerator());
      Assert.assertEquals(2,c.getDenominator());
      c=improperFormat.parse(source);
      Assert.assertNotNull(c);
      Assert.assertEquals(-1,c.getNumerator());
      Assert.assertEquals(2,c.getDenominator());
      source="1 / -2";
      c=properFormat.parse(source);
      Assert.assertNotNull(c);
      Assert.assertEquals(-1,c.getNumerator());
      Assert.assertEquals(2,c.getDenominator());
      c=improperFormat.parse(source);
      Assert.assertNotNull(c);
      Assert.assertEquals(-1,c.getNumerator());
      Assert.assertEquals(2,c.getDenominator());
    }
  }
  @Test public void testParseProper(){
    String source="1 2 / 3";
{
      Fraction c=properFormat.parse(source);
      Assert.assertNotNull(c);
      Assert.assertEquals(5,c.getNumerator());
      Assert.assertEquals(3,c.getDenominator());
    }
    try {
      improperFormat.parse(source);
      Assert.fail("invalid improper fraction.");
    }
 catch (    MathParseException ex) {
    }
  }
  @Test public void testParseProperNegative(){
    String source="-1 2 / 3";
{
      Fraction c=properFormat.parse(source);
      Assert.assertNotNull(c);
      Assert.assertEquals(-5,c.getNumerator());
      Assert.assertEquals(3,c.getDenominator());
    }
    try {
      improperFormat.parse(source);
      Assert.fail("invalid improper fraction.");
    }
 catch (    MathParseException ex) {
    }
  }
  @Test public void testParseProperInvalidMinus(){
    String source="2 -2 / 3";
    try {
      properFormat.parse(source);
      Assert.fail("invalid minus in improper fraction.");
    }
 catch (    MathParseException ex) {
    }
    source="2 2 / -3";
    try {
      properFormat.parse(source);
      Assert.fail("invalid minus in improper fraction.");
    }
 catch (    MathParseException ex) {
    }
  }
  @Test public void testNumeratorFormat(){
    NumberFormat old=properFormat.getNumeratorFormat();
    NumberFormat nf=NumberFormat.getInstance();
    nf.setParseIntegerOnly(true);
    properFormat.setNumeratorFormat(nf);
    Assert.assertEquals(nf,properFormat.getNumeratorFormat());
    properFormat.setNumeratorFormat(old);
    old=improperFormat.getNumeratorFormat();
    nf=NumberFormat.getInstance();
    nf.setParseIntegerOnly(true);
    improperFormat.setNumeratorFormat(nf);
    Assert.assertEquals(nf,improperFormat.getNumeratorFormat());
    improperFormat.setNumeratorFormat(old);
  }
  @Test public void testDenominatorFormat(){
    NumberFormat old=properFormat.getDenominatorFormat();
    NumberFormat nf=NumberFormat.getInstance();
    nf.setParseIntegerOnly(true);
    properFormat.setDenominatorFormat(nf);
    Assert.assertEquals(nf,properFormat.getDenominatorFormat());
    properFormat.setDenominatorFormat(old);
    old=improperFormat.getDenominatorFormat();
    nf=NumberFormat.getInstance();
    nf.setParseIntegerOnly(true);
    improperFormat.setDenominatorFormat(nf);
    Assert.assertEquals(nf,improperFormat.getDenominatorFormat());
    improperFormat.setDenominatorFormat(old);
  }
  @Test public void testWholeFormat(){
    ProperFractionFormat format=(ProperFractionFormat)properFormat;
    NumberFormat old=format.getWholeFormat();
    NumberFormat nf=NumberFormat.getInstance();
    nf.setParseIntegerOnly(true);
    format.setWholeFormat(nf);
    Assert.assertEquals(nf,format.getWholeFormat());
    format.setWholeFormat(old);
  }
  @Test public void testLongFormat(){
    Assert.assertEquals("10 / 1",improperFormat.format(10l));
  }
  @Test public void testDoubleFormat(){
    Assert.assertEquals("355 / 113",improperFormat.format(FastMath.PI));
  }
}
