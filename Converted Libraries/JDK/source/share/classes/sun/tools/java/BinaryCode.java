package sun.tools.java;
import java.io.*;
/** 
 * WARNING: The contents of this source file are not part of any
 * supported API.  Code that depends on them does so at its own risk:
 * they are subject to change or removal without notice.
 */
public class BinaryCode implements Constants {
  int maxStack;
  int maxLocals;
  BinaryExceptionHandler exceptionHandlers[];
  BinaryAttribute atts;
  BinaryConstantPool cpool;
  byte code[];
  /** 
 * Construct the binary code from the code attribute
 */
  public BinaryCode(  byte data[],  BinaryConstantPool cpool,  Environment env){
    DataInputStream in=new DataInputStream(new ByteArrayInputStream(data));
    try {
      this.cpool=cpool;
      this.maxStack=in.readUnsignedShort();
      this.maxLocals=in.readUnsignedShort();
      int code_length=in.readInt();
      this.code=new byte[code_length];
      in.read(this.code);
      int exception_count=in.readUnsignedShort();
      this.exceptionHandlers=new BinaryExceptionHandler[exception_count];
      for (int i=0; i < exception_count; i++) {
        int start=in.readUnsignedShort();
        int end=in.readUnsignedShort();
        int handler=in.readUnsignedShort();
        ClassDeclaration xclass=cpool.getDeclaration(env,in.readUnsignedShort());
        this.exceptionHandlers[i]=new BinaryExceptionHandler(start,end,handler,xclass);
      }
      this.atts=BinaryAttribute.load(in,cpool,~0);
      if (in.available() != 0) {
        System.err.println("Should have exhausted input stream!");
      }
    }
 catch (    IOException e) {
      throw new CompilerError(e);
    }
  }
  /** 
 * Accessors
 */
  public BinaryExceptionHandler getExceptionHandlers()[]{
    return exceptionHandlers;
  }
  public byte getCode()[]{
    return code;
  }
  public int getMaxStack(){
    return maxStack;
  }
  public int getMaxLocals(){
    return maxLocals;
  }
  public BinaryAttribute getAttributes(){
    return atts;
  }
  /** 
 * Load a binary class
 */
  public static BinaryCode load(  BinaryMember bf,  BinaryConstantPool cpool,  Environment env){
    byte code[]=bf.getAttribute(idCode);
    return (code != null) ? new BinaryCode(code,cpool,env) : null;
  }
}
