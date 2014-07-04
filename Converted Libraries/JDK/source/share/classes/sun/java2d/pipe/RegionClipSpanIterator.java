package sun.java2d.pipe;
import java.awt.geom.PathIterator;
import java.awt.Rectangle;
/** 
 * This class clips a SpanIterator to a Region and outputs the
 * resulting spans as another SpanIterator.
 * Spans are output in the usual y/x order, unless the input span
 * iterator doesn't conform to this order, or the iterator's span
 * straddle more than one band of the Region used for clipping.
 * Principle of operation:
 * The iterator maintains a several cursors onto the RegionIterator
 * in order to avoid having to buffer spans from the SpanIterator.
 * They are:
 * resetState    The initial state of the RegionIterator
 * lwm             Low Water Mark, a running start point for
 * processing each band. Usually goes down, but
 * can be reset to resetState if a span has a lower
 * start coordinate than the previous one.
 * row             The start of the current band of the RegionIterator
 * box             The current span of the current row
 * The main nextSpan() loop implements a coroutine like structure, with
 * three producers to get the next span, row and box calling each other
 * to iterate through the span iterator and region.
 * REMIND: Needs a native implementation!
 */
public class RegionClipSpanIterator implements SpanIterator {
  Region rgn;
  SpanIterator spanIter;
  RegionIterator resetState;
  RegionIterator lwm;
  RegionIterator row;
  RegionIterator box;
  int spanlox, spanhix, spanloy, spanhiy;
  int lwmloy, lwmhiy;
  int rgnlox, rgnloy, rgnhix, rgnhiy;
  int rgnbndslox, rgnbndsloy, rgnbndshix, rgnbndshiy;
  int rgnbox[]=new int[4];
  int spanbox[]=new int[4];
  boolean doNextSpan;
  boolean doNextBox;
  boolean done=false;
  public RegionClipSpanIterator(  Region rgn,  SpanIterator spanIter){
    this.spanIter=spanIter;
    resetState=rgn.getIterator();
    lwm=resetState.createCopy();
    if (!lwm.nextYRange(rgnbox)) {
      done=true;
      return;
    }
    rgnloy=lwmloy=rgnbox[1];
    rgnhiy=lwmhiy=rgnbox[3];
    rgn.getBounds(rgnbox);
    rgnbndslox=rgnbox[0];
    rgnbndsloy=rgnbox[1];
    rgnbndshix=rgnbox[2];
    rgnbndshiy=rgnbox[3];
    if (rgnbndslox >= rgnbndshix || rgnbndsloy >= rgnbndshiy) {
      done=true;
      return;
    }
    this.rgn=rgn;
    row=lwm.createCopy();
    box=row.createCopy();
    doNextSpan=true;
    doNextBox=false;
  }
  public void getPathBox(  int pathbox[]){
    int[] rgnbox=new int[4];
    rgn.getBounds(rgnbox);
    spanIter.getPathBox(pathbox);
    if (pathbox[0] < rgnbox[0]) {
      pathbox[0]=rgnbox[0];
    }
    if (pathbox[1] < rgnbox[1]) {
      pathbox[1]=rgnbox[1];
    }
    if (pathbox[2] > rgnbox[2]) {
      pathbox[2]=rgnbox[2];
    }
    if (pathbox[3] > rgnbox[3]) {
      pathbox[3]=rgnbox[3];
    }
  }
  public void intersectClipBox(  int lox,  int loy,  int hix,  int hiy){
    spanIter.intersectClipBox(lox,loy,hix,hiy);
  }
  public boolean nextSpan(  int resultbox[]){
    if (done) {
      return false;
    }
    int resultlox, resultloy, resulthix, resulthiy;
    boolean doNextRow=false;
    while (true) {
      if (doNextSpan) {
        if (!spanIter.nextSpan(spanbox)) {
          done=true;
          return false;
        }
 else {
          spanlox=spanbox[0];
          if (spanlox >= rgnbndshix) {
            continue;
          }
          spanloy=spanbox[1];
          if (spanloy >= rgnbndshiy) {
            continue;
          }
          spanhix=spanbox[2];
          if (spanhix <= rgnbndslox) {
            continue;
          }
          spanhiy=spanbox[3];
          if (spanhiy <= rgnbndsloy) {
            continue;
          }
        }
        if (lwmloy > spanloy) {
          lwm.copyStateFrom(resetState);
          lwm.nextYRange(rgnbox);
          lwmloy=rgnbox[1];
          lwmhiy=rgnbox[3];
        }
        while (lwmhiy <= spanloy) {
          if (!lwm.nextYRange(rgnbox))           break;
          lwmloy=rgnbox[1];
          lwmhiy=rgnbox[3];
        }
        if (lwmhiy > spanloy && lwmloy < spanhiy) {
          if (rgnloy != lwmloy) {
            row.copyStateFrom(lwm);
            rgnloy=lwmloy;
            rgnhiy=lwmhiy;
          }
          box.copyStateFrom(row);
          doNextBox=true;
          doNextSpan=false;
        }
        continue;
      }
      if (doNextRow) {
        doNextRow=false;
        boolean ok=row.nextYRange(rgnbox);
        if (ok) {
          rgnloy=rgnbox[1];
          rgnhiy=rgnbox[3];
        }
        if (!ok || rgnloy >= spanhiy) {
          doNextSpan=true;
        }
 else {
          box.copyStateFrom(row);
          doNextBox=true;
        }
        continue;
      }
      if (doNextBox) {
        boolean ok=box.nextXBand(rgnbox);
        if (ok) {
          rgnlox=rgnbox[0];
          rgnhix=rgnbox[2];
        }
        if (!ok || rgnlox >= spanhix) {
          doNextBox=false;
          if (rgnhiy >= spanhiy) {
            doNextSpan=true;
          }
 else {
            doNextRow=true;
          }
        }
 else {
          doNextBox=rgnhix <= spanlox;
        }
        continue;
      }
      doNextBox=true;
      if (spanlox > rgnlox) {
        resultlox=spanlox;
      }
 else {
        resultlox=rgnlox;
      }
      if (spanloy > rgnloy) {
        resultloy=spanloy;
      }
 else {
        resultloy=rgnloy;
      }
      if (spanhix < rgnhix) {
        resulthix=spanhix;
      }
 else {
        resulthix=rgnhix;
      }
      if (spanhiy < rgnhiy) {
        resulthiy=spanhiy;
      }
 else {
        resulthiy=rgnhiy;
      }
      if (resultlox >= resulthix || resultloy >= resulthiy) {
        continue;
      }
 else {
        break;
      }
    }
    resultbox[0]=resultlox;
    resultbox[1]=resultloy;
    resultbox[2]=resulthix;
    resultbox[3]=resulthiy;
    return true;
  }
  /** 
 * This method tells the iterator that it may skip all spans
 * whose Y range is completely above the indicated Y coordinate.
 */
  public void skipDownTo(  int y){
    spanIter.skipDownTo(y);
  }
  /** 
 * This method returns a native pointer to a function block that
 * can be used by a native method to perform the same iteration
 * cycle that the above methods provide while avoiding upcalls to
 * the Java object.
 * The definition of the structure whose pointer is returned by
 * this method is defined in:
 * <pre>
 * src/share/native/sun/java2d/pipe/SpanIterator.h
 * </pre>
 */
  public long getNativeIterator(){
    return 0;
  }
  protected void finalize(){
  }
}
