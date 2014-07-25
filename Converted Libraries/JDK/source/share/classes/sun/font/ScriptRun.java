package sun.font;
/** 
 * <code>ScriptRun</code> is used to find runs of characters in
 * the same script, as defined in the <code>Script</code> class.
 * It implements a simple iterator over an array of characters.
 * The iterator will assign <code>COMMON</code> and <code>INHERITED</code>
 * characters to the same script as the preceeding characters. If the
 * COMMON and INHERITED characters are first, they will be assigned to
 * the same script as the following characters.
 * The iterator will try to match paired punctuation. If it sees an
 * opening punctuation character, it will remember the script that
 * was assigned to that character, and assign the same script to the
 * matching closing punctuation.
 * No attempt is made to combine related scripts into a single run. In
 * particular, Hiragana, Katakana, and Han characters will appear in seperate
 * runs.
 * Here is an example of how to iterate over script runs:
 * <pre>
 * void printScriptRuns(char[] text)
 * {
 * ScriptRun scriptRun = new ScriptRun(text, 0, text.length);
 * while (scriptRun.next()) {
 * int start  = scriptRun.getScriptStart();
 * int limit  = scriptRun.getScriptLimit();
 * int script = scriptRun.getScriptCode();
 * System.out.println("Script \"" + Script.getName(script) + "\" from " +
 * start + " to " + limit + ".");
 * }
 * }
 * </pre>
 */
