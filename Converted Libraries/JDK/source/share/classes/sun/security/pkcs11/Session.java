package sun.security.pkcs11;
import java.lang.ref.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.security.*;
import sun.security.pkcs11.wrapper.*;
/** 
 * A session object. Sessions are obtained via the SessionManager,
 * see there for details. Most code will only ever need one method in
 * this class, the id() method to obtain the session id.
 * @author  Andreas Sterbenz
 * @since   1.5
 */
final class Session implements Comparable<Session> {
  private final static long MAX_IDLE_TIME=3 * 60 * 1000;
  final Token token;
  private final long id;
  private final AtomicInteger createdObjects;
  private long lastAccess;
  private final SessionRef sessionRef;
  Session(  Token token,  long id){
    this.token=token;
    this.id=id;
    createdObjects=new AtomicInteger();
    id();
    sessionRef=new SessionRef(this,id,token);
  }
  public int compareTo(  Session other){
    if (this.lastAccess == other.lastAccess) {
      return 0;
    }
 else {
      return (this.lastAccess < other.lastAccess) ? -1 : 1;
    }
  }
  boolean isLive(  long currentTime){
    return currentTime - lastAccess < MAX_IDLE_TIME;
  }
  long idInternal(){
    return id;
  }
  long id(){
    if (token.isPresent(this) == false) {
      throw new ProviderException("Token has been removed");
    }
    lastAccess=System.currentTimeMillis();
    return id;
  }
  void addObject(){
    int n=createdObjects.incrementAndGet();
  }
  void removeObject(){
    int n=createdObjects.decrementAndGet();
    if (n == 0) {
      token.sessionManager.demoteObjSession(this);
    }
 else     if (n < 0) {
      throw new ProviderException("Internal error: objects created " + n);
    }
  }
  boolean hasObjects(){
    return createdObjects.get() != 0;
  }
  void close(){
    if (hasObjects()) {
      throw new ProviderException("Internal error: close session with active objects");
    }
    sessionRef.dispose();
  }
}
final class SessionRef extends PhantomReference<Session> implements Comparable<SessionRef> {
  private static ReferenceQueue<Session> refQueue=new ReferenceQueue<Session>();
  private static Set<SessionRef> refList=Collections.synchronizedSortedSet(new TreeSet<SessionRef>());
  static ReferenceQueue<Session> referenceQueue(){
    return refQueue;
  }
  static int totalCount(){
    return refList.size();
  }
  private static void drainRefQueueBounded(){
    while (true) {
      SessionRef next=(SessionRef)refQueue.poll();
      if (next == null)       break;
      next.dispose();
    }
  }
  private long id;
  private Token token;
  SessionRef(  Session session,  long id,  Token token){
    super(session,refQueue);
    this.id=id;
    this.token=token;
    refList.add(this);
    drainRefQueueBounded();
  }
  void dispose(){
    refList.remove(this);
    try {
      token.p11.C_CloseSession(id);
    }
 catch (    PKCS11Exception e1) {
    }
catch (    ProviderException e2) {
    }
 finally {
      this.clear();
    }
  }
  public int compareTo(  SessionRef other){
    if (this.id == other.id) {
      return 0;
    }
 else {
      return (this.id < other.id) ? -1 : 1;
    }
  }
}
