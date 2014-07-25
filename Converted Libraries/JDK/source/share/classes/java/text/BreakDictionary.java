package java.text;
import java.io.*;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.MissingResourceException;
import sun.text.CompactByteArray;
import sun.text.SupplementaryCharacterData;
/** 
 * This is the class that represents the list of known words used by
 * DictionaryBasedBreakIterator.  The conceptual data structure used
 * here is a trie: there is a node hanging off the root node for every
 * letter that can start a word.  Each of these nodes has a node hanging
 * off of it for every letter that can be the second letter of a word
 * if this node is the first letter, and so on.  The trie is represented
 * as a two-dimensional array that can be treated as a table of state
 * transitions.  Indexes are used to compress this array, taking
 * advantage of the fact that this array will always be very sparse.
 */
class BreakDictionary {
  /** 
 * The version of the dictionary that was read in.
 */
  private static int supportedVersion=1;
  /** 
 * Maps from characters to column numbers.  The main use of this is to
 * avoid making room in the array for empty columns.
 */
  private CompactByteArray columnMap=null;
  private SupplementaryCharacterData supplementaryCharColumnMap=null;
  /** 
 * The number of actual columns in the table
 */
  private int numCols;
  /** 
 * Columns are organized into groups of 32.  This says how many
 * column groups.  (We could calculate this, but we store the
 * value to avoid having to repeatedly calculate it.)
 */
  private int numColGroups;
  /** 
 * The actual compressed state table.  Each conceptual row represents
 * a state, and the cells in it contain the row numbers of the states
 * to transition to for each possible letter.  0 is used to indicate
 * an illegal combination of letters (i.e., the error state).  The
 * table is compressed by eliminating all the unpopulated (i.e., zero)
 * cells.  Multiple conceptual rows can then be doubled up in a single
 * physical row by sliding them up and possibly shifting them to one
 * side or the other so the populated cells don't collide.  Indexes
 * are used to identify unpopulated cells and to locate populated cells.
 */
  private short[] table=null;
  /** 
 * This index maps logical row numbers to physical row numbers
 */
  private short[] rowIndex=null;
  /** 
 * A bitmap is used to tell which cells in the comceptual table are
 * populated.  This array contains all the unique bit combinations
 * in that bitmap.  If the table is more than 32 columns wide,
 * successive entries in this array are used for a single row.
 */
  private int[] rowIndexFlags=null;
  /** 
 * This index maps from a logical row number into the bitmap table above.
 * (This keeps us from storing duplicate bitmap combinations.)  Since there
 * are a lot of rows with only one populated cell, instead of wasting space
 * in the bitmap table, we just store a negative number in this index for
 * rows with one populated cell.  The absolute value of that number is
 * the column number of the populated cell.
 */
  private short[] rowIndexFlagsIndex=null;
  /** 
 * For each logical row, this index contains a constant that is added to
 * the logical column number to get the physical column number
 */
  private byte[] rowIndexShifts=null;
  public BreakDictionary(  String dictionaryName) throws IOException, MissingResourceException {
    readDictionaryFile(dictionaryName);
  }
  private void readDictionaryFile(  final String dictionaryName) throws IOException, MissingResourceException {
    BufferedInputStream in;
    try {
      in=(BufferedInputStream)AccessController.doPrivileged(new PrivilegedExceptionAction(){
        public Object run() throws Exception {
          return new BufferedInputStream(getClass().getResourceAsStream("/sun/text/resources/" + dictionaryName));
        }
      }
);
    }
 catch (    PrivilegedActionException e) {
      throw new InternalError(e.toString());
    }
    byte[] buf=new byte[8];
    if (in.read(buf) != 8) {
      throw new MissingResourceException("Wrong data length",dictionaryName,"");
    }
    int version=BreakIterator.getInt(buf,0);
    if (version != supportedVersion) {
      throw new MissingResourceException("Dictionary version(" + version + ") is unsupported",dictionaryName,"");
    }
    int len=BreakIterator.getInt(buf,4);
    buf=new byte[len];
    if (in.read(buf) != len) {
      throw new MissingResourceException("Wrong data length",dictionaryName,"");
    }
    in.close();
    int l;
    int offset=0;
    l=BreakIterator.getInt(buf,offset);
    offset+=4;
    short[] temp=new short[l];
    for (int i=0; i < l; i++, offset+=2) {
      temp[i]=BreakIterator.getShort(buf,offset);
    }
    l=BreakIterator.getInt(buf,offset);
    offset+=4;
    byte[] temp2=new byte[l];
    for (int i=0; i < l; i++, offset++) {
      temp2[i]=buf[offset];
    }
    columnMap=new CompactByteArray(temp,temp2);
    numCols=BreakIterator.getInt(buf,offset);
    offset+=4;
    numColGroups=BreakIterator.getInt(buf,offset);
    offset+=4;
    l=BreakIterator.getInt(buf,offset);
    offset+=4;
    rowIndex=new short[l];
    for (int i=0; i < l; i++, offset+=2) {
      rowIndex[i]=BreakIterator.getShort(buf,offset);
    }
    l=BreakIterator.getInt(buf,offset);
    offset+=4;
    rowIndexFlagsIndex=new short[l];
    for (int i=0; i < l; i++, offset+=2) {
      rowIndexFlagsIndex[i]=BreakIterator.getShort(buf,offset);
    }
    l=BreakIterator.getInt(buf,offset);
    offset+=4;
    rowIndexFlags=new int[l];
    for (int i=0; i < l; i++, offset+=4) {
      rowIndexFlags[i]=BreakIterator.getInt(buf,offset);
    }
    l=BreakIterator.getInt(buf,offset);
    offset+=4;
    rowIndexShifts=new byte[l];
    for (int i=0; i < l; i++, offset++) {
      rowIndexShifts[i]=buf[offset];
    }
    l=BreakIterator.getInt(buf,offset);
    offset+=4;
    table=new short[l];
    for (int i=0; i < l; i++, offset+=2) {
      table[i]=BreakIterator.getShort(buf,offset);
    }
    l=BreakIterator.getInt(buf,offset);
    offset+=4;
    int[] temp3=new int[l];
    for (int i=0; i < l; i++, offset+=4) {
      temp3[i]=BreakIterator.getInt(buf,offset);
    }
    supplementaryCharColumnMap=new SupplementaryCharacterData(temp3);
  }
  /** 
 * Uses the column map to map the character to a column number, then
 * passes the row and column number to getNextState()
 * @param row The current state
 * @param ch The character whose column we're interested in
 * @return The new state to transition to
 */
  public final short getNextStateFromCharacter(  int row,  int ch){
    int col;
    if (ch < Character.MIN_SUPPLEMENTARY_CODE_POINT) {
      col=columnMap.elementAt((char)ch);
    }
 else {
      col=supplementaryCharColumnMap.getValue(ch);
    }
    return getNextState(row,col);
  }
  /** 
 * Returns the value in the cell with the specified (logical) row and
 * column numbers.  In DictionaryBasedBreakIterator, the row number is
 * a state number, the column number is an input, and the return value
 * is the row number of the new state to transition to.  (0 is the
 * "error" state, and -1 is the "end of word" state in a dictionary)
 * @param row The row number of the current state
 * @param col The column number of the input character (0 means "not a
 * dictionary character")
 * @return The row number of the new state to transition to
 */
  public final short getNextState(  int row,  int col){
    if (cellIsPopulated(row,col)) {
      return internalAt(rowIndex[row],col + rowIndexShifts[row]);
    }
 else {
      return 0;
    }
  }
  /** 
 * Given (logical) row and column numbers, returns true if the
 * cell in that position is populated
 */
  private final boolean cellIsPopulated(  int row,  int col){
    if (rowIndexFlagsIndex[row] < 0) {
      return col == -rowIndexFlagsIndex[row];
    }
 else {
      int flags=rowIndexFlags[rowIndexFlagsIndex[row] + (col >> 5)];
      return (flags & (1 << (col & 0x1f))) != 0;
    }
  }
  /** 
 * Implementation of getNextState() when we know the specified cell is
 * populated.
 * @param row The PHYSICAL row number of the cell
 * @param col The PHYSICAL column number of the cell
 * @return The value stored in the cell
 */
  private final short internalAt(  int row,  int col){
    return table[row * numCols + col];
  }
}
