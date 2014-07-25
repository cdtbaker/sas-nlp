package java.text;
import java.io.InvalidObjectException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import sun.util.resources.LocaleData;
/** 
 * <code>DecimalFormat</code> is a concrete subclass of
 * <code>NumberFormat</code> that formats decimal numbers. It has a variety of
 * features designed to make it possible to parse and format numbers in any
 * locale, including support for Western, Arabic, and Indic digits.  It also
 * supports different kinds of numbers, including integers (123), fixed-point
 * numbers (123.4), scientific notation (1.23E4), percentages (12%), and
 * currency amounts ($123).  All of these can be localized.
 * <p>To obtain a <code>NumberFormat</code> for a specific locale, including the
 * default locale, call one of <code>NumberFormat</code>'s factory methods, such
 * as <code>getInstance()</code>.  In general, do not call the
 * <code>DecimalFormat</code> constructors directly, since the
 * <code>NumberFormat</code> factory methods may return subclasses other than
 * <code>DecimalFormat</code>. If you need to customize the format object, do
 * something like this:
 * <blockquote><pre>
 * NumberFormat f = NumberFormat.getInstance(loc);
 * if (f instanceof DecimalFormat) {
 * ((DecimalFormat) f).setDecimalSeparatorAlwaysShown(true);
 * }
 * </pre></blockquote>
 * <p>A <code>DecimalFormat</code> comprises a <em>pattern</em> and a set of
 * <em>symbols</em>.  The pattern may be set directly using
 * <code>applyPattern()</code>, or indirectly using the API methods.  The
 * symbols are stored in a <code>DecimalFormatSymbols</code> object.  When using
 * the <code>NumberFormat</code> factory methods, the pattern and symbols are
 * read from localized <code>ResourceBundle</code>s.
 * <h4>Patterns</h4>
 * <code>DecimalFormat</code> patterns have the following syntax:
 * <blockquote><pre>
 * <i>Pattern:</i>
 * <i>PositivePattern</i>
 * <i>PositivePattern</i> ; <i>NegativePattern</i>
 * <i>PositivePattern:</i>
 * <i>Prefix<sub>opt</sub></i> <i>Number</i> <i>Suffix<sub>opt</sub></i>
 * <i>NegativePattern:</i>
 * <i>Prefix<sub>opt</sub></i> <i>Number</i> <i>Suffix<sub>opt</sub></i>
 * <i>Prefix:</i>
 * any Unicode characters except &#92;uFFFE, &#92;uFFFF, and special characters
 * <i>Suffix:</i>
 * any Unicode characters except &#92;uFFFE, &#92;uFFFF, and special characters
 * <i>Number:</i>
 * <i>Integer</i> <i>Exponent<sub>opt</sub></i>
 * <i>Integer</i> . <i>Fraction</i> <i>Exponent<sub>opt</sub></i>
 * <i>Integer:</i>
 * <i>MinimumInteger</i>
 * #
 * # <i>Integer</i>
 * # , <i>Integer</i>
 * <i>MinimumInteger:</i>
 * 0
 * 0 <i>MinimumInteger</i>
 * 0 , <i>MinimumInteger</i>
 * <i>Fraction:</i>
 * <i>MinimumFraction<sub>opt</sub></i> <i>OptionalFraction<sub>opt</sub></i>
 * <i>MinimumFraction:</i>
 * 0 <i>MinimumFraction<sub>opt</sub></i>
 * <i>OptionalFraction:</i>
 * # <i>OptionalFraction<sub>opt</sub></i>
 * <i>Exponent:</i>
 * E <i>MinimumExponent</i>
 * <i>MinimumExponent:</i>
 * 0 <i>MinimumExponent<sub>opt</sub></i>
 * </pre></blockquote>
 * <p>A <code>DecimalFormat</code> pattern contains a positive and negative
 * subpattern, for example, <code>"#,##0.00;(#,##0.00)"</code>.  Each
 * subpattern has a prefix, numeric part, and suffix. The negative subpattern
 * is optional; if absent, then the positive subpattern prefixed with the
 * localized minus sign (<code>'-'</code> in most locales) is used as the
 * negative subpattern. That is, <code>"0.00"</code> alone is equivalent to
 * <code>"0.00;-0.00"</code>.  If there is an explicit negative subpattern, it
 * serves only to specify the negative prefix and suffix; the number of digits,
 * minimal digits, and other characteristics are all the same as the positive
 * pattern. That means that <code>"#,##0.0#;(#)"</code> produces precisely
 * the same behavior as <code>"#,##0.0#;(#,##0.0#)"</code>.
 * <p>The prefixes, suffixes, and various symbols used for infinity, digits,
 * thousands separators, decimal separators, etc. may be set to arbitrary
 * values, and they will appear properly during formatting.  However, care must
 * be taken that the symbols and strings do not conflict, or parsing will be
 * unreliable.  For example, either the positive and negative prefixes or the
 * suffixes must be distinct for <code>DecimalFormat.parse()</code> to be able
 * to distinguish positive from negative values.  (If they are identical, then
 * <code>DecimalFormat</code> will behave as if no negative subpattern was
 * specified.)  Another example is that the decimal separator and thousands
 * separator should be distinct characters, or parsing will be impossible.
 * <p>The grouping separator is commonly used for thousands, but in some
 * countries it separates ten-thousands. The grouping size is a constant number
 * of digits between the grouping characters, such as 3 for 100,000,000 or 4 for
 * 1,0000,0000.  If you supply a pattern with multiple grouping characters, the
 * interval between the last one and the end of the integer is the one that is
 * used. So <code>"#,##,###,####"</code> == <code>"######,####"</code> ==
 * <code>"##,####,####"</code>.
 * <h4>Special Pattern Characters</h4>
 * <p>Many characters in a pattern are taken literally; they are matched during
 * parsing and output unchanged during formatting.  Special characters, on the
 * other hand, stand for other characters, strings, or classes of characters.
 * They must be quoted, unless noted otherwise, if they are to appear in the
 * prefix or suffix as literals.
 * <p>The characters listed here are used in non-localized patterns.  Localized
 * patterns use the corresponding characters taken from this formatter's
 * <code>DecimalFormatSymbols</code> object instead, and these characters lose
 * their special status.  Two exceptions are the currency sign and quote, which
 * are not localized.
 * <blockquote>
 * <table border=0 cellspacing=3 cellpadding=0 summary="Chart showing symbol,
 * location, localized, and meaning.">
 * <tr bgcolor="#ccccff">
 * <th align=left>Symbol
 * <th align=left>Location
 * <th align=left>Localized?
 * <th align=left>Meaning
 * <tr valign=top>
 * <td><code>0</code>
 * <td>Number
 * <td>Yes
 * <td>Digit
 * <tr valign=top bgcolor="#eeeeff">
 * <td><code>#</code>
 * <td>Number
 * <td>Yes
 * <td>Digit, zero shows as absent
 * <tr valign=top>
 * <td><code>.</code>
 * <td>Number
 * <td>Yes
 * <td>Decimal separator or monetary decimal separator
 * <tr valign=top bgcolor="#eeeeff">
 * <td><code>-</code>
 * <td>Number
 * <td>Yes
 * <td>Minus sign
 * <tr valign=top>
 * <td><code>,</code>
 * <td>Number
 * <td>Yes
 * <td>Grouping separator
 * <tr valign=top bgcolor="#eeeeff">
 * <td><code>E</code>
 * <td>Number
 * <td>Yes
 * <td>Separates mantissa and exponent in scientific notation.
 * <em>Need not be quoted in prefix or suffix.</em>
 * <tr valign=top>
 * <td><code>;</code>
 * <td>Subpattern boundary
 * <td>Yes
 * <td>Separates positive and negative subpatterns
 * <tr valign=top bgcolor="#eeeeff">
 * <td><code>%</code>
 * <td>Prefix or suffix
 * <td>Yes
 * <td>Multiply by 100 and show as percentage
 * <tr valign=top>
 * <td><code>&#92;u2030</code>
 * <td>Prefix or suffix
 * <td>Yes
 * <td>Multiply by 1000 and show as per mille value
 * <tr valign=top bgcolor="#eeeeff">
 * <td><code>&#164;</code> (<code>&#92;u00A4</code>)
 * <td>Prefix or suffix
 * <td>No
 * <td>Currency sign, replaced by currency symbol.  If
 * doubled, replaced by international currency symbol.
 * If present in a pattern, the monetary decimal separator
 * is used instead of the decimal separator.
 * <tr valign=top>
 * <td><code>'</code>
 * <td>Prefix or suffix
 * <td>No
 * <td>Used to quote special characters in a prefix or suffix,
 * for example, <code>"'#'#"</code> formats 123 to
 * <code>"#123"</code>.  To create a single quote
 * itself, use two in a row: <code>"# o''clock"</code>.
 * </table>
 * </blockquote>
 * <h4>Scientific Notation</h4>
 * <p>Numbers in scientific notation are expressed as the product of a mantissa
 * and a power of ten, for example, 1234 can be expressed as 1.234 x 10^3.  The
 * mantissa is often in the range 1.0 <= x < 10.0, but it need not be.
 * <code>DecimalFormat</code> can be instructed to format and parse scientific
 * notation <em>only via a pattern</em>; there is currently no factory method
 * that creates a scientific notation format.  In a pattern, the exponent
 * character immediately followed by one or more digit characters indicates
 * scientific notation.  Example: <code>"0.###E0"</code> formats the number
 * 1234 as <code>"1.234E3"</code>.
 * <ul>
 * <li>The number of digit characters after the exponent character gives the
 * minimum exponent digit count.  There is no maximum.  Negative exponents are
 * formatted using the localized minus sign, <em>not</em> the prefix and suffix
 * from the pattern.  This allows patterns such as <code>"0.###E0 m/s"</code>.
 * <li>The minimum and maximum number of integer digits are interpreted
 * together:
 * <ul>
 * <li>If the maximum number of integer digits is greater than their minimum number
 * and greater than 1, it forces the exponent to be a multiple of the maximum
 * number of integer digits, and the minimum number of integer digits to be
 * interpreted as 1.  The most common use of this is to generate
 * <em>engineering notation</em>, in which the exponent is a multiple of three,
 * e.g., <code>"##0.#####E0"</code>. Using this pattern, the number 12345
 * formats to <code>"12.345E3"</code>, and 123456 formats to
 * <code>"123.456E3"</code>.
 * <li>Otherwise, the minimum number of integer digits is achieved by adjusting the
 * exponent.  Example: 0.00123 formatted with <code>"00.###E0"</code> yields
 * <code>"12.3E-4"</code>.
 * </ul>
 * <li>The number of significant digits in the mantissa is the sum of the
 * <em>minimum integer</em> and <em>maximum fraction</em> digits, and is
 * unaffected by the maximum integer digits.  For example, 12345 formatted with
 * <code>"##0.##E0"</code> is <code>"12.3E3"</code>. To show all digits, set
 * the significant digits count to zero.  The number of significant digits
 * does not affect parsing.
 * <li>Exponential patterns may not contain grouping separators.
 * </ul>
 * <h4>Rounding</h4>
 * <code>DecimalFormat</code> provides rounding modes defined in{@link java.math.RoundingMode} for formatting.  By default, it uses{@link java.math.RoundingMode#HALF_EVEN RoundingMode.HALF_EVEN}.
 * <h4>Digits</h4>
 * For formatting, <code>DecimalFormat</code> uses the ten consecutive
 * characters starting with the localized zero digit defined in the
 * <code>DecimalFormatSymbols</code> object as digits. For parsing, these
 * digits as well as all Unicode decimal digits, as defined by{@link Character#digit Character.digit}, are recognized.
 * <h4>Special Values</h4>
 * <p><code>NaN</code> is formatted as a string, which typically has a single character
 * <code>&#92;uFFFD</code>.  This string is determined by the
 * <code>DecimalFormatSymbols</code> object.  This is the only value for which
 * the prefixes and suffixes are not used.
 * <p>Infinity is formatted as a string, which typically has a single character
 * <code>&#92;u221E</code>, with the positive or negative prefixes and suffixes
 * applied.  The infinity string is determined by the
 * <code>DecimalFormatSymbols</code> object.
 * <p>Negative zero (<code>"-0"</code>) parses to
 * <ul>
 * <li><code>BigDecimal(0)</code> if <code>isParseBigDecimal()</code> is
 * true,
 * <li><code>Long(0)</code> if <code>isParseBigDecimal()</code> is false
 * and <code>isParseIntegerOnly()</code> is true,
 * <li><code>Double(-0.0)</code> if both <code>isParseBigDecimal()</code>
 * and <code>isParseIntegerOnly()</code> are false.
 * </ul>
 * <h4><a name="synchronization">Synchronization</a></h4>
 * <p>
 * Decimal formats are generally not synchronized.
 * It is recommended to create separate format instances for each thread.
 * If multiple threads access a format concurrently, it must be synchronized
 * externally.
 * <h4>Example</h4>
 * <blockquote><pre>
 * <strong>// Print out a number using the localized number, integer, currency,
 * // and percent format for each locale</strong>
 * Locale[] locales = NumberFormat.getAvailableLocales();
 * double myNumber = -1234.56;
 * NumberFormat form;
 * for (int j=0; j<4; ++j) {
 * System.out.println("FORMAT");
 * for (int i = 0; i < locales.length; ++i) {
 * if (locales[i].getCountry().length() == 0) {
 * continue; // Skip language-only locales
 * }
 * System.out.print(locales[i].getDisplayName());
 * switch (j) {
 * case 0:
 * form = NumberFormat.getInstance(locales[i]); break;
 * case 1:
 * form = NumberFormat.getIntegerInstance(locales[i]); break;
 * case 2:
 * form = NumberFormat.getCurrencyInstance(locales[i]); break;
 * default:
 * form = NumberFormat.getPercentInstance(locales[i]); break;
 * }
 * if (form instanceof DecimalFormat) {
 * System.out.print(": " + ((DecimalFormat) form).toPattern());
 * }
 * System.out.print(" -> " + form.format(myNumber));
 * try {
 * System.out.println(" -> " + form.parse(form.format(myNumber)));
 * } catch (ParseException e) {}
 * }
 * }
 * </pre></blockquote>
 * @see          <a href="http://java.sun.com/docs/books/tutorial/i18n/format/decimalFormat.html">Java Tutorial</a>
 * @see NumberFormat
 * @see DecimalFormatSymbols
 * @see ParsePosition
 * @author       Mark Davis
 * @author       Alan Liu
 */
