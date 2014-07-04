package sun.net.idn;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import sun.text.Normalizer;
import sun.text.normalizer.CharTrie;
import sun.text.normalizer.Trie;
import sun.text.normalizer.NormalizerImpl;
import sun.text.normalizer.VersionInfo;
import sun.text.normalizer.UCharacter;
import sun.text.normalizer.UCharacterIterator;
import sun.text.normalizer.UTF16;
import sun.net.idn.UCharacterDirection;
import sun.net.idn.StringPrepDataReader;
/** 
 * StringPrep API implements the StingPrep framework as described by
 * <a href="http://www.ietf.org/rfc/rfc3454.txt">RFC 3454</a>.
 * StringPrep prepares Unicode strings for use in network protocols.
 * Profiles of StingPrep are set of rules and data according to which the
 * Unicode Strings are prepared. Each profiles contains tables which describe
 * how a code point should be treated. The tables are broadly classied into
 * <ul>
 * <li> Unassigned Table: Contains code points that are unassigned
 * in the Unicode Version supported by StringPrep. Currently
 * RFC 3454 supports Unicode 3.2. </li>
 * <li> Prohibited Table: Contains code points that are prohibted from
 * the output of the StringPrep processing function. </li>
 * <li> Mapping Table: Contains code ponts that are deleted from the output or case mapped. </li>
 * </ul>
 * The procedure for preparing Unicode strings:
 * <ol>
 * <li> Map: For each character in the input, check if it has a mapping
 * and, if so, replace it with its mapping. </li>
 * <li> Normalize: Possibly normalize the result of step 1 using Unicode
 * normalization. </li>
 * <li> Prohibit: Check for any characters that are not allowed in the
 * output.  If any are found, return an error.</li>
 * <li> Check bidi: Possibly check for right-to-left characters, and if
 * any are found, make sure that the whole string satisfies the
 * requirements for bidirectional strings.  If the string does not
 * satisfy the requirements for bidirectional strings, return an
 * error.  </li>
 * </ol>
 * @author Ram Viswanadha
 * @draft ICU 2.8
 */
