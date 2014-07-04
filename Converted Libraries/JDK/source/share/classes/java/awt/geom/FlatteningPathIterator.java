package java.awt.geom;
import java.util.*;
/** 
 * The <code>FlatteningPathIterator</code> class returns a flattened view of
 * another {@link PathIterator} object.  Other {@link java.awt.Shape Shape}classes can use this class to provide flattening behavior for their paths
 * without having to perform the interpolation calculations themselves.
 * @author Jim Graham
 */
public class FlatteningPathIterator implements PathIterator {
  static final int GROW_SIZE=24;
  PathIterator src;
  double squareflat;
  int limit;
  double hold[]=new double[14];
  double curx, cury;
  double movx, movy;
  int holdType;
  int holdEnd;
  int holdIndex;
  int levels[];
  int levelIndex;
  boolean done;
  /** 
 * Constructs a new <code>FlatteningPathIterator</code> object that
 * flattens a path as it iterates over it.  The iterator does not
 * subdivide any curve read from the source iterator to more than
 * 10 levels of subdivision which yields a maximum of 1024 line
 * segments per curve.
 * @param src the original unflattened path being iterated over
 * @param flatness the maximum allowable distance between the
 * control points and the flattened curve
 */
  public FlatteningPathIterator(  PathIterator src,  double flatness){
    this(src,flatness,10);
  }
  /** 
 * Constructs a new <code>FlatteningPathIterator</code> object
 * that flattens a path as it iterates over it.
 * The <code>limit</code> parameter allows you to control the
 * maximum number of recursive subdivisions that the iterator
 * can make before it assumes that the curve is flat enough
 * without measuring against the <code>flatness</code> parameter.
 * The flattened iteration therefore never generates more than
 * a maximum of <code>(2^limit)</code> line segments per curve.
 * @param src the original unflattened path being iterated over
 * @param flatness the maximum allowable distance between the
 * control points and the flattened curve
 * @param limit the maximum number of recursive subdivisions
 * allowed for any curved segment
 * @exception <code>IllegalArgumentException</code> if
 * <code>flatness</code> or <code>limit</code>
 * is less than zero
 */
  public FlatteningPathIterator(  PathIterator src,  double flatness,  int limit){
    if (flatness < 0.0) {
      throw new IllegalArgumentException("flatness must be >= 0");
    }
    if (limit < 0) {
      throw new IllegalArgumentException("limit must be >= 0");
    }
    this.src=src;
    this.squareflat=flatness * flatness;
    this.limit=limit;
    this.levels=new int[limit + 1];
    next(false);
  }
  /** 
 * Returns the flatness of this iterator.
 * @return the flatness of this <code>FlatteningPathIterator</code>.
 */
  public double getFlatness(){
    return Math.sqrt(squareflat);
  }
  /** 
 * Returns the recursion limit of this iterator.
 * @return the recursion limit of this
 * <code>FlatteningPathIterator</code>.
 */
  public int getRecursionLimit(){
    return limit;
  }
  /** 
 * Returns the winding rule for determining the interior of the
 * path.
 * @return the winding rule of the original unflattened path being
 * iterated over.
 * @see PathIterator#WIND_EVEN_ODD
 * @see PathIterator#WIND_NON_ZERO
 */
  public int getWindingRule(){
    return src.getWindingRule();
  }
  /** 
 * Tests if the iteration is complete.
 * @return <code>true</code> if all the segments have
 * been read; <code>false</code> otherwise.
 */
  public boolean isDone(){
    return done;
  }
  void ensureHoldCapacity(  int want){
    if (holdIndex - want < 0) {
      int have=hold.length - holdIndex;
      int newsize=hold.length + GROW_SIZE;
      double newhold[]=new double[newsize];
      System.arraycopy(hold,holdIndex,newhold,holdIndex + GROW_SIZE,have);
      hold=newhold;
      holdIndex+=GROW_SIZE;
      holdEnd+=GROW_SIZE;
    }
  }
  /** 
 * Moves the iterator to the next segment of the path forwards
 * along the primary direction of traversal as long as there are
 * more points in that direction.
 */
  public void next(){
    next(true);
  }
  private void next(  boolean doNext){
    int level;
    if (holdIndex >= holdEnd) {
      if (doNext) {
        src.next();
      }
      if (src.isDone()) {
        done=true;
        return;
      }
      holdType=src.currentSegment(hold);
      levelIndex=0;
      levels[0]=0;
    }
switch (holdType) {
case SEG_MOVETO:
case SEG_LINETO:
      curx=hold[0];
    cury=hold[1];
  if (holdType == SEG_MOVETO) {
    movx=curx;
    movy=cury;
  }
holdIndex=0;
holdEnd=0;
break;
case SEG_CLOSE:
curx=movx;
cury=movy;
holdIndex=0;
holdEnd=0;
break;
case SEG_QUADTO:
if (holdIndex >= holdEnd) {
holdIndex=hold.length - 6;
holdEnd=hold.length - 2;
hold[holdIndex + 0]=curx;
hold[holdIndex + 1]=cury;
hold[holdIndex + 2]=hold[0];
hold[holdIndex + 3]=hold[1];
hold[holdIndex + 4]=curx=hold[2];
hold[holdIndex + 5]=cury=hold[3];
}
level=levels[levelIndex];
while (level < limit) {
if (QuadCurve2D.getFlatnessSq(hold,holdIndex) < squareflat) {
break;
}
ensureHoldCapacity(4);
QuadCurve2D.subdivide(hold,holdIndex,hold,holdIndex - 4,hold,holdIndex);
holdIndex-=4;
level++;
levels[levelIndex]=level;
levelIndex++;
levels[levelIndex]=level;
}
holdIndex+=4;
levelIndex--;
break;
case SEG_CUBICTO:
if (holdIndex >= holdEnd) {
holdIndex=hold.length - 8;
holdEnd=hold.length - 2;
hold[holdIndex + 0]=curx;
hold[holdIndex + 1]=cury;
hold[holdIndex + 2]=hold[0];
hold[holdIndex + 3]=hold[1];
hold[holdIndex + 4]=hold[2];
hold[holdIndex + 5]=hold[3];
hold[holdIndex + 6]=curx=hold[4];
hold[holdIndex + 7]=cury=hold[5];
}
level=levels[levelIndex];
while (level < limit) {
if (CubicCurve2D.getFlatnessSq(hold,holdIndex) < squareflat) {
break;
}
ensureHoldCapacity(6);
CubicCurve2D.subdivide(hold,holdIndex,hold,holdIndex - 6,hold,holdIndex);
holdIndex-=6;
level++;
levels[levelIndex]=level;
levelIndex++;
levels[levelIndex]=level;
}
holdIndex+=6;
levelIndex--;
break;
}
}
/** 
 * Returns the coordinates and type of the current path segment in
 * the iteration.
 * The return value is the path segment type:
 * SEG_MOVETO, SEG_LINETO, or SEG_CLOSE.
 * A float array of length 6 must be passed in and can be used to
 * store the coordinates of the point(s).
 * Each point is stored as a pair of float x,y coordinates.
 * SEG_MOVETO and SEG_LINETO types return one point,
 * and SEG_CLOSE does not return any points.
 * @param coords an array that holds the data returned from
 * this method
 * @return the path segment type of the current path segment.
 * @exception <code>NoSuchElementException</code> if there
 * are no more elements in the flattening path to be
 * returned.
 * @see PathIterator#SEG_MOVETO
 * @see PathIterator#SEG_LINETO
 * @see PathIterator#SEG_CLOSE
 */
public int currentSegment(float[] coords){
if (isDone()) {
throw new NoSuchElementException("flattening iterator out of bounds");
}
int type=holdType;
if (type != SEG_CLOSE) {
coords[0]=(float)hold[holdIndex + 0];
coords[1]=(float)hold[holdIndex + 1];
if (type != SEG_MOVETO) {
type=SEG_LINETO;
}
}
return type;
}
/** 
 * Returns the coordinates and type of the current path segment in
 * the iteration.
 * The return value is the path segment type:
 * SEG_MOVETO, SEG_LINETO, or SEG_CLOSE.
 * A double array of length 6 must be passed in and can be used to
 * store the coordinates of the point(s).
 * Each point is stored as a pair of double x,y coordinates.
 * SEG_MOVETO and SEG_LINETO types return one point,
 * and SEG_CLOSE does not return any points.
 * @param coords an array that holds the data returned from
 * this method
 * @return the path segment type of the current path segment.
 * @exception <code>NoSuchElementException</code> if there
 * are no more elements in the flattening path to be
 * returned.
 * @see PathIterator#SEG_MOVETO
 * @see PathIterator#SEG_LINETO
 * @see PathIterator#SEG_CLOSE
 */
public int currentSegment(double[] coords){
if (isDone()) {
throw new NoSuchElementException("flattening iterator out of bounds");
}
int type=holdType;
if (type != SEG_CLOSE) {
coords[0]=hold[holdIndex + 0];
coords[1]=hold[holdIndex + 1];
if (type != SEG_MOVETO) {
type=SEG_LINETO;
}
}
return type;
}
}
