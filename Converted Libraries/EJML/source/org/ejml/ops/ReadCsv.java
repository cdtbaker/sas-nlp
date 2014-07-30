package org.ejml.ops;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
/** 
 * <p>
 * Base class for reading CSV formatted files.  CSV stands for column-space-value where text strings are separated
 * by a space character.  The values are typically stored in a human readable format.  The encoded text for a single
 * variable is referred to as a word.
 * </p>
 * <p>
 * Comments are allowed and identified by starting a line with the comment character.  The comment character is user
 * configurable.  By default there is no comment character.
 * </p>
 * @author Peter Abeles
 */
public class ReadCsv {
  private boolean hasComment=false;
  private char comment;
  private BufferedReader in;
  private int lineNumber=0;
  /** 
 * Constructor for ReadCsv
 * @param in Where the input comes from.
 */
  public ReadCsv(  InputStream in){
    this.in=new BufferedReader(new InputStreamReader(in));
  }
  /** 
 * Sets the comment character.  All lines that start with this character will be ignored.
 * @param comment The new comment character.
 */
  public void setComment(  char comment){
    hasComment=true;
    this.comment=comment;
  }
  /** 
 * Returns how many lines have been read.
 * @return Line number
 */
  public int getLineNumber(){
    return lineNumber;
  }
  /** 
 * Returns the reader that it is using internally.
 * @return The reader.
 */
  public BufferedReader getReader(){
    return in;
  }
  /** 
 * Finds the next valid line of words in the stream and extracts them.
 * @return List of valid words on the line.  null if the end of the file has been reached.
 * @throws java.io.IOException
 */
  protected List<String> extractWords() throws IOException {
    while (true) {
      lineNumber++;
      String line=in.readLine();
      if (line == null) {
        return null;
      }
      if (hasComment) {
        if (line.charAt(0) == comment)         continue;
      }
      return parseWords(line);
    }
  }
  /** 
 * Extracts the words from a string.  Words are seperated by a space character.
 * @param line The line that is being parsed.
 * @return A list of words contained on the line.
 */
  protected List<String> parseWords(  String line){
    List<String> words=new ArrayList<String>();
    boolean insideWord=!isSpace(line.charAt(0));
    int last=0;
    for (int i=0; i < line.length(); i++) {
      char c=line.charAt(i);
      if (insideWord) {
        if (isSpace(c)) {
          words.add(line.substring(last,i));
          insideWord=false;
        }
      }
 else {
        if (!isSpace(c)) {
          last=i;
          insideWord=true;
        }
      }
    }
    if (insideWord) {
      words.add(line.substring(last));
    }
    return words;
  }
  /** 
 * Checks to see if 'c' is a space character or not.
 * @param c The character being tested.
 * @return if it is a space character or not.
 */
  private boolean isSpace(  char c){
    return c == ' ' || c == '\t';
  }
}