public class DecimalFormat extends NumberFormat {
  /** 
 * Creates a DecimalFormat using the default pattern and symbols
 * for the default locale. This is a convenient way to obtain a
 * DecimalFormat when internationalization is not the main concern.
 * <p>
 * To obtain standard formats for a given locale, use the factory methods
 * on NumberFormat such as getNumberInstance. These factories will
 * return the most appropriate sub-class of NumberFormat for a given
 * locale.
 * @see java.text.NumberFormat#getInstance
 * @see java.text.NumberFormat#getNumberInstance
 * @see java.text.NumberFormat#getCurrencyInstance
 * @see java.text.NumberFormat#getPercentInstance
 */
  public DecimalFormat(){
    Locale def=Locale.getDefault(Locale.Category.FORMAT);
    String pattern=cachedLocaleData.get(def);
    if (pattern == null) {
      ResourceBundle rb=LocaleData.getNumberFormatData(def);
      String[] all=rb.getStringArray("NumberPatterns");
      pattern=all[0];
      cachedLocaleData.putIfAbsent(def,pattern);
    }
    this.symbols=new DecimalFormatSymbols(def);
    applyPattern(pattern,false);
  }
  /** 
 * Creates a DecimalFormat using the given pattern and the symbols
 * for the default locale. This is a convenient way to obtain a
 * DecimalFormat when internationalization is not the main concern.
 * <p>
 * To obtain standard formats for a given locale, use the factory methods
 * on NumberFormat such as getNumberInstance. These factories will
 * return the most appropriate sub-class of NumberFormat for a given
 * locale.
 * @param pattern A non-localized pattern string.
 * @exception NullPointerException if <code>pattern</code> is null
 * @exception IllegalArgumentException if the given pattern is invalid.
 * @see java.text.NumberFormat#getInstance
 * @see java.text.NumberFormat#getNumberInstance
 * @see java.text.NumberFormat#getCurrencyInstance
 * @see java.text.NumberFormat#getPercentInstance
 */
  public DecimalFormat(  String pattern){
    this.symbols=new DecimalFormatSymbols(Locale.getDefault(Locale.Category.FORMAT));
    applyPattern(pattern,false);
  }
  /** 
 * Creates a DecimalFormat using the given pattern and symbols.
 * Use this constructor when you need to completely customize the
 * behavior of the format.
 * <p>
 * To obtain standard formats for a given
 * locale, use the factory methods on NumberFormat such as
 * getInstance or getCurrencyInstance. If you need only minor adjustments
 * to a standard format, you can modify the format returned by
 * a NumberFormat factory method.
 * @param pattern a non-localized pattern string
 * @param symbols the set of symbols to be used
 * @exception NullPointerException if any of the given arguments is null
 * @exception IllegalArgumentException if the given pattern is invalid
 * @see java.text.NumberFormat#getInstance
 * @see java.text.NumberFormat#getNumberInstance
 * @see java.text.NumberFormat#getCurrencyInstance
 * @see java.text.NumberFormat#getPercentInstance
 * @see java.text.DecimalFormatSymbols
 */
  public DecimalFormat(  String pattern,  DecimalFormatSymbols symbols){
    this.symbols=(DecimalFormatSymbols)symbols.clone();
    applyPattern(pattern,false);
  }
  /** 
 * Formats a number and appends the resulting text to the given string
 * buffer.
 * The number can be of any subclass of {@link java.lang.Number}.
 * <p>
 * This implementation uses the maximum precision permitted.
 * @param number     the number to format
 * @param toAppendTo the <code>StringBuffer</code> to which the formatted
 * text is to be appended
 * @param pos        On input: an alignment field, if desired.
 * On output: the offsets of the alignment field.
 * @return           the value passed in as <code>toAppendTo</code>
 * @exception IllegalArgumentException if <code>number</code> is
 * null or not an instance of <code>Number</code>.
 * @exception NullPointerException if <code>toAppendTo</code> or
 * <code>pos</code> is null
 * @exception ArithmeticException if rounding is needed with rounding
 * mode being set to RoundingMode.UNNECESSARY
 * @see java.text.FieldPosition
 */
  public final StringBuffer format(  Object number,  StringBuffer toAppendTo,  FieldPosition pos){
    if (number instanceof Long || number instanceof Integer || number instanceof Short|| number instanceof Byte|| number instanceof AtomicInteger|| number instanceof AtomicLong|| (number instanceof BigInteger && ((BigInteger)number).bitLength() < 64)) {
      return format(((Number)number).longValue(),toAppendTo,pos);
    }
 else     if (number instanceof BigDecimal) {
      return format((BigDecimal)number,toAppendTo,pos);
    }
 else     if (number instanceof BigInteger) {
      return format((BigInteger)number,toAppendTo,pos);
    }
 else     if (number instanceof Number) {
      return format(((Number)number).doubleValue(),toAppendTo,pos);
    }
 else {
      throw new IllegalArgumentException("Cannot format given Object as a Number");
    }
  }
  /** 
 * Formats a double to produce a string.
 * @param number    The double to format
 * @param result    where the text is to be appended
 * @param fieldPosition    On input: an alignment field, if desired.
 * On output: the offsets of the alignment field.
 * @exception ArithmeticException if rounding is needed with rounding
 * mode being set to RoundingMode.UNNECESSARY
 * @return The formatted number string
 * @see java.text.FieldPosition
 */
  public StringBuffer format(  double number,  StringBuffer result,  FieldPosition fieldPosition){
    fieldPosition.setBeginIndex(0);
    fieldPosition.setEndIndex(0);
    return format(number,result,fieldPosition.getFieldDelegate());
  }
  /** 
 * Formats a double to produce a string.
 * @param number    The double to format
 * @param result    where the text is to be appended
 * @param delegate notified of locations of sub fields
 * @exception ArithmeticException if rounding is needed with rounding
 * mode being set to RoundingMode.UNNECESSARY
 * @return The formatted number string
 */
  private StringBuffer format(  double number,  StringBuffer result,  FieldDelegate delegate){
    if (Double.isNaN(number) || (Double.isInfinite(number) && multiplier == 0)) {
      int iFieldStart=result.length();
      result.append(symbols.getNaN());
      delegate.formatted(INTEGER_FIELD,Field.INTEGER,Field.INTEGER,iFieldStart,result.length(),result);
      return result;
    }
    boolean isNegative=((number < 0.0) || (number == 0.0 && 1 / number < 0.0)) ^ (multiplier < 0);
    if (multiplier != 1) {
      number*=multiplier;
    }
    if (Double.isInfinite(number)) {
      if (isNegative) {
        append(result,negativePrefix,delegate,getNegativePrefixFieldPositions(),Field.SIGN);
      }
 else {
        append(result,positivePrefix,delegate,getPositivePrefixFieldPositions(),Field.SIGN);
      }
      int iFieldStart=result.length();
      result.append(symbols.getInfinity());
      delegate.formatted(INTEGER_FIELD,Field.INTEGER,Field.INTEGER,iFieldStart,result.length(),result);
      if (isNegative) {
        append(result,negativeSuffix,delegate,getNegativeSuffixFieldPositions(),Field.SIGN);
      }
 else {
        append(result,positiveSuffix,delegate,getPositiveSuffixFieldPositions(),Field.SIGN);
      }
      return result;
    }
    if (isNegative) {
      number=-number;
    }
    assert (number >= 0 && !Double.isInfinite(number));
synchronized (digitList) {
      int maxIntDigits=super.getMaximumIntegerDigits();
      int minIntDigits=super.getMinimumIntegerDigits();
      int maxFraDigits=super.getMaximumFractionDigits();
      int minFraDigits=super.getMinimumFractionDigits();
      digitList.set(isNegative,number,useExponentialNotation ? maxIntDigits + maxFraDigits : maxFraDigits,!useExponentialNotation);
      return subformat(result,delegate,isNegative,false,maxIntDigits,minIntDigits,maxFraDigits,minFraDigits);
    }
  }
  /** 
 * Format a long to produce a string.
 * @param number    The long to format
 * @param result    where the text is to be appended
 * @param fieldPosition    On input: an alignment field, if desired.
 * On output: the offsets of the alignment field.
 * @exception ArithmeticException if rounding is needed with rounding
 * mode being set to RoundingMode.UNNECESSARY
 * @return The formatted number string
 * @see java.text.FieldPosition
 */
  public StringBuffer format(  long number,  StringBuffer result,  FieldPosition fieldPosition){
    fieldPosition.setBeginIndex(0);
    fieldPosition.setEndIndex(0);
    return format(number,result,fieldPosition.getFieldDelegate());
  }
  /** 
 * Format a long to produce a string.
 * @param number    The long to format
 * @param result    where the text is to be appended
 * @param delegate notified of locations of sub fields
 * @return The formatted number string
 * @exception ArithmeticException if rounding is needed with rounding
 * mode being set to RoundingMode.UNNECESSARY
 * @see java.text.FieldPosition
 */
  private StringBuffer format(  long number,  StringBuffer result,  FieldDelegate delegate){
    boolean isNegative=(number < 0);
    if (isNegative) {
      number=-number;
    }
    boolean useBigInteger=false;
    if (number < 0) {
      if (multiplier != 0) {
        useBigInteger=true;
      }
    }
 else     if (multiplier != 1 && multiplier != 0) {
      long cutoff=Long.MAX_VALUE / multiplier;
      if (cutoff < 0) {
        cutoff=-cutoff;
      }
      useBigInteger=(number > cutoff);
    }
    if (useBigInteger) {
      if (isNegative) {
        number=-number;
      }
      BigInteger bigIntegerValue=BigInteger.valueOf(number);
      return format(bigIntegerValue,result,delegate,true);
    }
    number*=multiplier;
    if (number == 0) {
      isNegative=false;
    }
 else {
      if (multiplier < 0) {
        number=-number;
        isNegative=!isNegative;
      }
    }
synchronized (digitList) {
      int maxIntDigits=super.getMaximumIntegerDigits();
      int minIntDigits=super.getMinimumIntegerDigits();
      int maxFraDigits=super.getMaximumFractionDigits();
      int minFraDigits=super.getMinimumFractionDigits();
      digitList.set(isNegative,number,useExponentialNotation ? maxIntDigits + maxFraDigits : 0);
      return subformat(result,delegate,isNegative,true,maxIntDigits,minIntDigits,maxFraDigits,minFraDigits);
    }
  }
  /** 
 * Formats a BigDecimal to produce a string.
 * @param number    The BigDecimal to format
 * @param result    where the text is to be appended
 * @param fieldPosition    On input: an alignment field, if desired.
 * On output: the offsets of the alignment field.
 * @return The formatted number string
 * @exception ArithmeticException if rounding is needed with rounding
 * mode being set to RoundingMode.UNNECESSARY
 * @see java.text.FieldPosition
 */
  private StringBuffer format(  BigDecimal number,  StringBuffer result,  FieldPosition fieldPosition){
    fieldPosition.setBeginIndex(0);
    fieldPosition.setEndIndex(0);
    return format(number,result,fieldPosition.getFieldDelegate());
  }
  /** 
 * Formats a BigDecimal to produce a string.
 * @param number    The BigDecimal to format
 * @param result    where the text is to be appended
 * @param delegate notified of locations of sub fields
 * @exception ArithmeticException if rounding is needed with rounding
 * mode being set to RoundingMode.UNNECESSARY
 * @return The formatted number string
 */
  private StringBuffer format(  BigDecimal number,  StringBuffer result,  FieldDelegate delegate){
    if (multiplier != 1) {
      number=number.multiply(getBigDecimalMultiplier());
    }
    boolean isNegative=number.signum() == -1;
    if (isNegative) {
      number=number.negate();
    }
synchronized (digitList) {
      int maxIntDigits=getMaximumIntegerDigits();
      int minIntDigits=getMinimumIntegerDigits();
      int maxFraDigits=getMaximumFractionDigits();
      int minFraDigits=getMinimumFractionDigits();
      int maximumDigits=maxIntDigits + maxFraDigits;
      digitList.set(isNegative,number,useExponentialNotation ? ((maximumDigits < 0) ? Integer.MAX_VALUE : maximumDigits) : maxFraDigits,!useExponentialNotation);
      return subformat(result,delegate,isNegative,false,maxIntDigits,minIntDigits,maxFraDigits,minFraDigits);
    }
  }
  /** 
 * Format a BigInteger to produce a string.
 * @param number    The BigInteger to format
 * @param result    where the text is to be appended
 * @param fieldPosition    On input: an alignment field, if desired.
 * On output: the offsets of the alignment field.
 * @return The formatted number string
 * @exception ArithmeticException if rounding is needed with rounding
 * mode being set to RoundingMode.UNNECESSARY
 * @see java.text.FieldPosition
 */
  private StringBuffer format(  BigInteger number,  StringBuffer result,  FieldPosition fieldPosition){
    fieldPosition.setBeginIndex(0);
    fieldPosition.setEndIndex(0);
    return format(number,result,fieldPosition.getFieldDelegate(),false);
  }
  /** 
 * Format a BigInteger to produce a string.
 * @param number    The BigInteger to format
 * @param result    where the text is to be appended
 * @param delegate notified of locations of sub fields
 * @return The formatted number string
 * @exception ArithmeticException if rounding is needed with rounding
 * mode being set to RoundingMode.UNNECESSARY
 * @see java.text.FieldPosition
 */
  private StringBuffer format(  BigInteger number,  StringBuffer result,  FieldDelegate delegate,  boolean formatLong){
    if (multiplier != 1) {
      number=number.multiply(getBigIntegerMultiplier());
    }
    boolean isNegative=number.signum() == -1;
    if (isNegative) {
      number=number.negate();
    }
synchronized (digitList) {
      int maxIntDigits, minIntDigits, maxFraDigits, minFraDigits, maximumDigits;
      if (formatLong) {
        maxIntDigits=super.getMaximumIntegerDigits();
        minIntDigits=super.getMinimumIntegerDigits();
        maxFraDigits=super.getMaximumFractionDigits();
        minFraDigits=super.getMinimumFractionDigits();
        maximumDigits=maxIntDigits + maxFraDigits;
      }
 else {
        maxIntDigits=getMaximumIntegerDigits();
        minIntDigits=getMinimumIntegerDigits();
        maxFraDigits=getMaximumFractionDigits();
        minFraDigits=getMinimumFractionDigits();
        maximumDigits=maxIntDigits + maxFraDigits;
        if (maximumDigits < 0) {
          maximumDigits=Integer.MAX_VALUE;
        }
      }
      digitList.set(isNegative,number,useExponentialNotation ? maximumDigits : 0);
      return subformat(result,delegate,isNegative,true,maxIntDigits,minIntDigits,maxFraDigits,minFraDigits);
    }
  }
  /** 
 * Formats an Object producing an <code>AttributedCharacterIterator</code>.
 * You can use the returned <code>AttributedCharacterIterator</code>
 * to build the resulting String, as well as to determine information
 * about the resulting String.
 * <p>
 * Each attribute key of the AttributedCharacterIterator will be of type
 * <code>NumberFormat.Field</code>, with the attribute value being the
 * same as the attribute key.
 * @exception NullPointerException if obj is null.
 * @exception IllegalArgumentException when the Format cannot format the
 * given object.
 * @exception ArithmeticException if rounding is needed with rounding
 * mode being set to RoundingMode.UNNECESSARY
 * @param obj The object to format
 * @return AttributedCharacterIterator describing the formatted value.
 * @since 1.4
 */
  public AttributedCharacterIterator formatToCharacterIterator(  Object obj){
    CharacterIteratorFieldDelegate delegate=new CharacterIteratorFieldDelegate();
    StringBuffer sb=new StringBuffer();
    if (obj instanceof Double || obj instanceof Float) {
      format(((Number)obj).doubleValue(),sb,delegate);
    }
 else     if (obj instanceof Long || obj instanceof Integer || obj instanceof Short|| obj instanceof Byte|| obj instanceof AtomicInteger|| obj instanceof AtomicLong) {
      format(((Number)obj).longValue(),sb,delegate);
    }
 else     if (obj instanceof BigDecimal) {
      format((BigDecimal)obj,sb,delegate);
    }
 else     if (obj instanceof BigInteger) {
      format((BigInteger)obj,sb,delegate,false);
    }
 else     if (obj == null) {
      throw new NullPointerException("formatToCharacterIterator must be passed non-null object");
    }
 else {
      throw new IllegalArgumentException("Cannot format given Object as a Number");
    }
    return delegate.getIterator(sb.toString());
  }
  /** 
 * Complete the formatting of a finite number.  On entry, the digitList must
 * be filled in with the correct digits.
 */
  private StringBuffer subformat(  StringBuffer result,  FieldDelegate delegate,  boolean isNegative,  boolean isInteger,  int maxIntDigits,  int minIntDigits,  int maxFraDigits,  int minFraDigits){
    char zero=symbols.getZeroDigit();
    int zeroDelta=zero - '0';
    char grouping=symbols.getGroupingSeparator();
    char decimal=isCurrencyFormat ? symbols.getMonetaryDecimalSeparator() : symbols.getDecimalSeparator();
    if (digitList.isZero()) {
      digitList.decimalAt=0;
    }
    if (isNegative) {
      append(result,negativePrefix,delegate,getNegativePrefixFieldPositions(),Field.SIGN);
    }
 else {
      append(result,positivePrefix,delegate,getPositivePrefixFieldPositions(),Field.SIGN);
    }
    if (useExponentialNotation) {
      int iFieldStart=result.length();
      int iFieldEnd=-1;
      int fFieldStart=-1;
      int exponent=digitList.decimalAt;
      int repeat=maxIntDigits;
      int minimumIntegerDigits=minIntDigits;
      if (repeat > 1 && repeat > minIntDigits) {
        if (exponent >= 1) {
          exponent=((exponent - 1) / repeat) * repeat;
        }
 else {
          exponent=((exponent - repeat) / repeat) * repeat;
        }
        minimumIntegerDigits=1;
      }
 else {
        exponent-=minimumIntegerDigits;
      }
      int minimumDigits=minIntDigits + minFraDigits;
      if (minimumDigits < 0) {
        minimumDigits=Integer.MAX_VALUE;
      }
      int integerDigits=digitList.isZero() ? minimumIntegerDigits : digitList.decimalAt - exponent;
      if (minimumDigits < integerDigits) {
        minimumDigits=integerDigits;
      }
      int totalDigits=digitList.count;
      if (minimumDigits > totalDigits) {
        totalDigits=minimumDigits;
      }
      boolean addedDecimalSeparator=false;
      for (int i=0; i < totalDigits; ++i) {
        if (i == integerDigits) {
          iFieldEnd=result.length();
          result.append(decimal);
          addedDecimalSeparator=true;
          fFieldStart=result.length();
        }
        result.append((i < digitList.count) ? (char)(digitList.digits[i] + zeroDelta) : zero);
      }
      if (decimalSeparatorAlwaysShown && totalDigits == integerDigits) {
        iFieldEnd=result.length();
        result.append(decimal);
        addedDecimalSeparator=true;
        fFieldStart=result.length();
      }
      if (iFieldEnd == -1) {
        iFieldEnd=result.length();
      }
      delegate.formatted(INTEGER_FIELD,Field.INTEGER,Field.INTEGER,iFieldStart,iFieldEnd,result);
      if (addedDecimalSeparator) {
        delegate.formatted(Field.DECIMAL_SEPARATOR,Field.DECIMAL_SEPARATOR,iFieldEnd,fFieldStart,result);
      }
      if (fFieldStart == -1) {
        fFieldStart=result.length();
      }
      delegate.formatted(FRACTION_FIELD,Field.FRACTION,Field.FRACTION,fFieldStart,result.length(),result);
      int fieldStart=result.length();
      result.append(symbols.getExponentSeparator());
      delegate.formatted(Field.EXPONENT_SYMBOL,Field.EXPONENT_SYMBOL,fieldStart,result.length(),result);
      if (digitList.isZero()) {
        exponent=0;
      }
      boolean negativeExponent=exponent < 0;
      if (negativeExponent) {
        exponent=-exponent;
        fieldStart=result.length();
        result.append(symbols.getMinusSign());
        delegate.formatted(Field.EXPONENT_SIGN,Field.EXPONENT_SIGN,fieldStart,result.length(),result);
      }
      digitList.set(negativeExponent,exponent);
      int eFieldStart=result.length();
      for (int i=digitList.decimalAt; i < minExponentDigits; ++i) {
        result.append(zero);
      }
      for (int i=0; i < digitList.decimalAt; ++i) {
        result.append((i < digitList.count) ? (char)(digitList.digits[i] + zeroDelta) : zero);
      }
      delegate.formatted(Field.EXPONENT,Field.EXPONENT,eFieldStart,result.length(),result);
    }
 else {
      int iFieldStart=result.length();
      int count=minIntDigits;
      int digitIndex=0;
      if (digitList.decimalAt > 0 && count < digitList.decimalAt) {
        count=digitList.decimalAt;
      }
      if (count > maxIntDigits) {
        count=maxIntDigits;
        digitIndex=digitList.decimalAt - count;
      }
      int sizeBeforeIntegerPart=result.length();
      for (int i=count - 1; i >= 0; --i) {
        if (i < digitList.decimalAt && digitIndex < digitList.count) {
          result.append((char)(digitList.digits[digitIndex++] + zeroDelta));
        }
 else {
          result.append(zero);
        }
        if (isGroupingUsed() && i > 0 && (groupingSize != 0) && (i % groupingSize == 0)) {
          int gStart=result.length();
          result.append(grouping);
          delegate.formatted(Field.GROUPING_SEPARATOR,Field.GROUPING_SEPARATOR,gStart,result.length(),result);
        }
      }
      boolean fractionPresent=(minFraDigits > 0) || (!isInteger && digitIndex < digitList.count);
      if (!fractionPresent && result.length() == sizeBeforeIntegerPart) {
        result.append(zero);
      }
      delegate.formatted(INTEGER_FIELD,Field.INTEGER,Field.INTEGER,iFieldStart,result.length(),result);
      int sStart=result.length();
      if (decimalSeparatorAlwaysShown || fractionPresent) {
        result.append(decimal);
      }
      if (sStart != result.length()) {
        delegate.formatted(Field.DECIMAL_SEPARATOR,Field.DECIMAL_SEPARATOR,sStart,result.length(),result);
      }
      int fFieldStart=result.length();
      for (int i=0; i < maxFraDigits; ++i) {
        if (i >= minFraDigits && (isInteger || digitIndex >= digitList.count)) {
          break;
        }
        if (-1 - i > (digitList.decimalAt - 1)) {
          result.append(zero);
          continue;
        }
        if (!isInteger && digitIndex < digitList.count) {
          result.append((char)(digitList.digits[digitIndex++] + zeroDelta));
        }
 else {
          result.append(zero);
        }
      }
      delegate.formatted(FRACTION_FIELD,Field.FRACTION,Field.FRACTION,fFieldStart,result.length(),result);
    }
    if (isNegative) {
      append(result,negativeSuffix,delegate,getNegativeSuffixFieldPositions(),Field.SIGN);
    }
 else {
      append(result,positiveSuffix,delegate,getPositiveSuffixFieldPositions(),Field.SIGN);
    }
    return result;
  }
  /** 
 * Appends the String <code>string</code> to <code>result</code>.
 * <code>delegate</code> is notified of all  the
 * <code>FieldPosition</code>s in <code>positions</code>.
 * <p>
 * If one of the <code>FieldPosition</code>s in <code>positions</code>
 * identifies a <code>SIGN</code> attribute, it is mapped to
 * <code>signAttribute</code>. This is used
 * to map the <code>SIGN</code> attribute to the <code>EXPONENT</code>
 * attribute as necessary.
 * <p>
 * This is used by <code>subformat</code> to add the prefix/suffix.
 */
  private void append(  StringBuffer result,  String string,  FieldDelegate delegate,  FieldPosition[] positions,  Format.Field signAttribute){
    int start=result.length();
    if (string.length() > 0) {
      result.append(string);
      for (int counter=0, max=positions.length; counter < max; counter++) {
        FieldPosition fp=positions[counter];
        Format.Field attribute=fp.getFieldAttribute();
        if (attribute == Field.SIGN) {
          attribute=signAttribute;
        }
        delegate.formatted(attribute,attribute,start + fp.getBeginIndex(),start + fp.getEndIndex(),result);
      }
    }
  }
  /** 
 * Parses text from a string to produce a <code>Number</code>.
 * <p>
 * The method attempts to parse text starting at the index given by
 * <code>pos</code>.
 * If parsing succeeds, then the index of <code>pos</code> is updated
 * to the index after the last character used (parsing does not necessarily
 * use all characters up to the end of the string), and the parsed
 * number is returned. The updated <code>pos</code> can be used to
 * indicate the starting point for the next call to this method.
 * If an error occurs, then the index of <code>pos</code> is not
 * changed, the error index of <code>pos</code> is set to the index of
 * the character where the error occurred, and null is returned.
 * <p>
 * The subclass returned depends on the value of {@link #isParseBigDecimal}as well as on the string being parsed.
 * <ul>
 * <li>If <code>isParseBigDecimal()</code> is false (the default),
 * most integer values are returned as <code>Long</code>
 * objects, no matter how they are written: <code>"17"</code> and
 * <code>"17.000"</code> both parse to <code>Long(17)</code>.
 * Values that cannot fit into a <code>Long</code> are returned as
 * <code>Double</code>s. This includes values with a fractional part,
 * infinite values, <code>NaN</code>, and the value -0.0.
 * <code>DecimalFormat</code> does <em>not</em> decide whether to
 * return a <code>Double</code> or a <code>Long</code> based on the
 * presence of a decimal separator in the source string. Doing so
 * would prevent integers that overflow the mantissa of a double,
 * such as <code>"-9,223,372,036,854,775,808.00"</code>, from being
 * parsed accurately.
 * <p>
 * Callers may use the <code>Number</code> methods
 * <code>doubleValue</code>, <code>longValue</code>, etc., to obtain
 * the type they want.
 * <li>If <code>isParseBigDecimal()</code> is true, values are returned
 * as <code>BigDecimal</code> objects. The values are the ones
 * constructed by {@link java.math.BigDecimal#BigDecimal(String)}for corresponding strings in locale-independent format. The
 * special cases negative and positive infinity and NaN are returned
 * as <code>Double</code> instances holding the values of the
 * corresponding <code>Double</code> constants.
 * </ul>
 * <p>
 * <code>DecimalFormat</code> parses all Unicode characters that represent
 * decimal digits, as defined by <code>Character.digit()</code>. In
 * addition, <code>DecimalFormat</code> also recognizes as digits the ten
 * consecutive characters starting with the localized zero digit defined in
 * the <code>DecimalFormatSymbols</code> object.
 * @param text the string to be parsed
 * @param pos  A <code>ParsePosition</code> object with index and error
 * index information as described above.
 * @return     the parsed value, or <code>null</code> if the parse fails
 * @exception NullPointerException if <code>text</code> or
 * <code>pos</code> is null.
 */
  public Number parse(  String text,  ParsePosition pos){
    if (text.regionMatches(pos.index,symbols.getNaN(),0,symbols.getNaN().length())) {
      pos.index=pos.index + symbols.getNaN().length();
      return new Double(Double.NaN);
    }
    boolean[] status=new boolean[STATUS_LENGTH];
    if (!subparse(text,pos,positivePrefix,negativePrefix,digitList,false,status)) {
      return null;
    }
    if (status[STATUS_INFINITE]) {
      if (status[STATUS_POSITIVE] == (multiplier >= 0)) {
        return new Double(Double.POSITIVE_INFINITY);
      }
 else {
        return new Double(Double.NEGATIVE_INFINITY);
      }
    }
    if (multiplier == 0) {
      if (digitList.isZero()) {
        return new Double(Double.NaN);
      }
 else       if (status[STATUS_POSITIVE]) {
        return new Double(Double.POSITIVE_INFINITY);
      }
 else {
        return new Double(Double.NEGATIVE_INFINITY);
      }
    }
    if (isParseBigDecimal()) {
      BigDecimal bigDecimalResult=digitList.getBigDecimal();
      if (multiplier != 1) {
        try {
          bigDecimalResult=bigDecimalResult.divide(getBigDecimalMultiplier());
        }
 catch (        ArithmeticException e) {
          bigDecimalResult=bigDecimalResult.divide(getBigDecimalMultiplier(),roundingMode);
        }
      }
      if (!status[STATUS_POSITIVE]) {
        bigDecimalResult=bigDecimalResult.negate();
      }
      return bigDecimalResult;
    }
 else {
      boolean gotDouble=true;
      boolean gotLongMinimum=false;
      double doubleResult=0.0;
      long longResult=0;
      if (digitList.fitsIntoLong(status[STATUS_POSITIVE],isParseIntegerOnly())) {
        gotDouble=false;
        longResult=digitList.getLong();
        if (longResult < 0) {
          gotLongMinimum=true;
        }
      }
 else {
        doubleResult=digitList.getDouble();
      }
      if (multiplier != 1) {
        if (gotDouble) {
          doubleResult/=multiplier;
        }
 else {
          if (longResult % multiplier == 0) {
            longResult/=multiplier;
          }
 else {
            doubleResult=((double)longResult) / multiplier;
            gotDouble=true;
          }
        }
      }
      if (!status[STATUS_POSITIVE] && !gotLongMinimum) {
        doubleResult=-doubleResult;
        longResult=-longResult;
      }
      if (multiplier != 1 && gotDouble) {
        longResult=(long)doubleResult;
        gotDouble=((doubleResult != (double)longResult) || (doubleResult == 0.0 && 1 / doubleResult < 0.0)) && !isParseIntegerOnly();
      }
      return gotDouble ? (Number)new Double(doubleResult) : (Number)new Long(longResult);
    }
  }
  /** 
 * Return a BigInteger multiplier.
 */
  private BigInteger getBigIntegerMultiplier(){
    if (bigIntegerMultiplier == null) {
      bigIntegerMultiplier=BigInteger.valueOf(multiplier);
    }
    return bigIntegerMultiplier;
  }
  private transient BigInteger bigIntegerMultiplier;
  /** 
 * Return a BigDecimal multiplier.
 */
  private BigDecimal getBigDecimalMultiplier(){
    if (bigDecimalMultiplier == null) {
      bigDecimalMultiplier=new BigDecimal(multiplier);
    }
    return bigDecimalMultiplier;
  }
  private transient BigDecimal bigDecimalMultiplier;
  private static final int STATUS_INFINITE=0;
  private static final int STATUS_POSITIVE=1;
  private static final int STATUS_LENGTH=2;
  /** 
 * Parse the given text into a number.  The text is parsed beginning at
 * parsePosition, until an unparseable character is seen.
 * @param text The string to parse.
 * @param parsePosition The position at which to being parsing.  Upon
 * return, the first unparseable character.
 * @param digits The DigitList to set to the parsed value.
 * @param isExponent If true, parse an exponent.  This means no
 * infinite values and integer only.
 * @param status Upon return contains boolean status flags indicating
 * whether the value was infinite and whether it was positive.
 */
  private final boolean subparse(  String text,  ParsePosition parsePosition,  String positivePrefix,  String negativePrefix,  DigitList digits,  boolean isExponent,  boolean status[]){
    int position=parsePosition.index;
    int oldStart=parsePosition.index;
    int backup;
    boolean gotPositive, gotNegative;
    gotPositive=text.regionMatches(position,positivePrefix,0,positivePrefix.length());
    gotNegative=text.regionMatches(position,negativePrefix,0,negativePrefix.length());
    if (gotPositive && gotNegative) {
      if (positivePrefix.length() > negativePrefix.length()) {
        gotNegative=false;
      }
 else       if (positivePrefix.length() < negativePrefix.length()) {
        gotPositive=false;
      }
    }
    if (gotPositive) {
      position+=positivePrefix.length();
    }
 else     if (gotNegative) {
      position+=negativePrefix.length();
    }
 else {
      parsePosition.errorIndex=position;
      return false;
    }
    status[STATUS_INFINITE]=false;
    if (!isExponent && text.regionMatches(position,symbols.getInfinity(),0,symbols.getInfinity().length())) {
      position+=symbols.getInfinity().length();
      status[STATUS_INFINITE]=true;
    }
 else {
      digits.decimalAt=digits.count=0;
      char zero=symbols.getZeroDigit();
      char decimal=isCurrencyFormat ? symbols.getMonetaryDecimalSeparator() : symbols.getDecimalSeparator();
      char grouping=symbols.getGroupingSeparator();
      String exponentString=symbols.getExponentSeparator();
      boolean sawDecimal=false;
      boolean sawExponent=false;
      boolean sawDigit=false;
      int exponent=0;
      int digitCount=0;
      backup=-1;
      for (; position < text.length(); ++position) {
        char ch=text.charAt(position);
        int digit=ch - zero;
        if (digit < 0 || digit > 9) {
          digit=Character.digit(ch,10);
        }
        if (digit == 0) {
          backup=-1;
          sawDigit=true;
          if (digits.count == 0) {
            if (!sawDecimal) {
              continue;
            }
            --digits.decimalAt;
          }
 else {
            ++digitCount;
            digits.append((char)(digit + '0'));
          }
        }
 else         if (digit > 0 && digit <= 9) {
          sawDigit=true;
          ++digitCount;
          digits.append((char)(digit + '0'));
          backup=-1;
        }
 else         if (!isExponent && ch == decimal) {
          if (isParseIntegerOnly() || sawDecimal) {
            break;
          }
          digits.decimalAt=digitCount;
          sawDecimal=true;
        }
 else         if (!isExponent && ch == grouping && isGroupingUsed()) {
          if (sawDecimal) {
            break;
          }
          backup=position;
        }
 else         if (!isExponent && text.regionMatches(position,exponentString,0,exponentString.length()) && !sawExponent) {
          ParsePosition pos=new ParsePosition(position + exponentString.length());
          boolean[] stat=new boolean[STATUS_LENGTH];
          DigitList exponentDigits=new DigitList();
          if (subparse(text,pos,"",Character.toString(symbols.getMinusSign()),exponentDigits,true,stat) && exponentDigits.fitsIntoLong(stat[STATUS_POSITIVE],true)) {
            position=pos.index;
            exponent=(int)exponentDigits.getLong();
            if (!stat[STATUS_POSITIVE]) {
              exponent=-exponent;
            }
            sawExponent=true;
          }
          break;
        }
 else {
          break;
        }
      }
      if (backup != -1) {
        position=backup;
      }
      if (!sawDecimal) {
        digits.decimalAt=digitCount;
      }
      digits.decimalAt+=exponent;
      if (!sawDigit && digitCount == 0) {
        parsePosition.index=oldStart;
        parsePosition.errorIndex=oldStart;
        return false;
      }
    }
    if (!isExponent) {
      if (gotPositive) {
        gotPositive=text.regionMatches(position,positiveSuffix,0,positiveSuffix.length());
      }
      if (gotNegative) {
        gotNegative=text.regionMatches(position,negativeSuffix,0,negativeSuffix.length());
      }
      if (gotPositive && gotNegative) {
        if (positiveSuffix.length() > negativeSuffix.length()) {
          gotNegative=false;
        }
 else         if (positiveSuffix.length() < negativeSuffix.length()) {
          gotPositive=false;
        }
      }
      if (gotPositive == gotNegative) {
        parsePosition.errorIndex=position;
        return false;
      }
      parsePosition.index=position + (gotPositive ? positiveSuffix.length() : negativeSuffix.length());
    }
 else {
      parsePosition.index=position;
    }
    status[STATUS_POSITIVE]=gotPositive;
    if (parsePosition.index == oldStart) {
      parsePosition.errorIndex=position;
      return false;
    }
    return true;
  }
  /** 
 * Returns a copy of the decimal format symbols, which is generally not
 * changed by the programmer or user.
 * @return a copy of the desired DecimalFormatSymbols
 * @see java.text.DecimalFormatSymbols
 */
  public DecimalFormatSymbols getDecimalFormatSymbols(){
    try {
      return (DecimalFormatSymbols)symbols.clone();
    }
 catch (    Exception foo) {
      return null;
    }
  }
  /** 
 * Sets the decimal format symbols, which is generally not changed
 * by the programmer or user.
 * @param newSymbols desired DecimalFormatSymbols
 * @see java.text.DecimalFormatSymbols
 */
  public void setDecimalFormatSymbols(  DecimalFormatSymbols newSymbols){
    try {
      symbols=(DecimalFormatSymbols)newSymbols.clone();
      expandAffixes();
    }
 catch (    Exception foo) {
    }
  }
  /** 
 * Get the positive prefix.
 * <P>Examples: +123, $123, sFr123
 */
  public String getPositivePrefix(){
    return positivePrefix;
  }
  /** 
 * Set the positive prefix.
 * <P>Examples: +123, $123, sFr123
 */
  public void setPositivePrefix(  String newValue){
    positivePrefix=newValue;
    posPrefixPattern=null;
    positivePrefixFieldPositions=null;
  }
  /** 
 * Returns the FieldPositions of the fields in the prefix used for
 * positive numbers. This is not used if the user has explicitly set
 * a positive prefix via <code>setPositivePrefix</code>. This is
 * lazily created.
 * @return FieldPositions in positive prefix
 */
  private FieldPosition[] getPositivePrefixFieldPositions(){
    if (positivePrefixFieldPositions == null) {
      if (posPrefixPattern != null) {
        positivePrefixFieldPositions=expandAffix(posPrefixPattern);
      }
 else {
        positivePrefixFieldPositions=EmptyFieldPositionArray;
      }
    }
    return positivePrefixFieldPositions;
  }
  /** 
 * Get the negative prefix.
 * <P>Examples: -123, ($123) (with negative suffix), sFr-123
 */
  public String getNegativePrefix(){
    return negativePrefix;
  }
  /** 
 * Set the negative prefix.
 * <P>Examples: -123, ($123) (with negative suffix), sFr-123
 */
  public void setNegativePrefix(  String newValue){
    negativePrefix=newValue;
    negPrefixPattern=null;
  }
  /** 
 * Returns the FieldPositions of the fields in the prefix used for
 * negative numbers. This is not used if the user has explicitly set
 * a negative prefix via <code>setNegativePrefix</code>. This is
 * lazily created.
 * @return FieldPositions in positive prefix
 */
  private FieldPosition[] getNegativePrefixFieldPositions(){
    if (negativePrefixFieldPositions == null) {
      if (negPrefixPattern != null) {
        negativePrefixFieldPositions=expandAffix(negPrefixPattern);
      }
 else {
        negativePrefixFieldPositions=EmptyFieldPositionArray;
      }
    }
    return negativePrefixFieldPositions;
  }
  /** 
 * Get the positive suffix.
 * <P>Example: 123%
 */
  public String getPositiveSuffix(){
    return positiveSuffix;
  }
  /** 
 * Set the positive suffix.
 * <P>Example: 123%
 */
  public void setPositiveSuffix(  String newValue){
    positiveSuffix=newValue;
    posSuffixPattern=null;
  }
  /** 
 * Returns the FieldPositions of the fields in the suffix used for
 * positive numbers. This is not used if the user has explicitly set
 * a positive suffix via <code>setPositiveSuffix</code>. This is
 * lazily created.
 * @return FieldPositions in positive prefix
 */
  private FieldPosition[] getPositiveSuffixFieldPositions(){
    if (positiveSuffixFieldPositions == null) {
      if (posSuffixPattern != null) {
        positiveSuffixFieldPositions=expandAffix(posSuffixPattern);
      }
 else {
        positiveSuffixFieldPositions=EmptyFieldPositionArray;
      }
    }
    return positiveSuffixFieldPositions;
  }
  /** 
 * Get the negative suffix.
 * <P>Examples: -123%, ($123) (with positive suffixes)
 */
  public String getNegativeSuffix(){
    return negativeSuffix;
  }
  /** 
 * Set the negative suffix.
 * <P>Examples: 123%
 */
  public void setNegativeSuffix(  String newValue){
    negativeSuffix=newValue;
    negSuffixPattern=null;
  }
  /** 
 * Returns the FieldPositions of the fields in the suffix used for
 * negative numbers. This is not used if the user has explicitly set
 * a negative suffix via <code>setNegativeSuffix</code>. This is
 * lazily created.
 * @return FieldPositions in positive prefix
 */
  private FieldPosition[] getNegativeSuffixFieldPositions(){
    if (negativeSuffixFieldPositions == null) {
      if (negSuffixPattern != null) {
        negativeSuffixFieldPositions=expandAffix(negSuffixPattern);
      }
 else {
        negativeSuffixFieldPositions=EmptyFieldPositionArray;
      }
    }
    return negativeSuffixFieldPositions;
  }
  /** 
 * Gets the multiplier for use in percent, per mille, and similar
 * formats.
 * @see #setMultiplier(int)
 */
  public int getMultiplier(){
    return multiplier;
  }
  /** 
 * Sets the multiplier for use in percent, per mille, and similar
 * formats.
 * For a percent format, set the multiplier to 100 and the suffixes to
 * have '%' (for Arabic, use the Arabic percent sign).
 * For a per mille format, set the multiplier to 1000 and the suffixes to
 * have '&#92;u2030'.
 * <P>Example: with multiplier 100, 1.23 is formatted as "123", and
 * "123" is parsed into 1.23.
 * @see #getMultiplier
 */
  public void setMultiplier(  int newValue){
    multiplier=newValue;
    bigDecimalMultiplier=null;
    bigIntegerMultiplier=null;
  }
  /** 
 * Return the grouping size. Grouping size is the number of digits between
 * grouping separators in the integer portion of a number.  For example,
 * in the number "123,456.78", the grouping size is 3.
 * @see #setGroupingSize
 * @see java.text.NumberFormat#isGroupingUsed
 * @see java.text.DecimalFormatSymbols#getGroupingSeparator
 */
  public int getGroupingSize(){
    return groupingSize;
  }
  /** 
 * Set the grouping size. Grouping size is the number of digits between
 * grouping separators in the integer portion of a number.  For example,
 * in the number "123,456.78", the grouping size is 3.
 * <br>
 * The value passed in is converted to a byte, which may lose information.
 * @see #getGroupingSize
 * @see java.text.NumberFormat#setGroupingUsed
 * @see java.text.DecimalFormatSymbols#setGroupingSeparator
 */
  public void setGroupingSize(  int newValue){
    groupingSize=(byte)newValue;
  }
  /** 
 * Allows you to get the behavior of the decimal separator with integers.
 * (The decimal separator will always appear with decimals.)
 * <P>Example: Decimal ON: 12345 -> 12345.; OFF: 12345 -> 12345
 */
  public boolean isDecimalSeparatorAlwaysShown(){
    return decimalSeparatorAlwaysShown;
  }
  /** 
 * Allows you to set the behavior of the decimal separator with integers.
 * (The decimal separator will always appear with decimals.)
 * <P>Example: Decimal ON: 12345 -> 12345.; OFF: 12345 -> 12345
 */
  public void setDecimalSeparatorAlwaysShown(  boolean newValue){
    decimalSeparatorAlwaysShown=newValue;
  }
  /** 
 * Returns whether the {@link #parse(java.lang.String,java.text.ParsePosition)}method returns <code>BigDecimal</code>. The default value is false.
 * @see #setParseBigDecimal
 * @since 1.5
 */
  public boolean isParseBigDecimal(){
    return parseBigDecimal;
  }
  /** 
 * Sets whether the {@link #parse(java.lang.String,java.text.ParsePosition)}method returns <code>BigDecimal</code>.
 * @see #isParseBigDecimal
 * @since 1.5
 */
  public void setParseBigDecimal(  boolean newValue){
    parseBigDecimal=newValue;
  }
  /** 
 * Standard override; no change in semantics.
 */
  public Object clone(){
    try {
      DecimalFormat other=(DecimalFormat)super.clone();
      other.symbols=(DecimalFormatSymbols)symbols.clone();
      other.digitList=(DigitList)digitList.clone();
      return other;
    }
 catch (    Exception e) {
      throw new InternalError();
    }
  }
  /** 
 * Overrides equals
 */
  public boolean equals(  Object obj){
    if (obj == null)     return false;
    if (!super.equals(obj))     return false;
    DecimalFormat other=(DecimalFormat)obj;
    return ((posPrefixPattern == other.posPrefixPattern && positivePrefix.equals(other.positivePrefix)) || (posPrefixPattern != null && posPrefixPattern.equals(other.posPrefixPattern))) && ((posSuffixPattern == other.posSuffixPattern && positiveSuffix.equals(other.positiveSuffix)) || (posSuffixPattern != null && posSuffixPattern.equals(other.posSuffixPattern))) && ((negPrefixPattern == other.negPrefixPattern && negativePrefix.equals(other.negativePrefix)) || (negPrefixPattern != null && negPrefixPattern.equals(other.negPrefixPattern)))&& ((negSuffixPattern == other.negSuffixPattern && negativeSuffix.equals(other.negativeSuffix)) || (negSuffixPattern != null && negSuffixPattern.equals(other.negSuffixPattern)))&& multiplier == other.multiplier && groupingSize == other.groupingSize && decimalSeparatorAlwaysShown == other.decimalSeparatorAlwaysShown && parseBigDecimal == other.parseBigDecimal && useExponentialNotation == other.useExponentialNotation && (!useExponentialNotation || minExponentDigits == other.minExponentDigits) && maximumIntegerDigits == other.maximumIntegerDigits && minimumIntegerDigits == other.minimumIntegerDigits && maximumFractionDigits == other.maximumFractionDigits && minimumFractionDigits == other.minimumFractionDigits && roundingMode == other.roundingMode && symbols.equals(other.symbols);
  }
  /** 
 * Overrides hashCode
 */
  public int hashCode(){
    return super.hashCode() * 37 + positivePrefix.hashCode();
  }
  /** 
 * Synthesizes a pattern string that represents the current state
 * of this Format object.
 * @see #applyPattern
 */
  public String toPattern(){
    return toPattern(false);
  }
  /** 
 * Synthesizes a localized pattern string that represents the current
 * state of this Format object.
 * @see #applyPattern
 */
  public String toLocalizedPattern(){
    return toPattern(true);
  }
  /** 
 * Expand the affix pattern strings into the expanded affix strings.  If any
 * affix pattern string is null, do not expand it.  This method should be
 * called any time the symbols or the affix patterns change in order to keep
 * the expanded affix strings up to date.
 */
  private void expandAffixes(){
    StringBuffer buffer=new StringBuffer();
    if (posPrefixPattern != null) {
      positivePrefix=expandAffix(posPrefixPattern,buffer);
      positivePrefixFieldPositions=null;
    }
    if (posSuffixPattern != null) {
      positiveSuffix=expandAffix(posSuffixPattern,buffer);
      positiveSuffixFieldPositions=null;
    }
    if (negPrefixPattern != null) {
      negativePrefix=expandAffix(negPrefixPattern,buffer);
      negativePrefixFieldPositions=null;
    }
    if (negSuffixPattern != null) {
      negativeSuffix=expandAffix(negSuffixPattern,buffer);
      negativeSuffixFieldPositions=null;
    }
  }
  /** 
 * Expand an affix pattern into an affix string.  All characters in the
 * pattern are literal unless prefixed by QUOTE.  The following characters
 * after QUOTE are recognized: PATTERN_PERCENT, PATTERN_PER_MILLE,
 * PATTERN_MINUS, and CURRENCY_SIGN.  If CURRENCY_SIGN is doubled (QUOTE +
 * CURRENCY_SIGN + CURRENCY_SIGN), it is interpreted as an ISO 4217
 * currency code.  Any other character after a QUOTE represents itself.
 * QUOTE must be followed by another character; QUOTE may not occur by
 * itself at the end of the pattern.
 * @param pattern the non-null, possibly empty pattern
 * @param buffer a scratch StringBuffer; its contents will be lost
 * @return the expanded equivalent of pattern
 */
  private String expandAffix(  String pattern,  StringBuffer buffer){
    buffer.setLength(0);
    for (int i=0; i < pattern.length(); ) {
      char c=pattern.charAt(i++);
      if (c == QUOTE) {
        c=pattern.charAt(i++);
switch (c) {
case CURRENCY_SIGN:
          if (i < pattern.length() && pattern.charAt(i) == CURRENCY_SIGN) {
            ++i;
            buffer.append(symbols.getInternationalCurrencySymbol());
          }
 else {
            buffer.append(symbols.getCurrencySymbol());
          }
        continue;
case PATTERN_PERCENT:
      c=symbols.getPercent();
    break;
case PATTERN_PER_MILLE:
  c=symbols.getPerMill();
break;
case PATTERN_MINUS:
c=symbols.getMinusSign();
break;
}
}
buffer.append(c);
}
return buffer.toString();
}
/** 
 * Expand an affix pattern into an array of FieldPositions describing
 * how the pattern would be expanded.
 * All characters in the
 * pattern are literal unless prefixed by QUOTE.  The following characters
 * after QUOTE are recognized: PATTERN_PERCENT, PATTERN_PER_MILLE,
 * PATTERN_MINUS, and CURRENCY_SIGN.  If CURRENCY_SIGN is doubled (QUOTE +
 * CURRENCY_SIGN + CURRENCY_SIGN), it is interpreted as an ISO 4217
 * currency code.  Any other character after a QUOTE represents itself.
 * QUOTE must be followed by another character; QUOTE may not occur by
 * itself at the end of the pattern.
 * @param pattern the non-null, possibly empty pattern
 * @return FieldPosition array of the resulting fields.
 */
private FieldPosition[] expandAffix(String pattern){
ArrayList positions=null;
int stringIndex=0;
for (int i=0; i < pattern.length(); ) {
char c=pattern.charAt(i++);
if (c == QUOTE) {
int field=-1;
Format.Field fieldID=null;
c=pattern.charAt(i++);
switch (c) {
case CURRENCY_SIGN:
String string;
if (i < pattern.length() && pattern.charAt(i) == CURRENCY_SIGN) {
++i;
string=symbols.getInternationalCurrencySymbol();
}
 else {
string=symbols.getCurrencySymbol();
}
if (string.length() > 0) {
if (positions == null) {
positions=new ArrayList(2);
}
FieldPosition fp=new FieldPosition(Field.CURRENCY);
fp.setBeginIndex(stringIndex);
fp.setEndIndex(stringIndex + string.length());
positions.add(fp);
stringIndex+=string.length();
}
continue;
case PATTERN_PERCENT:
c=symbols.getPercent();
field=-1;
fieldID=Field.PERCENT;
break;
case PATTERN_PER_MILLE:
c=symbols.getPerMill();
field=-1;
fieldID=Field.PERMILLE;
break;
case PATTERN_MINUS:
c=symbols.getMinusSign();
field=-1;
fieldID=Field.SIGN;
break;
}
if (fieldID != null) {
if (positions == null) {
positions=new ArrayList(2);
}
FieldPosition fp=new FieldPosition(fieldID,field);
fp.setBeginIndex(stringIndex);
fp.setEndIndex(stringIndex + 1);
positions.add(fp);
}
}
stringIndex++;
}
if (positions != null) {
return (FieldPosition[])positions.toArray(EmptyFieldPositionArray);
}
return EmptyFieldPositionArray;
}
/** 
 * Appends an affix pattern to the given StringBuffer, quoting special
 * characters as needed.  Uses the internal affix pattern, if that exists,
 * or the literal affix, if the internal affix pattern is null.  The
 * appended string will generate the same affix pattern (or literal affix)
 * when passed to toPattern().
 * @param buffer the affix string is appended to this
 * @param affixPattern a pattern such as posPrefixPattern; may be null
 * @param expAffix a corresponding expanded affix, such as positivePrefix.
 * Ignored unless affixPattern is null.  If affixPattern is null, then
 * expAffix is appended as a literal affix.
 * @param localized true if the appended pattern should contain localized
 * pattern characters; otherwise, non-localized pattern chars are appended
 */
private void appendAffix(StringBuffer buffer,String affixPattern,String expAffix,boolean localized){
if (affixPattern == null) {
appendAffix(buffer,expAffix,localized);
}
 else {
int i;
for (int pos=0; pos < affixPattern.length(); pos=i) {
i=affixPattern.indexOf(QUOTE,pos);
if (i < 0) {
appendAffix(buffer,affixPattern.substring(pos),localized);
break;
}
if (i > pos) {
appendAffix(buffer,affixPattern.substring(pos,i),localized);
}
char c=affixPattern.charAt(++i);
++i;
if (c == QUOTE) {
buffer.append(c);
}
 else if (c == CURRENCY_SIGN && i < affixPattern.length() && affixPattern.charAt(i) == CURRENCY_SIGN) {
++i;
buffer.append(c);
}
 else if (localized) {
switch (c) {
case PATTERN_PERCENT:
c=symbols.getPercent();
break;
case PATTERN_PER_MILLE:
c=symbols.getPerMill();
break;
case PATTERN_MINUS:
c=symbols.getMinusSign();
break;
}
}
buffer.append(c);
}
}
}
/** 
 * Append an affix to the given StringBuffer, using quotes if
 * there are special characters.  Single quotes themselves must be
 * escaped in either case.
 */
private void appendAffix(StringBuffer buffer,String affix,boolean localized){
boolean needQuote;
if (localized) {
needQuote=affix.indexOf(symbols.getZeroDigit()) >= 0 || affix.indexOf(symbols.getGroupingSeparator()) >= 0 || affix.indexOf(symbols.getDecimalSeparator()) >= 0 || affix.indexOf(symbols.getPercent()) >= 0 || affix.indexOf(symbols.getPerMill()) >= 0 || affix.indexOf(symbols.getDigit()) >= 0 || affix.indexOf(symbols.getPatternSeparator()) >= 0 || affix.indexOf(symbols.getMinusSign()) >= 0 || affix.indexOf(CURRENCY_SIGN) >= 0;
}
 else {
needQuote=affix.indexOf(PATTERN_ZERO_DIGIT) >= 0 || affix.indexOf(PATTERN_GROUPING_SEPARATOR) >= 0 || affix.indexOf(PATTERN_DECIMAL_SEPARATOR) >= 0 || affix.indexOf(PATTERN_PERCENT) >= 0 || affix.indexOf(PATTERN_PER_MILLE) >= 0 || affix.indexOf(PATTERN_DIGIT) >= 0 || affix.indexOf(PATTERN_SEPARATOR) >= 0 || affix.indexOf(PATTERN_MINUS) >= 0 || affix.indexOf(CURRENCY_SIGN) >= 0;
}
if (needQuote) buffer.append('\'');
if (affix.indexOf('\'') < 0) buffer.append(affix);
 else {
for (int j=0; j < affix.length(); ++j) {
char c=affix.charAt(j);
buffer.append(c);
if (c == '\'') buffer.append(c);
}
}
if (needQuote) buffer.append('\'');
}
/** 
 * Does the real work of generating a pattern.  
 */
private String toPattern(boolean localized){
StringBuffer result=new StringBuffer();
for (int j=1; j >= 0; --j) {
if (j == 1) appendAffix(result,posPrefixPattern,positivePrefix,localized);
 else appendAffix(result,negPrefixPattern,negativePrefix,localized);
int i;
int digitCount=useExponentialNotation ? getMaximumIntegerDigits() : Math.max(groupingSize,getMinimumIntegerDigits()) + 1;
for (i=digitCount; i > 0; --i) {
if (i != digitCount && isGroupingUsed() && groupingSize != 0 && i % groupingSize == 0) {
result.append(localized ? symbols.getGroupingSeparator() : PATTERN_GROUPING_SEPARATOR);
}
result.append(i <= getMinimumIntegerDigits() ? (localized ? symbols.getZeroDigit() : PATTERN_ZERO_DIGIT) : (localized ? symbols.getDigit() : PATTERN_DIGIT));
}
if (getMaximumFractionDigits() > 0 || decimalSeparatorAlwaysShown) result.append(localized ? symbols.getDecimalSeparator() : PATTERN_DECIMAL_SEPARATOR);
for (i=0; i < getMaximumFractionDigits(); ++i) {
if (i < getMinimumFractionDigits()) {
result.append(localized ? symbols.getZeroDigit() : PATTERN_ZERO_DIGIT);
}
 else {
result.append(localized ? symbols.getDigit() : PATTERN_DIGIT);
}
}
if (useExponentialNotation) {
result.append(localized ? symbols.getExponentSeparator() : PATTERN_EXPONENT);
for (i=0; i < minExponentDigits; ++i) result.append(localized ? symbols.getZeroDigit() : PATTERN_ZERO_DIGIT);
}
if (j == 1) {
appendAffix(result,posSuffixPattern,positiveSuffix,localized);
if ((negSuffixPattern == posSuffixPattern && negativeSuffix.equals(positiveSuffix)) || (negSuffixPattern != null && negSuffixPattern.equals(posSuffixPattern))) {
if ((negPrefixPattern != null && posPrefixPattern != null && negPrefixPattern.equals("'-" + posPrefixPattern)) || (negPrefixPattern == posPrefixPattern && negativePrefix.equals(symbols.getMinusSign() + positivePrefix))) break;
}
result.append(localized ? symbols.getPatternSeparator() : PATTERN_SEPARATOR);
}
 else appendAffix(result,negSuffixPattern,negativeSuffix,localized);
}
return result.toString();
}
/** 
 * Apply the given pattern to this Format object.  A pattern is a
 * short-hand specification for the various formatting properties.
 * These properties can also be changed individually through the
 * various setter methods.
 * <p>
 * There is no limit to integer digits set
 * by this routine, since that is the typical end-user desire;
 * use setMaximumInteger if you want to set a real value.
 * For negative numbers, use a second pattern, separated by a semicolon
 * <P>Example <code>"#,#00.0#"</code> -> 1,234.56
 * <P>This means a minimum of 2 integer digits, 1 fraction digit, and
 * a maximum of 2 fraction digits.
 * <p>Example: <code>"#,#00.0#;(#,#00.0#)"</code> for negatives in
 * parentheses.
 * <p>In negative patterns, the minimum and maximum counts are ignored;
 * these are presumed to be set in the positive pattern.
 * @exception NullPointerException if <code>pattern</code> is null
 * @exception IllegalArgumentException if the given pattern is invalid.
 */
public void applyPattern(String pattern){
applyPattern(pattern,false);
}
/** 
 * Apply the given pattern to this Format object.  The pattern
 * is assumed to be in a localized notation. A pattern is a
 * short-hand specification for the various formatting properties.
 * These properties can also be changed individually through the
 * various setter methods.
 * <p>
 * There is no limit to integer digits set
 * by this routine, since that is the typical end-user desire;
 * use setMaximumInteger if you want to set a real value.
 * For negative numbers, use a second pattern, separated by a semicolon
 * <P>Example <code>"#,#00.0#"</code> -> 1,234.56
 * <P>This means a minimum of 2 integer digits, 1 fraction digit, and
 * a maximum of 2 fraction digits.
 * <p>Example: <code>"#,#00.0#;(#,#00.0#)"</code> for negatives in
 * parentheses.
 * <p>In negative patterns, the minimum and maximum counts are ignored;
 * these are presumed to be set in the positive pattern.
 * @exception NullPointerException if <code>pattern</code> is null
 * @exception IllegalArgumentException if the given pattern is invalid.
 */
public void applyLocalizedPattern(String pattern){
applyPattern(pattern,true);
}
/** 
 * Does the real work of applying a pattern.
 */
private void applyPattern(String pattern,boolean localized){
char zeroDigit=PATTERN_ZERO_DIGIT;
char groupingSeparator=PATTERN_GROUPING_SEPARATOR;
char decimalSeparator=PATTERN_DECIMAL_SEPARATOR;
char percent=PATTERN_PERCENT;
char perMill=PATTERN_PER_MILLE;
char digit=PATTERN_DIGIT;
char separator=PATTERN_SEPARATOR;
String exponent=PATTERN_EXPONENT;
char minus=PATTERN_MINUS;
if (localized) {
zeroDigit=symbols.getZeroDigit();
groupingSeparator=symbols.getGroupingSeparator();
decimalSeparator=symbols.getDecimalSeparator();
percent=symbols.getPercent();
perMill=symbols.getPerMill();
digit=symbols.getDigit();
separator=symbols.getPatternSeparator();
exponent=symbols.getExponentSeparator();
minus=symbols.getMinusSign();
}
boolean gotNegative=false;
decimalSeparatorAlwaysShown=false;
isCurrencyFormat=false;
useExponentialNotation=false;
int phaseOneStart=0;
int phaseOneLength=0;
int start=0;
for (int j=1; j >= 0 && start < pattern.length(); --j) {
boolean inQuote=false;
StringBuffer prefix=new StringBuffer();
StringBuffer suffix=new StringBuffer();
int decimalPos=-1;
int multiplier=1;
int digitLeftCount=0, zeroDigitCount=0, digitRightCount=0;
byte groupingCount=-1;
int phase=0;
StringBuffer affix=prefix;
for (int pos=start; pos < pattern.length(); ++pos) {
char ch=pattern.charAt(pos);
switch (phase) {
case 0:
case 2:
if (inQuote) {
if (ch == QUOTE) {
if ((pos + 1) < pattern.length() && pattern.charAt(pos + 1) == QUOTE) {
++pos;
affix.append("''");
}
 else {
inQuote=false;
}
continue;
}
}
 else {
if (ch == digit || ch == zeroDigit || ch == groupingSeparator || ch == decimalSeparator) {
phase=1;
if (j == 1) {
phaseOneStart=pos;
}
--pos;
continue;
}
 else if (ch == CURRENCY_SIGN) {
boolean doubled=(pos + 1) < pattern.length() && pattern.charAt(pos + 1) == CURRENCY_SIGN;
if (doubled) {
++pos;
}
isCurrencyFormat=true;
affix.append(doubled ? "'\u00A4\u00A4" : "'\u00A4");
continue;
}
 else if (ch == QUOTE) {
if (ch == QUOTE) {
if ((pos + 1) < pattern.length() && pattern.charAt(pos + 1) == QUOTE) {
++pos;
affix.append("''");
}
 else {
inQuote=true;
}
continue;
}
}
 else if (ch == separator) {
if (phase == 0 || j == 0) {
throw new IllegalArgumentException("Unquoted special character '" + ch + "' in pattern \""+ pattern+ '"');
}
start=pos + 1;
pos=pattern.length();
continue;
}
 else if (ch == percent) {
if (multiplier != 1) {
throw new IllegalArgumentException("Too many percent/per mille characters in pattern \"" + pattern + '"');
}
multiplier=100;
affix.append("'%");
continue;
}
 else if (ch == perMill) {
if (multiplier != 1) {
throw new IllegalArgumentException("Too many percent/per mille characters in pattern \"" + pattern + '"');
}
multiplier=1000;
affix.append("'\u2030");
continue;
}
 else if (ch == minus) {
affix.append("'-");
continue;
}
}
affix.append(ch);
break;
case 1:
if (j == 1) {
++phaseOneLength;
}
 else {
if (--phaseOneLength == 0) {
phase=2;
affix=suffix;
}
continue;
}
if (ch == digit) {
if (zeroDigitCount > 0) {
++digitRightCount;
}
 else {
++digitLeftCount;
}
if (groupingCount >= 0 && decimalPos < 0) {
++groupingCount;
}
}
 else if (ch == zeroDigit) {
if (digitRightCount > 0) {
throw new IllegalArgumentException("Unexpected '0' in pattern \"" + pattern + '"');
}
++zeroDigitCount;
if (groupingCount >= 0 && decimalPos < 0) {
++groupingCount;
}
}
 else if (ch == groupingSeparator) {
groupingCount=0;
}
 else if (ch == decimalSeparator) {
if (decimalPos >= 0) {
throw new IllegalArgumentException("Multiple decimal separators in pattern \"" + pattern + '"');
}
decimalPos=digitLeftCount + zeroDigitCount + digitRightCount;
}
 else if (pattern.regionMatches(pos,exponent,0,exponent.length())) {
if (useExponentialNotation) {
throw new IllegalArgumentException("Multiple exponential " + "symbols in pattern \"" + pattern + '"');
}
useExponentialNotation=true;
minExponentDigits=0;
pos=pos + exponent.length();
while (pos < pattern.length() && pattern.charAt(pos) == zeroDigit) {
++minExponentDigits;
++phaseOneLength;
++pos;
}
if ((digitLeftCount + zeroDigitCount) < 1 || minExponentDigits < 1) {
throw new IllegalArgumentException("Malformed exponential " + "pattern \"" + pattern + '"');
}
phase=2;
affix=suffix;
--pos;
continue;
}
 else {
phase=2;
affix=suffix;
--pos;
--phaseOneLength;
continue;
}
break;
}
}
if (zeroDigitCount == 0 && digitLeftCount > 0 && decimalPos >= 0) {
int n=decimalPos;
if (n == 0) {
++n;
}
digitRightCount=digitLeftCount - n;
digitLeftCount=n - 1;
zeroDigitCount=1;
}
if ((decimalPos < 0 && digitRightCount > 0) || (decimalPos >= 0 && (decimalPos < digitLeftCount || decimalPos > (digitLeftCount + zeroDigitCount))) || groupingCount == 0 || inQuote) {
throw new IllegalArgumentException("Malformed pattern \"" + pattern + '"');
}
if (j == 1) {
posPrefixPattern=prefix.toString();
posSuffixPattern=suffix.toString();
negPrefixPattern=posPrefixPattern;
negSuffixPattern=posSuffixPattern;
int digitTotalCount=digitLeftCount + zeroDigitCount + digitRightCount;
int effectiveDecimalPos=decimalPos >= 0 ? decimalPos : digitTotalCount;
setMinimumIntegerDigits(effectiveDecimalPos - digitLeftCount);
setMaximumIntegerDigits(useExponentialNotation ? digitLeftCount + getMinimumIntegerDigits() : MAXIMUM_INTEGER_DIGITS);
setMaximumFractionDigits(decimalPos >= 0 ? (digitTotalCount - decimalPos) : 0);
setMinimumFractionDigits(decimalPos >= 0 ? (digitLeftCount + zeroDigitCount - decimalPos) : 0);
setGroupingUsed(groupingCount > 0);
this.groupingSize=(groupingCount > 0) ? groupingCount : 0;
this.multiplier=multiplier;
setDecimalSeparatorAlwaysShown(decimalPos == 0 || decimalPos == digitTotalCount);
}
 else {
negPrefixPattern=prefix.toString();
negSuffixPattern=suffix.toString();
gotNegative=true;
}
}
if (pattern.length() == 0) {
posPrefixPattern=posSuffixPattern="";
setMinimumIntegerDigits(0);
setMaximumIntegerDigits(MAXIMUM_INTEGER_DIGITS);
setMinimumFractionDigits(0);
setMaximumFractionDigits(MAXIMUM_FRACTION_DIGITS);
}
if (!gotNegative || (negPrefixPattern.equals(posPrefixPattern) && negSuffixPattern.equals(posSuffixPattern))) {
negSuffixPattern=posSuffixPattern;
negPrefixPattern="'-" + posPrefixPattern;
}
expandAffixes();
}
/** 
 * Sets the maximum number of digits allowed in the integer portion of a
 * number.
 * For formatting numbers other than <code>BigInteger</code> and
 * <code>BigDecimal</code> objects, the lower of <code>newValue</code> and
 * 309 is used. Negative input values are replaced with 0.
 * @see NumberFormat#setMaximumIntegerDigits
 */
public void setMaximumIntegerDigits(int newValue){
maximumIntegerDigits=Math.min(Math.max(0,newValue),MAXIMUM_INTEGER_DIGITS);
super.setMaximumIntegerDigits((maximumIntegerDigits > DOUBLE_INTEGER_DIGITS) ? DOUBLE_INTEGER_DIGITS : maximumIntegerDigits);
if (minimumIntegerDigits > maximumIntegerDigits) {
minimumIntegerDigits=maximumIntegerDigits;
super.setMinimumIntegerDigits((minimumIntegerDigits > DOUBLE_INTEGER_DIGITS) ? DOUBLE_INTEGER_DIGITS : minimumIntegerDigits);
}
}
/** 
 * Sets the minimum number of digits allowed in the integer portion of a
 * number.
 * For formatting numbers other than <code>BigInteger</code> and
 * <code>BigDecimal</code> objects, the lower of <code>newValue</code> and
 * 309 is used. Negative input values are replaced with 0.
 * @see NumberFormat#setMinimumIntegerDigits
 */
public void setMinimumIntegerDigits(int newValue){
minimumIntegerDigits=Math.min(Math.max(0,newValue),MAXIMUM_INTEGER_DIGITS);
super.setMinimumIntegerDigits((minimumIntegerDigits > DOUBLE_INTEGER_DIGITS) ? DOUBLE_INTEGER_DIGITS : minimumIntegerDigits);
if (minimumIntegerDigits > maximumIntegerDigits) {
maximumIntegerDigits=minimumIntegerDigits;
super.setMaximumIntegerDigits((maximumIntegerDigits > DOUBLE_INTEGER_DIGITS) ? DOUBLE_INTEGER_DIGITS : maximumIntegerDigits);
}
}
/** 
 * Sets the maximum number of digits allowed in the fraction portion of a
 * number.
 * For formatting numbers other than <code>BigInteger</code> and
 * <code>BigDecimal</code> objects, the lower of <code>newValue</code> and
 * 340 is used. Negative input values are replaced with 0.
 * @see NumberFormat#setMaximumFractionDigits
 */
public void setMaximumFractionDigits(int newValue){
maximumFractionDigits=Math.min(Math.max(0,newValue),MAXIMUM_FRACTION_DIGITS);
super.setMaximumFractionDigits((maximumFractionDigits > DOUBLE_FRACTION_DIGITS) ? DOUBLE_FRACTION_DIGITS : maximumFractionDigits);
if (minimumFractionDigits > maximumFractionDigits) {
minimumFractionDigits=maximumFractionDigits;
super.setMinimumFractionDigits((minimumFractionDigits > DOUBLE_FRACTION_DIGITS) ? DOUBLE_FRACTION_DIGITS : minimumFractionDigits);
}
}
/** 
 * Sets the minimum number of digits allowed in the fraction portion of a
 * number.
 * For formatting numbers other than <code>BigInteger</code> and
 * <code>BigDecimal</code> objects, the lower of <code>newValue</code> and
 * 340 is used. Negative input values are replaced with 0.
 * @see NumberFormat#setMinimumFractionDigits
 */
public void setMinimumFractionDigits(int newValue){
minimumFractionDigits=Math.min(Math.max(0,newValue),MAXIMUM_FRACTION_DIGITS);
super.setMinimumFractionDigits((minimumFractionDigits > DOUBLE_FRACTION_DIGITS) ? DOUBLE_FRACTION_DIGITS : minimumFractionDigits);
if (minimumFractionDigits > maximumFractionDigits) {
maximumFractionDigits=minimumFractionDigits;
super.setMaximumFractionDigits((maximumFractionDigits > DOUBLE_FRACTION_DIGITS) ? DOUBLE_FRACTION_DIGITS : maximumFractionDigits);
}
}
/** 
 * Gets the maximum number of digits allowed in the integer portion of a
 * number.
 * For formatting numbers other than <code>BigInteger</code> and
 * <code>BigDecimal</code> objects, the lower of the return value and
 * 309 is used.
 * @see #setMaximumIntegerDigits
 */
public int getMaximumIntegerDigits(){
return maximumIntegerDigits;
}
/** 
 * Gets the minimum number of digits allowed in the integer portion of a
 * number.
 * For formatting numbers other than <code>BigInteger</code> and
 * <code>BigDecimal</code> objects, the lower of the return value and
 * 309 is used.
 * @see #setMinimumIntegerDigits
 */
public int getMinimumIntegerDigits(){
return minimumIntegerDigits;
}
/** 
 * Gets the maximum number of digits allowed in the fraction portion of a
 * number.
 * For formatting numbers other than <code>BigInteger</code> and
 * <code>BigDecimal</code> objects, the lower of the return value and
 * 340 is used.
 * @see #setMaximumFractionDigits
 */
public int getMaximumFractionDigits(){
return maximumFractionDigits;
}
/** 
 * Gets the minimum number of digits allowed in the fraction portion of a
 * number.
 * For formatting numbers other than <code>BigInteger</code> and
 * <code>BigDecimal</code> objects, the lower of the return value and
 * 340 is used.
 * @see #setMinimumFractionDigits
 */
public int getMinimumFractionDigits(){
return minimumFractionDigits;
}
/** 
 * Gets the currency used by this decimal format when formatting
 * currency values.
 * The currency is obtained by calling{@link DecimalFormatSymbols#getCurrency DecimalFormatSymbols.getCurrency}on this number format's symbols.
 * @return the currency used by this decimal format, or <code>null</code>
 * @since 1.4
 */
public Currency getCurrency(){
return symbols.getCurrency();
}
/** 
 * Sets the currency used by this number format when formatting
 * currency values. This does not update the minimum or maximum
 * number of fraction digits used by the number format.
 * The currency is set by calling{@link DecimalFormatSymbols#setCurrency DecimalFormatSymbols.setCurrency}on this number format's symbols.
 * @param currency the new currency to be used by this decimal format
 * @exception NullPointerException if <code>currency</code> is null
 * @since 1.4
 */
public void setCurrency(Currency currency){
if (currency != symbols.getCurrency()) {
symbols.setCurrency(currency);
if (isCurrencyFormat) {
expandAffixes();
}
}
}
/** 
 * Gets the {@link java.math.RoundingMode} used in this DecimalFormat.
 * @return The <code>RoundingMode</code> used for this DecimalFormat.
 * @see #setRoundingMode(RoundingMode)
 * @since 1.6
 */
public RoundingMode getRoundingMode(){
return roundingMode;
}
/** 
 * Sets the {@link java.math.RoundingMode} used in this DecimalFormat.
 * @param roundingMode The <code>RoundingMode</code> to be used
 * @see #getRoundingMode()
 * @exception NullPointerException if <code>roundingMode</code> is null.
 * @since 1.6
 */
public void setRoundingMode(RoundingMode roundingMode){
if (roundingMode == null) {
throw new NullPointerException();
}
this.roundingMode=roundingMode;
digitList.setRoundingMode(roundingMode);
}
/** 
 * Adjusts the minimum and maximum fraction digits to values that
 * are reasonable for the currency's default fraction digits.
 */
void adjustForCurrencyDefaultFractionDigits(){
Currency currency=symbols.getCurrency();
if (currency == null) {
try {
currency=Currency.getInstance(symbols.getInternationalCurrencySymbol());
}
 catch (IllegalArgumentException e) {
}
}
if (currency != null) {
int digits=currency.getDefaultFractionDigits();
if (digits != -1) {
int oldMinDigits=getMinimumFractionDigits();
if (oldMinDigits == getMaximumFractionDigits()) {
setMinimumFractionDigits(digits);
setMaximumFractionDigits(digits);
}
 else {
setMinimumFractionDigits(Math.min(digits,oldMinDigits));
setMaximumFractionDigits(digits);
}
}
}
}
/** 
 * Reads the default serializable fields from the stream and performs
 * validations and adjustments for older serialized versions. The
 * validations and adjustments are:
 * <ol>
 * <li>
 * Verify that the superclass's digit count fields correctly reflect
 * the limits imposed on formatting numbers other than
 * <code>BigInteger</code> and <code>BigDecimal</code> objects. These
 * limits are stored in the superclass for serialization compatibility
 * with older versions, while the limits for <code>BigInteger</code> and
 * <code>BigDecimal</code> objects are kept in this class.
 * If, in the superclass, the minimum or maximum integer digit count is
 * larger than <code>DOUBLE_INTEGER_DIGITS</code> or if the minimum or
 * maximum fraction digit count is larger than
 * <code>DOUBLE_FRACTION_DIGITS</code>, then the stream data is invalid
 * and this method throws an <code>InvalidObjectException</code>.
 * <li>
 * If <code>serialVersionOnStream</code> is less than 4, initialize
 * <code>roundingMode</code> to {@link java.math.RoundingMode#HALF_EVENRoundingMode.HALF_EVEN}.  This field is new with version 4.
 * <li>
 * If <code>serialVersionOnStream</code> is less than 3, then call
 * the setters for the minimum and maximum integer and fraction digits with
 * the values of the corresponding superclass getters to initialize the
 * fields in this class. The fields in this class are new with version 3.
 * <li>
 * If <code>serialVersionOnStream</code> is less than 1, indicating that
 * the stream was written by JDK 1.1, initialize
 * <code>useExponentialNotation</code>
 * to false, since it was not present in JDK 1.1.
 * <li>
 * Set <code>serialVersionOnStream</code> to the maximum allowed value so
 * that default serialization will work properly if this object is streamed
 * out again.
 * </ol>
 * <p>Stream versions older than 2 will not have the affix pattern variables
 * <code>posPrefixPattern</code> etc.  As a result, they will be initialized
 * to <code>null</code>, which means the affix strings will be taken as
 * literal values.  This is exactly what we want, since that corresponds to
 * the pre-version-2 behavior.
 */
private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
stream.defaultReadObject();
digitList=new DigitList();
if (serialVersionOnStream < 4) {
setRoundingMode(RoundingMode.HALF_EVEN);
}
if (super.getMaximumIntegerDigits() > DOUBLE_INTEGER_DIGITS || super.getMaximumFractionDigits() > DOUBLE_FRACTION_DIGITS) {
throw new InvalidObjectException("Digit count out of range");
}
if (serialVersionOnStream < 3) {
setMaximumIntegerDigits(super.getMaximumIntegerDigits());
setMinimumIntegerDigits(super.getMinimumIntegerDigits());
setMaximumFractionDigits(super.getMaximumFractionDigits());
setMinimumFractionDigits(super.getMinimumFractionDigits());
}
if (serialVersionOnStream < 1) {
useExponentialNotation=false;
}
serialVersionOnStream=currentSerialVersion;
}
private transient DigitList digitList=new DigitList();
/** 
 * The symbol used as a prefix when formatting positive numbers, e.g. "+".
 * @serial
 * @see #getPositivePrefix
 */
