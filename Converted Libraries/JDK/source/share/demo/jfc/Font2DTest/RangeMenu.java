import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.*;
import java.util.*;
import java.util.regex.*;
/** 
 * RangeMenu.java
 * @author Shinsuke Fukuda
 * @author Ankit Patel [Conversion to Swing - 01/07/30]
 */
public final class RangeMenu extends JComboBox implements ActionListener {
  private static final int[][] UNICODE_RANGES=getUnicodeRanges();
  private static final String[] UNICODE_RANGE_NAMES=getUnicodeRangeNames();
  private boolean useCustomRange=false;
  private int[] customRange={0x0000,0x007f};
  private final JDialog customRangeDialog;
  private final JTextField customRangeStart=new JTextField("0000",4);
  private final JTextField customRangeEnd=new JTextField("007F",4);
  private final int CUSTOM_RANGE_INDEX=UNICODE_RANGE_NAMES.length - 1;
  private final Font2DTest parent;
  public static final int SURROGATES_AREA_INDEX=91;
  public RangeMenu(  Font2DTest demo,  JFrame f){
    super();
    parent=demo;
    for (int i=0; i < UNICODE_RANGE_NAMES.length; i++)     addItem(UNICODE_RANGE_NAMES[i]);
    setSelectedIndex(0);
    addActionListener(this);
    customRangeDialog=new JDialog(f,"Custom Unicode Range",true);
    customRangeDialog.setResizable(false);
    JPanel dialogTop=new JPanel();
    JPanel dialogBottom=new JPanel();
    JButton okButton=new JButton("OK");
    JLabel from=new JLabel("From:");
    JLabel to=new JLabel("To:");
    Font labelFont=new Font("dialog",Font.BOLD,12);
    from.setFont(labelFont);
    to.setFont(labelFont);
    okButton.setFont(labelFont);
    dialogTop.add(from);
    dialogTop.add(customRangeStart);
    dialogTop.add(to);
    dialogTop.add(customRangeEnd);
    dialogBottom.add(okButton);
    okButton.addActionListener(this);
    customRangeDialog.getContentPane().setLayout(new BorderLayout());
    customRangeDialog.getContentPane().add("North",dialogTop);
    customRangeDialog.getContentPane().add("South",dialogBottom);
    customRangeDialog.pack();
  }
  public int[] getSelectedRange(){
    if (useCustomRange) {
      int startIndex, endIndex;
      String startText, endText;
      String empty="";
      try {
        startText=customRangeStart.getText().trim();
        endText=customRangeEnd.getText().trim();
        if (startText.equals(empty) && !endText.equals(empty)) {
          endIndex=Integer.parseInt(endText,16);
          startIndex=endIndex - 7 * 25;
        }
 else         if (!startText.equals(empty) && endText.equals(empty)) {
          startIndex=Integer.parseInt(startText,16);
          endIndex=startIndex + 7 * 25;
        }
 else {
          startIndex=Integer.parseInt(customRangeStart.getText(),16);
          endIndex=Integer.parseInt(customRangeEnd.getText(),16);
        }
      }
 catch (      Exception e) {
        customRangeStart.setText(Integer.toString(customRange[0],16));
        customRangeEnd.setText(Integer.toString(customRange[1],16));
        return customRange;
      }
      if (startIndex < 0)       startIndex=0;
      if (endIndex > 0xffff)       endIndex=0xffff;
      if (startIndex > endIndex)       startIndex=endIndex;
      customRange[0]=startIndex;
      customRange[1]=endIndex;
      return customRange;
    }
 else     return UNICODE_RANGES[getSelectedIndex()];
  }
  public void setSelectedRange(  String name,  int start,  int end){
    setSelectedItem(name);
    customRange[0]=start;
    customRange[1]=end;
    parent.fireRangeChanged();
  }
  public void actionPerformed(  ActionEvent e){
    Object source=e.getSource();
    if (source instanceof JComboBox) {
      String rangeName=(String)((JComboBox)source).getSelectedItem();
      if (rangeName.equals("Custom...")) {
        useCustomRange=true;
        customRangeDialog.setLocationRelativeTo(parent);
        customRangeDialog.show();
      }
 else {
        useCustomRange=false;
      }
      parent.fireRangeChanged();
    }
 else     if (source instanceof JButton) {
      customRangeDialog.hide();
    }
  }
  private static int[][] getUnicodeRanges(){
    List<Integer> ranges=new ArrayList<>();
    ranges.add(0);
    Character.UnicodeBlock currentBlock=Character.UnicodeBlock.of(0);
    for (int cp=0x000001; cp < 0x110000; cp++) {
      Character.UnicodeBlock ub=Character.UnicodeBlock.of(cp);
      if (currentBlock == null) {
        if (ub != null) {
          ranges.add(cp);
          currentBlock=ub;
        }
      }
 else {
        if (ub == null) {
          ranges.add(cp - 1);
          currentBlock=null;
        }
 else         if (cp == 0x10ffff) {
          ranges.add(cp);
        }
 else         if (!ub.equals(currentBlock)) {
          ranges.add(cp - 1);
          ranges.add(cp);
          currentBlock=ub;
        }
      }
    }
    ranges.add(0x00);
    ranges.add(0x7f);
    int[][] returnval=new int[ranges.size() / 2][2];
    for (int i=0; i < ranges.size() / 2; i++) {
      returnval[i][0]=ranges.get(2 * i);
      returnval[i][1]=ranges.get(2 * i + 1);
    }
    return returnval;
  }
  private static String[] getUnicodeRangeNames(){
    String[] names=new String[UNICODE_RANGES.length];
    for (int i=0; i < names.length; i++) {
      names[i]=titleCase(Character.UnicodeBlock.of(UNICODE_RANGES[i][0]).toString());
    }
    names[names.length - 1]="Custom...";
    return names;
  }
  private static String titleCase(  String str){
    str=str.replaceAll("_"," ");
    Pattern p=Pattern.compile("(^|\\W)([a-z])");
    Matcher m=p.matcher(str.toLowerCase(Locale.ROOT));
    StringBuffer sb=new StringBuffer();
    while (m.find()) {
      m.appendReplacement(sb,m.group(1) + m.group(2).toUpperCase(Locale.ROOT));
    }
    m.appendTail(sb);
    return sb.toString().replace("Cjk","CJK").replace("Nko","NKo");
  }
}
