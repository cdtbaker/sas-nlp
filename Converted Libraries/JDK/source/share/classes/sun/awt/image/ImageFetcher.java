package sun.awt.image;
import java.util.Vector;
import sun.awt.AppContext;
/** 
 * An ImageFetcher is a thread used to fetch ImageFetchable objects.
 * Once an ImageFetchable object has been fetched, the ImageFetcher
 * thread may also be used to animate it if necessary, via the
 * startingAnimation() / stoppingAnimation() methods.
 * There can be up to FetcherInfo.MAX_NUM_FETCHERS_PER_APPCONTEXT
 * ImageFetcher threads for each AppContext.  A per-AppContext queue
 * of ImageFetchables is used to track objects to fetch.
 * @author Jim Graham
 * @author Fred Ecks
 */
class ImageFetcher extends Thread {
  static final int HIGH_PRIORITY=8;
  static final int LOW_PRIORITY=3;
  static final int ANIM_PRIORITY=2;
  static final int TIMEOUT=5000;
  /** 
 * Constructor for ImageFetcher -- only called by add() below.
 */
  private ImageFetcher(  ThreadGroup threadGroup,  int index){
    super(threadGroup,"Image Fetcher " + index);
    setDaemon(true);
  }
  /** 
 * Adds an ImageFetchable to the queue of items to fetch.  Instantiates
 * a new ImageFetcher if it's reasonable to do so.
 * If there is no available fetcher to process an ImageFetchable, then
 * reports failure to caller.
 */
  public static boolean add(  ImageFetchable src){
    final FetcherInfo info=FetcherInfo.getFetcherInfo();
synchronized (info.waitList) {
      if (!info.waitList.contains(src)) {
        info.waitList.addElement(src);
        if (info.numWaiting == 0 && info.numFetchers < info.fetchers.length) {
          createFetchers(info);
        }
        if (info.numFetchers > 0) {
          info.waitList.notify();
        }
 else {
          info.waitList.removeElement(src);
          return false;
        }
      }
    }
    return true;
  }
  /** 
 * Removes an ImageFetchable from the queue of items to fetch.
 */
  public static void remove(  ImageFetchable src){
    final FetcherInfo info=FetcherInfo.getFetcherInfo();
synchronized (info.waitList) {
      if (info.waitList.contains(src)) {
        info.waitList.removeElement(src);
      }
    }
  }
  /** 
 * Checks to see if the given thread is one of the ImageFetchers.
 */
  public static boolean isFetcher(  Thread t){
    final FetcherInfo info=FetcherInfo.getFetcherInfo();
synchronized (info.waitList) {
      for (int i=0; i < info.fetchers.length; i++) {
        if (info.fetchers[i] == t) {
          return true;
        }
      }
    }
    return false;
  }
  /** 
 * Checks to see if the current thread is one of the ImageFetchers.
 */
  public static boolean amFetcher(){
    return isFetcher(Thread.currentThread());
  }
  /** 
 * Returns the next ImageFetchable to be processed.  If TIMEOUT
 * elapses in the mean time, or if the ImageFetcher is interrupted,
 * null is returned.
 */
  private static ImageFetchable nextImage(){
    final FetcherInfo info=FetcherInfo.getFetcherInfo();
synchronized (info.waitList) {
      ImageFetchable src=null;
      long end=System.currentTimeMillis() + TIMEOUT;
      while (src == null) {
        while (info.waitList.size() == 0) {
          long now=System.currentTimeMillis();
          if (now >= end) {
            return null;
          }
          try {
            info.numWaiting++;
            info.waitList.wait(end - now);
          }
 catch (          InterruptedException e) {
            return null;
          }
 finally {
            info.numWaiting--;
          }
        }
        src=(ImageFetchable)info.waitList.elementAt(0);
        info.waitList.removeElement(src);
      }
      return src;
    }
  }
  /** 
 * The main run() method of an ImageFetcher Thread.  Calls fetchloop()
 * to do the work, then removes itself from the array of ImageFetchers.
 */
  public void run(){
    final FetcherInfo info=FetcherInfo.getFetcherInfo();
    try {
      fetchloop();
    }
 catch (    Exception e) {
      e.printStackTrace();
    }
 finally {
synchronized (info.waitList) {
        Thread me=Thread.currentThread();
        for (int i=0; i < info.fetchers.length; i++) {
          if (info.fetchers[i] == me) {
            info.fetchers[i]=null;
            info.numFetchers--;
          }
        }
      }
    }
  }
  /** 
 * The main ImageFetcher loop.  Repeatedly calls nextImage(), and
 * fetches the returned ImageFetchable objects until nextImage()
 * returns null.
 */
  private void fetchloop(){
    Thread me=Thread.currentThread();
    while (isFetcher(me)) {
      me.interrupted();
      me.setPriority(HIGH_PRIORITY);
      ImageFetchable src=nextImage();
      if (src == null) {
        return;
      }
      try {
        src.doFetch();
      }
 catch (      Exception e) {
        System.err.println("Uncaught error fetching image:");
        e.printStackTrace();
      }
      stoppingAnimation(me);
    }
  }
  /** 
 * Recycles this ImageFetcher thread as an image animator thread.
 * Removes this ImageFetcher from the array of ImageFetchers, and
 * resets the thread name to "ImageAnimator".
 */
  static void startingAnimation(){
    final FetcherInfo info=FetcherInfo.getFetcherInfo();
    Thread me=Thread.currentThread();
synchronized (info.waitList) {
      for (int i=0; i < info.fetchers.length; i++) {
        if (info.fetchers[i] == me) {
          info.fetchers[i]=null;
          info.numFetchers--;
          me.setName("Image Animator " + i);
          if (info.waitList.size() > info.numWaiting) {
            createFetchers(info);
          }
          return;
        }
      }
    }
    me.setPriority(ANIM_PRIORITY);
    me.setName("Image Animator");
  }
  /** 
 * Returns this image animator thread back to service as an ImageFetcher
 * if possible.  Puts it back into the array of ImageFetchers and sets
 * the thread name back to "Image Fetcher".  If there are already the
 * maximum number of ImageFetchers, this method simply returns, and
 * fetchloop() will drop out when it sees that this thread isn't one of
 * the ImageFetchers, and this thread will die.
 */
  private static void stoppingAnimation(  Thread me){
    final FetcherInfo info=FetcherInfo.getFetcherInfo();
synchronized (info.waitList) {
      int index=-1;
      for (int i=0; i < info.fetchers.length; i++) {
        if (info.fetchers[i] == me) {
          return;
        }
        if (info.fetchers[i] == null) {
          index=i;
        }
      }
      if (index >= 0) {
        info.fetchers[index]=me;
        info.numFetchers++;
        me.setName("Image Fetcher " + index);
        return;
      }
    }
  }
  /** 
 * Create and start ImageFetcher threads in the appropriate ThreadGroup.
 */
  private static void createFetchers(  final FetcherInfo info){
    final AppContext appContext=AppContext.getAppContext();
    ThreadGroup threadGroup=appContext.getThreadGroup();
    ThreadGroup fetcherThreadGroup;
    try {
      if (threadGroup.getParent() != null) {
        fetcherThreadGroup=threadGroup;
      }
 else {
        threadGroup=Thread.currentThread().getThreadGroup();
        ThreadGroup parent=threadGroup.getParent();
        while ((parent != null) && (parent.getParent() != null)) {
          threadGroup=parent;
          parent=threadGroup.getParent();
        }
        fetcherThreadGroup=threadGroup;
      }
    }
 catch (    SecurityException e) {
      fetcherThreadGroup=appContext.getThreadGroup();
    }
    final ThreadGroup fetcherGroup=fetcherThreadGroup;
    java.security.AccessController.doPrivileged(new java.security.PrivilegedAction(){
      public Object run(){
        for (int i=0; i < info.fetchers.length; i++) {
          if (info.fetchers[i] == null) {
            ImageFetcher f=new ImageFetcher(fetcherGroup,i);
            try {
              f.start();
              info.fetchers[i]=f;
              info.numFetchers++;
              break;
            }
 catch (            Error e) {
            }
          }
        }
        return null;
      }
    }
);
    return;
  }
}
/** 
 * The FetcherInfo class encapsulates the per-AppContext ImageFetcher
 * information.  This includes the array of ImageFetchers, as well as
 * the queue of ImageFetchable objects.
 */
class FetcherInfo {
  static final int MAX_NUM_FETCHERS_PER_APPCONTEXT=4;
  Thread[] fetchers;
  int numFetchers;
  int numWaiting;
  Vector waitList;
  private FetcherInfo(){
    fetchers=new Thread[MAX_NUM_FETCHERS_PER_APPCONTEXT];
    numFetchers=0;
    numWaiting=0;
    waitList=new Vector();
  }
  private static final Object FETCHER_INFO_KEY=new StringBuffer("FetcherInfo");
  static FetcherInfo getFetcherInfo(){
    AppContext appContext=AppContext.getAppContext();
synchronized (appContext) {
      FetcherInfo info=(FetcherInfo)appContext.get(FETCHER_INFO_KEY);
      if (info == null) {
        info=new FetcherInfo();
        appContext.put(FETCHER_INFO_KEY,info);
      }
      return info;
    }
  }
}
