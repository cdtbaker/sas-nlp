package javax.swing.text;
import java.awt.Color;
import java.awt.Font;
import java.awt.font.TextAttribute;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;
import java.util.ArrayList;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import javax.swing.event.*;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;
import javax.swing.SwingUtilities;
import static sun.swing.SwingUtilities2.IMPLIED_CR;
/** 
 * A document that can be marked up with character and paragraph
 * styles in a manner similar to the Rich Text Format.  The element
 * structure for this document represents style crossings for
 * style runs.  These style runs are mapped into a paragraph element
 * structure (which may reside in some other structure).  The
 * style runs break at paragraph boundaries since logical styles are
 * assigned to paragraph boundaries.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases. The current serialization support is
 * appropriate for short term storage or RMI between applications running
 * the same version of Swing.  As of 1.4, support for long term storage
 * of all JavaBeans<sup><font size="-2">TM</font></sup>
 * has been added to the <code>java.beans</code> package.
 * Please see {@link java.beans.XMLEncoder}.
 * @author  Timothy Prinzing
 * @see Document
 * @see AbstractDocument
 */
public class DefaultStyledDocument extends AbstractDocument implements StyledDocument {
  /** 
 * Constructs a styled document.
 * @param c  the container for the content
 * @param styles resources and style definitions which may
 * be shared across documents
 */
  public DefaultStyledDocument(  Content c,  StyleContext styles){
    super(c,styles);
    listeningStyles=new Vector<Style>();
    buffer=new ElementBuffer(createDefaultRoot());
    Style defaultStyle=styles.getStyle(StyleContext.DEFAULT_STYLE);
    setLogicalStyle(0,defaultStyle);
  }
  /** 
 * Constructs a styled document with the default content
 * storage implementation and a shared set of styles.
 * @param styles the styles
 */
  public DefaultStyledDocument(  StyleContext styles){
    this(new GapContent(BUFFER_SIZE_DEFAULT),styles);
  }
  /** 
 * Constructs a default styled document.  This buffers
 * input content by a size of <em>BUFFER_SIZE_DEFAULT</em>
 * and has a style context that is scoped by the lifetime
 * of the document and is not shared with other documents.
 */
  public DefaultStyledDocument(){
    this(new GapContent(BUFFER_SIZE_DEFAULT),new StyleContext());
  }
  /** 
 * Gets the default root element.
 * @return the root
 * @see Document#getDefaultRootElement
 */
  public Element getDefaultRootElement(){
    return buffer.getRootElement();
  }
  /** 
 * Initialize the document to reflect the given element
 * structure (i.e. the structure reported by the
 * <code>getDefaultRootElement</code> method.  If the
 * document contained any data it will first be removed.
 */
  protected void create(  ElementSpec[] data){
    try {
      if (getLength() != 0) {
        remove(0,getLength());
      }
      writeLock();
      Content c=getContent();
      int n=data.length;
      StringBuilder sb=new StringBuilder();
      for (int i=0; i < n; i++) {
        ElementSpec es=data[i];
        if (es.getLength() > 0) {
          sb.append(es.getArray(),es.getOffset(),es.getLength());
        }
      }
      UndoableEdit cEdit=c.insertString(0,sb.toString());
      int length=sb.length();
      DefaultDocumentEvent evnt=new DefaultDocumentEvent(0,length,DocumentEvent.EventType.INSERT);
      evnt.addEdit(cEdit);
      buffer.create(length,data,evnt);
      super.insertUpdate(evnt,null);
      evnt.end();
      fireInsertUpdate(evnt);
      fireUndoableEditUpdate(new UndoableEditEvent(this,evnt));
    }
 catch (    BadLocationException ble) {
      throw new StateInvariantError("problem initializing");
    }
 finally {
      writeUnlock();
    }
  }
  /** 
 * Inserts new elements in bulk.  This is useful to allow
 * parsing with the document in an unlocked state and
 * prepare an element structure modification.  This method
 * takes an array of tokens that describe how to update an
 * element structure so the time within a write lock can
 * be greatly reduced in an asynchronous update situation.
 * <p>
 * This method is thread safe, although most Swing methods
 * are not. Please see
 * <A HREF="http://java.sun.com/docs/books/tutorial/uiswing/misc/threads.html">How
 * to Use Threads</A> for more information.
 * @param offset the starting offset >= 0
 * @param data the element data
 * @exception BadLocationException for an invalid starting offset
 */
  protected void insert(  int offset,  ElementSpec[] data) throws BadLocationException {
    if (data == null || data.length == 0) {
      return;
    }
    try {
      writeLock();
      Content c=getContent();
      int n=data.length;
      StringBuilder sb=new StringBuilder();
      for (int i=0; i < n; i++) {
        ElementSpec es=data[i];
        if (es.getLength() > 0) {
          sb.append(es.getArray(),es.getOffset(),es.getLength());
        }
      }
      if (sb.length() == 0) {
        return;
      }
      UndoableEdit cEdit=c.insertString(offset,sb.toString());
      int length=sb.length();
      DefaultDocumentEvent evnt=new DefaultDocumentEvent(offset,length,DocumentEvent.EventType.INSERT);
      evnt.addEdit(cEdit);
      buffer.insert(offset,length,data,evnt);
      super.insertUpdate(evnt,null);
      evnt.end();
      fireInsertUpdate(evnt);
      fireUndoableEditUpdate(new UndoableEditEvent(this,evnt));
    }
  finally {
      writeUnlock();
    }
  }
  /** 
 * Removes an element from this document.
 * <p>The element is removed from its parent element, as well as
 * the text in the range identified by the element.  If the
 * element isn't associated with the document, {@codeIllegalArgumentException} is thrown.</p>
 * <p>As empty branch elements are not allowed in the document, if the
 * element is the sole child, its parent element is removed as well,
 * recursively.  This means that when replacing all the children of a
 * particular element, new children should be added <em>before</em>
 * removing old children.
 * <p>Element removal results in two events being fired, the{@code DocumentEvent} for changes in element structure and {@codeUndoableEditEvent} for changes in document content.</p>
 * <p>If the element contains end-of-content mark (the last {@code"\n"} character in document), this character is not removed;
 * instead, preceding leaf element is extended to cover the
 * character.  If the last leaf already ends with {@code "\n",} it is
 * included in content removal.</p>
 * <p>If the element is {@code null,} {@code NullPointerException} is
 * thrown.  If the element structure would become invalid after the removal,
 * for example if the element is the document root element, {@codeIllegalArgumentException} is thrown.  If the current element structure is
 * invalid, {@code IllegalStateException} is thrown.</p>
 * @param elem                      the element to remove
 * @throws NullPointerException      if the element is {@code null}
 * @throws IllegalArgumentException  if the element could not be removed
 * @throws IllegalStateException     if the element structure is invalid
 * @since  1.7
 */
  public void removeElement(  Element elem){
    try {
      writeLock();
      removeElementImpl(elem);
    }
  finally {
      writeUnlock();
    }
  }
  private void removeElementImpl(  Element elem){
    if (elem.getDocument() != this) {
      throw new IllegalArgumentException("element doesn't belong to document");
    }
    BranchElement parent=(BranchElement)elem.getParentElement();
    if (parent == null) {
      throw new IllegalArgumentException("can't remove the root element");
    }
    int startOffset=elem.getStartOffset();
    int removeFrom=startOffset;
    int endOffset=elem.getEndOffset();
    int removeTo=endOffset;
    int lastEndOffset=getLength() + 1;
    Content content=getContent();
    boolean atEnd=false;
    boolean isComposedText=Utilities.isComposedTextElement(elem);
    if (endOffset >= lastEndOffset) {
      if (startOffset <= 0) {
        throw new IllegalArgumentException("can't remove the whole content");
      }
      removeTo=lastEndOffset - 1;
      try {
        if (content.getString(startOffset - 1,1).charAt(0) == '\n') {
          removeFrom--;
        }
      }
 catch (      BadLocationException ble) {
        throw new IllegalStateException(ble);
      }
      atEnd=true;
    }
    int length=removeTo - removeFrom;
    DefaultDocumentEvent dde=new DefaultDocumentEvent(removeFrom,length,DefaultDocumentEvent.EventType.REMOVE);
    UndoableEdit ue=null;
    while (parent.getElementCount() == 1) {
      elem=parent;
      parent=(BranchElement)parent.getParentElement();
      if (parent == null) {
        throw new IllegalStateException("invalid element structure");
      }
    }
    Element[] removed={elem};
    Element[] added={};
    int index=parent.getElementIndex(startOffset);
    parent.replace(index,1,added);
    dde.addEdit(new ElementEdit(parent,index,removed,added));
    if (length > 0) {
      try {
        ue=content.remove(removeFrom,length);
        if (ue != null) {
          dde.addEdit(ue);
        }
      }
 catch (      BadLocationException ble) {
        throw new IllegalStateException(ble);
      }
      lastEndOffset-=length;
    }
    if (atEnd) {
      Element prevLeaf=parent.getElement(parent.getElementCount() - 1);
      while ((prevLeaf != null) && !prevLeaf.isLeaf()) {
        prevLeaf=prevLeaf.getElement(prevLeaf.getElementCount() - 1);
      }
      if (prevLeaf == null) {
        throw new IllegalStateException("invalid element structure");
      }
      int prevStartOffset=prevLeaf.getStartOffset();
      BranchElement prevParent=(BranchElement)prevLeaf.getParentElement();
      int prevIndex=prevParent.getElementIndex(prevStartOffset);
      Element newElem;
      newElem=createLeafElement(prevParent,prevLeaf.getAttributes(),prevStartOffset,lastEndOffset);
      Element[] prevRemoved={prevLeaf};
      Element[] prevAdded={newElem};
      prevParent.replace(prevIndex,1,prevAdded);
      dde.addEdit(new ElementEdit(prevParent,prevIndex,prevRemoved,prevAdded));
    }
    postRemoveUpdate(dde);
    dde.end();
    fireRemoveUpdate(dde);
    if (!(isComposedText && (ue != null))) {
      fireUndoableEditUpdate(new UndoableEditEvent(this,dde));
    }
  }
  /** 
 * Adds a new style into the logical style hierarchy.  Style attributes
 * resolve from bottom up so an attribute specified in a child
 * will override an attribute specified in the parent.
 * @param nm   the name of the style (must be unique within the
 * collection of named styles).  The name may be null if the style
 * is unnamed, but the caller is responsible
 * for managing the reference returned as an unnamed style can't
 * be fetched by name.  An unnamed style may be useful for things
 * like character attribute overrides such as found in a style
 * run.
 * @param parent the parent style.  This may be null if unspecified
 * attributes need not be resolved in some other style.
 * @return the style
 */
  public Style addStyle(  String nm,  Style parent){
    StyleContext styles=(StyleContext)getAttributeContext();
    return styles.addStyle(nm,parent);
  }
  /** 
 * Removes a named style previously added to the document.
 * @param nm  the name of the style to remove
 */
  public void removeStyle(  String nm){
    StyleContext styles=(StyleContext)getAttributeContext();
    styles.removeStyle(nm);
  }
  /** 
 * Fetches a named style previously added.
 * @param nm  the name of the style
 * @return the style
 */
  public Style getStyle(  String nm){
    StyleContext styles=(StyleContext)getAttributeContext();
    return styles.getStyle(nm);
  }
  /** 
 * Fetches the list of of style names.
 * @return all the style names
 */
  public Enumeration<?> getStyleNames(){
    return ((StyleContext)getAttributeContext()).getStyleNames();
  }
  /** 
 * Sets the logical style to use for the paragraph at the
 * given position.  If attributes aren't explicitly set
 * for character and paragraph attributes they will resolve
 * through the logical style assigned to the paragraph, which
 * in turn may resolve through some hierarchy completely
 * independent of the element hierarchy in the document.
 * <p>
 * This method is thread safe, although most Swing methods
 * are not. Please see
 * <A HREF="http://java.sun.com/docs/books/tutorial/uiswing/misc/threads.html">How
 * to Use Threads</A> for more information.
 * @param pos the offset from the start of the document >= 0
 * @param s  the logical style to assign to the paragraph, null if none
 */
  public void setLogicalStyle(  int pos,  Style s){
    Element paragraph=getParagraphElement(pos);
    if ((paragraph != null) && (paragraph instanceof AbstractElement)) {
      try {
        writeLock();
        StyleChangeUndoableEdit edit=new StyleChangeUndoableEdit((AbstractElement)paragraph,s);
        ((AbstractElement)paragraph).setResolveParent(s);
        int p0=paragraph.getStartOffset();
        int p1=paragraph.getEndOffset();
        DefaultDocumentEvent e=new DefaultDocumentEvent(p0,p1 - p0,DocumentEvent.EventType.CHANGE);
        e.addEdit(edit);
        e.end();
        fireChangedUpdate(e);
        fireUndoableEditUpdate(new UndoableEditEvent(this,e));
      }
  finally {
        writeUnlock();
      }
    }
  }
  /** 
 * Fetches the logical style assigned to the paragraph
 * represented by the given position.
 * @param p the location to translate to a paragraph
 * and determine the logical style assigned >= 0.  This
 * is an offset from the start of the document.
 * @return the style, null if none
 */
  public Style getLogicalStyle(  int p){
    Style s=null;
    Element paragraph=getParagraphElement(p);
    if (paragraph != null) {
      AttributeSet a=paragraph.getAttributes();
      AttributeSet parent=a.getResolveParent();
      if (parent instanceof Style) {
        s=(Style)parent;
      }
    }
    return s;
  }
  /** 
 * Sets attributes for some part of the document.
 * A write lock is held by this operation while changes
 * are being made, and a DocumentEvent is sent to the listeners
 * after the change has been successfully completed.
 * <p>
 * This method is thread safe, although most Swing methods
 * are not. Please see
 * <A HREF="http://java.sun.com/docs/books/tutorial/uiswing/misc/threads.html">How
 * to Use Threads</A> for more information.
 * @param offset the offset in the document >= 0
 * @param length the length >= 0
 * @param s the attributes
 * @param replace true if the previous attributes should be replaced
 * before setting the new attributes
 */
  public void setCharacterAttributes(  int offset,  int length,  AttributeSet s,  boolean replace){
    if (length == 0) {
      return;
    }
    try {
      writeLock();
      DefaultDocumentEvent changes=new DefaultDocumentEvent(offset,length,DocumentEvent.EventType.CHANGE);
      buffer.change(offset,length,changes);
      AttributeSet sCopy=s.copyAttributes();
      int lastEnd;
      for (int pos=offset; pos < (offset + length); pos=lastEnd) {
        Element run=getCharacterElement(pos);
        lastEnd=run.getEndOffset();
        if (pos == lastEnd) {
          break;
        }
        MutableAttributeSet attr=(MutableAttributeSet)run.getAttributes();
        changes.addEdit(new AttributeUndoableEdit(run,sCopy,replace));
        if (replace) {
          attr.removeAttributes(attr);
        }
        attr.addAttributes(s);
      }
      changes.end();
      fireChangedUpdate(changes);
      fireUndoableEditUpdate(new UndoableEditEvent(this,changes));
    }
  finally {
      writeUnlock();
    }
  }
  /** 
 * Sets attributes for a paragraph.
 * <p>
 * This method is thread safe, although most Swing methods
 * are not. Please see
 * <A HREF="http://java.sun.com/docs/books/tutorial/uiswing/misc/threads.html">How
 * to Use Threads</A> for more information.
 * @param offset the offset into the paragraph >= 0
 * @param length the number of characters affected >= 0
 * @param s the attributes
 * @param replace whether to replace existing attributes, or merge them
 */
  public void setParagraphAttributes(  int offset,  int length,  AttributeSet s,  boolean replace){
    try {
      writeLock();
      DefaultDocumentEvent changes=new DefaultDocumentEvent(offset,length,DocumentEvent.EventType.CHANGE);
      AttributeSet sCopy=s.copyAttributes();
      Element section=getDefaultRootElement();
      int index0=section.getElementIndex(offset);
      int index1=section.getElementIndex(offset + ((length > 0) ? length - 1 : 0));
      boolean isI18N=Boolean.TRUE.equals(getProperty(I18NProperty));
      boolean hasRuns=false;
      for (int i=index0; i <= index1; i++) {
        Element paragraph=section.getElement(i);
        MutableAttributeSet attr=(MutableAttributeSet)paragraph.getAttributes();
        changes.addEdit(new AttributeUndoableEdit(paragraph,sCopy,replace));
        if (replace) {
          attr.removeAttributes(attr);
        }
        attr.addAttributes(s);
        if (isI18N && !hasRuns) {
          hasRuns=(attr.getAttribute(TextAttribute.RUN_DIRECTION) != null);
        }
      }
      if (hasRuns) {
        updateBidi(changes);
      }
      changes.end();
      fireChangedUpdate(changes);
      fireUndoableEditUpdate(new UndoableEditEvent(this,changes));
    }
  finally {
      writeUnlock();
    }
  }
  /** 
 * Gets the paragraph element at the offset <code>pos</code>.
 * A paragraph consists of at least one child Element, which is usually
 * a leaf.
 * @param pos the starting offset >= 0
 * @return the element
 */
  public Element getParagraphElement(  int pos){
    Element e;
    for (e=getDefaultRootElement(); !e.isLeaf(); ) {
      int index=e.getElementIndex(pos);
      e=e.getElement(index);
    }
    if (e != null)     return e.getParentElement();
    return e;
  }
  /** 
 * Gets a character element based on a position.
 * @param pos the position in the document >= 0
 * @return the element
 */
  public Element getCharacterElement(  int pos){
    Element e;
    for (e=getDefaultRootElement(); !e.isLeaf(); ) {
      int index=e.getElementIndex(pos);
      e=e.getElement(index);
    }
    return e;
  }
  /** 
 * Updates document structure as a result of text insertion.  This
 * will happen within a write lock.  This implementation simply
 * parses the inserted content for line breaks and builds up a set
 * of instructions for the element buffer.
 * @param chng a description of the document change
 * @param attr the attributes
 */
  protected void insertUpdate(  DefaultDocumentEvent chng,  AttributeSet attr){
    int offset=chng.getOffset();
    int length=chng.getLength();
    if (attr == null) {
      attr=SimpleAttributeSet.EMPTY;
    }
    Element paragraph=getParagraphElement(offset + length);
    AttributeSet pattr=paragraph.getAttributes();
    Element pParagraph=getParagraphElement(offset);
    Element run=pParagraph.getElement(pParagraph.getElementIndex(offset));
    int endOffset=offset + length;
    boolean insertingAtBoundry=(run.getEndOffset() == endOffset);
    AttributeSet cattr=run.getAttributes();
    try {
      Segment s=new Segment();
      Vector<ElementSpec> parseBuffer=new Vector<ElementSpec>();
      ElementSpec lastStartSpec=null;
      boolean insertingAfterNewline=false;
      short lastStartDirection=ElementSpec.OriginateDirection;
      if (offset > 0) {
        getText(offset - 1,1,s);
        if (s.array[s.offset] == '\n') {
          insertingAfterNewline=true;
          lastStartDirection=createSpecsForInsertAfterNewline(paragraph,pParagraph,pattr,parseBuffer,offset,endOffset);
          for (int counter=parseBuffer.size() - 1; counter >= 0; counter--) {
            ElementSpec spec=parseBuffer.elementAt(counter);
            if (spec.getType() == ElementSpec.StartTagType) {
              lastStartSpec=spec;
              break;
            }
          }
        }
      }
      if (!insertingAfterNewline)       pattr=pParagraph.getAttributes();
      getText(offset,length,s);
      char[] txt=s.array;
      int n=s.offset + s.count;
      int lastOffset=s.offset;
      for (int i=s.offset; i < n; i++) {
        if (txt[i] == '\n') {
          int breakOffset=i + 1;
          parseBuffer.addElement(new ElementSpec(attr,ElementSpec.ContentType,breakOffset - lastOffset));
          parseBuffer.addElement(new ElementSpec(null,ElementSpec.EndTagType));
          lastStartSpec=new ElementSpec(pattr,ElementSpec.StartTagType);
          parseBuffer.addElement(lastStartSpec);
          lastOffset=breakOffset;
        }
      }
      if (lastOffset < n) {
        parseBuffer.addElement(new ElementSpec(attr,ElementSpec.ContentType,n - lastOffset));
      }
      ElementSpec first=parseBuffer.firstElement();
      int docLength=getLength();
      if (first.getType() == ElementSpec.ContentType && cattr.isEqual(attr)) {
        first.setDirection(ElementSpec.JoinPreviousDirection);
      }
      if (lastStartSpec != null) {
        if (insertingAfterNewline) {
          lastStartSpec.setDirection(lastStartDirection);
        }
 else         if (pParagraph.getEndOffset() != endOffset) {
          lastStartSpec.setDirection(ElementSpec.JoinFractureDirection);
        }
 else {
          Element parent=pParagraph.getParentElement();
          int pParagraphIndex=parent.getElementIndex(offset);
          if ((pParagraphIndex + 1) < parent.getElementCount() && !parent.getElement(pParagraphIndex + 1).isLeaf()) {
            lastStartSpec.setDirection(ElementSpec.JoinNextDirection);
          }
        }
      }
      if (insertingAtBoundry && endOffset < docLength) {
        ElementSpec last=parseBuffer.lastElement();
        if (last.getType() == ElementSpec.ContentType && last.getDirection() != ElementSpec.JoinPreviousDirection && ((lastStartSpec == null && (paragraph == pParagraph || insertingAfterNewline)) || (lastStartSpec != null && lastStartSpec.getDirection() != ElementSpec.OriginateDirection))) {
          Element nextRun=paragraph.getElement(paragraph.getElementIndex(endOffset));
          if (nextRun.isLeaf() && attr.isEqual(nextRun.getAttributes())) {
            last.setDirection(ElementSpec.JoinNextDirection);
          }
        }
      }
 else       if (!insertingAtBoundry && lastStartSpec != null && lastStartSpec.getDirection() == ElementSpec.JoinFractureDirection) {
        ElementSpec last=parseBuffer.lastElement();
        if (last.getType() == ElementSpec.ContentType && last.getDirection() != ElementSpec.JoinPreviousDirection && attr.isEqual(cattr)) {
          last.setDirection(ElementSpec.JoinNextDirection);
        }
      }
      if (Utilities.isComposedTextAttributeDefined(attr)) {
        MutableAttributeSet mattr=(MutableAttributeSet)attr;
        mattr.addAttributes(cattr);
        mattr.addAttribute(AbstractDocument.ElementNameAttribute,AbstractDocument.ContentElementName);
        mattr.addAttribute(StyleConstants.NameAttribute,AbstractDocument.ContentElementName);
        if (mattr.isDefined(IMPLIED_CR)) {
          mattr.removeAttribute(IMPLIED_CR);
        }
      }
      ElementSpec[] spec=new ElementSpec[parseBuffer.size()];
      parseBuffer.copyInto(spec);
      buffer.insert(offset,length,spec,chng);
    }
 catch (    BadLocationException bl) {
    }
    super.insertUpdate(chng,attr);
  }
  /** 
 * This is called by insertUpdate when inserting after a new line.
 * It generates, in <code>parseBuffer</code>, ElementSpecs that will
 * position the stack in <code>paragraph</code>.<p>
 * It returns the direction the last StartSpec should have (this don't
 * necessarily create the last start spec).
 */
  short createSpecsForInsertAfterNewline(  Element paragraph,  Element pParagraph,  AttributeSet pattr,  Vector<ElementSpec> parseBuffer,  int offset,  int endOffset){
    if (paragraph.getParentElement() == pParagraph.getParentElement()) {
      ElementSpec spec=new ElementSpec(pattr,ElementSpec.EndTagType);
      parseBuffer.addElement(spec);
      spec=new ElementSpec(pattr,ElementSpec.StartTagType);
      parseBuffer.addElement(spec);
      if (pParagraph.getEndOffset() != endOffset)       return ElementSpec.JoinFractureDirection;
      Element parent=pParagraph.getParentElement();
      if ((parent.getElementIndex(offset) + 1) < parent.getElementCount())       return ElementSpec.JoinNextDirection;
    }
 else {
      Vector<Element> leftParents=new Vector<Element>();
      Vector<Element> rightParents=new Vector<Element>();
      Element e=pParagraph;
      while (e != null) {
        leftParents.addElement(e);
        e=e.getParentElement();
      }
      e=paragraph;
      int leftIndex=-1;
      while (e != null && (leftIndex=leftParents.indexOf(e)) == -1) {
        rightParents.addElement(e);
        e=e.getParentElement();
      }
      if (e != null) {
        for (int counter=0; counter < leftIndex; counter++) {
          parseBuffer.addElement(new ElementSpec(null,ElementSpec.EndTagType));
        }
        ElementSpec spec;
        for (int counter=rightParents.size() - 1; counter >= 0; counter--) {
          spec=new ElementSpec(rightParents.elementAt(counter).getAttributes(),ElementSpec.StartTagType);
          if (counter > 0)           spec.setDirection(ElementSpec.JoinNextDirection);
          parseBuffer.addElement(spec);
        }
        if (rightParents.size() > 0)         return ElementSpec.JoinNextDirection;
        return ElementSpec.JoinFractureDirection;
      }
    }
    return ElementSpec.OriginateDirection;
  }
  /** 
 * Updates document structure as a result of text removal.
 * @param chng a description of the document change
 */
  protected void removeUpdate(  DefaultDocumentEvent chng){
    super.removeUpdate(chng);
    buffer.remove(chng.getOffset(),chng.getLength(),chng);
  }
  /** 
 * Creates the root element to be used to represent the
 * default document structure.
 * @return the element base
 */
  protected AbstractElement createDefaultRoot(){
    writeLock();
    BranchElement section=new SectionElement();
    BranchElement paragraph=new BranchElement(section,null);
    LeafElement brk=new LeafElement(paragraph,null,0,1);
    Element[] buff=new Element[1];
    buff[0]=brk;
    paragraph.replace(0,0,buff);
    buff[0]=paragraph;
    section.replace(0,0,buff);
    writeUnlock();
    return section;
  }
  /** 
 * Gets the foreground color from an attribute set.
 * @param attr the attribute set
 * @return the color
 */
  public Color getForeground(  AttributeSet attr){
    StyleContext styles=(StyleContext)getAttributeContext();
    return styles.getForeground(attr);
  }
  /** 
 * Gets the background color from an attribute set.
 * @param attr the attribute set
 * @return the color
 */
  public Color getBackground(  AttributeSet attr){
    StyleContext styles=(StyleContext)getAttributeContext();
    return styles.getBackground(attr);
  }
  /** 
 * Gets the font from an attribute set.
 * @param attr the attribute set
 * @return the font
 */
  public Font getFont(  AttributeSet attr){
    StyleContext styles=(StyleContext)getAttributeContext();
    return styles.getFont(attr);
  }
  /** 
 * Called when any of this document's styles have changed.
 * Subclasses may wish to be intelligent about what gets damaged.
 * @param style The Style that has changed.
 */
  protected void styleChanged(  Style style){
    if (getLength() != 0) {
      if (updateRunnable == null) {
        updateRunnable=new ChangeUpdateRunnable();
      }
synchronized (updateRunnable) {
        if (!updateRunnable.isPending) {
          SwingUtilities.invokeLater(updateRunnable);
          updateRunnable.isPending=true;
        }
      }
    }
  }
  /** 
 * Adds a document listener for notification of any changes.
 * @param listener the listener
 * @see Document#addDocumentListener
 */
  public void addDocumentListener(  DocumentListener listener){
synchronized (listeningStyles) {
      int oldDLCount=listenerList.getListenerCount(DocumentListener.class);
      super.addDocumentListener(listener);
      if (oldDLCount == 0) {
        if (styleContextChangeListener == null) {
          styleContextChangeListener=createStyleContextChangeListener();
        }
        if (styleContextChangeListener != null) {
          StyleContext styles=(StyleContext)getAttributeContext();
          List<ChangeListener> staleListeners=AbstractChangeHandler.getStaleListeners(styleContextChangeListener);
          for (          ChangeListener l : staleListeners) {
            styles.removeChangeListener(l);
          }
          styles.addChangeListener(styleContextChangeListener);
        }
        updateStylesListeningTo();
      }
    }
  }
  /** 
 * Removes a document listener.
 * @param listener the listener
 * @see Document#removeDocumentListener
 */
  public void removeDocumentListener(  DocumentListener listener){
synchronized (listeningStyles) {
      super.removeDocumentListener(listener);
      if (listenerList.getListenerCount(DocumentListener.class) == 0) {
        for (int counter=listeningStyles.size() - 1; counter >= 0; counter--) {
          listeningStyles.elementAt(counter).removeChangeListener(styleChangeListener);
        }
        listeningStyles.removeAllElements();
        if (styleContextChangeListener != null) {
          StyleContext styles=(StyleContext)getAttributeContext();
          styles.removeChangeListener(styleContextChangeListener);
        }
      }
    }
  }
  /** 
 * Returns a new instance of StyleChangeHandler.
 */
  ChangeListener createStyleChangeListener(){
    return new StyleChangeHandler(this);
  }
  /** 
 * Returns a new instance of StyleContextChangeHandler.
 */
  ChangeListener createStyleContextChangeListener(){
    return new StyleContextChangeHandler(this);
  }
  /** 
 * Adds a ChangeListener to new styles, and removes ChangeListener from
 * old styles.
 */
  void updateStylesListeningTo(){
synchronized (listeningStyles) {
      StyleContext styles=(StyleContext)getAttributeContext();
      if (styleChangeListener == null) {
        styleChangeListener=createStyleChangeListener();
      }
      if (styleChangeListener != null && styles != null) {
        Enumeration styleNames=styles.getStyleNames();
        Vector v=(Vector)listeningStyles.clone();
        listeningStyles.removeAllElements();
        List<ChangeListener> staleListeners=AbstractChangeHandler.getStaleListeners(styleChangeListener);
        while (styleNames.hasMoreElements()) {
          String name=(String)styleNames.nextElement();
          Style aStyle=styles.getStyle(name);
          int index=v.indexOf(aStyle);
          listeningStyles.addElement(aStyle);
          if (index == -1) {
            for (            ChangeListener l : staleListeners) {
              aStyle.removeChangeListener(l);
            }
            aStyle.addChangeListener(styleChangeListener);
          }
 else {
            v.removeElementAt(index);
          }
        }
        for (int counter=v.size() - 1; counter >= 0; counter--) {
          Style aStyle=(Style)v.elementAt(counter);
          aStyle.removeChangeListener(styleChangeListener);
        }
        if (listeningStyles.size() == 0) {
          styleChangeListener=null;
        }
      }
    }
  }
  private void readObject(  ObjectInputStream s) throws ClassNotFoundException, IOException {
    listeningStyles=new Vector<Style>();
    s.defaultReadObject();
    if (styleContextChangeListener == null && listenerList.getListenerCount(DocumentListener.class) > 0) {
      styleContextChangeListener=createStyleContextChangeListener();
      if (styleContextChangeListener != null) {
        StyleContext styles=(StyleContext)getAttributeContext();
        styles.addChangeListener(styleContextChangeListener);
      }
      updateStylesListeningTo();
    }
  }
  /** 
 * The default size of the initial content buffer.
 */
  public static final int BUFFER_SIZE_DEFAULT=4096;
  protected ElementBuffer buffer;
  /** 
 * Styles listening to. 
 */
  private transient Vector<Style> listeningStyles;
  /** 
 * Listens to Styles. 
 */
  private transient ChangeListener styleChangeListener;
  /** 
 * Listens to Styles. 
 */
  private transient ChangeListener styleContextChangeListener;
  /** 
 * Run to create a change event for the document 
 */
  private transient ChangeUpdateRunnable updateRunnable;
  /** 
 * Default root element for a document... maps out the
 * paragraphs/lines contained.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases. The current serialization support is
 * appropriate for short term storage or RMI between applications running
 * the same version of Swing.  As of 1.4, support for long term storage
 * of all JavaBeans<sup><font size="-2">TM</font></sup>
 * has been added to the <code>java.beans</code> package.
 * Please see {@link java.beans.XMLEncoder}.
 */
protected class SectionElement extends BranchElement {
    /** 
 * Creates a new SectionElement.
 */
    public SectionElement(){
      super(null,null);
    }
    /** 
 * Gets the name of the element.
 * @return the name
 */
    public String getName(){
      return SectionElementName;
    }
  }
  /** 
 * Specification for building elements.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases. The current serialization support is
 * appropriate for short term storage or RMI between applications running
 * the same version of Swing.  As of 1.4, support for long term storage
 * of all JavaBeans<sup><font size="-2">TM</font></sup>
 * has been added to the <code>java.beans</code> package.
 * Please see {@link java.beans.XMLEncoder}.
 */
public static class ElementSpec {
    /** 
 * A possible value for getType.  This specifies
 * that this record type is a start tag and
 * represents markup that specifies the start
 * of an element.
 */
    public static final short StartTagType=1;
    /** 
 * A possible value for getType.  This specifies
 * that this record type is a end tag and
 * represents markup that specifies the end
 * of an element.
 */
    public static final short EndTagType=2;
    /** 
 * A possible value for getType.  This specifies
 * that this record type represents content.
 */
    public static final short ContentType=3;
    /** 
 * A possible value for getDirection.  This specifies
 * that the data associated with this record should
 * be joined to what precedes it.
 */
    public static final short JoinPreviousDirection=4;
    /** 
 * A possible value for getDirection.  This specifies
 * that the data associated with this record should
 * be joined to what follows it.
 */
    public static final short JoinNextDirection=5;
    /** 
 * A possible value for getDirection.  This specifies
 * that the data associated with this record should
 * be used to originate a new element.  This would be
 * the normal value.
 */
    public static final short OriginateDirection=6;
    /** 
 * A possible value for getDirection.  This specifies
 * that the data associated with this record should
 * be joined to the fractured element.
 */
    public static final short JoinFractureDirection=7;
    /** 
 * Constructor useful for markup when the markup will not
 * be stored in the document.
 * @param a the attributes for the element
 * @param type the type of the element (StartTagType, EndTagType,
 * ContentType)
 */
    public ElementSpec(    AttributeSet a,    short type){
      this(a,type,null,0,0);
    }
    /** 
 * Constructor for parsing inside the document when
 * the data has already been added, but len information
 * is needed.
 * @param a the attributes for the element
 * @param type the type of the element (StartTagType, EndTagType,
 * ContentType)
 * @param len the length >= 0
 */
    public ElementSpec(    AttributeSet a,    short type,    int len){
      this(a,type,null,0,len);
    }
    /** 
 * Constructor for creating a spec externally for batch
 * input of content and markup into the document.
 * @param a the attributes for the element
 * @param type the type of the element (StartTagType, EndTagType,
 * ContentType)
 * @param txt the text for the element
 * @param offs the offset into the text >= 0
 * @param len the length of the text >= 0
 */
    public ElementSpec(    AttributeSet a,    short type,    char[] txt,    int offs,    int len){
      attr=a;
      this.type=type;
      this.data=txt;
      this.offs=offs;
      this.len=len;
      this.direction=OriginateDirection;
    }
    /** 
 * Sets the element type.
 * @param type the type of the element (StartTagType, EndTagType,
 * ContentType)
 */
    public void setType(    short type){
      this.type=type;
    }
    /** 
 * Gets the element type.
 * @return  the type of the element (StartTagType, EndTagType,
 * ContentType)
 */
    public short getType(){
      return type;
    }
    /** 
 * Sets the direction.
 * @param direction the direction (JoinPreviousDirection,
 * JoinNextDirection)
 */
    public void setDirection(    short direction){
      this.direction=direction;
    }
    /** 
 * Gets the direction.
 * @return the direction (JoinPreviousDirection, JoinNextDirection)
 */
    public short getDirection(){
      return direction;
    }
    /** 
 * Gets the element attributes.
 * @return the attribute set
 */
    public AttributeSet getAttributes(){
      return attr;
    }
    /** 
 * Gets the array of characters.
 * @return the array
 */
    public char[] getArray(){
      return data;
    }
    /** 
 * Gets the starting offset.
 * @return the offset >= 0
 */
    public int getOffset(){
      return offs;
    }
    /** 
 * Gets the length.
 * @return the length >= 0
 */
    public int getLength(){
      return len;
    }
    /** 
 * Converts the element to a string.
 * @return the string
 */
    public String toString(){
      String tlbl="??";
      String plbl="??";
switch (type) {
case StartTagType:
        tlbl="StartTag";
      break;
case ContentType:
    tlbl="Content";
  break;
case EndTagType:
tlbl="EndTag";
break;
}
switch (direction) {
case JoinPreviousDirection:
plbl="JoinPrevious";
break;
case JoinNextDirection:
plbl="JoinNext";
break;
case OriginateDirection:
plbl="Originate";
break;
case JoinFractureDirection:
plbl="Fracture";
break;
}
return tlbl + ":" + plbl+ ":"+ getLength();
}
private AttributeSet attr;
private int len;
private short type;
private short direction;
private int offs;
private char[] data;
}
/** 
 * Class to manage changes to the element
 * hierarchy.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases. The current serialization support is
 * appropriate for short term storage or RMI between applications running
 * the same version of Swing.  As of 1.4, support for long term storage
 * of all JavaBeans<sup><font size="-2">TM</font></sup>
 * has been added to the <code>java.beans</code> package.
 * Please see {@link java.beans.XMLEncoder}.
 */
public class ElementBuffer implements Serializable {
/** 
 * Creates a new ElementBuffer.
 * @param root the root element
 * @since 1.4
 */
public ElementBuffer(Element root){
this.root=root;
changes=new Vector<ElemChanges>();
path=new Stack<ElemChanges>();
}
/** 
 * Gets the root element.
 * @return the root element
 */
public Element getRootElement(){
return root;
}
/** 
 * Inserts new content.
 * @param offset the starting offset >= 0
 * @param length the length >= 0
 * @param data the data to insert
 * @param de the event capturing this edit
 */
public void insert(int offset,int length,ElementSpec[] data,DefaultDocumentEvent de){
if (length == 0) {
return;
}
insertOp=true;
beginEdits(offset,length);
insertUpdate(data);
endEdits(de);
insertOp=false;
}
void create(int length,ElementSpec[] data,DefaultDocumentEvent de){
insertOp=true;
beginEdits(offset,length);
Element elem=root;
int index=elem.getElementIndex(0);
while (!elem.isLeaf()) {
Element child=elem.getElement(index);
push(elem,index);
elem=child;
index=elem.getElementIndex(0);
}
ElemChanges ec=path.peek();
Element child=ec.parent.getElement(ec.index);
ec.added.addElement(createLeafElement(ec.parent,child.getAttributes(),getLength(),child.getEndOffset()));
ec.removed.addElement(child);
while (path.size() > 1) {
pop();
}
int n=data.length;
AttributeSet newAttrs=null;
if (n > 0 && data[0].getType() == ElementSpec.StartTagType) {
newAttrs=data[0].getAttributes();
}
if (newAttrs == null) {
newAttrs=SimpleAttributeSet.EMPTY;
}
MutableAttributeSet attr=(MutableAttributeSet)root.getAttributes();
de.addEdit(new AttributeUndoableEdit(root,newAttrs,true));
attr.removeAttributes(attr);
attr.addAttributes(newAttrs);
for (int i=1; i < n; i++) {
insertElement(data[i]);
}
while (path.size() != 0) {
pop();
}
endEdits(de);
insertOp=false;
}
/** 
 * Removes content.
 * @param offset the starting offset >= 0
 * @param length the length >= 0
 * @param de the event capturing this edit
 */
public void remove(int offset,int length,DefaultDocumentEvent de){
beginEdits(offset,length);
removeUpdate();
endEdits(de);
}
/** 
 * Changes content.
 * @param offset the starting offset >= 0
 * @param length the length >= 0
 * @param de the event capturing this edit
 */
public void change(int offset,int length,DefaultDocumentEvent de){
beginEdits(offset,length);
changeUpdate();
endEdits(de);
}
/** 
 * Inserts an update into the document.
 * @param data the elements to insert
 */
protected void insertUpdate(ElementSpec[] data){
Element elem=root;
int index=elem.getElementIndex(offset);
while (!elem.isLeaf()) {
Element child=elem.getElement(index);
push(elem,(child.isLeaf() ? index : index + 1));
elem=child;
index=elem.getElementIndex(offset);
}
insertPath=new ElemChanges[path.size()];
path.copyInto(insertPath);
createdFracture=false;
int i;
recreateLeafs=false;
if (data[0].getType() == ElementSpec.ContentType) {
insertFirstContent(data);
pos+=data[0].getLength();
i=1;
}
 else {
fractureDeepestLeaf(data);
i=0;
}
int n=data.length;
for (; i < n; i++) {
insertElement(data[i]);
}
if (!createdFracture) fracture(-1);
while (path.size() != 0) {
pop();
}
if (offsetLastIndex && offsetLastIndexOnReplace) {
insertPath[insertPath.length - 1].index++;
}
for (int counter=insertPath.length - 1; counter >= 0; counter--) {
ElemChanges change=insertPath[counter];
if (change.parent == fracturedParent) change.added.addElement(fracturedChild);
if ((change.added.size() > 0 || change.removed.size() > 0) && !changes.contains(change)) {
changes.addElement(change);
}
}
if (offset == 0 && fracturedParent != null && data[0].getType() == ElementSpec.EndTagType) {
int counter=0;
while (counter < data.length && data[counter].getType() == ElementSpec.EndTagType) {
counter++;
}
ElemChanges change=insertPath[insertPath.length - counter - 1];
change.removed.insertElementAt(change.parent.getElement(--change.index),0);
}
}
/** 
 * Updates the element structure in response to a removal from the
 * associated sequence in the document.  Any elements consumed by the
 * span of the removal are removed.
 */
protected void removeUpdate(){
removeElements(root,offset,offset + length);
}
/** 
 * Updates the element structure in response to a change in the
 * document.
 */
protected void changeUpdate(){
boolean didEnd=split(offset,length);
if (!didEnd) {
while (path.size() != 0) {
pop();
}
split(offset + length,0);
}
while (path.size() != 0) {
pop();
}
}
boolean split(int offs,int len){
boolean splitEnd=false;
Element e=root;
int index=e.getElementIndex(offs);
while (!e.isLeaf()) {
push(e,index);
e=e.getElement(index);
index=e.getElementIndex(offs);
}
ElemChanges ec=path.peek();
Element child=ec.parent.getElement(ec.index);
if (child.getStartOffset() < offs && offs < child.getEndOffset()) {
int index0=ec.index;
int index1=index0;
if (((offs + len) < ec.parent.getEndOffset()) && (len != 0)) {
index1=ec.parent.getElementIndex(offs + len);
if (index1 == index0) {
ec.removed.addElement(child);
e=createLeafElement(ec.parent,child.getAttributes(),child.getStartOffset(),offs);
ec.added.addElement(e);
e=createLeafElement(ec.parent,child.getAttributes(),offs,offs + len);
ec.added.addElement(e);
e=createLeafElement(ec.parent,child.getAttributes(),offs + len,child.getEndOffset());
ec.added.addElement(e);
return true;
}
 else {
child=ec.parent.getElement(index1);
if ((offs + len) == child.getStartOffset()) {
index1=index0;
}
}
splitEnd=true;
}
pos=offs;
child=ec.parent.getElement(index0);
ec.removed.addElement(child);
e=createLeafElement(ec.parent,child.getAttributes(),child.getStartOffset(),pos);
ec.added.addElement(e);
e=createLeafElement(ec.parent,child.getAttributes(),pos,child.getEndOffset());
ec.added.addElement(e);
for (int i=index0 + 1; i < index1; i++) {
child=ec.parent.getElement(i);
ec.removed.addElement(child);
ec.added.addElement(child);
}
if (index1 != index0) {
child=ec.parent.getElement(index1);
pos=offs + len;
ec.removed.addElement(child);
e=createLeafElement(ec.parent,child.getAttributes(),child.getStartOffset(),pos);
ec.added.addElement(e);
e=createLeafElement(ec.parent,child.getAttributes(),pos,child.getEndOffset());
ec.added.addElement(e);
}
}
return splitEnd;
}
/** 
 * Creates the UndoableEdit record for the edits made
 * in the buffer.
 */
void endEdits(DefaultDocumentEvent de){
int n=changes.size();
for (int i=0; i < n; i++) {
ElemChanges ec=changes.elementAt(i);
Element[] removed=new Element[ec.removed.size()];
ec.removed.copyInto(removed);
Element[] added=new Element[ec.added.size()];
ec.added.copyInto(added);
int index=ec.index;
((BranchElement)ec.parent).replace(index,removed.length,added);
ElementEdit ee=new ElementEdit(ec.parent,index,removed,added);
de.addEdit(ee);
}
changes.removeAllElements();
path.removeAllElements();
}
/** 
 * Initialize the buffer
 */
void beginEdits(int offset,int length){
this.offset=offset;
this.length=length;
this.endOffset=offset + length;
pos=offset;
if (changes == null) {
changes=new Vector<ElemChanges>();
}
 else {
changes.removeAllElements();
}
if (path == null) {
path=new Stack<ElemChanges>();
}
 else {
path.removeAllElements();
}
fracturedParent=null;
fracturedChild=null;
offsetLastIndex=offsetLastIndexOnReplace=false;
}
/** 
 * Pushes a new element onto the stack that represents
 * the current path.
 * @param record Whether or not the push should be
 * recorded as an element change or not.
 * @param isFracture true if pushing on an element that was created
 * as the result of a fracture.
 */
void push(Element e,int index,boolean isFracture){
ElemChanges ec=new ElemChanges(e,index,isFracture);
path.push(ec);
}
void push(Element e,int index){
push(e,index,false);
}
void pop(){
ElemChanges ec=path.peek();
path.pop();
if ((ec.added.size() > 0) || (ec.removed.size() > 0)) {
changes.addElement(ec);
}
 else if (!path.isEmpty()) {
Element e=ec.parent;
if (e.getElementCount() == 0) {
ec=path.peek();
ec.added.removeElement(e);
}
}
}
/** 
 * move the current offset forward by n.
 */
void advance(int n){
pos+=n;
}
void insertElement(ElementSpec es){
ElemChanges ec=path.peek();
switch (es.getType()) {
case ElementSpec.StartTagType:
switch (es.getDirection()) {
case ElementSpec.JoinNextDirection:
Element parent=ec.parent.getElement(ec.index);
if (parent.isLeaf()) {
if ((ec.index + 1) < ec.parent.getElementCount()) parent=ec.parent.getElement(ec.index + 1);
 else throw new StateInvariantError("Join next to leaf");
}
push(parent,0,true);
break;
case ElementSpec.JoinFractureDirection:
if (!createdFracture) {
fracture(path.size() - 1);
}
if (!ec.isFracture) {
push(fracturedChild,0,true);
}
 else push(ec.parent.getElement(0),0,true);
break;
default :
Element belem=createBranchElement(ec.parent,es.getAttributes());
ec.added.addElement(belem);
push(belem,0);
break;
}
break;
case ElementSpec.EndTagType:
pop();
break;
case ElementSpec.ContentType:
int len=es.getLength();
if (es.getDirection() != ElementSpec.JoinNextDirection) {
Element leaf=createLeafElement(ec.parent,es.getAttributes(),pos,pos + len);
ec.added.addElement(leaf);
}
 else {
if (!ec.isFracture) {
Element first=null;
if (insertPath != null) {
for (int counter=insertPath.length - 1; counter >= 0; counter--) {
if (insertPath[counter] == ec) {
if (counter != (insertPath.length - 1)) first=ec.parent.getElement(ec.index);
break;
}
}
}
if (first == null) first=ec.parent.getElement(ec.index + 1);
Element leaf=createLeafElement(ec.parent,first.getAttributes(),pos,first.getEndOffset());
ec.added.addElement(leaf);
ec.removed.addElement(first);
}
 else {
Element first=ec.parent.getElement(0);
Element leaf=createLeafElement(ec.parent,first.getAttributes(),pos,first.getEndOffset());
ec.added.addElement(leaf);
ec.removed.addElement(first);
}
}
pos+=len;
break;
}
}
/** 
 * Remove the elements from <code>elem</code> in range
 * <code>rmOffs0</code>, <code>rmOffs1</code>. This uses
 * <code>canJoin</code> and <code>join</code> to handle joining
 * the endpoints of the insertion.
 * @return true if elem will no longer have any elements.
 */
boolean removeElements(Element elem,int rmOffs0,int rmOffs1){
if (!elem.isLeaf()) {
int index0=elem.getElementIndex(rmOffs0);
int index1=elem.getElementIndex(rmOffs1);
push(elem,index0);
ElemChanges ec=path.peek();
if (index0 == index1) {
Element child0=elem.getElement(index0);
if (rmOffs0 <= child0.getStartOffset() && rmOffs1 >= child0.getEndOffset()) {
ec.removed.addElement(child0);
}
 else if (removeElements(child0,rmOffs0,rmOffs1)) {
ec.removed.addElement(child0);
}
}
 else {
Element child0=elem.getElement(index0);
Element child1=elem.getElement(index1);
boolean containsOffs1=(rmOffs1 < elem.getEndOffset());
if (containsOffs1 && canJoin(child0,child1)) {
for (int i=index0; i <= index1; i++) {
ec.removed.addElement(elem.getElement(i));
}
Element e=join(elem,child0,child1,rmOffs0,rmOffs1);
ec.added.addElement(e);
}
 else {
int rmIndex0=index0 + 1;
int rmIndex1=index1 - 1;
if (child0.getStartOffset() == rmOffs0 || (index0 == 0 && child0.getStartOffset() > rmOffs0 && child0.getEndOffset() <= rmOffs1)) {
child0=null;
rmIndex0=index0;
}
if (!containsOffs1) {
child1=null;
rmIndex1++;
}
 else if (child1.getStartOffset() == rmOffs1) {
child1=null;
}
if (rmIndex0 <= rmIndex1) {
ec.index=rmIndex0;
}
for (int i=rmIndex0; i <= rmIndex1; i++) {
ec.removed.addElement(elem.getElement(i));
}
if (child0 != null) {
if (removeElements(child0,rmOffs0,rmOffs1)) {
ec.removed.insertElementAt(child0,0);
ec.index=index0;
}
}
if (child1 != null) {
if (removeElements(child1,rmOffs0,rmOffs1)) {
ec.removed.addElement(child1);
}
}
}
}
pop();
if (elem.getElementCount() == (ec.removed.size() - ec.added.size())) {
return true;
}
}
return false;
}
/** 
 * Can the two given elements be coelesced together
 * into one element?
 */
boolean canJoin(Element e0,Element e1){
if ((e0 == null) || (e1 == null)) {
return false;
}
boolean leaf0=e0.isLeaf();
boolean leaf1=e1.isLeaf();
if (leaf0 != leaf1) {
return false;
}
if (leaf0) {
return e0.getAttributes().isEqual(e1.getAttributes());
}
String name0=e0.getName();
String name1=e1.getName();
if (name0 != null) {
return name0.equals(name1);
}
if (name1 != null) {
return name1.equals(name0);
}
return true;
}
/** 
 * Joins the two elements carving out a hole for the
 * given removed range.
 */
Element join(Element p,Element left,Element right,int rmOffs0,int rmOffs1){
if (left.isLeaf() && right.isLeaf()) {
return createLeafElement(p,left.getAttributes(),left.getStartOffset(),right.getEndOffset());
}
 else if ((!left.isLeaf()) && (!right.isLeaf())) {
Element to=createBranchElement(p,left.getAttributes());
int ljIndex=left.getElementIndex(rmOffs0);
int rjIndex=right.getElementIndex(rmOffs1);
Element lj=left.getElement(ljIndex);
if (lj.getStartOffset() >= rmOffs0) {
lj=null;
}
Element rj=right.getElement(rjIndex);
if (rj.getStartOffset() == rmOffs1) {
rj=null;
}
Vector<Element> children=new Vector<Element>();
for (int i=0; i < ljIndex; i++) {
children.addElement(clone(to,left.getElement(i)));
}
if (canJoin(lj,rj)) {
Element e=join(to,lj,rj,rmOffs0,rmOffs1);
children.addElement(e);
}
 else {
if (lj != null) {
children.addElement(cloneAsNecessary(to,lj,rmOffs0,rmOffs1));
}
if (rj != null) {
children.addElement(cloneAsNecessary(to,rj,rmOffs0,rmOffs1));
}
}
int n=right.getElementCount();
for (int i=(rj == null) ? rjIndex : rjIndex + 1; i < n; i++) {
children.addElement(clone(to,right.getElement(i)));
}
Element[] c=new Element[children.size()];
children.copyInto(c);
((BranchElement)to).replace(0,0,c);
return to;
}
 else {
throw new StateInvariantError("No support to join leaf element with non-leaf element");
}
}
/** 
 * Creates a copy of this element, with a different
 * parent.
 * @param parent the parent element
 * @param clonee the element to be cloned
 * @return the copy
 */
public Element clone(Element parent,Element clonee){
if (clonee.isLeaf()) {
return createLeafElement(parent,clonee.getAttributes(),clonee.getStartOffset(),clonee.getEndOffset());
}
Element e=createBranchElement(parent,clonee.getAttributes());
int n=clonee.getElementCount();
Element[] children=new Element[n];
for (int i=0; i < n; i++) {
children[i]=clone(e,clonee.getElement(i));
}
((BranchElement)e).replace(0,0,children);
return e;
}
/** 
 * Creates a copy of this element, with a different
 * parent. Children of this element included in the
 * removal range will be discarded.
 */
Element cloneAsNecessary(Element parent,Element clonee,int rmOffs0,int rmOffs1){
if (clonee.isLeaf()) {
return createLeafElement(parent,clonee.getAttributes(),clonee.getStartOffset(),clonee.getEndOffset());
}
Element e=createBranchElement(parent,clonee.getAttributes());
int n=clonee.getElementCount();
ArrayList<Element> childrenList=new ArrayList<Element>(n);
for (int i=0; i < n; i++) {
Element elem=clonee.getElement(i);
if (elem.getStartOffset() < rmOffs0 || elem.getEndOffset() > rmOffs1) {
childrenList.add(cloneAsNecessary(e,elem,rmOffs0,rmOffs1));
}
}
Element[] children=new Element[childrenList.size()];
children=childrenList.toArray(children);
((BranchElement)e).replace(0,0,children);
return e;
}
/** 
 * Determines if a fracture needs to be performed. A fracture
 * can be thought of as moving the right part of a tree to a
 * new location, where the right part is determined by what has
 * been inserted. <code>depth</code> is used to indicate a
 * JoinToFracture is needed to an element at a depth
 * of <code>depth</code>. Where the root is 0, 1 is the children
 * of the root...
 * <p>This will invoke <code>fractureFrom</code> if it is determined
 * a fracture needs to happen.
 */
void fracture(int depth){
int cLength=insertPath.length;
int lastIndex=-1;
boolean needRecreate=recreateLeafs;
ElemChanges lastChange=insertPath[cLength - 1];
boolean childAltered=((lastChange.index + 1) < lastChange.parent.getElementCount());
int deepestAlteredIndex=(needRecreate) ? cLength : -1;
int lastAlteredIndex=cLength - 1;
createdFracture=true;
for (int counter=cLength - 2; counter >= 0; counter--) {
ElemChanges change=insertPath[counter];
if (change.added.size() > 0 || counter == depth) {
lastIndex=counter;
if (!needRecreate && childAltered) {
needRecreate=true;
if (deepestAlteredIndex == -1) deepestAlteredIndex=lastAlteredIndex + 1;
}
}
if (!childAltered && change.index < change.parent.getElementCount()) {
childAltered=true;
lastAlteredIndex=counter;
}
}
if (needRecreate) {
if (lastIndex == -1) lastIndex=cLength - 1;
fractureFrom(insertPath,lastIndex,deepestAlteredIndex);
}
}
/** 
 * Recreates the elements to the right of the insertion point.
 * This starts at <code>startIndex</code> in <code>changed</code>,
 * and calls duplicate to duplicate existing elements.
 * This will also duplicate the elements along the insertion
 * point, until a depth of <code>endFractureIndex</code> is
 * reached, at which point only the elements to the right of
 * the insertion point are duplicated.
 */
void fractureFrom(ElemChanges[] changed,int startIndex,int endFractureIndex){
ElemChanges change=changed[startIndex];
Element child;
Element newChild;
int changeLength=changed.length;
if ((startIndex + 1) == changeLength) child=change.parent.getElement(change.index);
 else child=change.parent.getElement(change.index - 1);
if (child.isLeaf()) {
newChild=createLeafElement(change.parent,child.getAttributes(),Math.max(endOffset,child.getStartOffset()),child.getEndOffset());
}
 else {
newChild=createBranchElement(change.parent,child.getAttributes());
}
fracturedParent=change.parent;
fracturedChild=newChild;
Element parent=newChild;
while (++startIndex < endFractureIndex) {
boolean isEnd=((startIndex + 1) == endFractureIndex);
boolean isEndLeaf=((startIndex + 1) == changeLength);
change=changed[startIndex];
if (isEnd) {
if (offsetLastIndex || !isEndLeaf) child=null;
 else child=change.parent.getElement(change.index);
}
 else {
child=change.parent.getElement(change.index - 1);
}
if (child != null) {
if (child.isLeaf()) {
newChild=createLeafElement(parent,child.getAttributes(),Math.max(endOffset,child.getStartOffset()),child.getEndOffset());
}
 else {
newChild=createBranchElement(parent,child.getAttributes());
}
}
 else newChild=null;
int kidsToMove=change.parent.getElementCount() - change.index;
Element[] kids;
int moveStartIndex;
int kidStartIndex=1;
if (newChild == null) {
if (isEndLeaf) {
kidsToMove--;
moveStartIndex=change.index + 1;
}
 else {
moveStartIndex=change.index;
}
kidStartIndex=0;
kids=new Element[kidsToMove];
}
 else {
if (!isEnd) {
kidsToMove++;
moveStartIndex=change.index;
}
 else {
moveStartIndex=change.index + 1;
}
kids=new Element[kidsToMove];
kids[0]=newChild;
}
for (int counter=kidStartIndex; counter < kidsToMove; counter++) {
Element toMove=change.parent.getElement(moveStartIndex++);
kids[counter]=recreateFracturedElement(parent,toMove);
change.removed.addElement(toMove);
}
((BranchElement)parent).replace(0,0,kids);
parent=newChild;
}
}
/** 
 * Recreates <code>toDuplicate</code>. This is called when an
 * element needs to be created as the result of an insertion. This
 * will recurse and create all the children. This is similiar to
 * <code>clone</code>, but deteremines the offsets differently.
 */
Element recreateFracturedElement(Element parent,Element toDuplicate){
if (toDuplicate.isLeaf()) {
return createLeafElement(parent,toDuplicate.getAttributes(),Math.max(toDuplicate.getStartOffset(),endOffset),toDuplicate.getEndOffset());
}
Element newParent=createBranchElement(parent,toDuplicate.getAttributes());
int childCount=toDuplicate.getElementCount();
Element[] newKids=new Element[childCount];
for (int counter=0; counter < childCount; counter++) {
newKids[counter]=recreateFracturedElement(newParent,toDuplicate.getElement(counter));
}
((BranchElement)newParent).replace(0,0,newKids);
return newParent;
}
/** 
 * Splits the bottommost leaf in <code>path</code>.
 * This is called from insert when the first element is NOT content.
 */
void fractureDeepestLeaf(ElementSpec[] specs){
ElemChanges ec=path.peek();
Element child=ec.parent.getElement(ec.index);
if (offset != 0) {
Element newChild=createLeafElement(ec.parent,child.getAttributes(),child.getStartOffset(),offset);
ec.added.addElement(newChild);
}
ec.removed.addElement(child);
if (child.getEndOffset() != endOffset) recreateLeafs=true;
 else offsetLastIndex=true;
}
/** 
 * Inserts the first content. This needs to be separate to handle
 * joining.
 */
void insertFirstContent(ElementSpec[] specs){
ElementSpec firstSpec=specs[0];
ElemChanges ec=path.peek();
Element child=ec.parent.getElement(ec.index);
int firstEndOffset=offset + firstSpec.getLength();
boolean isOnlyContent=(specs.length == 1);
switch (firstSpec.getDirection()) {
case ElementSpec.JoinPreviousDirection:
if (child.getEndOffset() != firstEndOffset && !isOnlyContent) {
Element newE=createLeafElement(ec.parent,child.getAttributes(),child.getStartOffset(),firstEndOffset);
ec.added.addElement(newE);
ec.removed.addElement(child);
if (child.getEndOffset() != endOffset) recreateLeafs=true;
 else offsetLastIndex=true;
}
 else {
offsetLastIndex=true;
offsetLastIndexOnReplace=true;
}
break;
case ElementSpec.JoinNextDirection:
if (offset != 0) {
Element newE=createLeafElement(ec.parent,child.getAttributes(),child.getStartOffset(),offset);
ec.added.addElement(newE);
Element nextChild=ec.parent.getElement(ec.index + 1);
if (isOnlyContent) newE=createLeafElement(ec.parent,nextChild.getAttributes(),offset,nextChild.getEndOffset());
 else newE=createLeafElement(ec.parent,nextChild.getAttributes(),offset,firstEndOffset);
ec.added.addElement(newE);
ec.removed.addElement(child);
ec.removed.addElement(nextChild);
}
break;
default :
if (child.getStartOffset() != offset) {
Element newE=createLeafElement(ec.parent,child.getAttributes(),child.getStartOffset(),offset);
ec.added.addElement(newE);
}
ec.removed.addElement(child);
Element newE=createLeafElement(ec.parent,firstSpec.getAttributes(),offset,firstEndOffset);
ec.added.addElement(newE);
if (child.getEndOffset() != endOffset) {
recreateLeafs=true;
}
 else {
offsetLastIndex=true;
}
break;
}
}
Element root;
transient int pos;
transient int offset;
transient int length;
transient int endOffset;
transient Vector<ElemChanges> changes;
transient Stack<ElemChanges> path;
transient boolean insertOp;
transient boolean recreateLeafs;
/** 
 * For insert, path to inserted elements. 
 */
transient ElemChanges[] insertPath;
/** 
 * Only for insert, set to true when the fracture has been created. 
 */
transient boolean createdFracture;
/** 
 * Parent that contains the fractured child. 
 */
transient Element fracturedParent;
/** 
 * Fractured child. 
 */
transient Element fracturedChild;
/** 
 * Used to indicate when fracturing that the last leaf should be
 * skipped. 
 */
transient boolean offsetLastIndex;
/** 
 * Used to indicate that the parent of the deepest leaf should
 * offset the index by 1 when adding/removing elements in an
 * insert. 
 */
transient boolean offsetLastIndexOnReplace;
class ElemChanges {
ElemChanges(Element parent,int index,boolean isFracture){
this.parent=parent;
this.index=index;
this.isFracture=isFracture;
added=new Vector<Element>();
removed=new Vector<Element>();
}
public String toString(){
return "added: " + added + "\nremoved: "+ removed+ "\n";
}
Element parent;
int index;
Vector<Element> added;
Vector<Element> removed;
boolean isFracture;
}
}
/** 
 * An UndoableEdit used to remember AttributeSet changes to an
 * Element.
 */
public static class AttributeUndoableEdit extends AbstractUndoableEdit {
public AttributeUndoableEdit(Element element,AttributeSet newAttributes,boolean isReplacing){
super();
this.element=element;
this.newAttributes=newAttributes;
this.isReplacing=isReplacing;
copy=element.getAttributes().copyAttributes();
}
/** 
 * Redoes a change.
 * @exception CannotRedoException if the change cannot be redone
 */
public void redo() throws CannotRedoException {
super.redo();
MutableAttributeSet as=(MutableAttributeSet)element.getAttributes();
if (isReplacing) as.removeAttributes(as);
as.addAttributes(newAttributes);
}
/** 
 * Undoes a change.
 * @exception CannotUndoException if the change cannot be undone
 */
public void undo() throws CannotUndoException {
super.undo();
MutableAttributeSet as=(MutableAttributeSet)element.getAttributes();
as.removeAttributes(as);
as.addAttributes(copy);
}
protected AttributeSet newAttributes;
protected AttributeSet copy;
protected boolean isReplacing;
protected Element element;
}
/** 
 * UndoableEdit for changing the resolve parent of an Element.
 */
static class StyleChangeUndoableEdit extends AbstractUndoableEdit {
public StyleChangeUndoableEdit(AbstractElement element,Style newStyle){
super();
this.element=element;
this.newStyle=newStyle;
oldStyle=element.getResolveParent();
}
/** 
 * Redoes a change.
 * @exception CannotRedoException if the change cannot be redone
 */
public void redo() throws CannotRedoException {
super.redo();
element.setResolveParent(newStyle);
}
/** 
 * Undoes a change.
 * @exception CannotUndoException if the change cannot be undone
 */
public void undo() throws CannotUndoException {
super.undo();
element.setResolveParent(oldStyle);
}
/** 
 * Element to change resolve parent of. 
 */
protected AbstractElement element;
/** 
 * New style. 
 */
protected Style newStyle;
/** 
 * Old style, before setting newStyle. 
 */
protected AttributeSet oldStyle;
}
/** 
 * Base class for style change handlers with support for stale objects detection.
 */
abstract static class AbstractChangeHandler implements ChangeListener {
private class DocReference extends WeakReference<DefaultStyledDocument> {
DocReference(DefaultStyledDocument d,ReferenceQueue<DefaultStyledDocument> q){
super(d,q);
}
/** 
 * Return a reference to the style change handler object.
 */
ChangeListener getListener(){
return AbstractChangeHandler.this;
}
}
/** 
 * Class-specific reference queues.  
 */
private final static Map<Class,ReferenceQueue<DefaultStyledDocument>> queueMap=new HashMap<Class,ReferenceQueue<DefaultStyledDocument>>();
/** 
 * A weak reference to the document object.  
 */
private DocReference doc;
AbstractChangeHandler(DefaultStyledDocument d){
Class c=getClass();
ReferenceQueue<DefaultStyledDocument> q;
synchronized (queueMap) {
q=queueMap.get(c);
if (q == null) {
q=new ReferenceQueue<DefaultStyledDocument>();
queueMap.put(c,q);
}
}
doc=new DocReference(d,q);
}
/** 
 * Return a list of stale change listeners.
 * A change listener becomes "stale" when its document is cleaned by GC.
 */
static List<ChangeListener> getStaleListeners(ChangeListener l){
List<ChangeListener> staleListeners=new ArrayList<ChangeListener>();
ReferenceQueue<DefaultStyledDocument> q=queueMap.get(l.getClass());
if (q != null) {
DocReference r;
synchronized (q) {
while ((r=(DocReference)q.poll()) != null) {
staleListeners.add(r.getListener());
}
}
}
return staleListeners;
}
/** 
 * The ChangeListener wrapper which guards against dead documents.
 */
public void stateChanged(ChangeEvent e){
DefaultStyledDocument d=doc.get();
if (d != null) {
fireStateChanged(d,e);
}
}
/** 
 * Run the actual class-specific stateChanged() method.  
 */
abstract void fireStateChanged(DefaultStyledDocument d,ChangeEvent e);
}
/** 
 * Added to all the Styles. When instances of this receive a
 * stateChanged method, styleChanged is invoked.
 */
static class StyleChangeHandler extends AbstractChangeHandler {
StyleChangeHandler(DefaultStyledDocument d){
super(d);
}
void fireStateChanged(DefaultStyledDocument d,ChangeEvent e){
Object source=e.getSource();
if (source instanceof Style) {
d.styleChanged((Style)source);
}
 else {
d.styleChanged(null);
}
}
}
/** 
 * Added to the StyleContext. When the StyleContext changes, this invokes
 * <code>updateStylesListeningTo</code>.
 */
static class StyleContextChangeHandler extends AbstractChangeHandler {
StyleContextChangeHandler(DefaultStyledDocument d){
super(d);
}
void fireStateChanged(DefaultStyledDocument d,ChangeEvent e){
d.updateStylesListeningTo();
}
}
/** 
 * When run this creates a change event for the complete document
 * and fires it.
 */
class ChangeUpdateRunnable implements Runnable {
boolean isPending=false;
public void run(){
synchronized (this) {
isPending=false;
}
try {
writeLock();
DefaultDocumentEvent dde=new DefaultDocumentEvent(0,getLength(),DocumentEvent.EventType.CHANGE);
dde.end();
fireChangedUpdate(dde);
}
  finally {
writeUnlock();
}
}
}
}
