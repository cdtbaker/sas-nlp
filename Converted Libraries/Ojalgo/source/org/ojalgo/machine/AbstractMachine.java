package org.ojalgo.machine;
import org.ojalgo.type.IntCount;
abstract class AbstractMachine extends BasicMachine {
  static final long K=1024L;
  static long elements(  final long availableMemory,  final long elementSize){
    return (availableMemory - 16L) / elementSize;
  }
  public final String architecture;
  /** 
 * The size of one top level (L3 or L2) cache unit in bytes.
 */
  public final long cache;
  /** 
 * The total number of processor cores.
 */
  public final int cores;
  /** 
 * The number of top level (L3 or L2) cache units.
 */
  public final int units;
  private AbstractMachine(  final long aMemory,  final int aThreads){
    super(aMemory,aThreads);
    throw new IllegalArgumentException();
  }
  protected AbstractMachine(  final Hardware hardware,  final Runtime runtime){
    super(runtime.maxMemory(),runtime.availableProcessors());
    architecture=hardware.architecture;
    cache=hardware.cache;
    cores=hardware.cores;
    units=hardware.units;
  }
  /** 
 * <code>new MemoryThreads[] { SYSTEM, L3, L2, L1 }</code>
 * or
 * <code>new MemoryThreads[] { SYSTEM, L2, L1 }</code>
 * or in worst case
 * <code>new MemoryThreads[] { SYSTEM, L1 }</code>
 */
  protected AbstractMachine(  final String anArchitecture,  final BasicMachine[] levels){
    super(levels[0].memory,levels[0].threads);
    architecture=anArchitecture;
    cores=threads / levels[levels.length - 1].threads;
    if (levels.length > 3) {
      cache=levels[levels.length - 3].memory;
      units=threads / levels[levels.length - 3].threads;
    }
 else     if (levels.length > 2) {
      cache=levels[levels.length - 2].memory;
      units=threads / levels[levels.length - 2].threads;
    }
 else {
      cache=levels[levels.length - 1].memory;
      units=threads / levels[levels.length - 1].threads;
    }
  }
  public IntCount countCores(){
    return new IntCount(cores);
  }
  public IntCount countThreads(){
    return new IntCount(threads);
  }
  public IntCount countUnits(){
    return new IntCount(units);
  }
  @Override public boolean equals(  final Object obj){
    if (this == obj) {
      return true;
    }
    if (!super.equals(obj)) {
      return false;
    }
    if (!(obj instanceof AbstractMachine)) {
      return false;
    }
    final AbstractMachine other=(AbstractMachine)obj;
    if (architecture == null) {
      if (other.architecture != null) {
        return false;
      }
    }
 else     if (!architecture.equals(other.architecture)) {
      return false;
    }
    if (cache != other.cache) {
      return false;
    }
    if (units != other.units) {
      return false;
    }
    if (units != other.units) {
      return false;
    }
    return true;
  }
  public int getCacheDim1D(  final long elementSize){
    return (int)AbstractMachine.elements(cache,elementSize);
  }
  public int getCacheDim2D(  final long elementSize){
    return (int)Math.sqrt(AbstractMachine.elements(cache,elementSize));
  }
  public int getMemoryDim1D(  final long elementSize){
    return (int)AbstractMachine.elements(memory,elementSize);
  }
  public int getMemoryDim2D(  final long elementSize){
    return (int)Math.sqrt(AbstractMachine.elements(memory,elementSize));
  }
  @Override public int hashCode(){
    final int prime=31;
    int result=super.hashCode();
    result=(prime * result) + ((architecture == null) ? 0 : architecture.hashCode());
    result=(prime * result) + (int)(cache ^ (cache >>> 32));
    result=(prime * result) + units;
    return result;
  }
  public boolean isMultiCore(){
    return cores > 1;
  }
  public boolean isMultiThread(){
    return threads > 1;
  }
  public boolean isMultiUnit(){
    return units > 1;
  }
}
