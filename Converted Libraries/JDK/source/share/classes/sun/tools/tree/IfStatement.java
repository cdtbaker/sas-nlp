package sun.tools.tree;
import sun.tools.java.*;
import sun.tools.asm.Assembler;
import sun.tools.asm.Label;
import java.io.PrintStream;
import java.util.Hashtable;
/** 
 * WARNING: The contents of this source file are not part of any
 * supported API.  Code that depends on them does so at its own risk:
 * they are subject to change or removal without notice.
 */
public class IfStatement extends Statement {
  Expression cond;
  Statement ifTrue;
  Statement ifFalse;
  /** 
 * Constructor
 */
  public IfStatement(  long where,  Expression cond,  Statement ifTrue,  Statement ifFalse){
    super(IF,where);
    this.cond=cond;
    this.ifTrue=ifTrue;
    this.ifFalse=ifFalse;
  }
  /** 
 * Check statement
 */
  Vset check(  Environment env,  Context ctx,  Vset vset,  Hashtable exp){
    checkLabel(env,ctx);
    CheckContext newctx=new CheckContext(ctx,this);
    ConditionVars cvars=cond.checkCondition(env,newctx,reach(env,vset),exp);
    cond=convert(env,newctx,Type.tBoolean,cond);
    Vset vsTrue=cvars.vsTrue.clearDeadEnd();
    Vset vsFalse=cvars.vsFalse.clearDeadEnd();
    vsTrue=ifTrue.check(env,newctx,vsTrue,exp);
    if (ifFalse != null)     vsFalse=ifFalse.check(env,newctx,vsFalse,exp);
    vset=vsTrue.join(vsFalse.join(newctx.vsBreak));
    return ctx.removeAdditionalVars(vset);
  }
  /** 
 * Inline
 */
  public Statement inline(  Environment env,  Context ctx){
    ctx=new Context(ctx,this);
    cond=cond.inlineValue(env,ctx);
    if (ifTrue != null) {
      ifTrue=ifTrue.inline(env,ctx);
    }
    if (ifFalse != null) {
      ifFalse=ifFalse.inline(env,ctx);
    }
    if (cond.equals(true)) {
      return eliminate(env,ifTrue);
    }
    if (cond.equals(false)) {
      return eliminate(env,ifFalse);
    }
    if ((ifTrue == null) && (ifFalse == null)) {
      return eliminate(env,new ExpressionStatement(where,cond).inline(env,ctx));
    }
    if (ifTrue == null) {
      cond=new NotExpression(cond.where,cond).inlineValue(env,ctx);
      return eliminate(env,new IfStatement(where,cond,ifFalse,null));
    }
    return this;
  }
  /** 
 * Create a copy of the statement for method inlining
 */
  public Statement copyInline(  Context ctx,  boolean valNeeded){
    IfStatement s=(IfStatement)clone();
    s.cond=cond.copyInline(ctx);
    if (ifTrue != null) {
      s.ifTrue=ifTrue.copyInline(ctx,valNeeded);
    }
    if (ifFalse != null) {
      s.ifFalse=ifFalse.copyInline(ctx,valNeeded);
    }
    return s;
  }
  /** 
 * The cost of inlining this statement
 */
  public int costInline(  int thresh,  Environment env,  Context ctx){
    int cost=1 + cond.costInline(thresh,env,ctx);
    if (ifTrue != null) {
      cost+=ifTrue.costInline(thresh,env,ctx);
    }
    if (ifFalse != null) {
      cost+=ifFalse.costInline(thresh,env,ctx);
    }
    return cost;
  }
  /** 
 * Code
 */
  public void code(  Environment env,  Context ctx,  Assembler asm){
    CodeContext newctx=new CodeContext(ctx,this);
    Label l1=new Label();
    cond.codeBranch(env,newctx,asm,l1,false);
    ifTrue.code(env,newctx,asm);
    if (ifFalse != null) {
      Label l2=new Label();
      asm.add(true,where,opc_goto,l2);
      asm.add(l1);
      ifFalse.code(env,newctx,asm);
      asm.add(l2);
    }
 else {
      asm.add(l1);
    }
    asm.add(newctx.breakLabel);
  }
  /** 
 * Print
 */
  public void print(  PrintStream out,  int indent){
    super.print(out,indent);
    out.print("if ");
    cond.print(out);
    out.print(" ");
    ifTrue.print(out,indent);
    if (ifFalse != null) {
      out.print(" else ");
      ifFalse.print(out,indent);
    }
  }
}
