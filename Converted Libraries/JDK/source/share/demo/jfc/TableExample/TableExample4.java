import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.Color;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager.LookAndFeelInfo;
/** 
 * Another JTable example, showing how column attributes can be refined
 * even when columns have been created automatically. Here we create some
 * specialised renderers and editors as well as changing widths and colors
 * for some of the columns in the SwingSet demo table.
 * @author Philip Milne
 */
public class TableExample4 {
  public TableExample4(){
    JFrame frame=new JFrame("Table");
    frame.addWindowListener(new WindowAdapter(){
      @Override public void windowClosing(      WindowEvent e){
        System.exit(0);
      }
    }
);
    final String[] names={"First Name","Last Name","Favorite Color","Favorite Number","Vegetarian"};
    final Object[][] data={{"Mark","Andrews","Red",new Integer(2),Boolean.TRUE},{"Tom","Ball","Blue",new Integer(99),Boolean.FALSE},{"Alan","Chung","Green",new Integer(838),Boolean.FALSE},{"Jeff","Dinkins","Turquois",new Integer(8),Boolean.TRUE},{"Amy","Fowler","Yellow",new Integer(3),Boolean.FALSE},{"Brian","Gerhold","Green",new Integer(0),Boolean.FALSE},{"James","Gosling","Pink",new Integer(21),Boolean.FALSE},{"David","Karlton","Red",new Integer(1),Boolean.FALSE},{"Dave","Kloba","Yellow",new Integer(14),Boolean.FALSE},{"Peter","Korn","Purple",new Integer(12),Boolean.FALSE},{"Phil","Milne","Purple",new Integer(3),Boolean.FALSE},{"Dave","Moore","Green",new Integer(88),Boolean.FALSE},{"Hans","Muller","Maroon",new Integer(5),Boolean.FALSE},{"Rick","Levenson","Blue",new Integer(2),Boolean.FALSE},{"Tim","Prinzing","Blue",new Integer(22),Boolean.FALSE},{"Chester","Rose","Black",new Integer(0),Boolean.FALSE},{"Ray","Ryan","Gray",new Integer(77),Boolean.FALSE},{"Georges","Saab","Red",new Integer(4),Boolean.FALSE},{"Willie","Walker","Phthalo Blue",new Integer(4),Boolean.FALSE},{"Kathy","Walrath","Blue",new Integer(8),Boolean.FALSE},{"Arnaud","Weber","Green",new Integer(44),Boolean.FALSE}};
    @SuppressWarnings("serial") TableModel dataModel=new AbstractTableModel(){
      public int getColumnCount(){
        return names.length;
      }
      public int getRowCount(){
        return data.length;
      }
      public Object getValueAt(      int row,      int col){
        return data[row][col];
      }
      @Override public String getColumnName(      int column){
        return names[column];
      }
      @Override public Class getColumnClass(      int c){
        return getValueAt(0,c).getClass();
      }
      @Override public boolean isCellEditable(      int row,      int col){
        return true;
      }
      @Override public void setValueAt(      Object aValue,      int row,      int column){
        System.out.println("Setting value to: " + aValue);
        data[row][column]=aValue;
      }
    }
;
    JTable tableView=new JTable(dataModel);
    tableView.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    JComboBox comboBox=new JComboBox();
    comboBox.addItem("Red");
    comboBox.addItem("Orange");
    comboBox.addItem("Yellow");
    comboBox.addItem("Green");
    comboBox.addItem("Blue");
    comboBox.addItem("Indigo");
    comboBox.addItem("Violet");
    TableColumn colorColumn=tableView.getColumn("Favorite Color");
    colorColumn.setCellEditor(new DefaultCellEditor(comboBox));
    DefaultTableCellRenderer colorColumnRenderer=new DefaultTableCellRenderer();
    colorColumnRenderer.setBackground(Color.pink);
    colorColumnRenderer.setToolTipText("Click for combo box");
    colorColumn.setCellRenderer(colorColumnRenderer);
    TableCellRenderer headerRenderer=colorColumn.getHeaderRenderer();
    if (headerRenderer instanceof DefaultTableCellRenderer) {
      ((DefaultTableCellRenderer)headerRenderer).setToolTipText("Hi Mom!");
    }
    TableColumn vegetarianColumn=tableView.getColumn("Vegetarian");
    vegetarianColumn.setPreferredWidth(100);
    TableColumn numbersColumn=tableView.getColumn("Favorite Number");
    @SuppressWarnings("serial") DefaultTableCellRenderer numberColumnRenderer=new DefaultTableCellRenderer(){
      @Override public void setValue(      Object value){
        int cellValue=(value instanceof Number) ? ((Number)value).intValue() : 0;
        setForeground((cellValue > 30) ? Color.black : Color.red);
        setText((value == null) ? "" : value.toString());
      }
    }
;
    numberColumnRenderer.setHorizontalAlignment(JLabel.RIGHT);
    numbersColumn.setCellRenderer(numberColumnRenderer);
    numbersColumn.setPreferredWidth(110);
    JScrollPane scrollpane=new JScrollPane(tableView);
    scrollpane.setBorder(new BevelBorder(BevelBorder.LOWERED));
    scrollpane.setPreferredSize(new Dimension(430,200));
    frame.getContentPane().add(scrollpane);
    frame.pack();
    frame.setVisible(true);
  }
  public static void main(  String[] args){
    try {
      for (      LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
        if ("Nimbus".equals(info.getName())) {
          UIManager.setLookAndFeel(info.getClassName());
          break;
        }
      }
    }
 catch (    Exception ex) {
      Logger.getLogger(TableExample4.class.getName()).log(Level.SEVERE,"Failed to apply Nimbus look and feel",ex);
    }
    new TableExample4();
  }
}
