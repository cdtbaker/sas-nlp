package sun.text.bidi;
/** 
 * A BidiRun represents a sequence of characters at the same embedding level.
 * The Bidi algorithm decomposes a piece of text into sequences of characters
 * at the same embedding level, each such sequence is called a <quote>run</quote>.
 * <p>A BidiRun represents such a run by storing its essential properties,
 * but does not duplicate the characters which form the run.
 * <p>The &quot;limit&quot; of the run is the position just after the
 * last character, i.e., one more than that position.
 * <p>This class has no public constructor, and its members cannot be
 * modified by users.
 * @see com.ibm.icu.text.Bidi
 */
public class BidiRun {
  int start;
  int limit;
  int insertRemove;
  byte level;
  BidiRun(){
    this(0,0,(byte)0);
  }
  BidiRun(  int start,  int limit,  byte embeddingLevel){
    this.start=start;
    this.limit=limit;
    this.level=embeddingLevel;
  }
  void copyFrom(  BidiRun run){
    this.start=run.start;
    this.limit=run.limit;
    this.level=run.level;
    this.insertRemove=run.insertRemove;
  }
  /** 
 * Get level of run
 */
  public byte getEmbeddingLevel(){
    return level;
  }
  /** 
 * Check if run level is even
 * @return true if the embedding level of this run is even, i.e. it is a
 * left-to-right run.
 */
  boolean isEvenRun(){
    return (level & 1) == 0;
  }
}
