package sun.nio.ch;
/** 
 * Manipulates a native array of structs corresponding to (fd, events) pairs.
 * typedef struct pollfd {
 * SOCKET fd;            // 4 bytes
 * short events;         // 2 bytes
 * } pollfd_t;
 * @author Konstantin Kladko
 * @author Mike McCloskey
 */
class PollArrayWrapper {
  private AllocatedNativeObject pollArray;
  long pollArrayAddress;
  private static final short FD_OFFSET=0;
  private static final short EVENT_OFFSET=4;
  static short SIZE_POLLFD=8;
  static final short POLLIN=AbstractPollArrayWrapper.POLLIN;
  static final short POLLOUT=AbstractPollArrayWrapper.POLLOUT;
  static final short POLLERR=AbstractPollArrayWrapper.POLLERR;
  static final short POLLHUP=AbstractPollArrayWrapper.POLLHUP;
  static final short POLLNVAL=AbstractPollArrayWrapper.POLLNVAL;
  static final short POLLREMOVE=AbstractPollArrayWrapper.POLLREMOVE;
  static final short POLLCONN=0x0002;
  private int size;
  PollArrayWrapper(  int newSize){
    int allocationSize=newSize * SIZE_POLLFD;
    pollArray=new AllocatedNativeObject(allocationSize,true);
    pollArrayAddress=pollArray.address();
    this.size=newSize;
  }
  void addEntry(  int index,  SelectionKeyImpl ski){
    putDescriptor(index,ski.channel.getFDVal());
  }
  void replaceEntry(  PollArrayWrapper source,  int sindex,  PollArrayWrapper target,  int tindex){
    target.putDescriptor(tindex,source.getDescriptor(sindex));
    target.putEventOps(tindex,source.getEventOps(sindex));
  }
  void grow(  int newSize){
    PollArrayWrapper temp=new PollArrayWrapper(newSize);
    for (int i=0; i < size; i++)     replaceEntry(this,i,temp,i);
    pollArray.free();
    pollArray=temp.pollArray;
    this.size=temp.size;
    pollArrayAddress=pollArray.address();
  }
  void free(){
    pollArray.free();
  }
  void putDescriptor(  int i,  int fd){
    pollArray.putInt(SIZE_POLLFD * i + FD_OFFSET,fd);
  }
  void putEventOps(  int i,  int event){
    pollArray.putShort(SIZE_POLLFD * i + EVENT_OFFSET,(short)event);
  }
  int getEventOps(  int i){
    return pollArray.getShort(SIZE_POLLFD * i + EVENT_OFFSET);
  }
  int getDescriptor(  int i){
    return pollArray.getInt(SIZE_POLLFD * i + FD_OFFSET);
  }
  void addWakeupSocket(  int fdVal,  int index){
    putDescriptor(index,fdVal);
    putEventOps(index,POLLIN);
  }
}
