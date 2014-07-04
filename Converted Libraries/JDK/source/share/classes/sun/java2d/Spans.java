package sun.java2d;
import java.util.Comparator;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
/** 
 * Maintains a list of half-open intervals, called Spans.
 * A Span can be tested against the list of Spans
 * for intersection.
 */
public class Spans {
  /** 
 * This class will sort and collapse its span
 * entries after this many span additions via
 * the <code>add</code> method.
 */
  private static final int kMaxAddsSinceSort=256;
  /** 
 * Holds a list of individual
 * Span instances.
 */
  private List mSpans=new Vector(kMaxAddsSinceSort);
  /** 
 * The number of <code>Span</code>
 * instances that have been added
 * to this object without a sort
 * and collapse taking place.
 */
  private int mAddsSinceSort=0;
  public Spans(){
  }
  /** 
 * Add a span covering the half open interval
 * including <code>start</code> up to
 * but not including <code>end</code>.
 */
  public void add(  float start,  float end){
    if (mSpans != null) {
      mSpans.add(new Span(start,end));
      if (++mAddsSinceSort >= kMaxAddsSinceSort) {
        sortAndCollapse();
      }
    }
  }
  /** 
 * Add a span which covers the entire range.
 * This call is logically equivalent to
 * <code>add(Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY)</code>
 * The result of making this call is that
 * all future <code>add</code> calls are ignored
 * and the <code>intersects</code> method always
 * returns true.
 */
  public void addInfinite(){
    mSpans=null;
  }
  /** 
 * Returns true if the span defined by the half-open
 * interval from <code>start</code> up to,
 * but not including, <code>end</code> intersects
 * any of the spans defined by this instance.
 */
  public boolean intersects(  float start,  float end){
    boolean doesIntersect;
    if (mSpans != null) {
      if (mAddsSinceSort > 0) {
        sortAndCollapse();
      }
      int found=Collections.binarySearch(mSpans,new Span(start,end),SpanIntersection.instance);
      doesIntersect=found >= 0;
    }
 else {
      doesIntersect=true;
    }
    return doesIntersect;
  }
  /** 
 * Sort the spans in ascending order by their
 * start position. After the spans are sorted
 * collapse any spans that intersect into a
 * single span. The result is a sorted,
 * non-overlapping list of spans.
 */
  private void sortAndCollapse(){
    Collections.sort(mSpans);
    mAddsSinceSort=0;
    Iterator iter=mSpans.iterator();
    Span span=null;
    if (iter.hasNext()) {
      span=(Span)iter.next();
    }
    while (iter.hasNext()) {
      Span nextSpan=(Span)iter.next();
      if (span.subsume(nextSpan)) {
        iter.remove();
      }
 else {
        span=nextSpan;
      }
    }
  }
  /** 
 * Holds a single half-open interval.
 */
static class Span implements Comparable {
    /** 
 * The span includes the starting point.
 */
    private float mStart;
    /** 
 * The span goes up to but does not include
 * the ending point.
 */
    private float mEnd;
    /** 
 * Create a half-open interval including
 * <code>start</code> but not including
 * <code>end</code>.
 */
    Span(    float start,    float end){
      mStart=start;
      mEnd=end;
    }
    /** 
 * Return the start of the <code>Span</code>.
 * The start is considered part of the
 * half-open interval.
 */
    final float getStart(){
      return mStart;
    }
    /** 
 * Return the end of the <code>Span</code>.
 * The end is not considered part of the
 * half-open interval.
 */
    final float getEnd(){
      return mEnd;
    }
    /** 
 * Change the initial position of the
 * <code>Span</code>.
 */
    final void setStart(    float start){
      mStart=start;
    }
    /** 
 * Change the terminal position of the
 * <code>Span</code>.
 */
    final void setEnd(    float end){
      mEnd=end;
    }
    /** 
 * Attempt to alter this <code>Span</code>
 * to include <code>otherSpan</code> without
 * altering this span's starting position.
 * If <code>otherSpan</code> can be so consumed
 * by this <code>Span</code> then <code>true</code>
 * is returned.
 */
    boolean subsume(    Span otherSpan){
      boolean isSubsumed=contains(otherSpan.mStart);
      if (isSubsumed && otherSpan.mEnd > mEnd) {
        mEnd=otherSpan.mEnd;
      }
      return isSubsumed;
    }
    /** 
 * Return true if the passed in position
 * lies in the half-open interval defined
 * by this <code>Span</code>.
 */
    boolean contains(    float pos){
      return mStart <= pos && pos < mEnd;
    }
    /** 
 * Rank spans according to their starting
 * position. The end position is ignored
 * in this ranking.
 */
    public int compareTo(    Object o){
      Span otherSpan=(Span)o;
      float otherStart=otherSpan.getStart();
      int result;
      if (mStart < otherStart) {
        result=-1;
      }
 else       if (mStart > otherStart) {
        result=1;
      }
 else {
        result=0;
      }
      return result;
    }
    public String toString(){
      return "Span: " + mStart + " to "+ mEnd;
    }
  }
  /** 
 * This class ranks a pair of <code>Span</code>
 * instances. If the instances intersect they
 * are deemed equal otherwise they are ranked
 * by their relative position. Use
 * <code>SpanIntersection.instance</code> to
 * get the single instance of this class.
 */
static class SpanIntersection implements Comparator {
    /** 
 * This class is a Singleton and the following
 * is the single instance.
 */
    static final SpanIntersection instance=new SpanIntersection();
    /** 
 * Only this class can create instances of itself.
 */
    private SpanIntersection(){
    }
    public int compare(    Object o1,    Object o2){
      int result;
      Span span1=(Span)o1;
      Span span2=(Span)o2;
      if (span1.getEnd() <= span2.getStart()) {
        result=-1;
      }
 else       if (span1.getStart() >= span2.getEnd()) {
        result=1;
      }
 else {
        result=0;
      }
      return result;
    }
  }
}
