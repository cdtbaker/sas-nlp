package com.sun.tools.jdi;
import com.sun.jdi.*;
import java.util.EventListener;
interface ThreadListener extends EventListener {
  boolean threadResumable(  ThreadAction action);
}
