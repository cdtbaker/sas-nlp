package sun.nio.ch;
import java.nio.channels.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.lang.ref.*;
import java.io.FileDescriptor;
import java.io.IOException;
abstract class FileLockTable {
  protected FileLockTable(){
  }
  /** 
 * Creates and returns a file lock table for a channel that is connected to
 * the a system-wide map of all file locks for the Java virtual machine.
 */
  public static FileLockTable newSharedFileLockTable(  Channel channel,  FileDescriptor fd) throws IOException {
    return new SharedFileLockTable(channel,fd);
  }
  /** 
 * Adds a file lock to the table.
 * @throws OverlappingFileLockException if the file lock overlaps
 * with an existing file lock in the table
 */
  public abstract void add(  FileLock fl) throws OverlappingFileLockException ;
  /** 
 * Remove an existing file lock from the table.
 */
  public abstract void remove(  FileLock fl);
  /** 
 * Removes all file locks from the table.
 * @return  The list of file locks removed
 */
  public abstract List<FileLock> removeAll();
  /** 
 * Replaces an existing file lock in the table.
 */
  public abstract void replace(  FileLock fl1,  FileLock fl2);
}
/** 
 * A file lock table that is over a system-wide map of all file locks.
 */
class SharedFileLockTable extends FileLockTable {
  /** 
 * A weak reference to a FileLock.
 * <p>
 * SharedFileLockTable uses a list of file lock references to avoid keeping the
 * FileLock (and FileChannel) alive.
 */
private static class FileLockReference extends WeakReference<FileLock> {
    private FileKey fileKey;
    FileLockReference(    FileLock referent,    ReferenceQueue<FileLock> queue,    FileKey key){
      super(referent,queue);
      this.fileKey=key;
    }
    FileKey fileKey(){
      return fileKey;
    }
  }
  private static ConcurrentHashMap<FileKey,List<FileLockReference>> lockMap=new ConcurrentHashMap<FileKey,List<FileLockReference>>();
  private static ReferenceQueue<FileLock> queue=new ReferenceQueue<FileLock>();
  private final Channel channel;
  private final FileKey fileKey;
  SharedFileLockTable(  Channel channel,  FileDescriptor fd) throws IOException {
    this.channel=channel;
    this.fileKey=FileKey.create(fd);
  }
  @Override public void add(  FileLock fl) throws OverlappingFileLockException {
    List<FileLockReference> list=lockMap.get(fileKey);
    for (; ; ) {
      if (list == null) {
        list=new ArrayList<FileLockReference>(2);
        List<FileLockReference> prev;
synchronized (list) {
          prev=lockMap.putIfAbsent(fileKey,list);
          if (prev == null) {
            list.add(new FileLockReference(fl,queue,fileKey));
            break;
          }
        }
        list=prev;
      }
synchronized (list) {
        List<FileLockReference> current=lockMap.get(fileKey);
        if (list == current) {
          checkList(list,fl.position(),fl.size());
          list.add(new FileLockReference(fl,queue,fileKey));
          break;
        }
        list=current;
      }
    }
    removeStaleEntries();
  }
  private void removeKeyIfEmpty(  FileKey fk,  List<FileLockReference> list){
    assert Thread.holdsLock(list);
    assert lockMap.get(fk) == list;
    if (list.isEmpty()) {
      lockMap.remove(fk);
    }
  }
  @Override public void remove(  FileLock fl){
    assert fl != null;
    List<FileLockReference> list=lockMap.get(fileKey);
    if (list == null)     return;
synchronized (list) {
      int index=0;
      while (index < list.size()) {
        FileLockReference ref=list.get(index);
        FileLock lock=ref.get();
        if (lock == fl) {
          assert (lock != null) && (lock.acquiredBy() == channel);
          ref.clear();
          list.remove(index);
          break;
        }
        index++;
      }
    }
  }
  @Override public List<FileLock> removeAll(){
    List<FileLock> result=new ArrayList<FileLock>();
    List<FileLockReference> list=lockMap.get(fileKey);
    if (list != null) {
synchronized (list) {
        int index=0;
        while (index < list.size()) {
          FileLockReference ref=list.get(index);
          FileLock lock=ref.get();
          if (lock != null && lock.acquiredBy() == channel) {
            ref.clear();
            list.remove(index);
            result.add(lock);
          }
 else {
            index++;
          }
        }
        removeKeyIfEmpty(fileKey,list);
      }
    }
    return result;
  }
  @Override public void replace(  FileLock fromLock,  FileLock toLock){
    List<FileLockReference> list=lockMap.get(fileKey);
    assert list != null;
synchronized (list) {
      for (int index=0; index < list.size(); index++) {
        FileLockReference ref=list.get(index);
        FileLock lock=ref.get();
        if (lock == fromLock) {
          ref.clear();
          list.set(index,new FileLockReference(toLock,queue,fileKey));
          break;
        }
      }
    }
  }
  private void checkList(  List<FileLockReference> list,  long position,  long size) throws OverlappingFileLockException {
    assert Thread.holdsLock(list);
    for (    FileLockReference ref : list) {
      FileLock fl=ref.get();
      if (fl != null && fl.overlaps(position,size))       throw new OverlappingFileLockException();
    }
  }
  private void removeStaleEntries(){
    FileLockReference ref;
    while ((ref=(FileLockReference)queue.poll()) != null) {
      FileKey fk=ref.fileKey();
      List<FileLockReference> list=lockMap.get(fk);
      if (list != null) {
synchronized (list) {
          list.remove(ref);
          removeKeyIfEmpty(fk,list);
        }
      }
    }
  }
}
