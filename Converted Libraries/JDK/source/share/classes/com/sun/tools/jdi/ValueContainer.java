package com.sun.tools.jdi;
import com.sun.jdi.*;
interface ValueContainer {
  Type type() throws ClassNotLoadedException ;
  Type findType(  String signature) throws ClassNotLoadedException ;
  String typeName();
  String signature();
}
