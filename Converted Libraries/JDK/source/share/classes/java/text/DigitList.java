package java.text;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
/** 
 * Digit List. Private to DecimalFormat.
 * Handles the transcoding
 * between numeric values and strings of characters.  Only handles
 * non-negative numbers.  The division of labor between DigitList and
 * DecimalFormat is that DigitList handles the radix 10 representation
 * issues; DecimalFormat handles the locale-specific issues such as
 * positive/negative, grouping, decimal point, currency, and so on.
 * A DigitList is really a representation of a floating point value.
 * It may be an integer value; we assume that a double has sufficient
 * precision to represent all digits of a long.
 * The DigitList representation consists of a string of characters,
 * which are the digits radix 10, from '0' to '9'.  It also has a radix
 * 10 exponent associated with it.  The value represented by a DigitList
 * object can be computed by mulitplying the fraction f, where 0 <= f < 1,
 * derived by placing all the digits of the list to the right of the
 * decimal point, by 10^exponent.
 * @see Locale
 * @see Format
 * @see NumberFormat
 * @see DecimalFormat
 * @see ChoiceFormat
 * @see MessageFormat
 * @author       Mark Davis, Alan Liu
 */
final class DigitList implements Cloneable {
  /** 
 * The maximum number of significant digits in an IEEE 754 double, that
 * is, in a Java double.  This must not be increased, or garbage digits
 * will be generated, and should not be decreased, or accuracy will be lost.
 */
  public static final int MAX_COUNT=19;
  /** 
 * These data members are intentionally public and can be set directly.
 * The value represented is given by placing the decimal point before
 * digits[decimalAt].  If decimalAt is < 0, then leading zeros between
 * the decimal point and the first nonzero digit are implied.  If decimalAt
 * is > count, then trailing zeros between the digits[count-1] and the
 * decimal point are implied.
 * Equivalently, the represented value is given by f * 10^decimalAt.  Here
 * f is a value 0.1 <= f < 1 arrived at by placing the digits in Digits to
 * the right of the decimal.
 * DigitList is normalized, so if it is non-zero, figits[0] is non-zero.  We
 * don't allow denormalized numbers because our exponent is effectively of
 * unlimited magnitude.  The count value contains the number of significant
 * digits present in digits[].
 * Zero is represented by any DigitList with count == 0 or with each digits[i]
 * for all i <= count == '0'.
 */
  public int decimalAt=0;
  public int count=0;
  public char[] digits=new char[MAX_COUNT];
  private char[] data;
  private RoundingMode roundingMode=RoundingMode.HALF_EVEN;
  private boolean isNegative=false;
  /** 
 * Return true if the represented number is zero.
 */
  boolean isZero(){
    for (int i=0; i < count; ++i) {
      if (digits[i] != '0') {
        return false;
      }
    }
    return true;
  }
  /** 
 * Set the rounding mode
 */
  void setRoundingMode(  RoundingMode r){
    roundingMode=r;
  }
  /** 
 * Clears out the digits.
 * Use before appending them.
 * Typically, you set a series of digits with append, then at the point
 * you hit the decimal point, you set myDigitList.decimalAt = myDigitList.count;
 * then go on appending digits.
 */
  public void clear(){
    decimalAt=0;
    count=0;
  }
  /** 
 * Appends a digit to the list, extending the list when necessary.
 */
  public void append(  char digit){
    if (count == digits.length) {
      char[] data=new char[count + 100];
      System.arraycopy(digits,0,data,0,count);
      digits=data;
    }
    digits[count++]=digit;
  }
  /** 
 * Utility routine to get the value of the digit list
 * If (count == 0) this throws a NumberFormatException, which
 * mimics Long.parseLong().
 */
  public final double getDouble(){
    if (count == 0) {
      return 0.0;
    }
    StringBuffer temp=getStringBuffer();
    temp.append('.');
    temp.append(digits,0,count);
    temp.append('E');
    temp.append(decimalAt);
    return Double.parseDouble(temp.toString());
  }
  /** 
 * Utility routine to get the value of the digit list.
 * If (count == 0) this returns 0, unlike Long.parseLong().
 */
  public final long getLong(){
    if (count == 0) {
      return 0;
    }
    if (isLongMIN_VALUE()) {
      return Long.MIN_VALUE;
    }
    StringBuffer temp=getStringBuffer();
    temp.append(digits,0,count);
    for (int i=count; i < decimalAt; ++i) {
      temp.append('0');
    }
    return Long.parseLong(temp.toString());
  }
  public final BigDecimal getBigDecimal(){
    if (count == 0) {
      if (decimalAt == 0) {
        return BigDecimal.ZERO;
      }
 else {
        return new BigDecimal("0E" + decimalAt);
      }
    }
    if (decimalAt == count) {
      return new BigDecimal(digits,0,count);
    }
 else {
      return new BigDecimal(digits,0,count).scaleByPowerOfTen(decimalAt - count);
    }
  }
  /** 
 * Return true if the number represented by this object can fit into
 * a long.
 * @param isPositive true if this number should be regarded as positive
 * @param ignoreNegativeZero true if -0 should be regarded as identical to
 * +0; otherwise they are considered distinct
 * @return true if this number fits into a Java long
 */
  boolean fitsIntoLong(  boolean isPositive,  boolean ignoreNegativeZero){
    while (count > 0 && digits[count - 1] == '0') {
      --count;
    }
    if (count == 0) {
      return isPositive || ignoreNegativeZero;
    }
    if (decimalAt < count || decimalAt > MAX_COUNT) {
      return false;
    }
    if (decimalAt < MAX_COUNT)     return true;
    for (int i=0; i < count; ++i) {
      char dig=digits[i], max=LONG_MIN_REP[i];
      if (dig > max)       return false;
      if (dig < max)       return true;
    }
    if (count < decimalAt)     return true;
    return !isPositive;
  }
  /** 
 * Set the digit list to a representation of the given double value.
 * This method supports fixed-point notation.
 * @param isNegative Boolean value indicating whether the number is negative.
 * @param source Value to be converted; must not be Inf, -Inf, Nan,
 * or a value <= 0.
 * @param maximumFractionDigits The most fractional digits which should
 * be converted.
 */
  public final void set(  boolean isNegative,  double source,  int maximumFractionDigits){
    set(isNegative,source,maximumFractionDigits,true);
  }
  /** 
 * Set the digit list to a representation of the given double value.
 * This method supports both fixed-point and exponential notation.
 * @param isNegative Boolean value indicating whether the number is negative.
 * @param source Value to be converted; must not be Inf, -Inf, Nan,
 * or a value <= 0.
 * @param maximumDigits The most fractional or total digits which should
 * be converted.
 * @param fixedPoint If true, then maximumDigits is the maximum
 * fractional digits to be converted.  If false, total digits.
 */
  final void set(  boolean isNegative,  double source,  int maximumDigits,  boolean fixedPoint){
    set(isNegative,Double.toString(source),maximumDigits,fixedPoint);
  }
  /** 
 * Generate a representation of the form DDDDD, DDDDD.DDDDD, or
 * DDDDDE+/-DDDDD.
 */
  final void set(  boolean isNegative,  String s,  int maximumDigits,  boolean fixedPoint){
    this.isNegative=isNegative;
    int len=s.length();
    char[] source=getDataChars(len);
    s.getChars(0,len,source,0);
    decimalAt=-1;
    count=0;
    int exponent=0;
    int leadingZerosAfterDecimal=0;
    boolean nonZeroDigitSeen=false;
    for (int i=0; i < len; ) {
      char c=source[i++];
      if (c == '.') {
        decimalAt=count;
      }
 else       if (c == 'e' || c == 'E') {
        exponent=parseInt(source,i,len);
        break;
      }
 else {
        if (!nonZeroDigitSeen) {
          nonZeroDigitSeen=(c != '0');
          if (!nonZeroDigitSeen && decimalAt != -1)           ++leadingZerosAfterDecimal;
        }
        if (nonZeroDigitSeen) {
          digits[count++]=c;
        }
      }
    }
    if (decimalAt == -1) {
      decimalAt=count;
    }
    if (nonZeroDigitSeen) {
      decimalAt+=exponent - leadingZerosAfterDecimal;
    }
    if (fixedPoint) {
      if (-decimalAt > maximumDigits) {
        count=0;
        return;
      }
 else       if (-decimalAt == maximumDigits) {
        if (shouldRoundUp(0)) {
          count=1;
          ++decimalAt;
          digits[0]='1';
        }
 else {
          count=0;
        }
        return;
      }
    }
    while (count > 1 && digits[count - 1] == '0') {
      --count;
    }
    round(fixedPoint ? (maximumDigits + decimalAt) : maximumDigits);
  }
  /** 
 * Round the representation to the given number of digits.
 * @param maximumDigits The maximum number of digits to be shown.
 * Upon return, count will be less than or equal to maximumDigits.
 */
  private final void round(  int maximumDigits){
    if (maximumDigits >= 0 && maximumDigits < count) {
      if (shouldRoundUp(maximumDigits)) {
        for (; ; ) {
          --maximumDigits;
          if (maximumDigits < 0) {
            digits[0]='1';
            ++decimalAt;
            maximumDigits=0;
            break;
          }
          ++digits[maximumDigits];
          if (digits[maximumDigits] <= '9')           break;
        }
        ++maximumDigits;
      }
      count=maximumDigits;
      while (count > 1 && digits[count - 1] == '0') {
        --count;
      }
    }
  }
  /** 
 * Return true if truncating the representation to the given number
 * of digits will result in an increment to the last digit.  This
 * method implements the rounding modes defined in the
 * java.math.RoundingMode class.
 * [bnf]
 * @param maximumDigits the number of digits to keep, from 0 to
 * <code>count-1</code>.  If 0, then all digits are rounded away, and
 * this method returns true if a one should be generated (e.g., formatting
 * 0.09 with "#.#").
 * @exception ArithmeticException if rounding is needed with rounding
 * mode being set to RoundingMode.UNNECESSARY
 * @return true if digit <code>maximumDigits-1</code> should be
 * incremented
 */
  private boolean shouldRoundUp(  int maximumDigits){
    if (maximumDigits < count) {
switch (roundingMode) {
case UP:
        for (int i=maximumDigits; i < count; ++i) {
          if (digits[i] != '0') {
            return true;
          }
        }
      break;
case DOWN:
    break;
case CEILING:
  for (int i=maximumDigits; i < count; ++i) {
    if (digits[i] != '0') {
      return !isNegative;
    }
  }
break;
case FLOOR:
for (int i=maximumDigits; i < count; ++i) {
if (digits[i] != '0') {
  return isNegative;
}
}
break;
case HALF_UP:
if (digits[maximumDigits] >= '5') {
return true;
}
break;
case HALF_DOWN:
if (digits[maximumDigits] > '5') {
return true;
}
 else if (digits[maximumDigits] == '5') {
for (int i=maximumDigits + 1; i < count; ++i) {
if (digits[i] != '0') {
return true;
}
}
}
break;
case HALF_EVEN:
if (digits[maximumDigits] > '5') {
return true;
}
 else if (digits[maximumDigits] == '5') {
for (int i=maximumDigits + 1; i < count; ++i) {
if (digits[i] != '0') {
return true;
}
}
return maximumDigits > 0 && (digits[maximumDigits - 1] % 2 != 0);
}
break;
case UNNECESSARY:
for (int i=maximumDigits; i < count; ++i) {
if (digits[i] != '0') {
throw new ArithmeticException("Rounding needed with the rounding mode being set to RoundingMode.UNNECESSARY");
}
}
break;
default :
assert false;
}
}
return false;
}
/** 
 * Utility routine to set the value of the digit list from a long
 */
public final void set(boolean isNegative,long source){
set(isNegative,source,0);
}
/** 
 * Set the digit list to a representation of the given long value.
 * @param isNegative Boolean value indicating whether the number is negative.
 * @param source Value to be converted; must be >= 0 or ==
 * Long.MIN_VALUE.
 * @param maximumDigits The most digits which should be converted.
 * If maximumDigits is lower than the number of significant digits
 * in source, the representation will be rounded.  Ignored if <= 0.
 */
public final void set(boolean isNegative,long source,int maximumDigits){
this.isNegative=isNegative;
if (source <= 0) {
if (source == Long.MIN_VALUE) {
decimalAt=count=MAX_COUNT;
System.arraycopy(LONG_MIN_REP,0,digits,0,count);
}
 else {
decimalAt=count=0;
}
}
 else {
int left=MAX_COUNT;
int right;
while (source > 0) {
digits[--left]=(char)('0' + (source % 10));
source/=10;
}
decimalAt=MAX_COUNT - left;
for (right=MAX_COUNT - 1; digits[right] == '0'; --right) ;
count=right - left + 1;
System.arraycopy(digits,left,digits,0,count);
}
if (maximumDigits > 0) round(maximumDigits);
}
/** 
 * Set the digit list to a representation of the given BigDecimal value.
 * This method supports both fixed-point and exponential notation.
 * @param isNegative Boolean value indicating whether the number is negative.
 * @param source Value to be converted; must not be a value <= 0.
 * @param maximumDigits The most fractional or total digits which should
 * be converted.
 * @param fixedPoint If true, then maximumDigits is the maximum
 * fractional digits to be converted.  If false, total digits.
 */
final void set(boolean isNegative,BigDecimal source,int maximumDigits,boolean fixedPoint){
String s=source.toString();
extendDigits(s.length());
set(isNegative,s,maximumDigits,fixedPoint);
}
/** 
 * Set the digit list to a representation of the given BigInteger value.
 * @param isNegative Boolean value indicating whether the number is negative.
 * @param source Value to be converted; must be >= 0.
 * @param maximumDigits The most digits which should be converted.
 * If maximumDigits is lower than the number of significant digits
 * in source, the representation will be rounded.  Ignored if <= 0.
 */
final void set(boolean isNegative,BigInteger source,int maximumDigits){
this.isNegative=isNegative;
String s=source.toString();
int len=s.length();
extendDigits(len);
s.getChars(0,len,digits,0);
decimalAt=len;
int right;
for (right=len - 1; right >= 0 && digits[right] == '0'; --right) ;
count=right + 1;
if (maximumDigits > 0) {
round(maximumDigits);
}
}
/** 
 * equality test between two digit lists.
 */
public boolean equals(Object obj){
if (this == obj) return true;
if (!(obj instanceof DigitList)) return false;
DigitList other=(DigitList)obj;
if (count != other.count || decimalAt != other.decimalAt) return false;
for (int i=0; i < count; i++) if (digits[i] != other.digits[i]) return false;
return true;
}
/** 
 * Generates the hash code for the digit list.
 */
public int hashCode(){
int hashcode=decimalAt;
for (int i=0; i < count; i++) {
hashcode=hashcode * 37 + digits[i];
}
return hashcode;
}
/** 
 * Creates a copy of this object.
 * @return a clone of this instance.
 */
public Object clone(){
try {
DigitList other=(DigitList)super.clone();
char[] newDigits=new char[digits.length];
System.arraycopy(digits,0,newDigits,0,digits.length);
other.digits=newDigits;
other.tempBuffer=null;
return other;
}
 catch (CloneNotSupportedException e) {
throw new InternalError();
}
}
/** 
 * Returns true if this DigitList represents Long.MIN_VALUE;
 * false, otherwise.  This is required so that getLong() works.
 */
private boolean isLongMIN_VALUE(){
if (decimalAt != count || count != MAX_COUNT) {
return false;
}
for (int i=0; i < count; ++i) {
if (digits[i] != LONG_MIN_REP[i]) return false;
}
return true;
}
private static final int parseInt(char[] str,int offset,int strLen){
char c;
boolean positive=true;
if ((c=str[offset]) == '-') {
positive=false;
offset++;
}
 else if (c == '+') {
offset++;
}
int value=0;
while (offset < strLen) {
c=str[offset++];
if (c >= '0' && c <= '9') {
value=value * 10 + (c - '0');
}
 else {
break;
}
}
return positive ? value : -value;
}
private static final char[] LONG_MIN_REP="9223372036854775808".toCharArray();
public String toString(){
if (isZero()) {
return "0";
}
StringBuffer buf=getStringBuffer();
buf.append("0.");
buf.append(digits,0,count);
buf.append("x10^");
buf.append(decimalAt);
return buf.toString();
}
private StringBuffer tempBuffer;
private StringBuffer getStringBuffer(){
if (tempBuffer == null) {
tempBuffer=new StringBuffer(MAX_COUNT);
}
 else {
tempBuffer.setLength(0);
}
return tempBuffer;
}
private void extendDigits(int len){
if (len > digits.length) {
digits=new char[len];
}
}
private final char[] getDataChars(int length){
if (data == null || data.length < length) {
data=new char[length];
}
return data;
}
}