private String positivePrefix="";
/** 
 * The symbol used as a suffix when formatting positive numbers.
 * This is often an empty string.
 * @serial
 * @see #getPositiveSuffix
 */
private String positiveSuffix="";
/** 
 * The symbol used as a prefix when formatting negative numbers, e.g. "-".
 * @serial
 * @see #getNegativePrefix
 */
private String negativePrefix="-";
/** 
 * The symbol used as a suffix when formatting negative numbers.
 * This is often an empty string.
 * @serial
 * @see #getNegativeSuffix
 */
private String negativeSuffix="";
/** 
 * The prefix pattern for non-negative numbers.  This variable corresponds
 * to <code>positivePrefix</code>.
 * <p>This pattern is expanded by the method <code>expandAffix()</code> to
 * <code>positivePrefix</code> to update the latter to reflect changes in
 * <code>symbols</code>.  If this variable is <code>null</code> then
 * <code>positivePrefix</code> is taken as a literal value that does not
 * change when <code>symbols</code> changes.  This variable is always
 * <code>null</code> for <code>DecimalFormat</code> objects older than
 * stream version 2 restored from stream.
 * @serial
 * @since 1.3
 */
private String posPrefixPattern;
/** 
 * The suffix pattern for non-negative numbers.  This variable corresponds
 * to <code>positiveSuffix</code>.  This variable is analogous to
 * <code>posPrefixPattern</code>; see that variable for further
 * documentation.
 * @serial
 * @since 1.3
 */
