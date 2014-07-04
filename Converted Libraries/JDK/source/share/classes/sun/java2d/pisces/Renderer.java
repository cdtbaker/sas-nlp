package sun.java2d.pisces;
import sun.awt.geom.PathConsumer2D;
final class Renderer implements PathConsumer2D {
private class ScanlineIterator {
    private int[] crossings;
    private final int maxY;
    private int nextY;
    private int edgeCount;
    private int[] edgePtrs;
    private static final int INIT_CROSSINGS_SIZE=10;
    private ScanlineIterator(    int start,    int end){
      crossings=new int[INIT_CROSSINGS_SIZE];
      edgePtrs=new int[INIT_CROSSINGS_SIZE];
      nextY=start;
      maxY=end;
      edgeCount=0;
    }
    private int next(){
      int cury=nextY++;
      int bucket=cury - boundsMinY;
      int count=this.edgeCount;
      int ptrs[]=this.edgePtrs;
      int bucketcount=edgeBucketCounts[bucket];
      if ((bucketcount & 0x1) != 0) {
        int newCount=0;
        for (int i=0; i < count; i++) {
          int ecur=ptrs[i];
          if (edges[ecur + YMAX] > cury) {
            ptrs[newCount++]=ecur;
          }
        }
        count=newCount;
      }
      ptrs=Helpers.widenArray(ptrs,count,bucketcount >> 1);
      for (int ecur=edgeBuckets[bucket]; ecur != NULL; ecur=(int)edges[ecur + NEXT]) {
        ptrs[count++]=ecur;
      }
      this.edgePtrs=ptrs;
      this.edgeCount=count;
      int xings[]=this.crossings;
      if (xings.length < count) {
        this.crossings=xings=new int[ptrs.length];
      }
      for (int i=0; i < count; i++) {
        int ecur=ptrs[i];
        float curx=edges[ecur + CURX];
        int cross=((int)curx) << 1;
        edges[ecur + CURX]=curx + edges[ecur + SLOPE];
        if (edges[ecur + OR] > 0) {
          cross|=1;
        }
        int j=i;
        while (--j >= 0) {
          int jcross=xings[j];
          if (jcross <= cross) {
            break;
          }
          xings[j + 1]=jcross;
          ptrs[j + 1]=ptrs[j];
        }
        xings[j + 1]=cross;
        ptrs[j + 1]=ecur;
      }
      return count;
    }
    private boolean hasNext(){
      return nextY < maxY;
    }
    private int curY(){
      return nextY - 1;
    }
  }
  private static final int YMAX=0;
  private static final int CURX=1;
  private static final int OR=2;
  private static final int SLOPE=3;
  private static final int NEXT=4;
  private float edgeMinY=Float.POSITIVE_INFINITY;
  private float edgeMaxY=Float.NEGATIVE_INFINITY;
  private float edgeMinX=Float.POSITIVE_INFINITY;
  private float edgeMaxX=Float.NEGATIVE_INFINITY;
  private static final int SIZEOF_EDGE=5;
  private static final int NULL=-SIZEOF_EDGE;
  private float[] edges=null;
  private static final int INIT_NUM_EDGES=8;
  private int[] edgeBuckets=null;
  private int[] edgeBucketCounts=null;
  private int numEdges;
  private static final float DEC_BND=20f;
  private static final float INC_BND=8f;
  private void addEdgeToBucket(  final int eptr,  final int bucket){
    edges[eptr + NEXT]=edgeBuckets[bucket];
    edgeBuckets[bucket]=eptr;
    edgeBucketCounts[bucket]+=2;
  }
  private void quadBreakIntoLinesAndAdd(  float x0,  float y0,  final Curve c,  final float x2,  final float y2){
    final float QUAD_DEC_BND=32;
    final int countlg=4;
    int count=1 << countlg;
    int countsq=count * count;
    float maxDD=Math.max(c.dbx / countsq,c.dby / countsq);
    while (maxDD > QUAD_DEC_BND) {
      maxDD/=4;
      count<<=1;
    }
    countsq=count * count;
    final float ddx=c.dbx / countsq;
    final float ddy=c.dby / countsq;
    float dx=c.bx / countsq + c.cx / count;
    float dy=c.by / countsq + c.cy / count;
    while (count-- > 1) {
      float x1=x0 + dx;
      dx+=ddx;
      float y1=y0 + dy;
      dy+=ddy;
      addLine(x0,y0,x1,y1);
      x0=x1;
      y0=y1;
    }
    addLine(x0,y0,x2,y2);
  }
  private void curveBreakIntoLinesAndAdd(  float x0,  float y0,  final Curve c,  final float x3,  final float y3){
    final int countlg=3;
    int count=1 << countlg;
    float dddx, dddy, ddx, ddy, dx, dy;
    dddx=2f * c.dax / (1 << (3 * countlg));
    dddy=2f * c.day / (1 << (3 * countlg));
    ddx=dddx + c.dbx / (1 << (2 * countlg));
    ddy=dddy + c.dby / (1 << (2 * countlg));
    dx=c.ax / (1 << (3 * countlg)) + c.bx / (1 << (2 * countlg)) + c.cx / (1 << countlg);
    dy=c.ay / (1 << (3 * countlg)) + c.by / (1 << (2 * countlg)) + c.cy / (1 << countlg);
    float x1=x0, y1=y0;
    while (count > 0) {
      while (Math.abs(ddx) > DEC_BND || Math.abs(ddy) > DEC_BND) {
        dddx/=8;
        dddy/=8;
        ddx=ddx / 4 - dddx;
        ddy=ddy / 4 - dddy;
        dx=(dx - ddx) / 2;
        dy=(dy - ddy) / 2;
        count<<=1;
      }
      while (count % 2 == 0 && Math.abs(dx) <= INC_BND && Math.abs(dy) <= INC_BND) {
        dx=2 * dx + ddx;
        dy=2 * dy + ddy;
        ddx=4 * (ddx + dddx);
        ddy=4 * (ddy + dddy);
        dddx=8 * dddx;
        dddy=8 * dddy;
        count>>=1;
      }
      count--;
      if (count > 0) {
        x1+=dx;
        dx+=ddx;
        ddx+=dddx;
        y1+=dy;
        dy+=ddy;
        ddy+=dddy;
      }
 else {
        x1=x3;
        y1=y3;
      }
      addLine(x0,y0,x1,y1);
      x0=x1;
      y0=y1;
    }
  }
  private void addLine(  float x1,  float y1,  float x2,  float y2){
    float or=1;
    if (y2 < y1) {
      or=y2;
      y2=y1;
      y1=or;
      or=x2;
      x2=x1;
      x1=or;
      or=0;
    }
    final int firstCrossing=Math.max((int)Math.ceil(y1),boundsMinY);
    final int lastCrossing=Math.min((int)Math.ceil(y2),boundsMaxY);
    if (firstCrossing >= lastCrossing) {
      return;
    }
    if (y1 < edgeMinY) {
      edgeMinY=y1;
    }
    if (y2 > edgeMaxY) {
      edgeMaxY=y2;
    }
    final float slope=(x2 - x1) / (y2 - y1);
    if (slope > 0) {
      if (x1 < edgeMinX) {
        edgeMinX=x1;
      }
      if (x2 > edgeMaxX) {
        edgeMaxX=x2;
      }
    }
 else {
      if (x2 < edgeMinX) {
        edgeMinX=x2;
      }
      if (x1 > edgeMaxX) {
        edgeMaxX=x1;
      }
    }
    final int ptr=numEdges * SIZEOF_EDGE;
    edges=Helpers.widenArray(edges,ptr,SIZEOF_EDGE);
    numEdges++;
    edges[ptr + OR]=or;
    edges[ptr + CURX]=x1 + (firstCrossing - y1) * slope;
    edges[ptr + SLOPE]=slope;
    edges[ptr + YMAX]=lastCrossing;
    final int bucketIdx=firstCrossing - boundsMinY;
    addEdgeToBucket(ptr,bucketIdx);
    edgeBucketCounts[lastCrossing - boundsMinY]|=1;
  }
  public static final int WIND_EVEN_ODD=0;
  public static final int WIND_NON_ZERO=1;
  final private int SUBPIXEL_LG_POSITIONS_X;
  final private int SUBPIXEL_LG_POSITIONS_Y;
  final private int SUBPIXEL_POSITIONS_X;
  final private int SUBPIXEL_POSITIONS_Y;
  final private int SUBPIXEL_MASK_X;
  final private int SUBPIXEL_MASK_Y;
  final int MAX_AA_ALPHA;
  PiscesCache cache;
  private final int boundsMinX, boundsMinY, boundsMaxX, boundsMaxY;
  private final int windingRule;
  private float x0, y0;
  private float pix_sx0, pix_sy0;
  public Renderer(  int subpixelLgPositionsX,  int subpixelLgPositionsY,  int pix_boundsX,  int pix_boundsY,  int pix_boundsWidth,  int pix_boundsHeight,  int windingRule){
    this.SUBPIXEL_LG_POSITIONS_X=subpixelLgPositionsX;
    this.SUBPIXEL_LG_POSITIONS_Y=subpixelLgPositionsY;
    this.SUBPIXEL_MASK_X=(1 << (SUBPIXEL_LG_POSITIONS_X)) - 1;
    this.SUBPIXEL_MASK_Y=(1 << (SUBPIXEL_LG_POSITIONS_Y)) - 1;
    this.SUBPIXEL_POSITIONS_X=1 << (SUBPIXEL_LG_POSITIONS_X);
    this.SUBPIXEL_POSITIONS_Y=1 << (SUBPIXEL_LG_POSITIONS_Y);
    this.MAX_AA_ALPHA=(SUBPIXEL_POSITIONS_X * SUBPIXEL_POSITIONS_Y);
    this.windingRule=windingRule;
    this.boundsMinX=pix_boundsX * SUBPIXEL_POSITIONS_X;
    this.boundsMinY=pix_boundsY * SUBPIXEL_POSITIONS_Y;
    this.boundsMaxX=(pix_boundsX + pix_boundsWidth) * SUBPIXEL_POSITIONS_X;
    this.boundsMaxY=(pix_boundsY + pix_boundsHeight) * SUBPIXEL_POSITIONS_Y;
    edges=new float[INIT_NUM_EDGES * SIZEOF_EDGE];
    numEdges=0;
    edgeBuckets=new int[boundsMaxY - boundsMinY];
    java.util.Arrays.fill(edgeBuckets,NULL);
    edgeBucketCounts=new int[edgeBuckets.length + 1];
  }
  private float tosubpixx(  float pix_x){
    return pix_x * SUBPIXEL_POSITIONS_X;
  }
  private float tosubpixy(  float pix_y){
    return pix_y * SUBPIXEL_POSITIONS_Y;
  }
  public void moveTo(  float pix_x0,  float pix_y0){
    closePath();
    this.pix_sx0=pix_x0;
    this.pix_sy0=pix_y0;
    this.y0=tosubpixy(pix_y0);
    this.x0=tosubpixx(pix_x0);
  }
  public void lineTo(  float pix_x1,  float pix_y1){
    float x1=tosubpixx(pix_x1);
    float y1=tosubpixy(pix_y1);
    addLine(x0,y0,x1,y1);
    x0=x1;
    y0=y1;
  }
  private Curve c=new Curve();
  @Override public void curveTo(  float x1,  float y1,  float x2,  float y2,  float x3,  float y3){
    final float xe=tosubpixx(x3);
    final float ye=tosubpixy(y3);
    c.set(x0,y0,tosubpixx(x1),tosubpixy(y1),tosubpixx(x2),tosubpixy(y2),xe,ye);
    curveBreakIntoLinesAndAdd(x0,y0,c,xe,ye);
    x0=xe;
    y0=ye;
  }
  @Override public void quadTo(  float x1,  float y1,  float x2,  float y2){
    final float xe=tosubpixx(x2);
    final float ye=tosubpixy(y2);
    c.set(x0,y0,tosubpixx(x1),tosubpixy(y1),xe,ye);
    quadBreakIntoLinesAndAdd(x0,y0,c,xe,ye);
    x0=xe;
    y0=ye;
  }
  public void closePath(){
    lineTo(pix_sx0,pix_sy0);
  }
  public void pathDone(){
    closePath();
  }
  @Override public long getNativeConsumer(){
    throw new InternalError("Renderer does not use a native consumer.");
  }
  private void _endRendering(  final int pix_bboxx0,  final int pix_bboxx1,  int ymin,  int ymax){
    int mask=(windingRule == WIND_EVEN_ODD) ? 0x1 : ~0x0;
    int width=pix_bboxx1 - pix_bboxx0;
    int[] alpha=new int[width + 2];
    int bboxx0=pix_bboxx0 << SUBPIXEL_LG_POSITIONS_X;
    int bboxx1=pix_bboxx1 << SUBPIXEL_LG_POSITIONS_X;
    int pix_maxX=Integer.MIN_VALUE;
    int pix_minX=Integer.MAX_VALUE;
    int y=boundsMinY;
    ScanlineIterator it=this.new ScanlineIterator(ymin,ymax);
    for (; it.hasNext(); ) {
      int numCrossings=it.next();
      int[] crossings=it.crossings;
      y=it.curY();
      if (numCrossings > 0) {
        int lowx=crossings[0] >> 1;
        int highx=crossings[numCrossings - 1] >> 1;
        int x0=Math.max(lowx,bboxx0);
        int x1=Math.min(highx,bboxx1);
        pix_minX=Math.min(pix_minX,x0 >> SUBPIXEL_LG_POSITIONS_X);
        pix_maxX=Math.max(pix_maxX,x1 >> SUBPIXEL_LG_POSITIONS_X);
      }
      int sum=0;
      int prev=bboxx0;
      for (int i=0; i < numCrossings; i++) {
        int curxo=crossings[i];
        int curx=curxo >> 1;
        int crorientation=((curxo & 0x1) << 1) - 1;
        if ((sum & mask) != 0) {
          int x0=Math.max(prev,bboxx0);
          int x1=Math.min(curx,bboxx1);
          if (x0 < x1) {
            x0-=bboxx0;
            x1-=bboxx0;
            int pix_x=x0 >> SUBPIXEL_LG_POSITIONS_X;
            int pix_xmaxm1=(x1 - 1) >> SUBPIXEL_LG_POSITIONS_X;
            if (pix_x == pix_xmaxm1) {
              alpha[pix_x]+=(x1 - x0);
              alpha[pix_x + 1]-=(x1 - x0);
            }
 else {
              int pix_xmax=x1 >> SUBPIXEL_LG_POSITIONS_X;
              alpha[pix_x]+=SUBPIXEL_POSITIONS_X - (x0 & SUBPIXEL_MASK_X);
              alpha[pix_x + 1]+=(x0 & SUBPIXEL_MASK_X);
              alpha[pix_xmax]-=SUBPIXEL_POSITIONS_X - (x1 & SUBPIXEL_MASK_X);
              alpha[pix_xmax + 1]-=(x1 & SUBPIXEL_MASK_X);
            }
          }
        }
        sum+=crorientation;
        prev=curx;
      }
      if ((y & SUBPIXEL_MASK_Y) == SUBPIXEL_MASK_Y) {
        emitRow(alpha,y >> SUBPIXEL_LG_POSITIONS_Y,pix_minX,pix_maxX);
        pix_minX=Integer.MAX_VALUE;
        pix_maxX=Integer.MIN_VALUE;
      }
    }
    if (pix_maxX >= pix_minX) {
      emitRow(alpha,y >> SUBPIXEL_LG_POSITIONS_Y,pix_minX,pix_maxX);
    }
  }
  public void endRendering(){
    int spminX=Math.max((int)Math.ceil(edgeMinX),boundsMinX);
    int spmaxX=Math.min((int)Math.ceil(edgeMaxX),boundsMaxX);
    int spminY=Math.max((int)Math.ceil(edgeMinY),boundsMinY);
    int spmaxY=Math.min((int)Math.ceil(edgeMaxY),boundsMaxY);
    int pminX=spminX >> SUBPIXEL_LG_POSITIONS_X;
    int pmaxX=(spmaxX + SUBPIXEL_MASK_X) >> SUBPIXEL_LG_POSITIONS_X;
    int pminY=spminY >> SUBPIXEL_LG_POSITIONS_Y;
    int pmaxY=(spmaxY + SUBPIXEL_MASK_Y) >> SUBPIXEL_LG_POSITIONS_Y;
    if (pminX > pmaxX || pminY > pmaxY) {
      this.cache=new PiscesCache(boundsMinX >> SUBPIXEL_LG_POSITIONS_X,boundsMinY >> SUBPIXEL_LG_POSITIONS_Y,boundsMaxX >> SUBPIXEL_LG_POSITIONS_X,boundsMaxY >> SUBPIXEL_LG_POSITIONS_Y);
      return;
    }
    this.cache=new PiscesCache(pminX,pminY,pmaxX,pmaxY);
    _endRendering(pminX,pmaxX,spminY,spmaxY);
  }
  public PiscesCache getCache(){
    if (cache == null) {
      throw new InternalError("cache not yet initialized");
    }
    return cache;
  }
  private void emitRow(  int[] alphaRow,  int pix_y,  int pix_from,  int pix_to){
    if (cache != null) {
      if (pix_to >= pix_from) {
        cache.startRow(pix_y,pix_from);
        int from=pix_from - cache.bboxX0;
        int to=pix_to - cache.bboxX0;
        int runLen=1;
        int startVal=alphaRow[from];
        for (int i=from + 1; i <= to; i++) {
          int nextVal=startVal + alphaRow[i];
          if (nextVal == startVal) {
            runLen++;
          }
 else {
            cache.addRLERun(startVal,runLen);
            runLen=1;
            startVal=nextVal;
          }
        }
        cache.addRLERun(startVal,runLen);
      }
    }
    java.util.Arrays.fill(alphaRow,0);
  }
}
