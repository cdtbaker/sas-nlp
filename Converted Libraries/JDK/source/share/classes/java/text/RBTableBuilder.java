package java.text;
import java.util.Vector;
import sun.text.UCompactIntArray;
import sun.text.IntHashtable;
import sun.text.ComposedCharIter;
import sun.text.CollatorUtilities;
import sun.text.normalizer.NormalizerImpl;
/** 
 * This class contains all the code to parse a RuleBasedCollator pattern
 * and build a RBCollationTables object from it.  A particular instance
 * of tis class exists only during the actual build process-- once an
 * RBCollationTables object has been built, the RBTableBuilder object
 * goes away.  This object carries all of the state which is only needed
 * during the build process, plus a "shadow" copy of all of the state
 * that will go into the tables object itself.  This object communicates
 * with RBCollationTables through a separate class, RBCollationTables.BuildAPI,
 * this is an inner class of RBCollationTables and provides a separate
 * private API for communication with RBTableBuilder.
 * This class isn't just an inner class of RBCollationTables itself because
 * of its large size.  For source-code readability, it seemed better for the
 * builder to have its own source file.
 */
final class RBTableBuilder {
  public RBTableBuilder(  RBCollationTables.BuildAPI tables){
    this.tables=tables;
  }
  /** 
 * Create a table-based collation object with the given rules.
 * This is the main function that actually builds the tables and
 * stores them back in the RBCollationTables object.  It is called
 * ONLY by the RBCollationTables constructor.
 * @see java.util.RuleBasedCollator#RuleBasedCollator
 * @exception ParseException If the rules format is incorrect.
 */
  public void build(  String pattern,  int decmp) throws ParseException {
    boolean isSource=true;
    int i=0;
    String expChars;
    String groupChars;
    if (pattern.length() == 0)     throw new ParseException("Build rules empty.",0);
    mapping=new UCompactIntArray((int)RBCollationTables.UNMAPPED);
    pattern=NormalizerImpl.canonicalDecomposeWithSingleQuotation(pattern);
    mPattern=new MergeCollation(pattern);
    int order=0;
    for (i=0; i < mPattern.getCount(); ++i) {
      PatternEntry entry=mPattern.getItemAt(i);
      if (entry != null) {
        groupChars=entry.getChars();
        if (groupChars.length() > 1) {
switch (groupChars.charAt(groupChars.length() - 1)) {
case '@':
            frenchSec=true;
          groupChars=groupChars.substring(0,groupChars.length() - 1);
        break;
case '!':
      seAsianSwapping=true;
    groupChars=groupChars.substring(0,groupChars.length() - 1);
  break;
}
}
order=increment(entry.getStrength(),order);
expChars=entry.getExtension();
if (expChars.length() != 0) {
addExpandOrder(groupChars,expChars,order);
}
 else if (groupChars.length() > 1) {
char ch=groupChars.charAt(0);
if (Character.isHighSurrogate(ch) && groupChars.length() == 2) {
addOrder(Character.toCodePoint(ch,groupChars.charAt(1)),order);
}
 else {
addContractOrder(groupChars,order);
}
}
 else {
char ch=groupChars.charAt(0);
addOrder(ch,order);
}
}
}
addComposedChars();
commit();
mapping.compact();
tables.fillInTables(frenchSec,seAsianSwapping,mapping,contractTable,expandTable,contractFlags,maxSecOrder,maxTerOrder);
}
/** 
 * Add expanding entries for pre-composed unicode characters so that this
 * collator can be used reasonably well with decomposition turned off.
 */
private void addComposedChars() throws ParseException {
ComposedCharIter iter=new ComposedCharIter();
int c;
while ((c=iter.next()) != ComposedCharIter.DONE) {
if (getCharOrder(c) == RBCollationTables.UNMAPPED) {
String s=iter.decomposition();
if (s.length() == 1) {
int order=getCharOrder(s.charAt(0));
if (order != RBCollationTables.UNMAPPED) {
addOrder(c,order);
}
continue;
}
 else if (s.length() == 2) {
char ch0=s.charAt(0);
if (Character.isHighSurrogate(ch0)) {
int order=getCharOrder(s.codePointAt(0));
if (order != RBCollationTables.UNMAPPED) {
  addOrder(c,order);
}
continue;
}
}
int contractOrder=getContractOrder(s);
if (contractOrder != RBCollationTables.UNMAPPED) {
addOrder(c,contractOrder);
}
 else {
boolean allThere=true;
for (int i=0; i < s.length(); i++) {
if (getCharOrder(s.charAt(i)) == RBCollationTables.UNMAPPED) {
  allThere=false;
  break;
}
}
if (allThere) {
addExpandOrder(c,s,RBCollationTables.UNMAPPED);
}
}
}
}
}
/** 
 * Look up for unmapped values in the expanded character table.
 * When the expanding character tables are built by addExpandOrder,
 * it doesn't know what the final ordering of each character
 * in the expansion will be.  Instead, it just puts the raw character
 * code into the table, adding CHARINDEX as a flag.  Now that we've
 * finished building the mapping table, we can go back and look up
 * that character to see what its real collation order is and
 * stick that into the expansion table.  That lets us avoid doing
 * a two-stage lookup later.
 */
private final void commit(){
if (expandTable != null) {
for (int i=0; i < expandTable.size(); i++) {
int[] valueList=(int[])expandTable.elementAt(i);
for (int j=0; j < valueList.length; j++) {
int order=valueList[j];
if (order < RBCollationTables.EXPANDCHARINDEX && order > CHARINDEX) {
int ch=order - CHARINDEX;
int realValue=getCharOrder(ch);
if (realValue == RBCollationTables.UNMAPPED) {
  valueList[j]=IGNORABLEMASK & ch;
}
 else {
  valueList[j]=realValue;
}
}
}
}
}
}
/** 
 * Increment of the last order based on the comparison level.
 */
private final int increment(int aStrength,int lastValue){
switch (aStrength) {
case Collator.PRIMARY:
lastValue+=PRIMARYORDERINCREMENT;
lastValue&=RBCollationTables.PRIMARYORDERMASK;
isOverIgnore=true;
break;
case Collator.SECONDARY:
lastValue+=SECONDARYORDERINCREMENT;
lastValue&=RBCollationTables.SECONDARYDIFFERENCEONLY;
if (!isOverIgnore) maxSecOrder++;
break;
case Collator.TERTIARY:
lastValue+=TERTIARYORDERINCREMENT;
if (!isOverIgnore) maxTerOrder++;
break;
}
return lastValue;
}
/** 
 * Adds a character and its designated order into the collation table.
 */
private final void addOrder(int ch,int anOrder){
int order=mapping.elementAt(ch);
if (order >= RBCollationTables.CONTRACTCHARINDEX) {
int length=1;
if (Character.isSupplementaryCodePoint(ch)) {
length=Character.toChars(ch,keyBuf,0);
}
 else {
keyBuf[0]=(char)ch;
}
addContractOrder(new String(keyBuf,0,length),anOrder);
}
 else {
mapping.setElementAt(ch,anOrder);
}
}
private final void addContractOrder(String groupChars,int anOrder){
addContractOrder(groupChars,anOrder,true);
}
/** 
 * Adds the contracting string into the collation table.
 */
private final void addContractOrder(String groupChars,int anOrder,boolean fwd){
if (contractTable == null) {
contractTable=new Vector(INITIALTABLESIZE);
}
int ch=groupChars.codePointAt(0);
int entry=mapping.elementAt(ch);
Vector entryTable=getContractValuesImpl(entry - RBCollationTables.CONTRACTCHARINDEX);
if (entryTable == null) {
int tableIndex=RBCollationTables.CONTRACTCHARINDEX + contractTable.size();
entryTable=new Vector(INITIALTABLESIZE);
contractTable.addElement(entryTable);
entryTable.addElement(new EntryPair(groupChars.substring(0,Character.charCount(ch)),entry));
mapping.setElementAt(ch,tableIndex);
}
int index=RBCollationTables.getEntry(entryTable,groupChars,fwd);
if (index != RBCollationTables.UNMAPPED) {
EntryPair pair=(EntryPair)entryTable.elementAt(index);
pair.value=anOrder;
}
 else {
EntryPair pair=(EntryPair)entryTable.lastElement();
if (groupChars.length() > pair.entryName.length()) {
entryTable.addElement(new EntryPair(groupChars,anOrder,fwd));
}
 else {
entryTable.insertElementAt(new EntryPair(groupChars,anOrder,fwd),entryTable.size() - 1);
}
}
if (fwd && groupChars.length() > 1) {
addContractFlags(groupChars);
addContractOrder(new StringBuffer(groupChars).reverse().toString(),anOrder,false);
}
}
/** 
 * If the given string has been specified as a contracting string
 * in this collation table, return its ordering.
 * Otherwise return UNMAPPED.
 */
private int getContractOrder(String groupChars){
int result=RBCollationTables.UNMAPPED;
if (contractTable != null) {
int ch=groupChars.codePointAt(0);
Vector entryTable=getContractValues(ch);
if (entryTable != null) {
int index=RBCollationTables.getEntry(entryTable,groupChars,true);
if (index != RBCollationTables.UNMAPPED) {
EntryPair pair=(EntryPair)entryTable.elementAt(index);
result=pair.value;
}
}
}
return result;
}
private final int getCharOrder(int ch){
int order=mapping.elementAt(ch);
if (order >= RBCollationTables.CONTRACTCHARINDEX) {
Vector groupList=getContractValuesImpl(order - RBCollationTables.CONTRACTCHARINDEX);
EntryPair pair=(EntryPair)groupList.firstElement();
order=pair.value;
}
return order;
}
/** 
 * Get the entry of hash table of the contracting string in the collation
 * table.
 * @param ch the starting character of the contracting string
 */
private Vector getContractValues(int ch){
int index=mapping.elementAt(ch);
return getContractValuesImpl(index - RBCollationTables.CONTRACTCHARINDEX);
}
private Vector getContractValuesImpl(int index){
if (index >= 0) {
return (Vector)contractTable.elementAt(index);
}
 else {
return null;
}
}
/** 
 * Adds the expanding string into the collation table.
 */
private final void addExpandOrder(String contractChars,String expandChars,int anOrder) throws ParseException {
int tableIndex=addExpansion(anOrder,expandChars);
if (contractChars.length() > 1) {
char ch=contractChars.charAt(0);
if (Character.isHighSurrogate(ch) && contractChars.length() == 2) {
char ch2=contractChars.charAt(1);
if (Character.isLowSurrogate(ch2)) {
addOrder(Character.toCodePoint(ch,ch2),tableIndex);
}
}
 else {
addContractOrder(contractChars,tableIndex);
}
}
 else {
addOrder(contractChars.charAt(0),tableIndex);
}
}
private final void addExpandOrder(int ch,String expandChars,int anOrder) throws ParseException {
int tableIndex=addExpansion(anOrder,expandChars);
addOrder(ch,tableIndex);
}
/** 
 * Create a new entry in the expansion table that contains the orderings
 * for the given characers.  If anOrder is valid, it is added to the
 * beginning of the expanded list of orders.
 */
private int addExpansion(int anOrder,String expandChars){
if (expandTable == null) {
expandTable=new Vector(INITIALTABLESIZE);
}
int offset=(anOrder == RBCollationTables.UNMAPPED) ? 0 : 1;
int[] valueList=new int[expandChars.length() + offset];
if (offset == 1) {
valueList[0]=anOrder;
}
int j=offset;
for (int i=0; i < expandChars.length(); i++) {
char ch0=expandChars.charAt(i);
char ch1;
int ch;
if (Character.isHighSurrogate(ch0)) {
if (++i == expandChars.length() || !Character.isLowSurrogate(ch1=expandChars.charAt(i))) {
break;
}
ch=Character.toCodePoint(ch0,ch1);
}
 else {
ch=ch0;
}
int mapValue=getCharOrder(ch);
if (mapValue != RBCollationTables.UNMAPPED) {
valueList[j++]=mapValue;
}
 else {
valueList[j++]=CHARINDEX + ch;
}
}
if (j < valueList.length) {
int[] tmpBuf=new int[j];
while (--j >= 0) {
tmpBuf[j]=valueList[j];
}
valueList=tmpBuf;
}
int tableIndex=RBCollationTables.EXPANDCHARINDEX + expandTable.size();
expandTable.addElement(valueList);
return tableIndex;
}
private void addContractFlags(String chars){
char c0;
int c;
int len=chars.length();
for (int i=0; i < len; i++) {
c0=chars.charAt(i);
c=Character.isHighSurrogate(c0) ? Character.toCodePoint(c0,chars.charAt(++i)) : c0;
contractFlags.put(c,1);
}
}
final static int CHARINDEX=0x70000000;
private final static int IGNORABLEMASK=0x0000ffff;
private final static int PRIMARYORDERINCREMENT=0x00010000;
private final static int SECONDARYORDERINCREMENT=0x00000100;
private final static int TERTIARYORDERINCREMENT=0x00000001;
private final static int INITIALTABLESIZE=20;
private final static int MAXKEYSIZE=5;
private RBCollationTables.BuildAPI tables=null;
private MergeCollation mPattern=null;
private boolean isOverIgnore=false;
private char[] keyBuf=new char[MAXKEYSIZE];
private IntHashtable contractFlags=new IntHashtable(100);
private boolean frenchSec=false;
private boolean seAsianSwapping=false;
private UCompactIntArray mapping=null;
private Vector contractTable=null;
private Vector expandTable=null;
private short maxSecOrder=0;
private short maxTerOrder=0;
}
