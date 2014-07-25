package java.text;
import java.util.Vector;
import java.util.Stack;
import java.util.Hashtable;
import java.text.CharacterIterator;
import java.io.InputStream;
import java.io.IOException;
/** 
 * A subclass of RuleBasedBreakIterator that adds the ability to use a dictionary
 * to further subdivide ranges of text beyond what is possible using just the
 * state-table-based algorithm.  This is necessary, for example, to handle
 * word and line breaking in Thai, which doesn't use spaces between words.  The
 * state-table-based algorithm used by RuleBasedBreakIterator is used to divide
 * up text as far as possible, and then contiguous ranges of letters are
 * repeatedly compared against a list of known words (i.e., the dictionary)
 * to divide them up into words.
 * DictionaryBasedBreakIterator uses the same rule language as RuleBasedBreakIterator,
 * but adds one more special substitution name: &lt;dictionary&gt;.  This substitution
 * name is used to identify characters in words in the dictionary.  The idea is that
 * if the iterator passes over a chunk of text that includes two or more characters
 * in a row that are included in &lt;dictionary&gt;, it goes back through that range and
 * derives additional break positions (if possible) using the dictionary.
 * DictionaryBasedBreakIterator is also constructed with the filename of a dictionary
 * file.  It follows a prescribed search path to locate the dictionary (right now,
 * it looks for it in /com/ibm/text/resources in each directory in the classpath,
 * and won't find it in JAR files, but this location is likely to change).  The
 * dictionary file is in a serialized binary format.  We have a very primitive (and
 * slow) BuildDictionaryFile utility for creating dictionary files, but aren't
 * currently making it public.  Contact us for help.
 */
