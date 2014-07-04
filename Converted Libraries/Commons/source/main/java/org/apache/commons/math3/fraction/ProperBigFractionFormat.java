package org.apache.commons.math3.fraction;
import java.math.BigInteger;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import org.apache.commons.math3.exception.NullArgumentException;
/** 
 * Formats a BigFraction number in proper format.  The number format for each of
 * the whole number, numerator and, denominator can be configured.
 * <p>
 * Minus signs are only allowed in the whole number part - i.e.,
 * "-3 1/2" is legitimate and denotes -7/2, but "-3 -1/2" is invalid and
 * will result in a <code>ParseException</code>.</p>
 * @since 1.1
 * @version $Id: ProperBigFractionFormat.java 1416643 2012-12-03 19:37:14Z tn $
 */
public class ProperBigFractionFormat extends BigFractionFormat {
  /** 
 * Serializable version identifier 
 */
  private static final long serialVersionUID=-6337346779577272307L;
  /** 
 * The format used for the whole number. 
 */
  private NumberFormat wholeFormat;
  /** 
 * Create a proper formatting instance with the default number format for
 * the whole, numerator, and denominator.
 */
  public ProperBigFractionFormat(){
    this(getDefaultNumberFormat());
  }
  /** 
 * Create a proper formatting instance with a custom number format for the
 * whole, numerator, and denominator.
 * @param format the custom format for the whole, numerator, and
 * denominator.
 */
  public ProperBigFractionFormat(  final NumberFormat format){
    this(format,(NumberFormat)format.clone(),(NumberFormat)format.clone());
  }
  /** 
 * Create a proper formatting instance with a custom number format for each
 * of the whole, numerator, and denominator.
 * @param wholeFormat the custom format for the whole.
 * @param numeratorFormat the custom format for the numerator.
 * @param denominatorFormat the custom format for the denominator.
 */
  public ProperBigFractionFormat(  final NumberFormat wholeFormat,  final NumberFormat numeratorFormat,  final NumberFormat denominatorFormat){
    super(numeratorFormat,denominatorFormat);
    setWholeFormat(wholeFormat);
  }
  /** 
 * Formats a {@link BigFraction} object to produce a string.  The BigFraction
 * is output in proper format.
 * @param fraction the object to format.
 * @param toAppendTo where the text is to be appended
 * @param pos On input: an alignment field, if desired. On output: the
 * offsets of the alignment field
 * @return the value passed in as toAppendTo.
 */
  @Override public StringBuffer format(  final BigFraction fraction,  final StringBuffer toAppendTo,  final FieldPosition pos){
    pos.setBeginIndex(0);
    pos.setEndIndex(0);
    BigInteger num=fraction.getNumerator();
    BigInteger den=fraction.getDenominator();
    BigInteger whole=num.divide(den);
    num=num.remainder(den);
    if (!BigInteger.ZERO.equals(whole)) {
      getWholeFormat().format(whole,toAppendTo,pos);
      toAppendTo.append(' ');
      if (num.compareTo(BigInteger.ZERO) < 0) {
        num=num.negate();
      }
    }
    getNumeratorFormat().format(num,toAppendTo,pos);
    toAppendTo.append(" / ");
    getDenominatorFormat().format(den,toAppendTo,pos);
    return toAppendTo;
  }
  /** 
 * Access the whole format.
 * @return the whole format.
 */
  public NumberFormat getWholeFormat(){
    return wholeFormat;
  }
  /** 
 * Parses a string to produce a {@link BigFraction} object.  This method
 * expects the string to be formatted as a proper BigFraction.
 * <p>
 * Minus signs are only allowed in the whole number part - i.e.,
 * "-3 1/2" is legitimate and denotes -7/2, but "-3 -1/2" is invalid and
 * will result in a <code>ParseException</code>.</p>
 * @param source the string to parse
 * @param pos input/ouput parsing parameter.
 * @return the parsed {@link BigFraction} object.
 */
  @Override public BigFraction parse(  final String source,  final ParsePosition pos){
    BigFraction ret=super.parse(source,pos);
    if (ret != null) {
      return ret;
    }
    final int initialIndex=pos.getIndex();
    parseAndIgnoreWhitespace(source,pos);
    BigInteger whole=parseNextBigInteger(source,pos);
    if (whole == null) {
      pos.setIndex(initialIndex);
      return null;
    }
    parseAndIgnoreWhitespace(source,pos);
    BigInteger num=parseNextBigInteger(source,pos);
    if (num == null) {
      pos.setIndex(initialIndex);
      return null;
    }
    if (num.compareTo(BigInteger.ZERO) < 0) {
      pos.setIndex(initialIndex);
      return null;
    }
    final int startIndex=pos.getIndex();
    final char c=parseNextCharacter(source,pos);
switch (c) {
case 0:
      return new BigFraction(num);
case '/':
    break;
default :
  pos.setIndex(initialIndex);
pos.setErrorIndex(startIndex);
return null;
}
parseAndIgnoreWhitespace(source,pos);
final BigInteger den=parseNextBigInteger(source,pos);
if (den == null) {
pos.setIndex(initialIndex);
return null;
}
if (den.compareTo(BigInteger.ZERO) < 0) {
pos.setIndex(initialIndex);
return null;
}
boolean wholeIsNeg=whole.compareTo(BigInteger.ZERO) < 0;
if (wholeIsNeg) {
whole=whole.negate();
}
num=whole.multiply(den).add(num);
if (wholeIsNeg) {
num=num.negate();
}
return new BigFraction(num,den);
}
/** 
 * Modify the whole format.
 * @param format The new whole format value.
 * @throws NullArgumentException if {@code format} is {@code null}.
 */
public void setWholeFormat(final NumberFormat format){
if (format == null) {
throw new NullArgumentException(LocalizedFormats.WHOLE_FORMAT);
}
this.wholeFormat=format;
}
}