public final class StringPrep {
  /** 
 * Option to prohibit processing of unassigned code points in the input
 * @see #prepare
 * @draft ICU 2.8
 */
  public static final int DEFAULT=0x0000;
  /** 
 * Option to allow processing of unassigned code points in the input
 * @see #prepare
 * @draft ICU 2.8
 */
  public static final int ALLOW_UNASSIGNED=0x0001;
  private static final int UNASSIGNED=0x0000;
  private static final int MAP=0x0001;
  private static final int PROHIBITED=0x0002;
  private static final int DELETE=0x0003;
  private static final int TYPE_LIMIT=0x0004;
  private static final int NORMALIZATION_ON=0x0001;
  private static final int CHECK_BIDI_ON=0x0002;
  private static final int TYPE_THRESHOLD=0xFFF0;
  private static final int MAX_INDEX_VALUE=0x3FBF;
  private static final int MAX_INDEX_TOP_LENGTH=0x0003;
  private static final int INDEX_TRIE_SIZE=0;
  private static final int INDEX_MAPPING_DATA_SIZE=1;
  private static final int NORM_CORRECTNS_LAST_UNI_VERSION=2;
  private static final int ONE_UCHAR_MAPPING_INDEX_START=3;
  private static final int TWO_UCHARS_MAPPING_INDEX_START=4;
  private static final int THREE_UCHARS_MAPPING_INDEX_START=5;
  private static final int FOUR_UCHARS_MAPPING_INDEX_START=6;
  private static final int OPTIONS=7;
  private static final int INDEX_TOP=16;
  /** 
 * Default buffer size of datafile
 */
  private static final int DATA_BUFFER_SIZE=25000;
private static final class StringPrepTrieImpl implements Trie.DataManipulate {
    private CharTrie sprepTrie=null;
    /** 
 * Called by com.ibm.icu.util.Trie to extract from a lead surrogate's
 * data the index array offset of the indexes for that lead surrogate.
 * @param property data value for a surrogate from the trie, including
 * the folding offset
 * @return data offset or 0 if there is no data for the lead surrogate
 */
    public int getFoldingOffset(    int value){
      return value;
    }
  }
  private StringPrepTrieImpl sprepTrieImpl;
  private int[] indexes;
  private char[] mappingData;
  private byte[] formatVersion;
  private VersionInfo sprepUniVer;
  private VersionInfo normCorrVer;
  private boolean doNFKC;
  private boolean checkBiDi;
  private char getCodePointValue(  int ch){
    return sprepTrieImpl.sprepTrie.getCodePointValue(ch);
  }
  private static VersionInfo getVersionInfo(  int comp){
    int micro=comp & 0xFF;
    int milli=(comp >> 8) & 0xFF;
    int minor=(comp >> 16) & 0xFF;
    int major=(comp >> 24) & 0xFF;
    return VersionInfo.getInstance(major,minor,milli,micro);
  }
  private static VersionInfo getVersionInfo(  byte[] version){
    if (version.length != 4) {
      return null;
    }
    return VersionInfo.getInstance((int)version[0],(int)version[1],(int)version[2],(int)version[3]);
  }
  /** 
 * Creates an StringPrep object after reading the input stream.
 * The object does not hold a reference to the input steam, so the stream can be
 * closed after the method returns.
 * @param inputStream The stream for reading the StringPrep profile binarySun
 * @throws IOException
 * @draft ICU 2.8
 */
  public StringPrep(  InputStream inputStream) throws IOException {
    BufferedInputStream b=new BufferedInputStream(inputStream,DATA_BUFFER_SIZE);
    StringPrepDataReader reader=new StringPrepDataReader(b);
    indexes=reader.readIndexes(INDEX_TOP);
    byte[] sprepBytes=new byte[indexes[INDEX_TRIE_SIZE]];
    mappingData=new char[indexes[INDEX_MAPPING_DATA_SIZE] / 2];
    reader.read(sprepBytes,mappingData);
    sprepTrieImpl=new StringPrepTrieImpl();
    sprepTrieImpl.sprepTrie=new CharTrie(new ByteArrayInputStream(sprepBytes),sprepTrieImpl);
    formatVersion=reader.getDataFormatVersion();
    doNFKC=((indexes[OPTIONS] & NORMALIZATION_ON) > 0);
    checkBiDi=((indexes[OPTIONS] & CHECK_BIDI_ON) > 0);
    sprepUniVer=getVersionInfo(reader.getUnicodeVersion());
    normCorrVer=getVersionInfo(indexes[NORM_CORRECTNS_LAST_UNI_VERSION]);
    VersionInfo normUniVer=NormalizerImpl.getUnicodeVersion();
    if (normUniVer.compareTo(sprepUniVer) < 0 && normUniVer.compareTo(normCorrVer) < 0 && ((indexes[OPTIONS] & NORMALIZATION_ON) > 0)) {
      throw new IOException("Normalization Correction version not supported");
    }
    b.close();
  }
private static final class Values {
    boolean isIndex;
    int value;
    int type;
    public void reset(){
      isIndex=false;
      value=0;
      type=-1;
    }
  }
  private static final void getValues(  char trieWord,  Values values){
    values.reset();
    if (trieWord == 0) {
      values.type=TYPE_LIMIT;
    }
 else     if (trieWord >= TYPE_THRESHOLD) {
      values.type=(trieWord - TYPE_THRESHOLD);
    }
 else {
      values.type=MAP;
      if ((trieWord & 0x02) > 0) {
        values.isIndex=true;
        values.value=trieWord >> 2;
      }
 else {
        values.isIndex=false;
        values.value=(trieWord << 16) >> 16;
        values.value=(values.value >> 2);
      }
      if ((trieWord >> 2) == MAX_INDEX_VALUE) {
        values.type=DELETE;
        values.isIndex=false;
        values.value=0;
      }
    }
  }
  private StringBuffer map(  UCharacterIterator iter,  int options) throws ParseException {
    Values val=new Values();
    char result=0;
    int ch=UCharacterIterator.DONE;
    StringBuffer dest=new StringBuffer();
    boolean allowUnassigned=((options & ALLOW_UNASSIGNED) > 0);
    while ((ch=iter.nextCodePoint()) != UCharacterIterator.DONE) {
      result=getCodePointValue(ch);
      getValues(result,val);
      if (val.type == UNASSIGNED && allowUnassigned == false) {
        throw new ParseException("An unassigned code point was found in the input " + iter.getText(),iter.getIndex());
      }
 else       if ((val.type == MAP)) {
        int index, length;
        if (val.isIndex) {
          index=val.value;
          if (index >= indexes[ONE_UCHAR_MAPPING_INDEX_START] && index < indexes[TWO_UCHARS_MAPPING_INDEX_START]) {
            length=1;
          }
 else           if (index >= indexes[TWO_UCHARS_MAPPING_INDEX_START] && index < indexes[THREE_UCHARS_MAPPING_INDEX_START]) {
            length=2;
          }
 else           if (index >= indexes[THREE_UCHARS_MAPPING_INDEX_START] && index < indexes[FOUR_UCHARS_MAPPING_INDEX_START]) {
            length=3;
          }
 else {
            length=mappingData[index++];
          }
          dest.append(mappingData,index,length);
          continue;
        }
 else {
          ch-=val.value;
        }
      }
 else       if (val.type == DELETE) {
        continue;
      }
      UTF16.append(dest,ch);
    }
    return dest;
  }
  private StringBuffer normalize(  StringBuffer src){
    return new StringBuffer(Normalizer.normalize(src.toString(),java.text.Normalizer.Form.NFKC,Normalizer.UNICODE_3_2 | NormalizerImpl.BEFORE_PRI_29));
  }
  /** 
 * Prepare the input buffer for use in applications with the given profile. This operation maps, normalizes(NFKC),
 * checks for prohited and BiDi characters in the order defined by RFC 3454
 * depending on the options specified in the profile.
 * @param src           A UCharacterIterator object containing the source string
 * @param options       A bit set of options:
 * - StringPrep.NONE               Prohibit processing of unassigned code points in the input
 * - StringPrep.ALLOW_UNASSIGNED   Treat the unassigned code points are in the input
 * as normal Unicode code points.
 * @return StringBuffer A StringBuffer containing the output
 * @throws ParseException
 * @draft ICU 2.8
 */
  public StringBuffer prepare(  UCharacterIterator src,  int options) throws ParseException {
    StringBuffer mapOut=map(src,options);
    StringBuffer normOut=mapOut;
    if (doNFKC) {
      normOut=normalize(mapOut);
    }
    int ch;
    char result;
    UCharacterIterator iter=UCharacterIterator.getInstance(normOut);
    Values val=new Values();
    int direction=UCharacterDirection.CHAR_DIRECTION_COUNT, firstCharDir=UCharacterDirection.CHAR_DIRECTION_COUNT;
    int rtlPos=-1, ltrPos=-1;
    boolean rightToLeft=false, leftToRight=false;
    while ((ch=iter.nextCodePoint()) != UCharacterIterator.DONE) {
      result=getCodePointValue(ch);
      getValues(result,val);
      if (val.type == PROHIBITED) {
        throw new ParseException("A prohibited code point was found in the input" + iter.getText(),val.value);
      }
      direction=UCharacter.getDirection(ch);
      if (firstCharDir == UCharacterDirection.CHAR_DIRECTION_COUNT) {
        firstCharDir=direction;
      }
      if (direction == UCharacterDirection.LEFT_TO_RIGHT) {
        leftToRight=true;
        ltrPos=iter.getIndex() - 1;
      }
      if (direction == UCharacterDirection.RIGHT_TO_LEFT || direction == UCharacterDirection.RIGHT_TO_LEFT_ARABIC) {
        rightToLeft=true;
        rtlPos=iter.getIndex() - 1;
      }
    }
    if (checkBiDi == true) {
      if (leftToRight == true && rightToLeft == true) {
        throw new ParseException("The input does not conform to the rules for BiDi code points." + iter.getText(),(rtlPos > ltrPos) ? rtlPos : ltrPos);
      }
      if (rightToLeft == true && !((firstCharDir == UCharacterDirection.RIGHT_TO_LEFT || firstCharDir == UCharacterDirection.RIGHT_TO_LEFT_ARABIC) && (direction == UCharacterDirection.RIGHT_TO_LEFT || direction == UCharacterDirection.RIGHT_TO_LEFT_ARABIC))) {
        throw new ParseException("The input does not conform to the rules for BiDi code points." + iter.getText(),(rtlPos > ltrPos) ? rtlPos : ltrPos);
      }
    }
    return normOut;
  }
}
