package javax.swing;
import java.util.*;
import java.io.Serializable;
/** 
 * The default model for combo boxes.
 * @param<E>
 *  the type of the elements of this model
 * @author Arnaud Weber
 * @author Tom Santos
 */
public class DefaultComboBoxModel<E> extends AbstractListModel<E> implements MutableComboBoxModel<E>, Serializable {
  Vector<E> objects;
  Object selectedObject;
  /** 
 * Constructs an empty DefaultComboBoxModel object.
 */
  public DefaultComboBoxModel(){
    objects=new Vector<E>();
  }
  /** 
 * Constructs a DefaultComboBoxModel object initialized with
 * an array of objects.
 * @param items  an array of Object objects
 */
  public DefaultComboBoxModel(  final E items[]){
    objects=new Vector<E>();
    objects.ensureCapacity(items.length);
    int i, c;
    for (i=0, c=items.length; i < c; i++)     objects.addElement(items[i]);
    if (getSize() > 0) {
      selectedObject=getElementAt(0);
    }
  }
  /** 
 * Constructs a DefaultComboBoxModel object initialized with
 * a vector.
 * @param v  a Vector object ...
 */
  public DefaultComboBoxModel(  Vector<E> v){
    objects=v;
    if (getSize() > 0) {
      selectedObject=getElementAt(0);
    }
  }
  /** 
 * Set the value of the selected item. The selected item may be null.
 * <p>
 * @param anObject The combo box value or null for no selection.
 */
  public void setSelectedItem(  Object anObject){
    if ((selectedObject != null && !selectedObject.equals(anObject)) || selectedObject == null && anObject != null) {
      selectedObject=anObject;
      fireContentsChanged(this,-1,-1);
    }
  }
  public Object getSelectedItem(){
    return selectedObject;
  }
  public int getSize(){
    return objects.size();
  }
  public E getElementAt(  int index){
    if (index >= 0 && index < objects.size())     return objects.elementAt(index);
 else     return null;
  }
  /** 
 * Returns the index-position of the specified object in the list.
 * @param anObject
 * @return an int representing the index position, where 0 is
 * the first position
 */
  public int getIndexOf(  Object anObject){
    return objects.indexOf(anObject);
  }
  public void addElement(  E anObject){
    objects.addElement(anObject);
    fireIntervalAdded(this,objects.size() - 1,objects.size() - 1);
    if (objects.size() == 1 && selectedObject == null && anObject != null) {
      setSelectedItem(anObject);
    }
  }
  public void insertElementAt(  E anObject,  int index){
    objects.insertElementAt(anObject,index);
    fireIntervalAdded(this,index,index);
  }
  public void removeElementAt(  int index){
    if (getElementAt(index) == selectedObject) {
      if (index == 0) {
        setSelectedItem(getSize() == 1 ? null : getElementAt(index + 1));
      }
 else {
        setSelectedItem(getElementAt(index - 1));
      }
    }
    objects.removeElementAt(index);
    fireIntervalRemoved(this,index,index);
  }
  public void removeElement(  Object anObject){
    int index=objects.indexOf(anObject);
    if (index != -1) {
      removeElementAt(index);
    }
  }
  /** 
 * Empties the list.
 */
  public void removeAllElements(){
    if (objects.size() > 0) {
      int firstIndex=0;
      int lastIndex=objects.size() - 1;
      objects.removeAllElements();
      selectedObject=null;
      fireIntervalRemoved(this,firstIndex,lastIndex);
    }
 else {
      selectedObject=null;
    }
  }
}