private String posSuffixPattern;
/** 
 * The prefix pattern for negative numbers.  This variable corresponds
 * to <code>negativePrefix</code>.  This variable is analogous to
 * <code>posPrefixPattern</code>; see that variable for further
 * documentation.
 * @serial
 * @since 1.3
 */
private String negPrefixPattern;
/** 
 * The suffix pattern for negative numbers.  This variable corresponds
 * to <code>negativeSuffix</code>.  This variable is analogous to
 * <code>posPrefixPattern</code>; see that variable for further
 * documentation.
 * @serial
 * @since 1.3
 */
private String negSuffixPattern;
/** 
 * The multiplier for use in percent, per mille, etc.
 * @serial
 * @see #getMultiplier
 */
private int multiplier=1;
/** 
 * The number of digits between grouping separators in the integer
 * portion of a number.  Must be greater than 0 if
 * <code>NumberFormat.groupingUsed</code> is true.
 * @serial
 * @see #getGroupingSize
 * @see java.text.NumberFormat#isGroupingUsed
 */
private byte groupingSize=3;
/** 
 * If true, forces the decimal separator to always appear in a formatted
 * number, even if the fractional part of the number is zero.
 * @serial
 * @see #isDecimalSeparatorAlwaysShown
 */
private boolean decimalSeparatorAlwaysShown=false;
/** 
 * If true, parse returns BigDecimal wherever possible.
 * @serial
 * @see #isParseBigDecimal
 * @since 1.5
 */
