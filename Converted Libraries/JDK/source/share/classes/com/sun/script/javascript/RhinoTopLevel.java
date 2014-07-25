package com.sun.script.javascript;
import sun.org.mozilla.javascript.internal.*;
import javax.script.*;
/** 
 * This class serves as top level scope for Rhino. This class adds
 * 3 top level functions (bindings, scope, sync) and two constructors
 * (JSAdapter, JavaAdapter).
 * @author A. Sundararajan
 * @since 1.6
 */
public final class RhinoTopLevel extends ImporterTopLevel {
  RhinoTopLevel(  Context cx,  RhinoScriptEngine engine){
    super(cx);
    this.engine=engine;
    new LazilyLoadedCtor(this,"JSAdapter","com.sun.script.javascript.JSAdapter",false);
    JavaAdapter.init(cx,this,false);
    String names[]={"bindings","scope","sync"};
    defineFunctionProperties(names,RhinoTopLevel.class,ScriptableObject.DONTENUM);
  }
  /** 
 * The bindings function takes a JavaScript scope object
 * of type ExternalScriptable and returns the underlying Bindings
 * instance.
 * var page = scope(pageBindings);
 * with (page) {
 * // code that uses page scope
 * }
 * var b = bindings(page);
 * // operate on bindings here.
 */
  public static Object bindings(  Context cx,  Scriptable thisObj,  Object[] args,  Function funObj){
    if (args.length == 1) {
      Object arg=args[0];
      if (arg instanceof Wrapper) {
        arg=((Wrapper)arg).unwrap();
      }
      if (arg instanceof ExternalScriptable) {
        ScriptContext ctx=((ExternalScriptable)arg).getContext();
        Bindings bind=ctx.getBindings(ScriptContext.ENGINE_SCOPE);
        return Context.javaToJS(bind,ScriptableObject.getTopLevelScope(thisObj));
      }
    }
    return cx.getUndefinedValue();
  }
  /** 
 * The scope function creates a new JavaScript scope object
 * with given Bindings object as backing store. This can be used
 * to create a script scope based on arbitrary Bindings instance.
 * For example, in webapp scenario, a 'page' level Bindings instance
 * may be wrapped as a scope and code can be run in JavaScripe 'with'
 * statement:
 * var page = scope(pageBindings);
 * with (page) {
 * // code that uses page scope
 * }
 */
  public static Object scope(  Context cx,  Scriptable thisObj,  Object[] args,  Function funObj){
    if (args.length == 1) {
      Object arg=args[0];
      if (arg instanceof Wrapper) {
        arg=((Wrapper)arg).unwrap();
      }
      if (arg instanceof Bindings) {
        ScriptContext ctx=new SimpleScriptContext();
        ctx.setBindings((Bindings)arg,ScriptContext.ENGINE_SCOPE);
        Scriptable res=new ExternalScriptable(ctx);
        res.setPrototype(ScriptableObject.getObjectPrototype(thisObj));
        res.setParentScope(ScriptableObject.getTopLevelScope(thisObj));
        return res;
      }
    }
    return cx.getUndefinedValue();
  }
  /** 
 * The sync function creates a synchronized function (in the sense
 * of a Java synchronized method) from an existing function. The
 * new function synchronizes on the <code>this</code> object of
 * its invocation.
 * js> var o = { f : sync(function(x) {
 * print("entry");
 * Packages.java.lang.Thread.sleep(x*1000);
 * print("exit");
 * })};
 * js> thread(function() {o.f(5);});
 * entry
 * js> thread(function() {o.f(5);});
 * js>
 * exit
 * entry
 * exit
 */
  public static Object sync(  Context cx,  Scriptable thisObj,  Object[] args,  Function funObj){
    if (args.length == 1 && args[0] instanceof Function) {
      return new Synchronizer((Function)args[0]);
    }
 else {
      throw Context.reportRuntimeError("wrong argument(s) for sync");
    }
  }
  RhinoScriptEngine getScriptEngine(){
    return engine;
  }
  private RhinoScriptEngine engine;
}