class DictionaryBasedBreakIterator extends RuleBasedBreakIterator {
  /** 
 * a list of known words that is used to divide up contiguous ranges of letters,
 * stored in a compressed, indexed, format that offers fast access
 */
  private BreakDictionary dictionary;
  /** 
 * a list of flags indicating which character categories are contained in
 * the dictionary file (this is used to determine which ranges of characters
 * to apply the dictionary to)
 */
  private boolean[] categoryFlags;
  /** 
 * a temporary hiding place for the number of dictionary characters in the
 * last range passed over by next()
 */
  private int dictionaryCharCount;
  /** 
 * when a range of characters is divided up using the dictionary, the break
 * positions that are discovered are stored here, preventing us from having
 * to use either the dictionary or the state table again until the iterator
 * leaves this range of text
 */
  private int[] cachedBreakPositions;
  /** 
 * if cachedBreakPositions is not null, this indicates which item in the
 * cache the current iteration position refers to
 */
  private int positionInCache;
  /** 
 * Constructs a DictionaryBasedBreakIterator.
 * @param description Same as the description parameter on RuleBasedBreakIterator,
 * except for the special meaning of "<dictionary>".  This parameter is just
 * passed through to RuleBasedBreakIterator's constructor.
 * @param dictionaryFilename The filename of the dictionary file to use
 */
  public DictionaryBasedBreakIterator(  String dataFile,  String dictionaryFile) throws IOException {
    super(dataFile);
    byte[] tmp=super.getAdditionalData();
    if (tmp != null) {
      prepareCategoryFlags(tmp);
      super.setAdditionalData(null);
    }
    dictionary=new BreakDictionary(dictionaryFile);
  }
  private void prepareCategoryFlags(  byte[] data){
    categoryFlags=new boolean[data.length];
    for (int i=0; i < data.length; i++) {
      categoryFlags[i]=(data[i] == (byte)1) ? true : false;
    }
  }
  public void setText(  CharacterIterator newText){
    super.setText(newText);
    cachedBreakPositions=null;
    dictionaryCharCount=0;
    positionInCache=0;
  }
  /** 
 * Sets the current iteration position to the beginning of the text.
 * (i.e., the CharacterIterator's starting offset).
 * @return The offset of the beginning of the text.
 */
  public int first(){
    cachedBreakPositions=null;
    dictionaryCharCount=0;
    positionInCache=0;
    return super.first();
  }
  /** 
 * Sets the current iteration position to the end of the text.
 * (i.e., the CharacterIterator's ending offset).
 * @return The text's past-the-end offset.
 */
  public int last(){
    cachedBreakPositions=null;
    dictionaryCharCount=0;
    positionInCache=0;
    return super.last();
  }
  /** 
 * Advances the iterator one step backwards.
 * @return The position of the last boundary position before the
 * current iteration position
 */
  public int previous(){
    CharacterIterator text=getText();
    if (cachedBreakPositions != null && positionInCache > 0) {
      --positionInCache;
      text.setIndex(cachedBreakPositions[positionInCache]);
      return cachedBreakPositions[positionInCache];
    }
 else {
      cachedBreakPositions=null;
      int result=super.previous();
      if (cachedBreakPositions != null) {
        positionInCache=cachedBreakPositions.length - 2;
      }
      return result;
    }
  }
  /** 
 * Sets the current iteration position to the last boundary position
 * before the specified position.
 * @param offset The position to begin searching from
 * @return The position of the last boundary before "offset"
 */
  public int preceding(  int offset){
    CharacterIterator text=getText();
    checkOffset(offset,text);
    if (cachedBreakPositions == null || offset <= cachedBreakPositions[0] || offset > cachedBreakPositions[cachedBreakPositions.length - 1]) {
      cachedBreakPositions=null;
      return super.preceding(offset);
    }
 else {
      positionInCache=0;
      while (positionInCache < cachedBreakPositions.length && offset > cachedBreakPositions[positionInCache]) {
        ++positionInCache;
      }
      --positionInCache;
      text.setIndex(cachedBreakPositions[positionInCache]);
      return text.getIndex();
    }
  }
  /** 
 * Sets the current iteration position to the first boundary position after
 * the specified position.
 * @param offset The position to begin searching forward from
 * @return The position of the first boundary after "offset"
 */
  public int following(  int offset){
    CharacterIterator text=getText();
    checkOffset(offset,text);
    if (cachedBreakPositions == null || offset < cachedBreakPositions[0] || offset >= cachedBreakPositions[cachedBreakPositions.length - 1]) {
      cachedBreakPositions=null;
      return super.following(offset);
    }
 else {
      positionInCache=0;
      while (positionInCache < cachedBreakPositions.length && offset >= cachedBreakPositions[positionInCache]) {
        ++positionInCache;
      }
      text.setIndex(cachedBreakPositions[positionInCache]);
      return text.getIndex();
    }
  }
  /** 
 * This is the implementation function for next().
 */
  protected int handleNext(){
    CharacterIterator text=getText();
    if (cachedBreakPositions == null || positionInCache == cachedBreakPositions.length - 1) {
      int startPos=text.getIndex();
      dictionaryCharCount=0;
      int result=super.handleNext();
      if (dictionaryCharCount > 1 && result - startPos > 1) {
        divideUpDictionaryRange(startPos,result);
      }
 else {
        cachedBreakPositions=null;
        return result;
      }
    }
    if (cachedBreakPositions != null) {
      ++positionInCache;
      text.setIndex(cachedBreakPositions[positionInCache]);
      return cachedBreakPositions[positionInCache];
    }
    return -9999;
  }
  /** 
 * Looks up a character category for a character.
 */
  protected int lookupCategory(  int c){
    int result=super.lookupCategory(c);
    if (result != RuleBasedBreakIterator.IGNORE && categoryFlags[result]) {
      ++dictionaryCharCount;
    }
    return result;
  }
  /** 
 * This is the function that actually implements the dictionary-based
 * algorithm.  Given the endpoints of a range of text, it uses the
 * dictionary to determine the positions of any boundaries in this
 * range.  It stores all the boundary positions it discovers in
 * cachedBreakPositions so that we only have to do this work once
 * for each time we enter the range.
 */
  private void divideUpDictionaryRange(  int startPos,  int endPos){
    CharacterIterator text=getText();
    text.setIndex(startPos);
    int c=getCurrent();
    int category=lookupCategory(c);
    while (category == IGNORE || !categoryFlags[category]) {
      c=getNext();
      category=lookupCategory(c);
    }
    Stack currentBreakPositions=new Stack();
    Stack possibleBreakPositions=new Stack();
    Vector wrongBreakPositions=new Vector();
    int state=0;
    int farthestEndPoint=text.getIndex();
    Stack bestBreakPositions=null;
    c=getCurrent();
    while (true) {
      if (dictionary.getNextState(state,0) == -1) {
        possibleBreakPositions.push(Integer.valueOf(text.getIndex()));
      }
      state=dictionary.getNextStateFromCharacter(state,c);
      if (state == -1) {
        currentBreakPositions.push(Integer.valueOf(text.getIndex()));
        break;
      }
 else       if (state == 0 || text.getIndex() >= endPos) {
        if (text.getIndex() > farthestEndPoint) {
          farthestEndPoint=text.getIndex();
          bestBreakPositions=(Stack)(currentBreakPositions.clone());
        }
        Integer newStartingSpot=null;
        while (!possibleBreakPositions.isEmpty() && wrongBreakPositions.contains(possibleBreakPositions.peek())) {
          possibleBreakPositions.pop();
        }
        if (possibleBreakPositions.isEmpty()) {
          if (bestBreakPositions != null) {
            currentBreakPositions=bestBreakPositions;
            if (farthestEndPoint < endPos) {
              text.setIndex(farthestEndPoint + 1);
            }
 else {
              break;
            }
          }
 else {
            if ((currentBreakPositions.size() == 0 || ((Integer)(currentBreakPositions.peek())).intValue() != text.getIndex()) && text.getIndex() != startPos) {
              currentBreakPositions.push(new Integer(text.getIndex()));
            }
            getNext();
            currentBreakPositions.push(new Integer(text.getIndex()));
          }
        }
 else {
          Integer temp=(Integer)possibleBreakPositions.pop();
          Object temp2=null;
          while (!currentBreakPositions.isEmpty() && temp.intValue() < ((Integer)currentBreakPositions.peek()).intValue()) {
            temp2=currentBreakPositions.pop();
            wrongBreakPositions.addElement(temp2);
          }
          currentBreakPositions.push(temp);
          text.setIndex(((Integer)currentBreakPositions.peek()).intValue());
        }
        c=getCurrent();
        if (text.getIndex() >= endPos) {
          break;
        }
      }
 else {
        c=getNext();
      }
    }
    if (!currentBreakPositions.isEmpty()) {
      currentBreakPositions.pop();
    }
    currentBreakPositions.push(Integer.valueOf(endPos));
    cachedBreakPositions=new int[currentBreakPositions.size() + 1];
    cachedBreakPositions[0]=startPos;
    for (int i=0; i < currentBreakPositions.size(); i++) {
      cachedBreakPositions[i + 1]=((Integer)currentBreakPositions.elementAt(i)).intValue();
    }
    positionInCache=0;
  }
}