private boolean parseBigDecimal=false;
/** 
 * True if this object represents a currency format.  This determines
 * whether the monetary decimal separator is used instead of the normal one.
 */
private transient boolean isCurrencyFormat=false;
/** 
 * The <code>DecimalFormatSymbols</code> object used by this format.
 * It contains the symbols used to format numbers, e.g. the grouping separator,
 * decimal separator, and so on.
 * @serial
 * @see #setDecimalFormatSymbols
 * @see java.text.DecimalFormatSymbols
 */
private DecimalFormatSymbols symbols=null;
/** 
 * True to force the use of exponential (i.e. scientific) notation when formatting
 * numbers.
 * @serial
 * @since 1.2
 */
private boolean useExponentialNotation;
/** 
 * FieldPositions describing the positive prefix String. This is
 * lazily created. Use <code>getPositivePrefixFieldPositions</code>
 * when needed.
 */
private transient FieldPosition[] positivePrefixFieldPositions;
/** 
 * FieldPositions describing the positive suffix String. This is
 * lazily created. Use <code>getPositiveSuffixFieldPositions</code>
 * when needed.
 */
private transient FieldPosition[] positiveSuffixFieldPositions;
/** 
 * FieldPositions describing the negative prefix String. This is
 * lazily created. Use <code>getNegativePrefixFieldPositions</code>
 * when needed.
 */
