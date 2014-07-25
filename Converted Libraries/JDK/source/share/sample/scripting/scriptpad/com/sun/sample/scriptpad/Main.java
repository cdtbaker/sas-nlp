package com.sun.sample.scriptpad;
import javax.script.*;
import java.io.*;
/** 
 * This is the entry point of "Scriptpad" sample. This class creates
 * ScriptEngine and evaluates few JavaScript "files" -- which are stored
 * as resources (please refer to src/resources/*.js). Actual code for the
 * scriptpad's main functionality lives in these JavaScript files.
 */
public class Main {
  public static void main(  String[] args) throws Exception {
    ScriptEngineManager m=new ScriptEngineManager();
    ScriptEngine engine=m.getEngineByName("js");
    engine.put("engine",engine);
    eval(engine,"conc.js");
    eval(engine,"gui.js");
    eval(engine,"scriptpad.js");
    eval(engine,"mm.js");
  }
  private static void eval(  ScriptEngine engine,  String name) throws Exception {
    InputStream is=Main.class.getResourceAsStream("/resources/" + name);
    engine.put(ScriptEngine.NAME,name);
    engine.eval(new InputStreamReader(is));
  }
}