public final class ScriptRun {
  private char[] text;
  private int textStart;
  private int textLimit;
  private int scriptStart;
  private int scriptLimit;
  private int scriptCode;
  private int stack[];
  private int parenSP;
  public ScriptRun(){
  }
  /** 
 * Construct a <code>ScriptRun</code> object which iterates over a subrange
 * of the given characetrs.
 * @param chars the array of characters over which to iterate.
 * @param start the index of the first character over which to iterate
 * @param count the number of characters over which to iterate
 */
  public ScriptRun(  char[] chars,  int start,  int count){
    init(chars,start,count);
  }
  public void init(  char[] chars,  int start,  int count){
    if (chars == null || start < 0 || count < 0 || count > chars.length - start) {
      throw new IllegalArgumentException();
    }
    text=chars;
    textStart=start;
    textLimit=start + count;
    scriptStart=textStart;
    scriptLimit=textStart;
    scriptCode=Script.INVALID_CODE;
    parenSP=0;
  }
  /** 
 * Get the starting index of the current script run.
 * @return the index of the first character in the current script run.
 */
  public final int getScriptStart(){
    return scriptStart;
  }
  /** 
 * Get the index of the first character after the current script run.
 * @return the index of the first character after the current script run.
 */
  public final int getScriptLimit(){
    return scriptLimit;
  }
  /** 
 * Get the script code for the script of the current script run.
 * @return the script code for the script of the current script run.
 * @see #Script
 */
  public final int getScriptCode(){
    return scriptCode;
  }
  /** 
 * Find the next script run. Returns <code>false</code> if there
 * isn't another run, returns <code>true</code> if there is.
 * @return <code>false</code> if there isn't another run, <code>true</code> if there is.
 */
  public final boolean next(){
    int startSP=parenSP;
    if (scriptLimit >= textLimit) {
      return false;
    }
    scriptCode=Script.COMMON;
    scriptStart=scriptLimit;
    int ch;
    while ((ch=nextCodePoint()) != DONE) {
      int sc=ScriptRunData.getScript(ch);
      int pairIndex=sc == Script.COMMON ? getPairIndex(ch) : -1;
      if (pairIndex >= 0) {
        if ((pairIndex & 1) == 0) {
          if (stack == null) {
            stack=new int[32];
          }
 else           if (parenSP == stack.length) {
            int[] newstack=new int[stack.length + 32];
            System.arraycopy(stack,0,newstack,0,stack.length);
            stack=newstack;
          }
          stack[parenSP++]=pairIndex;
          stack[parenSP++]=scriptCode;
        }
 else         if (parenSP > 0) {
          int pi=pairIndex & ~1;
          while ((parenSP-=2) >= 0 && stack[parenSP] != pi)           ;
          if (parenSP >= 0) {
            sc=stack[parenSP + 1];
          }
 else {
            parenSP=0;
          }
          if (parenSP < startSP) {
            startSP=parenSP;
          }
        }
      }
      if (sameScript(scriptCode,sc)) {
        if (scriptCode <= Script.INHERITED && sc > Script.INHERITED) {
          scriptCode=sc;
          while (startSP < parenSP) {
            stack[startSP + 1]=scriptCode;
            startSP+=2;
          }
        }
        if (pairIndex > 0 && (pairIndex & 1) != 0 && parenSP > 0) {
          parenSP-=2;
        }
      }
 else {
        pushback(ch);
        break;
      }
    }
    return true;
  }
  static final int SURROGATE_START=0x10000;
  static final int LEAD_START=0xd800;
  static final int LEAD_LIMIT=0xdc00;
  static final int TAIL_START=0xdc00;
  static final int TAIL_LIMIT=0xe000;
  static final int LEAD_SURROGATE_SHIFT=10;
  static final int SURROGATE_OFFSET=SURROGATE_START - (LEAD_START << LEAD_SURROGATE_SHIFT) - TAIL_START;
  static final int DONE=-1;
  private final int nextCodePoint(){
    if (scriptLimit >= textLimit) {
      return DONE;
    }
    int ch=text[scriptLimit++];
    if (ch >= LEAD_START && ch < LEAD_LIMIT && scriptLimit < textLimit) {
      int nch=text[scriptLimit];
      if (nch >= TAIL_START && nch < TAIL_LIMIT) {
        ++scriptLimit;
        ch=(ch << LEAD_SURROGATE_SHIFT) + nch + SURROGATE_OFFSET;
      }
    }
    return ch;
  }
  private final void pushback(  int ch){
    if (ch >= 0) {
      if (ch >= 0x10000) {
        scriptLimit-=2;
      }
 else {
        scriptLimit-=1;
      }
    }
  }
  /** 
 * Compare two script codes to see if they are in the same script. If one script is
 * a strong script, and the other is INHERITED or COMMON, it will compare equal.
 * @param scriptOne one of the script codes.
 * @param scriptTwo the other script code.
 * @return <code>true</code> if the two scripts are the same.
 * @see com.ibm.icu.lang.Script
 */
  private static boolean sameScript(  int scriptOne,  int scriptTwo){
    return scriptOne == scriptTwo || scriptOne <= Script.INHERITED || scriptTwo <= Script.INHERITED;
  }
  /** 
 * Find the highest bit that's set in a word. Uses a binary search through
 * the bits.
 * @param n the word in which to find the highest bit that's set.
 * @return the bit number (counting from the low order bit) of the highest bit.
 */
  private static final byte highBit(  int n){
    if (n <= 0) {
      return -32;
    }
    byte bit=0;
    if (n >= 1 << 16) {
      n>>=16;
      bit+=16;
    }
    if (n >= 1 << 8) {
      n>>=8;
      bit+=8;
    }
    if (n >= 1 << 4) {
      n>>=4;
      bit+=4;
    }
    if (n >= 1 << 2) {
      n>>=2;
      bit+=2;
    }
    if (n >= 1 << 1) {
      n>>=1;
      bit+=1;
    }
    return bit;
  }
  /** 
 * Search the pairedChars array for the given character.
 * @param ch the character for which to search.
 * @return the index of the character in the table, or -1 if it's not there.
 */
  private static int getPairIndex(  int ch){
    int probe=pairedCharPower;
    int index=0;
    if (ch >= pairedChars[pairedCharExtra]) {
      index=pairedCharExtra;
    }
    while (probe > (1 << 0)) {
      probe>>=1;
      if (ch >= pairedChars[index + probe]) {
        index+=probe;
      }
    }
    if (pairedChars[index] != ch) {
      index=-1;
    }
    return index;
  }
  private static int pairedChars[]={0x0028,0x0029,0x003c,0x003e,0x005b,0x005d,0x007b,0x007d,0x00ab,0x00bb,0x2018,0x2019,0x201c,0x201d,0x2039,0x203a,0x3008,0x3009,0x300a,0x300b,0x300c,0x300d,0x300e,0x300f,0x3010,0x3011,0x3014,0x3015,0x3016,0x3017,0x3018,0x3019,0x301a,0x301b};
  private static final int pairedCharPower=1 << highBit(pairedChars.length);
  private static final int pairedCharExtra=pairedChars.length - pairedCharPower;
}
