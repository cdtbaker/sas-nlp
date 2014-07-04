import java.util.EventObject;
import java.util.List;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
/** 
 * The OldJTable is an unsupported class containing some methods that were
 * deleted from the JTable between releases 0.6 and 0.7
 */
@SuppressWarnings("serial") public class OldJTable extends JTable {
  public int getColumnIndex(  Object identifier){
    return getColumnModel().getColumnIndex(identifier);
  }
  public TableColumn addColumn(  Object columnIdentifier,  int width){
    return addColumn(columnIdentifier,width,null,null,null);
  }
  public TableColumn addColumn(  Object columnIdentifier,  List columnData){
    return addColumn(columnIdentifier,-1,null,null,columnData);
  }
  public TableColumn addColumn(  Object columnIdentifier,  int width,  TableCellRenderer renderer,  TableCellEditor editor){
    return addColumn(columnIdentifier,width,renderer,editor,null);
  }
  public TableColumn addColumn(  Object columnIdentifier,  int width,  TableCellRenderer renderer,  TableCellEditor editor,  List columnData){
    checkDefaultTableModel();
    DefaultTableModel m=(DefaultTableModel)getModel();
    m.addColumn(columnIdentifier,columnData.toArray());
    TableColumn newColumn=new TableColumn(m.getColumnCount() - 1,width,renderer,editor);
    super.addColumn(newColumn);
    return newColumn;
  }
  public void removeColumn(  Object columnIdentifier){
    super.removeColumn(getColumn(columnIdentifier));
  }
  public void addRow(  Object[] rowData){
    checkDefaultTableModel();
    ((DefaultTableModel)getModel()).addRow(rowData);
  }
  public void addRow(  List rowData){
    checkDefaultTableModel();
    ((DefaultTableModel)getModel()).addRow(rowData.toArray());
  }
  public void removeRow(  int rowIndex){
    checkDefaultTableModel();
    ((DefaultTableModel)getModel()).removeRow(rowIndex);
  }
  public void moveRow(  int startIndex,  int endIndex,  int toIndex){
    checkDefaultTableModel();
    ((DefaultTableModel)getModel()).moveRow(startIndex,endIndex,toIndex);
  }
  public void insertRow(  int rowIndex,  Object[] rowData){
    checkDefaultTableModel();
    ((DefaultTableModel)getModel()).insertRow(rowIndex,rowData);
  }
  public void insertRow(  int rowIndex,  List rowData){
    checkDefaultTableModel();
    ((DefaultTableModel)getModel()).insertRow(rowIndex,rowData.toArray());
  }
  public void setNumRows(  int newSize){
    checkDefaultTableModel();
    ((DefaultTableModel)getModel()).setNumRows(newSize);
  }
  public void setDataVector(  Object[][] newData,  List columnIds){
    checkDefaultTableModel();
    ((DefaultTableModel)getModel()).setDataVector(newData,columnIds.toArray());
  }
  public void setDataVector(  Object[][] newData,  Object[] columnIds){
    checkDefaultTableModel();
    ((DefaultTableModel)getModel()).setDataVector(newData,columnIds);
  }
  protected void checkDefaultTableModel(){
    if (!(dataModel instanceof DefaultTableModel))     throw new InternalError("In order to use this method, the data model must be an instance of DefaultTableModel.");
  }
  public Object getValueAt(  Object columnIdentifier,  int rowIndex){
    return super.getValueAt(rowIndex,getColumnIndex(columnIdentifier));
  }
  public boolean isCellEditable(  Object columnIdentifier,  int rowIndex){
    return super.isCellEditable(rowIndex,getColumnIndex(columnIdentifier));
  }
  public void setValueAt(  Object aValue,  Object columnIdentifier,  int rowIndex){
    super.setValueAt(aValue,rowIndex,getColumnIndex(columnIdentifier));
  }
  public boolean editColumnRow(  Object identifier,  int row){
    return super.editCellAt(row,getColumnIndex(identifier));
  }
  public void moveColumn(  Object columnIdentifier,  Object targetColumnIdentifier){
    moveColumn(getColumnIndex(columnIdentifier),getColumnIndex(targetColumnIdentifier));
  }
  public boolean isColumnSelected(  Object identifier){
    return isColumnSelected(getColumnIndex(identifier));
  }
  public TableColumn addColumn(  int modelColumn,  int width){
    return addColumn(modelColumn,width,null,null);
  }
  public TableColumn addColumn(  int modelColumn){
    return addColumn(modelColumn,75,null,null);
  }
  /** 
 * Creates a new column with <I>modelColumn</I>, <I>width</I>,
 * <I>renderer</I>, and <I>editor</I> and adds it to the end of
 * the JTable's array of columns. This method also retrieves the
 * name of the column using the model's <I>getColumnName(modelColumn)</I>
 * method, and sets the both the header value and the identifier
 * for this TableColumn accordingly.
 * <p>
 * The <I>modelColumn</I> is the index of the column in the model which
 * will supply the data for this column in the table. This, like the
 * <I>columnIdentifier</I> in previous releases, does not change as the
 * columns are moved in the view.
 * <p>
 * For the rest of the JTable API, and all of its associated classes,
 * columns are referred to in the co-ordinate system of the view, the
 * index of the column in the model is kept inside the TableColumn
 * and is used only to retrieve the information from the appropraite
 * column in the model.
 * <p>
 * @param modelColumn     The index of the column in the model
 * @param width           The new column's width.  Or -1 to use
 * the default width
 * @param renderer        The renderer used with the new column.
 * Or null to use the default renderer.
 * @param editor          The editor used with the new column.
 * Or null to use the default editor.
 */
  public TableColumn addColumn(  int modelColumn,  int width,  TableCellRenderer renderer,  TableCellEditor editor){
    TableColumn newColumn=new TableColumn(modelColumn,width,renderer,editor);
    addColumn(newColumn);
    return newColumn;
  }
  public boolean editColumnRow(  int columnIndex,  int rowIndex){
    return super.editCellAt(rowIndex,columnIndex);
  }
  public boolean editColumnRow(  int columnIndex,  int rowIndex,  EventObject e){
    return super.editCellAt(rowIndex,columnIndex,e);
  }
}
