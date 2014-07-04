package javax.swing;
import java.io.*;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.Dialog;
import java.awt.Window;
import java.awt.Component;
import java.awt.Container;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.awt.event.WindowListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.IllegalComponentStateException;
import java.awt.Point;
import java.awt.Rectangle;
import java.text.*;
import java.util.Locale;
import javax.accessibility.*;
import javax.swing.event.*;
import javax.swing.text.*;
/** 
 * A class to monitor the progress of some operation. If it looks
 * like the operation will take a while, a progress dialog will be popped up.
 * When the ProgressMonitor is created it is given a numeric range and a
 * descriptive string. As the operation progresses, call the setProgress method
 * to indicate how far along the [min,max] range the operation is.
 * Initially, there is no ProgressDialog. After the first millisToDecideToPopup
 * milliseconds (default 500) the progress monitor will predict how long
 * the operation will take.  If it is longer than millisToPopup (default 2000,
 * 2 seconds) a ProgressDialog will be popped up.
 * <p>
 * From time to time, when the Dialog box is visible, the progress bar will
 * be updated when setProgress is called.  setProgress won't always update
 * the progress bar, it will only be done if the amount of progress is
 * visibly significant.
 * <p>
 * For further documentation and examples see
 * <a
 * href="http://java.sun.com/docs/books/tutorial/uiswing/components/progress.html">How to Monitor Progress</a>,
 * a section in <em>The Java Tutorial.</em>
 * @see ProgressMonitorInputStream
 * @author James Gosling
 * @author Lynn Monsanto (accessibility)
 */
