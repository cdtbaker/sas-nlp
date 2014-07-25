package sun.java2d.pisces;
import java.util.Iterator;
final class Curve {
  float ax, ay, bx, by, cx, cy, dx, dy;
  float dax, day, dbx, dby;
  Curve(){
  }
  void set(  float[] points,  int type){
switch (type) {
case 8:
      set(points[0],points[1],points[2],points[3],points[4],points[5],points[6],points[7]);
    break;
case 6:
  set(points[0],points[1],points[2],points[3],points[4],points[5]);
break;
default :
throw new InternalError("Curves can only be cubic or quadratic");
}
}
void set(float x1,float y1,float x2,float y2,float x3,float y3,float x4,float y4){
ax=3 * (x2 - x3) + x4 - x1;
ay=3 * (y2 - y3) + y4 - y1;
bx=3 * (x1 - 2 * x2 + x3);
by=3 * (y1 - 2 * y2 + y3);
cx=3 * (x2 - x1);
cy=3 * (y2 - y1);
dx=x1;
dy=y1;
dax=3 * ax;
day=3 * ay;
dbx=2 * bx;
dby=2 * by;
}
void set(float x1,float y1,float x2,float y2,float x3,float y3){
ax=ay=0f;
bx=x1 - 2 * x2 + x3;
by=y1 - 2 * y2 + y3;
cx=2 * (x2 - x1);
cy=2 * (y2 - y1);
dx=x1;
dy=y1;
dax=0;
day=0;
dbx=2 * bx;
dby=2 * by;
}
float xat(float t){
return t * (t * (t * ax + bx) + cx) + dx;
}
float yat(float t){
return t * (t * (t * ay + by) + cy) + dy;
}
float dxat(float t){
return t * (t * dax + dbx) + cx;
}
float dyat(float t){
return t * (t * day + dby) + cy;
}
int dxRoots(float[] roots,int off){
return Helpers.quadraticRoots(dax,dbx,cx,roots,off);
}
int dyRoots(float[] roots,int off){
return Helpers.quadraticRoots(day,dby,cy,roots,off);
}
int infPoints(float[] pts,int off){
final float a=dax * dby - dbx * day;
final float b=2 * (cy * dax - day * cx);
final float c=cy * dbx - cx * dby;
return Helpers.quadraticRoots(a,b,c,pts,off);
}
private int perpendiculardfddf(float[] pts,int off){
assert pts.length >= off + 4;
final float a=2 * (dax * dax + day * day);
final float b=3 * (dax * dbx + day * dby);
final float c=2 * (dax * cx + day * cy) + dbx * dbx + dby * dby;
final float d=dbx * cx + dby * cy;
return Helpers.cubicRootsInAB(a,b,c,d,pts,off,0f,1f);
}
int rootsOfROCMinusW(float[] roots,int off,final float w,final float err){
assert off <= 6 && roots.length >= 10;
int ret=off;
int numPerpdfddf=perpendiculardfddf(roots,off);
float t0=0, ft0=ROCsq(t0) - w * w;
roots[off + numPerpdfddf]=1f;
numPerpdfddf++;
for (int i=off; i < off + numPerpdfddf; i++) {
float t1=roots[i], ft1=ROCsq(t1) - w * w;
if (ft0 == 0f) {
roots[ret++]=t0;
}
 else if (ft1 * ft0 < 0f) {
roots[ret++]=falsePositionROCsqMinusX(t0,t1,w * w,err);
}
t0=t1;
ft0=ft1;
}
return ret - off;
}
private static float eliminateInf(float x){
return (x == Float.POSITIVE_INFINITY ? Float.MAX_VALUE : (x == Float.NEGATIVE_INFINITY ? Float.MIN_VALUE : x));
}
private float falsePositionROCsqMinusX(float x0,float x1,final float x,final float err){
final int iterLimit=100;
int side=0;
float t=x1, ft=eliminateInf(ROCsq(t) - x);
float s=x0, fs=eliminateInf(ROCsq(s) - x);
float r=s, fr;
for (int i=0; i < iterLimit && Math.abs(t - s) > err * Math.abs(t + s); i++) {
r=(fs * t - ft * s) / (fs - ft);
fr=ROCsq(r) - x;
if (sameSign(fr,ft)) {
ft=fr;
t=r;
if (side < 0) {
fs/=(1 << (-side));
side--;
}
 else {
side=-1;
}
}
 else if (fr * fs > 0) {
fs=fr;
s=r;
if (side > 0) {
ft/=(1 << side);
side++;
}
 else {
side=1;
}
}
 else {
break;
}
}
return r;
}
private static boolean sameSign(double x,double y){
return (x < 0 && y < 0) || (x > 0 && y > 0);
}
private float ROCsq(final float t){
final float dx=t * (t * dax + dbx) + cx;
final float dy=t * (t * day + dby) + cy;
final float ddx=2 * dax * t + dbx;
final float ddy=2 * day * t + dby;
final float dx2dy2=dx * dx + dy * dy;
final float ddx2ddy2=ddx * ddx + ddy * ddy;
final float ddxdxddydy=ddx * dx + ddy * dy;
return dx2dy2 * ((dx2dy2 * dx2dy2) / (dx2dy2 * ddx2ddy2 - ddxdxddydy * ddxdxddydy));
}
static Iterator<Integer> breakPtsAtTs(final float[] pts,final int type,final float[] Ts,final int numTs){
assert pts.length >= 2 * type && numTs <= Ts.length;
return new Iterator<Integer>(){
final Integer i0=0;
final Integer itype=type;
int nextCurveIdx=0;
Integer curCurveOff=i0;
float prevT=0;
@Override public boolean hasNext(){
return nextCurveIdx < numTs + 1;
}
@Override public Integer next(){
Integer ret;
if (nextCurveIdx < numTs) {
float curT=Ts[nextCurveIdx];
float splitT=(curT - prevT) / (1 - prevT);
Helpers.subdivideAt(splitT,pts,curCurveOff,pts,0,pts,type,type);
prevT=curT;
ret=i0;
curCurveOff=itype;
}
 else {
ret=curCurveOff;
}
nextCurveIdx++;
return ret;
}
@Override public void remove(){
}
}
;
}
}
