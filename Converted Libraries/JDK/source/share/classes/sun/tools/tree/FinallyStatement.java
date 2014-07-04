package sun.tools.tree;
import sun.tools.java.*;
import sun.tools.asm.Assembler;
import sun.tools.asm.Label;
import sun.tools.asm.TryData;
import sun.tools.asm.CatchData;
import java.io.PrintStream;
import java.util.Hashtable;
import java.util.Enumeration;
/** 
 * WARNING: The contents of this source file are not part of any
 * supported API.  Code that depends on them does so at its own risk:
 * they are subject to change or removal without notice.
 */
public class FinallyStatement extends Statement {
  Statement body;
  Statement finalbody;
  boolean finallyCanFinish;
  boolean needReturnSlot;
  Statement init;
  LocalMember tryTemp;
  /** 
 * Constructor
 */
  public FinallyStatement(  long where,  Statement body,  Statement finalbody){
    super(FINALLY,where);
    this.body=body;
    this.finalbody=finalbody;
  }
  /** 
 * Check statement
 */
  Vset check(  Environment env,  Context ctx,  Vset vset,  Hashtable exp){
    vset=reach(env,vset);
    Hashtable newexp=new Hashtable();
    CheckContext newctx1=new CheckContext(ctx,this);
    Vset vset1=body.check(env,newctx1,vset.copy(),newexp).join(newctx1.vsBreak);
    CheckContext newctx2=new CheckContext(ctx,this);
    newctx2.vsContinue=null;
    Vset vset2=finalbody.check(env,newctx2,vset,exp);
    finallyCanFinish=!vset2.isDeadEnd();
    vset2=vset2.join(newctx2.vsBreak);
    if (finallyCanFinish) {
      for (Enumeration e=newexp.keys(); e.hasMoreElements(); ) {
        Object def=e.nextElement();
        exp.put(def,newexp.get(def));
      }
    }
    return ctx.removeAdditionalVars(vset1.addDAandJoinDU(vset2));
  }
  /** 
 * Inline
 */
  public Statement inline(  Environment env,  Context ctx){
    if (tryTemp != null) {
      ctx=new Context(ctx,this);
      ctx.declare(env,tryTemp);
    }
    if (init != null) {
      init=init.inline(env,ctx);
    }
    if (body != null) {
      body=body.inline(env,ctx);
    }
    if (finalbody != null) {
      finalbody=finalbody.inline(env,ctx);
    }
    if (body == null) {
      return eliminate(env,finalbody);
    }
    if (finalbody == null) {
      return eliminate(env,body);
    }
    return this;
  }
  /** 
 * Create a copy of the statement for method inlining
 */
  public Statement copyInline(  Context ctx,  boolean valNeeded){
    FinallyStatement s=(FinallyStatement)clone();
    if (tryTemp != null) {
      s.tryTemp=tryTemp.copyInline(ctx);
    }
    if (init != null) {
      s.init=init.copyInline(ctx,valNeeded);
    }
    if (body != null) {
      s.body=body.copyInline(ctx,valNeeded);
    }
    if (finalbody != null) {
      s.finalbody=finalbody.copyInline(ctx,valNeeded);
    }
    return s;
  }
  /** 
 * Compute cost of inlining this statement
 */
  public int costInline(  int thresh,  Environment env,  Context ctx){
    int cost=4;
    if (init != null) {
      cost+=init.costInline(thresh,env,ctx);
      if (cost >= thresh)       return cost;
    }
    if (body != null) {
      cost+=body.costInline(thresh,env,ctx);
      if (cost >= thresh)       return cost;
    }
    if (finalbody != null) {
      cost+=finalbody.costInline(thresh,env,ctx);
    }
    return cost;
  }
  /** 
 * Code
 */
  public void code(  Environment env,  Context ctx,  Assembler asm){
    ctx=new Context(ctx);
    Integer num1=null, num2=null;
    Label endLabel=new Label();
    if (tryTemp != null) {
      ctx.declare(env,tryTemp);
    }
    if (init != null) {
      CodeContext exprctx=new CodeContext(ctx,this);
      init.code(env,exprctx,asm);
    }
    if (finallyCanFinish) {
      LocalMember f1, f2;
      ClassDefinition thisClass=ctx.field.getClassDefinition();
      if (needReturnSlot) {
        Type returnType=ctx.field.getType().getReturnType();
        LocalMember localfield=new LocalMember(0,thisClass,0,returnType,idFinallyReturnValue);
        ctx.declare(env,localfield);
        env.debugOutput("Assigning return slot to " + localfield.number);
      }
      f1=new LocalMember(where,thisClass,0,Type.tObject,null);
      f2=new LocalMember(where,thisClass,0,Type.tInt,null);
      num1=new Integer(ctx.declare(env,f1));
      num2=new Integer(ctx.declare(env,f2));
    }
    TryData td=new TryData();
    td.add(null);
    CodeContext bodyctx=new CodeContext(ctx,this);
    asm.add(where,opc_try,td);
    body.code(env,bodyctx,asm);
    asm.add(bodyctx.breakLabel);
    asm.add(td.getEndLabel());
    if (finallyCanFinish) {
      asm.add(where,opc_jsr,bodyctx.contLabel);
      asm.add(where,opc_goto,endLabel);
    }
 else {
      asm.add(where,opc_goto,bodyctx.contLabel);
    }
    CatchData cd=td.getCatch(0);
    asm.add(cd.getLabel());
    if (finallyCanFinish) {
      asm.add(where,opc_astore,num1);
      asm.add(where,opc_jsr,bodyctx.contLabel);
      asm.add(where,opc_aload,num1);
      asm.add(where,opc_athrow);
    }
 else {
      asm.add(where,opc_pop);
    }
    asm.add(bodyctx.contLabel);
    bodyctx.contLabel=null;
    bodyctx.breakLabel=endLabel;
    if (finallyCanFinish) {
      asm.add(where,opc_astore,num2);
      finalbody.code(env,bodyctx,asm);
      asm.add(where,opc_ret,num2);
    }
 else {
      finalbody.code(env,bodyctx,asm);
    }
    asm.add(endLabel);
  }
  /** 
 * Print
 */
  public void print(  PrintStream out,  int indent){
    super.print(out,indent);
    out.print("try ");
    if (body != null) {
      body.print(out,indent);
    }
 else {
      out.print("<empty>");
    }
    out.print(" finally ");
    if (finalbody != null) {
      finalbody.print(out,indent);
    }
 else {
      out.print("<empty>");
    }
  }
}
