package cern.jet.random;
/** 
 * Not yet commented.
 */
class Stack {
  int N;
  int[] v;
  int i;
  /** 
 * Constructs a new stack with the given capacity.
 */
  public Stack(  int capacity){
    this.N=capacity;
    this.i=-1;
    this.v=new int[N];
  }
  /** 
 * Returns the topmost element.
 */
  public int pop(){
    if (this.i < 0)     throw new InternalError("Cannot pop stack!");
    this.i--;
    return this.v[this.i + 1];
  }
  /** 
 * Places the given value on top of the stack.
 */
  public void push(  int value){
    this.i++;
    if (this.i >= this.N)     throw new InternalError("Cannot push stack!");
    this.v[this.i]=value;
  }
  /** 
 * Returns the number of elements contained.
 */
  public int size(){
    return i + 1;
  }
}
