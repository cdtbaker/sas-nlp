package sun.tools.java;
import java.io.IOException;
import java.io.DataInputStream;
import java.io.DataOutputStream;
/** 
 * This class is used to represent an attribute from a binary class.
 * This class should go away once arrays are objects.
 * WARNING: The contents of this source file are not part of any
 * supported API.  Code that depends on them does so at its own risk:
 * they are subject to change or removal without notice.
 */
public final class BinaryAttribute implements Constants {
  Identifier name;
  byte data[];
  BinaryAttribute next;
  /** 
 * Constructor
 */
  BinaryAttribute(  Identifier name,  byte data[],  BinaryAttribute next){
    this.name=name;
    this.data=data;
    this.next=next;
  }
  /** 
 * Load a list of attributes
 */
  public static BinaryAttribute load(  DataInputStream in,  BinaryConstantPool cpool,  int mask) throws IOException {
    BinaryAttribute atts=null;
    int natt=in.readUnsignedShort();
    for (int i=0; i < natt; i++) {
      Identifier id=cpool.getIdentifier(in.readUnsignedShort());
      int len=in.readInt();
      if (id.equals(idCode) && ((mask & ATT_CODE) == 0)) {
        in.skipBytes(len);
      }
 else {
        byte data[]=new byte[len];
        in.readFully(data);
        atts=new BinaryAttribute(id,data,atts);
      }
    }
    return atts;
  }
  static void write(  BinaryAttribute attributes,  DataOutputStream out,  BinaryConstantPool cpool,  Environment env) throws IOException {
    int attributeCount=0;
    for (BinaryAttribute att=attributes; att != null; att=att.next)     attributeCount++;
    out.writeShort(attributeCount);
    for (BinaryAttribute att=attributes; att != null; att=att.next) {
      Identifier name=att.name;
      byte data[]=att.data;
      out.writeShort(cpool.indexString(name.toString(),env));
      out.writeInt(data.length);
      out.write(data,0,data.length);
    }
  }
  /** 
 * Accessors
 */
  public Identifier getName(){
    return name;
  }
  public byte getData()[]{
    return data;
  }
  public BinaryAttribute getNextAttribute(){
    return next;
  }
}