private transient FieldPosition[] negativePrefixFieldPositions;
/** 
 * FieldPositions describing the negative suffix String. This is
 * lazily created. Use <code>getNegativeSuffixFieldPositions</code>
 * when needed.
 */
private transient FieldPosition[] negativeSuffixFieldPositions;
/** 
 * The minimum number of digits used to display the exponent when a number is
 * formatted in exponential notation.  This field is ignored if
 * <code>useExponentialNotation</code> is not true.
 * @serial
 * @since 1.2
 */
private byte minExponentDigits;
/** 
 * The maximum number of digits allowed in the integer portion of a
 * <code>BigInteger</code> or <code>BigDecimal</code> number.
 * <code>maximumIntegerDigits</code> must be greater than or equal to
 * <code>minimumIntegerDigits</code>.
 * @serial
 * @see #getMaximumIntegerDigits
 * @since 1.5
 */
private int maximumIntegerDigits=super.getMaximumIntegerDigits();
/** 
 * The minimum number of digits allowed in the integer portion of a
 * <code>BigInteger</code> or <code>BigDecimal</code> number.
 * <code>minimumIntegerDigits</code> must be less than or equal to
 * <code>maximumIntegerDigits</code>.
 * @serial
 * @see #getMinimumIntegerDigits
 * @since 1.5
 */