public class ProgressMonitor implements Accessible {
  private ProgressMonitor root;
  private JDialog dialog;
  private JOptionPane pane;
  private JProgressBar myBar;
  private JLabel noteLabel;
  private Component parentComponent;
  private String note;
  private Object[] cancelOption=null;
  private Object message;
  private long T0;
  private int millisToDecideToPopup=500;
  private int millisToPopup=2000;
  private int min;
  private int max;
  /** 
 * Constructs a graphic object that shows progress, typically by filling
 * in a rectangular bar as the process nears completion.
 * @param parentComponent the parent component for the dialog box
 * @param message a descriptive message that will be shown
 * to the user to indicate what operation is being monitored.
 * This does not change as the operation progresses.
 * See the message parameters to methods in{@link JOptionPane#message}for the range of values.
 * @param note a short note describing the state of the
 * operation.  As the operation progresses, you can call
 * setNote to change the note displayed.  This is used,
 * for example, in operations that iterate through a
 * list of files to show the name of the file being processes.
 * If note is initially null, there will be no note line
 * in the dialog box and setNote will be ineffective
 * @param min the lower bound of the range
 * @param max the upper bound of the range
 * @see JDialog
 * @see JOptionPane
 */
  public ProgressMonitor(  Component parentComponent,  Object message,  String note,  int min,  int max){
    this(parentComponent,message,note,min,max,null);
  }
  private ProgressMonitor(  Component parentComponent,  Object message,  String note,  int min,  int max,  ProgressMonitor group){
    this.min=min;
    this.max=max;
    this.parentComponent=parentComponent;
    cancelOption=new Object[1];
    cancelOption[0]=UIManager.getString("OptionPane.cancelButtonText");
    this.message=message;
    this.note=note;
    if (group != null) {
      root=(group.root != null) ? group.root : group;
      T0=root.T0;
      dialog=root.dialog;
    }
 else {
      T0=System.currentTimeMillis();
    }
  }
private class ProgressOptionPane extends JOptionPane {
    ProgressOptionPane(    Object messageList){
      super(messageList,JOptionPane.INFORMATION_MESSAGE,JOptionPane.DEFAULT_OPTION,null,ProgressMonitor.this.cancelOption,null);
    }
    public int getMaxCharactersPerLineCount(){
      return 60;
    }
    public JDialog createDialog(    Component parentComponent,    String title){
      final JDialog dialog;
      Window window=JOptionPane.getWindowForComponent(parentComponent);
      if (window instanceof Frame) {
        dialog=new JDialog((Frame)window,title,false);
      }
 else {
        dialog=new JDialog((Dialog)window,title,false);
      }
      if (window instanceof SwingUtilities.SharedOwnerFrame) {
        WindowListener ownerShutdownListener=SwingUtilities.getSharedOwnerFrameShutdownListener();
        dialog.addWindowListener(ownerShutdownListener);
      }
      Container contentPane=dialog.getContentPane();
      contentPane.setLayout(new BorderLayout());
      contentPane.add(this,BorderLayout.CENTER);
      dialog.pack();
      dialog.setLocationRelativeTo(parentComponent);
      dialog.addWindowListener(new WindowAdapter(){
        boolean gotFocus=false;
        public void windowClosing(        WindowEvent we){
          setValue(cancelOption[0]);
        }
        public void windowActivated(        WindowEvent we){
          if (!gotFocus) {
            selectInitialValue();
            gotFocus=true;
          }
        }
      }
);
      addPropertyChangeListener(new PropertyChangeListener(){
        public void propertyChange(        PropertyChangeEvent event){
          if (dialog.isVisible() && event.getSource() == ProgressOptionPane.this && (event.getPropertyName().equals(VALUE_PROPERTY) || event.getPropertyName().equals(INPUT_VALUE_PROPERTY))) {
            dialog.setVisible(false);
            dialog.dispose();
          }
        }
      }
);
      return dialog;
    }
    /** 
 * Gets the AccessibleContext for the ProgressOptionPane
 * @return the AccessibleContext for the ProgressOptionPane
 * @since 1.5
 */
    public AccessibleContext getAccessibleContext(){
      return ProgressMonitor.this.getAccessibleContext();
    }
    private AccessibleContext getAccessibleJOptionPane(){
      return super.getAccessibleContext();
    }
  }
  /** 
 * Indicate the progress of the operation being monitored.
 * If the specified value is >= the maximum, the progress
 * monitor is closed.
 * @param nv an int specifying the current value, between the
 * maximum and minimum specified for this component
 * @see #setMinimum
 * @see #setMaximum
 * @see #close
 */
  public void setProgress(  int nv){
    if (nv >= max) {
      close();
    }
 else {
      if (myBar != null) {
        myBar.setValue(nv);
      }
 else {
        long T=System.currentTimeMillis();
        long dT=(int)(T - T0);
        if (dT >= millisToDecideToPopup) {
          int predictedCompletionTime;
          if (nv > min) {
            predictedCompletionTime=(int)(dT * (max - min) / (nv - min));
          }
 else {
            predictedCompletionTime=millisToPopup;
          }
          if (predictedCompletionTime >= millisToPopup) {
            myBar=new JProgressBar();
            myBar.setMinimum(min);
            myBar.setMaximum(max);
            myBar.setValue(nv);
            if (note != null)             noteLabel=new JLabel(note);
            pane=new ProgressOptionPane(new Object[]{message,noteLabel,myBar});
            dialog=pane.createDialog(parentComponent,UIManager.getString("ProgressMonitor.progressText"));
            dialog.show();
          }
        }
      }
    }
  }
  /** 
 * Indicate that the operation is complete.  This happens automatically
 * when the value set by setProgress is >= max, but it may be called
 * earlier if the operation ends early.
 */
  public void close(){
    if (dialog != null) {
      dialog.setVisible(false);
      dialog.dispose();
      dialog=null;
      pane=null;
      myBar=null;
    }
  }
  /** 
 * Returns the minimum value -- the lower end of the progress value.
 * @return an int representing the minimum value
 * @see #setMinimum
 */
  public int getMinimum(){
    return min;
  }
  /** 
 * Specifies the minimum value.
 * @param m  an int specifying the minimum value
 * @see #getMinimum
 */
  public void setMinimum(  int m){
    if (myBar != null) {
      myBar.setMinimum(m);
    }
    min=m;
  }
  /** 
 * Returns the maximum value -- the higher end of the progress value.
 * @return an int representing the maximum value
 * @see #setMaximum
 */
  public int getMaximum(){
    return max;
  }
  /** 
 * Specifies the maximum value.
 * @param m  an int specifying the maximum value
 * @see #getMaximum
 */
  public void setMaximum(  int m){
    if (myBar != null) {
      myBar.setMaximum(m);
    }
    max=m;
  }
  /** 
 * Returns true if the user hits the Cancel button in the progress dialog.
 */
  public boolean isCanceled(){
    if (pane == null)     return false;
    Object v=pane.getValue();
    return ((v != null) && (cancelOption.length == 1) && (v.equals(cancelOption[0])));
  }
  /** 
 * Specifies the amount of time to wait before deciding whether or
 * not to popup a progress monitor.
 * @param millisToDecideToPopup  an int specifying the time to wait,
 * in milliseconds
 * @see #getMillisToDecideToPopup
 */
  public void setMillisToDecideToPopup(  int millisToDecideToPopup){
    this.millisToDecideToPopup=millisToDecideToPopup;
  }
  /** 
 * Returns the amount of time this object waits before deciding whether
 * or not to popup a progress monitor.
 * @see #setMillisToDecideToPopup
 */
  public int getMillisToDecideToPopup(){
    return millisToDecideToPopup;
  }
  /** 
 * Specifies the amount of time it will take for the popup to appear.
 * (If the predicted time remaining is less than this time, the popup
 * won't be displayed.)
 * @param millisToPopup  an int specifying the time in milliseconds
 * @see #getMillisToPopup
 */
  public void setMillisToPopup(  int millisToPopup){
    this.millisToPopup=millisToPopup;
  }
  /** 
 * Returns the amount of time it will take for the popup to appear.
 * @see #setMillisToPopup
 */
  public int getMillisToPopup(){
    return millisToPopup;
  }
  /** 
 * Specifies the additional note that is displayed along with the
 * progress message. Used, for example, to show which file the
 * is currently being copied during a multiple-file copy.
 * @param note  a String specifying the note to display
 * @see #getNote
 */
  public void setNote(  String note){
    this.note=note;
    if (noteLabel != null) {
      noteLabel.setText(note);
    }
  }
  /** 
 * Specifies the additional note that is displayed along with the
 * progress message.
 * @return a String specifying the note to display
 * @see #setNote
 */
  public String getNote(){
    return note;
  }
  /** 
 * The <code>AccessibleContext</code> for the <code>ProgressMonitor</code>
 * @since 1.5
 */
  protected AccessibleContext accessibleContext=null;
  private AccessibleContext accessibleJOptionPane=null;
  /** 
 * Gets the <code>AccessibleContext</code> for the
 * <code>ProgressMonitor</code>
 * @return the <code>AccessibleContext</code> for the
 * <code>ProgressMonitor</code>
 * @since 1.5
 */
  public AccessibleContext getAccessibleContext(){
    if (accessibleContext == null) {
      accessibleContext=new AccessibleProgressMonitor();
    }
    if (pane != null && accessibleJOptionPane == null) {
      if (accessibleContext instanceof AccessibleProgressMonitor) {
        ((AccessibleProgressMonitor)accessibleContext).optionPaneCreated();
      }
    }
    return accessibleContext;
  }
  /** 
 * <code>AccessibleProgressMonitor</code> implements accessibility
 * support for the <code>ProgressMonitor</code> class.
 * @since 1.5
 */
protected class AccessibleProgressMonitor extends AccessibleContext implements AccessibleText, ChangeListener, PropertyChangeListener {
    private Object oldModelValue;
    /** 
 * AccessibleProgressMonitor constructor
 */
    protected AccessibleProgressMonitor(){
    }
    private void optionPaneCreated(){
      accessibleJOptionPane=((ProgressOptionPane)pane).getAccessibleJOptionPane();
      if (myBar != null) {
        myBar.addChangeListener(this);
      }
      if (noteLabel != null) {
        noteLabel.addPropertyChangeListener(this);
      }
    }
    /** 
 * Invoked when the target of the listener has changed its state.
 * @param e  a <code>ChangeEvent</code> object. Must not be null.
 * @throws NullPointerException if the parameter is null.
 */
    public void stateChanged(    ChangeEvent e){
      if (e == null) {
        return;
      }
      if (myBar != null) {
        Object newModelValue=myBar.getValue();
        firePropertyChange(ACCESSIBLE_VALUE_PROPERTY,oldModelValue,newModelValue);
        oldModelValue=newModelValue;
      }
    }
    /** 
 * This method gets called when a bound property is changed.
 * @param e A <code>PropertyChangeEvent</code> object describing
 * the event source and the property that has changed. Must not be null.
 * @throws NullPointerException if the parameter is null.
 */
    public void propertyChange(    PropertyChangeEvent e){
      if (e.getSource() == noteLabel && e.getPropertyName() == "text") {
        firePropertyChange(ACCESSIBLE_TEXT_PROPERTY,null,0);
      }
    }
    /** 
 * Gets the accessibleName property of this object.  The accessibleName
 * property of an object is a localized String that designates the purpose
 * of the object.  For example, the accessibleName property of a label
 * or button might be the text of the label or button itself.  In the
 * case of an object that doesn't display its name, the accessibleName
 * should still be set.  For example, in the case of a text field used
 * to enter the name of a city, the accessibleName for the en_US locale
 * could be 'city.'
 * @return the localized name of the object; null if this
 * object does not have a name
 * @see #setAccessibleName
 */
    public String getAccessibleName(){
      if (accessibleName != null) {
        return accessibleName;
      }
 else       if (accessibleJOptionPane != null) {
        return accessibleJOptionPane.getAccessibleName();
      }
      return null;
    }
    /** 
 * Gets the accessibleDescription property of this object.  The
 * accessibleDescription property of this object is a short localized
 * phrase describing the purpose of the object.  For example, in the
 * case of a 'Cancel' button, the accessibleDescription could be
 * 'Ignore changes and close dialog box.'
 * @return the localized description of the object; null if
 * this object does not have a description
 * @see #setAccessibleDescription
 */
    public String getAccessibleDescription(){
      if (accessibleDescription != null) {
        return accessibleDescription;
      }
 else       if (accessibleJOptionPane != null) {
        return accessibleJOptionPane.getAccessibleDescription();
      }
      return null;
    }
    /** 
 * Gets the role of this object.  The role of the object is the generic
 * purpose or use of the class of this object.  For example, the role
 * of a push button is AccessibleRole.PUSH_BUTTON.  The roles in
 * AccessibleRole are provided so component developers can pick from
 * a set of predefined roles.  This enables assistive technologies to
 * provide a consistent interface to various tweaked subclasses of
 * components (e.g., use AccessibleRole.PUSH_BUTTON for all components
 * that act like a push button) as well as distinguish between sublasses
 * that behave differently (e.g., AccessibleRole.CHECK_BOX for check boxes
 * and AccessibleRole.RADIO_BUTTON for radio buttons).
 * <p>Note that the AccessibleRole class is also extensible, so
 * custom component developers can define their own AccessibleRole's
 * if the set of predefined roles is inadequate.
 * @return an instance of AccessibleRole describing the role of the object
 * @see AccessibleRole
 */
    public AccessibleRole getAccessibleRole(){
      return AccessibleRole.PROGRESS_MONITOR;
    }
    /** 
 * Gets the state set of this object.  The AccessibleStateSet of an object
 * is composed of a set of unique AccessibleStates.  A change in the
 * AccessibleStateSet of an object will cause a PropertyChangeEvent to
 * be fired for the ACCESSIBLE_STATE_PROPERTY property.
 * @return an instance of AccessibleStateSet containing the
 * current state set of the object
 * @see AccessibleStateSet
 * @see AccessibleState
 * @see #addPropertyChangeListener
 */
    public AccessibleStateSet getAccessibleStateSet(){
      if (accessibleJOptionPane != null) {
        return accessibleJOptionPane.getAccessibleStateSet();
      }
      return null;
    }
    /** 
 * Gets the Accessible parent of this object.
 * @return the Accessible parent of this object; null if this
 * object does not have an Accessible parent
 */
    public Accessible getAccessibleParent(){
      return dialog;
    }
    private AccessibleContext getParentAccessibleContext(){
      if (dialog != null) {
        return dialog.getAccessibleContext();
      }
      return null;
    }
    /** 
 * Gets the 0-based index of this object in its accessible parent.
 * @return the 0-based index of this object in its parent; -1 if this
 * object does not have an accessible parent.
 * @see #getAccessibleParent
 * @see #getAccessibleChildrenCount
 * @see #getAccessibleChild
 */
    public int getAccessibleIndexInParent(){
      if (accessibleJOptionPane != null) {
        return accessibleJOptionPane.getAccessibleIndexInParent();
      }
      return -1;
    }
    /** 
 * Returns the number of accessible children of the object.
 * @return the number of accessible children of the object.
 */
    public int getAccessibleChildrenCount(){
      AccessibleContext ac=getPanelAccessibleContext();
      if (ac != null) {
        return ac.getAccessibleChildrenCount();
      }
      return 0;
    }
    /** 
 * Returns the specified Accessible child of the object.  The Accessible
 * children of an Accessible object are zero-based, so the first child
 * of an Accessible child is at index 0, the second child is at index 1,
 * and so on.
 * @param i zero-based index of child
 * @return the Accessible child of the object
 * @see #getAccessibleChildrenCount
 */
    public Accessible getAccessibleChild(    int i){
      AccessibleContext ac=getPanelAccessibleContext();
      if (ac != null) {
        return ac.getAccessibleChild(i);
      }
      return null;
    }
    private AccessibleContext getPanelAccessibleContext(){
      if (myBar != null) {
        Component c=myBar.getParent();
        if (c instanceof Accessible) {
          return c.getAccessibleContext();
        }
      }
      return null;
    }
    /** 
 * Gets the locale of the component. If the component does not have a
 * locale, then the locale of its parent is returned.
 * @return this component's locale.  If this component does not have
 * a locale, the locale of its parent is returned.
 * @exception IllegalComponentStateExceptionIf the Component does not have its own locale and has not yet been
 * added to a containment hierarchy such that the locale can be
 * determined from the containing parent.
 */
    public Locale getLocale() throws IllegalComponentStateException {
      if (accessibleJOptionPane != null) {
        return accessibleJOptionPane.getLocale();
      }
      return null;
    }
    /** 
 * Gets the AccessibleComponent associated with this object that has a
 * graphical representation.
 * @return AccessibleComponent if supported by object; else return null
 * @see AccessibleComponent
 */
    public AccessibleComponent getAccessibleComponent(){
      if (accessibleJOptionPane != null) {
        return accessibleJOptionPane.getAccessibleComponent();
      }
      return null;
    }
    /** 
 * Gets the AccessibleValue associated with this object that supports a
 * Numerical value.
 * @return AccessibleValue if supported by object; else return null
 * @see AccessibleValue
 */
    public AccessibleValue getAccessibleValue(){
      if (myBar != null) {
        return myBar.getAccessibleContext().getAccessibleValue();
      }
      return null;
    }
    /** 
 * Gets the AccessibleText associated with this object presenting
 * text on the display.
 * @return AccessibleText if supported by object; else return null
 * @see AccessibleText
 */
    public AccessibleText getAccessibleText(){
      if (getNoteLabelAccessibleText() != null) {
        return this;
      }
      return null;
    }
    private AccessibleText getNoteLabelAccessibleText(){
      if (noteLabel != null) {
        return noteLabel.getAccessibleContext().getAccessibleText();
      }
      return null;
    }
    /** 
 * Given a point in local coordinates, return the zero-based index
 * of the character under that Point.  If the point is invalid,
 * this method returns -1.
 * @param p the Point in local coordinates
 * @return the zero-based index of the character under Point p; if
 * Point is invalid return -1.
 */
    public int getIndexAtPoint(    Point p){
      AccessibleText at=getNoteLabelAccessibleText();
      if (at != null && sameWindowAncestor(pane,noteLabel)) {
        Point noteLabelPoint=SwingUtilities.convertPoint(pane,p,noteLabel);
        if (noteLabelPoint != null) {
          return at.getIndexAtPoint(noteLabelPoint);
        }
      }
      return -1;
    }
    /** 
 * Determines the bounding box of the character at the given
 * index into the string.  The bounds are returned in local
 * coordinates.  If the index is invalid an empty rectangle is returned.
 * @param i the index into the String
 * @return the screen coordinates of the character's bounding box,
 * if index is invalid return an empty rectangle.
 */
    public Rectangle getCharacterBounds(    int i){
      AccessibleText at=getNoteLabelAccessibleText();
      if (at != null && sameWindowAncestor(pane,noteLabel)) {
        Rectangle noteLabelRect=at.getCharacterBounds(i);
        if (noteLabelRect != null) {
          return SwingUtilities.convertRectangle(noteLabel,noteLabelRect,pane);
        }
      }
      return null;
    }
    private boolean sameWindowAncestor(    Component src,    Component dest){
      if (src == null || dest == null) {
        return false;
      }
      return SwingUtilities.getWindowAncestor(src) == SwingUtilities.getWindowAncestor(dest);
    }
    /** 
 * Returns the number of characters (valid indicies)
 * @return the number of characters
 */
    public int getCharCount(){
      AccessibleText at=getNoteLabelAccessibleText();
      if (at != null) {
        return at.getCharCount();
      }
      return -1;
    }
    /** 
 * Returns the zero-based offset of the caret.
 * Note: That to the right of the caret will have the same index
 * value as the offset (the caret is between two characters).
 * @return the zero-based offset of the caret.
 */
    public int getCaretPosition(){
      AccessibleText at=getNoteLabelAccessibleText();
      if (at != null) {
        return at.getCaretPosition();
      }
      return -1;
    }
    /** 
 * Returns the String at a given index.
 * @param part the CHARACTER, WORD, or SENTENCE to retrieve
 * @param index an index within the text
 * @return the letter, word, or sentence
 */
    public String getAtIndex(    int part,    int index){
      AccessibleText at=getNoteLabelAccessibleText();
      if (at != null) {
        return at.getAtIndex(part,index);
      }
      return null;
    }
    /** 
 * Returns the String after a given index.
 * @param part the CHARACTER, WORD, or SENTENCE to retrieve
 * @param index an index within the text
 * @return the letter, word, or sentence
 */
    public String getAfterIndex(    int part,    int index){
      AccessibleText at=getNoteLabelAccessibleText();
      if (at != null) {
        return at.getAfterIndex(part,index);
      }
      return null;
    }
    /** 
 * Returns the String before a given index.
 * @param part the CHARACTER, WORD, or SENTENCE to retrieve
 * @param index an index within the text
 * @return the letter, word, or sentence
 */
    public String getBeforeIndex(    int part,    int index){
      AccessibleText at=getNoteLabelAccessibleText();
      if (at != null) {
        return at.getBeforeIndex(part,index);
      }
      return null;
    }
    /** 
 * Returns the AttributeSet for a given character at a given index
 * @param i the zero-based index into the text
 * @return the AttributeSet of the character
 */
    public AttributeSet getCharacterAttribute(    int i){
      AccessibleText at=getNoteLabelAccessibleText();
      if (at != null) {
        return at.getCharacterAttribute(i);
      }
      return null;
    }
    /** 
 * Returns the start offset within the selected text.
 * If there is no selection, but there is
 * a caret, the start and end offsets will be the same.
 * @return the index into the text of the start of the selection
 */
    public int getSelectionStart(){
      AccessibleText at=getNoteLabelAccessibleText();
      if (at != null) {
        return at.getSelectionStart();
      }
      return -1;
    }
    /** 
 * Returns the end offset within the selected text.
 * If there is no selection, but there is
 * a caret, the start and end offsets will be the same.
 * @return the index into teh text of the end of the selection
 */
    public int getSelectionEnd(){
      AccessibleText at=getNoteLabelAccessibleText();
      if (at != null) {
        return at.getSelectionEnd();
      }
      return -1;
    }
    /** 
 * Returns the portion of the text that is selected.
 * @return the String portion of the text that is selected
 */
    public String getSelectedText(){
      AccessibleText at=getNoteLabelAccessibleText();
      if (at != null) {
        return at.getSelectedText();
      }
      return null;
    }
  }
}