private int minimumIntegerDigits=super.getMinimumIntegerDigits();
/** 
 * The maximum number of digits allowed in the fractional portion of a
 * <code>BigInteger</code> or <code>BigDecimal</code> number.
 * <code>maximumFractionDigits</code> must be greater than or equal to
 * <code>minimumFractionDigits</code>.
 * @serial
 * @see #getMaximumFractionDigits
 * @since 1.5
 */
private int maximumFractionDigits=super.getMaximumFractionDigits();
/** 
 * The minimum number of digits allowed in the fractional portion of a
 * <code>BigInteger</code> or <code>BigDecimal</code> number.
 * <code>minimumFractionDigits</code> must be less than or equal to
 * <code>maximumFractionDigits</code>.
 * @serial
 * @see #getMinimumFractionDigits
 * @since 1.5
 */
private int minimumFractionDigits=super.getMinimumFractionDigits();
/** 
 * The {@link java.math.RoundingMode} used in this DecimalFormat.
 * @serial
 * @since 1.6
 */
private RoundingMode roundingMode=RoundingMode.HALF_EVEN;
static final int currentSerialVersion=4;
/** 
 * The internal serial version which says which version was written.
 * Possible values are:
 * <ul>
 * <li><b>0</b> (default): versions before the Java 2 platform v1.2
 * <li><b>1</b>: version for 1.2, which includes the two new fields
 * <code>useExponentialNotation</code> and
 * <code>minExponentDigits</code>.
 * <li><b>2</b>: version for 1.3 and later, which adds four new fields:
 * <code>posPrefixPattern</code>, <code>posSuffixPattern</code>,
 * <code>negPrefixPattern</code>, and <code>negSuffixPattern</code>.
 * <li><b>3</b>: version for 1.5 and later, which adds five new fields:
 * <code>maximumIntegerDigits</code>,
 * <code>minimumIntegerDigits</code>,
 * <code>maximumFractionDigits</code>,
 * <code>minimumFractionDigits</code>, and
 * <code>parseBigDecimal</code>.
 * <li><b>4</b>: version for 1.6 and later, which adds one new field:
 * <code>roundingMode</code>.
 * </ul>
 * @since 1.2
 * @serial
 */
private int serialVersionOnStream=currentSerialVersion;
private static final char PATTERN_ZERO_DIGIT='0';
private static final char PATTERN_GROUPING_SEPARATOR=',';
private static final char PATTERN_DECIMAL_SEPARATOR='.';
private static final char PATTERN_PER_MILLE='\u2030';
private static final char PATTERN_PERCENT='%';
private static final char PATTERN_DIGIT='#';
private static final char PATTERN_SEPARATOR=';';
private static final String PATTERN_EXPONENT="E";
private static final char PATTERN_MINUS='-';
/** 
 * The CURRENCY_SIGN is the standard Unicode symbol for currency.  It
 * is used in patterns and substituted with either the currency symbol,
 * or if it is doubled, with the international currency symbol.  If the
 * CURRENCY_SIGN is seen in a pattern, then the decimal separator is
 * replaced with the monetary decimal separator.
 * The CURRENCY_SIGN is not localized.
 */
private static final char CURRENCY_SIGN='\u00A4';
private static final char QUOTE='\'';
private static FieldPosition[] EmptyFieldPositionArray=new FieldPosition[0];
static final int DOUBLE_INTEGER_DIGITS=309;
static final int DOUBLE_FRACTION_DIGITS=340;
static final int MAXIMUM_INTEGER_DIGITS=Integer.MAX_VALUE;
static final int MAXIMUM_FRACTION_DIGITS=Integer.MAX_VALUE;
static final long serialVersionUID=864413376551465018L;
/** 
 * Cache to hold the NumberPattern of a Locale.
 */
private static final ConcurrentMap<Locale,String> cachedLocaleData=new ConcurrentHashMap<Locale,String>(3);
}
